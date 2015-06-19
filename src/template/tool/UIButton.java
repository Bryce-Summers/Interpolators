package template.tool;


import processing.core.*;

//=================================================================
//=================================================================
public class UIButton {
		
	gui_mainControls App;
	
	boolean bClickHappenedInMe;
	int bx, by, bw, bh, br, bb; 
	String  buttonText;
	boolean bResettable;
	boolean bAlwaysShowIcon;
	boolean bCenteredText;
	boolean bIsMomentary;
	boolean bHasTooltip; 
	int buttonState; 
	int buttonStateBeforePressing;
	PImage icon; 
	UITooltip tip; 


	//------------------------------------------------------
	UIButton (String s, int x, int y, int w, int h, boolean bRes, boolean bAsi) {
		  
		this.App = UIConstants.App;
		  
	    buttonText = s;
	    bx = x; 
	    by = y; 
	    bw = w; 
	    bh = h; 
	    br = bx+bw;
	    bb = by+bh;
	    bResettable = bRes; 
	    bHasTooltip = false;
	    bIsMomentary = false; 
	    bCenteredText = false;
	    bAlwaysShowIcon = bAsi;
	    bClickHappenedInMe = false;
	    buttonState = UIConstants.BUTTON_STATE_UNSELECTED;
	    buttonStateBeforePressing = UIConstants.BUTTON_STATE_UNSELECTED;
	    icon = null;
	    tip = null;
	}

	void makeTooltip (String str) {
	    tip = new UITooltip (str);
	    bHasTooltip = true;
	}

	void setIsMomentary (boolean b) { 
		bIsMomentary = b;
	}

	void setImage(PImage img) {
		icon = img;
	} 

	void setState(int s) {
		buttonState = s;
	}


	//------------------------------------------------------
	boolean handleMouse() {
	    boolean bFreshlySelected = false;
	    boolean bMouseIsInside = (((App.mouseX >= bx) && (App.mouseY >= by) && (App.mouseX <= (bx+bw)) && (App.mouseY <= (by+bh))));
	    if (bHasTooltip) {
	    	tip.update(bMouseIsInside);
	    }

	    if (bIsMomentary) {
	    	if (App.mousePressed && bClickHappenedInMe) {
	    		if (bMouseIsInside) {
	    			buttonState = UIConstants.BUTTON_STATE_SELECTED;
	    		} else {
	    			buttonState = UIConstants.BUTTON_STATE_PRESSING;
	    		}
	    	} else if (App.mousePressed && !bClickHappenedInMe) {
	    		buttonState = UIConstants.BUTTON_STATE_UNSELECTED;
	    	} else if (!App.mousePressed && bClickHappenedInMe) {

	    		if ((buttonState == UIConstants.BUTTON_STATE_SELECTED) || (buttonState == UIConstants.BUTTON_STATE_PRESSING)) {
	    			if (bMouseIsInside) {
	    				buttonState = UIConstants.BUTTON_STATE_HOVERING;
	    				bClickHappenedInMe = false;
	    				bFreshlySelected = true;
	    			} else {
	    				buttonState = UIConstants.BUTTON_STATE_UNSELECTED;
	    				bClickHappenedInMe = false;
	    			}
	    		}
	    	} else if (!App.mousePressed && !bClickHappenedInMe) {
	    		if (bMouseIsInside) {
	    			buttonState = UIConstants.BUTTON_STATE_HOVERING;
	    		} else {
	    			buttonState = UIConstants.BUTTON_STATE_UNSELECTED;
	    		}
	    	}
	    	;
	    	;
	    } else {
	    	if (!App.mousePressed && !bClickHappenedInMe) {
	    		if (buttonState != UIConstants.BUTTON_STATE_SELECTED) {
	    			if (bMouseIsInside) {
	    				buttonState = UIConstants.BUTTON_STATE_HOVERING;
	    			} else {
	    				buttonState = UIConstants.BUTTON_STATE_UNSELECTED;
	    			}
	    		} else {
	    			;
	    		}
	    		;
	    	} else if (App.mousePressed && bClickHappenedInMe) {
	    		if (buttonState != UIConstants.BUTTON_STATE_PRESSING) {
	    			buttonStateBeforePressing = buttonState;
	    		}
	    		buttonState = UIConstants.BUTTON_STATE_PRESSING;
	    		;
	    	} else if (!App.mousePressed && bClickHappenedInMe) {
	    		if (bMouseIsInside) {
	    			if (bResettable && (buttonStateBeforePressing == UIConstants.BUTTON_STATE_SELECTED)) {
	    				buttonState = UIConstants.BUTTON_STATE_UNSELECTED;
	    			} else {	
	    				if (buttonState != UIConstants.BUTTON_STATE_SELECTED) {
	    					bFreshlySelected = true;
	    				}
	    				buttonState = UIConstants.BUTTON_STATE_SELECTED;
	    			}
	    		}
	    		bClickHappenedInMe = false;
	    	}
	    }
	    return bFreshlySelected;
	}

	// -------------------------------
	// Returns true iff this button is curently selected.
	boolean isSelected()
	{
		return buttonState == UIConstants.BUTTON_STATE_SELECTED;
	}
	
	//------------------------------------------------------
	void handleMousePressed() {
		if ((App.mouseX >= bx) && (App.mouseY >= by) && (App.mouseX <= br) && (App.mouseY <= bb)) {
			bClickHappenedInMe = true;
	    } else {
	    	bClickHappenedInMe = false;
	    }
	}

	void drawTooltip() {
		// draw the tooltip 
	    if (bHasTooltip) {
	    	tip.draw();
	    }
	}

