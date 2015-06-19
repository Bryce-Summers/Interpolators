import java.text.*;

PFont buttonFont; 
PImage titlebar; 
PImage sfcilogo;

final int NO_BUTTON_SELECTED = -1;
final int BUTTON_STATE_UNSELECTED = 0; 
final int BUTTON_STATE_HOVERING = 1;
final int BUTTON_STATE_PRESSING = 2; 
final int BUTTON_STATE_SELECTED = 3;

final int BUTTONGROUP_SINGLE_CHOICE = 1; 
final int BUTTONGROUP_MULTIPLE_CHOICE = 2; 

UIButtonGroup buttonGroupA;
UIButtonGroup buttonGroupB;
UIButton generateButton;


String[] namesA = {
  "All", "Sigmoid", "Ogee", "Ease-In", "Ease-Out", "Penner's", "Gaussian", "Bezier", "Staircase", "Window", "Other"
};
String[] imagesA = {
  null, "Sigmoid.png", "Ogee.png", "Ease-In.png", "Ease-Out.png", null, "Gaussian.png", null, "Staircase.png", "Window.png", null
};
String[] tooltipStringsA = {
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

String[] namesB = {
  "Flip X?", "Flip Y?", "Constrain Output?", "Comment Only?", "Use New Tab?"
};
String[] imagesB = {
  "check_24.png", "check_24.png", "check_24.png", "check_24.png", "check_24.png"
};
String[] tooltipStringsB = {
  "When selected, flips the input (horizontally).", 
  "When selected, flips the output (vertically).", 
  "When selected, clamps the output to the range [0...1].", 
  "When selected, only inserts the function call.", 
  "When selected, places generated code in a new tab.\nIf unselected, code is inserted at the cursor."
};
//   


//----------------
void setup() {
  size (1200, 600);
  titlebar = loadImage("titlebar.png"); 
  sfcilogo = loadImage("sfci-logo.png"); 

  buttonFont = loadFont("Roboto-Medium-16.vlw");
  buttonGroupA = new UIButtonGroup (BUTTONGROUP_SINGLE_CHOICE, namesA, imagesA, tooltipStringsA, 7, 48, 130, 42, 8);
  buttonGroupB = new UIButtonGroup (BUTTONGROUP_MULTIPLE_CHOICE, namesB, imagesB, tooltipStringsB, 987, 79, 191, 42, 8);

  generateButton = new UIButton("GENERATE CODE!", 987, 343, 192, 181, false, false);
  generateButton.bCenteredText = true; 
  generateButton.setIsMomentary(true);
  generateButton.makeTooltip ("When clicked, inserts code for\nthis function into your project.");
}

//----------------
void draw() {
  background(255);
  image (titlebar, 0, 0); 
  image (sfcilogo, width - sfcilogo.width, height - sfcilogo.height); 

  buttonGroupA.update();
  buttonGroupA.drawButtonGroup();

  buttonGroupB.update();
  buttonGroupB.drawButtonGroup();

  boolean bDoGenerate = generateButton.handleMouse();
  if (bDoGenerate) { 
    // println ("GENERATE!");
  }
  generateButton.drawButton();

  buttonGroupA.drawTooltips(); 
  buttonGroupB.drawTooltips();
  generateButton.drawTooltip();
}

//----------------
void mousePressed() {
  buttonGroupA.handleMousePressed();
  buttonGroupB.handleMousePressed();
  generateButton.handleMousePressed();
}

//----------------
void drawButtonGroupHeadings() {
  fill(100); 
  textAlign (LEFT);
  text ("Family: " + buttonGroupA.getSelectedButtonString(), 7, 20); 
  text ("Options: " + buttonGroupB.getSelectedButtonString(), 200, 20);
}


//=================================================================
//=================================================================
class Tooltip {

  String tooltipString; 
  int millisUntilDisplay = 800; 
  boolean bDisplaying; 
  boolean bPrevMouseInsideMyButton; 
  int mouseStartedBeingInButtonTime; 
  float alpha; 
  float maxAlpha = 216; 

  Tooltip (String str) {
    tooltipString = str;
    bDisplaying = false; 
    mouseStartedBeingInButtonTime = 0;
    bPrevMouseInsideMyButton = false; 
    alpha = 0;
  }

  //-------------------------------------------------
  void update (boolean bMouseIsInsideMyButton) {

    int now = (int) millis(); 
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
      alpha = 0.0;
      bDisplaying = false;
    }

    bPrevMouseInsideMyButton = bMouseIsInsideMyButton;
  }

  //-------------------------------------------------
  void draw() {
    if (bDisplaying) {
      float mx = mouseX; 
      float my = mouseY; 
      textAlign (LEFT, CENTER); 
      float tw = textWidth(tooltipString); 
      float lineheight = textAscent() + textDescent();
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


      float ttx = mouseX; 
      if ((ttx + boxw) > (width-tmargin)) {
        ttx = width - boxw - tmargin;
      }
      float tty = mouseY; 

      pushMatrix (); 
      translate (ttx, tty); 
      stroke (40, 40, 30, alpha); 
      fill (250, 250, 230, alpha); 
      rect (0, 0, boxw, boxh);
      fill (40, 40, 30, alpha); 
      text (tooltipString, tmargin, boxh/2); 
      popMatrix();
    }
  }
}


