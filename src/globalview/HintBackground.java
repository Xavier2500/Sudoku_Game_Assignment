package globalview;

import extrabuttons.HintButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel that controls the hint functionality of the game. Holds information on
 * the number of hints left to the user.
 * 
 * @author dbson
 */
public class HintBackground extends JPanel{
    private GridPanel grid;
    
    private JPanel hint;
    private JPanel totalHintPanel;
    
    private JLabel hintsLeft;
    
    private JButton hintReturn;
    private HintButton[][] hintButtons = new HintButton[SudokuGrid.NUMBER_OF_ROWS][SudokuGrid.ROW_LENGTH];
    
    private BufferedImage backgroundImage;
    
    private int imageWidth;
    private int imageHeight;
    private int hintsAvailable = 3;
    
    private static final int GRIDPANEL_SPACING = 27;
    
    public HintBackground(GridPanel grid) {
        this.grid = grid;
        
        try {
            this.backgroundImage = ImageIO.read(new File("Sudoku_Background.jpg"));
            
            this.imageWidth = this.backgroundImage.getWidth();
            this.imageHeight = this.backgroundImage.getHeight();
            
            this.setHintPanel();
            this.hint.setLocation(15, 15);
            this.hint.setSize((this.imageWidth - GRIDPANEL_SPACING), (this.imageHeight - GRIDPANEL_SPACING));
            this.hint.setVisible(true);
            
            this.add(this.hint);
            this.add(this.totalHintPanel);
            
        } catch (IOException ex) {
            System.err.println("Failed to find image file.");
        }
    }
    
    /**
     * Constructs the layout of the hint buttons, to fit the position of the
     * text fields stored in the grid panel. Allows for a seamless transition
     * between the grid panel and the hint panel.
     */
    public void setHintPanel() {
        this.hint = new JPanel();
        
        this.hint.setLayout(new GridLayout(9, 9, 25, 25));
        this.hint.setBackground(new Color(0, 0, 0, 0));
        
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                int value = this.grid.getResetGrid().getCell(i, j);
                
                HintButton nextCell = new HintButton("", i, j);
                nextCell.setMargin(new Insets(0, 0, 0, 0));
                nextCell.setFont(new Font("Arial", Font.PLAIN, 20));
                nextCell.setHorizontalAlignment(SwingConstants.CENTER);
                
                String sValue = Integer.toString(value);
                
                if (value == 0) {
                    sValue = Integer.toString(grid.getWorkingGrid().getCell(i, j));
                    if (sValue.equalsIgnoreCase("0")) {
                        sValue = "";
                    }
                } else {
                    nextCell.setEnabled(false);
                }
                
                nextCell.setText(sValue);
                this.hintButtons[i][j] = nextCell;
                
                this.hint.add(this.hintButtons[i][j]);
            }
        }
        
        this.setHintControls();
    }
    
    /**
     * Sets up the hint control panel, which contains a label informing the user
     * of the number of hints they have left, and a button for the user to exit
     * the hint functionality without using a hint.
     */
    private void setHintControls() {
        int numberHints = this.hintsAvailable;
        String hintsLeftText = ("You have " + numberHints
                + ((numberHints == 1) ? " hint" : " hints") + " left.");
        
        this.hintsLeft = new JLabel(hintsLeftText);
        
        this.hintReturn = new JButton("Return to Grid");
        
        int xPos = this.hint.getX();
        int yPos = (this.hint.getY() + this.imageHeight + 10);
        
        this.totalHintPanel = new JPanel();
        this.totalHintPanel.setLayout(new GridLayout(0, 2));
        this.totalHintPanel.setSize(this.imageWidth, 30);
        this.totalHintPanel.setLocation(xPos, yPos);
        this.totalHintPanel.setVisible(true);
        
        this.totalHintPanel.add(this.hintsLeft);
        this.totalHintPanel.add(this.hintReturn);
    }
    
    /**
     * Update the view of the hint grid to represent all user given values at
     * the time of view transition.
     */
    public void updateHintView() {
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                HintButton currentField = this.hintButtons[i][j];
                String cellView = ("" + currentField.getText());
                String cellValue = ("" + this.grid.getWorkingGrid().getCell(i, j));
                
                if (!cellView.equalsIgnoreCase(cellValue)) {
                    if (cellValue.equalsIgnoreCase("0")) {
                        cellValue = "";
                    }
                    currentField.setText(cellValue);
                }
            }
        }
    }
    
    /**
     * Used to update the hint label, giving the accurate number of hints left.
     */
    public void updateHintLabel() {
        int numberHints = this.hintsAvailable;
        String hintsLeftText = ("You have " + numberHints
                + ((numberHints == 1) ? " hint" : " hints") + " left.");
        
        this.hintsLeft.setText(hintsLeftText);
    }
    
    /**
     * Sets a cell from the working grid to it's correct value, stored in the
     * system.
     * 
     * @param row The row of the selected cell.
     * @param column The column of the selected cell.
     * @return Number of hints still available.
     */
    public int useHint(int row, int column) {
        this.hintsAvailable--;
        
        String correctValue = Integer.toString(this.grid.getCompleteGrid().getCell(row, column));
        this.grid.getWorkingGrid().setCell(row, column, correctValue);
        this.grid.updateView();
        
        return this.hintsAvailable;
    }
    
    /**
     * Turn off all buttons in the hint panel, to ensure they cannot be pressed
     * after the maximum number of hints have been used.
     */
    public void deactivateAllHints() {
        for (int i = 0; i < SudokuGrid.NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < SudokuGrid.ROW_LENGTH; j++) {
                this.hintButtons[i][j].setEnabled(false);
            }
        }
    }
    
    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getHintsAvailable() {
        return hintsAvailable;
    }

    public HintButton[][] getHintButtons() {
        return hintButtons;
    }

    public JButton getHintReturn() {
        return hintReturn;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(this.backgroundImage, 0, 0, this);
    }
}
