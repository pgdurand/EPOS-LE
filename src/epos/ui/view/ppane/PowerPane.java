/*
 * Created on 17.08.2004
 */
package epos.ui.view.ppane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
/*import epos.ui.view.ppane.docking.DockingContainer;
import epos.ui.view.ppane.docking.DockingContainerChangeEvent;
import epos.ui.view.ppane.docking.DockingContainerChangeListener;
import epos.ui.view.ppane.docking.DockingPane;
*/
/**
 * @author Thasso
 */
@SuppressWarnings("unchecked")
public class PowerPane<V extends View<V,C>, C extends Content> extends JPanel implements ContentChangeListener {        
    private static Logger log = Logger.getLogger(PowerPane.class);
    public static final String PROPERTY_CONTENT_CHANGED = "Content changed";
    public static final String PROPERTY_VIEW_CHANGED = "View Changed";
    // Fixes #93
	private static final int INITIAL_DOCK_SIZE = 180;
    
    /**
     * Store persistent information in this node
     */
    //protected QNodeView backend;
    
    /**
     * The Main View 
     */
    protected V view = null;
            
    /**
     * The Content
     */
    protected C content = null;
            
    /**
     * All Tools
     */
    protected ArrayList<Tool<V,C>> tools;
    
    
    // GUI Stuff 
    //
    //
    /**
     * The toolbar configuration
     */
    //protected ToolbarConfiguration toolbarConfiguration;
    /**
     * store tools in toolbar here
     */
    protected List<Tool> toolbarTools = new ArrayList<Tool>();
    /**
     * The view component
     */
    protected JComponent viewComponent;
    /**
     * The docking pane
     */
    //protected DockingPane dock;
    /**
     * split pane 
     */
    //protected JSplitPane mainSplit;
    protected JPanel mainSplit;
    
	//protected Map<Component, DockingContainer> components2docks;
	
