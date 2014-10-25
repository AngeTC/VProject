package textRelated;

import helperAndResourceClasses.BashCommand;

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
import videoManipulation.VideoPane;

/**
 * Pane for text adding functionality.
 * 
 * @author Original author: kxie094 (and further developed by acas212)
 *
 */
@SuppressWarnings("serial")
public class TextPane extends JPanel {
	// Main sections
	private JPanel _textAndChangesPanel = new JPanel();
	private JPanel _addTextPanel = new JPanel();
	private JPanel _changesPanel = new JPanel();
	private JPanel _buttonPanel = new JPanel();
	
	// Sub sections
	private final JPanel _textOptionPanel = new JPanel();
	private final JPanel _imageAndConfirmPanel = new JPanel();
	private final JPanel _confirmationPanel = new JPanel();
	private final JPanel _spinnerPanel1 = new JPanel();
	private final JPanel _spinnerPanel2 = new JPanel();
	private final JPanel _fontAndColourPanel = new JPanel();
	private final JPanel _tableButtonsPanel = new JPanel();
	private final JPanel _editAndDeletePanel = new JPanel();
	private final JPanel _inputVideoPanel = new JPanel();
	private final JPanel _progressAndButtonPanel = new JPanel(new BorderLayout());
	
	// Button panel
	private final JButton _loadButton = new JButton("Load data");
	private final JButton _saveButton = new JButton("Save data");
	
	// Add text panel components
	
	// Text section
	private final JTextArea _textInput = new JTextArea("Type a caption to add...");
	private final JScrollPane _textScroll = new JScrollPane(_textInput);
	private final JButton _fontButton = new JButton("Font");
	private final JButton _colourButton = new JButton("Colour");
	private final JLabel _durationLabel1 = new JLabel("Start (hh:mm:ss)");
	private final JSpinner _hoursSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
	private final JSpinner _minsSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JSpinner _secsSpinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JLabel _timeSep1 = new JLabel(":");
	private final JLabel _timeSep2 = new JLabel(":");
	private final JLabel _durationLabel2 = new JLabel("End (hh:mm:ss)");
	private final JSpinner _hoursSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
	private final JSpinner _minsSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JSpinner _secsSpinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
	private final JLabel _timeSep3 = new JLabel(":");
	private final JLabel _timeSep4 = new JLabel(":");
	
	private final JLabel _videoLabel = new JLabel("Selected Video:");
	private final JTextField _inputVideo = new JTextField("Pick a video to add text to.", 16);
	private final JButton _chooseVideoButton = new JButton("Choose");
	
	private final JButton _addButton = new JButton("Add Caption");
	
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
	private final JButton _exportingButton = new JButton("Add Selected Text");
	private final JButton _deleteButton = new JButton("Delete Caption");
	private final JButton _cancelButton = new JButton("Cancel");
	
	//Default font, size and color.
	private String _fontPath = "/usr/share/fonts/truetype/freefont/FreeSans.ttf";
	private String _fontSize = "12";
	private String _colourHexValue = "0x000000";
	
	boolean canDraw;
	
	AvconvTask _avconvTask;
	
