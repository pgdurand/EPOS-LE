package epos.ui.view.ppane;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;



public interface Controller<V extends View<V,C>, C extends Content> extends ControlledUnit<V,C>, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{
	public static final int NONE=0;
	public static final int BUTTON1=1;
	public static final int BUTTON2=2;
	public static final int BUTTON3=4;
	public static final int BUTTONS=BUTTON1 | BUTTON2 | BUTTON3;	
	public static final int PRESSED=8;
	public static final int RELEASED=16;
	public static final int CLICKED=PRESSED & RELEASED;
	public static final int MOVE=32;
	public static final int WHEEL=64;
	public static final int DRAG=MOVE&PRESSED;
		
	public int getMouseDisableActions();	
}