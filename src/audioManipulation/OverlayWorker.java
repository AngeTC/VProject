package audioManipulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * SwingWorker for overlaying and
 * replacing audio in the background.
 * 
 * @author acas212
 */
class OverlayWorker extends SwingWorker<Void, Void> {

	private final AudioPane audioPane;
	private boolean _replaceSelected;
	private File _audio;
	private File _video;
	private Process _process;

	/**
	 * Constructor for OverlayWorker.
	 */
	public OverlayWorker(AudioPane audioPane, boolean replaceSelected, File audio, File video) {
		this.audioPane = audioPane;
		this._replaceSelected = replaceSelected;
		this._audio = audio;
		this._video = video;
	}

	/**
	 * Runs the background process for 'avconv' for replacing / overlaying audio.
	 */
	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder builder;
		
		//TODO need to get extension of output to equal extension of video input.

		//Set default process as overlaying.
		builder = new ProcessBuilder("avconv", "-y", "-i", _video.getAbsolutePath(), "-i", _audio.getAbsolutePath(), "-filter_complex", "amix=inputs=2", "-strict", "experimental", this.audioPane._chosenVideoName.getText() + ".mp4");

		//If replace is to be done, switch to other process.
		if (_replaceSelected) {
			builder = new ProcessBuilder("avconv", "-y", "-i", _video.getAbsolutePath(), "-i", _audio.getAbsolutePath(), "-c:v", "copy", "-c:a", "copy", "-map", "0:0", "-map", "1:a", this.audioPane._chosenVideoName.getText() + ".mp4");
		}

		try {
			//Create and run new process for avconv.
			builder.redirectErrorStream(true);
			System.out.println("beforeProcess");

			_process = builder.start();

			System.out.println("afterProcess");
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
				}
			}

			/*
			 *If any error occurred, display new error message by extracting the final
			 *line of the output list.
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
	 * Worker has finished / been cancelled.
	 */
	@Override
	protected void done() {
		try {
			this.audioPane._processBar.setIndeterminate(false);
			this.audioPane._processBar.setString("No Tasks Being Performed");

			//If cancelled, display 'cancelled' message.
			if (isCancelled()) {
				if (_replaceSelected) {
					JOptionPane.showMessageDialog(null, "Replace has been cancelled.");
				} else {
					JOptionPane.showMessageDialog(null, "Overlay has been cancelled.");
				}

				//If no errors occurred, display 'complete' status.	
			} else if (_process.waitFor() == 0) {
				if (_replaceSelected) {
					JOptionPane.showMessageDialog(null, "Replace Complete.");
				} else {
					JOptionPane.showMessageDialog(null, "Overlay Complete.");
				}
			}

			//Enable function buttons.
			this.audioPane.enableFunctions();

		} catch (InterruptedException e) {}
	}
}