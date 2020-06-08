/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentgui;

/**
 *
 * @author dbson
 */
public class CompletionThread extends Thread{
    private GridPanel toView;
    
    @Override
    public void run() {
        double completionPercent = 0.0;
        
        while (completionPercent != 100.0) {
            completionPercent = toView.checkCompletion();
        }
        
        //TODO: Give Completion
    }
}
