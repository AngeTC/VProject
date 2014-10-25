package videoManipulation;

import helperAndResourceClasses.BashCommand;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;


@SuppressWarnings("serial")
public class VideoPane extends JPanel implements ActionListener {

	private JPanel _orientationAndVideoSelectPanel = new JPanel(new BorderLayout());
	private JPanel _videoSelectPanel = new JPanel();
	private JPanel _orientationPanel = new JPanel(new GridLayout(1,2));
	private JPanel _rotatePanel = new JPanel(new BorderLayout());
	private JPanel _flipPanel = new JPanel(new BorderLayout());
	private JPanel _rotateButtonPanel = new JPanel();
	private JPanel _flipButtonPanel = new JPanel();
	private JPanel _speedChangePanel = new JPanel();
	private JPanel _fadePanel = new JPanel(new BorderLayout());
	private JPanel _fadeTimePanel = new JPanel(new BorderLayout());
	private JPanel _startFadeTimePanel = new JPanel(new BorderLayout());
	private JPanel _fadeDurationTimePanel = new JPanel(new BorderLayout());
	private JPanel _startTimeSpinnerPanel = new JPanel();
	private JPanel _durationSpinnerPanel = new JPanel();
	private JPanel _fadeButtonPanel = new JPanel();
	private JPanel _processAndCancelPanel = new JPanel(new BorderLayout());

	private JTextField _videoSelectField = new JTextField(15);

	private JLabel _rotateLabel = new JLabel("Rotate Video:");
	private JLabel _flipLabel = new JLabel("Flip Video:");

	private String[] _rotateOptions = {"90°", "180°", "270°"};
	private String[] _flipOptions = {"Flip Vertically", "Flip Horizontally"};
	private String[] _speedOptions = {"2x", "1.5x", "0.75x", "0.5x",};
	private String[] _fadeOptions = {"Fade In", "Fade Out"};

	private JComboBox<String> _rotateSelect = new JComboBox<String>(_rotateOptions);
	private JComboBox<String> _flipSelect = new JComboBox<String>(_flipOptions);
	private JComboBox<String> _speedSelect = new JComboBox<String>(_speedOptions);
	private JComboBox<String> _fadeSelect = new JComboBox<String>(_fadeOptions);

	private JButton _applyRotate = new JButton("Apply Rotate");
	private JButton _applyFlip = new JButton("Apply Flip");
	private JButton _videoSelectButton = new JButton("Select Video To Edit");
	private JButton _applySpeedChange = new JButton("Apply Speed Change");
	private JButton _applyFade = new JButton("Apply Fade");
	private JButton _cancelButton = new JButton("Cancel");

	private final JLabel _startTimeLabel = new JLabel("Start Time of Fade (hh:mm:ss)");
	private final JSpinner _hoursSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
	private final JSpinner _minsSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JSpinner _secsSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JLabel _timeSep1 = new JLabel(":");
	private final JLabel _timeSep2 = new JLabel(":");
	private final JLabel _durationLabel = new JLabel("Fade Duration (hh:mm:ss)");
	private final JSpinner _hoursSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
	private final JSpinner _minsSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JSpinner _secsSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JLabel _timeSep3 = new JLabel(":");
	private final JLabel _timeSep4 = new JLabel(":");

	JProgressBar _processBar = new JProgressBar();

	private File _selectedVideo;

	private OrientationWorker _oriWorker;
	private SpeedChangeWorker _spdWorker;
	private FadeWorker _fadeWorker;

