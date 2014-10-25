package audioManipulation;

import helperAndResourceClasses.BashCommand;
import helperAndResourceClasses.SaveOutputChooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import vlcPlayer.PlayerPane;

/**
 * Panel containing audio functionality.
 * 
 * @author acas212 & kxie094
 *
 *Credits To: http://stackoverflow.com/questions/5766175/word-wrap-in-jbuttons
 *(Add word wrapping in JButton Text.)
 */
@SuppressWarnings("serial")
public class AudioPane extends JPanel {

	private final AudioPane audioPane = this;

	private final JPanel _stripPanel = new JPanel(new BorderLayout());
	private final JPanel _stripCBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
	private final JPanel _stripButtonPanel = new JPanel();
	private final JPanel _replaceOverlayPanel = new JPanel(new BorderLayout());
	private final JPanel _audioSelectSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
	private final JPanel _audioButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
	private	final JPanel _replaceOverlayButtonPanel = new JPanel();
	//private final JPanel _outputNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
	private final JPanel _processBarPanel = new JPanel(new BorderLayout());
	private final JPanel _bottomSubButtonPanel = new JPanel(new GridLayout(0,3));

	//private final JLabel _outputAudioLabel = new JLabel("Output Audio Name: (For Stripping) ");
	//private final JLabel _outputVideoLabel = new JLabel("Output Video Name: (For All Functions)");

	private final JTextField _chosenAudioInput = new JTextField(28);
	final JTextField _chosenVideoName = new JTextField(25);
	final JTextField _chosenAudioName = new JTextField(25);

	private final JButton _audioFileButton = new JButton("Select Audio File");
	private final JButton _audioPreviewButton = new JButton("Preview Audio File");
	private final JButton _stripButton = new JButton("Strip Audio");
	private final JButton _replaceButton = new JButton("Replace Audio");
	private final JButton _overlayButton = new JButton("Overlay Audio");
	private final JButton _cancelButton = new JButton("Cancel");

	private final JCheckBox _removeAudioOnVideo = new JCheckBox("Remove Stripped Audio on Video?");
	private final JCheckBox _haveAudioOutput = new JCheckBox("Create Output of Stripped Audio?");

	final JProgressBar _processBar = new JProgressBar();

	ExtractWorker _eW;
	StripWorker _sW;
	private OverlayWorker _oW;

	SaveOutputChooser saveVideoChooser;
	SaveOutputChooser saveAudioChooser;

	private File _selectedAudio;
	private File _currentWorkingVideo;

	public AudioPane() {

		//Set layout.
		setLayout(new FlowLayout());

		//Add and construct strip panel.
		add(_stripPanel);
		_stripPanel.setBorder(BorderFactory.createTitledBorder("Strip Audio:"));
		_stripPanel.setPreferredSize(new Dimension(380, 120));
		_stripCBoxPanel.setPreferredSize(new Dimension(260, 100));

		_stripPanel.add(_stripCBoxPanel, BorderLayout.CENTER);
		_stripPanel.add(_stripButtonPanel, BorderLayout.SOUTH);

		_stripCBoxPanel.add(_removeAudioOnVideo);
		_stripCBoxPanel.add(_haveAudioOutput);
		_stripButtonPanel.add(_stripButton);

		//Add and construct replace/overlay panel.
		add(_replaceOverlayPanel);
		_replaceOverlayPanel.setBorder(BorderFactory.createTitledBorder("Replace / Overlay Audio:"));
		_replaceOverlayPanel.setPreferredSize(new Dimension(380, 140));

		_replaceOverlayPanel.add(_audioSelectSubPanel, BorderLayout.CENTER);
		_replaceOverlayPanel.add(_replaceOverlayButtonPanel, BorderLayout.SOUTH);

		_audioSelectSubPanel.add(_chosenAudioInput);
		_audioSelectSubPanel.add(_audioButtonPanel);
		_audioButtonPanel.add(_audioFileButton);
		_audioButtonPanel.add(_audioPreviewButton);

		//Set format of text on replace / overlay buttons.
		_replaceButton.setText("<html><center>"+"Replace"+"<br>"+"Audio"+"</center></html>");
		_overlayButton.setText("<html><center>"+"Overlay"+"<br>"+"Audio"+"</center></html>");

		_replaceOverlayButtonPanel.add(_replaceButton);
		_replaceOverlayButtonPanel.add(_overlayButton);

		_chosenAudioInput.setEditable(false);

		//Add and construct process bar panel.
		add(_processBarPanel);
		_processBarPanel.setPreferredSize(new Dimension(385, 60));
		_processBarPanel.add(_processBar, BorderLayout.NORTH);
		_processBarPanel.add(_cancelButton, BorderLayout.CENTER);

		_processBar.setPreferredSize(new Dimension(385, 30));
		_processBar.setString("No Tasks Being Performed");
		_processBar.setStringPainted(true);

		//Set action listeners for buttons.
		setListeners();
	}

