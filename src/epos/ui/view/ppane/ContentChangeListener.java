/*
 * Created on Sep 21, 2004
 */
package epos.ui.view.ppane;

import java.util.EventListener;

/**
 * @author Thasso
 */
public interface ContentChangeListener extends EventListener {
	
	public void contentChange(ContentChangeEvent event); 
}
