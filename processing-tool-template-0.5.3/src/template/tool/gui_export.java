package template.tool;

import processing.app.Editor;
import processing.core.PApplet;

/*
 * This class should handle the exportation of a function and parameters from this tool.
 */

public class gui_export  extends PApplet
{

	gui_mainControls mainDisplay;
	
	// The editor that will received the constructed text.
	public static Editor editor;
	
	// Should code be inserted into a new tab.
	boolean new_tab = true;
	
	// Should only a comment containing the parameters be exported.
	boolean parameter_comment_only = false;
	
	private class checkBoxButton
	{
		// Checked if true.
		// Non checked if false.
	}
	
	// Use the Go button to send the function to the main processing window.
	private class Go_Button
	{
		
	}
	
	
}
