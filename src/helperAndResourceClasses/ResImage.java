package helperAndResourceClasses;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Class which encapsulates an image from the 
 * resource folder to be returned as an Image 
 * Class.
 * 
 * @author acas212
 *
 */

/*
 * 
Below is the license for use of the images:

The MIT License (MIT)

Copyright (c) <year> <copyright holders>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/

public class ResImage {
	
	private Image _img;

	/**
	 * Constructor for ResImage.
	 * @param imageName
	 */
	public ResImage(String imageName) {
		try {
			URL imageURL = getClass().getResource("/" + imageName);
			_img = ImageIO.read(imageURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return image obtained.
	 * @return
	 */
	public Image getResImage() {
		return _img;
	}
	
}
