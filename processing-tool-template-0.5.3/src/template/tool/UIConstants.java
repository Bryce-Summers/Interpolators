package template.tool;

import processing.core.PFont;

public class UIConstants {
	
	
	static PFont buttonFont;
	static gui_mainControls App;
	
	final static int NO_BUTTON_SELECTED = -1;
	final static int BUTTON_STATE_UNSELECTED = 0; 
	final static int BUTTON_STATE_HOVERING = 1;
	final static int BUTTON_STATE_PRESSING = 2; 
	final static int BUTTON_STATE_SELECTED = 3;

	final static int BUTTONGROUP_SINGLE_CHOICE = 1;
	final static int BUTTONGROUP_MULTIPLE_CHOICE = 2;
	
	static UIButtonGroup buttonGroupA;
	static UIButtonGroup buttonGroupB;
	static UIButton generateButton;
	

	static String[] namesA = {
	  "All", "Sigmoid", "Ogee", "Ease-In", "Ease-Out", "Penner's", "Gaussian", "Bezier", "Staircase", "Window", "Other"
	};
	static String[] imagesA = {
	  null, "Sigmoid.png", "Ogee.png", "Ease-In.png", "Ease-Out.png", null, "Gaussian.png", null, "Staircase.png", "Window.png", null
	};

	static String[] namesB = {
	  "Flip X?", "Flip Y?", "Constrain Output?", "Comment Only?", "Use New Tab?"
	};
	static String[] imagesB = {
	  "check_24.png", "check_24.png", "check_24.png", "check_24.png", "check_24.png"
	};
	
	// -- Initialize all of the constants.
	public static void init(gui_mainControls App_in)
	{
		App = App_in;
		
		buttonFont = App.loadFont("Roboto-Medium-16.vlw");
		buttonGroupA = new UIButtonGroup (BUTTONGROUP_SINGLE_CHOICE, namesA, imagesA, 7, 7, 130, 42, 7);
		
		int output_controls_x = 970;
		buttonGroupB = new UIButtonGroup (BUTTONGROUP_MULTIPLE_CHOICE, namesB, imagesB, output_controls_x, 7, 192, 42, 7);

		generateButton = new UIButton("GENERATE!", output_controls_x, 350, 192, 72, false, false);
		generateButton.bCenteredText = true;
		generateButton.setIsMomentary(true);
		
		
		buttonGroupB.buttonArray[4].buttonState = BUTTON_STATE_SELECTED;
		
		
	}
	
	
	//----------------
	static void draw() {
		
	
		
	  // App.background(255);
	  //drawButtonGroupHeadings();

	  buttonGroupA.update();
	  buttonGroupA.drawButtonGroup();

	  buttonGroupB.update();
	  buttonGroupB.drawButtonGroup();

	  boolean bDoGenerate = generateButton.handleMouse();
	  generateButton.drawButton();
	  if (bDoGenerate){
	    App.export();
	  }
	  	  
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
