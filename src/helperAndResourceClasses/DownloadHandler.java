package helperAndResourceClasses;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * Pop up window for handling downloads.
 * 
 * @author acas212
 */
@SuppressWarnings("serial")
public class DownloadHandler extends JDialog implements ActionListener {

	private JPanel _topPanel = new JPanel(new BorderLayout());
	
	private JPanel _urlPanel = new JPanel(new FlowLayout());
	private JLabel _urlLabel = new JLabel("URL:");
	private JTextField _urlField = new JTextField(30);
	
	private JPanel _buttonPanel = new JPanel(new FlowLayout());
	private JCheckBox _openSourceCheck = new JCheckBox("Is the file open-source?");
	private JButton _downloadButton = new JButton("Download");
	private JButton _cancelButton = new JButton("Cancel");
	
	private JProgressBar _downloadProgress = new JProgressBar();
	
	private DownloadWorker _dW;

	/**
	 * SwingWorker for download functionality, used
	 * for running 'wget' in the background.
	 * 
	 * @author acas212
	 *
	 */
	class DownloadWorker extends SwingWorker<Void, String> {

		private String _url;
		private int _overwriteEnabled;
		private Process _process;

		
		/**
		 * Constructor for DownloadWorker.
		 * 
		 * @param url
		 * @param overwriteEnabled
		 */
		public DownloadWorker(String url, int overwriteEnabled) {
			_url = url;
			_overwriteEnabled = overwriteEnabled;
		}

		/**
		 * Runs the background process for 'wget'.
		 */
		@Override
		protected Void doInBackground() throws Exception {

			ProcessBuilder builder;

			/*
			 * If the file to be downloaded is to be continued,
			 * append the '-c' to the wget command. Else run 
			 * normal wget.
			 */
			if (_overwriteEnabled == 1) {
				builder = new ProcessBuilder("wget", "-c", _url);
			} else {
				builder = new ProcessBuilder("wget", _url);
			}

			try {		
				//Create and run new process for wget.
				builder.redirectErrorStream(true);

				_process = builder.start();
				InputStream stdout = _process.getInputStream();

				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String line = null;

				//New list for download output.
				ArrayList<String> downloadOutput = new ArrayList<String>();

				while ((line = stdoutBuffered.readLine()) != null ) {
					//If cancelled, end wget command and reset download bar to 0.
					if (this.isCancelled()) {
						_process.destroy();
						_downloadProgress.setValue(0);
					
					//Else, run publish() and add line to the output list.
					} else {
						publish(line);
						downloadOutput.add(line);
					}
				}

				/*
				 *If any error occurred, display new error message by extracting the final
				 *line of the download output list.
				 */
				if (_process.waitFor() != 0) {
					String errorMsg = downloadOutput.get(downloadOutput.size() - 1);
					JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
				}

			} catch (Exception e) {}
			return null;
		}

		/**
		 * Method used to update current status of progress bar.
		 */
		@Override
		protected void process(List<String> chunks) {

			for (String line : chunks) {
				//Extract all lines with '%' (Which contain the current progress percentage.)
				if (line.contains("%")) {
					//Extract all numbers with a scanner.
					@SuppressWarnings("resource")
					Scanner s = new Scanner(line).useDelimiter("\\D+");
					
					//Get the second number from the scanner, which should be the percentage.
					String percentage = s.next();
					percentage = s.next();

					//Finally, set the progress bar's value to the newly obtained percentage.
					_downloadProgress.setValue(Integer.parseInt(percentage));
				}
			}
		} 

		/**
		 * Method which displays the final message once the 
		 * Worker has finished / been cancelled.
		 * Also handles the logging of a successful
		 * download.
		 */
		protected void done() {
			try {
				//If cancelled, display 'cancelled' message.
				if (isCancelled()) {
					JOptionPane.showMessageDialog(null, "Download has been cancelled.");

				//If no errors occurred, display 'complete' status.
				} else if (_process.waitFor() == 0) {
					JOptionPane.showMessageDialog(null, "Download Complete.");
				}

				//Reset progress bar status.
				_downloadProgress.setValue(0);
			} catch (InterruptedException e) {}
		}
	}
	
	public DownloadHandler(JFrame frame) {
		super(frame, "Download", true);
		
		setLayout(new BorderLayout());
		setResizable(false);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setSize(500,100);
		
		add(_topPanel, BorderLayout.NORTH);
		
		_downloadProgress.setStringPainted(true);
		add(_downloadProgress, BorderLayout.CENTER);
		
		_topPanel.add(_urlPanel, BorderLayout.NORTH);
		_urlPanel.add(_urlLabel);
		_urlPanel.add(_urlField);	
		
		_topPanel.add(_buttonPanel, BorderLayout.CENTER);
		_buttonPanel.add(_openSourceCheck);
		_buttonPanel.add(_downloadButton);
		_buttonPanel.add(_cancelButton);
		
		_downloadButton.addActionListener(this);
		_cancelButton.addActionListener(this);
		
		setVisible(true);
	}

	/**
	 * Method which performs error checking and handles DownloadWorker,
	 * in response to when a button is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		//If cancel button was pressed, cancel DownloadWorker.
		if (ae.getSource().equals(_cancelButton)) {
			_dW.cancel(true);
		} else {

			//Check if file is open source.
			if (!_openSourceCheck.isSelected()) {
				JOptionPane.showMessageDialog(null, "File is not open-source. Cannot permit download.");
			
			//Check if a URL is given.
			} else if (_urlField.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "No URL was given. Please retype.");
			} else {

				BashCommand bC = new BashCommand();
				//Get filename using Bash.
				String getBaseName = "echo $(basename " + _urlField.getText() + ")";
				String[] baseNameOutput = bC.runBash(getBaseName);

				//Check if file already exists using bash.
				String checkFileExists = "if [ ! -f " + baseNameOutput[0] + " ]; then echo 0; else echo 1; fi";
				String[] fileExists = bC.runBash(checkFileExists);

				int enableDownload = 1;
				int enableOverwrite = 0;

				//Display confirm message if file doesn't exist yet.
				if (fileExists[0].equals("0")) {
					enableDownload = JOptionPane.showConfirmDialog(null, baseNameOutput[0] + " can be dowloaded. Start download?", "Download Can Proceed", JOptionPane.YES_NO_OPTION);

				//Else ask if the file should be overwritten or continued.
				} else {
					Object[] options = { "Overwrite", "Continue" };
					enableOverwrite = JOptionPane.showOptionDialog(this, baseNameOutput[0] + " already exists. Overwrite file or continue download?", "File Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						bC.runBash("rm " + baseNameOutput[0]);
					}
					enableDownload = 0;
				}
				
				//If download enabled, start new DownloadWorker to download new file.
				if (enableDownload == 0) {
					try {
						_dW = new DownloadWorker(_urlField.getText(), enableOverwrite);
						_dW.execute();
					} catch (Exception e) {}
				}
			}
		}
	}
}
