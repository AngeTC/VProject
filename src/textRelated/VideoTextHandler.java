package textRelated;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class VideoTextHandler {
	//private static AvconvTask _task;

	public static String makeCommand(String videoPath, String text, String start, String end, String fontPath, String colour, String outputFile) {
		String command = "avconv -i " + videoPath + " -vf " + "drawtext=\"fontfile='" + fontPath 
				+ "':fontcolor=" + colour + ":text='" + text + "':draw='gt(t," + start + ")*lt(t," + end + ")'\" " + outputFile;
		
		System.out.println(command); //TODO
		
		return command;
	}
	
	/**
	 * Constructor for VideoTextHandler
	 * 
	 * @param cmd
	 */
/*	public VideoTextHandler(String cmd) {
		_task = new AvconvTask(cmd);
		_task.execute();
	}*/

	
}
