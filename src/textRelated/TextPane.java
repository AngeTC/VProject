package textRelated;

import helperAndResourceClasses.BashCommand;
import helperAndResourceClasses.SaveOutputChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import say.swing.JFontChooser;
import vlcPlayer.PlayerPane;

/**
 * Pane for text adding functionality.
 * 
 * @author Original author: kxie094 (and further developed by acas212)
 *
 */
@SuppressWarnings("serial")
public class TextPane extends JPanel {
	// Main Sections:
	private final JPanel _textAndChangesPanel = new JPanel();
	private final JPanel _addTextPanel = new JPanel();
	private final JPanel _changesPanel = new JPanel();
	private final JPanel _buttonPanel = new JPanel();
	private final JPanel _textOptionPanel = new JPanel();
	private final JPanel _addAndDeletePanel = new JPanel();
	private final JPanel _confirmationPanel = new JPanel();
	private final JPanel _spinnerPanel1 = new JPanel();
	private final JPanel _spinnerPanel2 = new JPanel();
	private final JPanel _fontAndColourPanel = new JPanel();
	private final JPanel _tableButtonsPanel = new JPanel();
	private final JPanel _exportPanel = new JPanel();
	private final JPanel _progressAndButtonPanel = new JPanel(new BorderLayout());

	private final JButton _loadButton = new JButton("Load data");
	private final JButton _saveButton = new JButton("Save data");
	private final JButton _fontButton = new JButton("Font");
	private final JButton _colourButton = new JButton("Colour");
	private final JButton _chooseVideoButton = new JButton("Choose");
	private final JButton _addButton = new JButton("Add Caption");
	private final JButton _exportingButton = new JButton("Add Selected Text");
	private final JButton _deleteButton = new JButton("Delete Caption");
	private final JButton _cancelButton = new JButton("Cancel");
	
	private final JTextField _textInput = new JTextField("Type a caption to add...");
	private final JScrollPane _textScroll = new JScrollPane(_textInput);

	private final JLabel _durationLabel1 = new JLabel("Start (hh:mm:ss)");
	private final JLabel _timeSep1 = new JLabel(":");
	private final JLabel _timeSep2 = new JLabel(":");
	private final JLabel _durationLabel2 = new JLabel("End (hh:mm:ss)  ");
	private final JLabel _timeSep3 = new JLabel(":");
	private final JLabel _timeSep4 = new JLabel(":");

	private final JSpinner _hoursSpinner1 = new JSpinner(new SpinnerNumberModel(00, 00, 99, 1));
	private final JSpinner _minsSpinner1 = new JSpinner(new SpinnerNumberModel(00, 00, 59, 1));
	private final JSpinner _secsSpinner1 = new JSpinner(new SpinnerNumberModel(00, 00, 59, 1));
	private final JSpinner _hoursSpinner2 = new JSpinner(new SpinnerNumberModel(00, 00, 99, 1));
	private final JSpinner _minsSpinner2 = new JSpinner(new SpinnerNumberModel(00, 00, 59, 1));
	private final JSpinner _secsSpinner2 = new JSpinner(new SpinnerNumberModel(05, 00, 59, 1));

	final JProgressBar _progressBar = new JProgressBar();
	
	// JTable and associated buttons
	String[] fields = {"Text", "Start", "End", "Font", "Colour"};

	Vector<String> columns = new Vector<String>();

	DefaultTableModel _tableModel = new DefaultTableModel(fields, 0) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private final JTable _captionsTable = new JTable(_tableModel);
	private final JScrollPane _tableScrollPane = new JScrollPane(_captionsTable);


	//Default font, size and color.
	private String _fontPath = "/usr/share/fonts/truetype/freefont/FreeSans.ttf";
	private String _fontSize = "12";
	private String _colourHexValue = "0x000000";

	boolean canDraw;

	private AvconvTask _avconvTask;
	
	private SaveOutputChooser saveVideoChooser;

