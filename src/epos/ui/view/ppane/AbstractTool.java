package epos.ui.view.ppane;

import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.jdesktop.application.AbstractBean;

/*import epos.Epos;
import epos.ui.annotations.AnnotationUtils;
import epos.ui.util.PPAction;
*/
public abstract class AbstractTool<V extends View<V,C>, C extends Content> extends AbstractBean implements Tool<V,C>
{ 
			
	protected Logger log = Logger.getLogger(getClass());
	protected PowerPane<V,C> parent;
	protected String extension;
	protected List<Action> toolbarActions;
	private boolean actionsInitialized;
	protected List<JComponent> components;
	protected Map<JComponent, Action> componentActions;

	
	public AbstractTool(){
		super();
	}
	
	public PowerPane<V, C> getParentPP() {
		return parent;
	}
	
	public void setParentPP(PowerPane<V, C> parent) {
		this.parent = parent;
	}
	
	public void update(PowerPane<V, C> parent) {
		this.parent = parent;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public void setExtension(String uniqueId) {
		this.extension = uniqueId;
	}	
		
	public ActionMap getActions() {		
		/*if(!actionsInitialized) initializeActions();
		return Epos.getEpos().getContext().getActionMap(this);*/
		return null;
	}
		
	protected void initializeActions() {
		//toolbarActions = AnnotationUtils.getToolbarActions(this);
		actionsInitialized = true;
	}

	public List<Action> getMenuActions() {
		return null;
	}
	
	public List<Action> getToolbarActions() {
		if(!actionsInitialized) initializeActions();
		return toolbarActions;
	}	
	
	public List<JComponent> getComponents() {
		if(components == null) initializeComponents();
		return null;//components;
	}
	
	public Action getComponentAction(JComponent component){
		if(components == null) initializeComponents();
		return null;//componentActions.get(component);
	}
	
	protected void initializeComponents(){
		/*Object[] cpms= AnnotationUtils.getAnnotatedComponents(this);		
		components = (List<JComponent>) cpms[0];
		componentActions = (Map<JComponent, Action>) cpms[1];*/		
	}
	
	public void initialized(){
		
	}
}
