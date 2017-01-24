package epos.ui.view.treeview;


import java.awt.Point;

import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ViewportChangeListener implements ChangeListener{
    private JViewport viewPort;
    private Point location;
    private boolean change = false;
 
    public ViewportChangeListener(JViewport vport){
        super();
        this.viewPort = vport;
    }
    public void setLocation(Point location){
        this.location = location;
        change = true;
    }
 
    public void stateChanged(ChangeEvent e) {
        if(viewPort.isValid() && change && location != null){
            viewPort.setViewPosition(location);            
            change = false;
        }
    }
}

