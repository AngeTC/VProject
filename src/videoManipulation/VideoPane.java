package videoManipulation;

import helperAndResourceClasses.BashCommand;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;


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

	private JProgressBar _processBar = new JProgressBar();

	private File _selectedVideo;

	private OrientationWorker _oriWorker;
	private SpeedChangeWorker _spdWorker;
	private FadeWorker _fadeWorker;

	public VideoPane() {

		//Orientation (Rotate and flip) Layout:
		_orientationAndVideoSelectPanel.add(_orientationPanel, BorderLayout.CENTER);
		_orientationAndVideoSelectPanel.add(_videoSelectPanel, BorderLayout.NORTH);

		_videoSelectPanel.setBorder(BorderFactory.createTitledBorder("Select Video To Edit:"));
		_videoSelectPanel.setPreferredSize(new Dimension(370, 70));
		_videoSelectPanel.add(_videoSelectField);
		_videoSelectField.setEditable(false);
		_videoSelectPanel.add(_videoSelectButton);

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
						_oriWorker = new OrientationWorker(_selectedVideo, outputFile + ".mp4", rotateOption, false);
						_oriWorker.execute();

						_processBar.setIndeterminate(true);
						_processBar.setString("Rotating In Progress");

						disableFunctions();
					} else {
						String flipOption = _flipOptions[(_flipSelect.getSelectedIndex())];
						_oriWorker = new OrientationWorker(_selectedVideo, outputFile + ".mp4", flipOption, true);
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
					_spdWorker = new SpeedChangeWorker(_selectedVideo, outputFile + ".mp4", _speedOptions[_speedSelect.getSelectedIndex()]);
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
					
					_fadeWorker = new FadeWorker(_selectedVideo, outputFile + ".mp4", 
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
	 * SwingWorker for changing orientation of video.
	 */
	class OrientationWorker extends SwingWorker<Void, Void> {

		private boolean _isFlip = false;
		private String _selection;
		private String _outputName;
		private File _video;
		private Process _process;

		/**
		 * Constructor for OrienationWorker.
		 */
		public OrientationWorker(File video, String output, String select, boolean isFlip) {
			_selection = select;
			_outputName = output;
			_isFlip = isFlip;
			_video = video;
		}

		/**
		 * Runs the background process for 'avconv'.
		 */
		@Override
		protected Void doInBackground() throws Exception {

			ProcessBuilder builder = null;

			if (_isFlip) {
				if (_selection.equals("Flip Vertically")) {
					builder = new ProcessBuilder("avconv", "-y", "-i", _video.getPath(), "-vf", "vflip", "-strict", "experimental", _outputName);

				} else if (_selection.equals("Flip Horizontally")) {
					builder = new ProcessBuilder("avconv", "-y", "-i", _video.getPath(), "-vf", "hflip", "-strict", "experimental", _outputName);

				}

			} else {
				if (_selection.equals("90")) {
					builder = new ProcessBuilder("avconv", "-y", "-i", _video.getPath(), "-vf", "transpose=1", "-strict", "experimental", _outputName);

				} else if (_selection.equals("180")) {
					builder = new ProcessBuilder("avconv", "-y", "-i", _video.getPath(), "-vf", "transpose=1,transpose=1", "-strict", "experimental", _outputName);

				} else if (_selection.equals("270")) {
					builder = new ProcessBuilder("avconv", "-y", "-i", _video.getPath(), "-vf", "transpose=2", "-strict", "experimental", _outputName);

				}
			}

			try {		
				//Create and run new process for avconv command.
				builder.redirectErrorStream(true);

				_process = builder.start();
				InputStream stdout = _process.getInputStream();

				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String line = null;

				//New list for avconv output.
				ArrayList<String> extractOutput = new ArrayList<String>();

				while ((line = stdoutBuffered.readLine()) != null ) {
					//If cancelled, end avconv command.
					if (this.isCancelled()) {
						_process.destroy();

						//Else, add line to the output list.
					} else {
						extractOutput.add(line);
						System.out.println(line);
					}
				}

				/*
				 *If any error occurred, display new error message by extracting the final
				 *line of the extract output list.
				 */
				if (_process.waitFor() != 0) {
					String errorMsg = extractOutput.get(extractOutput.size() - 1);
					JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
				}

			} catch (Exception e) {}
			return null;
		}

		/**
		 * Method which displays the final message once the
		 * worker has finished / been cancelled.
		 */
		@Override
		protected void done() {
			try {
				//If cancelled, display 'cancelled' message.
				if (isCancelled()) {
					JOptionPane.showMessageDialog(null, "Orientaion Change Cancelled.");

					//If no errors occurred, display 'complete' status.	
				} else if (_process.waitFor() == 0) {
					JOptionPane.showMessageDialog(null, "Orientaion Change Complete.");
				}

				//Enable function buttons.
				enableFunctions();

				//Reset Process bar:
				_processBar.setIndeterminate(false);
				_processBar.setString("No Tasks Being Performed");

			} catch (InterruptedException e) {}
		}
	}

	/**
	 * SwingWorker for changing speed of video.
	 */
	class SpeedChangeWorker extends SwingWorker<Void, Void> {

		File _video;
		String _outputName;
		double _speedValue;
		Process changingProcess;

		public SpeedChangeWorker(File video, String outputFile, String speedValue) {
			_video = video;
			_outputName = outputFile;

			if (speedValue.equals("2x")) {
				_speedValue = 2;
			} else if (speedValue.equals("1.5x")) {
				_speedValue = 1.5;
			} else if (speedValue.equals("0.75x")) {
				_speedValue = 0.75;
			} else if (speedValue.equals("0.5x")) {
				_speedValue = 0.5;
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			try {			
				//New list for avconv output.
				ArrayList<String> speedOutput = new ArrayList<String>();

				//Process 1 (Extract video only):  
				ProcessBuilder builder = new ProcessBuilder("avconv", "-y", "-i", _video.getPath(), "-map", "0:0", "video.mp4");
				builder.redirectErrorStream(true);
				changingProcess = builder.start();
				InputStream stdout = changingProcess.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String line = null;

				while ((line = stdoutBuffered.readLine()) != null ) {
					//If cancelled, end current avconv command and exit doInBackground().
					if (this.isCancelled()) {
						changingProcess.destroy();
						return null;
					} else {
						//Else, add line to the output list.
						speedOutput.add(line);
						System.out.println(line);
					}
				}

				if (changingProcess.waitFor() != 0) {
					String errorMsg = speedOutput.get(speedOutput.size() - 1);
					JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
					return null;
				}

				//Process 2 (Extract audio only):
				builder = new ProcessBuilder("avconv", "-y", "-i", _video.getPath(), "-map", "0:1", "audio.wav");
				changingProcess = builder.start();
				stdout = changingProcess.getInputStream();
				stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

				while ((line = stdoutBuffered.readLine()) != null ) {
					//If cancelled, end current avconv command and exit doInBackground().
					if (this.isCancelled()) {
						changingProcess.destroy();
						return null;
					} else {
						//Else, add line to the output list.
						speedOutput.add(line);
						System.out.println(line);
					}
				}

				if (changingProcess.waitFor() != 0) {
					String errorMsg = speedOutput.get(speedOutput.size() - 1);
					JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
					return null;
				}

				//Process 3 (Change Audio Speed with sox function):
				builder = new ProcessBuilder("sox", "audio.wav", "audioq.wav", "tempo", Double.toString(_speedValue));
				changingProcess = builder.start();
				stdout = changingProcess.getInputStream();
				stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				line = null;

				while ((line = stdoutBuffered.readLine()) != null ) {
					//If cancelled, end current avconv command and exit doInBackground().
					if (this.isCancelled()) {
						changingProcess.destroy();
						return null;
					} else {
						//Else, add line to the output list.
						speedOutput.add(line);
						System.out.println(line);
					}
				}
				if (changingProcess.waitFor() != 0) {
					String errorMsg = speedOutput.get(speedOutput.size() - 1);
					JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
					return null;
				}

				//Process 4 (Change video speed with avconv):
				builder = new ProcessBuilder("avconv", "-y", "-i", "video.mp4", "-filter:v",
						"setpts=" + Double.toString(1/_speedValue) + "*PTS", "output.mp4");
				changingProcess = builder.start();
				stdout = changingProcess.getInputStream();
				stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				line = null;

				while ((line = stdoutBuffered.readLine()) != null ) {
					//If cancelled, end current avconv command and exit doInBackground().
					if (this.isCancelled()) {
						changingProcess.destroy();
						return null;
					} else {
						//Else, add line to the output list.
						speedOutput.add(line);
						System.out.println(line);
					}
				}
				if (changingProcess.waitFor() != 0) {
					String errorMsg = speedOutput.get(speedOutput.size() - 1);
					JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
					return null;
				}

				//Process 5 (Combine modified video and audio together):
				builder = new ProcessBuilder("avconv", "-y", "-i", "output.mp4", "-i", "audioq.wav", 
						"-strict", "experimental", _outputName);
				changingProcess = builder.start();
				stdout = changingProcess.getInputStream();
				stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				line = null;

				while ((line = stdoutBuffered.readLine()) != null ) {
					//If cancelled, end current avconv command and exit doInBackground().
					if (this.isCancelled()) {
						changingProcess.destroy();
						return null;
					} else {
						//Else, add line to the output list.
						speedOutput.add(line);
						System.out.println(line);
					}
				}

				if (changingProcess.waitFor() != 0) {
					String errorMsg = speedOutput.get(speedOutput.size() - 1);
					JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
					return null;
				}

			} catch (IOException | HeadlessException | InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void done() {
			try {
				//If cancelled, display 'cancelled' message.
				if (isCancelled()) {
					JOptionPane.showMessageDialog(null, "Speed change has been cancelled.");

				} else if (changingProcess.waitFor() == 0) {
					//If no errors occurred, display 'complete' status.	
					JOptionPane.showMessageDialog(null, "Speed Change Complete.");
				}

				//Remove Temporary files.
				removeTempFiles();

				//Enable function buttons.
				enableFunctions();

				//Reset Process bar:
				_processBar.setIndeterminate(false);
				_processBar.setString("No Tasks Being Performed");

			} catch (InterruptedException e) {}
		}
	}

	/**
	 * SwingWorker for adding fade to video.
	 */
	class FadeWorker extends SwingWorker<Void, Void> {

		private String _fadeSelection;
		private String _startFrame;
		private String _durationOfFrames;
		private String _outputName;
		private File _video;

		private Process _process;

		/**
		 * Constructor for FadeWorker.
		 */
		public FadeWorker(File video, String output, String fadeSelect, String startFrame, String durationOfFrames) {
			System.out.println(fadeSelect);
			if (fadeSelect.equals("Fade In")) {
				_fadeSelection = "in";
			} else  {
				_fadeSelection = "out";
			} 
			_startFrame = startFrame;
			_durationOfFrames = durationOfFrames;
			_outputName = output;
			_video = video;
		}

		/**
		 * Runs the background process for 'avconv'.
		 */
		@Override
		protected Void doInBackground() throws Exception {

			ProcessBuilder builder = new ProcessBuilder("avconv", "-y", "-i", _video.getPath(), 
					"-filter:v", "fade=" + _fadeSelection + ":" + _startFrame + 
					":" + _durationOfFrames, "-strict", "experimental", _outputName);

			try {		
				//Create and run new process for avconv command.
				builder.redirectErrorStream(true);

				_process = builder.start();
				InputStream stdout = _process.getInputStream();

				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String line = null;

				//New list for avconv output.
				ArrayList<String> avconvOutput = new ArrayList<String>();

				while ((line = stdoutBuffered.readLine()) != null ) {
					//If cancelled, end avconv command.
					if (this.isCancelled()) {
						_process.destroy();
						
						//Else, add line to the output list.
					} else {
						avconvOutput.add(line);
						System.out.println(line);
					}
				}

				/*
				 *If any error occurred, display new error message by extracting the final
				 *line of the extract output list.
				 */
				if (_process.waitFor() != 0) {
					String errorMsg = avconvOutput.get(avconvOutput.size() - 1);
					JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
				}

			} catch (Exception e) {}
			return null;
		}

		/**
		 * Method which displays the final message once the
		 * worker has finished / been cancelled.
		 */
		@Override
		protected void done() {
			try {
				//If cancelled, display 'cancelled' message.
				if (isCancelled()) {
					JOptionPane.showMessageDialog(null, "Fade Cancelled.");

					//If no errors occurred, display 'complete' status.	
				} else if (_process.waitFor() == 0) {
					JOptionPane.showMessageDialog(null, "Fade Complete.");
				}

				//Enable function buttons.
				enableFunctions();

				//Reset Process bar:
				_processBar.setIndeterminate(false);
				_processBar.setString("No Tasks Being Performed");

			} catch (InterruptedException e) {}
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

