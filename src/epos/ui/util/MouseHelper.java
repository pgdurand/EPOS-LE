package epos.ui.util;

import java.awt.event.MouseEvent;

/**
 * A Helper class to make desission about mouse clicks. This also work properly on a Mac!
 * 
 * <br>
 * Beside this, this halper can be switched to a lefty mouse ;) This is done using a system property "mousehelper.lefty=true".
 * @author thasso
 *
 */
public class MouseHelper {
	public static String LEFTY_PROPERTY = "mousehelper.lefty";
	public static boolean LEFTY = false;
	static{
		if(System.getProperty("mousehelper.lefty") != null){
			String value = System.getProperty(LEFTY_PROPERTY);
			if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("on")){
				LEFTY = true;
			}
		}
	}
	/**
	 * 
	 * 
	 * @param e the mouse event
	 * @return Returns true if the right mouse button was pressed (or ctrl-click on a mac)
	 */
	public static boolean isRightClick(MouseEvent e){
		if(LEFTY)
			return isLeftClick(e);
		boolean b =  e.getButton() == MouseEvent.BUTTON3 || e.isPopupTrigger();
		return b;

	}
	/**
	 * 
	 * @param e the mouse event
	 * @return Returns true if the left mouse button was pressed
	 */
	public static boolean isLeftClick(MouseEvent e){
		if(LEFTY)
			return isRightClick(e);
		boolean b =(e.getButton() == MouseEvent.BUTTON1 && ! e.isPopupTrigger());
		return b;
	}

}
