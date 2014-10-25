package helperAndResourceClasses;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * Class used to run bash commands from the java 
 * VAMIX program.
 * 
 * @author acas212
 *
 */
public class BashCommand {
	
	/**
	 * Runs a new process, given a Bash command in the
	 * form of a string, returning the output of the 
	 * command in the form of a string array. 
	 * @param command
	 */
	public String[] runBash(String command) {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
		
		ArrayList<String> comOutput = new ArrayList<String>();
		
		try {
			
			/*
			 * Create new process by starting the builder.
			 */
			builder.redirectErrorStream(true);

			Process process = builder.start();
			InputStream stdout = process.getInputStream();

			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line = null;
			
			/*
			 *If error occurred, display new error message by extracting the final
			 *line of the output.
			 */
			if (process.waitFor() != 0) {
				String errorMsg = "";
				while ((line = stdoutBuffered.readLine()) != null ) {
					errorMsg = line;
				}
				JOptionPane.showMessageDialog(null, "Error: " + errorMsg);
			
			//Else, just add the line of the output to the list.
			} else {
				while ((line = stdoutBuffered.readLine()) != null ) {
					comOutput.add(line);
				}
			}
		} catch (Exception e1) {}
		
		//Return final list as an array.
		return comOutput.toArray(new String[0]);
	}
}
