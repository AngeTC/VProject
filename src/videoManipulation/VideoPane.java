package videoManipulation;

import helperAndResourceClasses.BashCommand;
import helperAndResourceClasses.SaveOutputChooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import vlcPlayer.PlayerPane;


@SuppressWarnings("serial")
public class VideoPane extends JPanel {

	private VideoPane videoPane = this;

	private JPanel _orientationAndVideoSelectPanel = new JPanel(new BorderLayout());
	private JPanel _orientationPanel = new JPanel(new GridLayout(1,2));
	private JPanel _rotatePanel = new JPanel(new BorderLayout());
	private JPanel _flipPanel = new JPanel(new BorderLayout());
	private JPanel _rotateButtonPanel = new JPanel();
	private JPanel _flipButtonPanel = new JPanel();
	private JPanel _fadePanel = new JPanel(new BorderLayout());
	private JPanel _fadeTimePanel = new JPanel(new BorderLayout());
	private JPanel _startFadeTimePanel = new JPanel(new BorderLayout());
	private JPanel _fadeDurationTimePanel = new JPanel(new BorderLayout());
	private JPanel _startTimeSpinnerPanel = new JPanel();
	private JPanel _durationSpinnerPanel = new JPanel();
	private JPanel _fadeButtonPanel = new JPanel();
	private JPanel _processAndCancelPanel = new JPanel(new BorderLayout());

	private JLabel _rotateLabel = new JLabel("Rotate Video:");
	private JLabel _flipLabel = new JLabel("Flip Video:");

	private String[] _rotateOptions = {"90°", "180°", "270°"};
	private String[] _flipOptions = {"Flip Vertically", "Flip Horizontally"};
	private String[] _fadeOptions = {"Fade In", "Fade Out"};

	private JComboBox<String> _rotateSelect = new JComboBox<String>(_rotateOptions);
	private JComboBox<String> _flipSelect = new JComboBox<String>(_flipOptions);
	private JComboBox<String> _fadeSelect = new JComboBox<String>(_fadeOptions);

	private JButton _applyRotate = new JButton("Apply Rotate");
	private JButton _applyFlip = new JButton("Apply Flip");
	private JButton _applyFade = new JButton("Apply Fade");
	private JButton _cancelButton = new JButton("Cancel");

	private final JLabel _startTimeLabel = new JLabel("Start Time of Fade (hh:mm:ss)");
	private final JSpinner _hoursSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
	private final JSpinner _minsSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JSpinner _secsSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JLabel _timeSeparator1 = new JLabel(":");
	private final JLabel _timeSeparator2 = new JLabel(":");
	private final JLabel _durationLabel = new JLabel("Fade Duration (mm:ss)");
	private final JSpinner _minsSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JSpinner _secsSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JLabel _timeSeparator3 = new JLabel(":");

	JProgressBar _processBar = new JProgressBar();

	private File _selectedVideo;

	private OrientationWorker _oriWorker;
	private FadeWorker _fadeWorker;

	private SaveOutputChooser saveVideoChooser;

