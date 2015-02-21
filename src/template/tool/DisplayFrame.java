package template.tool;

import processing.app.Editor;



public class DisplayFrame extends javax.swing.JFrame {
    public DisplayFrame(Editor editor){
        this.setSize(600, 600); //The window Dimensions
        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBounds(20, 20, 600, 600);
        GolanSketch.editor = editor;
        processing.core.PApplet sketch = new GolanSketch();
        panel.add(sketch);
        this.add(panel);
        sketch.init(); //this is the function used to start the execution of the sketch
        this.setVisible(true);
    }
}