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
public class AudioPane extends JPanel implements ActionListener {

	private final AudioPane audioPane = this;
	
	private final JPanel _stripPanel = new JPanel(new BorderLayout());
	private final JPanel _stripCBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
	private final JPanel _stripButtonPanel = new JPanel();
	private final JPanel _audioSelectPanel = new JPanel(new BorderLayout());
	private final JPanel _audioSelectSubPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
	private final JPanel _audioButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
	private final JPanel _outputNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
	private final JPanel _bottomButtonPanel = new JPanel(new BorderLayout());
	private final JPanel _bottomSubButtonPanel = new JPanel(new GridLayout(0,3));
	
	private final JLabel _stripLabel = new JLabel("(For Strip Function)");
	private final JLabel _outputAudioLabel = new JLabel("Output Audio Name: (For Stripping) ");
	private final JLabel _outputVideoLabel = new JLabel("Output Video Name: (For All Functions)");
	private final JLabel _replaceAndOverlayLabel = new JLabel("(For Replace/Overlay Functions)");
	
	private final JTextField _chosenAudioInput = new JTextField(25);
	final JTextField _chosenVideoName = new JTextField(25);
	final JTextField _chosenAudioName = new JTextField(25);
	
	private final JButton _audioFileButton = new JButton("Select Audio File");
	private final JButton _audioPreviewButton = new JButton("Preview Audio File");
	private final JButton _stripButton = new JButton("Strip Audio");
	private final JButton _cancelButton = new JButton("Cancel");
	private final JButton _replaceButton = new JButton("Replace Audio");
	private final JButton _overlayButton = new JButton("Overlay Audio");
	
	private final JCheckBox _removeAudioOnVideo = new JCheckBox("Remove Stripped Audio on Video?");
	private final JCheckBox _haveAudioOutput = new JCheckBox("Create Output of Stripped Audio?");
	
	final JProgressBar _processBar = new JProgressBar();

	ExtractWorker _eW;
	StripWorker _sW;
	private OverlayWorker _oW;

	private File _selectedAudio;
	private File _currentWorkingVideo;

	public AudioPane() {

		//Set layout.
		setLayout(new FlowLayout());

		//Set preferred dimensions of panels (and process bar).
		_audioSelectPanel.setPreferredSize(new Dimension(385, 100));
		_outputNamePanel.setPreferredSize(new Dimension(385, 140));

		_bottomButtonPanel.setPreferredSize(new Dimension(385, 100));
		_bottomSubButtonPanel.setPreferredSize(new Dimension(385, 40));

		//Add and construct strip options panel.
		add(_stripPanel);
		_stripPanel.setBorder(BorderFactory.createTitledBorder("Strip Audio:"));
		_stripPanel.setPreferredSize(new Dimension(385, 100));
		_stripCBoxPanel.setPreferredSize(new Dimension(260, 100));
		
		_stripPanel.add(_stripCBoxPanel, BorderLayout.CENTER);
		_stripPanel.add(_stripButtonPanel, BorderLayout.SOUTH);
		_stripCBoxPanel.add(_removeAudioOnVideo);
		_stripCBoxPanel.add(_haveAudioOutput);
		_stripButtonPanel.add(_stripButton);

		//Add and construct audio selection panel.
		add(_audioSelectPanel);
		_audioSelectPanel.setBorder(BorderFactory.createTitledBorder("Audio Select:"));
		_audioSelectPanel.add(_replaceAndOverlayLabel, BorderLayout.NORTH);
		_audioSelectPanel.add(_audioSelectSubPanel, BorderLayout.CENTER);
		_audioSelectSubPanel.add(_chosenAudioInput);
		_audioSelectSubPanel.add(_audioButtonPanel);
		_audioButtonPanel.add(_audioFileButton);
		_audioButtonPanel.add(_audioPreviewButton);

		_chosenAudioInput.setEditable(false);

		//Add and construct output naming area panel.
		add(_outputNamePanel);
		_outputNamePanel.setBorder(BorderFactory.createTitledBorder("Output Names:"));
		_outputNamePanel.add(_outputAudioLabel);
		_outputNamePanel.add(_chosenAudioName);
		_outputNamePanel.add(_outputVideoLabel);
		_outputNamePanel.add(_chosenVideoName);

		//Add process bar.
		//add(_processPanel);
		//_processPanel.
		_processBar.setPreferredSize(new Dimension(385, 30));
		add(_processBar);
		_processBar.setString("No Tasks Being Performed");
		_processBar.setStringPainted(true);

		//Add and construct function button panel.
		add(_bottomButtonPanel);
		_bottomButtonPanel.setBorder(BorderFactory.createTitledBorder("Audio Functions:"));
		_bottomButtonPanel.add(_bottomSubButtonPanel, BorderLayout.NORTH);
		_bottomButtonPanel.add(_cancelButton, BorderLayout.CENTER);
		//_bottomSubButtonPanel.add(_stripButton);
		_bottomSubButtonPanel.add(_replaceButton);
		_bottomSubButtonPanel.add(_overlayButton);

		//Set format of text on function buttons.
		_stripButton.setText("<html><center>"+"Strip"+"<br>"+"Audio"+"</center></html>");
		_replaceButton.setText("<html><center>"+"Replace"+"<br>"+"Audio"+"</center></html>");
		_overlayButton.setText("<html><center>"+"Overlay"+"<br>"+"Audio"+"</center></html>");

		//Add new listener to button to open up a file chooser.
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
						System.out.println(type);
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

		setListeners();
	}

