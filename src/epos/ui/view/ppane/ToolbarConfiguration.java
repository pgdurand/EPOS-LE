package epos.ui.view.ppane;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import epos.Epos;
//import epos.ui.util.ComponentFactory;

public class ToolbarConfiguration {
	public static final String SEPERATOR = "Seperator--;;--";
	
	private String viewer;
	private List<String> usedTools;
	transient private JToolBar toolbar;
	transient private Map<String, Action> allActions;
	transient private PowerPane<?, ?> parent; 
	
	
	public ToolbarConfiguration(){
		super();
	}
	
	public ToolbarConfiguration(String viewer){
		super();
		this.viewer = viewer;
	}
	
	public void setParent(PowerPane<?, ?> parent){
		this.parent = parent;
	}
	
	@org.jdesktop.application.Action
	public void showConfigDialog(){
		System.out.println("show dialog");
		List<Action> used = new ArrayList<Action>();
		List<Action> unused = new ArrayList<Action>();
		for (String key : allActions.keySet()) {
			if(!usedTools.contains(key)){
				unused.add(allActions.get(key));				
			}
		}
		for (String s : usedTools) {
			Action a = allActions.get(s);
			if(s.equals(SEPERATOR)){
				a = new ToolbarConfigPanel.Seperator();
			}
			used.add(a);
		}
		ToolbarConfigPanel.showDialog(used, unused, parent, this);
	}
	
	public void setActions(List<Action> availableToolbarActions){
		this.allActions = new HashMap<String, Action>();
		for (Action action : availableToolbarActions) {
			String name = (String) action.getValue(Action.NAME);
			if(name == null || name.equals("")) name = action.toString();
			this.allActions.put(name, action);			
		}		
	}
		
	public JToolBar getToolbar(){
		if(toolbar == null) initToolbar();
		return toolbar;
	}
	public void reinitToolbar() {
		initToolbar();
	}
	
	protected void initToolbar() {
		if(toolbar == null){
			toolbar = new JToolBar();
		}else{
			toolbar.removeAll();
		}

		if(usedTools == null && allActions != null){
			usedTools = new ArrayList<String>();
			for (String name : allActions.keySet()) {
				usedTools.add(name);
			}
		}

		if(usedTools != null && allActions != null){
			toolbar.setVisible(true);
			for (String name : usedTools) {
				if(name == null) continue;
				if(name.equals(SEPERATOR)){
					toolbar.addSeparator();
				}else{
					if(allActions.get(name) != null){
						//toolbar.add(ComponentFactory.getToolbarButton(allActions.get(name)));
					}else{					
						Logger.getLogger(getClass()).warn("Action key "+name+" not found in action map!");
					}
				}
			}
		}else{
			toolbar.setVisible(false);
		}
		toolbar.revalidate();
		toolbar.repaint();
	}
	
	public void save(){
		System.out.println("saving toolbar stuff....." + viewer);
		if(viewer == null) return;		
		try {
			Epos.getAppContext().getLocalStorage().save(this,"toolbar-" + viewer+".toolbar");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done...");
	}
	
	public static ToolbarConfiguration getConfiguration(String viewer, URL defaultFile){
		ToolbarConfiguration config = null;
		try {
			config = (ToolbarConfiguration) Epos.getAppContext().getLocalStorage().load("toolbar-" + viewer+".toolbar");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(config == null && defaultFile != null){
			// load default - if we can find one
			//Epos.getAppContext().getLocalStorage().load(arg0)
		    XMLDecoder d;
			try {
				d = new XMLDecoder(defaultFile.openStream());
				config = (ToolbarConfiguration) d.readObject();
			} catch (IOException e) {
			}
		}
		
		if(config == null){
			config = new ToolbarConfiguration(viewer);
		}
		return config;		
	}

	public void setUsedActions(List<String> usedActions) {
		this.usedTools = usedActions;
		reinitToolbar();
		save();
	}

	public String getViewer() {
		return viewer;
	}

	public void setViewer(String viewer) {
		this.viewer = viewer;
	}

	public List<String> getUsedTools() {
		return usedTools;
	}

	public void setUsedTools(List<String> usedTools) {
		this.usedTools = usedTools;
	}

}
