package vamixUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JProgressBar;

import vlcPlayer.PlayerPane;

/**
 * Seek bar used to skip through media.
 * 
 * @author acas212
 *
 *Credits to: http://stackoverflow.com/questions/18146914/get-value-on-clicking-jprogressbar
 *(Detecting where on progress bar it is clicked.)
 */
@SuppressWarnings("serial")
public class SeekBar extends JProgressBar {

	/**
	 * Constructor for the SeekBar.
	 * 
	 * @param mediaLength
	 */
	public SeekBar(int mediaLength) {

		super(0, mediaLength);

		//Add a mouse listener to find and calculate where to transition to on the video/audio.
		addMouseListener(new MouseAdapter() {            
			public void mouseClicked(MouseEvent e) {

				//Retrieves the mouse position relative to the bar's origin.
				int mouseX = e.getX();

				//Computes how far along the mouse is relative to the component width then multiply it by the progress bar's maximum value.
				int seekTime = (int)Math.round(((double)mouseX / (double)getWidth()) * getMaximum());
				int currentTime = PlayerPane.getInstance().getTime().intValue();

				//Set bar value to the specified seek time.
				setValue(seekTime);

				//Skip to the specified seek time.
				PlayerPane.getInstance().skip(seekTime - currentTime);
			}                                     
		});
	};

	/**
	 * Set new maximum value for the seek bar.
	 * 
	 * @param newMax
	 */
	public void setNewTotalLength(int newMax) {
		setMaximum(newMax);
	}
}
