/**
 * Allows the user to generate code for easing functions.
 *
 * Copyright (c) 2015 Bryce Summers and Golan Levin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 */

package template.tool;
 
import processing.app.*;
import processing.app.tools.*;
   
public class InterpolationCurveTool implements Tool
{
 
	// when creating a tool, the name of the main class which implements Tool
	// must be the same as the value defined for project.name in your build.properties
 
 
	// The text editor that the user is currently using.
	Editor editor;

	public void init(Editor editor)
	{
		this.editor = editor;
	}

	public void run()
	{
		new DisplayFrame(editor).setVisible(true);
	}

	public String getMenuTitle()
	{
	    return "Interpolation Curves";
	}
	
 }



