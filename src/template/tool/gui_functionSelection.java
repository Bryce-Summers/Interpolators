package template.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import processing.core.PApplet;

import java.lang.reflect.Method;

public class gui_functionSelection
{
	
	// We need to be able to receive and send data to the main controls.
	private gui_mainControls mainDisplay;
	
	public FunctionList button_list;

		
	// -- Private Classes.
	private class group_Button
	{
		// A map from strings of this button's type to ids into the function array.
		LinkedHashSet<listButton> myFunctions;
		
		
		// Needs some bounds.
	}
	
	
	// A Class that displays a set of list buttons.
	public class FunctionList implements Iterable<listButton>
	{
		public ArrayList<listButton> myListButtons = new ArrayList<listButton>();
		
		public void addFunction(int index, String message)
		{
			myListButtons.add(new listButton(index, message));
		}

		@Override
		public Iterator<listButton> iterator()
		{
			return myListButtons.iterator();
		}
	}
	
	// Clicking on a list button should update the main display pane.
	public class listButton
	{
		// The name of this function.
		String function_name;
		
		// The ID of this function.
		Integer myID;
		
		public listButton(int index, String message)
		{
			function_name = message;
			myID = index;
		}
		
		//action --> update the id of the mainDisplay.
	}
	
	
	
	
	
	// -- Constructor.
	public gui_functionSelection(gui_mainControls display)
	{
		this.mainDisplay = display;
		createButtons();
		
	}
	
	public void createButtons()
	{
		/*
		 * Sigmoids.
		 * Ogees
		 * Ease-in
		 * ease-out
		 * penners
		 * staircases
		 * windows
		 * halfwindows
		 * general
		 */
		
		button_list = new FunctionList();
		
		ArrayList<Method> functions = mainDisplay.functionMethodArraylist;
		
		int len = functions.size();
		for(int i = 0; i < len; i++)
		{
			Method M = functions.get(i);
			button_list.addFunction(i, M.getName());
		}
		
	}
	
}
