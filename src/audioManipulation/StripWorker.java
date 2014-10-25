package audioManipulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * SwingWorker for stripping functionality, used
 * for removing the audio from the video in the background.
 * 
 * @author acas212
 */
class StripWorker extends SwingWorker<Void, Void> {

	private final AudioPane audioPane;
	private boolean _audioRemoved;
	private boolean _audioOut;
	private File _video;
	private Process _process;

	/**
	 * Constructor for StripWorker.
	 */
	public StripWorker(AudioPane audioPane, boolean audioKept, boolean audioOut, File video) {
		this.audioPane = audioPane;
		this._audioRemoved = audioKept;
		this._audioOut = audioOut;
		this._video = video;
	}

	/**
	 * Runs the background process for 'avconv' for stripping audio.
	 */
	@Override
	protected Void doInBackground() throws Exception {

		ProcessBuilder builder = null;

		//If audio should be removed:
		if (_audioRemoved) {
			//If audio should be made into new output:
			if (_audioOut) {
				//Extract and remove audio from video:
				this.audioPane._eW = new ExtractWorker(this.audioPane, _video);
				this.audioPane._eW.execute();
				builder = new ProcessBuilder("avconv", "-y", "-i", _video.getAbsolutePath(), "-an", this.audioPane._chosenVideoName.getText() + ".mp4");

			} else {
				//Remove audio from video only:
				builder = new ProcessBuilder("avconv", "-y", "-i", _video.getAbsolutePath(), "-an", this.audioPane._chosenVideoName.getText() + ".mp4");
			}
		
		//Else audio should not be removed:
		} else { 
			if (_audioOut) {
				//Just extract audio.
				this.audioPane._eW = new ExtractWorker(this.audioPane, _video);
				this.audioPane._eW.execute();
			}
		}

		try {		
			//Create and run new process for avconv.
			builder.redirectErrorStream(true);

			_process = builder.start();
			InputStream stdout = _process.getInputStream();

			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;

			//New list for extract output.
			ArrayList<String> stripOutput = new ArrayList<String>();

			while ((line = stdoutBuffered.readLine()) != null ) {
				//If cancelled, end avconv command.
				if (this.isCancelled()) {
					this.audioPane._eW.cancel(true);
					_process.destroy();

					//Else, add line to the output list.
				} else {
					stripOutput.add(line);
				}
			}

			/*
			 *If any error occurred, display new error message by extracting the final
			 *line of the extract output list.
			 */
			if (_process.waitFor() != 0) {
				String errorMsg = stripOutput.get(stripOutput.size() - 1);
				JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
			}

		} catch (Exception e) {}
		return null;
	}

	/**
	 * Method which displays the final message once the 
	 * Worker has finished / been cancelled.  Also handles 
	 * the logging of a successful extract.
	 */
	@Override
	protected void done() {
		try {
			if (_process != null) {

				//If other worker not made:
				if (this.audioPane._eW == null) {
					//End process bar normally.
					this.audioPane._processBar.setIndeterminate(false);
					this.audioPane._processBar.setString("No Tasks Being Performed");
					//Else:
				} else {
					//Make a check if other worker is finished.
					if (this.audioPane._eW.isDone() == true) {
						this.audioPane._processBar.setIndeterminate(false);
						this.audioPane._processBar.setString("No Tasks Being Performed");
					}
				}

				//If cancelled, display 'cancelled' message.
				if (isCancelled()) {
					JOptionPane.showMessageDialog(null, "Strip has been cancelled.");

					//If no errors occurred, display 'complete' status.	
				} else if (_process.waitFor() == 0) {
					JOptionPane.showMessageDialog(null, "Strip Complete.");
				}
			}

			//Enable function buttons.
			this.audioPane.enableFunctions();
		} catch (InterruptedException e) {}
	}
}