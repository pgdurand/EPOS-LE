package epos.ui.view.ppane;

import java.awt.Graphics;
import java.util.List;

import javax.swing.KeyStroke;

public abstract class AbstractRenderer<V extends View<V,C>, C extends Content> implements Renderer<V,C> {
	private PowerPane<V, C> parent;
	public AbstractRenderer(PowerPane<V, C> parent){
		super();
		this.parent = parent;
	}
	protected int layerLevel = 0;
	public int getLayerLevel() {
		return layerLevel;
	}
	public void setLayerLevel(int level) {
		this.layerLevel = level;
	}
	public abstract void render(Graphics g);
	public abstract boolean conflicts(ControlledUnit<V, C> otherUnit);
	public abstract void disable();
	public List<KeyStroke> getKeyStrokes() {
		return null;
	}
	public PowerPane<V, C> getParent() {
		return parent;
	}
}
