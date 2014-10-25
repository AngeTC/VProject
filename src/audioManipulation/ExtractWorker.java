package audioManipulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * SwingWorker for extract functionality, used
 * for extracting the video audio in the background.
 * 
 * @author acas212
 */
class ExtractWorker extends SwingWorker<Void, Void> {

	private final AudioPane audioPane;
	private File _video;
	private Process _process;

	/**
	 * Constructor for ExtractWorker.
	 */
	public ExtractWorker(AudioPane audioPane, File video) {
		this.audioPane = audioPane;
		this._video = video;
	}

	/**
	 * Runs the background process for 'avconv'.
	 */
	@Override
	protected Void doInBackground() throws Exception {

		ProcessBuilder builder = new ProcessBuilder("avconv", "-y", "-i", _video.getAbsolutePath(), "-map", "0:a", "-strict", "experimental", this.audioPane._chosenAudioName.getText() + ".mp3");
		try {		
			//Create and run new process for avconv.
			builder.redirectErrorStream(true);

			_process = builder.start();
			InputStream stdout = _process.getInputStream();

			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;

			//New list for extract output.
			ArrayList<String> extractOutput = new ArrayList<String>();

			while ((line = stdoutBuffered.readLine()) != null ) {
				//If cancelled, end avconv command.
				if (this.isCancelled()) {
					_process.destroy();

					//Else, add line to the output list.
				} else {
					extractOutput.add(line);
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
	 * Worker has finished / been cancelled.
	 */
	@Override
	protected void done() {
		try {
			//If other worker not made: 
			if (this.audioPane._sW == null) {
				//End process bar normally.
				this.audioPane._processBar.setIndeterminate(false);
				this.audioPane._processBar.setString("No Tasks Being Performed");
				//Else:
			} else {
				//Make a check if other worker is finished.
				if (this.audioPane._sW.isDone() == true) {
					this.audioPane._processBar.setIndeterminate(false);
					this.audioPane._processBar.setString("No Tasks Being Performed");
				}
			}

			//If cancelled, display 'cancelled' message.
			if (isCancelled()) {
				JOptionPane.showMessageDialog(null, "Stripped Audio Extraction has been cancelled.");

			//If no errors occurred, display 'complete' status.	
			} else if (_process.waitFor() == 0) {
				JOptionPane.showMessageDialog(null, "Stripped Audio Extraction Complete.");
			}

			//Enable function buttons.
			this.audioPane.enableFunctions();
		} catch (InterruptedException e) {}
	}
}