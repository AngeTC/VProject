package helperAndResourceClasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import vamixUI.VamixGUI;
import vlcPlayer.PlayerPane;

/**
 * Button which opens a file chooser to
 * play a media file.
 * 
 * @author acas212
 */
@SuppressWarnings("serial")
public class FileChooseButton extends JButton { 

	/**
	 * Constructor of File Choose Button.
	 */
	public FileChooseButton(String name) {
		//Set text on button.
		setText(name);

		//Add new listener to button to create a new file chooser.
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				JFileChooser fileChooser = new JFileChooser();

				int returnValue = fileChooser.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();

					try {
						//Check if the file is a video/audio file.
						String type = Files.probeContentType(selectedFile.toPath());
						if(type.contains("video") || type.contains("audio")) {

							//Play file selected from chooser.
							PlayerPane.getInstance().setMediaPath(selectedFile.getAbsolutePath());
							PlayerPane.getInstance().play();

							//Set play button on VamixGUI as pressed.
							VamixGUI.getInstance().setUpPlay();

							//Send error if file is invalid.
						} else {
							JOptionPane.showMessageDialog(null, "File selected is not a video " +
									"or audio file. Please select another file.");
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
}