	/*protected DockingContainerChangeListener dockingCL = new DockingContainerChangeListener() {	
		public void dockingContainerChange(DockingContainerChangeEvent evt) {
			DockingContainer container = (DockingContainer) evt.getSource();
			if(dockSelectActions != null){
				ApplicationAction action = dockSelectActions.get(container);
				if(action != null){
					action.setSelected(container.isActivated());
				}
			}
//			if(evt.isSelected()){
//				if(docks2tools== null) return;
//				DockingContainer container = (DockingContainer) evt.getSource();				
//				Tool t = docks2tools.get(container);
//				if(t != null){
//					if(container.isActivated()){
//						activateTool(t, null);
//					}else{
//						deactivateTool(t);
//					}
//				}
//			}
		}	
	};
	*/
    //
    //
    ////////
	protected MouseListener focusMouseListener = new MouseAdapter(){		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(view != null && ! view.getViewComponent().hasFocus()){
				view.getViewComponent().requestFocusInWindow();
			}
		}	
	};
	/**
	 * The default tool
	 */
	//protected Tool defaultTool;
	/**
	 * The power pane name
	 */
	protected String name; 
	private ControllerMouseListener controllerManager;
	//private HashMap<DockingContainer, ApplicationAction> dockSelectActions;
	//private List<Action> toolbarActions;
	private ResourceMap factoryResourceMap;
    /////// Constructors 
    //
    //
    /**
     * Construct a new PowerPane Mapper Object.
     * You have to call
     * setView() and setContent() after construction.
     * @param factoryResourceMap 
     */    
    protected PowerPane(String name, ResourceMap factoryResourceMap){
    	super(new BorderLayout());
    	this.factoryResourceMap = factoryResourceMap;
    	setName(name);
        tools = new ArrayList<Tool<V,C>>();
        controllerManager = new ControllerMouseListener();
        
        //dock = new DockingPane();
        //mainSplit = new JSplitPane();
        mainSplit = new JPanel(new BorderLayout());

        //mainSplit.setLeftComponent(dock);
        // get URL to default toolbar definition
		/*String toolbarFile = factoryResourceMap.getString("toolbarURL");		
		URL toolbarURL = null;	
		if(toolbarFile != null && !toolbarFile.equals("")){
			String toolbarURLString = factoryResourceMap.getResourcesDir() + toolbarFile; 
			toolbarURL = factoryResourceMap.getClassLoader().getResource(toolbarURLString);
		}*/

        //URL defaultToolbarURL = Epos.getAppContext().getResourceMap(getClass()). 
        /*toolbarConfiguration = ToolbarConfiguration.getConfiguration(getName(), toolbarURL);
        toolbarConfiguration.getToolbar().setFloatable(false);
        toolbarConfiguration.getToolbar().setRollover(true);		
        toolbarConfiguration.getToolbar().setFocusable(false);
        toolbarConfiguration.getToolbar().setVisible(false);
        toolbarConfiguration.getToolbar().addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				if(MouseHelper.isRightClick(e)){
					JPopupMenu m = new JPopupMenu();
					m.add(getConfigToolbarAction());
					m.show(toolbarConfiguration.getToolbar(), e.getX(), e.getY());
				} 
			}		
		});*/

        
        
        setLayout(new BorderLayout());
		//add(toolbarConfiguration.getToolbar(), BorderLayout.NORTH);
		
		add(mainSplit, BorderLayout.CENTER);
		
//		/*
//		 * create the root pane for the floating containers and 
//		 * add a custom close listener that manages the close operations
//		 * and disbales the tools assigned to the specific floatingcontainer
//		 */
//		root = new FloatingDock();
//		root.setCloseListener(new CloseListener() {		
//			@Override
//			public void close(FloatingContainer container) {
//				for (Tool r : floaters.keySet()) {					
//					if(floaters.get(r) == container){
//						deactivateTool(r);
//						break;
//					}
//				}
//			}		
//		});
//
//		add(root.getComponent(), BorderLayout.CENTER);			
    }
    

    /**
     * Construct a new PPMapper with a given 
     * View and a given Content.
     * 
     * @param view
     * @param content
     */
    public PowerPane(V view, C content, ResourceMap factoryResourceMap){
    	this(view.getClass().getName(), factoryResourceMap);    	
        setContent(content);
        setView(view);        
    }
    
    /**
     * sets the content. and updates the view's content if a view is set.
     *  
     * TODO: no events are fired here, we meight want to change this
     * and inform someone about the content switch !!!
     * 
     * @param content the new content
     */
	public void setContent(C content) {
		if(this.content != content){
			content.removeContentChangeListener(this);
			C old = this.content;
			this.content = content;
			this.content.addContentChangeListener(this);
			firePropertyChange(PROPERTY_CONTENT_CHANGED, old, content);
		}        
    }
    
	/**
	 * sets the view. If an old view is already set, it is removed and the content
	 * is passed to the new view.
	 * <br>
	 * a mouse listener is added to the views compoent to request focus on mouse release 
	 * and the view components input map is modified and the fade action is added
	 * to enable or disable fading of floating contaners.
	 * 
	 * @param view
	 */
    @SuppressWarnings("unchecked")
	public void setView(V view) {
    	V old = this.view;
        if(this.view != null && view != this.view){
        	mainSplit.remove(this.view.getComponent());
        	this.view.setParentPane(null);
        	this.view.getViewComponent().removeMouseListener(focusMouseListener);
        	this.view.getViewComponent().getActionMap().remove("fadeOut");        	
        	this.view.getViewComponent().getActionMap().remove("fadeIn");
        }
        this.view = view;
        this.view.setParentPane(this);
        this.view.getComponent().setFocusable(true);
       	        
		this.view.getViewComponent().addMouseListener(focusMouseListener);				
		this.viewComponent = this.view.getComponent();
		this.viewComponent.setBorder(BorderFactory.createEmptyBorder());
		//mainSplit.setRightComponent(this.viewComponent);
		mainSplit.add(this.viewComponent, BorderLayout.CENTER);
		
		controllerManager.init();
		
       	firePropertyChange(PROPERTY_VIEW_CHANGED, old, view);
       	this.view.getViewComponent().requestFocusInWindow();
    }
        
    /**
     * returns the current content
     * @return content
     */
    public C getContent() {
		return content;
	}
    /**
     * Returns the current view
     * @return view
     */
	public V getView() {
		return view;
	}
	
	/**
	 * Returns the name of this ppane.
	 * @return name
	 */
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
                
	/**
	 * register tool. This delegates to {@link #registerTool(Tool, boolean)} with the
	 * addToToolbar options set to false.
	 *  
	 * @param r the tool
	 */
	public void registerTool(Tool r){
    	if(r == null)
    		throw new IllegalArgumentException("You can not register a null Object !");
    	if(tools.contains(r)) return;    	
    	tools.add(r);    	            	     
    	r.setParentPP(this);
    	r.update(this);
	}

	/**
     * The method returns a registered instance of the Tool represented by the given class.
     * Null is returned if the tool is not registered.
     *  
     * @param <T>Tool
     * @param clazz The tool class
     * @return the instance of the tool or null
     */
    public <T extends Tool> T getTool(Class<T> clazz){
    	for (Tool t : tools) {
			if(t.getClass().equals(clazz)){
				return (T) t;
			}
		}
    	return null;
    }
    	           			
    /**
     * close this PPMapper and remove it from its parent.
     *
     */
	public void dispose(){
		//toolbarConfiguration.save();
		this.view = null;
		content.close();
		this.content = null;
		if(listenerList != null){			
            //Object[] listeners  = listenerList.getListenerList();
            ///FIXME dont know if this is enough to clean the list
            listenerList = null;
        }		
		if(getParent() != null)
			getParent().remove(this);
		System.gc();
	}
			
	/**
	 * @return the root
	 */
	public Component getRoot() {
		return viewComponent;//root.getComponent();
	}


	/*public QNodeView getBackend() {
		return backend;
	}


	public void setBackend(QNodeView backend) {
		this.backend = backend;
		if(getView() != null){
			getView().setBackendAvailable(backend != null ? true:false);
		}
	}*/
	/*
	public void initializeTools() {	
		toolbarActions = new ArrayList<Action>();
		
		/// check view
		if(view != null){
			List<Action> toolbarActions = AnnotationUtils.getToolbarActions(view);
			if(toolbarActions != null){				
				for (Action action : toolbarActions) {				
					this.toolbarActions.add(action);
				}
			}			
			// components
			Object[] cpms = AnnotationUtils.getAnnotatedComponents(view);
			List<JComponent> components = (List<JComponent>) cpms[0];
			Map<JComponent, Action> actions = (Map<JComponent, Action>) cpms[1];
			for (JComponent c : components) {
				Action a = actions.get(c);
				addDockContainer(c, a);
			}			
			registerActionKeys(Epos.getAppContext().getActionMap(view));
		}
		
		// check content		
		if(content != null){
			List<Action> toolbarActions = AnnotationUtils.getToolbarActions(content);
			if(toolbarActions != null){
				for (Action action : toolbarActions) {
					this.toolbarActions.add(action);
				}
			}
			// components
			Object[] cpms = AnnotationUtils.getAnnotatedComponents(content);
			List<JComponent> components = (List<JComponent>) cpms[0];
			Map<JComponent, Action> actions = (Map<JComponent, Action>) cpms[1];
			for (JComponent c : components) {
				Action a = actions.get(c);
				addDockContainer(c, a);
			}
			
			registerActionKeys(Epos.getAppContext().getActionMap(content));
		}
		
		for (Tool tool : tools) {
			// check for components
			List<JComponent> components = tool.getComponents();
			if(components != null && components.size() >0){
	    		for (JComponent component : components) {
	    			Action action  = tool.getComponentAction(component);
	    			addDockContainer(component, action);
				}
			}
			
			// add toolbar actions			
			List<Action> toolbarActions = tool.getToolbarActions();
			if(toolbarActions != null){				
				this.toolbarActions.addAll(toolbarActions);
			}
			
			// register all accelerator actions
			ActionMap actions= tool.getActions();
			registerActionKeys(actions);					
		}
		for (Tool tool : tools) {
			tool.initialized();	
		}

		
		remove(mainSplit);
		remove(viewComponent);		
		if(components2docks == null || components2docks.size() == 0){
			// remove split
			add(viewComponent, BorderLayout.CENTER);
			//mainSplit.setDividerSize(0);
		}else{
			mainSplit.setRightComponent(viewComponent);
			add(mainSplit, BorderLayout.CENTER);
			mainSplit.setDividerSize(4);
			// Fixes #93
			mainSplit.setDividerLocation(INITIAL_DOCK_SIZE);
		}
				
		toolbarConfiguration.setActions(toolbarActions);
		toolbarConfiguration.reinitToolbar();
				

		revalidate();
		repaint();
		
	}*/
	protected void registerActionKeys(ActionMap actions) {
		if(actions != null && actions.size() > 0){
			for (Object key : actions.allKeys()) {
				Action a = actions.get(key);
				if(a.getValue(Action.ACCELERATOR_KEY) != null){
					KeyStroke keyStroke =(KeyStroke) a.getValue(Action.ACCELERATOR_KEY); 
					if(getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(keyStroke) != null){
						log.error("Keystroke "+ keyStroke + " is already registered !");
					}else{
						getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, key);
						getActionMap().put(key, a);
					}
				}
			}
		}
	}


	/*protected void addDockContainer(JComponent component, Action action){
		if(components2docks == null){
			components2docks = new HashMap<Component, DockingContainer>();
		}		    		 

		DockingContainer container = new DockingContainer(component.getName(), component, action != null, false);
		if(action != null){
			if(dockSelectActions == null) dockSelectActions = new HashMap<DockingContainer, ApplicationAction>();
			dockSelectActions.put(container, (ApplicationAction)action);
			container.setActivationAction((ApplicationAction)action);			
			// add pcl to listen to activations from outside using the action
			ComponentFactory.prepareSelectAction(action, container.getActivationBox());
		}
		components2docks.put(component, container);
		container.addDockingEventListener(dockingCL);				
		dock.add(container);			
	}
*/
	/*public Action getConfigToolbarAction(){
		return Epos.getAppContext().getActionMap(toolbarConfiguration).get("showConfigDialog");
	}*/


	public void activateController(Controller controller) {
		controllerManager.enableController(controller);
	}
	public void activateRenderer(Renderer<V,C> renderer) {
		view.enableRenderer((Renderer) renderer);
	}	
	public void deactivateController(Controller controller) {		
		controllerManager.disableController(controller);
	}
	public void deactivateRenderer(Renderer<V,C> renderer) {
		view.disableRenderer(renderer);
	}
	
	/*public DockingContainer getDockForComponent(Component component){
		if(components2docks == null) return null;
		return components2docks.get(component);
	}*/

	class ControllerMouseListener implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener{
		protected Map<KeyStroke, List<Controller<V, C>>> controllerMap;
		protected List<Controller> nullController;
		private KeyStroke controllerKeyPressed = null;
				
		public ControllerMouseListener(){
			super();
			this.nullController = new ArrayList<Controller>();
			this.controllerMap = new HashMap<KeyStroke, List<Controller<V,C>>>();	
		}
		
		public void init(){
			if(getView().getViewComponent() instanceof JScrollPane){
				JScrollPane p = (JScrollPane) getView().getViewComponent();
				p.getViewport().getView().addKeyListener(this);
				p.getViewport().getView().addMouseListener(this);
				p.getViewport().getView().addMouseMotionListener(this);			
			}else{
				getView().getViewComponent().addKeyListener(this);
				getView().getViewComponent().addMouseListener(this);
				getView().getViewComponent().addMouseMotionListener(this);
			}
		}
		
		public void enableController(Controller controller){
			if(controller.getKeyStrokes() == null){
				ArrayList<Controller> toRemove = new ArrayList<Controller>();
				nullController.remove(controller);
				for (Controller c : nullController) {
					if(controller.conflicts(c)){
						toRemove.add(c);
					}
				}
				nullController.add(0, controller);
				for (Controller c : toRemove) {
					c.disable();
				}
			}else{
				
				List<KeyStroke> strokes =controller.getKeyStrokes();
				for (KeyStroke keyStroke : strokes) {
					if(keyStroke == null){
						ArrayList<Controller> toRemove = new ArrayList<Controller>();
						nullController.remove(controller);
						for (Controller c : nullController) {
							if(controller.conflicts(c)){
								toRemove.add(c);
							}
						}
						nullController.add(0, controller);
						for (Controller c : toRemove) {
							c.disable();
						}
						
					}else{
						List<Controller<V,C>> list = controllerMap.get(keyStroke);
						if(list == null){
							list = new ArrayList<Controller<V,C>>();
							controllerMap.put(keyStroke, list);
						}
						list.remove(controller);
						ArrayList<Controller<V,C>> toRemove = new ArrayList<Controller<V,C>>();
						for (Controller<V,C> c : list) {
							if(controller.conflicts(c)){
								toRemove.add(c);
							}
						}
						for (Controller<V,C> c : toRemove) {
							c.disable();				
						}
						list.add(0, controller);
					}
				}
			}
		}
		
		public void disableController(Controller controller){
			if(controller.getKeyStrokes() == null){
				nullController.remove(controller);
			}else{
				List<KeyStroke> strokes =controller.getKeyStrokes();
				for (KeyStroke keyStroke : strokes) {
					if(keyStroke == null){
						nullController.remove(controller);
					}else{
						List<Controller<V,C>> list = controllerMap.get(keyStroke);
						if(list != null){
							list.remove(controller);	
						}
					}
				}
			}	
		}

		public void keyPressed(KeyEvent e) {
			controllerKeyPressed  = KeyStroke.getKeyStrokeForEvent(e);
			
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.keyPressed(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {
						currentController.keyPressed(e);
					}
				}				
			}

		}
		
		public void keyReleased(KeyEvent e) {
			controllerKeyPressed = KeyStroke.getKeyStrokeForEvent(e);
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.keyReleased(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {
						currentController.keyReleased(e);
					}
				}				
			}
			controllerKeyPressed = null;
		}
		
		public void keyTyped(KeyEvent e) {
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.keyTyped(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {
						currentController.keyTyped(e);
					}
				}				
			}
		}
		
		public void mouseClicked(MouseEvent e) {
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.mouseClicked(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {
						currentController.mouseClicked(e);
					}
				}				
			}
		}

		public void mouseEntered(MouseEvent e) {
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.mouseEntered(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {
						currentController.mouseEntered(e);
					}
				}
			}
		}
		
		public void mouseExited(MouseEvent e) {
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.mouseExited(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {
						currentController.mouseExited(e);
					}
					
				}
			}

		}
		public void mousePressed(MouseEvent e) {
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.mousePressed(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {

						currentController.mousePressed(e);
					}
				}
			}

		}
		public void mouseReleased(MouseEvent e) {
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.mouseReleased(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {

						currentController.mouseReleased(e);
					}
				}
			}
		}
		public void mouseDragged(MouseEvent e) {
			if(controllerKeyPressed == null){			
				for (Controller controller : nullController) {
					controller.mouseDragged(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {

						currentController.mouseDragged(e);
					}
				}
			}
		}
		public void mouseMoved(MouseEvent e) {
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.mouseMoved(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {
						currentController.mouseMoved(e);
					}
				}
			}
		}
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(controllerKeyPressed == null){
				for (Controller controller : nullController) {
					controller.mouseWheelMoved(e);
				}
			}else{
				List<Controller<V,C>> list = controllerMap.get(controllerKeyPressed);
				if(list != null){
					for (Controller<V,C> currentController : list) {
						currentController.mouseWheelMoved(e);
					}
				}
			}
		}
	}
	
	/**
	 * Reacts on content change events and triggers a call to {@link Tool#update(PowerPane)} for
	 * each registered {@link Tool}.
	 * 
	 */
	public void contentChange(ContentChangeEvent event) {
		for (Tool<V,C> t : tools) {
			t.update(this);
		}
	}
	
}

