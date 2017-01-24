package epos.ui.view.ppane;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import javax.swing.KeyStroke;

public abstract class AbstractController<V extends View<V,C>, C extends Content> implements Controller<V,C>{

	private PowerPane<V, C> parent;

	public AbstractController(PowerPane<V, C> parent){
		super();
		this.parent = parent;
	}

	public abstract int getMouseDisableActions();
	public boolean conflicts(ControlledUnit<V, C> otherUnit){
		if(otherUnit instanceof Controller){
			return (((Controller)otherUnit).getMouseDisableActions() & getMouseDisableActions()) > 0;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<KeyStroke> getKeyStrokes(){
		return null;
	}
	public abstract void disable();
	
	public PowerPane<V, C> getParent() {
		return parent;
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}
	public void mouseWheelMoved(MouseWheelEvent e) {}
	public void keyPressed(KeyEvent e){};
	public void keyReleased(KeyEvent e) {};
	public void keyTyped(KeyEvent e) {};	
}