	public VideoPane() {

		//Orientation (Rotate and flip) Layout:
		_orientationAndVideoSelectPanel.add(_orientationPanel, BorderLayout.CENTER);
//		//_orientationAndVideoSelectPanel.add(_videoSelectPanel, BorderLayout.NORTH);
//
//		_videoSelectPanel.setBorder(BorderFactory.createTitledBorder("Select Video To Edit:"));
//		_videoSelectPanel.setPreferredSize(new Dimension(370, 70));
//		_videoSelectPanel.add(_videoSelectField);
//		_videoSelectField.setEditable(false);
//		_videoSelectPanel.add(_videoSelectButton);

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

		//Speed Changing Function Layout:
		_speedChangePanel.setBorder(BorderFactory.createTitledBorder("Change Speed of Video:"));
		_speedChangePanel.setPreferredSize(new Dimension(370, 80));
		_speedChangePanel.add(_speedSelect);
		_speedChangePanel.add(_applySpeedChange);

		add(_speedChangePanel);

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
		_startTimeSpinnerPanel.add(_timeSep1);
		_startTimeSpinnerPanel.add(_minsSpinner1);
		_startTimeSpinnerPanel.add(_timeSep2);
		_startTimeSpinnerPanel.add(_secsSpinner1);

		_fadeDurationTimePanel.add(_durationLabel, BorderLayout.NORTH);
		_fadeDurationTimePanel.add(_durationSpinnerPanel, BorderLayout.CENTER);
		_durationSpinnerPanel.add(_hoursSpinner2);
		_durationSpinnerPanel.add(_timeSep3);
		_durationSpinnerPanel.add(_minsSpinner2);
		_durationSpinnerPanel.add(_timeSep4);
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

		//Add action listeners:
		_applyRotate.addActionListener(this);
		_applyFlip.addActionListener(this);
		_videoSelectButton.addActionListener(this);
		_applySpeedChange.addActionListener(this);
		_applyFade.addActionListener(this);
		_cancelButton.addActionListener(this);
	} 


	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == _cancelButton) {
			//Cancel all workers available.
			if (_oriWorker != null) {
				_oriWorker.cancel(true);
			}
			if (_spdWorker != null) {
				_spdWorker.cancel(true);
			}
			if (_fadeWorker != null) {
				_fadeWorker.cancel(true);
			}
			
			System.out.println(getDurationTime(_selectedVideo.getAbsolutePath())); //TODO
			
		} else if (ae.getSource() == _videoSelectButton) {
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
					if(type.contains("video")) {

						//Set it as new selected video.
						_selectedVideo = selectedFile;
						_videoSelectField.setText(selectedFile.getName());

						System.out.println(getFPS(_selectedVideo.getAbsolutePath()));
					} else {
						//Else return error.
						JOptionPane.showMessageDialog(null, "File selected is not a video " +
								"file. Please select another file.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 

		} else if (ae.getSource() == _applyRotate || ae.getSource() == _applyFlip) {

			//Send error if no video is selected.
			if (_videoSelectField.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "No video file to edit has been selected. " +
						"Please select a file.");	
			} else {
				//Ask for output file name:
				String outputFile = JOptionPane.showInputDialog(null, "Enter output video file name:", "Output File Name",
						JOptionPane.WARNING_MESSAGE);

				boolean canRotateOrFlip = false;

				if (outputFile.equals("")) {
					//If no output name was given, send error.
					JOptionPane.showMessageDialog(null, "No output name given. Please revise.");	

				} else if (outputFile != null) {
					//Check if output file already exists.
					String wd = System.getProperty("user.dir");
					String path = wd + "/" + outputFile + ".mp4";

					String[] outputNameExists = new BashCommand().runBash("if [ ! -f " + path + " ]; then echo 0; else echo 1; fi");

					if (outputNameExists[0].equals("1")) {
						//If file exists, ask for overwrite:
						int selectedOption = JOptionPane.showConfirmDialog(null, 
								"Output file exists. Overwrite?", 
								"Choose", 
								JOptionPane.YES_NO_OPTION); 
						if (selectedOption == JOptionPane.YES_OPTION) {
							new BashCommand().runBash("rm " + path);
							canRotateOrFlip = true;
						} else {
							canRotateOrFlip = false;
						}
					} else {
						canRotateOrFlip = true;
					}
				}


				if (canRotateOrFlip) {
					if (ae.getSource() == _applyRotate) {
						String rotateOption = _rotateOptions[(_rotateSelect.getSelectedIndex())].replace("°", "");
						_oriWorker = new OrientationWorker(this, _selectedVideo, outputFile + ".mp4", rotateOption, false);
						_oriWorker.execute();

						_processBar.setIndeterminate(true);
						_processBar.setString("Rotating In Progress");

						disableFunctions();
					} else {
						String flipOption = _flipOptions[(_flipSelect.getSelectedIndex())];
						_oriWorker = new OrientationWorker(this, _selectedVideo, outputFile + ".mp4", flipOption, true);
						_oriWorker.execute();

						_processBar.setIndeterminate(true);
						_processBar.setString("Video Flip In Progress");

						disableFunctions();
					}
				}
			} 

		} else if (ae.getSource() == _applySpeedChange) {

			//Send error if no video is selected.
			if (_videoSelectField.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "No video file to edit has been selected. " +
						"Please select a file.");	
			} else {
				//Ask for output file name:
				String outputFile = JOptionPane.showInputDialog(null, "Enter output video file name:", "Output File Name",
						JOptionPane.WARNING_MESSAGE);

				boolean canChangeSpeed = false;

				if (outputFile.equals("")) {
					//If no output name was given, send error.
					JOptionPane.showMessageDialog(null, "No output name given. Please revise.");	

				} else if (outputFile != null) {
					//Check if output file already exists.
					String wd = System.getProperty("user.dir");
					String path = wd + "/" + outputFile + ".mp4";

					String[] outputNameExists = new BashCommand().runBash("if [ ! -f " + path + " ]; then echo 0; else echo 1; fi");

					if (outputNameExists[0].equals("1")) {
						//If output file exists, ask for overwrite:
						int selectedOption = JOptionPane.showConfirmDialog(null, 
								"Output file exists. Overwrite?", 
								"Choose", 
								JOptionPane.YES_NO_OPTION); 

						if (selectedOption == JOptionPane.YES_OPTION) {
							new BashCommand().runBash("rm " + path);
							canChangeSpeed = true;
						} else {
							canChangeSpeed = false;
						}
					} else {
						canChangeSpeed = true;
					}
				}

				if (canChangeSpeed) {
					_spdWorker = new SpeedChangeWorker(this, _selectedVideo, outputFile + ".mp4", _speedOptions[_speedSelect.getSelectedIndex()]);
					_spdWorker.execute();

					_processBar.setIndeterminate(true);
					_processBar.setString("Speed Change In Progress");

					disableFunctions();
				}
			}
		} else if(ae.getSource() == _applyFade) {
			
			int selectedVideoFPS = getFPS(_selectedVideo.getAbsolutePath());
			int startFadeTime = getTimeInSeconds(_hoursSpinner1.getValue().toString(), 
					_minsSpinner1.getValue().toString(), _secsSpinner1.getValue().toString());
			int fadeDuration = getTimeInSeconds(_hoursSpinner2.getValue().toString(), 
					_minsSpinner2.getValue().toString(), _secsSpinner2.getValue().toString());
			
			//Send error if no video is selected.
			if (_videoSelectField.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "No video file to edit has been selected. " +
						"Please select a file.");	
			
				//Send error if the fade duration is zero.
			} else if(fadeDuration == 0) {
				JOptionPane.showMessageDialog(null, "Please enter a duration greater than 0.");	
				
				//Send error if the start of fade duration is greater 
				//than the length of the video.
			} else if(startFadeTime > getDurationTime(_selectedVideo.getAbsolutePath())) {
				JOptionPane.showMessageDialog(null, "Start of fade exceeds the length of the " +
						"video. Please revise");	
				
			}else {
				//Ask for output file name:
				String outputFile = JOptionPane.showInputDialog(null, "Enter output video file name:", "Output File Name",
						JOptionPane.WARNING_MESSAGE);

				boolean canFade = false;

				if (outputFile.equals("")) {
					//If no output name was given, send error.
					JOptionPane.showMessageDialog(null, "No output name given. Please revise.");	

				} else if (outputFile != null) {
					//Check if output file already exists.
					String wd = System.getProperty("user.dir");
					String path = wd + "/" + outputFile + ".mp4";

					String[] outputNameExists = new BashCommand().runBash("if [ ! -f " + path + " ]; then echo 0; else echo 1; fi");

					if (outputNameExists[0].equals("1")) {
						//If output file exists, ask for overwrite:
						int selectedOption = JOptionPane.showConfirmDialog(null, 
								"Output file exists. Overwrite?", 
								"Choose", 
								JOptionPane.YES_NO_OPTION); 

						if (selectedOption == JOptionPane.YES_OPTION) {
							new BashCommand().runBash("rm " + path);
							canFade = true;
						} else {
							canFade = false;
						}
					} else {
						canFade = true;
					}
				}

				if (canFade) {
					String fadeSelection = _fadeOptions[_fadeSelect.getSelectedIndex()];
					String startFrame = Integer.toString(selectedVideoFPS * startFadeTime); 
					String durationOfFrames = Integer.toString(selectedVideoFPS * fadeDuration);
					
					_fadeWorker = new FadeWorker(this, _selectedVideo, outputFile + ".mp4", 
							fadeSelection, startFrame, durationOfFrames);
					_fadeWorker.execute();

					_processBar.setIndeterminate(true);
					_processBar.setString("Fade In Progress");

					disableFunctions();
				}
			}
		}
	}

	/**
	 * Method for removing the temporary files created
	 * by the speed adjustment function.
	 */
	public void removeTempFiles() {
		//Remove temp files:
		new BashCommand().runBash("rm audio.wav");
		new BashCommand().runBash("rm audioq.wav");
		new BashCommand().runBash("rm video.mp4");
		new BashCommand().runBash("rm output.mp4");
	}

	/**
	 * Method for retrieving the 'frames per second' 
	 * of a given video path.
	 */
	public int getFPS(String path) {
		try {
			ProcessBuilder builder = new ProcessBuilder("avprobe", path);
			builder.redirectErrorStream(true);
			Process p = builder.start();

			InputStream stdout = p.getInputStream();

			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;

			//New list for probe output.
			ArrayList<String> probeOutput = new ArrayList<String>();

			while ((line = stdoutBuffered.readLine()) != null ) {
				if (line.contains("fps")) {
					probeOutput.add(line);
				}
			}
			//Tons of splitting to extract fps.
			String[] split = probeOutput.get(0).split(",");
			String[] split2 = split[4].split(" ");
			return Math.round(Float.parseFloat(split2[1]));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Method for retrieving the total duration time 
	 * of a given video path (in seconds).
	 */
	public int getDurationTime(String path) {
		try {
			ProcessBuilder builder = new ProcessBuilder("avprobe", path);
			builder.redirectErrorStream(true);
			Process p = builder.start();

			InputStream stdout = p.getInputStream();

			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;

			//New list for probe output.
			ArrayList<String> probeOutput = new ArrayList<String>();

			while ((line = stdoutBuffered.readLine()) != null ) {
				if (line.contains("Duration")) {
					probeOutput.add(line);
				}
			}
			//Tons of splitting to extract duration time.
			String[] split = probeOutput.get(0).split(",");
			String[] split2 = split[0].split(" ");
			String[] split3 = split2[3].split("\\.");
			String[] durationTime = split3[0].split(":");
			return getTimeInSeconds(durationTime[0], durationTime[1], durationTime[2]);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Enables Function Buttons;
	 */
	public void enableFunctions() {
		_applyRotate.setEnabled(true);
		_applyFlip.setEnabled(true);
		_applySpeedChange.setEnabled(true);
		_applyFade.setEnabled(true);
	}

	/**
	 * Disables function buttons.
	 */
	public void disableFunctions() {
		_applyRotate.setEnabled(false);
		_applyFlip.setEnabled(false);
		_applySpeedChange.setEnabled(false);
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

