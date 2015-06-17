package template.tool;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import processing.app.Editor;



public class DisplayFrame extends javax.swing.JFrame {
    public DisplayFrame(Editor editor){
        this.setSize(1200, 600); //The window Dimensions
        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setBounds(20, 20, 1200, 600);
        gui_mainControls.editor = editor;


        // Initialize and start the Modular Sketches.

        final gui_mainControls main_controls = new gui_mainControls();
        main_controls.init(); // Needs to go first.        
        
        panel.add(main_controls);
        
        this.add(panel);
        
        this.setVisible(true);
        
        
        // Resizing hack to fix Java windowing bug.
        this.setSize(1201, 600);
        this.setSize(1200 + 50, 600 + 60);
        
        // Shut Down.
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
            	
            	// We need to be sure to shut the main controls down as well.
            	main_controls.stop();
            	main_controls.dispose();
                //System.exit(0);
            	
            	dispose();
            }
        } );
    }
}