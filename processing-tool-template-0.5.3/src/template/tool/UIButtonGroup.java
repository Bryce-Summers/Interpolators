package template.tool;

import processing.core.PApplet;
import processing.core.PFont;

public class UIButtonGroup {

	gui_mainControls App;
	
	PFont buttonFont;
	
	int nButtons;
	UIButton buttonArray[];
	int whichButtonSelected[]; 
	int buttonGroupStyle;
	
	//----------------
	UIButtonGroup (int style, String[] names, String[] imageNames, int gx, int gy, int bw, int bh, int margin) {
		
		this.App = UIConstants.App;
		
	  buttonGroupStyle = style; 
	  boolean bResettable = (buttonGroupStyle == UIConstants.BUTTONGROUP_MULTIPLE_CHOICE);
	  boolean bAlwaysShowIcon = (buttonGroupStyle == UIConstants.BUTTONGROUP_SINGLE_CHOICE);
	
	  nButtons = names.length;
	
	  if (buttonGroupStyle == UIConstants.BUTTONGROUP_SINGLE_CHOICE) {
	    whichButtonSelected = new int[1]; 
	    whichButtonSelected[0] = UIConstants.NO_BUTTON_SELECTED;
	  } else if (buttonGroupStyle == UIConstants.BUTTONGROUP_MULTIPLE_CHOICE) {
	    whichButtonSelected = new int[nButtons]; 
	    for (int i=0; i<nButtons; i++) {
	      whichButtonSelected[i] = UIConstants.NO_BUTTON_SELECTED;
	    }
	  }
	
	  buttonArray = new UIButton[nButtons]; 
	  for (int i=0; i<nButtons; i++) {
	    String buttonName = names[i];
	    int by = gy + i*(bh+margin); 
	    buttonArray[i] = new UIButton(buttonName, gx, by, bw, bh, bResettable, bAlwaysShowIcon);
	  }
	
	  if (imageNames != null) {
	    for (int i=0; i<nButtons; i++) {
	      String iName = imageNames[i];
	      if (iName != null) {
	        buttonArray[i].setImage (App.loadImage(iName));
	      }
	    }
	  }
	}
	
	//----------------
	int[] getSelectedButtons() {
	  if (buttonGroupStyle == UIConstants.BUTTONGROUP_SINGLE_CHOICE) {
	    whichButtonSelected[0] = UIConstants.NO_BUTTON_SELECTED;
	    for (int i=0; i<nButtons; i++) {
	      if (buttonArray[i].buttonState == UIConstants.BUTTON_STATE_SELECTED) {
	        whichButtonSelected[0] = i;
	      }
	    }
	  } else if (buttonGroupStyle == UIConstants.BUTTONGROUP_MULTIPLE_CHOICE) {
	    for (int i=0; i<nButtons; i++) {
	      whichButtonSelected[i] = UIConstants.NO_BUTTON_SELECTED;
	      if (buttonArray[i].buttonState == UIConstants.BUTTON_STATE_SELECTED) {
	        whichButtonSelected[i] = i;
	      }
	    }
	  }
	  return whichButtonSelected;
	}
	
	//----------------
	String getSelectedButtonString() {
	  String out = "["; 
	  int count = 0; 
	  for (int i=0; i<nButtons; i++) {
	    if (buttonArray[i].buttonState == UIConstants.BUTTON_STATE_SELECTED) {
	      if (count > 0) {
	        out += ", ";
	      }
	      out += "" + i;
	      count++;
	    }
	  }
	  out += "]"; 
	  return out;
	}
	
	
	//----------------
	void update() {
	  handleMouse();
	}
	
	//----------------
	void handleMouse() {
	  int whichWasFreshlySelected = UIConstants.NO_BUTTON_SELECTED;
	  for (int i=0; i<nButtons; i++) {
	    boolean bWasButtonFreshlySelected = buttonArray[i].handleMouse();
	    if (bWasButtonFreshlySelected) {
	      whichWasFreshlySelected = i;
	    }
	  }
	
	  // Allow toggling (for Multi-choice buttons
	  if (buttonGroupStyle == UIConstants.BUTTONGROUP_MULTIPLE_CHOICE) {
	    ;
	  }
	
	  // Enforce exclusivity (for Radio buttons)
	  if (buttonGroupStyle == UIConstants.BUTTONGROUP_SINGLE_CHOICE) {
	    if (whichWasFreshlySelected != UIConstants.NO_BUTTON_SELECTED) {
	      for (int i=0; i<nButtons; i++) {
	        if (i != whichWasFreshlySelected) {
	          buttonArray[i].setState(UIConstants.BUTTON_STATE_UNSELECTED);
	        }
	      }
	    }
	  }
	}
	
	//----------------
	void handleMousePressed() {
	  for (int i=0; i<nButtons; i++) {
	    buttonArray[i].handleMousePressed();
	  }
	}
	
	//----------------
	void drawButtonGroup() {
	  for (int i=0; i<nButtons; i++) {
	    buttonArray[i].drawButton();
	  }
	}
}