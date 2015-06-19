package template.tool;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/*
 * Written by Golan Levin, 6-18-2015.
 * 
 * Translated by Bryce Summers 6-19-2015.
 * 
 * This class specifies tooltip behaviors that provide useful feedback to user
 * regarding the functionality of various UI components, primarily buttons.
 */

public class UITooltip
{
	gui_mainControls App;

	String tooltipString; 
	int millisUntilDisplay = 800; 
	boolean bDisplaying; 
	boolean bPrevMouseInsideMyButton; 
	int mouseStartedBeingInButtonTime; 
	float alpha; 
	float maxAlpha = 216; 

	// -- Constructor.
	public UITooltip (String str) {
		
		this.App = UIConstants.App;
		
		tooltipString = str;
	    bDisplaying = false; 
	    mouseStartedBeingInButtonTime = 0;
	    bPrevMouseInsideMyButton = false; 
	    alpha = 0;
	}

	//-------------------------------------------------
	void update (boolean bMouseIsInsideMyButton) {

		int now = (int) App.millis(); 
	    if (bMouseIsInsideMyButton) {
	    	if (bPrevMouseInsideMyButton == false) {
	    		mouseStartedBeingInButtonTime = now;
	    	}
	    	if ((now - mouseStartedBeingInButtonTime) >= millisUntilDisplay) {
	    		bDisplaying = true; 
	    		alpha = maxAlpha;
	    	}
	    }

	    if (bMouseIsInsideMyButton) {
	    	alpha = maxAlpha;
	    } else {
	    	alpha *= 0.75;
	    }
	    if (alpha < 0.025) {
	    	alpha = 0.0f;
	    	bDisplaying = false;
	    }

	    bPrevMouseInsideMyButton = bMouseIsInsideMyButton;
	  }

	  //-------------------------------------------------
	  void draw() {
		  if (bDisplaying) {
			  float mx = App.mouseX; 
			  float my = App.mouseY; 
			  App.textAlign (App.LEFT, App.CENTER); 
			  float tw = App.textWidth(tooltipString); 
			  float lineheight = App.textAscent() + App.textDescent();
			  float tmargin = 12;


			  int lineCount = 1;
			  CharacterIterator it = new StringCharacterIterator(tooltipString);
			  for (char ch = it.first (); ch != CharacterIterator.DONE; ch = it.next()) {
				  if (ch == '\n') {
					  lineCount++;
				  }
			  }

			  float boxw = tw+tmargin*2;
			  float boxh = lineheight*lineCount +tmargin*2;

			  // Tool tip x.
			  float ttx = App.mouseX; 
			  if ((ttx + boxw) > (App.width-tmargin)) {
				  ttx = App.width - boxw - tmargin;
			  }
			  float tty = App.mouseY;
			  if ((tty + boxh) > (App.height-tmargin)) {
				  tty = App.height - boxh - tmargin;
			  }

			  App.pushMatrix (); 
			  App.translate (ttx, tty); 
			  App.stroke (40, 40, 30, alpha); 
			  App.fill (250, 250, 230, alpha); 
			  App.rect (0, 0, boxw, boxh);
			  App.fill (40, 40, 30, alpha); 
			  App.text (tooltipString, tmargin, boxh/2); 
			  App.popMatrix();
		  }
	  }// End of Draw.
}// End of class.
