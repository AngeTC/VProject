package helperAndResourceClasses;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * JFileChooser for selecting a location to save 
 * an output.
 * 
 * @author acas212
 *
 */

@SuppressWarnings("serial")
public class SaveOutputChooser extends JFileChooser {
	
	String _saveFilePath = "";
	
	/**
	 * Constructor for SaveOutputChooser.
	 */
	public SaveOutputChooser() {
		
		int selection = this.showSaveDialog(null);
		
		if (selection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = this.getSelectedFile();
		    _saveFilePath = fileToSave.getAbsolutePath();
		}
	}
	
	/**
	 * Returns the specified file path for the file 
	 * to save.
	 * @return
	 */
	public String getSavePath() {
		return _saveFilePath;
	}
}
