package textRelated;

import helperAndResourceClasses.BashCommand;

/**
 * Class for fonts...
 * 
 * @author kxie094
 *
 */
public class FontHandler {
	public static String getPathForFont(String fontName, int styleCode) {
		BashCommand cmd = new BashCommand();
		String command = "find /usr/share/fonts ";
		
		command = command.concat("-name " + "\"*" + fontName + "*\"" + " -print");
		String[] output = cmd.runBash(command);
		
		// Return if font is not found
		if (output == null || output.length < 1) {
			return "";
		}
		
		String path = "";
		// Select correct path for styled font
		if (styleCode == 1) {
			for (String s : output) {
				if (s.contains("Bold") && !(s.contains("Italic") || s.contains("Oblique"))) {
					path = s;
				} 
			}
		} else if (styleCode == 2) {
			for (String s : output) {
				if (s.contains("Italic") || s.contains("Oblique") && !(s.contains("Bold"))) {
					path = s;
				} 
			}
		} else if (styleCode == 3) {
			for (String s : output) {
				if (s.contains("BoldItalic") || s.contains("BoldOblique")) {
					path = s;
				} 
			}
		} else {
			path = output[0];
		}
		
		return path;
	}
}