	/**
	 * Constructor for TextPane.
	 */
	public TextPane() {
		setLayout(new BorderLayout());
		// button and video, NORTH
		JPanel videoAndButtons = new JPanel();
		videoAndButtons.setLayout(new GridLayout(2,0));
		_buttonPanel.setLayout(new GridLayout(1,0));
		_buttonPanel.add(_loadButton);
		_buttonPanel.add(_saveButton);
		_inputVideoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		_inputVideoPanel.add(_videoLabel);
		_inputVideoPanel.add(_inputVideo);
		_inputVideoPanel.add(_chooseVideoButton);
		_inputVideo.setEditable(false);
		videoAndButtons.add(_buttonPanel);
		videoAndButtons.add(_inputVideoPanel);
		add(videoAndButtons, BorderLayout.NORTH);
		
		// add text panel and changes panel, CENTRE
		_textAndChangesPanel.setLayout(new GridLayout(2,0));
		//*****ADD TEXT PANEL******
		_addTextPanel.setLayout(new BorderLayout());
		// Text options
		_textOptionPanel.setLayout(new BorderLayout());
		_textOptionPanel.add(_textScroll, BorderLayout.CENTER);
		_fontAndColourPanel.setLayout(new GridLayout(0,1));
		new JPanel();
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = e.getAllFonts();
		Vector<String> allFonts = new Vector<String>();
		for (Font f : fonts) {
			allFonts.add(f.getName());
		}
		_fontAndColourPanel.add(_fontButton);
		_fontAndColourPanel.add(_colourButton);
		_textOptionPanel.add(_fontAndColourPanel, BorderLayout.EAST);
		// spinner panels
		
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
		
		TitledBorder addTextBorder = BorderFactory.createTitledBorder("Add text");
		_addTextPanel.setBorder(addTextBorder);
		
		_addTextPanel.add(_textOptionPanel, BorderLayout.CENTER);
		
		// image and confirm buttons
		_imageAndConfirmPanel.setLayout(new GridLayout(0,1));
		_confirmationPanel.setLayout(new GridLayout(1,0));
		_confirmationPanel.add(_addButton);
		_confirmationPanel.add(_deleteButton);
		
		_imageAndConfirmPanel.add(_confirmationPanel);
		
		_addTextPanel.add(_imageAndConfirmPanel, BorderLayout.SOUTH);
		_textAndChangesPanel.add(_addTextPanel);
		
		//****CHANGES PANEL******
		TitledBorder changeBorder = new TitledBorder("Added Captions:");
		_changesPanel.setBorder(changeBorder);
		_changesPanel.setLayout(new BorderLayout());
		_changesPanel.add(_tableScrollPane, BorderLayout.CENTER);
		_captionsTable.getTableHeader().setReorderingAllowed(false);
		// Add Table buttons
		_tableButtonsPanel.setLayout(new GridLayout(1,0));

		_editAndDeletePanel.setLayout(new GridLayout(1,1));
		_editAndDeletePanel.add(_exportingButton);
		_editAndDeletePanel.add(_cancelButton);
		_tableButtonsPanel.add(_editAndDeletePanel);
		
		_progressBar.setString("No Task Being Performed");
		_progressBar.setStringPainted(true);
		_progressBar.setPreferredSize(new Dimension(370, 30));
		
		_progressAndButtonPanel.add(_progressBar, BorderLayout.NORTH);
		_progressAndButtonPanel.add(_tableButtonsPanel, BorderLayout.SOUTH);
	
		_changesPanel.add(_progressAndButtonPanel, BorderLayout.SOUTH);
		
		// Add Changes panel
		_textAndChangesPanel.add(_changesPanel);
		
		add(_textAndChangesPanel, BorderLayout.CENTER);
		_captionsTable.getColumnModel().getColumn(0).setPreferredWidth(130);
		_captionsTable.getColumnModel().getColumn(4).setPreferredWidth(60);
		_captionsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
	
		
		//-------END OF LAYING OUT OF COMPONENTS-----------
		
		
		//---------START LISTENERS/FUNCTIONALITY-----------
		
		_addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addCaptionToTable();
			}
		});
		
		_deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (_captionsTable.getSelectedRow() != -1) {
					_tableModel.removeRow(_captionsTable.getSelectedRow());
				}
			}	
		});
		
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
					System.out.println(newColour.getRed());
					System.out.println(newColour.getGreen());
					System.out.println(newColour.getBlue());
					_colourHexValue = "0x" + hex.substring(2);
					System.out.println(_colourHexValue);
				}
				
			}
			
		});
		
		
		_exportingButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (_tableModel.getRowCount() < 1) {
					JOptionPane.showMessageDialog(null, "No captions added yet.");
					return;
				} else if (_inputVideo.getText().equals("Pick a video to add text to.")) {
					JOptionPane.showMessageDialog(null, "No video has been selected. Please choose a video file.");
					return;
				} else if (_captionsTable.getSelectedRow() == -1) {
					JOptionPane.showMessageDialog(null, "Please select caption from text log to apply change.");
					return;
				} 
				
				//Ask for output name.
				String outputFile = JOptionPane.showInputDialog(null, "Enter output video file name:", "Output File Name",
				        JOptionPane.WARNING_MESSAGE);
				if (outputFile != null) {
					String wd = System.getProperty("user.dir");
					String path = wd + "/" + outputFile + ".mp4";
					
					String[] outputNameExists = new BashCommand().runBash("if [ ! -f " + path + " ]; then echo 0; else echo 1; fi");
					
					if (outputNameExists[0].equals("1")) {
						int selectedOption = JOptionPane.showConfirmDialog(null, 
                                "File exists. Overwrite?", 
                                "Choose", 
                                JOptionPane.YES_NO_OPTION); 
						if (selectedOption == JOptionPane.YES_OPTION) {
							new BashCommand().runBash("rm " + path);

							canDraw = true;
						} else {
							canDraw = false;
						}
					} else {
						canDraw = true;
					}
				}
				
				if (canDraw && _captionsTable.getSelectedRow() != -1) {
					int row = _captionsTable.getSelectedRow();
						
						_avconvTask = new AvconvTask(TextPane.this, _inputVideo.getText(), _tableModel.getValueAt(row, 0).toString(), getTimeInSeconds(_tableModel.getValueAt(row, 1).toString()), getTimeInSeconds(_tableModel.getValueAt(row, 2).toString()),
								_fontPath, _colourHexValue, _fontSize, outputFile + ".mp4");
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
		
		_chooseVideoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
				
					String type;
					try {
						// Check if chosen file is a video
						type = Files.probeContentType(fc.getSelectedFile().toPath());
						if (type.contains("video")) {
							_inputVideo.setText(fc.getSelectedFile().toString());
						} else {
							JOptionPane.showMessageDialog(null, "Invalid video file chosen.");
							return;
						}
					} catch (IOException e1) {}
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
	public String getTimeInSeconds(String time) {
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
		} else if (getTimeAsString(1).equals("0:0:0") && getTimeAsString(2).equals("0:0:0")) { // TODO check start and end times
			JOptionPane.showMessageDialog(null, "Please enter a duration greater than 0.");
			return;
		} else if (Integer.parseInt(getTimeInSeconds(2)) <= Integer.parseInt(getTimeInSeconds(1))) {
			JOptionPane.showMessageDialog(null, "End time must be greater than start time. " +
					"Please revise.");
			return;
		} else if (new VideoPane().getDurationTime(_inputVideo.getText()) < Integer.parseInt(getTimeInSeconds(1))) {
			JOptionPane.showMessageDialog(null, "Start time is greater than length of video. " +
					"Please revise.");
			return;
		}
		
		String[] captionData = {_textInput.getText(), getTimeAsString(1), getTimeAsString(2), _fontPath, _colourHexValue};
		_tableModel.addRow(captionData);
	}
}