	/**
	 * Constructor for VideoPane
	 */
	public VideoPane() {
		
		_orientationAndVideoSelectPanel.add(_orientationPanel, BorderLayout.CENTER);

		_orientationPanel.setPreferredSize(new Dimension(370, 100));
		_orientationPanel.setBorder(BorderFactory.createTitledBorder("Video Orientation:"));
		_orientationPanel.add(_rotatePanel);
		_orientationPanel.add(_flipPanel);

		_rotatePanel.add(_rotateLabel, BorderLayout.NORTH);
		_rotatePanel.add(_rotateSelect, BorderLayout.CENTER);
		_rotatePanel.add(_rotateButtonPanel, BorderLayout.SOUTH);
		_rotateButtonPanel.add(_applyRotate);

		_flipPanel.add(_flipLabel, BorderLayout.NORTH);
		_flipPanel.add(_flipSelect, BorderLayout.CENTER);
		_flipPanel.add(_flipButtonPanel, BorderLayout.SOUTH);
		_flipButtonPanel.add(_applyFlip);

		add(_orientationAndVideoSelectPanel);

		//Fade Function Layout:
		_fadePanel.setBorder(BorderFactory.createTitledBorder("Add Fade to Video:"));
		_fadePanel.setPreferredSize(new Dimension(370, 150));
		_fadePanel.add(_fadeTimePanel, BorderLayout.CENTER);
		_fadePanel.add(_fadeButtonPanel, BorderLayout.SOUTH);

		_fadeButtonPanel.add(_fadeSelect);
		_fadeButtonPanel.add(_applyFade);

		_fadeTimePanel.setPreferredSize(new Dimension(360, 100));
		_fadeTimePanel.add(_startFadeTimePanel, BorderLayout.NORTH);
		_fadeTimePanel.add(_fadeDurationTimePanel, BorderLayout.CENTER);

		_startFadeTimePanel.add(_startTimeLabel, BorderLayout.NORTH);
		_startFadeTimePanel.add(_startTimeSpinnerPanel, BorderLayout.CENTER);
		_startTimeSpinnerPanel.add(_hoursSpinner1);
		_startTimeSpinnerPanel.add(_timeSeparator1);
		_startTimeSpinnerPanel.add(_minsSpinner1);
		_startTimeSpinnerPanel.add(_timeSeparator2);
		_startTimeSpinnerPanel.add(_secsSpinner1);

		_fadeDurationTimePanel.add(_durationLabel, BorderLayout.NORTH);
		_fadeDurationTimePanel.add(_durationSpinnerPanel, BorderLayout.CENTER);
		_durationSpinnerPanel.add(_minsSpinner2);
		_durationSpinnerPanel.add(_timeSeparator3);
		_durationSpinnerPanel.add(_secsSpinner2);

		add(_fadePanel);

		//Processing Bar Layout:
		_processAndCancelPanel.setPreferredSize(new Dimension(370, 60));
		_processAndCancelPanel.add(_processBar, BorderLayout.CENTER);
		_processAndCancelPanel.add(_cancelButton, BorderLayout.SOUTH);

		_processBar.setString("No Tasks Being Performed");
		_processBar.setStringPainted(true);
		_processBar.setPreferredSize(new Dimension(370, 30));

		add(_processAndCancelPanel);

		setListeners();
	}


