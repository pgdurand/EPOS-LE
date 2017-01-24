package epos.ui.view.ppane;

import java.util.List;

import javax.swing.KeyStroke;

public interface ControlledUnit<V extends View<V,C>, C extends Content>{
	public PowerPane<V,C> getParent();
	public List<KeyStroke> getKeyStrokes();
	public boolean conflicts(ControlledUnit<V,C> otherUnit);
	public void disable();
}
