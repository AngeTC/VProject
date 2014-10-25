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
	
	public SaveOutputChooser() {
		setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int selection = this.showSaveDialog(null);
		
		if (selection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = this.getSelectedFile();
		    System.out.println("Save as file: " + fileToSave.getAbsolutePath());
		    _saveFilePath = fileToSave.getAbsolutePath();
		}
	}
	
	public String getSavePath() {
		return _saveFilePath;
	}
}
