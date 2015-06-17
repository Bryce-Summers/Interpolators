package template.tool;

import processing.app.Editor;
import processing.app.Sketch;
import processing.core.*;
import processing.event.MouseEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

// Imports for PDF, to save a vector graphic of the function.
//import processing.pdf.*;

// FIXME : Clean up the dependency injection code and make it more robust.
// FIXME : Improve the user interface.
// Give the user the option of injecting only a parameter comment.

public class gui_mainControls extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean doSavePDF=false;

	boolean bDrawProbe = true;
	boolean bDrawGrayScale = true;
	boolean bDrawNoiseHistories = true; 
	boolean bDrawModeSpecificGraphics = true;
	boolean bDrawAnimatingRadiusCircle = true;

	int boundingBoxStrokeColor = color(180);

	//-----------------------------------------------------
	float xscale = 300;
	float yscale = 300;
	float bandTh  = 60;
	float margin0 = 10;
	float margin1 = 5;
	float margin2 = 90;
	float xoffset = margin0 + bandTh + margin1 + 550;
	float yoffset = margin0 + bandTh + margin1 + 60;
	float xoffset2 = xoffset - 90; 
	
	
	float param_a = 0.25f;
	float param_b = 0.75f;
	float param_c = 0.75f;
	float param_d = 0.25f;
	int   param_n = 3;
	float f_n = param_n;

	float probe_x = 0.5f;
	float probe_y = 0.5f;
	float animationConstant = 1000.0f;

	boolean visited = false;
	boolean bClickedInGraph = false;

	
	int MAX_N_float_PARAMS = 4;

	//DataHistoryGraph noiseHistory;
	DataHistoryGraph cosHistory;
	//DataHistoryGraph triHistory;
	
	
	// The important variables when dealing with functions.
	int FUNCTIONMODE = 0;// The index of the current function???
	String functionName = "";
	public ArrayList<Method> functionMethodArraylist;
	int nFunctionMethods;

	public static Editor editor = null;

	PFont Font_bold = createFont("Arial Bold", 12);
	PFont Font_normal = createFont("Arial", 12);

	// Bryce's Variables.
	
	VScrollbar scroll;
	
	// Horizontal Scroll Bars for the Parameters.
	HScrollbar parameter_scroll_a;
	HScrollbar parameter_scroll_b;
	HScrollbar parameter_scroll_c;
	HScrollbar parameter_scroll_d;
	HScrollbar parameter_scroll_n;
	
	int selection_text_x = 150;//500;
	int scroll_x = selection_text_x + 320 + 40;
	int selection_line_h = 20;
	int export_offsetY = 100;
	
	// -- Export variables.
	int go_x = 1000 + 50;
	int go_y = 350 + export_offsetY;
	
	int newTab_x = 1000;
	int newTab_y = 250 + export_offsetY;
	
	int comment_x = 1000;
	int comment_y = 200 + export_offsetY;
	
	int clamp_x = 1000;
	int clamp_y = 150 + export_offsetY;
	
	int flipX_x = 1000;
	int flipX_y = 50 + export_offsetY;
	
	int flipY_x = 1000;
	int flipY_y = 100 + export_offsetY;
	
	int comment_radius = 20;
	int newTab_radius = 20;
	int clamp_radius = 20;
	int flipX_radius = 20;
	int flipY_radius = 20;
	int drawButton_radius = 50;
		
	boolean bool_comment = false;
	boolean bool_newTab = true;
	boolean bool_clamp = false;
	boolean bool_flipX = false;
	boolean bool_flipY = false;
	
	f_group[] function_groups;
	int group_index = 0;
	
	gui_functionSelection selector;
	
	gui_mainControls me = this;
	
	public void addSelectionControls(gui_functionSelection selector)
	{
		this.selector = selector;
	}
	
	class f_group implements Iterable<Integer>
	{
		ArrayList<Integer> indices = new ArrayList<Integer>();
		
		int x, y;
		int w, h;
		
		Method method;
		Object[] inputs;
		// Specifies what the indice of this function group is in the global function group array.
		int myIndex = -1;
		
		String name = "";
		
		// Specifies what the current_index is within this function group.
		int current_index = 0;
		
		public f_group(int x, int y, int w, int h, int index)
		{
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;	
			this.myIndex = index;
		}
		
		public void addIndice(int i)
		{
			indices.add(i);
		}
		
		public boolean mouseIn()
		{
			return	x <= mouseX && mouseX <= x + w &&
					y <= mouseY && mouseY <= y + h;
		}
		
		public Iterator<Integer> iterator()
		{
			return indices.iterator();
		}
		
		public void draw()
		{
			if(mouseIn())
			{
				fill(0, 255, 255);
			}
			else
			{
				fill(255);
			}
			
			for(int i = 0; i < w; i++)
			{
				//line(i, functionMethodArraylist.get(i));
			}
			
			rect(x, y, w, h);
			
			fill(0);
			text(name, x + 10, y + h/2);
		}

		public void mouseP() 
		{
			
			group_index = myIndex;

			// Reset the position of the vertical scrollbar.
			scroll.setVal(0);
			
			current_index = 0;
			FUNCTIONMODE = get(current_index);
			
		}

		public int size() 
		{
			return indices.size();
		}

		public int get(int index)
		{
			if(index < 0 || index > indices.size())
			{
				return 0;
			}
			
			return indices.get(index);
		}

		public void name(String string)
		{
			this.name = string;
		}
		
		// Scrolls the index up.
		public void index_increment()
		{
			current_index = (current_index + 1) % indices.size();
			FUNCTIONMODE = get(current_index);
		}
		
		// Scrolls the index down.
		public void index_decrement()
		{
			int len = indices.size();
			current_index = (current_index + len - 1) % len; 
			FUNCTIONMODE = get(current_index);
		}
		
		public void index_fix()
		{
			FUNCTIONMODE = get(current_index);
		}
	}
	
	Object active_scrollbar = null;
	
	class VScrollbar {
		  int swidth, sheight;    // width and height of bar
		  float xpos, ypos;       // x and y position of bar
		  float spos, newspos;    // y position of slider
		  float sposMin, sposMax; // max and min values of slider
		  int loose;              // how loose/heavy
		  boolean over;           // is the mouse over the slider?
		  boolean locked;
		  float ratio;

		  VScrollbar (float xp, float yp, int sw, int sh, int l) {
		    swidth = sw;
		    sheight = sh;
		    int heighttowidth = sh - sw;
		    ratio = (float)sh / (float)heighttowidth;
		    xpos = xp - swidth/2;
		    ypos = yp;
		    spos = ypos + sheight/2 - swidth/2;
		    newspos = spos;
		    sposMin = ypos;
		    sposMax = ypos + sheight - swidth;
		    loose = l;
		  }

		  public void setVal(float percentage)
		  {
			  float target = ypos + (sheight - swidth)*percentage;
			  //spos = target;
			  newspos = target;
		  }

		void update() {
		    if (overEvent()) {
		      over = true;
		    } else {
		      over = false;
		    }
		    if (mousePressed && over && active_scrollbar == null) {
		      locked = true;
		      active_scrollbar = this;
		    }
		    if (!mousePressed) {
		      locked = false;
		      active_scrollbar = null;
		      
		    }
		    if (locked) {
		      newspos = constrain(mouseY-swidth/2, sposMin, sposMax);
		    }
		    if (abs(newspos - spos) > 1) {
		      spos = spos + (newspos-spos)/loose;
		    }
		  }

		  float constrain(float val, float minv, float maxv) {
		    return min(max(val, minv), maxv);
		  }

		  boolean overEvent() {
		    if (mouseX > xpos && mouseX < xpos+swidth &&
		       mouseY > ypos && mouseY < ypos+sheight) {
		      return true;
		    } else {
		      return false;
		    }
		  }

		  void display() {
		    noStroke();
		    fill(204);
		    rect(xpos, ypos, swidth, sheight);
		    if (over || locked) {
		      fill(0, 0, 0);
		    } else {
		      fill(102, 102, 102);
		    }
		    rect(xpos, spos, swidth, swidth);
		  }

		  float getPos() {
		    // Convert spos to be values between
		    // 0 and the total height of the scrollbar
		    return (spos - swidth/2 - ypos)/(sheight - swidth);
		  }
		}
	
	// Horizontal Scrollbar.
	class HScrollbar {
		  int swidth, sheight;    // width and height of bar
		  float xpos, ypos;       // x and y position of bar
		  float spos, newspos;    // x position of slider
		  float sposMin, sposMax; // max and min values of slider
		  int loose;              // how loose/heavy
		  boolean over;           // is the mouse over the slider?
		  boolean locked;
		  float ratio;
		  
		  boolean active = true;

		  HScrollbar (float xp, float yp, int sw, int sh, int l) {
		    swidth = sw;
		    sheight = sh;
		    int widthtoheight = sw - sh;
		    ratio = (float)sw / (float)widthtoheight;
		    xpos = xp;
		    ypos = yp-sheight/2;
		    spos = xpos + swidth/2 - sheight/2;
		    newspos = spos;
		    sposMin = xpos;
		    sposMax = xpos + swidth - sheight;
		    loose = l;
		  }

		  void update() {
		    if (overEvent()) {
		      over = true;
		    } else {
		      over = false;
		    }
		  
		    if (mousePressed && over && active_scrollbar == null && active)
		    {
			   locked = true;
			   active_scrollbar = this;
			}
			if (!mousePressed) {
				locked = false;
				active_scrollbar = null;
			}
		    
		    if (locked) {
		      newspos = constrain(mouseX-sheight/2, sposMin, sposMax);
		    }
		    if (abs(newspos - spos) > 1) {
		      spos = spos + (newspos-spos)/loose;
		    }
		  }

		  float constrain(float val, float minv, float maxv) {
		    return min(max(val, minv), maxv);
		  }

		  boolean overEvent() {
		    if (mouseX > xpos && mouseX < xpos+swidth &&
		       mouseY > ypos && mouseY < ypos+sheight) {
		      return true;
		    } else {
		      return false;
		    }
		  }

		  void display() {
		    noStroke();
		    
		    if(active)
		    {
		    	// Scrollbar Active color.
		    	fill(204);
		    }
		    else
		    {
		    	// Scrollbar InActive Color.
		    	fill(220);
		    }
		    rect(xpos, ypos, swidth, sheight);
		    
		    if(!active)
		    {
		    	return;
		    }
		    
		    // Select Region.
		    if (over || locked) {
		      fill(0, 0, 0);
		    } else {
		      fill(102, 102, 102);
		    }
		    rect(spos, ypos, sheight, sheight);
		  }

		  float getPos() {
		    // Convert spos to be values between
		    // 0 and the total width of the scrollbar
		    return spos * ratio;
		  }
		  
		  float getVal(float range)
		  {
			  float output = (spos - sposMin) / (swidth - sheight)*range;
			  
			  if(output < .01)
			  {
				  return 0.0f;
			  }
			  
			  if(output > range - .01)
			  {
				  return range;
			  }
			  
			  return output;
		  }
		  
		  public void setVal(float percentage)
		  {
			  float target = xpos + (swidth - sheight)*percentage;
			  //spos = target;
			  newspos = target;
		  }
		}


	
	
	//-----------------------------------------------------
	public void keyPressed() {
	  int nFunctions = functionMethodArraylist.size(); 
	  
	  if (key == CODED) { 
	    if ((keyCode == DOWN) || (keyCode == RIGHT)) { 
	      FUNCTIONMODE = (FUNCTIONMODE+1)%nFunctions;
	    } 
	    else if ((keyCode == UP) || (keyCode == LEFT)) { 
	      FUNCTIONMODE = (FUNCTIONMODE-1+nFunctions)%nFunctions;
	    }
	  } 
	  if (key=='P') {
	    doSavePDF = true;
	  }
	  
	}
	
	// The function that sends the function data to the text editor.
	public void export()
	{
		// Add a new tab if neccessary.
		if(bool_newTab)
		{
			// New Tab code.
			Sketch sketch = editor.getSketch();
			sketch.handleNewCode();

		}
		
		// Find the name of the function we are searching for.
		int whichFunction = FUNCTIONMODE%nFunctionMethods;  
		Method whichMethod = functionMethodArraylist.get(whichFunction); 
		String methodName = whichMethod.getName();
		
		// Handle comment only.
		if(bool_comment)
		{
			StringBuilder output = new StringBuilder();
			comment(output, methodName);
			editor.insertText(output.toString());
			return;
		}
		
		
 
		HashSet<String> functions_included = new HashSet<String>();
		HashSet<String> helper_names = new HashSet<String>();
		
		helper_names.add("sinc");
		helper_names.add("linetopoint");
		helper_names.add("computeFilletParameters");
		helper_names.add("IsPerpendicular");
		helper_names.add("calcCircleFrom3Points");
		helper_names.add("slopeFromT");
		helper_names.add("xFromT");
		helper_names.add("yFromT");
		helper_names.add("B0");
		helper_names.add("B1");
		helper_names.add("B2");
		helper_names.add("B3");
		helper_names.add("findx");
		helper_names.add("findy");
		helper_names.add("function_CubicBezier");
		
		HashSet<String> dependancies = new HashSet<String>();

		StringBuilder text = new StringBuilder();
		String function_text = getCodeString(methodName, dependancies, helper_names, true);
		
		// The functions that have already been added.
		HashSet<String> names_done = new HashSet<String>();
		names_done.add(methodName);
		text.append(function_text);
		
		// FIXME : Helper functions should not have input comments.
		
		while(true)
		{
			boolean done = true;
			
			Object[] names = dependancies.toArray();
			
			for(Object o : names)
			{
				String str = (String)o;
				
				if(!names_done.contains(str))
				{
					function_text = getCodeString(str, dependancies, helper_names, false);
					text.append(function_text);
					names_done.add(str);
					done = false;
				}
			}
			
			if(done)
			{
				break;
			}
		}
		
		
		editor.insertText(text.toString());

		/*
		sinc
		linetopoint
		computeFilletParameters
		IsPerpendicular
		calcCircleFrom3Points
		slopeFromT
		xFromT
		yFromT
		B0
		B1
		B2
		B3
		findx
		findy
		*/
		
	}

	// Appends a proper usage comment to the given string builder.
	private void comment(StringBuilder output, String function_name)
	{
		// Append a line with the arguments.
		
		int num_args = getCurrentFunctionNArgs();
		
		boolean hasFinalIntArg = doesCurrentFunctionHaveFinalIntegerArgument(); 
		
		// Detects whether we should add a comment string with the arguments.
		boolean hasArgs = num_args > 0 || hasFinalIntArg;
		
		// Don't count the final n value as a regular argument.
		if(hasFinalIntArg)
		{
			num_args--;
		}
		
		if(hasArgs)
		{
			output.append("//" + function_name + "(");
		}
		
		output.append("input");
		
		if(num_args >= 2)
		{
			output.append(", " + param_a);	
		}
		
		if(num_args >= 3)
		{
			output.append(", " + param_b);	
		}
		
		if(num_args >= 4)
		{
			output.append(", " + param_c);	
		}
		
		if(num_args >= 5)
		{
			output.append(", " + param_d);	
		}
		
		if(hasFinalIntArg)
		{
			output.append(", " + param_n);
		}
		
		if(hasArgs)
		{
			// End of arguments.
			output.append(");\n");
		}
	}
	
	// returns a string representing the code for the given function name;
	String getCodeString(String function_name, HashSet<String> dependancies, HashSet<String> helper_names,
				boolean initial_function)
	{
		
		StringBuilder output = new StringBuilder();
		
		output.append("\n\n");
		
		if(initial_function)
		{
			comment(output, function_name);
			
			
			
		}
				
		
		File file = loadFile("code.txt");
		
		ArrayList<String> all_code = readFile(file);
		
		boolean found_string = false;
		int leftP = 0;
		
		// Process every line of the function.
		for(String str : all_code)
		{			
			if(!found_string && str.contains(function_name))
			{
				// Eliminate false positives.
				if(!str.contains("=") && !str.contains("\""))
				{
					found_string = true;
				}
			}
			
			if(!found_string)
			{
				continue;
			}
			
			if(str.contains("functionName"))
			{
				continue;
			}
			
			for(String helper_name : helper_names)
			{
				if(!dependancies.contains(helper_name) &&
				   str.contains(helper_name))
				{
					dependancies.add(helper_name);
				}
			}

			// Remove all Tabs.
			str = str.replace("\t", "");

			// -- Handle mutated and literal translation of code.
			
			String[] flipY_split = str.split("return");
			
			// Flip Outputs (Flip Y).
			if(initial_function && (bool_flipY || bool_clamp) && flipY_split.length >= 2)
			{				

				flipY_split[1] = flipY_split[1].replace(";", "");
				str = flipY_split[1];
								
				// Perform the transformations.
				if(bool_flipY)
				{
					str =  "1 -(" + str + ")";
				}

				if(bool_clamp)
				{
					str = "constrain(" + str +", 0, 1)";
				}
				
				// Reconstruct a well formed return statement.
				String prefix = flipY_split[0];// Whitespace mostly.
				output.append(prefix + "return " + str + ";\n");
				
			}
			// Flip Input (Flip X)
			else if(initial_function && bool_flipX && leftP == 0)
			{
				// Append the Function call.
				output.append(str);
				// New Line characters.
				output.append("\n");
				
				// Chop off the left.
				String parameter_name = str.split("\\(")[1];
				
				// Chop off the right.
				parameter_name = parameter_name.split(",|\\)")[0];
				
				parameter_name = parameter_name.replace("float", "");
				parameter_name = parameter_name.replace(" ", "");
				
				// Append the input flipping code.
				output.append("  " + parameter_name + " = " + "1 - " + parameter_name + ";\n");
			}
			else // Append the unaltered code string.
			{
				output.append(str);
				// New Line characters.
				output.append("\n");
			}
			
			// Count them parenthesis and keep a positive attitude.
			leftP += str.length() - str.replace("{", "").length();
			leftP -= str.length() - str.replace("}", "").length();
			
			if(leftP <= 0)
			{
				break;
			}
		}
		
		output.append("\n\n");
		return output.toString();
	}
	
	public static ArrayList<String> readFile(File file)
	{
		ArrayList<String> output = new ArrayList<String>();
		
	    BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e)
		{
			// FIXME : Consider throwing an exception that can be caught.
			e.printStackTrace();
		}
		
		try
		{
			String line = br.readLine();

			while (line != null)
			{
				output.add(line);
				line = br.readLine();
			}
			br.close();
		}
	    catch (IOException e){e.printStackTrace();}

		return output;	
	}
	
	/**
	 * getPath will return the path to a file or folder inside the data folder
	 * of the tool or an absolute path.
	 * 
	 * @param theFilename
	 * @return
	 */
	public String getPath(String theFilename) {
		if (theFilename.startsWith("/")) {
			return theFilename;
		}
		return File.separator + "data" + File.separator + theFilename;
	}


	/**
	 * load a file from the data folder or an absolute path.
	 * 
	 * @param theFilename
	 * @return
	 */
	public File loadFile(String theFilename) {

		if (theFilename.startsWith(File.separator)) {
			return new File(theFilename);
		}
		
		String rel_path = getPath(theFilename);
		
		String filePath = new File("").getAbsolutePath();
		String absolutePath = filePath + rel_path;
	
		return new File(absolutePath);

	}
	

	//-----------------------------------------------------
	public void setup() {
	  int scrW = (int)(margin0 + bandTh + margin1 + xscale + margin0);
	  int scrH = (int)(margin0 + bandTh + margin1 + yscale + margin2 + bandTh + margin0 + bandTh + margin0 + bandTh + margin1);
	  //size (scrW, scrH);
	  size(1200, 600);
	  
	  initHistories();
	  
	  introspect();
	  
	  selector = new gui_functionSelection(this);
	  
	  setup_buttons();
	  
	  setup_scrollbars();  
	  setupGroups();

	}
	
	public void setup_buttons()
	{
		UIConstants.init(this);
	}
	
	public void setup_scrollbars()
	{
		scroll = new VScrollbar(scroll_x, 0, 32, 600, 4);
		scroll.setVal(0);
		
		int x = (int) xoffset + 100;
		int y = (int) (yoffset + 320);
		int width  = 200;
		int height = 10;
		int l = 4;
		
		int y_inc = 15;
		
		parameter_scroll_a = new HScrollbar(x, y, width, height, l);
		y += y_inc;
		
		parameter_scroll_b = new HScrollbar(x, y, width, height, l);
		y += y_inc;
		
		parameter_scroll_c = new HScrollbar(x, y, width, height, l);
		y += y_inc;
		
		parameter_scroll_d = new HScrollbar(x, y, width, height, l);
		y += y_inc;
		
		parameter_scroll_n = new HScrollbar(x, y, width, height, l);
		y += y_inc;
		
		
	}
	
	// Orders Methods based on thier name strings.
	class MethodComparator implements Comparator<Method> {
	    @Override
	    public int compare(Method m1, Method m2) {
	    	String name1 = m1.getName().toLowerCase();
	    	String name2 = m2.getName().toLowerCase();
	    	
	        return name1.compareTo(name2);
	    }
	}
	
	public void setupGroups()
	{
		  function_groups = new f_group[12];
		  
		  for(int i = 0; i < function_groups.length; i++)
		  {
			  function_groups[i] = new f_group(selection_text_x - 100, i*50, 90, 40, i);
		  }
		  
		  function_groups[0].name("All");
		  function_groups[1].name("Sigmoid");
		  function_groups[2].name("Ogee");
		  function_groups[3].name("EaseIn");
		  function_groups[4].name("EaseOut");
		  function_groups[5].name("Penner");
		  function_groups[6].name("Gaussian");
		  function_groups[7].name("Bezier");		  
		  function_groups[8].name("Staircase");
		  function_groups[9].name("Windows");
		  function_groups[10].name("Half");
		  function_groups[11].name("Other");

		  // Sort the functions by their name strings.
		  // It is quite mysterious how the list of methods needs to be sorted in multiple locations.
		  // This could possible be investigated...
		  Collections.sort(functionMethodArraylist, new MethodComparator());
		  		  
		  int len = functionMethodArraylist.size();
		  for(int i = 0; i < len; i++)
		  {
			  Method m = functionMethodArraylist.get(i);
			  
			  String name = m.getName().toLowerCase();
			  
			  // All.
			  function_groups[0].addIndice(i);
			  
			  boolean found = false;
			  
			  if(name.contains("sigmoid"))
			  {
				  function_groups[1].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("ogee"))
			  {
				  function_groups[2].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("ease") && name.contains("in"))
			  {
				  System.out.println(name);
				  function_groups[3].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("ease") && name.contains("out"))
			  {
				  function_groups[4].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("penner"))
			  {
				  function_groups[5].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("gaussian"))
			  {
				  function_groups[6].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("bezier"))
			  {
				  function_groups[7].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("staircase"))
			  {
				  function_groups[8].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("window"))
			  {
				  function_groups[9].addIndice(i);
				  found = true;
			  }
			  
			  if(name.contains("half"))
			  {
				  function_groups[10].addIndice(i);
				  found = true;
			  }
			  
			  			  
			  if(!found)
			  {
				  function_groups[11].addIndice(i);
			  }			  
			  
			  
		  }
		  
		  
		  // Setup the global variables correctly.
		  fixFUNCTIONMODE();
	}
	
	public void fixFUNCTIONMODE()
	{
		// Synchronize the globlal index with the correct index.
		function_groups[group_index].index_fix();		
	}

	//-----------------------------------------------------
	public void mouseMoved() {
	  visited = true;
	}

	
	public void mouseWheel(MouseEvent event)
	{
		  float e = event.getCount();
		  
		  // In Selection Bounds.
		  if(inFunctionSelectionBounds())
		  {
			
			int up = ceil(e);
			  
			while(up > 0)
			{
				function_groups[group_index].index_increment();
				up--;
			}
			
			int down = ceil(-e);
			
			while(down > 0)
			{
				function_groups[group_index].index_decrement();
				down--;
			}
			return;
		  }
		  
		  f_n -= e;
		  f_n = constrain(f_n, 1, 10);
		  param_n = (int)f_n;
		  parameter_scroll_n.setVal((param_n -1)/9.0f);

	}
	
	
	int whichButton = 0; 
	public void mousePressed() {
		
		UIConstants.mousePressed();
		
		/*
		for(f_group g : function_groups)
		{
			if(g.mouseIn())
			g.mouseP();
		}
		*/

		/*
		if(mouseInCircle(comment_x, comment_y, comment_radius))
		{
			bool_comment = !bool_comment;
			return;
		}
		
		if(mouseInCircle(newTab_x, newTab_y, newTab_radius))
		{
			bool_newTab = !bool_newTab;
			return;
		}
		
		if(mouseInCircle(clamp_x, clamp_y, clamp_radius))
		{
			bool_clamp = !bool_clamp;
		}
		
		if(mouseInCircle(go_x, go_y, drawButton_radius))
		{
			export();
		}
		
		if(mouseInCircle(flipX_x, flipX_y, flipX_radius))
		{
			bool_flipX = !bool_flipX;
		}
		
		if(mouseInCircle(flipY_x, flipY_y, flipY_radius))
		{
			bool_flipY = !bool_flipY;
		}
		
		drawButton(newTab_x, newTab_y, newTab_radius, getBooleanString(bool_newTab));
		text("Use New Tab?", newTab_x + newTab_radius*2, newTab_y);
		drawButton(go_x, go_y, drawButton_radius, "Go!");
		*/
		
		// Function Selection Bounds checking mouse pressing code.
		if(inFunctionSelectionBounds())
		{
			f_group group = function_groups[group_index];
			FUNCTIONMODE = constrain((mouseY + selection_line_h/2 - getSelectionYStart()) / selection_line_h,
										0, group.size() - 1);
			// FUNCTIONMODE = 
			FUNCTIONMODE = group.get(FUNCTIONMODE);
			
			return;
		}
			
		
		
	  bClickedInGraph = false;
	  if ((mouseX >= xoffset) && (mouseX <= (xoffset + xscale)) && 
	    (mouseY >= yoffset) && (mouseY <= (yoffset + yscale))) {

	    if (mouseButton == LEFT) {
	      whichButton = 1;
	    } 
	    else if (mouseButton == RIGHT) {
	      whichButton = 2;
	    } 
	    else {
	      whichButton = 0;
	    }

	    bClickedInGraph = true;
	  }
	}

	public void mouseReleased() {
	  whichButton = 0;
	  
	}