	private void setListeners() {

		//Add listeners to the buttons:

		_audioFileButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				JFileChooser fileChooser = new JFileChooser();
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {

					File selectedFile = fileChooser.getSelectedFile();

					String type;

					//Some error checking:
					try {
						type = Files.probeContentType(selectedFile.toPath());
						//If the selected file is an audio file:
						if(type.contains("audio")) {
							//Set it as new selected audio.
							_selectedAudio = selectedFile;
							_chosenAudioInput.setText(selectedFile.getName());
							//Else return error.
						} else {
							JOptionPane.showMessageDialog(null, "File selected is not a audio " +
									"file. Please select another file.");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} 
			}
		});

		_audioPreviewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				//If no audio selected, send error.
				if (_selectedAudio == null) {
					JOptionPane.showMessageDialog(null, "Error: No audio selected to replace / " +
							"overlay with. Please select an audio file using the button above.");

				} else {
					PlayerPane.getInstance().tempPlay(_selectedAudio.getAbsolutePath());
				}
			}
		});

		_stripButton.addActionListener(new  ActionListener() {

			String[] outputAudioExists;
			String[] outputVideoExists;

			@Override
			public void actionPerformed(ActionEvent e) {

				//Check to see of there is an editable video playing.
				if (PlayerPane.getInstance().getMediaPath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: No video to edit. Please select a file by " +
							"playing one using the Open button.");
					return;
				}

				//Open new save choosers for asking for audio and / or video output name(s).
				if (_removeAudioOnVideo.isSelected()) {
					saveVideoChooser = new SaveOutputChooser();
					saveVideoChooser.setDialogTitle("Save Stripped Video");

					//Use bash to check if output video already exists.
					outputVideoExists = new BashCommand().runBash("if [ ! -f " + 
							saveVideoChooser.getSavePath() + ".mp4" + " ]; then echo 0; else echo 1; fi");
				}

				if (_haveAudioOutput.isSelected()) {
					saveAudioChooser = new SaveOutputChooser();
					saveAudioChooser.setDialogTitle("Save Stripped Audio");

					//Use bash to check if output audio already exists.
					outputAudioExists = new BashCommand().runBash("if [ ! -f " + 
							saveAudioChooser.getSavePath() + ".mp3" + " ]; then echo 0; else echo 1; fi");
				}

				/*
				 * Series of Strip related error checking.
				 */
				boolean canStrip = false;

				//Send error if no option for strip is chosen.
				if (!_removeAudioOnVideo.isSelected() && !_haveAudioOutput.isSelected()) {
					JOptionPane.showMessageDialog(null, "Error: No option selected for audio " +
							"stripping. Please select a combination from the two provided " +
							"checkboxes.");
				} 

				//If file has no audio stream, send error.
				else if (!PlayerPane.getInstance().hasAudioStream()) {
					JOptionPane.showMessageDialog(null, "Error: Video has no audio stream. No " +
							"audio can be stripped.");
				} 
				/*
				 *	Handle special case when both options are selected:
				 */

				else if (_removeAudioOnVideo.isSelected() && _haveAudioOutput.isSelected()) {
					boolean validAudio = true;
					boolean validVideo = true;

					//If no output audio name is specified, send error.
					if (saveAudioChooser.getSavePath().equals("")) {
						JOptionPane.showMessageDialog(null, "Error: Please supply name for extracted " +
								"audio from stripped video.");
						validAudio = false;

						//If no output video name is specified, send error.
					} else if (saveVideoChooser.getSavePath().equals("")) {
						JOptionPane.showMessageDialog(null, "Error: Please supply name for output " +
								"video.");
						validVideo = false;

					} else {
						//Ask to overwrite if audio name already exists.
						if (outputAudioExists[0].equals("1")) {
							Object[] options = { "Overwrite", "Cancel" };
							int enableOverwrite = JOptionPane.showOptionDialog(audioPane, saveAudioChooser.getSavePath() + ".mp3"
									+ " already exists. Overwrite file? Please choose a new filename if not overriding.", 
									"File Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
									options, options[0]);
							if (enableOverwrite == 1) {
								validAudio = false;
							}


						} 
						//Ask to overwrite if video name already exists.
						else if (outputVideoExists[0].equals("1")) {
							Object[] options = { "Overwrite", "Cancel" };
							int enableOverwrite = JOptionPane.showOptionDialog(audioPane, saveVideoChooser.getSavePath() + ".mp4"
									+ " already exists. Overwrite file? Please choose a new filename if not overriding.", 
									"File Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
									options, options[0]);
							if (enableOverwrite == 1) {
								validVideo = false;
							}
						}
					}

					//If both audio and video are valid, proceed to strip.
					if (validAudio && validVideo) {
						canStrip = true;
					}
				} 

				/*
				 *	Else just handle one option being selected:
				 */

				//No output audio name given, send error.
				else if (_haveAudioOutput.isSelected() && saveAudioChooser.getSavePath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for extracted " +
							"audio from stripped video.");

				} 
				//Output audio named already exists, ask for overwrite.
				else if (_haveAudioOutput.isSelected() && outputAudioExists[0].equals("1")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(audioPane, saveAudioChooser.getSavePath() + ".mp3" + " already exists. " +
							"Overwrite file? Please choose a new filename if not overriding.", "File Already " +
									"Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canStrip = true;
					}

				}  
				//No Output video name given, send error.
				else if (_removeAudioOnVideo.isSelected() && saveVideoChooser.getSavePath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for stripped video.");

				} 
				//Output video named already exists, ask for overwrite.
				else if (_removeAudioOnVideo.isSelected() && outputVideoExists[0].equals("1")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(audioPane, saveVideoChooser.getSavePath() + ".mp4" + " already exists. " +
							"Overwrite file? Please choose a new filename if not overriding.", "File Already " +
									"Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canStrip = true;
					}

				} 
				//Else stripping should be fine.
				else {
					canStrip = true;
				}

				if (canStrip) { 
					//Set processBar to processing.
					_processBar.setIndeterminate(true);
					_processBar.setString("Strip In Progress");

					//Start new StripWorker.
					_currentWorkingVideo = new File(PlayerPane.getInstance().getMediaPath());
					_sW = new StripWorker(audioPane, _removeAudioOnVideo.isSelected(), _haveAudioOutput.isSelected(), _currentWorkingVideo);
					_sW.execute();

					//Disable function buttons
					disableFunctions();
				}
			}
		});

		_replaceButton.addActionListener(new ActionListener() {

			String[] outputVideoExists;

			@Override
			public void actionPerformed(ActionEvent e) {

				//Check to see of there is an editable video playing.
				if (PlayerPane.getInstance().getMediaPath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: No video to edit. Please select a file by " +
							"playing one using the Open button.");
					return;
				}

				//If no audio selected, send error.
				if (_selectedAudio == null) {
					JOptionPane.showMessageDialog(null, "Error: No audio selected to replace / " +
							"overlay with. Please select an audio file using the button above.");
					return;
				}

				//Open new save chooser for video output name.
				saveVideoChooser = new SaveOutputChooser();
				saveVideoChooser.setDialogTitle("Save Replaced Audio Video");

				//Use bash to check if output video already exists.
				outputVideoExists = new BashCommand().runBash("if [ ! -f " + 
						saveVideoChooser.getSavePath() + ".mp4" + " ]; then echo 0; else echo 1; fi");

				boolean canReplace = false;
				
				//Send error if no file name given.
				if (saveVideoChooser.getSavePath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for output video.");
					return;
				}

				//Ask for overwrite if output video file already exists.
				if (outputVideoExists[0].equals("1")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(audioPane, saveVideoChooser.getSavePath() + ".mp4" + 
							" already exists. Overwrite file? Please choose a new filename if not overriding.", "File " +
									"Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canReplace = true;
					}
				} else {
					//Else replace is fine.
					canReplace = true;
				}	

				if (canReplace) {
					//Set processBar to processing.
					_processBar.setIndeterminate(true);
					_processBar.setString("Replace In Progress");

					//Start new OverlayWorker to replace.
					_currentWorkingVideo = new File(PlayerPane.getInstance().getMediaPath());
					_oW = new OverlayWorker(audioPane, true, _selectedAudio, _currentWorkingVideo);
					_oW.execute();

					//Disable function buttons
					disableFunctions();
				}
			}
		});

		_overlayButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String[] outputVideoExists;

				//Check to see of there is an editable video playing.
				if (PlayerPane.getInstance().getMediaPath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: No video to edit. Please select a file by " +
							"playing one using the Open button.");
					return;
				}

				//If no audio selected, send error.
				if (_selectedAudio == null) {
					JOptionPane.showMessageDialog(null, "Error: No audio selected to replace / " +
							"overlay with. Please select an audio file using the button above.");
					return;
				}

				//Open new save chooser for video output name.
				saveVideoChooser = new SaveOutputChooser();
				saveVideoChooser.setDialogTitle("Save Overlayed Audio Video");

				//Use bash to check if output video already exists.
				outputVideoExists = new BashCommand().runBash("if [ ! -f " + 
						saveVideoChooser.getSavePath() + ".mp4" + " ]; then echo 0; else echo 1; fi");

				boolean canOverlay = false;

				//Send error if no file name given.
				if (saveVideoChooser.getSavePath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for output video.");
					return;
				}
				
				//Ask for overwrite if output video file already exists.
				if (outputVideoExists[0].equals("1") && !saveVideoChooser.getSavePath().equals("")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(audioPane, saveVideoChooser.getSavePath() + ".mp4" 
							+ " already exists. Overwrite file? Please choose a new filename if not overriding.", "File " +
									"Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canOverlay = true;
					}
				} else {
					//Else replace is fine.
					canOverlay = true;
				}	

				if (canOverlay) {
					//Set processBar to processing.
					_processBar.setIndeterminate(true);
					_processBar.setString("Overlay In Progress");

					//Start new OverlayWorker to overlay.
					_currentWorkingVideo = new File(PlayerPane.getInstance().getMediaPath());
					_oW = new OverlayWorker(audioPane, false, _selectedAudio, _currentWorkingVideo);
					_oW.execute();

					//Disable function buttons
					disableFunctions();
				}
			}
		});

		_cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Cancel all workers available.
				if (_sW != null) {
					_sW.cancel(true);
				}
				if (_eW != null) {
					_eW.cancel(true);
				}
				if (_oW != null) {
					_oW.cancel(true);
				}
			}
		});
	}

	/**
	 * Enable function buttons.
	 */
	public void enableFunctions() {
		_stripButton.setEnabled(true);
		_replaceButton.setEnabled(true);
		_overlayButton.setEnabled(true);
	}

	/**
	 * Disable function buttons.
	 */
	public void disableFunctions() {
		_stripButton.setEnabled(false);
		_replaceButton.setEnabled(false);
		_overlayButton.setEnabled(false);
	}
}
