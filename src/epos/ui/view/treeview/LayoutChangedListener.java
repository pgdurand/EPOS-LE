/*
 * Created on Aug 23, 2003
 */
package epos.ui.view.treeview;

import java.util.EventListener;


/**
 * EventListener for Layout changes.
 * 
 * @author Thasso
  */
public interface LayoutChangedListener extends EventListener {
	public void layoutChanged(LayoutChangedEvent e);
}