// Determines the x region of the screen that cooresponds to the list of functions.
// This function will be used to determine if mouse presses fall in that region.
boolean inFunctionSelectionBounds()
{
	return selection_text_x <= mouseX && mouseX <= scroll_x - 30;
}

	//====================================================
	void drawPDF() {


	  String pdfFilename = functionName;
	  /*
	  if (useParameterA) { 
	   pdfFilename += "_a=" + nf(param_a, 1, 2);
	   }
	   if (useParameterB) { 
	   pdfFilename += "_b=" + nf(param_b, 1, 2);
	   }
	   if (useParameterC) { 
	   pdfFilename += "_c=" + nf(param_c, 1, 2);
	   }
	   if (useParameterD) { 
	   pdfFilename += "_d=" + nf(param_d, 1, 2);
	   }
	   if (useParameterN) { 
	   pdfFilename += "_n=" + nf(param_n, 1, 2);
	   }
	   */
	  pdfFilename += ".pdf";


	  beginRecord(PDF, pdfFilename); 

	  strokeJoin(MITER);
	  strokeCap(ROUND);
	  strokeWeight(1.0f);
	  noFill();
	  stroke(0);
	  rect(0, 0, width, height);

	  background (255, 255, 255);
	  stroke(128);
	  fill(255);
	  rect(xoffset, yoffset, xscale, yscale);
	  drawModeSpecificGraphics();

	  // draw the function's curve
	  float x = 0;
	  float y = 1 - function (x, param_a, param_b, param_c, param_d, param_n);
	  float qx = xoffset + xscale * x;
	  float qy = yoffset + yscale * y;
	  float px = qx;
	  float  py = qy;
	  stroke(0);
	  noFill();
	  beginShape();
	  vertex(px, py);
	  for (float i=0; i<=xscale; i+=0.1) {
	    x = (float)i/xscale;
	    y = 1 - function (x, param_a, param_b, param_c, param_d, param_n);
	    px = xoffset + (xscale * x);
	    py = yoffset + (yscale * y);
	    //line (qx, qy, px, py);
	    vertex(px, py);
	    qx = px;
	    qy = py;
	  }
	  endShape();

	  //---------------------------
	  // draw the function's gray levels
	  py = yoffset-(bandTh+margin1);
	  qy = yoffset-margin1;
	  beginShape(QUAD_STRIP);
	  for (float j=0; j<=xscale; j++) {
	    float j1 = j; 
	    float x1 = j1/(float)xscale;
	    float y1 = function (1-x1, param_a, param_b, param_c, param_d, param_n);
	    float g1 = 255.0f * y1;
	    float px1 = xoffset + xscale - j1;

	    noStroke();
	    fill(g1, g1, g1);
	    vertex(px1, py); 
	    vertex(px1, qy);
	  }
	  endShape();
	  noFill();
	  stroke(128);
	  rect(xoffset, yoffset-(bandTh+margin1), xscale, bandTh);

	  endRecord();
	  strokeWeight (1); 
	  doSavePDF=false;
	}



	//-----------------------------------------------------
	public void draw() {


		mainDraw();

		selectDraw();
		
		//drawExports();
		
		UIConstants.draw();
	}
	
	public void drawExports()
	{
		
		drawButton(flipX_x, flipX_y, flipX_radius, getBooleanString(bool_flipX));
		text("Flip X?", flipX_x + flipX_radius*2, flipX_y);
		
		drawButton(flipY_x, flipY_y, flipY_radius, getBooleanString(bool_flipY));
		text("Flip Y?", flipY_x + flipY_radius*2, flipY_y);
		
		drawButton(comment_x, comment_y, comment_radius, getBooleanString(bool_comment));
		text("Comment Only?", comment_x + comment_radius*2, comment_y);
		
		drawButton(clamp_x, clamp_y, clamp_radius, getBooleanString(bool_clamp));
		text("Constrain Output?", clamp_x + clamp_radius*2, clamp_y);
		
		drawButton(newTab_x, newTab_y, newTab_radius, getBooleanString(bool_newTab));
		text("Use New Tab?", newTab_x + newTab_radius*2, newTab_y);
		drawButton(go_x, go_y, drawButton_radius, "Go!");
	}
	
	public String getBooleanString(boolean val)
	{
		return val ? "Yes" : "No";
	}
	
	public void drawButton(int x, int y, int radius, String message)
	{
		radius = radius*2;
		fill(0);
		ellipse(x, y, radius, radius);
		if(mouseInCircle(x, y, radius/2))
		{
			fill(255, 0, 00);
		}
		else
		{
			fill(0, 255, 00);						
		}
		ellipse(x, y, radius-4, radius-4);
		
		
		textAlign(CENTER, CENTER);
		
		fill(0);
		text(message, x, y);
		textAlign(LEFT, UP);
		
		

	}
	
	public boolean mouseInCircle(int x, int y, int radius)
	{
		int dx = x - mouseX;
		int dy = y - mouseY;
		return dx*dx + dy*dy < radius*radius;
	}
	
	public void selectDraw()
	{
		/*
		for(f_group g : function_groups)
		{
			g.draw();
		}
		*/
		
		if(selector == null)
		{
			return;
		}
		
		fill(0);
			
			
		int y = getSelectionYStart();

		f_group group = function_groups[group_index];
		
		for(Integer index : group)
		{
			gui_functionSelection.listButton b = selector.button_list.myListButtons.get(index);
			
			if(index == FUNCTIONMODE)
			{
				textFont(Font_bold);
			}
			else
			{
				textFont(Font_normal);
			}

		  	//textSize(32);
		   	text(b.function_name + "()", selection_text_x, y);
		   	y += selection_line_h;
		   	//println("Printing text on screen : " + b.function_name);
		   	
		   	index++;
		}		
		
		scroll.update();
		scroll.display();
		
		
		int nCurrentFunctionArgs = getCurrentFunctionNArgs() - 1; // we subtract 1, for x itself
		boolean bHasFinalIntArg = doesCurrentFunctionHaveFinalIntegerArgument(); 
		
		parameter_scroll_a.active = (nCurrentFunctionArgs > 0); 
		parameter_scroll_b.active = (nCurrentFunctionArgs > 1);
		parameter_scroll_c.active = (nCurrentFunctionArgs > 2);
		parameter_scroll_d.active = (nCurrentFunctionArgs > 3);
		parameter_scroll_n.active = (bHasFinalIntArg);
		
		handleHScrollbar(parameter_scroll_a);
		param_a = parameter_scroll_a.getVal(1.0f);
		handleHScrollbar(parameter_scroll_b);
		param_b = parameter_scroll_b.getVal(1.0f);
		handleHScrollbar(parameter_scroll_c);
		param_c = parameter_scroll_c.getVal(1.0f);
		handleHScrollbar(parameter_scroll_d);
		param_d = parameter_scroll_d.getVal(1.0f);
		handleHScrollbar(parameter_scroll_n);
		param_n = (int)(parameter_scroll_n.getVal(10.0f) + 1);
		
		textFont(Font_normal);
				
	}
	
	void handleHScrollbar(HScrollbar H)
	{
		H.update();
		H.display();
	}
	
	
	public int getSelectionYStart()
	{
		double spos = Math.max(0, scroll.getPos());
		int val = (int)(spos*selection_line_h*(function_groups[group_index].size() - 12));
				return 20 - val;
	}
	
	public void mainDraw()
	{
	  updateParameters(); 

	  if (doSavePDF) {
	    drawPDF();
	    doSavePDF = false;
	  }  
	  else {

	    background (255);

	    //---------------------------
	    // Draw the animating probe
	    if (bDrawProbe) {
	      drawAnimatingProbe();
	    }
	    //---------------------------
	    // Draw the animating circle 
	    if (bDrawAnimatingRadiusCircle) {
	      drawAnimatingRadiusCircle();
	    }
	    //---------------------------
	    // Extra mode-specific graphics for Bezier, etc.
	    if (bDrawModeSpecificGraphics) {
	      drawModeSpecificGraphics();
	    }
	    //---------------------------
	    // Draw the function's curve
	    drawMainFunctionCurve();

	    //---------------------------
	    // Draw the function's gray levels
	    if (bDrawGrayScale) {
	      drawGrayLevels();
	    }
	    //---------------------------
	    // Draw a noise signal, and a filtered version.
	    if (bDrawNoiseHistories) {
	      drawNoiseHistories();
	    }
	    //---------------------------
	    // Draw labels
	    drawLabels();
	  }
	}

	//-----------------------------------------------------
	void updateParameters() {

	  float acf = animationConstant;
	  probe_x = abs(millis()%(2*(int)acf) - acf)/acf;

	  if (mousePressed && bClickedInGraph) {
	    if (visited) {
	      if (whichButton == 1) {
	        // Use the left mouse button for parameters a & b
	        param_a =   (float)(mouseX - xoffset)/xscale;
	        param_b = 1-(float)(mouseY - yoffset)/yscale;
	        param_a = constrain(param_a, 0, 1); 
	        param_b = constrain(param_b, 0, 1);
	        
	        parameter_scroll_a.setVal(param_a);
	        parameter_scroll_b.setVal(param_b);
	      } 
	      else if (whichButton == 2) {
	        // Use the left mouse button for parameters c & d
	        param_c =   (float)(mouseX - xoffset)/xscale;
	        param_d = 1-(float)(mouseY - yoffset)/yscale;
	        param_c = constrain(param_c, 0, 1); 
	        param_d = constrain(param_d, 0, 1);
	        
	        parameter_scroll_c.setVal(param_c);
	        parameter_scroll_d.setVal(param_d);
	      }
	    }
	  }
	}

	//-----------------------------------------------------
	void drawMainFunctionCurve() {
	  float x, y;
	  float px, py;
	  float qx, qy;

	  noFill(); 
	  stroke(boundingBoxStrokeColor);
	  rect(xoffset, yoffset, xscale, yscale);

	  x = 0;
	  y = 1 - function (x, param_a, param_b, param_c, param_d, param_n);
	  qx = xoffset + xscale * x;
	  qy = yoffset + yscale * y;
	  px = qx;
	  py = qy;

	  for (int i=0; i<=xscale; i++) {
	    x = (float)i/xscale;
	    y = 1 - function (x, param_a, param_b, param_c, param_d, param_n);

	    stroke(0);
	    if ((y < 0) || (y > 1)) {
	      stroke(boundingBoxStrokeColor);
	    } 

	    px = xoffset + round(xscale * x);
	    py = yoffset + round(yscale * y);
	    line (qx, qy, px, py);
	    qx = px;
	    qy = py;
	  }
	}


	//-----------------------------------------------------
	void drawAnimatingProbe() {

	  // inspired by @marcinignac & @soulwire 
	  // from http://codepen.io/vorg/full/Aqyre 

	  float x = constrain (probe_x, 0, 1);
	  float y = probe_y = 1 - function (x, param_a, param_b, param_c, param_d, param_n);
	  float px = xoffset + round(xscale * x);
	  float py = yoffset + round(yscale * y);
	  float qy = yoffset + yscale;

	  // draw bounding box
	  noFill();
	  stroke(boundingBoxStrokeColor);
	  rect(margin0 + xoffset2, yoffset, bandTh, yscale);

	  // draw probe element
	  stroke(255, 128, 128);
	  line (px, qy, px, py);
	  stroke(128, 128, 255);
	  line (px, py, xoffset, py);
	  fill(0);
	  noStroke();
	  ellipseMode (CENTER);
	  ellipse(margin0+bandTh/2.0f + xoffset2, py, 11, 11);
	}


	//-----------------------------------------------------
	void drawAnimatingRadiusCircle() {
	  // Draw a circle whose radius is linked to the function value. 
	  // Inspired by @marcinignac & @soulwire: http://codepen.io/vorg/full/Aqyre   

	  float blooperCx = margin0+bandTh/2.0f + xoffset2;
	  float blooperCy = margin0+bandTh/2.0f + yoffset - 75;
	  float val = function (probe_x, param_a, param_b, param_c, param_d, param_n);
	  float blooperR = bandTh * val;

	  smooth(); 
	  float grayBg = map(val, 0, 1, 220, 255);
	  fill (grayBg); 
	  ellipse (blooperCx, blooperCy, bandTh, bandTh);

	  noStroke();
	  fill (160);
	  float grayFg = map(val, 0, 1, 127, 160);
	  fill (grayFg);
	  ellipse (blooperCx, blooperCy, blooperR, blooperR);
	}

	//-----------------------------------------------------
	void drawGrayLevels() {

	  smooth();
	  for (int j=0; j<=xscale; j++) {
	    float x = (float)j / (float)xscale;
	    float y = function (1.0f-x, param_a, param_b, param_c, param_d, param_n);
	    float g = 255.0f * y;

	    float py = yoffset-(bandTh+margin1);
	    float qy = yoffset-margin1;
	    float px = xoffset + xscale - j;

	    stroke(g, g, g);
	    line (px, py, px, qy);
	  }

	  // draw the bounding box
	  noFill();
	  stroke(boundingBoxStrokeColor);
	  rect(xoffset, yoffset-(bandTh+margin1), xscale, bandTh);
	}


	//===========================================================
	class DataHistoryGraph {
	  float rawHistory[];
	  int nData;

	  //-------------------------
	  DataHistoryGraph (int len) {
	    nData = len;
	    rawHistory      = new float[nData];
	    for (int i=0; i<nData; i++) {
	      rawHistory[i] = 0.5f;
	    }
	  }

	  //-------------------------
	  void update (float newVal) {
	    // update noise history
	    for (int i=0; i<(nData-1); i++) {
	      rawHistory[i] = rawHistory[i+1];
	    }
	    rawHistory[nData-1] = newVal;
	  }

	  //-------------------------
	  void draw (float xoffset, float nhy) {

	    // draw bounding rectangles
	    noFill(); 
	    stroke(boundingBoxStrokeColor);
	    rect (xoffset, nhy, xscale, bandTh);

	    // draw raw noise history
	    noFill(); 
	    stroke(180); 
	    beginShape(); 
	    for (int i=0; i<nData; i++) {
	      float x = xoffset + i;
	      float valRaw = 1.0f - constrain(rawHistory[i], 0, 1);
	      float y = nhy + bandTh * valRaw;
	      vertex(x, y);
	    }
	    endShape(); 

	    // draw filtered noise history
	    noFill(); 
	    stroke(0); 
	    
	    float x2 = xoffset + 0;
	    float y2 = getYFiltered(0, nhy);
	    
	    for (int i=0; i<nData; i++) {
	      float x = xoffset + i;
	      float y = getYFiltered(i, nhy);
	      
	         
		  if ((y  < 0) || (y > 1) ||
			  (y2 < 0) || (y2 > 1))
		  {
			  stroke(boundingBoxStrokeColor);
		  }
		  else
		  {
			  stroke(0);
		  }
		  
		  float screen_y1 = nhy + bandTh * y;
		  float screen_y2 = nhy + bandTh * y2;
	      
	      line(x,  screen_y1,
	    	   x2, screen_y2);
	      
	      x2 = x;
	      y2 = y;
	    }
	    
	  }
	  
	  float getYFiltered(int i, float nhy)
	  {
		  float valRaw = rawHistory[i];
	      float valFiltered = 1.0f - function (valRaw, param_a, param_b, param_c, param_d, param_n);
	      
	      if(bool_clamp)
		      valFiltered = constrain(valFiltered, 0, 1);
	      
	      
	      return valFiltered;
	  }
	  
	}


	//-----------------------------------------------------
	void initHistories() {

	  //noiseHistory = new DataHistoryGraph ((int)xscale);
	  cosHistory   = new DataHistoryGraph ((int)xscale);
	  //triHistory   = new DataHistoryGraph ((int)xscale);
	}


	//-----------------------------------------------------
	void drawNoiseHistories() {

	  // update with the latest incoming values
	  //int nData = (int)xscale;
	  //float noiseVal = noise(millis()/ (nData/2.0f));
	  float cosVal   = 0.5f + (0.5f * cos(PI * millis()/animationConstant));
	  
	  float ac = animationConstant;
	  
	  //float tv = (((int)(millis()/ac))%2 == 0) ? (millis()%ac) : (ac - (millis()%ac));
	  //float triVal = 1.0f - tv/ac;
	  
	  
	  //noiseHistory.update( noiseVal ); 
	  cosHistory.update  ( cosVal );  
	  //triHistory.update  ( triVal ); 

	  float nhy = margin0 + bandTh + margin1 + yscale + margin2 + yoffset - 70;
	  
	  /*
	  noiseHistory.draw ( xoffset, nhy); 
	  nhy += (bandTh + margin1);
	  */ 
	  cosHistory.draw   ( xoffset, nhy);
	  nhy += (bandTh + margin1); 
	  //triHistory.draw   ( xoffset, nhy);
	}


	float code_button_x = 0.0f;
	float code_button_y = 0.0f;
	float code_button_w = 0.0f;
	float code_button_h = 0.0f;

	//-----------------------
	void drawLabels() {

	  int nCurrentFunctionArgs = getCurrentFunctionNArgs() - 1; // we subtract 1, for x itself
	  boolean bHasFinalIntArg = doesCurrentFunctionHaveFinalIntegerArgument(); 

	  float grayEnable = 64;
	  float grayDisable = 192;
	  float textLineHeight = 13; 
	  float yBase = 15; 

	  float params[] = {
	    param_a, param_b, param_c, param_d
	  }; 

	  //------------------
	  fill(grayEnable);
	  textSize(16);
	  textAlign(CENTER, CENTER);
	  text(functionName, xoffset + 150, 40);//yoffset+yscale+yBase);
	  textAlign(LEFT);
	 // textSize(9);
	  int lastArgIndex = (bHasFinalIntArg) ? (nCurrentFunctionArgs-1) : nCurrentFunctionArgs; 

	  float yPos; 
	  for (int i=0; i<MAX_N_float_PARAMS; i++) {
	    char argName = (char)('a'+i);
	    yPos = yoffset+yscale+ yBase+((i+1)*textLineHeight);

	    if (i<lastArgIndex) {
	      fill (grayEnable);
	      text(argName + ": " + nf(params[i], 1, 3), xoffset, yPos);
	    } 
	    else {
	      fill (grayDisable);
	      text(argName + ": -----", xoffset, yPos);
	    }
	  }

	  //------------------
	  yPos = yoffset+yscale+ yBase + ((MAX_N_float_PARAMS+1)*textLineHeight);
	  if (bHasFinalIntArg) {
	    fill (grayEnable);
	    text("n: " + param_n, xoffset, yPos);
	  } 
	  else {
	    fill (grayDisable);
	    text("n: -----", xoffset, yPos);
	  }
	}

	//-----------------------------------------------------
	void drawModeSpecificGraphics() {

	  int whichFunction = FUNCTIONMODE%nFunctionMethods;  
	  Method whichMethod = functionMethodArraylist.get(whichFunction); 
	  String methodName = whichMethod.getName(); 

	  Type[] params = whichMethod.getGenericParameterTypes();
	  int nParams = params.length;

	  // determine if the current function has an integer argument.
	  String lastParamString = params[nParams-1].toString();
	  boolean bHasIntegerArgument = (lastParamString.equals("int"));

	  float x,  y;
	  float xa, yb;
	  float xc, yd;
	  float K = 12;
	  float cr = 7;

	  noFill();
	  stroke(180, 180, 255);

	  switch (nParams) {
	  case 2:
	    if ( methodName.equals("function_AdjustableFwhmHalfGaussian") ||
	        methodName.equals("function_AdjustableSigmaHalfGaussian")) {
	      x = xoffset + param_a * xscale;
	      float val = 1.0f - function (param_a, param_a, param_b, param_c, param_d, param_n);
	      y = yoffset + yscale * val; 
	      line (x, yoffset+yscale, x, y); 
	      line (xoffset, y, x, y);
	    }
	    break;

	  case 3:
	    if (bHasIntegerArgument == false) {
	      // through a point
	      x = xoffset + param_a * xscale;
	      y = yoffset + (1-param_b) * yscale;

	      if (methodName.equals("function_QuadraticBezier")) {
	        line (xoffset, yoffset + yscale, x, y);
	        line (xoffset + xscale, yoffset, x, y);
	      }

	      line(x-K, y, x+K, y); 
	      line(x, y-K, x, y+K);
	      fill (255, 255, 255); 
	      ellipse(x, y, cr, cr);
	    } 
	    else {
	      
	    }
	    break;

	  case 4:
	    if (methodName.equals("function_CircularFillet")) {
	    	
	    	// FIx this in time;
	    	if(true)
	    	{
	    		break;
	    	}
	    	
	    /*
	      x = xoffset + arcCenterX * xscale;
	      y = yoffset + (1-arcCenterY) * yscale;
	      float d = 2.0 * arcRadius * xscale;
	     	    	
	      ellipseMode(CENTER);
	      ellipse(x, y, d, d);
	      */

	      x = xoffset + param_a * xscale;
	      y = yoffset + (1-param_b) * yscale;
	      line(x-K, y, x+K, y); 
	      line(x, y-K, x, y+K);
	      fill (255, 255, 255); 
	      ellipse(x, y, cr, cr);
	    } 
	    else {
	      x = xoffset + param_a * xscale;
	      y = yoffset + (1-param_b) * yscale;
	      line(x-K, y, x+K, y); 
	      line(x, y-K, x, y+K);
	      fill (255, 255, 255); 
	      ellipse(x, y, cr, cr);
	    }
	    break;

	  case 5: // (including x itself)
	    if (bHasIntegerArgument == false) {
	      // two crosses
	      xa = xoffset + param_a * xscale;
	      yb = yoffset + (1-param_b) * yscale;
	      xc = xoffset + param_c * xscale;
	      yd = yoffset + (1-param_d) * yscale;
	      line(xa-K, yb, xa+K, yb); 
	      line(xa, yb-K, xa, yb+K); 
	      line(xc-K, yd, xc+K, yd); 
	      line(xc, yd-K, xc, yd+K);

	      if ((methodName.equals("function_CubicBezier")) || 
	          (methodName.equals("function_floatQuadraticBezier"))) {
	        line (xoffset, yoffset + yscale, xa, yb);
	        line (xc, yd, xa, yb);
	        line (xoffset + xscale, yoffset, xc, yd);
	      }
	      

	      fill (255, 255, 255); 
	      ellipse(xa, yb, cr, cr); 
	      ellipse(xc, yd, cr, cr);
	    }
	    break;
	  }

	}


	//===============================================================
	int getCurrentFunctionNArgs() {
	  int whichFunction = FUNCTIONMODE % nFunctionMethods;  
	  Method whichMethod = functionMethodArraylist.get(whichFunction); 
	  Type[] params = whichMethod.getGenericParameterTypes();
	  int nParams = params.length;
	  return nParams;
	}

	//===============================================================
	boolean doesCurrentFunctionHaveFinalIntegerArgument() {
	  // determine if the current function has a (final) integer argument.

	  int whichFunction = FUNCTIONMODE % nFunctionMethods;  
	  Method whichMethod = functionMethodArraylist.get(whichFunction); 
	  Type[] params = whichMethod.getGenericParameterTypes();
	  int nParams = params.length;

	  boolean bHasFinalIntegerArgument = false;
	  for (int p=0; p<nParams; p++) {
	    String paramString = params[p].toString();
	    if (paramString.equals("int")) {
	      bHasFinalIntegerArgument = true;
	    }
	  }
	  return bHasFinalIntegerArgument;
	}

	//===============================================================
	float function (float x, float a, float b, float c, float d, int n) {

		// FLip X.
		if(bool_flipX)
		{
			x = 1.0f - x;
		}
		
	  float out = 0; 
	  nFunctionMethods = functionMethodArraylist.size(); 
	  if (nFunctionMethods > 0) {
	    int whichFunction = FUNCTIONMODE%nFunctionMethods;  
	    Method whichMethod = functionMethodArraylist.get(whichFunction); 

	    int nParams = getCurrentFunctionNArgs(); 
	    boolean bHasFinalIntegerArgument = doesCurrentFunctionHaveFinalIntegerArgument();

	    // Invoke() the current shaping function, 
	    // with the correct number and type(s) of arguments. 
	    // Note: we don't have any 1-argument functions with an integer arg.
	    // Note: we don't have any 6-argument functions without an integer arg.
	    try {
	      float F = 0.0f;

	      if (bHasFinalIntegerArgument) {
	        switch(nParams) {
	        case 2: 
	          F = (Float) whichMethod.invoke(this, x, n);
	          break;

	        case 3: 
	          F = (Float) whichMethod.invoke(this, x, a, n);
	          break;

	        case 4: 
	          F = (Float) whichMethod.invoke(this, x, a, b, n);
	          break;

	        case 5: 
	          F = (Float) whichMethod.invoke(this, x, a, b, c, n);
	          break;

	        case 6: 
	          F = (Float) whichMethod.invoke(this, x, a, b, c, d, n);
	          break;
	        }
	      }

	      else if (bHasFinalIntegerArgument == false) {
	        switch(nParams) {
	        case 1: 
	          F = (Float) whichMethod.invoke(this, x); 
	          break;

	        case 2: 
	          F = (Float) whichMethod.invoke(this, x, a);
	          break;

	        case 3: 
	          F = (Float) whichMethod.invoke(this, x, a, b);
	          break;

	        case 4: 
	          F = (Float) whichMethod.invoke(this, x, a, b, c);
	          break;

	        case 5: 
	          F = (Float) whichMethod.invoke(this, x, a, b, c, d);
	          break;
	        }
	      }
	      out = F;//.floatValue();
	    } 

	    catch (Exception e) {
	      // Print out what went wrong.
	      println("Problem calling method: " + whichMethod.getName());
	      println(e +  ": " + e.getMessage() );
	      e.printStackTrace(); 
	      Throwable cause = e.getCause();
	      println (cause.getMessage());
	    }
	  }

	  if(bool_flipY)
	  {
			out = 1 - out;
	  }
	  
	  if(bool_clamp)
	  {
		  out = constrain(out, 0, 1);
	  }
	  
	  return out;
	}




	/////////////////////////////////////////////////////////////////////////
	//
	// Notes for introspection 
	// Documentation here: 
	// http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Object.html
	// http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/reflect/Method.html
	// http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Class.html
	//
	// Be sure to import the following: 
	// import java.lang.*;
	// import java.lang.reflect.Method;
	// import java.lang.reflect.Type;
	//
	// Other notes:
	// Method.invoke(..) // allows calling of a function!
	// String rts = m.getReturnType().toString(); // assumed to be float, for us.

	void introspect() {
	  // Examine the current class, extract the names of the functions,  
	  // then compile an ArrayList containing all the shaper functions. 
	  functionMethodArraylist = new ArrayList<Method>();
	  nFunctionMethods = 0; 

	  try {
	    // This fetches the class name for the current (PApplet) class, 
	    // which happens to contain all of the functions. For Processing, if the functions
	    // were instead inside an inner class (say, "FunctionManager", we would  
	    // add the following to the fullClassName: // + "$" + "FunctionManager";
	    String fullClassName = this.getClass().getName(); 
	    Class myClassName = Class.forName(fullClassName);

	    int funcCount = 0; 
	    Method[] methods = myClassName.getMethods();// Only works for Public classes.

	    if (methods.length>0) {
	      // count (specifically) the shaper functions.
	      // copy into local arraylist data structure
	      for (int i=0; i<methods.length; i++)
	      {
	    	  Method m = methods[i];
	    	  String methodName = m.getName();
	        
	    	  if (methodName.startsWith ("function_"))
	    	  { 
	    		  // println ('"' + methodName + '"'); 
	    		  funcCount++;
	    		  functionMethodArraylist.add(m);
	    	  }
	      }
	      
		  // Sort the functions by their name strings.
		  Collections.sort(functionMethodArraylist, new MethodComparator());
	      
	      
	      nFunctionMethods = functionMethodArraylist.size(); 
	      println("nFunctionMethods = " + nFunctionMethods);
	    }
	  }
	  catch (Exception e) {
	    println (e);
	  }
	}
	
	
	/*
	 *  ALL of the Functions in java class form. I am putting these in the one file.
	 */
	
	
	// Penner's Equations


	//------------------------------------------------------------------
	public float function_PennerEaseInBack (float x) {
	  functionName = "Penner's Ease-In Back";

	  float s = 1.70158f;
	  float y = x*x*((s+1.0f)*x - s);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_PennerEaseOutBack (float x) {
	  functionName = "Penner's Ease-Out Back";

	  float s = 1.70158f;
	  x = x-1.0f;
	  float y = (x*x*((s+1.0f)*x + s) + 1.0f);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInOutBack (float x) {
	  functionName = "Penner's EaseInOut Back";

	  float s = 1.70158f * 1.525f;
	  x /= 0.5;

	  float y = 0; 
	  if (x < 1) {
	    y = 1.0f/2.0f* (x*x*((s+1.0f)*x - s));
	  } 
	  else {
	    x -= 2.0f;
	    y = 1.0f/2.0f* (x*x*((s+1.0f)*x + s) + 2.0f);
	  } 
	  return y;
	}


	//------------------------------------------------------------------
	public float function_PennerEaseInQuadratic (float t) {
	  functionName = "Penner's EaseIn Quadratic";
	  return t*t;
	}
	//------------------------------------------------------------------
	public float function_PennerEaseOutQuadratic (float t) {
	  functionName = "Penner's EaseOut Quadratic";
	  return -1.0f *(t)*(t-2);
	}
	//------------------------------------------------------------------
	public float function_PennerEaseInOutQuadratic (float t) {
	  functionName = "Penner's EaseInOut Quadratic";
	  if ((t/=1.f/2) < 1) return 1.f/2*t*t;
	  return -1.f/2 * ((--t)*(t-2) - 1);
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInCubic (float x) {
	  functionName = "Penner's EaseIn Cubic";
	  return x*x*x;
	}

	//------------------------------------------------------------------
	public float function_PennerEaseOutCubic (float x) {
	  functionName = "Penner's EaseOut Cubic";
	  x = x-1.0f;
	  return (x*x*x + 1);
	}


	//------------------------------------------------------------------
	public float function_PennerEaseInOutCubic (float x) {
	  functionName = "Penner's EaseInOut Cubic";
	  
	  x *= 2.0; 
	  float y = 0; 

	  if (x < 1) {
	    y = 0.5f * x*x*x;
	  } 
	  else {
	    x -= 2.0;
	    y = 0.5f * (x*x*x + 2.0f);
	  }
	  return y;
	}


	//------------------------------------------------------------------
	public float function_PennerEaseInQuartic (float t) {
	  functionName = "Penner's EaseIn Quartic";
	  return t*t*t*t;
	}
	//------------------------------------------------------------------
	public float function_PennerEaseOutQuartic (float t) {
	  functionName = "Penner's EaseOut Quartic";
	  return -1.0f * ((t=t-1)*t*t*t - 1.0f);
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInOutQuartic (float t) {
	  functionName = "Penner's EaseInOut Quartic";

	  if ((t/=1.0f/2.0f) < 1) return 1.0f/2.0f*t*t*t*t;
	  return -1.0f/2.0f * ((t-=2.0f)*t*t*t - 2.0f);
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInQuintic (float t) {
	  functionName = "Penner's EaseIn Quintic";
	  return t*t*t*t*t;
	}
	//------------------------------------------------------------------
	public float function_PennerEaseOutQuintic (float t) {
	  functionName = "Penner's EaseOut Quintic";
	  t = t-1;
	  return (t*t*t*t*t + 1.0f);
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInOutQuintic (float t) {
	  functionName = "Penner's EaseInOut Quintic";
	  if ((t/=1.f/2) < 1) return 1.f/2*t*t*t*t*t;
	  return 1.f/2*((t-=2)*t*t*t*t + 2);
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInSine (float t) {
	  functionName = "Penner's EaseIn Sine";
	  return -1.0f * cos(t * (PI/2)) + 1;
	}

	//------------------------------------------------------------------
	public float function_PennerEaseOutSine(float t) {
	  functionName = "Penner's EaseOut Sine";
	  return sin(t * (PI/2));
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInOutSine(float t) {
	  functionName = "Penner's EaseInOut Sine";
	  return -0.5f * (cos(PI*t) - 1);
	}




	//------------------------------------------------------------------
	public float function_PennerEaseInExpo(float t) {
	  functionName = "Penner's EaseIn Exponential";
	  return (t==0) ? 0 : pow(2, 10 * (t - 1));
	}

	//------------------------------------------------------------------
	public float function_PennerEaseOutExpo(float t) {
	  functionName = "Penner's EaseOut Exponential";
	  return (t==1) ? 1 : (-pow(2, -10 * t) + 1);
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInOutExpo(float t) {
	  functionName = "Penner's EaseInOut Exponential";
	  if (t==0) return 0.0f;
	  if (t==1) return 1.0f;
	  if ((t/=1.f/2) < 1) return 1.0f/2 * pow(2, 10 * (t - 1));
	  return 1.f/2 * (-pow(2, -10 * --t) + 2);
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInElastic (float t) {
	  functionName = "Penner's EaseIn Elastic";

	  if (t==0) return 0.0f; 
	  if (t==1) return 1.0f;
	  float p=0.3f;

	  float s=p/4;
	  float postFix = pow(2, 10.0f * (t-=1)); // this is a fix, again, with post-increment operators
	  return -(postFix * sin((t-s)*(2*PI)/p ));
	}

	//------------------------------------------------------------------
	public float function_PennerEaseOutElastic(float t) {
	  functionName = "Penner's EaseOut Elastic";
	  
	  if (t==0) return 0.0f; 
	  if (t==1) return 1.0f;
	  float p = 0.3f;
	  float s = p/4;

	  return (pow(2, -10*t) * sin( (t-s)*(2*PI)/p ) + 1);
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInOutElastic (float t) {
	  functionName = "Penner's EaseInOut Elastic";

	  if (t==0) return 0; 
	  if ((t/=0.5)==2) return 1;
	  float p=(.3f*1.5f);
	  float a=1;
	  float s=p/4;

	  if (t < 1) {
	    float postFix = pow(2, 10*(t-=1)); // postIncrement is evil
	    return -0.5f*(postFix* sin( (t-s)*(2*PI)/p ));
	  } 
	  float postFix = pow(2, -10*(t-=1)); // postIncrement is evil
	  return postFix * sin( (t-s)*(2*PI)/p )*.5f + 1;
	}



	//------------------------------------------------------------------
	public float function_PennerEaseOutBounce (float t) {
	  functionName = "Penner's EaseOut Bounce";

	  if ((t) < (1/2.75f)) {
	    return (7.5625f* t*t);
	  } 
	  else if (t < (2/2.75f)) {
	    float postFix = t-=(1.5f/2.75f);
	    return (7.5625f*(postFix)*t + 0.75f);
	  } 
	  else if (t < (2.5/2.75)) {
	    float postFix = t-=(2.25f/2.75f);
	    return (7.5625f*(postFix)*t + 0.9375f);
	  } 
	  else {
	    float postFix = t-=(2.625f/2.75f);
	    return (7.5625f*(postFix)*t + 0.984375f);
	  }
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInBounce (float t) {
	  functionName = "Penner's EaseIn Bounce";
	  return (1.0f - function_PennerEaseOutBounce (1.0f-t));
	}

	//------------------------------------------------------------------
	public float function_PennerEaseInOutBounce(float t) {
	  functionName = "Penner's EaseInOut Bounce";
	  if (t < 0.5) {
	    return function_PennerEaseInBounce (t*2) * .5f;
	  } 
	  else {
	    return function_PennerEaseOutBounce (t*2-1) * .5f + .5f;
	  }
	}

	//------------------------------------------------------------------
	public float function_Staircase (float x, int n) {
	  functionName = "Staircase";
	  float y = floor(x*n) / (float)(n-1);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_ExponentialSmoothedStaircase (float x, float a, int n) {
	  functionName = "Smoothed Exponential Staircase";
	  // See http://web.mit.edu/fnl/volume/204/winston.html
	  
	  float fa = sq (map(a, 0,1, 5,30));
	  float y = 0; 
	  for (int i=0; i<n; i++){
	    y += (1.0/(n-1.0))/ (1.0 + exp(fa*(((i+1.0f)/n) - x)));
	  }
	  y = constrain(y, 0,1); 
	  return y;
	}



	//------------------------------------------------------------------
	public float function_Gompertz (float x, float a) {
	  // http://en.wikipedia.org/wiki/Gompertz_curve
	  functionName = "Gompertz Function";
	 
	  float min_param_a = 0.0f + EPSILON;
	  a = max(a, min_param_a); 

	  float b = -8.0f;
	  float c = 0 - a*16.0f;
	  float y = exp( b * exp(c * x));

	  float maxVal = exp(b * exp(c));
	  float minVal = exp(b );
	  y = map(y, minVal, maxVal, 0, 1); 

	  return y ;
	}

	// -- BRYCE F01
	
	// Generalized map
	public float function_GeneralizedLinearMap (float x, float a, float b, float c, float d) {
	  functionName = "Generalized Linear Map";
	  
	  float y = 0;
	  if (a < c) {
	    if (x <= a) {
	      y = b;
	    } 
	    else if (x >= c) {
	      y = d;
	    } 
	    else {
	      y = map(x, a, c, b, d);
	    }
	  } 
	  else {
	    if (x <= c) {
	      y = d;
	    } 
	    else if (x >= a) {
	      y = b;
	    } 
	    else {
	      y = map(x, c, a, d, b);
	    }
	  }
	  return y;
	}





	// Double-(Odd) Polynomial Seat
	//------------------------------------------------------------------
	public float function_DoubleOddPolynomialOgee (float x, float a, float b, int n) {
	  functionName = "Double Odd-Polynomial Ogee";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float min_param_b = 0.0f;
	  float max_param_b = 1.0f;

	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 
	  int p = 2*n + 1;
	  float y = 0;
	  if (x <= a) {
	    y = b - b*pow(1-x/a, p);
	  } 
	  else {
	    y = b + (1-b)*pow((x-a)/(1-a), p);
	  }
	  return y;
	}

	//-- BRYCE f02
	
	// Double-Linear Interpolator

	//------------------------------------------------------------------
	public float function_DoubleLinear (float x, float a, float b) {
	  functionName = "Double-Linear";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float min_param_b = 0.0f;
	  float max_param_b = 1.0f;
	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 

	  float y = 0;
	  if (x<=a) {
	    y = (b/a) * x;
	  } 
	  else {
	    y = b + ((1-b)/(1-a))*(x-a);
	  }
	  return y;
	}


	//------------------------------------------------------------------
	// Generalized map
	public float function_TripleLinear (float x, float a, float b, float c, float d) {
	  functionName = "Triple Linear";

	  float y = 0;
	  if (a < c) {
	    if (x <= a) {
	      y = map(x, 0, a, 0, b);
	    } 
	    else if (x >= c) {
	      y = map(x, c, 1, d, 1);
	    } 
	    else {
	      y = map(x, a, c, b, d);
	    }
	  } 
	  else {
	    if (x <= c) {
	      y = map(x, 0, c, 0, d);
	    } 
	    else if (x >= a) {
	      y = map(x, a, 1, b, 1);
	    } 
	    else {
	      y = map(x, c, a, d, b);
	    }
	  }
	  return y;
	}

	//------------------------------------------------------------------
	public float function_VariableStaircase (float x, float a, int n) {
	  functionName = "Variable Staircase";

	  float aa = (a - 0.5f);
	  if (aa == 0) {
	    return x;
	  }

	  float x0 = (floor (x*n))/ (float) n; 
	  float x1 = (ceil  (x*n))/ (float) n;
	  float y0 = x0; 
	  float y1 = x1; 

	  float px = 0.5f*(x0+x1) + aa/n;
	  float py = 0.5f*(x0+x1) - aa/n;

	  float y = 0;
	  if ((x < px) && (x > x0)) {
	    y = map(x, x0, px, y0, py);
	  } 
	  else {
	    y = map(x, px, x1, py, y1);
	  }

	  return y;
	}


	// FIXME : Quadratic Bezier Staircases have a bug that causes discontinuities.
	//------------------------------------------------------------------
	public float function_QuadraticBezierStaircase (float x, float a, int n) {
	  functionName = "Quadratic Bezier Staircase";

	  float aa = (a - 0.5f);
	  if (aa == 0) {
	    return x;
	  }

	  /* Skyscraper function.
	  float x0 = (floor (x*n))/ (float) n; 
	  float x1 = (ceil (x*n))/ (float) n;
	  
	  
	  float y0 = x0; 
	  float y1 = (floor((x + x/n)*n))/ (float) n;
	  */
	  
	  float x0 = (floor (x*n))/ (float) n;
	  float x1 = (ceil (x*n))/ (float) n;
	  	  
	  float y0 = x0; 
	  float y1 = x1; 

	  float px = 0.5f*(x0+x1) + aa/n;
	  float py = 0.5f*(x0+x1) - aa/n;

	  float p0x = (x0 + px)/2.0f;
	  float p0y = (y0 + py)/2.0f;
	  float p1x = (x1 + px)/2.0f;
	  float p1y = (y1 + py)/2.0f;
	  

	  float y = 0;
	  float denom = (1.0f/n)*0.5f;
	  
	  if ((x <= p0x) && (x >= x0)) {
	    // left side
	    if (floor (x*n) <= 0){
	      y = map(x, x0, px, y0, py);
	    } else {
	      
	      if (abs(x - x0) < EPSILON){
	        // problem when x == x0 !
	      }
	      
	      float za = (x0  - (p1x - 1.0f/n))/denom; 
	      float zb = (y0  - (p1y - 1.0f/n))/denom; 
	      float zx = ( x  - (p1x - 1.0f/n))/denom; 
	      float om2a = 1.0f - 2.0f*za;
	      
	      float interior = max (0, za*za + om2a*zx);
	      float t = (sqrt(interior) - za)/om2a;
	      float zy = (1.0f-2.0f*zb)*(t*t) + (2*zb)*t;
	      zy *= (p1y - p0y);
	      zy += p1y; //(p1y - 1.0/n);
	      if (x > x0){
	        zy -= 1.0/n;
	      }
	      y = zy;
	    }
	  } 

	  else if ((x >= p1x) && (x <= x1)) {
	    // right side
	    if (ceil  (x*n) >= n) {
	      y = map(x, px, x1, py, y1);
	    } 
	    else {
	      if (abs(x - x1) < EPSILON){
	        // problem when x == x1 !
	      }
	      
	      float za = (x1 - p1x)/denom; 
	      float zb = (y1 - p1y)/denom; 
	      float zx = ( x - p1x)/denom; 
	      if (za == 0.5) {
	        za += EPSILON;
	      }
	      float om2a = 1.0f - 2.0f*za;
	      if (abs(om2a) < EPSILON) {
	        om2a = ((om2a < 0) ? -1:1) * EPSILON;
	      }
	      
	      float interior = max (0, za*za + om2a*zx);
	      float t = (sqrt(interior) - za)/om2a;
	      float zy = (1.0f-2.0f*zb)*(t*t) + (2f*zb)*t;
	      zy *= (p1y - p0y);
	      zy += p1y;
	      y = zy;
	    }
	  } 

	  else {
	    // center
	    float za = (px - p0x)/denom; 
	    float zb = (py - p0y)/denom; 
	    float zx = ( x - p0x)/denom; 
	    if (za == 0.5) {
	      za += EPSILON;
	    }
	    float om2a = 1.0f - 2.0f*za;
	    float t = (sqrt(za*za + om2a*zx) - za)/om2a;
	    float zy = (1.0f-2.0f*zb)*(t*t) + (2*zb)*t;
	    zy *= (p1y - p0y);
	    zy += p0y;
	    y = zy;
	  }
	  return y;

	}
	
	// -- BRYCE f03
	
	// Symmetric Double-Element Sigmoids

	//------------------------------------------------------------------
	public float function_DoubleExponentialSigmoid (float x, float a){
	  functionName = "Double-Exponential Sigmoid";
	  
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 
	  a = 1-a;
	  
	  float y = 0;
	  if (x<=0.5){
	    y = (pow(2.0f*x, 1.0f/a))/2.0f;
	  } 
	  else {
	    y = 1.0f - (pow(2.0f*(1.0f-x), 1.0f/a))/2.0f;
	  }
	  return y;
	}



	//------------------------------------------------------------------
	public float function_AdjustableCenterDoubleExponentialSigmoid (float x, float a, float b){
	  
	  functionName = "Adjustable-Center Double-Exponential Sigmoid";
	  
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 
	  a = 1-a;
	  
	  float y = 0;
	  float w = max(0, min(1, x-(b-0.5f)));
	  if (w<=0.5f){
	    y = (pow(2.0f*w, 1.0f/a))/2.0f;
	  } 
	  else {
	    y = 1.0f - (pow(2.0f*(1.0f-w), 1.0f/a))/2.0f;
	  }
	  return y;
	}



	//------------------------------------------------------------------
	public float function_DoubleQuadraticSigmoid (float x){
	  functionName = "Double-Quadratic Sigmoid";

	  float y = 0;
	  if (x<=0.5){
	    y = sq(2.0f*x)/2.0f;
	  } 
	  else {
	    y = 1.0f - sq(2.0f*(x-1.0f))/2.0f;
	  }
	  return y;
	}


	//------------------------------------------------------------------
	public float function_DoublePolynomialSigmoid (float x, int n){
	  functionName = "Double-Polynomial Sigmoid";

	  float y = 0;
	  if (n%2 == 0){ 
	    // even polynomial
	    if (x<=0.5f){
	      y = pow(2.0f*x, n)/2.0f;
	    } 
	    else {
	      y = 1.0f - pow(2*(x-1.0f), n)/2.0f;
	    }
	  } 
	  
	  else { 
	    // odd polynomial
	    if (x<=0.5f){
	      y = pow(2.0f*x, n)/2.0f;
	    } 
	    else {
	      y = 1.0f + pow(2.0f*(x-1.0f), n)/2.0f;
	    }

	  }

	  return y;
	}
	
	
	// -- BRYCE f04
	
	//------------------------------------------------------------------
	public float function_DoubleEllipticOgee (float x, float a, float b){
	  functionName = "Double-Elliptic Ogee";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 
	  float y = 0;

	  if (x<=a){
	    y = (b/a) * sqrt(sq(a) - sq(x-a));
	  } 
	  else {
	    y = 1.0f - ((1.0f-b)/(1.0f-a))*sqrt(sq(1.0f-a) - sq(x-a));
	  }
	  return y;
	}

	//------------------------------------------------------------------
	public float function_AdjustableCenterEllipticWindow (float x, float a){
	  functionName = "Adjustable-Center Elliptic Window";
	  
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a);
	  
	  float y = 0;

	  if (x<=a){
	    y = (1.0f/a) * sqrt(sq(a) - sq(x-a));
	  } 
	  else {
	    y = (1.0f/(1-a)) * sqrt(sq(1.0f-a) - sq(x-a));
	  }
	  return y;
	}


	//------------------------------------------------------------------
	public float function_AdjustableCenterHyperellipticWindow (float x, float a, int n){
	  functionName = "Adjustable-Center Hyperelliptic Window";
	  
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a);
	  
	  float y = 0;
	  float pwn = n * 2.0f; 

	  if (x<=a){
	    y = (1.0f/a) * pow( pow(a, pwn)     - pow(x-a, pwn), 1.0f/pwn);
	  } 
	  else {
	    y =  ((1.0f/ (1-a)))  * pow( pow(1.0f-a, pwn) - pow(x-a, pwn), 1.0f/pwn);
	  }
	  return y;
	}

	//------------------------------------------------------------------
	public float function_AdjustableCenterSquircularWindow (float x, float a, int n){
	  functionName = "Adjustable-Center Squircular Window";
	  
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a);
	  
	  float y = 0;
	  float pwn = max(2, n * 2.0f); 

	  if (x<=a){
	    y = (1-a) + pow( pow(a, pwn) - pow(x-a, pwn), 1.0f/pwn);
	  } 
	  else {
	    y = a + pow( pow(1.0f-a, pwn) - pow(x-a, pwn), 1.0f/pwn);
	  }
	  return y;
	}
	
	// -- Bryce F05
	
	// Double-Cubic Seat 

	//------------------------------------------------------------------
	public float function_DoubleCubicOgee (float x, float a, float b){
	  functionName = "Double-Cubic Ogee";
	  
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float min_param_b = 0.0f;
	  float max_param_b = 1.0f;

	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 
	  float y = 0;
	  if (x <= a){
	    y = b - b*pow(1.0f-x/a, 3.0f);
	  } 
	  else {
	    y = b + (1.0f-b)*pow((x-a)/(1.0f-a), 3.0f);
	  }
	  return y;
	}
	
	// -- BRYCE f06
	
	//------------------------------------------------------------------
	public float function_DoubleCircularSigmoid (float x, float a) {
	  functionName = "Double-Circular Sigmoid";
	  
	  float y = 0;
	  if (x<=a) {
	    y = a - sqrt(a*a - x*x);
	  } 
	  else {
	    y = a + sqrt(sq(1.0f-a) - sq(x-1.0f));
	  }
	  return y;
	}

	//------------------------------------------------------------------
	public float function_DoubleSquircularSigmoid (float x, float a, int n) {
	  functionName = "Double-Squircular Sigmoid";
	 
	  float pwn = max(2, n * 2.0f); 
	  float y = 0;
	  if (x<=a) {
	    y = a - pow( pow(a,pwn) - pow(x,pwn), 1.0f/pwn);
	  } 
	  else {
	    y = a + pow(pow(1.0f-a, pwn) - pow(x-1.0f, pwn), 1.0f/pwn);
	  }
	  return y;
	}


	//------------------------------------------------------------------
	public float function_CircularEaseIn (float x) {
	  functionName = "Circular Ease In";

	  float y = 1.0f - sqrt(1.0f - x*x);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_CircularEaseOut (float x) {
	  functionName = "Circular Ease Out";

	  float y = sqrt(1.0f - sq(1.0f - x));
	  return y;
	}


	//------------------------------------------------------------------
	public float function_CircularEaseInOut (float x) {
	  functionName = "Penner's Circular Ease InOut";

	  float y = 0; 
	  x *= 2.0f; 
	  
	  if (x < 1) {
	    y =  -0.5f * (sqrt(1.0f - x*x) - 1.0f);
	  } else {
	    x -= 2.0f;
	    y =   0.5f * (sqrt(1.0f - x*x) + 1.0f);
	  }
	  
	  return y;
	}


	//------------------------------------------------------------------
	public float function_DoubleQuadraticBezier (float x, float a, float b, float c, float d) {
	  functionName = "Double Quadratic Bezier";
	  // also see http://engineeringtraining.tpub.com/14069/css/14069_150.htm
	  // produces mysterious values when a=0,b=1,c=0.667,d=0.417
	  
	  float xmid = (a + c)/2.0f; 
	  float ymid = (b + d)/2.0f; 
	  xmid = constrain (xmid, EPSILON, 1.0f-EPSILON);
	  ymid = constrain (ymid, EPSILON, 1.0f-EPSILON);
	  
	  float y = 0;
	  float om2a;
	  float t; 
	  float xx; 
	  float aa; 
	  float bb;

	  if (x <= xmid){
	    xx = x / xmid;
	    aa = a / xmid; 
	    bb = b / ymid; 
	    om2a = 1.0f - 2.0f*aa;
	    if (om2a == 0) {
	       om2a = EPSILON; 
	    }   
	    t = (sqrt(aa*aa + om2a*xx) - aa)/om2a;
	    y = (1.0f-2.0f*bb)*(t*t) + (2*bb)*t;
	    y *= ymid;
	  }
	  else {
	     xx = (x - xmid)/(1.0f-xmid);
	     aa = (c - xmid)/(1.0f-xmid); 
	     bb = (d - ymid)/(1.0f-ymid); 
	     om2a = 1.0f - 2.0f*aa;
	     if (om2a == 0) {
	       om2a = EPSILON; 
	     }     
	     t = (sqrt(aa*aa + om2a*xx) - aa)/om2a;
	     y = (1.0f-2.0f*bb)*(t*t) + (2*bb)*t;
	     y *= (1.0 - ymid); 
	     y += ymid;
	  }

	  return y; 
	}

	// -- BRYCE f07
	
	// Double-Elliptic Sigmoid

	//------------------------------------------------------------------
	public float function_DoubleEllipticSigmoid (float x, float a, float b){
	  functionName = "Double-Elliptic Sigmoid";

	  float y = 0;
	  if (x<=a){
	    if (a <= 0){
	      y = 0;
	    } else {
	      y = b * (1.0f - (sqrt(sq(a) - sq(x))/a));
	    }
	  } 
	  else {
	    if (a >= 1){
	      y = 1.0f;
	    } else {
	      y = b + ((1.0f-b)/(1.0f-a))*sqrt(sq(1.0f-a) - sq(x-1.0f));
	    }
	  }
	  return y;
	}
	
	// BRYCE f08
	
	// Simplified Double-Cubic Seat

	//------------------------------------------------------------------
	public float function_DoubleCubicOgeeSimplified (float x, float a, float b){
	  functionName = "Simplified Double-Cubic Ogee";
	  b = 1 - b; //reverse, for intelligibility.
	  
	  float y = 0;
	  if (x<=a){
	    if (a <= 0){
	      y = 0; 
	    } else {
	      float val = 1 - x/a;
	      y = b*x + (1-b)*a*(1.0f- val*val*val);
	    }
	  } 
	  else {
	    if (a >= 1){
	      y = 1;
	    } else {
	      float val = (x-a)/(1-a);
	      y = b*x + (1-b)*(a + (1-a)* val*val*val);
	    }
	  }

	  return y;
	}
	
	// BRYCE f09
	
	
	//------------------------------------------------------------------
	public float function_RaisedInvertedCosine (float x) {
	  functionName = "Raised Inverted Cosine";

	  float y = (1.0f - cos(PI*x))/2.0f;
	  return y;
	}

	//------------------------------------------------------------------
	public float function_BlinnWyvillCosineApproximation (float x) {
	  functionName = "Blinn/Wyvill's Cosine Approximation";

	  float x2 = x*x;
	  float x4 = x2*x2;
	  float x6 = x4*x2;
	  float fa = ( 4.0f/9.0f);
	  float fb = (17.0f/9.0f);
	  float fc = (22.0f/9.0f);
	  float y = fa*x6 - fb*x4 + fc*x2;

	  return y;
	}

	//------------------------------------------------------------------
	public float function_SmoothStep (float x) { 
	  // http://en.wikipedia.org/wiki/Smoothstep
	  functionName = "Smooth Step";

	  return x*x*(3.0f - 2.0f*x);
	}

	//------------------------------------------------------------------
	public float function_SmootherStep (float x) { 
	  // http://en.wikipedia.org/wiki/Smoothstep
	  functionName = "Perlin's Smoother Step";

	  return x*x*x*(x*(x*6.0f - 15.0f) + 10.0f);
	}

	//------------------------------------------------------------------
	public float function_MaclaurinCos (float x) {
	  // http://blogs.ubc.ca/infiniteseriesmodule/units/unit-3-power-series/taylor-series/the-maclaurin-expansion-of-cosx/

	  functionName = "Maclaurin Cosine Approximation";
	  int nTerms = 6; // anything less is fouled

	  x *= PI;
	  float xp = 1.0f;
	  float x2 = x*x;

	  float sig  = 1.0f;
	  float fact = 1.0f;
	  float out = xp;

	  for (int i=0; i<nTerms; i++) {
	    xp   *= x2; 
	    sig  = 0-sig;
	    fact *= (i*2+1); 
	    fact *= (i*2+2);
	    out  += sig * (xp / fact);
	  }

	  out = (1.0f - out)/2.0f;
	  return out;
	}

	//------------------------------------------------------------------
	// from http://paulbourke.net/miscellaneous/interpolation/
	public float function_CatmullRomInterpolate (float x, float a, float b) {
	  functionName = "Catmull-Rom Interpolation";

	  float y0 = a; 
	  float y3 = b; 
	  float x2 = x*x;

	  /*
	   float y1 = 0; //1.0/3.0
	   float y2 = 1; 
	   
	   float a0 = -0.5*y0 + 1.5*y1 - 1.5*y2 + 0.5*y3;
	   float a1 =      y0 - 2.5*y1 + 2.0*y2 - 0.5*y3;
	   float a2 = -0.5*y0          + 0.5*y2;
	   float a3 =               y1;
	   return (a0*x*x2 + a1*x2 + a2*x + a3);
	   */

	  float a0 = -0.5f*y0 + 0.5f*y3 - 1.5f ;
	  float a1 =       y0 - 0.5f*y3 + 2.0f ;
	  float a2 = -0.5f*y0           + 0.5f ;

	  float out = a0*x*x2 + a1*x2 + a2*x;
	  return constrain (out, 0, 1);
	}


	//------------------------------------------------------------------
	// from http://musicdsp.org/showArchiveComment.php?ArchiveID=93
	// by Laurent de Soras
	public float function_Hermite (float x, float a, float b, float c, float d) {
	  functionName = "Hermite (de Soras)";
	  a = map(a, 0,1, -1,1);
	  c = map(c, 0,1, -1,1);
	  
	  float hC = (c - a) * 0.5f;
	  float hV = (b - d);
	  float hW = hC + hV;
	  float hA = hW + hV + (c - b) * 0.5f;
	  float hB = hW + hA;

	  return ((((hA * x) - hB) * x + hC) * x + b);
	}


	//------------------------------------------------------------------
	// from http://paulbourke.net/miscellaneous/interpolation/
	public float function_Hermite2 (float x, float a, float b, float c, float d) {
	  functionName = "Hermite (Bourke)";

	  /*
	   Tension: 1 is high, 0 normal, -1 is low
	   Bias: 0 is even, positive is towards first segment, negative towards the other
	   */

	  float tension = map (c, 0,1, -1,1); 
	  float bias    = map (d, 0,1, -1,1); 

	  float y0 = 2.0f * (a - 0.5f);  //? a
	  float y1 = 0.0f; 
	  float y2 = 1.0f; 
	  float y3 = b;

	  float x2 =  x * x;
	  float x3 = x2 * x;

	  float m0, m1;
	  m0  = (y1-y0)*(1.0f+bias)*(1.0f-tension)/2.0f;
	  m0 += (y2-y1)*(1.0f-bias)*(1.0f-tension)/2.0f;
	  m1  = (y2-y1)*(1.0f+bias)*(1.0f-tension)/2.0f;
	  m1 += (y3-y2)*(1.0f-bias)*(1.0f-tension)/2.0f;

	  float a0  =  2.0f*x3 - 3.0f*x2 + 1.0f;
	  float a1  =       x3 - 2.0f*x2 + x;
	  float a2  =       x3 -      x2;
	  float a3  = -2.0f*x3 + 3.0f*x2;

	  return (a0*y1 + a1*m0 + a2*m1 + a3*y2);
	}

	//------------------------------------------------------------------
	public float function_NormalizedErf (float x) {
	  // http://en.wikipedia.org/wiki/Error_function
	  // Note that this implementation is a shifted, scaled and normalized error function!
	  functionName = "Error Function";
	  
	  float erfBound = 2.0f; // set bounds for artificial "normalization"
	  float erfBoundNorm = 0.99532226501f; // this = erf(2.0), i.e., erf(erfBound)
	  float z = map(x, 0.0f, 1.0f, 0-erfBound, erfBound); 

	  float z2 = z*z; 
	  float a = (8.0f*(PI-3.0f)) / ((3*PI)*(4.0f-PI)); 
	  float out = sqrt (1.0f - exp(0 - z2*(  (a*z2 + 4.0f/PI) / (a*z2 + 1.0f))));
	  if (z < 0.0) out = 0-out;

	  out /= erfBoundNorm;
	  out = (out+1.0f) / 2.0f; 

	  return out;
	}

	//------------------------------------------------------------------
	public float function_NormalizedInverseErf (float x) {
	  // http://en.wikipedia.org/wiki/Error_function
	  // Note that this implementation is a shifted, scaled and normalized error function!
	  functionName = "Inverse Error Function";

	  float erfBound = 2.0f;
	  float erfBoundNorm = 0.99532226501f; // this = erf(2.0), i.e., erf(erfBound)
	  float z = map(x, 0, 1, -erfBoundNorm, erfBoundNorm); 
	  float z2 = z*z;
	  float a = (8.0f*(PI-3.0f)) / ((3*PI)*(4.0f-PI)); 

	  float A = (2.0f / (PI *a)) + (log(1.0f-z2) / 2.0f);
	  float B = (log(1.0f-z2) / a);
	  float out = sqrt( sqrt(A*A - B) - A );

	  if (z < 0.0) out = 0-out;
	  out /= erfBound; 
	  out = (out+1.0f); 
	  out /= 2.0;

	  out = constrain(out, 0, 1);  // necessary
	  return out;
	}

	//------------------------------------------------------------------
	public float function_SimpleHalfGaussian (float x) {
	  // http://en.wikipedia.org/wiki/Gaussian_function
	  functionName = "Simple Gaussian (Half)";

	  float sigma = 0.25f; // produces results < 0.001 at f(0); 
	  float out = exp(0.0f - (sq(x-1.0f) / (2.0f*sigma*sigma))); 
	  return out;
	}

	//------------------------------------------------------------------
	public float function_AdjustableFwhmHalfGaussian (float x, float a) {
	  // http://en.wikipedia.org/wiki/Gaussian_function
	  // http://en.wikipedia.org/wiki/Full_width_at_half_maximum
	  functionName = "Adjustable-FWHM Gaussian (Half)";

	  float denom = sqrt(2.0f*log(2.0f));
	  float sigma = (1.0f - a) / denom;

	  // 68.26894921371
	  float out = exp(0.0f - (sq(x-1.0f) / (2.0f*sigma*sigma))); 
	  return out;
	}

	//------------------------------------------------------------------
	public float function_HalfGaussianThroughAPoint (float x, float a, float b) {
	  // http://en.wikipedia.org/wiki/Gaussian_function
	  // http://en.wikipedia.org/wiki/Full_width_at_half_maximum
	  functionName = "Gaussian Through A Point (Half)";
	  
	  b = max(0.0000001f, b); 
	  float denom = sqrt(2.0f*log(1.0f/b));
	  float sigma = (1.0f - a) / denom;

	  // 68.26894921371
	  float out = exp(0.0f - (sq(x-1.0f) / (2.0f*sigma*sigma))); 
	  return out;
	}

	//------------------------------------------------------------------
	public float function_AdjustableSigmaHalfGaussian (float x, float a) {
	  // http://en.wikipedia.org/wiki/Gaussian_function
	  functionName = "Adjustable-Sigma Gaussian (Half)";

	  float sigma = 1.0f-a;
	  float out = exp(0.0f - (sq(x-1.0f) / (2.0f*sigma*sigma))); 
	  return out;
	}

	//------------------------------------------------------------------
	public float function_HalfLanczosSincWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Lanczos Sinc Window (Half)";

	  float y = sinc (1.0f - x);
	  return y;
	}

	float sinc (float x) {
	  float pix = PI*x;
	  if (x == 0) {
	    return 1.0f;
	  } 
	  else {
	    return (sin(pix) / pix);
	  }
	}

	//------------------------------------------------------------------
	public float function_HalfNuttallWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Nuttall Window (Half)";

	  final float a0 = 0.355768f;
	  final float a1 = 0.487396f;
	  final float a2 = 0.144232f;
	  final float a3 = 0.012604f;

	  x *= 0.5;
	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix) - a3*cos(6*pix);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_HalfBlackmanNuttallWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Blackman_Nuttall Window (Half)";

	  final float a0 = 0.3635819f;
	  final float a1 = 0.4891775f;
	  final float a2 = 0.1365995f;
	  final float a3 = 0.0106411f;

	  x *= 0.5;
	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix) - a3*cos(6*pix);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_HalfBlackmanHarrisWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Blackman_Harris Window (Half)";

	  final float a0 = 0.35875f;
	  final float a1 = 0.48829f;
	  final float a2 = 0.14128f;
	  final float a3 = 0.01168f;

	  x *= 0.5f;
	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix) - a3*cos(6*pix);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_HalfExactBlackmanWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Exact Blackman Window (Half)";

	  final float a0 = 7938.0f / 18608.0f;
	  final float a1 = 9240.0f / 18608.0f;
	  final float a2 = 1430.0f / 18608.0f;

	  x *= 0.5;
	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_HalfGeneralizedBlackmanWindow (float x, float a) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Generalized Blackman Window (Half)";

	  float a0 = (1.0f - a)/2.0f;
	  float a1 = 0.5f;
	  float a2 = a / 2.0f;

	  x *= 0.5;
	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_HalfFlatTopWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Flat Top Window (Half)";

	  final float a0 = 1.000f;
	  final float a1 = 1.930f;
	  final float a2 = 1.290f;
	  final float a3 = 0.388f;
	  final float a4 = 0.032f;

	  x *= 0.5;
	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix) - a3*cos(6*pix) + a4*cos(8*pix);
	  y /= (a0 + a1 + a2 + a3 + a4); 

	  return y;
	}

	//------------------------------------------------------------------
	public float function_HalfBartlettHannWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Bartlett-Hann Window (Half)";

	  final float a0 = 0.62f;
	  final float a1 = 0.48f;
	  final float a2 = 0.38f;

	  x *= 0.5;
	  float y = a0 - a1*abs(x - 0.5f) - a2*cos(2*PI*x);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_BartlettWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  // Triangular window with zero-valued end-points:
	  functionName = "Bartlett (Triangle) Window";

	  float y = 2.0f * (0.5f - abs(x - 0.5f));
	  return y;
	}

	//------------------------------------------------------------------
	public float function_TukeyWindow (float x, float a) {
	  functionName = "Tukey Window";
	  // http://en.wikipedia.org/wiki/Window_function 
	  // The Tukey window, also known as the tapered cosine window, 
	  // can be regarded as a cosine lobe of width \tfrac{\alpha N}{2} 
	  // that is convolved with a rectangle window of width \left(1 -\tfrac{\alpha}{2}\right)N.  
	  // At alpha=0 it becomes rectangular, and at alpha=1 it becomes a Hann window.

	  float ah = a/2.0f; 
	  float omah = 1.0f - ah;

	  float y = 1.0f;
	  if (x <= ah) {
	    y = 0.5f * (1.0f + cos(PI* ((2*x/a) - 1.0f)));
	  } 
	  else if (x > omah) {
	    y = 0.5f * (1.0f + cos(PI* ((2*x/a) - (2/a) + 1.0f)));
	  } 
	  return y;
	}

	//------------------------------------------------------------------
	public float function_AdjustableCenterCosineWindow (float x, float a) {
	  functionName = "Adjustable Center Cosine Window";
	  
	  float ah = a/2.0f; 
	  float omah = 1.0f - ah;

	  float y = 1.0f;
	  if (x <= a) {
	    y = 0.5f * (1.0f + cos(PI* ((x/a) - 1.0f)));
	  } 
	  else {
	    y = 0.5f * (1.0f + cos(PI* (((x-a)/(1.0f-a))  )));
	  } 
	  return y;
	}

	//------------------------------------------------------------------
	public float function_CosineWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Cosine Window";

	  float y = sin (PI*x);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_AdjustableSigmaGaussian (float x, float a) {
	  // http://en.wikipedia.org/wiki/Gaussian_function
	  functionName = "Adjustable-Sigma Gaussian Window";
	 
	  x *= 2.0f;
	  a *= 2.0f; 
	  float sigma = a;
	  float out = exp(0.0f - (sq(x-1.0f) / (2.0f*sigma*sigma))); 
	  return out;
	}

	//------------------------------------------------------------------
	public float function_SlidingAdjustableSigmaGaussian (float x, float a, float b) {
	  // http://en.wikipedia.org/wiki/Gaussian_function
	  functionName = "Sliding Adjustable-Sigma Gaussian Window";
	  
	  x *= 2.0;
	  b *= 2.0; 
	  float sigma = b;
	  float dx = 2.0f*(a - 0.5f); 
	  float out = exp(0.0f - (sq(x-1.0f -dx) / (2.0f*sigma*sigma))); 
	  return out;
	}

	//------------------------------------------------------------------
	public float function_LanczosSincWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Lanczos Sinc Window";

	  x *= 2.0f;
	  float y = sinc (1.0f - x);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_NuttallWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Nuttall Window";

	  final float a0 = 0.355768f;
	  final float a1 = 0.487396f;
	  final float a2 = 0.144232f;
	  final float a3 = 0.012604f;

	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix) - a3*cos(6*pix);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_BlackmanNuttallWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Blackman_Nuttall Window";

	  final float a0 = 0.3635819f;
	  final float a1 = 0.4891775f;
	  final float a2 = 0.1365995f;
	  final float a3 = 0.0106411f;

	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix) - a3*cos(6*pix);
	  return y;
	}


	//------------------------------------------------------------------
	public float function_BlackmanHarrisWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Blackman_Harris Window";

	  final float a0 = 0.35875f;
	  final float a1 = 0.48829f;
	  final float a2 = 0.14128f;
	  final float a3 = 0.01168f;

	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix) - a3*cos(6*pix);
	  return y;
	}


	//------------------------------------------------------------------
	public float function_ExactBlackmanWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Exact Blackman Window";

	  final float a0 = 7938.0f / 18608.0f;
	  final float a1 = 9240.0f / 18608.0f;
	  final float a2 = 1430.0f / 18608.0f;

	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix);
	  return y;
	}


	//------------------------------------------------------------------
	public float function_GeneralizedBlackmanWindow (float x, float a) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Generalized Blackman Window";

	  float a0 = (1.0f - a)/2.0f;
	  float a1 = 0.5f;
	  float a2 = a / 2.0f;

	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix);
	  return y;
	}


	//------------------------------------------------------------------
	public float function_FlatTopWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Flat Top Window";

	  final float a0 = 1.000f;
	  final float a1 = 1.930f;
	  final float a2 = 1.290f;
	  final float a3 = 0.388f;
	  final float a4 = 0.032f;

	  float pix = PI*x;
	  float y = a0 - a1*cos(2*pix) + a2*cos(4*pix) - a3*cos(6*pix) + a4*cos(8*pix);
	  y /= (a0 + a1 + a2 + a3 + a4); 

	  return y;
	}

	//------------------------------------------------------------------
	public float function_BartlettHannWindow (float x) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Bartlett-Hann Window";

	  final float a0 = 0.62f;
	  final float a1 = 0.48f;
	  final float a2 = 0.38f;

	  float y = a0 - a1*abs(x - 0.5f) - a2*cos(2*PI*x);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_HannWindow (float x){
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Hann (Raised Cosine) Window";

	  float y = 0.5f * (1.0f - cos(TWO_PI*x));
	  return y;
	}

	//------------------------------------------------------------------
	public float function_HammingWindow (float x){
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Hamming Window";

	  float y = 0.54f - 0.46f*cos(TWO_PI*x);
	  return y;
	}

	//------------------------------------------------------------------
	public float function_GeneralizedTriangleWindow (float x, float a) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Generalized Triangle Window";
	 
	  float y = 0; 
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 
	  
	  if (x < a){
	    y = (x / a); 
	  } else {
	    y = 1.0f - ((x-a)/(1.0f-a));
	  }
	  return y;
	}

	//------------------------------------------------------------------
	public float function_PoissonWindow (float x, float a) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Poisson or Exponential Window";

	  float epsilon = 0.00001f;
	  float tau = max(a, epsilon); 
	  
	  float y = exp (0.0f - (abs(x - 0.5f))*(1.0f/tau));
	  return y; 
	}

	//------------------------------------------------------------------
	public float function_HannPoissonWindow (float x, float a) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Hann-Poisson Window";

	  float epsilon = 0.00001f;
	  float tau = 25.0f * max(a*a*a*a, epsilon); // nice control
	  
	  float hy = 0.5f * (1.0f - cos(TWO_PI*x));
	  float py = exp (0.0f - (abs(x - 0.5f))*(1.0f/tau));
	  return (hy * py); 
	}

	//------------------------------------------------------------------
	public float function_HannPoissonWindowSliding (float x, float a, float b) {
	  // http://en.wikipedia.org/wiki/Window_function 
	  functionName = "Sliding Hann-Poisson Window";

	  float epsilon = 0.00001f;
	  float tau = 25.0f * max(b*b*b*b, epsilon); // nice range of control
	  
	  float newx = constrain(x + (0.5f - a), 0, 1); 
	  float hy = 0.5f * (1.0f - cos(TWO_PI*newx));
	  float py = exp (0.0f - (abs(newx - 0.5f))*(1.0f/tau));
	  return (hy * py); 
	}
	
	// BRYCE f10
	
	// Exponential shapers

	public float function_ExponentialEmphasis (float x, float a) {
	  functionName = "Exponential Emphasis";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 

	  if (a < 0.5) {
	    // emphasis
	    a = 2*(a);
	    float y = pow(x, a);
	    return y;
	  } 
	  else {
	    // de-emphasis
	    a = 2*(a-0.5f);
	    float y = pow(x, 1.0f/(1-a));
	    return y;
	  }
	}

	//------------------------------------------------------------------
	public float function_IterativeSquareRoot (float x) {
	  // http://en.wikipedia.org/wiki/Methods_of_computing_square_roots
	  // Ancient Babylonian technology
	  functionName = "Iterative (Heron's) Square Root";
	  float y = 0.5f; 
	  int n = 6;
	  for (int i=0; i<n; i++) {
	    y = (y + x/y)/2.0f;
	  }
	  return y;
	}

	//------------------------------------------------------------------
	public float function_FastSquareRoot(float x) {
	  // http://en.wikipedia.org/wiki/Fast_inverse_square_root
	  // http://stackoverflow.com/questions/11513344/how-to-implement-the-fast-inverse-square-root-in-java
	  functionName = "FastSquareRoot";
	  
	  float xhalf = 0.5f * x;
	  int i = Float.floatToIntBits(x);
	  i = 0x5f3759df - (i>>1);
	  x = Float.intBitsToFloat(i);
	  x = x*(1.5f - xhalf*x*x);
	  return 1.0f/x;
	}
	
	// BRYCE f12
	
	// Joining Two Lines with a Circular Arc Fillet
	// Adapted from Robert D. Miller / Graphics Gems III.

	float arcStartAngle;
	float arcEndAngle;
	float arcStartX, arcStartY;
	float arcEndX, arcEndY;
	float arcCenterX, arcCenterY;
	float arcRadius;

	// ====================================================================
	public float function_CircularFillet (float x, float a, float b, float c) {
	  functionName = "Double-Linear with Circular Fillet";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float min_param_b = 0.0f + EPSILON;
	  float max_param_b = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 

	  float R = c;
	  computeFilletParameters (0, 0, a, b, a, b, 1, 1, R);

	  float t = 0;
	  float y = 0;
	  x = constrain(x, 0, 1); 

	  if (x <= arcStartX) {
	    if (arcStartX < EPSILON){
	      y = 0;
	    } else {
	      t = x / arcStartX;
	      y = t * arcStartY;
	    }
	  } 
	  else if (x >= arcEndX) {
	    t = (x - arcEndX)/(1 - arcEndX);
	    y = arcEndY + t*(1 - arcEndY);
	  } 
	  else {
	    if (x >= arcCenterX) {
	      y = arcCenterY - sqrt(sq(arcRadius) - sq(x-arcCenterX));
	    } 
	    else {
	      y = arcCenterY + sqrt(sq(arcRadius) - sq(x-arcCenterX));
	    }
	  }
	  return y;
	}


	// ====================================================================
	// Return signed distance from line Ax + By + C = 0 to point P.
	public float linetopoint (float a, float b, float c, float ptx, float pty) {
	  float lp = 0.0f;
	  float d = sqrt((a*a)+(b*b));
	  if (d != 0.0) {
	    lp = (a*ptx + b*pty + c)/d;
	  }
	  return lp;
	}

	// ====================================================================
	// Compute the paramters of a circular arc fillet between lines L1 (p1 to p2) and
	// L2 (p3 to p4) with radius R.  
	// 
	void computeFilletParameters (
	float p1x, float p1y, 
	float p2x, float p2y, 
	float p3x, float p3y, 
	float p4x, float p4y, 
	float r) {

	  float c1   = p2x*p1y - p1x*p2y;
	  float a1   = p2y-p1y;
	  float b1   = p1x-p2x;
	  float c2   = p4x*p3y - p3x*p4y;
	  float a2   = p4y-p3y;
	  float b2   = p3x-p4x;
	  if ((a1*b2) == (a2*b1)) {  /* Parallel or coincident lines */
	    return;
	  }

	  float d1, d2;
	  float mPx, mPy;
	  mPx= (p3x + p4x)/2.0f;
	  mPy= (p3y + p4y)/2.0f;
	  d1 = linetopoint(a1, b1, c1, mPx, mPy);  /* Find distance p1p2 to p3 */
	  if (d1 == 0.0) {
	    return;
	  }
	  mPx= (p1x + p2x)/2.0f;
	  mPy= (p1y + p2y)/2.0f;
	  d2 = linetopoint(a2, b2, c2, mPx, mPy);  /* Find distance p3p4 to p2 */
	  if (d2 == 0.0) {
	    return;
	  }

	  float c1p, c2p, d;
	  float rr = r;
	  if (d1 <= 0.0) {
	    rr= -rr;
	  }
	  c1p = c1 - rr*sqrt((a1*a1)+(b1*b1));  /* Line parallel l1 at d */
	  rr = r;
	  if (d2 <= 0.0) {
	    rr = -rr;
	  }
	  c2p = c2 - rr*sqrt((a2*a2)+(b2*b2));  /* Line parallel l2 at d */
	  d = (a1*b2)-(a2*b1);

	  float pCx = (c2p*b1-c1p*b2)/d;                /* Intersect constructed lines */
	  float pCy = (c1p*a2-c2p*a1)/d;                /* to find center of arc */
	  float pAx = 0;
	  float pAy = 0;
	  float pBx = 0;
	  float pBy = 0;
	  float dP, cP;

	  dP = (a1*a1) + (b1*b1);              /* Clip or extend lines as required */
	  if (dP != 0.0) {
	    cP = a1*pCy - b1*pCx;
	    pAx = (-a1*c1 - b1*cP)/dP;
	    pAy = ( a1*cP - b1*c1)/dP;
	  }
	  dP = (a2*a2) + (b2*b2);
	  if (dP != 0.0) {
	    cP = a2*pCy - b2*pCx;
	    pBx = (-a2*c2 - b2*cP)/dP;
	    pBy = ( a2*cP - b2*c2)/dP;
	  }

	  float gv1x = pAx-pCx; 
	  float gv1y = pAy-pCy;
	  float gv2x = pBx-pCx; 
	  float gv2y = pBy-pCy;

	  float arcStart = (float) atan2(gv1y, gv1x); 
	  float arcAngle = 0.0f;
	  float dd = (float) sqrt(((gv1x*gv1x)+(gv1y*gv1y)) * ((gv2x*gv2x)+(gv2y*gv2y)));
	  if (dd != (float) 0.0) {
	    arcAngle = (acos((gv1x*gv2x + gv1y*gv2y)/dd));
	  } 
	  float crossProduct = (gv1x*gv2y - gv2x*gv1y);
	  if (crossProduct < 0.0) { 
	    arcStart -= arcAngle;
	  }

	  float arc1 = arcStart;
	  float arc2 = arcStart + arcAngle;
	  if (crossProduct < 0.0) {
	    arc1 = arcStart + arcAngle;
	    arc2 = arcStart;
	  }

	  arcCenterX    = pCx;
	  arcCenterY    = pCy;
	  arcStartAngle = arc1;
	  arcEndAngle   = arc2;
	  arcRadius     = r;
	  arcStartX = arcCenterX + arcRadius*cos(arcStartAngle);
	  arcStartY = arcCenterY + arcRadius*sin(arcStartAngle);
	  arcEndX   = arcCenterX + arcRadius*cos(arcEndAngle);
	  arcEndY   = arcCenterY + arcRadius*sin(arcEndAngle);
	}
	
	// BRYCE f11
	
	// Symmetric Double-Exponential Seat

	//------------------------------------------------------------------
	public float function_DoubleExponentialOgee (float x, float a){
	  functionName = "Double-Exponential Ogee";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 

	  float y = 0;
	  if (x<=0.5f){
	    y = (pow(2.0f*x, 1.0f-a))/2.0f;
	  } 
	  else {
	    y = 1.0f - (pow(2.0f*(1.0f-x), 1.0f-a))/2.0f;
	  }
	  return y;
	}
	
	// BRYCE f13
	
	// Adapted from Paul Bourke 

	float m_Centerx;
	float m_Centery;
	float m_dRadius;

	//==============================================================
	public float function_CircularArcThroughAPoint (float x, float a, float b){
	  functionName = "Circular Arc Through a Given Point";
	  
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float min_param_b = 0.0f + EPSILON;
	  float max_param_b = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 
	  x = constrain(x, 0+EPSILON,1-EPSILON);
	  
	  float pt1x = 0;
	  float pt1y = 0;
	  float pt2x = a;
	  float pt2y = b;
	  float pt3x = 1;
	  float pt3y = 1;

	  if      (!IsPerpendicular(pt1x,pt1y, pt2x,pt2y, pt3x,pt3y) )	calcCircleFrom3Points (pt1x,pt1y, pt2x,pt2y, pt3x,pt3y);	
	  else if (!IsPerpendicular(pt1x,pt1y, pt3x,pt3y, pt2x,pt2y) )	calcCircleFrom3Points (pt1x,pt1y, pt3x,pt3y, pt2x,pt2y);	
	  else if (!IsPerpendicular(pt2x,pt2y, pt1x,pt1y, pt3x,pt3y) )	calcCircleFrom3Points (pt2x,pt2y, pt1x,pt1y, pt3x,pt3y);	
	  else if (!IsPerpendicular(pt2x,pt2y, pt3x,pt3y, pt1x,pt1y) )	calcCircleFrom3Points (pt2x,pt2y, pt3x,pt3y, pt1x,pt1y);	
	  else if (!IsPerpendicular(pt3x,pt3y, pt2x,pt2y, pt1x,pt1y) )	calcCircleFrom3Points (pt3x,pt3y, pt2x,pt2y, pt1x,pt1y);	
	  else if (!IsPerpendicular(pt3x,pt3y, pt1x,pt1y, pt2x,pt2y) )	calcCircleFrom3Points (pt3x,pt3y, pt1x,pt1y, pt2x,pt2y);	
	  else { 
	    return 0;
	  }
	  //------------------
	  // constrain
	  if ((m_Centerx > 0) && (m_Centerx < 1)){
	     if (a < m_Centerx){
	       m_Centerx = 1;
	       m_Centery = 0;
	       m_dRadius = 1;
	     } else {
	       m_Centerx = 0;
	       m_Centery = 1;
	       m_dRadius = 1;
	     }
	  }
	  
	  
	  //------------------
	  float y = 0;
	  if (x >= m_Centerx){
	    y = m_Centery - sqrt(sq(m_dRadius) - sq(x-m_Centerx)); 
	  } 
	  else{
	    y = m_Centery + sqrt(sq(m_dRadius) - sq(x-m_Centerx)); 
	  }
	  return y;
	}

	//==============================================================
	boolean IsPerpendicular(
	float pt1x, float pt1y,
	float pt2x, float pt2y,
	float pt3x, float pt3y)
	{
	  // Check the given point are perpendicular to x or y axis 
	  float yDelta_a = pt2y - pt1y;
	  float xDelta_a = pt2x - pt1x;
	  float yDelta_b = pt3y - pt2y;
	  float xDelta_b = pt3x - pt2x;

	  // checking whether the line of the two pts are vertical
	  if (abs(xDelta_a) <= EPSILON && abs(yDelta_b) <= EPSILON){
	    return false;
	  }
	  if (abs(yDelta_a) <= EPSILON){
	    return true;
	  }
	  else if (abs(yDelta_b) <= EPSILON){
	    return true;
	  }
	  else if (abs(xDelta_a)<= EPSILON){
	    return true;
	  }
	  else if (abs(xDelta_b)<= EPSILON){
	    return true;
	  }
	  else return false;
	}


	//==============================================================
	void calcCircleFrom3Points (
	float pt1x, float pt1y,
	float pt2x, float pt2y,
	float pt3x, float pt3y)
	{
	  float yDelta_a = pt2y - pt1y;
	  float xDelta_a = pt2x - pt1x;
	  float yDelta_b = pt3y - pt2y;
	  float xDelta_b = pt3x - pt2x;

	  if (abs(xDelta_a) <= EPSILON && abs(yDelta_b) <= EPSILON){
	    m_Centerx = 0.5f*(pt2x + pt3x);
	    m_Centery = 0.5f*(pt1y + pt2y);
	    m_dRadius = sqrt(sq(m_Centerx-pt1x) + sq(m_Centery-pt1y));
	    return;
	  }

	  // IsPerpendicular() assure that xDelta(s) are not zero
	  float aSlope = yDelta_a / xDelta_a; 
	  float bSlope = yDelta_b / xDelta_b;
	  if (abs(aSlope-bSlope) <= EPSILON){	// checking whether the given points are colinear. 	
	    return;
	  }

	  // calc center
	  m_Centerx = (aSlope*bSlope*(pt1y - pt3y) + bSlope*(pt1x + pt2x)- aSlope*(pt2x+pt3x) )/(2* (bSlope-aSlope) );
	  m_Centery = -1*(m_Centerx - (pt1x+pt2x)/2)/aSlope +  (pt1y+pt2y)/2;
	  m_dRadius = sqrt(sq(m_Centerx-pt1x) + sq(m_Centery-pt1y));
	}
	
	
	// BRYCE f14.
	
	// Bezier Shapers
	// adapted from BEZMATH.PS (1993)
	// by Don Lancaster, SYNERGETICS Inc. 
	// http://www.tinaja.com/text/bezmath.html

	//------------------------------------------------------------------
	public float function_QuadraticBezier (float x, float a, float b){
	  functionName = "Quadratic Bezier";

	  float min_param_a = 0.0f;
	  float max_param_a = 1.0f;
	  float min_param_b = 0.0f;
	  float max_param_b = 1.0f;
	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 

	  if (a == 0.5){
	    a += EPSILON;
	  }
	  // solve t from x (an inverse operation)
	  float om2a = 1.0f - 2.0f*a;
	  float t = (sqrt(a*a + om2a*x) - a)/om2a;
	  float y = (1.0f-2.0f*b)*(t*t) + (2*b)*t;
	  return y;
	}


	//------------------------------------------------------------------
	public float function_CubicBezier (float x, float a, float b, float c, float d){
	  functionName = "Cubic Bezier";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float min_param_b = 0.0f;
	  float max_param_b = 1.0f;
	  float min_param_c = 0.0f + EPSILON;
	  float max_param_c = 1.0f - EPSILON;
	  float min_param_d = 0.0f;
	  float max_param_d = 1.0f;
	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 
	  c = constrain(c, min_param_c, max_param_c); 
	  d = constrain(d, min_param_d, max_param_d); 

	  //-------------------------------------------
	  float y0a = 0.00f; // initial y
	  float x0a = 0.00f; // initial x 
	  float y1a = b;    // 1st influence y   
	  float x1a = a;    // 1st influence x 
	  float y2a = d;    // 2nd influence y
	  float x2a = c;    // 2nd influence x
	  float y3a = 1.00f; // final y 
	  float x3a = 1.00f; // final x 

	  float A =   x3a - 3*x2a + 3*x1a - x0a;
	  float B = 3*x2a - 6*x1a + 3*x0a;
	  float C = 3*x1a - 3*x0a;   
	  float D =   x0a;

	  float E =   y3a - 3*y2a + 3*y1a - y0a;    
	  float F = 3*y2a - 6*y1a + 3*y0a;             
	  float G = 3*y1a - 3*y0a;             
	  float H =   y0a;

	  // Solve for t given x (using Newton-Raphelson), then solve for y given t.
	  // Assume for the first guess that t = x.
	  float currentt = x;
	  int nRefinementIterations = 5;
	  for (int i=0; i<nRefinementIterations; i++){
	    float currentx = xFromT (currentt, A,B,C,D); 
	    float currentslope = slopeFromT (currentt, A,B,C);
	    currentt -= (currentx - x)*(currentslope);
	    currentt = constrain(currentt, 0,1.0f);
	  } 
	 
	  //------------
	  float y = yFromT (currentt,  E,F,G,H);
	  return y;
	}


	//==========================================================
	public float slopeFromT (float t, float A, float B, float C){
	  float dtdx = 1.0f/(3.0f*A*t*t + 2.0f*B*t + C); 
	  return dtdx;
	}
	//==========================================================
	public float xFromT (float t, float A, float B, float C, float D){
	  float x = A*(t*t*t) + B*(t*t) + C*t + D;
	  return x;
	}
	//==========================================================
	public float yFromT (float t, float E, float F, float G, float H){
	  float y = E*(t*t*t) + F*(t*t) + G*t + H;
	  return y;
	}
	
	
	// BRYCE f15
	
	
	// Parabola (Quadratic) Through a Point
	//------------------------------------------------------------------
	public float function_ParabolaThroughAPoint (float x, float a, float b){
	  functionName = "Quadratic Through a Given Point";
	  
	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float min_param_b = 0.0f;
	  float max_param_b = 1.0f;
	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 
	  
	  float A = (1-b)/(1-a) - (b/a);
	  float B = (A*(a*a)-b)/a;
	  float y = A*(x*x) - B*(x);
	  y = constrain(y, 0,1); 
	  
	  return y;
	}


	//------------------------------------------------------------------
	// generalized damped sinusoid
	public float function_DampedSinusoid (float x, float a){
	  // http://en.wikipedia.org/wiki/Damped_sine_wave
	  functionName = "Generalized Damped Sinusoid";
	  
	  float omega  = 100*a;
	  float lambda = -6.90775527f; // ln(lambda) = 0.001 // decay constant
	  float phi = 0;
	  float e = 2.718281828459045f;
	  
	  float t = x;
	  float y = pow(e, lambda*t) * cos(omega*t + phi);
	  return y;
	}

	//------------------------------------------------------------------
	// generalized damped sinusoid
	public float function_DampedSinusoidReverse (float x, float a){
	  // http://en.wikipedia.org/wiki/Damped_sine_wave
	  functionName = "Generalized Damped Sinusoid (Reverse)";
	  
	  float omega = 100*a;
	  float lambda = -6.90775527f; // ln(lambda) = 0.001
	  float phi = 0;
	  float e = 2.718281828459045f;
	  
	  float t = 1.0f-x;
	  float y = pow(e, lambda*t) * cos(omega*t + phi);
	  return y;
	}
	
	// BRYCE f16
	

	//------------------------------------------------------------------
	public float function_CubicBezierThrough2Points (float x, float a, float b, float c, float d){
	  functionName = "Cubic Bezier (Nearly) Through 2 Points"; 

	  float y = 0;

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float min_param_b = 0.0f + EPSILON;
	  float max_param_b = 1.0f - EPSILON;
	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b); 

	  float x0 = 0;  
	  float y0 = 0;
	  float x4 = a;  
	  float y4 = b;
	  float x5 = c;  
	  float y5 = d;
	  float x3 = 1;  
	  float y3 = 1;
	  float x1,y1,x2,y2; // to be solved.

	  float t1 = 0.3f;
	  float t2 = 0.7f;

	  float B0t1 = B0(t1);
	  float B1t1 = B1(t1);
	  float B2t1 = B2(t1);
	  float B3t1 = B3(t1);
	  float B0t2 = B0(t2);
	  float B1t2 = B1(t2);
	  float B2t2 = B2(t2);
	  float B3t2 = B3(t2);

	  float ccx = x4 - x0*B0t1 - x3*B3t1;
	  float ccy = y4 - y0*B0t1 - y3*B3t1;
	  float ffx = x5 - x0*B0t2 - x3*B3t2;
	  float ffy = y5 - y0*B0t2 - y3*B3t2;

	  x2 = (ccx - (ffx*B1t1)/B1t2) / (B2t1 - (B1t1*B2t2)/B1t2);
	  y2 = (ccy - (ffy*B1t1)/B1t2) / (B2t1 - (B1t1*B2t2)/B1t2);
	  x1 = (ccx - x2*B2t1) / B1t1;
	  y1 = (ccy - y2*B2t1) / B1t1;

	  x1 = constrain(x1, 0+EPSILON,1-EPSILON); 
	  x2 = constrain(x2, 0+EPSILON,1-EPSILON); 

	  y = function_CubicBezier (x, x1,y1, x2,y2);
	  y = constrain(y,0,1); 
	  
	  functionName = "Cubic Bezier (Nearly) Through 2 Points";  
	  return y;

	}

	//==============================================================
	float B0 (float t){
	  return (1-t)*(1-t)*(1-t);
	}
	float B1 (float t){
	  return  3*t* (1-t)*(1-t);
	}
	float B2 (float t){
	  return 3*t*t* (1-t);
	}
	float B3 (float t){
	  return t*t*t;
	}
	float  findx (float t, float x0, float x1, float x2, float x3){
	  return x0*B0(t) + x1*B1(t) + x2*B2(t) + x3*B3(t);
	}
	float  findy (float t, float y0, float y1, float y2, float y3){
	  return y0*B0(t) + y1*B1(t) + y2*B2(t) + y3*B3(t);
	}


	// BRYCE f17.
	
	
	//------------------------------------------------------------------
	public float function_DoubleCircularOgee (float x, float a){
	  functionName = "Double-Circular Ogee";
	  
	  float min_param_a = 0.0f;
	  float max_param_a = 1.0f;
	  
	  a = constrain(a, min_param_a, max_param_a); 
	  float y = 0;
	  if (x<=a){
	    y = sqrt(sq(a) - sq(x-a));
	  } 
	  else {
	    y = 1 - sqrt(sq(1-a) - sq(x-a));
	  }
	  return y;
	}


	public float function_DoubleSquircularOgee (float x, float a, int n){
	  // http://en.wikipedia.org/wiki/Squircle
	  functionName = "Double-Squircular Ogee";
	  
	  float min_param_a = 0.0f;
	  float max_param_a = 1.0f;
	  a = constrain(a, min_param_a, max_param_a); 
	  float pown = 2.0f * n; 
	  
	  float y = 0;
	  if (x<=a){
	    y = pow( pow(a,pown) - pow(x-a, pown), 1.0f/pown);
	  } 
	  else {
	    y = 1.0f - pow( pow(1-a,pown) - pow(x-a, pown), 1.0f/pown);
	  }
	  return y;
	}
	
	// BRYCE f18
	
	
	// Modified (Normalized) Logistic Sigmoid 

	//------------------------------------------------------------------
	public float function_GeneralSigmoidLogitCombo (float x, float a, float b){
	  
	  float y = 0; 
	  if (a < 0.5f){
	    // Logit
	    float dy = b - 0.5f;
	    y = dy + function_NormalizedLogit (x, 1.0f-(2.0f*a));
	  } else {
	    // Sigmoid
	    float dx = b - 0.5f;
	    y = function_NormalizedLogisticSigmoid (x+dx, (2.0f*(a-0.5f)));
	  }
	  
	  functionName = "General Sigmoid-Logit Combination";
	  y = constrain(y, 0,1); 
	  return y;
	}






	//------------------------------------------------------------------
	public float function_NormalizedLogisticSigmoid (float x, float a) {
	  functionName = "Normalized Logistic Sigmoid";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float emph = 5.0f;

	  a = constrain(a, min_param_a, max_param_a);
	  a = (1.0f/(1.0f-a) - 1.0f); 
	  a = emph * a;

	  float y    = 1.0f / (1.0f + exp(0 - (x-0.5f)*a    ));
	  float miny = 1.0f / (1.0f + exp(  0.5f*a    ));
	  float maxy = 1.0f / (1.0f + exp( -0.5f*a    ));
	  y = map(y, miny, maxy, 0, 1); 
	  return y;
	}


	//------------------------------------------------------------------
	public float function_NormalizedLogit (float x, float a) {
	  // http://en.wikipedia.org/wiki/Logit
	  functionName = "Normalized Logit Function";

	  float min_param_a = 0.0f + EPSILON;
	  float max_param_a = 1.0f - EPSILON;
	  float emph = 5.0f;

	  a = constrain(a, min_param_a, max_param_a); 
	  a = (1/(1-a) - 1); 
	  a = emph * a;

	  float minx = 1.0f / (1.0f + exp(  0.5f*a    ));
	  float maxx = 1.0f / (1.0f + exp( -0.5f*a    ));
	  x = map(x, 0,1, minx, maxx);

	  float y = log ( x / (1.0f - x)) ;
	  y *= 1.0f/a; 
	  y += 0.5f;

	  y = constrain (y, 0, 1); 
	  return y;
	}
	
	// BRYCE f19
	
	// A Flexible Quartic Equation

	//------------------------------------------------------------------
	public float function_NiftyQuartic (float x, float a, float b) {
	  functionName = "Quartic";

	  float min_param_a = 0.0f;
	  float max_param_a = 1.0f;
	  float min_param_b = 0.0f;
	  float max_param_b = 1.0f;
	  a = constrain(a, min_param_a, max_param_a); 
	  b = constrain(b, min_param_b, max_param_b);

	  a = 1.0f-a;
	  float w = (1-2*a)*(x*x) + (2*a)*x;
	  float y = (1-2*b)*(w*w) + (2*b)*w;
	  return y;
	}


	public float function_Identity (float x) {
	  functionName = "Identity Function";
	  return x;
	}

	public float function_Inverse (float x) {
	  functionName = "Inverse Function";
	  return 1.0f-x;
	}

	public float function_BoxcarFunction (float x){
	  // http://mathworld.wolfram.com/BoxcarFunction.html
	  // Also see http://mathworld.wolfram.com/HeavisideStepFunction.html
	  functionName = "Boxcar Function";
	  if (x < 0.5){
	    return 0.0f; 
	  } else if (x > 0.5){
	    return 1.0f; 
	  } 
	  return 0.5f;
	}
	
	
}