	private void setListeners() {

		//Add listeners to the other buttons.
		_audioPreviewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				//If no audio selected, send error.
				if (_selectedAudio == null) {
					JOptionPane.showMessageDialog(null, "Error: No audio selected to replace / " +
							"overlay with. Please select an audio file using the button above.");

					//If preview button, begin preview on main player.
				} else {
					PlayerPane.getInstance().tempPlay(_selectedAudio.getAbsolutePath());
				}
			}
		});

		_stripButton.addActionListener(new  ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//Use bash to check if output audio and video filenames exist.
				String[] outputAudioExists = new BashCommand().runBash("if [ ! -f " + _chosenAudioName.getText() + " ]; then echo 0; else echo 1; fi");
				String[] outputVideoExists = new BashCommand().runBash("if [ ! -f " + _chosenVideoName.getText() + " ]; then echo 0; else echo 1; fi");
				
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
				//Handle special case when both options are selected.
				else if (_removeAudioOnVideo.isSelected() && _haveAudioOutput.isSelected()) {
					boolean validAudio = true;
					boolean validVideo = true;

					//If no output audio name is specified, send error.
					if (_chosenAudioName.getText().equals("")) {
						JOptionPane.showMessageDialog(null, "Error: Please supply name for extracted " +
								"audio from stripped video.");
						validAudio = false;

						//If no output video name is specified, send error.
					} else if (_chosenVideoName.getText().equals("")) {
						JOptionPane.showMessageDialog(null, "Error: Please supply name for output " +
								"video.");
						validVideo = false;

					} else {
						//Ask to overwrite if audio name already exists.
						if (outputAudioExists[0].equals("1")) {
							Object[] options = { "Overwrite", "Cancel" };
							int enableOverwrite = JOptionPane.showOptionDialog(audioPane, _chosenAudioName.getText() + " already exists. Overwrite file? Please choose a new filename if not overriding.", "File Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							if (enableOverwrite == 1) {
								validAudio = false;
							}

							//Ask to overwrite if video name already exists.
						} else if (outputVideoExists[0].equals("1")) {
							Object[] options = { "Overwrite", "Cancel" };
							int enableOverwrite = JOptionPane.showOptionDialog(audioPane, _chosenVideoName.getText() + " already exists. Overwrite file? Please choose a new filename if not overriding.", "File Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							if (enableOverwrite == 1) {
								validVideo = false;
							}
						}
					}

					//If both audio and video are valid, proceed to strip.
					if (validAudio && validVideo) {
						canStrip = true;
					}

					//No output audio name given, send error.
				} else if (_haveAudioOutput.isSelected() && _chosenAudioName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for extracted " +
							"audio from stripped video.");

					//Output audio named already exists, ask for overwrite.
				} else if (_haveAudioOutput.isSelected() && outputAudioExists[0].equals("1")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(audioPane, _chosenAudioName.getText() + " already exists. " +
							"Overwrite file? Please choose a new filename if not overriding.", "File Already " +
									"Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canStrip = true;
					}

					//No Output video name given, send error.
				}  else if (_removeAudioOnVideo.isSelected() && _chosenVideoName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for stripped video.");

					//Output video named already exists, ask for overwrite.
				} else if (_removeAudioOnVideo.isSelected() && outputVideoExists[0].equals("1")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(audioPane, _chosenVideoName.getText() + " already exists. " +
							"Overwrite file? Please choose a new filename if not overriding.", "File Already " +
									"Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canStrip = true;
					}

					//Else stripping should be fine.
				} else {
					canStrip = true;
				}

				if (canStrip) { 

					SaveOutputChooser chooser = new SaveOutputChooser();

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
		_replaceButton.addActionListener(this);
		_overlayButton.addActionListener(this);
		_cancelButton.addActionListener(this);
	}

	/**
	 * Method explaining actions performed depending on
	 * which button is pressed and a series of error
	 * checking.
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		//Use bash to check if output audio and video filenames exist.
		String[] outputAudioExists = new BashCommand().runBash("if [ ! -f " + _chosenAudioName.getText() + " ]; then echo 0; else echo 1; fi");
		String[] outputVideoExists = new BashCommand().runBash("if [ ! -f " + _chosenVideoName.getText() + " ]; then echo 0; else echo 1; fi");

		//If cancel is pressed:
		if (ae.getSource() == _cancelButton) {
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

			//Check to see of there is an editable video to use.
		} else if (PlayerPane.getInstance().getMediaPath().equals("")) {
			JOptionPane.showMessageDialog(null, "Error: No video to edit. Please select a file by " +
					"playing one using the Open button or using the 'Files' " +
					"tab on the right.");

			//If Strip button is pressed:
		} else if (ae.getSource() == _stripButton) {
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
			//Handle special case when both options are selected.
			else if (_removeAudioOnVideo.isSelected() && _haveAudioOutput.isSelected()) {
				boolean validAudio = true;
				boolean validVideo = true;

				//If no output audio name is specified, send error.
				if (_chosenAudioName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for extracted " +
							"audio from stripped video.");
					validAudio = false;

					//If no output video name is specified, send error.
				} else if (_chosenVideoName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for output " +
							"video.");
					validVideo = false;

				} else {
					//Ask to overwrite if audio name already exists.
					if (outputAudioExists[0].equals("1")) {
						Object[] options = { "Overwrite", "Cancel" };
						int enableOverwrite = JOptionPane.showOptionDialog(this, _chosenAudioName.getText() + " already exists. Overwrite file? Please choose a new filename if not overriding.", "File Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						if (enableOverwrite == 1) {
							validAudio = false;
						}

						//Ask to overwrite if video name already exists.
					} else if (outputVideoExists[0].equals("1")) {
						Object[] options = { "Overwrite", "Cancel" };
						int enableOverwrite = JOptionPane.showOptionDialog(this, _chosenVideoName.getText() + " already exists. Overwrite file? Please choose a new filename if not overriding.", "File Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						if (enableOverwrite == 1) {
							validVideo = false;
						}
					}
				}

				//If both audio and video are valid, proceed to strip.
				if (validAudio && validVideo) {
					canStrip = true;
				}

				//No output audio name given, send error.
			} else if (_haveAudioOutput.isSelected() && _chosenAudioName.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Error: Please supply name for extracted " +
						"audio from stripped video.");

				//Output audio named already exists, ask for overwrite.
			} else if (_haveAudioOutput.isSelected() && outputAudioExists[0].equals("1")) {
				Object[] options = { "Overwrite", "Cancel" };
				int enableOverwrite = JOptionPane.showOptionDialog(this, _chosenAudioName.getText() + " already exists. " +
						"Overwrite file? Please choose a new filename if not overriding.", "File Already " +
								"Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				if (enableOverwrite == 0) {
					canStrip = true;
				}

				//No Output video name given, send error.
			}  else if (_removeAudioOnVideo.isSelected() && _chosenVideoName.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Error: Please supply name for stripped video.");

				//Output video named already exists, ask for overwrite.
			} else if (_removeAudioOnVideo.isSelected() && outputVideoExists[0].equals("1")) {
				Object[] options = { "Overwrite", "Cancel" };
				int enableOverwrite = JOptionPane.showOptionDialog(this, _chosenVideoName.getText() + " already exists. " +
						"Overwrite file? Please choose a new filename if not overriding.", "File Already " +
								"Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				if (enableOverwrite == 0) {
					canStrip = true;
				}

				//Else stripping should be fine.
			} else {
				canStrip = true;
			}

			if (canStrip) { 

				SaveOutputChooser chooser = new SaveOutputChooser();

				//Set processBar to processing.
				_processBar.setIndeterminate(true);
				_processBar.setString("Strip In Progress");

				//Start new StripWorker.
				_currentWorkingVideo = new File(PlayerPane.getInstance().getMediaPath());
				_sW = new StripWorker(this, _removeAudioOnVideo.isSelected(), _haveAudioOutput.isSelected(), _currentWorkingVideo);
				_sW.execute();

				//Disable function buttons
				disableFunctions();
			}

		} else {

			//Must be related to replace and overlay functionality.

			//If no audio selected, send error.
			if (_selectedAudio == null) {
				JOptionPane.showMessageDialog(null, "Error: No audio selected to replace / " +
						"overlay with. Please select an audio file using the button above.");

				//If preview button, begin preview on main player.
			} else if (ae.getSource() == _audioPreviewButton) {
				PlayerPane.getInstance().tempPlay(_selectedAudio.getAbsolutePath());

				//If no output video name specified, send error.
			} else if (_chosenVideoName.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Error: Please supply name for output " +
						"video.");

				//If replace button:
			} else if (ae.getSource() == _replaceButton) {
				boolean canReplace = false;

				//Ask for overwrite if output video file already exists.
				if (outputVideoExists[0].equals("1")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(this, _chosenVideoName.getText() + " already " +
							"exists. Overwrite file? Please choose a new filename if not overriding.", "File " +
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
					_oW = new OverlayWorker(this, true, _selectedAudio, _currentWorkingVideo);
					_oW.execute();

					//Disable function buttons
					disableFunctions();
				}

			} else if (ae.getSource() == _overlayButton) {
				boolean canOverlay = false;

				//Ask for overwrite if output video file already exists.
				if (outputVideoExists[0].equals("1")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(this, _chosenVideoName.getText() + " already " +
							"exists. Overwrite file? Please choose a new filename if not overriding.", "File " +
									"Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (enableOverwrite == 0) {
						canOverlay = true;
					}
				} else {
					//Else overlay is fine.
					canOverlay = true;
				}

				if (canOverlay) {
					//Set processBar to processing.
					_processBar.setIndeterminate(true);
					_processBar.setString("Overlay In Progress");

					//Start new OverlayWorker to overlay.
					_currentWorkingVideo = new File(PlayerPane.getInstance().getMediaPath());
					_oW = new OverlayWorker(this, false, _selectedAudio, _currentWorkingVideo);
					_oW.execute();

					//Disable function buttons
					disableFunctions();
				}
			}
		} 
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
