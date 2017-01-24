/*
 * Created on Sep 21, 2004
 */
package epos.ui.view.ppane;

import java.util.EventObject;

/**
 * @author Thasso
 */
public class ContentChangeEvent extends EventObject {

	/**
	 * @param source
	 */
	public ContentChangeEvent(Content source) {
		super(source);		
	}

}
