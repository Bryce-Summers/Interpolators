package template.tool;

import processing.core.PFont;
import processing.core.PImage;
import java.text.*;

public class UIConstants {
	
	static gui_mainControls App;
	
	static PFont buttonFont; 
	static PImage titlebar; 
	static PImage sfcilogo;


	final static int NO_BUTTON_SELECTED      = -1;
	final static int BUTTON_STATE_UNSELECTED =  0; 
	final static int BUTTON_STATE_HOVERING   =  1;
	final static int BUTTON_STATE_PRESSING   =  2; 
	final static int BUTTON_STATE_SELECTED   =  3;

	final static int BUTTONGROUP_SINGLE_CHOICE   = 1; 
	final static int BUTTONGROUP_MULTIPLE_CHOICE = 2; 
	
	static UIButtonGroup buttonGroupA;
	static UIButtonGroup buttonGroupB;
	static UIButton generateButton;
	

	static String[] namesA = {
	  "All", "Sigmoid", "Ogee", "Ease-In", "Ease-Out", "Penner's", "Staircase", "Window", "General", "Linear", "Other"
	};
	static String[] imagesA = {
	  null, "Sigmoid.png", "Ogee.png", "Ease-In.png", "Ease-Out.png", null, "Staircase.png", "Window.png", null, null, null
	};
	
	static String[] tooltipStringsA = {
	  "All functions", 
	  "Sigmoidal (cyma recta) functions", 
	  "Ogee (cyma reversa) functions", 
	  "Ease-in functions", 
	  "Ease-out functions", 
	  "Robert Penner's functions", 
	  "Gaussian functions", 
	  "Bezier functions", 
	  "Staircase functions", 
	  "Windowing functions", 
	  "Miscellaneous functions"
	};

	static String[] namesB = {
	  "Flip X?", "Flip Y?", "Constrain Output?", "Comment Only?", "Use New Tab?"
	};
	static String[] imagesB = {
	  "check_24.png", "check_24.png", "check_24.png", "check_24.png", "check_24.png"
	};
	static String[] tooltipStringsB = {
	  "When selected, flips the input (horizontally).", 
	  "When selected, flips the output (vertically).", 
	  "When selected, clamps the output to the range [0...1].", 
	  "When selected, only inserts the function call.", 
	  "When selected, places generated code in a new tab.\nIf unselected, code is inserted at the cursor."
	};
	//   
	
	// -- Initialize all of the constants.
	public static void init(gui_mainControls App_in)
	{
		App = App_in;
		
		titlebar = App.loadImage("titlebar.png"); 
		sfcilogo = App.loadImage("sfci-logo.png"); 

		buttonFont = App.loadFont("Roboto-Medium-16.vlw");
		buttonGroupA = new UIButtonGroup (BUTTONGROUP_SINGLE_CHOICE, namesA, imagesA, tooltipStringsA, 7, 48, 130, 42, 8);
		buttonGroupB = new UIButtonGroup (BUTTONGROUP_MULTIPLE_CHOICE, namesB, imagesB, tooltipStringsB, 987, 79, 191, 42, 8);

		generateButton = new UIButton("GENERATE CODE!", 987, 343, 192, 181, false, false);
		generateButton.bCenteredText = true; 
		generateButton.setIsMomentary(true);
		generateButton.makeTooltip ("When clicked, inserts code for\nthis function into your project.");
		
		buttonGroupB.buttonArray[4].buttonState = BUTTON_STATE_SELECTED;
	}
	
	static int timer = 60;
	
	//----------------
	static void draw() {
		
		//App.background(255);
		App.image (titlebar, 0, 0); 
		App.image (sfcilogo, App.width - sfcilogo.width, App.height - sfcilogo.height); 

		buttonGroupA.update();
		buttonGroupA.drawButtonGroup();

		buttonGroupB.update();
		buttonGroupB.drawButtonGroup();

		boolean bDoGenerate = generateButton.handleMouse();
		generateButton.drawButton();
		if (bDoGenerate && timer <= 0){
			App.println("Timer = " + timer);
		   App.export();
		   javax.swing.JOptionPane.showMessageDialog(null, "Code has been Generated!");
		   timer = 60;
		}
		else if(timer > 0)
		{
			timer--;
		}
		
		

		buttonGroupA.drawTooltips(); 
		buttonGroupB.drawTooltips();
		generateButton.drawTooltip();
	  	  
		// -- Handle Output Settings.

		UIButton[] buttons = buttonGroupB.buttonArray;

		App.bool_flipX   = buttons[0].isSelected();
		App.bool_flipY   = buttons[1].isSelected();
		App.bool_clamp   = buttons[2].isSelected();
		App.bool_comment = buttons[3].isSelected();
		App.bool_newTab  = buttons[4].isSelected();
		
	}

	//----------------
	static void mousePressed() {
		buttonGroupA.handleMousePressed();
		buttonGroupB.handleMousePressed();
		generateButton.handleMousePressed();
	  
		// -- Handle Group index setting.
		UIButton[] buttons = buttonGroupA.buttonArray;
		int len = buttons.length;
		for(int i = 0; i < len; i++)
		{
			if(buttonGroupA.buttonArray[i].bClickHappenedInMe)
			{
				App.function_groups[i].mouseP();
				App.println("Selected " + i);
			}
		}

	}	

	//----------------
	static void drawButtonGroupHeadings() {
	  App.fill(100); 
	  App.textAlign (App.LEFT);
	  App.text ("Family: "  + buttonGroupA.getSelectedButtonString(), 7, 20);
	  App.text ("Options: " + buttonGroupB.getSelectedButtonString(), 200, 20);
	}


}
