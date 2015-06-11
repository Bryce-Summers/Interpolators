# Interpolators
A Processing 3 Tool that extends the work of Golan Levin's pattern master project.

Please see: https://github.com/golanlevin/Pattern_Master

Current Status
============

Almost Done.

Functional Visualization:
----------
Done, may need some redesign.

Selection of Functions
------------

Done, I might want to implement the drawing of the functions as icons next to their names.

Features of function code Exportation to Processing 3.
--------------

Example comments are generated with the code of each of the functions. They contain the parameters that the user saw visualized inside of the tool.
Comment Only Option works. Comments are generated correctly. Code is justified apprpiatly without ugly tabs.

The New Tab option works. When using this option, the user is prompted to name the new window upon exportation.

Automatic Output Clamping is a work in progress.

Automatic x / y flipping adds conveniance code to the 

Current Visual appearance
==================

![alt text](https://github.com/Bryce-Summers/Interpolators/blob/master/Screenshots/interpolators_screenshot2.png "Visual Appearance, 5/4/2015")


Usage Notes
-------------

It the tool every seems to not be drawing properly in the Graphic User Interface Window, then please try resizing the window and the drawing should go back to normal.

Make sure you have written the setup() and/or draw() function, or Proccessing 3.0 will tell you a weird error story about the left and right Parentheses in the interpolation curves functions. ('(' annd ')').


Draw the Mouse with the left or right arrow keys to manipulate parameters.
Use the Scroll Wheen to modify the value of parameter 'n'.
