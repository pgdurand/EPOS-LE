/*
 * Created on 22.05.2006
 */
package epos.ui.view.ppane;

import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;

import org.jdesktop.application.ProxyActions;

@ProxyActions({"enable", "action"})
public interface Tool<V extends View<V,C>, C extends Content>{
    /**
     * Update this Tool
     * 
     * @param parent
     */
    public void update(PowerPane<V,C> parent);
            	            
    /**
     * Sets the parent of the registrable.
     * this is important to do the enabeling/disabbeling of absolute registrables.
     * 
     * @param parent
     */
    public void setParentPP(PowerPane<V,C> parent);
    
    /**
     * 
     * @return Returns the parent mapper
     */
    public PowerPane<V,C> getParentPP(); 
    		
	/**
	 * Sets the unique id of the underlying extension
	 * @param uniqueId
	 */
	public void setExtension(String uniqueId);
	
	/**
	 * Returns the unique id of the underlying extension or null.
	 * 
	 * @return extension id or null
	 */
	public String getExtension();
	/**
	 * Returns all {@link Action}s associated with this tool
	 * 
	 * @return actionMap of all actions
	 */
	public ActionMap getActions();
	
	/**
	 * Returns {@link Action}s that should be embedded into the toolbar
	 * @return toolbar actions
	 */
	public List<Action> getToolbarActions();
	
	/**
	 * Returns {@link Action}s that should be included into the menu
	 * @return menu actions
	 */
	public List<Action> getMenuActions();
	/**
	 * Returns a list of components that will be embedded into the dock 
	 * @return list of components or null
	 */
	public List<JComponent> getComponents();

	/**
	 * Returns an actions that is associated with a given component
	 * 
	 * @param component
	 * @return
	 */
	public Action getComponentAction(JComponent component);
	/**
	 * Called by the parent after the tool was initialized
	 */
	public void initialized();

}
