/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package globalview;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Sets up the grid panel onto a panel with the sudoku background.
 * 
 * @author dbson
 */
public class SudokuBackground extends JPanel {
    private GridPanel grid;
    
    private BufferedImage backgroundImage;
    
    private int imageWidth;
    private int imageHeight;
    
    private static final int GRIDPANEL_SPACING = 27;
    
    public SudokuBackground(GridPanel grid) {
        try {
            this.backgroundImage = ImageIO.read(new File("Sudoku_Background.jpg"));
            
            this.imageWidth = this.backgroundImage.getWidth();
            this.imageHeight = this.backgroundImage.getHeight();
            
            this.grid = grid;
            this.grid.setLocation(15, 15);
            this.grid.setSize((this.imageWidth - GRIDPANEL_SPACING), (this.imageHeight - GRIDPANEL_SPACING));
            this.grid.setVisible(true);

            this.add(this.grid);
            
        } catch (IOException ex) {
            System.err.println("Failed to find image file.");
        }
    }
    
    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
       g.drawImage(this.backgroundImage, 0, 0, this);
    }
}