//=================================================================
//=================================================================
class UIButtonGroup {

  int nButtons;
  UIButton buttonArray[];
  int whichButtonSelected[]; 
  int buttonGroupStyle;

  //----------------
  UIButtonGroup (int style, String[] names, String[] imageNames, String tooltipStrings[], int gx, int gy, int bw, int bh, int margin) {
    buttonGroupStyle = style; 
    boolean bResettable = (buttonGroupStyle == BUTTONGROUP_MULTIPLE_CHOICE);
    boolean bAlwaysShowIcon = (buttonGroupStyle == BUTTONGROUP_SINGLE_CHOICE);

    nButtons = names.length;

    if (buttonGroupStyle == BUTTONGROUP_SINGLE_CHOICE) {
      whichButtonSelected = new int[1]; 
      whichButtonSelected[0] = NO_BUTTON_SELECTED;
    } else if (buttonGroupStyle == BUTTONGROUP_MULTIPLE_CHOICE) {
      whichButtonSelected = new int[nButtons]; 
      for (int i=0; i<nButtons; i++) {
        whichButtonSelected[i] = NO_BUTTON_SELECTED;
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
          buttonArray[i].setImage (loadImage(iName));
        }
      }
    }

    for (int i=0; i<nButtons; i++) {
      String tipString = tooltipStrings[i];
      buttonArray[i].makeTooltip (tipString);
    }
  }
  
  //----------------
  boolean getIsButtonSelected(int which){
    boolean bOut = false; 
    if ((which < nButtons) && (which >= 0)){
      int state = whichButtonSelected[which];
      if (state != NO_BUTTON_SELECTED){
        bOut = true; 
      }
    }
    return bOut; 
  }

  //----------------
  int[] getSelectedButtons() {
    if (buttonGroupStyle == BUTTONGROUP_SINGLE_CHOICE) {
      whichButtonSelected[0] = NO_BUTTON_SELECTED;
      for (int i=0; i<nButtons; i++) {
        if (buttonArray[i].buttonState == BUTTON_STATE_SELECTED) {
          whichButtonSelected[0] = i;
        }
      }
    } else if (buttonGroupStyle == BUTTONGROUP_MULTIPLE_CHOICE) {
      for (int i=0; i<nButtons; i++) {
        whichButtonSelected[i] = NO_BUTTON_SELECTED;
        if (buttonArray[i].buttonState == BUTTON_STATE_SELECTED) {
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
      if (buttonArray[i].buttonState == BUTTON_STATE_SELECTED) {
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
    int whichWasFreshlySelected = NO_BUTTON_SELECTED;
    for (int i=0; i<nButtons; i++) {
      boolean bWasButtonFreshlySelected = buttonArray[i].handleMouse();
      if (bWasButtonFreshlySelected) {
        whichWasFreshlySelected = i;
      }
    }

    // Allow toggling (for Multi-choice buttons
    if (buttonGroupStyle == BUTTONGROUP_MULTIPLE_CHOICE) {
      ;
    }

    // Enforce exclusivity (for Radio buttons)
    if (buttonGroupStyle == BUTTONGROUP_SINGLE_CHOICE) {
      if (whichWasFreshlySelected != NO_BUTTON_SELECTED) {
        for (int i=0; i<nButtons; i++) {
          if (i != whichWasFreshlySelected) {
            buttonArray[i].setState(BUTTON_STATE_UNSELECTED);
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

  //----------------
  void drawTooltips() {
    for (int i=0; i<nButtons; i++) {
      buttonArray[i].drawTooltip();
    }
  }
}


//=================================================================
//=================================================================
class UIButton {

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
  Tooltip tip; 


  //------------------------------------------------------
  UIButton (String s, int x, int y, int w, int h, boolean bRes, boolean bAsi) {
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
    buttonState = BUTTON_STATE_UNSELECTED;
    buttonStateBeforePressing = BUTTON_STATE_UNSELECTED;
    icon = null;
    tip = null;
  }

  void makeTooltip (String str) {
    tip = new Tooltip (str);
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
    boolean bMouseIsInside = (((mouseX >= bx) && (mouseY >= by) && (mouseX <= (bx+bw)) && (mouseY <= (by+bh))));
    if (bHasTooltip) {
      tip.update(bMouseIsInside);
    }

    if (bIsMomentary) {
      if (mousePressed && bClickHappenedInMe) {
        if (bMouseIsInside) {
          buttonState = BUTTON_STATE_SELECTED;
        } else {
          buttonState = BUTTON_STATE_PRESSING;
        }
      } else if (mousePressed && !bClickHappenedInMe) {
        buttonState = BUTTON_STATE_UNSELECTED;
      } else if (!mousePressed && bClickHappenedInMe) {

        if ((buttonState == BUTTON_STATE_SELECTED) || (buttonState == BUTTON_STATE_PRESSING)) {
          if (bMouseIsInside) {
            buttonState = BUTTON_STATE_HOVERING;
            bClickHappenedInMe = false;
            bFreshlySelected = true;
          } else {
            buttonState = BUTTON_STATE_UNSELECTED;
            bClickHappenedInMe = false;
          }
        }
      } else if (!mousePressed && !bClickHappenedInMe) {
        if (bMouseIsInside) {
          buttonState = BUTTON_STATE_HOVERING;
        } else {
          buttonState = BUTTON_STATE_UNSELECTED;
        }
      }
      ;
      ;
    } else {
      if (!mousePressed && !bClickHappenedInMe) {
        if (buttonState != BUTTON_STATE_SELECTED) {
          if (bMouseIsInside) {
            buttonState = BUTTON_STATE_HOVERING;
          } else {
            buttonState = BUTTON_STATE_UNSELECTED;
          }
        } else {
          ;
        }
        ;
      } else if (mousePressed && bClickHappenedInMe) {
        if (buttonState != BUTTON_STATE_PRESSING) {
          buttonStateBeforePressing = buttonState;
        }
        buttonState = BUTTON_STATE_PRESSING;
        ;
      } else if (!mousePressed && bClickHappenedInMe) {
        if (bMouseIsInside) {
          if (bResettable && (buttonStateBeforePressing == BUTTON_STATE_SELECTED)) {
            buttonState = BUTTON_STATE_UNSELECTED;
          } else {
            if (buttonState != BUTTON_STATE_SELECTED) {
              bFreshlySelected = true;
            }
            buttonState = BUTTON_STATE_SELECTED;
          }
        }
        bClickHappenedInMe = false;
      }
    }
    return bFreshlySelected;
  }


  //------------------------------------------------------
  void handleMousePressed() {
    if ((mouseX >= bx) && (mouseY >= by) && (mouseX <= br) && (mouseY <= bb)) {
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
    color black = color (0); 
    color buttonBorderColorA = color (60, 60, 65, 90); 
    color buttonBorderColorB = color (0, 0, 0, 90); 
    color buttonFillColorA = color (238, 238, 243); 
    color buttonFillColorB = color (220, 225, 230); 
    color buttonHighlightColor = color (250, 250, 255, 180); 
    color buttonShadowColor = color (180, 185, 190, 180); 
    color textColorB = color (0, 0, 10, 160);
    color textColorA = color (255, 255, 255, 180);

    int dec = 8;
    int tdy = 0; 
    boolean b2PixelBevel = false;

    switch (buttonState) {
    default: 
      break;
    case BUTTON_STATE_SELECTED:
      buttonFillColorA = color (200, 255, 210); 
      buttonFillColorB = color (180, 240, 190); 
      textColorB = color (0, 0, 5, 240);
      if (bIsMomentary) {
        buttonBorderColorB = color (40, 40, 45, 128); 
        buttonBorderColorA = color (0, 0, 0, 128); 
        buttonShadowColor = color (255, 255, 240, 180); 
        buttonHighlightColor = color (200, 195, 190, 180);
        tdy = 1;
      }
      break;
    case BUTTON_STATE_HOVERING:
      buttonFillColorA = color (248, 244, 240); 
      buttonFillColorB = color (230, 225, 225); 
      buttonHighlightColor = color (255, 255, 240, 180); 
      buttonShadowColor = color (200, 195, 190, 180); 
      break;
    case BUTTON_STATE_PRESSING:
      buttonFillColorA = color (248-dec, 244-dec, 240-dec); 
      buttonFillColorB = color (230-dec, 225-dec, 225-dec); 
      buttonBorderColorB = color (40, 40, 45, 128); 
      buttonBorderColorA = color (0, 0, 0, 128); 
      buttonShadowColor = color (255, 255, 240, 180); 
      buttonHighlightColor = color (200, 195, 190, 180);
      tdy = 1;
      break;
    }

    // fill the button with a gradient
    for (int y=by; y<=bb; y++) {
      float yFrac = map(y, by, bb, 0, 1); 
      color rowColor = lerpColor(buttonFillColorA, buttonFillColorB, yFrac);
      stroke (rowColor); 
      if ((y == by) || (y == bb)) {
        line (bx+1, y, br-1, y);
      } else {
        line (bx, y, br, y);
      }
    }

    // draw the highlights and shadows of the button body
    stroke (buttonHighlightColor); 
    line (bx+1, by+1, br-2, by+1); 
    line (bx+1, by+1, bx+1, bb-2); 
    if (b2PixelBevel) {
      line (bx+1, by+2, br-3, by+2); 
      line (bx+2, by+1, bx+2, bb-3);
    }
    stroke (buttonShadowColor); 
    line (bx+2, bb-1, br-1, bb-1); 
    line (br-1, by+2, br-1, bb-2); 
    if (b2PixelBevel) {
      line (bx+3, bb-2, br-2, bb-2); 
      line (br-2, by+3, br-2, bb-3);
    }

    // draw the external borders
    stroke (buttonBorderColorA);
    line (bx+1, by, br-1, by); 
    line (bx, by+1, bx, bb-1);  
    stroke (buttonBorderColorB);
    line (bx+1, bb, br-1, bb); 
    line (br, by+1, br, bb-1);

    // render the text
    textFont (buttonFont); 
    float th = textAscent(); 
    float ty = by + (bh + th)*0.5; 
    float tx = bx + 11; 
    textAlign (LEFT);
    if (bCenteredText) {
      textAlign (CENTER); 
      tx = (bx + bw/2.0);
    } 
    fill (textColorA); 
    text (buttonText, tx+1, ty+1+tdy); 
    fill (textColorB); 
    text (buttonText, tx, ty+tdy);

    if (icon != null) {
      if (bAlwaysShowIcon) {
        float cw = 24; 
        float ch = 24; 
        float cx = br - cw - (bh-ch)/2;
        float cy = by + (bh-ch)/2;
        if (buttonState == BUTTON_STATE_SELECTED) {
          tint(255, 255, 255, 210);
        } else {
          tint(255, 255, 255, 80);
        }
        image (icon, cx, cy, cw, ch);
        noTint();
      } else {
        if (buttonState == BUTTON_STATE_SELECTED) {
          float cw = 24; 
          float ch = 24; 
          float cx = br - cw - (bh-ch)/2;
          float cy = by + (bh-ch)/2;
          tint(255, 255, 255, 210); 
          image (icon, cx, cy, cw, ch);
          noTint();
        }
      }
    }
  }
}

