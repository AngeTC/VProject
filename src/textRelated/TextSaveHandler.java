package textRelated;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Save and load functionality.
 * 
 * @author acas212
 *
 * Credits To:
 * 
 * http://stackoverflow.com/questions/10413977/javaremoving-all-the-rows-of-defaulttablemodel
 * (Deleting all rows of JTable)
 * 
 * http://stackoverflow.com/questions/19440778/loading-text-file-into-jtable
 * (Loading text file to JTable)
 * 
 * http://stackoverflow.com/questions/15769642/how-do-i-save-and-load-a-jtable-as-txt-file
 * (Saving JTable contents to text file)
 */
public class TextSaveHandler {
	
	/**
	 * Method saves contents of JTable to a text file;
	 * 
	 * @param table
	 * @param saveName
	 */
	public static void saveTable(JTable table, String saveName) {
		try {
			BufferedWriter bfw = new BufferedWriter(new FileWriter(saveName + ".txtsavefile"));
			for (int i = 0 ; i < table.getColumnCount() ; i++) {
				bfw.write(table.getColumnName(i));
				bfw.write("\t");
			}

			for (int i = 0 ; i < table.getRowCount(); i++) {
				bfw.newLine();
				for(int j = 0 ; j < table.getColumnCount();j++) {
					bfw.write((String)(table.getValueAt(i,j)));
					bfw.write("\t");;
				}
			}
			
			bfw.close();
			
		} catch (Exception e){}
	}

	/**
	 * Method loads contents of a save text file into the
	 * JTable;
	 * 
	 * @param table
	 * @param saveName
	 */
	public static void loadTable(JTable table, String saveName) {
		try {
			DefaultTableModel tm = (DefaultTableModel) table.getModel();

			//Remove rows from DefaultTableModel.
			if (tm.getRowCount() > 0) {
				for (int i = tm.getRowCount() - 1; i > -1; i--) {
					tm.removeRow(i);
				}
			}

			BufferedReader bfw = new BufferedReader(new FileReader(saveName));
			String line;
			while((line = bfw.readLine()) != null ) {
				//Get all lines except first and add them to the model.
				if (!line.contains("Text	Start	End	Font	Colour	")) {
					tm.addRow( line.split("\t") );
				}
			}

			//Close reader.
			bfw.close();
		} catch (Exception e){}
	}
}