	private void setListeners() {

		//Add action listeners:
		_applyRotate.addActionListener(new ActionListener() {

			String[] outputVideoExists;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (PlayerPane.getInstance().getMediaPath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: No video to edit. Please select a file by " +
							"playing one using the Open button.");
					return;
				}

				//Open new save chooser for video output name.
				saveVideoChooser = new SaveOutputChooser();
				saveVideoChooser.setDialogTitle("Save Rotated Video");

				//Use bash to check if output video already exists.
				outputVideoExists = new BashCommand().runBash("if [ ! -f " + 
						saveVideoChooser.getSavePath() + ".mp4" + " ]; then echo 0; else echo 1; fi");

				boolean canRotate = false;

				//Send error if no file name given.
				if (saveVideoChooser.getSavePath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for output video.");
					return;
				}

				//Ask for overwrite if output video file already exists.
				if (outputVideoExists[0].equals("1") && !saveVideoChooser.getSavePath().equals("")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(null, saveVideoChooser.getSavePath() + ".mp4" 
							+ " already exists. Overwrite file? Please choose a new filename if not overriding.", "File " +
									"Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canRotate = true;
					}
				} else {
					//Else rotate is fine.
					canRotate = true;
				}

				if (canRotate) {

					_selectedVideo = new File(PlayerPane.getInstance().getMediaPath());
					String rotateOption = _rotateOptions[(_rotateSelect.getSelectedIndex())].replace("°", "");

					_oriWorker = new OrientationWorker(videoPane, _selectedVideo, saveVideoChooser.getSavePath() 
							+ ".mp4", rotateOption, false);
					_oriWorker.execute();

					_processBar.setIndeterminate(true);
					_processBar.setString("Rotating In Progress");

					disableFunctions();
				}
			}
		});

		_applyFlip.addActionListener(new ActionListener() {

			String[] outputVideoExists;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (PlayerPane.getInstance().getMediaPath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: No video to edit. Please select a file by " +
							"playing one using the Open button.");
					return;
				}

				//Open new save chooser for video output name.
				saveVideoChooser = new SaveOutputChooser();
				saveVideoChooser.setDialogTitle("Save Flipped Video");

				//Use bash to check if output video already exists.
				outputVideoExists = new BashCommand().runBash("if [ ! -f " + 
						saveVideoChooser.getSavePath() + ".mp4" + " ]; then echo 0; else echo 1; fi");

				boolean canFlip = false;

				//Send error if no file name given.
				if (saveVideoChooser.getSavePath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for output video.");
					return;
				}

				//Ask for overwrite if output video file already exists.
				if (outputVideoExists[0].equals("1") && !saveVideoChooser.getSavePath().equals("")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(null, saveVideoChooser.getSavePath() + ".mp4" 
							+ " already exists. Overwrite file? Please choose a new filename if not overriding.", "File " +
									"Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canFlip = true;
					}
				} else {
					//Else flip is fine.
					canFlip = true;
				}

				if (canFlip) {

					_selectedVideo = new File(PlayerPane.getInstance().getMediaPath());
					String rotateOption = _flipOptions[(_flipSelect.getSelectedIndex())];

					_oriWorker = new OrientationWorker(videoPane, _selectedVideo, saveVideoChooser.getSavePath() 
							+ ".mp4", rotateOption, true);
					_oriWorker.execute();

					_processBar.setIndeterminate(true);
					_processBar.setString("Flipping In Progress");

					disableFunctions();
				}
			}
		});

		_applyFade.addActionListener(new ActionListener() {

			String[] outputVideoExists;
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Send error if no video is selected.
				if (PlayerPane.getInstance().getMediaPath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: No video to edit. Please select a file by " +
							"playing one using the Open button.");
					return;
				} 
				
				int selectedVideoFPS = PlayerPane.getInstance().getFPS();
				int startFadeTime = getTimeInSeconds(_hoursSpinner1.getValue().toString(), 
						_minsSpinner1.getValue().toString(), _secsSpinner1.getValue().toString());
				int fadeDuration = getTimeInSeconds("0", 
						_minsSpinner2.getValue().toString(), _secsSpinner2.getValue().toString());

				if(fadeDuration == 0) {
					JOptionPane.showMessageDialog(null, "Please enter a duration greater than 0.");	
					return;

				} 
				//Send error if the start of fade duration is greater 
				//than the length of the video.
				if(startFadeTime > PlayerPane.getInstance().getPlayTime()) {
					JOptionPane.showMessageDialog(null, "Start of fade exceeds the length of the " +
							"video. Please revise");	

				} 

				//Ask for output file name:
				//Open new save chooser for video output name.
				saveVideoChooser = new SaveOutputChooser();
				saveVideoChooser.setDialogTitle("Save Faded Video");

				//Use bash to check if output video already exists.
				outputVideoExists = new BashCommand().runBash("if [ ! -f " + 
						saveVideoChooser.getSavePath() + ".mp4" + " ]; then echo 0; else echo 1; fi");

				boolean canFade = false;

				if (saveVideoChooser.getSavePath().equals("")) {
					//If no output name was given, send error.
					JOptionPane.showMessageDialog(null, "No output name given. Please revise.");	

				} //Ask for overwrite if output video file already exists.
				if (outputVideoExists[0].equals("1") && !saveVideoChooser.getSavePath().equals("")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(null, saveVideoChooser.getSavePath() + ".mp4" 
							+ " already exists. Overwrite file? Please choose a new filename if not overriding.", "File " +
									"Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canFade = true;
					}
				} else {
					//Else adding fade is fine.
					canFade = true;
				}

				if (canFade) {
					_selectedVideo = new File(PlayerPane.getInstance().getMediaPath());
					String fadeSelection = _fadeOptions[_fadeSelect.getSelectedIndex()];
					String startFrame = Integer.toString(selectedVideoFPS * startFadeTime); 
					String durationOfFrames = Integer.toString(selectedVideoFPS * fadeDuration);

					_fadeWorker = new FadeWorker(videoPane, _selectedVideo, saveVideoChooser.getSavePath() + ".mp4", 
							fadeSelection, startFrame, durationOfFrames);
					_fadeWorker.execute();

					_processBar.setIndeterminate(true);
					_processBar.setString("Fade In Progress");

					disableFunctions();
				}
			}
		});


		_cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Cancel all workers available.
				if (_oriWorker != null) {
					_oriWorker.cancel(true);
				}
				if (_fadeWorker != null) {
					_fadeWorker.cancel(true);
				}
			}
		});
	} 

	/**
	 * Enables Function Buttons;
	 */
	public void enableFunctions() {
		_applyRotate.setEnabled(true);
		_applyFlip.setEnabled(true);
		_applyFade.setEnabled(true);
	}

	/**
	 * Disables function buttons.
	 */
	public void disableFunctions() {
		_applyRotate.setEnabled(false);
		_applyFlip.setEnabled(false);
		_applyFade.setEnabled(false);
	}

	/**
	 * Returns duration time in seconds(int) given strings
	 * of hours, minutes and seconds.
	 */
	public int getTimeInSeconds(String hours, String mins, String secs) {

		int hoursInSecs = Integer.parseInt(hours) * 360;
		int minsInSecs = Integer.parseInt(mins) * 60;
		int finalSecs = Integer.parseInt(secs);

		int outTime = hoursInSecs + minsInSecs + finalSecs;
		return outTime;
	}
}