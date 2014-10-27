package vamixUI;
import functionality.audio.AudioPane;
import functionality.subtitle.SubtitlePane;
import functionality.text.TextPane;
import functionality.video.VideoPane;
import helperClasses.DownloadHandler;
import helperClasses.PlayFileChooseButton;
import helperClasses.ResImage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import vlcPlayer.PlayerPane;

/**
 * Main GUI of Vamix program.
 * (A singleton class.)
 * 
 * @author acas212
 * 
 * Credits to: http://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
 * (Converting milliseconds to 'time format' -> (hh:mm:ss))
 */
@SuppressWarnings("serial")
public class VamixGUI extends JFrame implements ActionListener, ChangeListener {

	private static VamixGUI _guiInstance = null;

	private JTabbedPane _tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private AudioPane _audioTab = new AudioPane();
	private TextPane _textTab = new TextPane();
	private VideoPane _videoTab = new VideoPane();
	private SubtitlePane _subTab = new SubtitlePane();

	private JPanel _leftPanel = new JPanel();

	private JPanel _topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	private JButton _fileButton = new PlayFileChooseButton("Open File");
	private JButton _downloadButton = new JButton("Download");

	private JPanel _botButtonPanel = new JPanel();

	private JPanel _mediaSliderPanel = new JPanel(new BorderLayout(10, 10));
	private SeekBar _seekBar = new SeekBar(0);
	private JLabel _timeLabel = new JLabel();

