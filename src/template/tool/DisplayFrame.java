package template.tool;

import processing.app.Editor;



public class DisplayFrame extends javax.swing.JFrame {
    public DisplayFrame(Editor editor){
        this.setSize(1200, 600); //The window Dimensions
        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBounds(20, 20, 1200, 600);
        gui_mainControls.editor = editor;
                

        // Initialize and start the Modular Sketches.
        
        gui_mainControls main_controls = new gui_mainControls();
        main_controls.init(); // Needs to go first.        
                
        panel.add(main_controls);
        
        this.add(panel);
        
        this.setVisible(true);
    }
}