	/**
	 * Constructor for TextPane.
	 */
	public TextPane() {
		
		//Set layout.
		setLayout(new BorderLayout());
		
		//Add and construct panel for save and load buttons.
		JPanel videoAndButtons = new JPanel();
		videoAndButtons.setLayout(new GridLayout(2,0));
		_buttonPanel.setLayout(new GridLayout(1,0));
		_buttonPanel.add(_loadButton);
		_buttonPanel.add(_saveButton);
		videoAndButtons.add(_buttonPanel);
		add(videoAndButtons, BorderLayout.NORTH);

		//Add and construct text field and font buttons panel.
		_textAndChangesPanel.setLayout(new GridLayout(2,0));
		_addTextPanel.setLayout(new BorderLayout());
		_textOptionPanel.setLayout(new BorderLayout());
		_textOptionPanel.add(_textScroll, BorderLayout.CENTER);
		_fontAndColourPanel.setLayout(new GridLayout(0,1));
//		//new JPanel(); TODO
//		
//		//Get all 
//		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		Font[] fonts = e.getAllFonts();
//		Vector<String> allFonts = new Vector<String>();
//		for (Font f : fonts) {
//			allFonts.add(f.getName());
//		}
		
		_fontAndColourPanel.add(_fontButton);
		_fontAndColourPanel.add(_colourButton);
		_textOptionPanel.add(_fontAndColourPanel, BorderLayout.EAST);
		
		//Add and construct panel with start and end time spinners.
		_spinnerPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
		_spinnerPanel1.add(_durationLabel1);
		_spinnerPanel1.add(_hoursSpinner1);
		_spinnerPanel1.add(_timeSep1);
		_spinnerPanel1.add(_minsSpinner1);
		_spinnerPanel1.add(_timeSep2);
		_spinnerPanel1.add(_secsSpinner1);

		_spinnerPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
		_spinnerPanel2.add(_durationLabel2);
		_spinnerPanel2.add(_hoursSpinner2);
		_spinnerPanel2.add(_timeSep3);
		_spinnerPanel2.add(_minsSpinner2);
		_spinnerPanel2.add(_timeSep4);
		_spinnerPanel2.add(_secsSpinner2);

		JPanel spinnersPanel = new JPanel();
		spinnersPanel.setLayout(new GridLayout(2,0));
		spinnersPanel.add(_spinnerPanel1);
		spinnersPanel.add(_spinnerPanel2);

		_textOptionPanel.add(spinnersPanel, BorderLayout.SOUTH);

		TitledBorder addTextBorder = BorderFactory.createTitledBorder("Add Text:");
		_addTextPanel.setBorder(addTextBorder);

		_addTextPanel.add(_textOptionPanel, BorderLayout.CENTER);

		//Add and construct panel with add and delete caption buttons
		_addAndDeletePanel.setLayout(new GridLayout(0,1));
		_confirmationPanel.setLayout(new GridLayout(1,0));
		_confirmationPanel.add(_addButton);
		_confirmationPanel.add(_deleteButton);
		_addAndDeletePanel.add(_confirmationPanel);
		_addTextPanel.add(_addAndDeletePanel, BorderLayout.SOUTH);
		_textAndChangesPanel.add(_addTextPanel);

		//Add and construct panel with table of captions.
		TitledBorder changeBorder = new TitledBorder("Added Captions:");
		_changesPanel.setBorder(changeBorder);
		_changesPanel.setLayout(new BorderLayout());
		_changesPanel.add(_tableScrollPane, BorderLayout.CENTER);
		_captionsTable.getTableHeader().setReorderingAllowed(false);
		_tableButtonsPanel.setLayout(new GridLayout(1,0));
		_exportPanel.setLayout(new GridLayout(1,1));
		_exportPanel.add(_exportingButton);
		_exportPanel.add(_cancelButton);
		_tableButtonsPanel.add(_exportPanel);

		//Set up progressBar and add it to panel.
		_progressBar.setString("No Task Being Performed");
		_progressBar.setStringPainted(true);
		_progressBar.setPreferredSize(new Dimension(370, 30));

		_progressAndButtonPanel.add(_progressBar, BorderLayout.NORTH);
		_progressAndButtonPanel.add(_tableButtonsPanel, BorderLayout.SOUTH);

		_changesPanel.add(_progressAndButtonPanel, BorderLayout.SOUTH);

		// Add Changes panel.
		_textAndChangesPanel.add(_changesPanel);

		//Set default widths in the table.
		add(_textAndChangesPanel, BorderLayout.CENTER);
		_captionsTable.getColumnModel().getColumn(0).setPreferredWidth(130);
		_captionsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
		_captionsTable.getColumnModel().getColumn(3).setPreferredWidth(60);

		//Set listeners for all buttons.
		setListeners();
	}