	private JPanel _mediaControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));

	private JPanel _mediaButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	private JToggleButton _playButton = new JToggleButton();
	private JButton _rewindButton = new JButton();
	private JButton _fastFwdButton = new JButton();
	private JButton _stopButton = new JButton();

	private JPanel _volumePanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
	private JSlider  _volumeControl = new JSlider(0, 150);
	private JToggleButton _muteButton = new JToggleButton();

	private static PlayerPane _playerPanel = PlayerPane.getInstance(); 

	private boolean _isMediaLoaded = false;

	/**
	 * Private constructor for VamixGUI.
	 */
	private VamixGUI() {
		super("VAMIX");
		setSize(1050, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Disable resizing.
		setResizable(false);

		//Set the Layout for the main GUI.
		setLayout(new FlowLayout(FlowLayout.LEFT,10,10));

		//Set size and Layout of leftPanel.
		_leftPanel.setPreferredSize(new Dimension(600, 550));
		_leftPanel.setLayout(new BorderLayout(10,10));

		//Set size and add tabs to tabbedPane.
		_tabbedPane.setPreferredSize(new Dimension(400, 550));
		_tabbedPane.add(_audioTab, "Audio");
		_tabbedPane.add(_videoTab, "Video");
		_tabbedPane.add(_textTab, "Text");
		_tabbedPane.add(_subTab, "Subtitles");

		//Add leftPanel and TabbedPane to the Main GUI.
		add(_leftPanel, BorderLayout.CENTER);
		add(_tabbedPane, BorderLayout.EAST);

		//Set sizes of the top button panel and its buttons.
		_topButtonPanel.setPreferredSize(new Dimension(600,50));
		_fileButton.setPreferredSize(new Dimension(170,50));
		_downloadButton.setPreferredSize(new Dimension(170,50));

		//Set icons onto top buttons.
		_fileButton.setIcon(new ImageIcon(new ResImage("folder.png").getResImage()));
		//_saveButton.setIcon(new ImageIcon(new ResImage("floppy.png").getResImage().getScaledInstance(40, 40, 0)));
		_downloadButton.setIcon(new ImageIcon(new ResImage("download.png").getResImage()));

		//Add top buttons to the top panel.
		_topButtonPanel.add(_fileButton);
		//_topButtonPanel.add(_saveButton);
		_topButtonPanel.add(_downloadButton);

		//Set size of the VLC player panel.
		_playerPanel.setPreferredSize(new Dimension(600,400));

		//Set event handler to reset play button when end of video / audio is reached.
		_playerPanel.addMediaEventHandler(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				_playButton.setSelected(false); //Now reset to play button.
				_isMediaLoaded = false;
				_playerPanel.stop();
			}
		});
		
		//Ensure player is unmuted.
		_playerPanel.unMute();

		//Set size and layout of the bottom button panel.
		_botButtonPanel.setLayout(new BorderLayout(10,10));
		_botButtonPanel.setPreferredSize(new Dimension(600,70));
		_botButtonPanel.add(_mediaSliderPanel,BorderLayout.NORTH);
		_botButtonPanel.add(_mediaControlPanel,BorderLayout.CENTER);

		//Set size and add the seek bar to the panel.
		_mediaSliderPanel.setPreferredSize(new Dimension(600,20));
		_mediaSliderPanel.add(_seekBar, BorderLayout.CENTER);

		//Add time to the slider panel.
		_mediaSliderPanel.add(_timeLabel, BorderLayout.WEST);

		//Set size and layout of the media control panel.
		//(Contains button panel and volume control.)
		_mediaControlPanel.setLayout(new GridLayout(0,2));
		_mediaControlPanel.setPreferredSize(new Dimension(600,50));

		//Set layout of panel containing all media control buttons:
		_mediaButtonPanel.setLayout(new GridLayout(0,4));

		//Place appropriate icons on the media control buttons.
		_playButton.setIcon(new ImageIcon(new ResImage("play.png").getResImage()));
		_playButton.setSelectedIcon(new ImageIcon(new ResImage("pause.png").getResImage()));

		_rewindButton.setIcon(new ImageIcon(new ResImage("rewind.png").getResImage()));
		_stopButton.setIcon(new ImageIcon(new ResImage("stop.png").getResImage()));
		_fastFwdButton.setIcon(new ImageIcon(new ResImage("fastForward.png").getResImage()));

		//Add buttons to button panel...
		_mediaButtonPanel.add(_playButton);
		_mediaButtonPanel.add(_rewindButton);
		_mediaButtonPanel.add(_stopButton);
		_mediaButtonPanel.add(_fastFwdButton);

		//...then add button panel to final control panel.
		_mediaControlPanel.add(_mediaButtonPanel);

		//Set layout of volume panel.
		_volumePanel.setLayout(new BorderLayout(0,0));

		//Set appropriate icons to the mute button.
		_muteButton.setIcon(new ImageIcon(new ResImage("volume.png").getResImage()));
		_muteButton.setSelectedIcon(new ImageIcon(new ResImage("noVolume.png").getResImage()));

		//Add mute and volume slider to volume panel...
		_volumePanel.add(_volumeControl, BorderLayout.CENTER);
		_volumePanel.add(_muteButton, BorderLayout.WEST);

		//...then add volume panel to final control panel.
		_mediaControlPanel.add(_volumePanel);

		//Add top, bottom and the VLC Player panels to the left panel.
		_leftPanel.add(_topButtonPanel, BorderLayout.NORTH);
		_leftPanel.add(_playerPanel, BorderLayout.CENTER);
		_leftPanel.add(_botButtonPanel, BorderLayout.SOUTH);

		//Add Main frame as listener to all appropriate components.
		_downloadButton.addActionListener(this);
		_playButton.addActionListener(this);
		_stopButton.addActionListener(this);
		_fastFwdButton.addActionListener(this);
		_rewindButton.addActionListener(this);
		_muteButton.addActionListener(this);
		_volumeControl.addChangeListener(this);

		//Set timer component to continuously update the time label and seek bar:
		Timer currentTimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Get current time of media from media player (in milliseconds).
				Long mediaTime = _playerPanel.getTime();

				//Convert time to string.
				String currentTime = String.format("%02d:%02d:%02d", 
						TimeUnit.MILLISECONDS.toHours(mediaTime),
						TimeUnit.MILLISECONDS.toMinutes(mediaTime) - 
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mediaTime)),
						TimeUnit.MILLISECONDS.toSeconds(mediaTime) - 
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaTime))
						);

				//Update time and seek bar.
				_timeLabel.setText(currentTime);
				_seekBar.setValue(mediaTime.intValue());
			}
		});
		//Start 'time updater' timer.
		currentTimer.start();

		//Add listener to fast forward button.
		_fastFwdButton.addMouseListener(new MouseAdapter() {
			//Timer to continually skip forward.
			private Timer timePressed = new Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					_playerPanel.skip(5000);
				}
			});

			//Start timer to continually skip when mouse is pressed.
			@Override
			public void mousePressed(MouseEvent me) {
				timePressed.start();
			}

			//Stop timer when mouse is released.
			@Override
			public void mouseReleased(MouseEvent me) {
				timePressed.stop();
			}
		});

		//Add listener to rewind button.
		_rewindButton.addMouseListener(new MouseAdapter() {
			//Timer to continually skip backward.
			private Timer timePressed = new Timer(100, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					_playerPanel.skip(-5000);
				}
			});

			//Start timer to continually skip when mouse is pressed.
			@Override
			public void mousePressed(MouseEvent me) {
				timePressed.start();
			}

			//Stop timer when mouse is released.
			@Override
			public void mouseReleased(MouseEvent me) {
				timePressed.stop();
			}
		});

		//Finally set frame as visible.
		this.setVisible(true);
	}

	/**
	 * Method to get single instance of VamixGUI.
	 */
	public static VamixGUI getInstance() {
		if (_guiInstance == null) {
			_guiInstance = new VamixGUI();
		}
		return _guiInstance;
	}

	/**
	 * Method explaining actions performed depending on
	 * which button is pressed.
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {

		//If play button pressed:
		if (ae.getSource() == _playButton) {
			//If no media has started being played...
			if (!_isMediaLoaded) {		
				//...play media specified by mediaPath.
				_playerPanel.play();
				setUpPlay();

			} else { 
				//Else toggle pause on the media player.
				_playerPanel.pause();
			}

			//If stop button pressed:
		} else if (ae.getSource() == _stopButton) {
			//Stop media, while reassigning the appropriate booleans
			//and resetting the play button.
			_playerPanel.stop();
			_playButton.setSelected(false);
			_isMediaLoaded = false;

			//If mute button pressed:	
		} else if (ae.getSource() == _muteButton) {
			//Toggle mute on media player.
			_playerPanel.mute();

			//If download button pressed:
		} else if (ae.getSource() == _downloadButton) {
			//Bring up new dialog window which handles downloads.
			new DownloadHandler(VamixGUI.this);
		}
	}

	/**
	 * Method for when volume slider changes.
	 */
	@Override
	public void stateChanged(ChangeEvent ce) {
		//If volume slider is changed:
		if (ce.getSource() == _volumeControl) {
			//Set new volume for media player.
			_playerPanel.setVolume(_volumeControl.getValue());
		}
	}

	/**
	 * Method for setting the play button as selected
	 * and changing the appropriate boolean and setting
	 * new length for the seek bar.
	 */
	public void setUpPlay() {
		//Set play button as selected.
		_playButton.setSelected(true);
		_isMediaLoaded = true;

		//Must wait a while before obtaining length of media file.
		try {
			//Wait for short period.
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Get total playtime of media file.
		int totalPlayTime = _playerPanel.getLength();

		//Set new length of seek bar with new playtime.
		_seekBar.setNewTotalLength(totalPlayTime);
		_playerPanel.setPlayTime(totalPlayTime);
	}
	
	/**
	 * Returns the current time from the time label.
	 * 
	 * Used by SubtitlePane to set new start and end 
	 * times.
	 * 
	 * @return
	 */
	public String getCurrentTime() {
		return _timeLabel.getText();
	}

	/**
	 * Main method to start running the GUI.
	 * @param args
	 */
	public static void main(String[] args){
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				VamixGUI.getInstance();
			}
		});
	}
}