	//------------------------------------------------------
	void drawButton() {

		// BUTTON_STATE_UNSELECTED color properties (default):
	    int black = App.color (0); 
	    int buttonBorderColorA = App.color (60, 60, 65, 90); 
	    int buttonBorderColorB = App.color (0, 0, 0, 90); 
	    int buttonFillColorA = App.color (238, 238, 243); 
	    int buttonFillColorB = App.color (220, 225, 230); 
	    int buttonHighlightColor = App.color (250, 250, 255, 180); 
	    int buttonShadowColor = App.color (180, 185, 190, 180); 
	    int textColorB = App.color (0, 0, 10, 160);
	    int textColorA = App.color (255, 255, 255, 180);

	    int dec = 8;
	    int tdy = 0; 
	    boolean b2PixelBevel = false;

	    switch (buttonState) {
	    default: 
	      break;
	    case UIConstants.BUTTON_STATE_SELECTED:
	      buttonFillColorA = App.color (200, 255, 210); 
	      buttonFillColorB = App.color (180, 240, 190); 
	      textColorB = App.color (0, 0, 5, 240);
	      if (bIsMomentary) {
	    	  buttonBorderColorB   = App.color (40, 40, 45, 128); 
	    	  buttonBorderColorA   = App.color (0, 0, 0, 128); 
	    	  buttonShadowColor    = App.color (255, 255, 240, 180); 
	    	  buttonHighlightColor = App.color (200, 195, 190, 180);
	    	  tdy = 1;
	      }
	      break;
	    case UIConstants.BUTTON_STATE_HOVERING:
	    	buttonFillColorA = App.color (248, 244, 240); 
	    	buttonFillColorB = App.color (230, 225, 225); 
	    	buttonHighlightColor = App.color (255, 255, 240, 180); 
	    	buttonShadowColor = App.color (200, 195, 190, 180); 
	    	break;
	    case UIConstants.BUTTON_STATE_PRESSING:
	      buttonFillColorA = App.color (248-dec, 244-dec, 240-dec); 
	      buttonFillColorB = App.color (230-dec, 225-dec, 225-dec); 
	      buttonBorderColorB = App.color (40, 40, 45, 128); 
	      buttonBorderColorA = App.color (0, 0, 0, 128); 
	      buttonShadowColor = App.color (255, 255, 240, 180); 
	      buttonHighlightColor = App.color (200, 195, 190, 180);
	      tdy = 1;
	      break;
	    }

	    // fill the button with a gradient
	    for (int y=by; y<=bb; y++) {
	    	float yFrac = App.map(y, by, bb, 0, 1); 
	    	int rowColor = App.lerpColor(buttonFillColorA, buttonFillColorB, yFrac);
	    	App.stroke (rowColor); 
	    	if ((y == by) || (y == bb)) {
	    		App.line (bx+1, y, br-1, y);
	    	} else {
	    		App.line (bx, y, br, y);
	    	}
	    }

	    // draw the highlights and shadows of the button body
	    App.stroke (buttonHighlightColor); 
	    App. line (bx+1, by+1, br-2, by+1); 
	    App.line (bx+1, by+1, bx+1, bb-2); 
	    if (b2PixelBevel) {
	    	App. line (bx+1, by+2, br-3, by+2); 
	    	App.line (bx+2, by+1, bx+2, bb-3);
	    }
	    App.stroke (buttonShadowColor); 
	    App.line (bx+2, bb-1, br-1, bb-1); 
	    App.line (br-1, by+2, br-1, bb-2); 
	    if (b2PixelBevel) {
	    	App.line (bx+3, bb-2, br-2, bb-2); 
	    	App.line (br-2, by+3, br-2, bb-3);
	    }

	    // draw the external borders
	    App.stroke (buttonBorderColorA);
	    App.line (bx+1, by, br-1, by); 
	    App.line (bx, by+1, bx, bb-1);  
	    App.stroke (buttonBorderColorB);
	    App.line (bx+1, bb, br-1, bb); 
	    App.line (br, by+1, br, bb-1);

	    // render the text
	    App.textFont (UIConstants.buttonFont); 
	    float th = App.textAscent(); 
	    float ty = by + (bh + th)*0.5f; 
	    float tx = bx + 11; 
	    App.textAlign (App.LEFT);
	    if (bCenteredText) {
	    	App.textAlign (App.CENTER); 
	    	tx = (bx + bw/2.0f);
	    } 
	    App.fill (textColorA); 
	    App.text (buttonText, tx+1, ty+1+tdy); 
	    App.fill (textColorB); 
	    App.text (buttonText, tx, ty+tdy);

	    if (icon != null) {
	    	if (bAlwaysShowIcon) {
	    		float cw = 24; 
	    		float ch = 24; 
	    		float cx = br - cw - (bh-ch)/2;
	    		float cy = by + (bh-ch)/2;
	    		if (buttonState == UIConstants.BUTTON_STATE_SELECTED) {
	    			App.tint(255, 255, 255, 210);
	    		} else {
	    			App.tint(255, 255, 255, 80);
	    		}
	    		App.image (icon, cx, cy, cw, ch);
	    		App.noTint();
	    	} else {
	    		if (buttonState == UIConstants.BUTTON_STATE_SELECTED) {
	    			float cw = 24; 
	    			float ch = 24; 
	    			float cx = br - cw - (bh-ch)/2;
	    			float cy = by + (bh-ch)/2;
	    			App.tint(255, 255, 255, 210); 
	    			App.image (icon, cx, cy, cw, ch);
	    			App.noTint();
	    		}
	    	}
	    }
	}
}