	/**
	 * Method for setting listeners for TextPane's Buttons.
	 */
	private void setListeners() {
		
		/*
		 * Adds caption details as a new line in caption table.
		 */
		_addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//Add caption to the table.
				addCaptionToTable();
			}
		});

		/*
		 * Removes selected line from caption table.
		 */
		_deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_captionsTable.getSelectedRow() != -1) {
					//Remove selected row of table.
					_tableModel.removeRow(_captionsTable.getSelectedRow());
				}
			}	
		});

		/*
		 * Opens new Font Chooser, to select new font and size.
		 */
		_fontButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFontChooser fc = new JFontChooser();
				int returnValue = fc.showDialog(null);
				if (returnValue == JFontChooser.OK_OPTION) {
					Font selected = fc.getSelectedFont();

					String fontSize =Integer.toString(selected.getSize());
					_fontSize = fontSize;

					String path = FontHandler.getPathForFont(selected.getName(), 0);

					// Set path if font found, else use default.
					if (path != "") {
						_fontPath = path;
					}
				}
			}
		});

		/*
		 * Opens up new Color chooser, to select new font color.
		 */
		_colourButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Color newColour = JColorChooser.showDialog(
						_fontAndColourPanel,
						"Choose text color",
						Color.BLACK);
				// Convert color to hex to be used in avconv.
				if (newColour != null) {
					String hex = Integer.toHexString(newColour.getRGB());
					_colourHexValue = "0x" + hex.substring(2);
				}
			}
		});

		/*
		 * Creates new video output, applying the selected caption
		 * to new output.
		 */
		_exportingButton.addActionListener(new ActionListener() {

			String[] outputVideoExists;
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (PlayerPane.getInstance().getMediaPath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: No video to edit. Please select a file by " +
							"playing one using the Open button.");
					return;
				}
				
				if (_tableModel.getRowCount() < 1) {
					JOptionPane.showMessageDialog(null, "No captions added yet.");
					return;
				} 
				
				if (_captionsTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select caption from text log to apply change.");
					return;
				} 

				//Open new save chooser for video output name.
				saveVideoChooser = new SaveOutputChooser();
				saveVideoChooser.setDialogTitle("Save Added Text Video");

				//Use bash to check if output video already exists.
				outputVideoExists = new BashCommand().runBash("if [ ! -f " + 
						saveVideoChooser.getSavePath() + ".mp4" + " ]; then echo 0; else echo 1; fi");

				//Send error if no file name given.
				if (saveVideoChooser.getSavePath().equals("")) {
					JOptionPane.showMessageDialog(null, "Error: Please supply name for output video.");
					return;
				}

				//Ask for overwrite if output video file already exists.
				if (outputVideoExists[0].equals("1") && !saveVideoChooser.getSavePath().equals("")) {
					Object[] options = { "Overwrite", "Cancel" };
					int enableOverwrite = JOptionPane.showOptionDialog(null, saveVideoChooser.getSavePath() + ".mp4" 
							+ " already exists. Overwrite file? Please choose a new filename if not overriding.", "File " +
									"Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

					if (enableOverwrite == 0) {
						canDraw = true;
					}
					
				} else {
					//Else flip is fine.
					canDraw = true;
				}
				
				if (canDraw && _captionsTable.getSelectedRow() != -1) {
					int row = _captionsTable.getSelectedRow();

					//Start new 
					_avconvTask = new AvconvTask(TextPane.this, PlayerPane.getInstance().getMediaPath(),
							_tableModel.getValueAt(row, 0).toString(), 
							getTimeInSeconds(_tableModel.getValueAt(row, 1).toString()), 
							getTimeInSeconds(_tableModel.getValueAt(row, 2).toString()),
							_fontPath, _colourHexValue, _fontSize, 
							saveVideoChooser.getSavePath() + ".mp4");
					
					_avconvTask.execute();

					_progressBar.setIndeterminate(true);
					_progressBar.setString("Text Adding In Progress");
				}
			}			
		});

		_cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_avconvTask != null) {
					_avconvTask.cancel(true);
				}
			}
		});

		_loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String type;
					try {
						type = Files.probeContentType(fc.getSelectedFile().toPath());
						if (type.contains("text") && fc.getSelectedFile().getName().contains(".txtsavefile")) {
							TextSaveHandler.loadTable(_captionsTable, fc.getSelectedFile().getName());
						} else {
							JOptionPane.showMessageDialog(null, "Invalid save file chosen. Please revise.");
							return;
						}
					} catch (IOException e1) {}
				}
			}
		});

		_saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String outputFile = JOptionPane.showInputDialog(null, "Enter output save file name:", "Dialog for Input",
						JOptionPane.WARNING_MESSAGE);
				if (outputFile != null) {
					String workingDir = System.getProperty("user.dir");
					String path = workingDir + "/" + outputFile + ".txtsavefile";

					String[] outputSaveExists = new BashCommand().runBash("if [ ! -f " + path + " ]; then echo 0; else echo 1; fi");

					if (outputSaveExists[0].equals("1")) {
						int selectedOption = JOptionPane.showConfirmDialog(null, 
								"Save File exists. Overwrite?", 
								"Choose", 
								JOptionPane.YES_NO_OPTION); 
						if (selectedOption == JOptionPane.YES_OPTION) {
							new BashCommand().runBash("rm " + path);
							TextSaveHandler.saveTable(_captionsTable, outputFile);
						} 
					} else {
						TextSaveHandler.saveTable(_captionsTable, outputFile);
					}
				}
			}
		});
	}

	/**
	 * Returns string representation of duration time from one 
	 * of the two spinner groups.
	 */
	private String getTimeAsString(int spinnerNumber) {
		String hours = "";
		String mins = "";
		String secs = ""; 
		if (spinnerNumber == 1) {
			hours = _hoursSpinner1.getValue().toString();
			mins = _minsSpinner1.getValue().toString();
			secs = _secsSpinner1.getValue().toString();
		} else if (spinnerNumber == 2) {
			hours = _hoursSpinner2.getValue().toString();
			mins = _minsSpinner2.getValue().toString();
			secs = _secsSpinner2.getValue().toString();
		}
		String time = hours + ":" + mins + ":" + secs;

		return time;
	}

	/**
	 * Returns string representation of duration time in seconds.
	 */
	private String getTimeInSeconds(int spinnerNumber) {
		int hours = 0;
		int mins = 0;
		int secs = 0;
		if (spinnerNumber == 1) {
			hours = Integer.parseInt( _hoursSpinner1.getValue().toString()) * 360;
			mins = Integer.parseInt( _minsSpinner1.getValue().toString()) * 60;
			secs = Integer.parseInt( _secsSpinner1.getValue().toString());
		} else if (spinnerNumber == 2) {
			hours = Integer.parseInt( _hoursSpinner2.getValue().toString()) * 360;
			mins = Integer.parseInt( _minsSpinner2.getValue().toString()) * 60;
			secs = Integer.parseInt( _secsSpinner2.getValue().toString());
		}

		String time = Integer.toString(hours + mins + secs);
		return time;
	}

	/**
	 * Returns string representation of duration time in seconds.
	 */
	private String getTimeInSeconds(String time) {
		String[] times = time.split(":");

		int hours = Integer.parseInt(times[0]);
		int mins = Integer.parseInt(times[1]);
		int secs = Integer.parseInt(times[2]);

		hours = hours * 360;
		mins = mins * 60;

		String outTime = Integer.toString(hours + mins + secs);
		return outTime;
	}

	/**
	 * Method for adding caption change to table.
	 */
	private void addCaptionToTable() {
		if (_textInput.getText().length() <= 0) {
			JOptionPane.showMessageDialog(null, "Please enter some text to add as a caption.");
			return;
		} 

		if (getTimeAsString(1).equals("0:0:0") && getTimeAsString(2).equals("0:0:0")) {
			JOptionPane.showMessageDialog(null, "Please enter a duration greater than 0.");
			return;
		} 

		if (Integer.parseInt(getTimeInSeconds(2)) <= Integer.parseInt(getTimeInSeconds(1))) {
			JOptionPane.showMessageDialog(null, "End time must be greater than start time. " +
					"Please revise.");
			return;
		} 

		if (PlayerPane.getInstance().getPlayTime() < Integer.parseInt(getTimeInSeconds(1))) {
			JOptionPane.showMessageDialog(null, "Start time is greater than length of video. " +
					"Please revise.");
			return;
		}
		
		if (PlayerPane.getInstance().getPlayTime() < Integer.parseInt(getTimeInSeconds(1))) {
			JOptionPane.showMessageDialog(null, "End time is greater than length of video. " +
					"Please revise.");
			return;
		}

		String[] captionData = {_textInput.getText(), getTimeAsString(1), getTimeAsString(2), _fontPath, _colourHexValue};
		_tableModel.addRow(captionData);
	}
}
