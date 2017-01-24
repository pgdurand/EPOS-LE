/*
 * Created on 06.06.2006
 */
package epos.ui.view.treeview.renderer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.jdesktop.application.Action;

import epos.Epos;
import epos.model.tree.TreeNode;
import epos.ui.util.ColorChooserPanel;
import epos.ui.util.MouseHelper;
import epos.ui.view.ppane.AbstractController;
import epos.ui.view.ppane.AbstractRenderer;
import epos.ui.view.ppane.AbstractTool;
import epos.ui.view.ppane.ControlledUnit;
import epos.ui.view.ppane.Controller;
import epos.ui.view.ppane.Renderer;
import epos.ui.view.treeview.ImmutableException;
import epos.ui.view.treeview.TreeContent;
import epos.ui.view.treeview.TreeView;
import epos.ui.view.treeview.ColorManager.EdgeColorizations;
import epos.ui.view.treeview.ColorStyle.Colors;
import epos.ui.view.treeview.components.ComponentManager;
import epos.ui.view.treeview.components.NodeComponent;
import epos.ui.view.treeview.components.SelectionManager.SelectionType;

public class NodeSelectRenderer extends AbstractTool<TreeView, TreeContent> {

	/**
	 * the click point
	 */
	protected Point clickPoint = null;
	/**
	 * the last mouse position
	 */
	protected Point movePoint = null;
	protected Rectangle selecteRect = new Rectangle();
	private MouseAdapter mouseAdapter;
	protected boolean altDown;
	protected boolean shiftDown;
	protected Cursor oldCursor;
	protected Cursor defaultCursor;
	protected Cursor crossPlusCursor;
	protected Cursor crossMinusCursor;

	private NodeComponent nextNodes;
	private Rectangle searchRectangle = new Rectangle(10, 10);
	private int searchSpace = 20;
	private ArrayList<NodeComponent> tempSelection;

	private JPopupMenu menu;
	private boolean nodeSelectionEnabled;
	private SelecteController nodeSelectController;
	private SelectRenderer nodeSelectRenderer;
	private boolean mouseOverSelectionEnabled;
	private OverNodeController overController;
	private OverRenderer overRenderer;

	public NodeSelectRenderer() {
		super();
	}
	
	@Action
	public void selectSubtree(){
		Collection<NodeComponent> nodes = tempSelection;
		if (nodes == null) {
			return;
		}
		parent.getView().getSelectionManager().addToSelection(nodes,
				EnumSet.of(SelectionType.SUBTREE));		
	}
	@Action
	public void selectPathToRoot(){
		Collection<NodeComponent> nodes = tempSelection;
		if (nodes == null) {
			return;
		}
		parent.getView().getSelectionManager().addToSelection(nodes,
				EnumSet.of(SelectionType.PATH_TO_ROOT));		
	}
	@Action
	public void selectAll(){
		TreeNode root = parent.getView().getTree().getRoot();
		NodeComponent nc = parent.getView().getNodesComponent(root);
		parent.getView().getSelectionManager().addToSelection(nc,
				EnumSet.of(SelectionType.SUBTREE));		
	}
	@Action
	public void clearSelection(){
		parent.getView().getSelectionManager().setSelection(null);
	}
	@Action
	public void editNodeLabel(){
		Collection<NodeComponent> nodes = tempSelection;
		if (nodes == null || nodes.size() == 0) {
			return;
		}		
		NodeComponent nc = nodes.iterator().next();
		String label = nc.getLabel();
		if(label == null){
			label = "";
		}
		Object ret  = JOptionPane.showInputDialog(
					Epos.getEpos().getMainFrame(), 
					"Change node label", 
					"Node Label", 
					JOptionPane.QUESTION_MESSAGE, 
					null, 
					null, 
					label);
		if(ret != null){
//			if(ret.toString().length() == 0){
//				ret = null;
//			}			
			nc.getNode().setLabel(ret.toString());
			parent.getView().getTreeLayout().treeStructureChanged();
			parent.getView().firePropertyChange(ComponentManager.PROPERTY_STRUCTURAL_CHANGE, -1, 1);
			parent.getView().getTreeLayout().treeStructureChanged();
			parent.getView().doLayout();
			parent.getView().repaint();
			//parent.getView().fireLayoutChangedEvent(parent.getView(), parent.getView().getTreeLayout());
			// delegate to backend
			parent.getContent().updateTreeNode(nc.getNode());
		}			
	}

	protected void showPopupMenu(MouseEvent e) {
		if (menu == null) {
			menu = new JPopupMenu();
			/*menu.add(getActions().get("editNodeLabel"));
			menu.add(parent.getTool(CollapseTool.class).getActions().get(
					"collapseSelected"));
			menu.add(parent.getTool(RotateAction.class).getActions().get(
					"rotate"));*/

			JMenu subtreeMarker = new JMenu("Colorize Subtree");
			final JColorChooser col = new JColorChooser();
			col.setPreviewPanel(new JPanel());
			col.getSelectionModel().addChangeListener(
					new ColorizeListener(EnumSet
							.of(EdgeColorizations.EDGES_TO_LEAVES)));
			col
					.setChooserPanels(new AbstractColorChooserPanel[] { new ColorChooserPanel() });
			subtreeMarker.add(col);

			JMenu rootMarker = new JMenu("Path to Root");
			final JColorChooser col2 = new JColorChooser();
			col2.setPreviewPanel(new JPanel());
			col2.getSelectionModel().addChangeListener(
					new ColorizeListener(EnumSet
							.of(EdgeColorizations.EDGES_TO_ROOT)));
			col2
					.setChooserPanels(new AbstractColorChooserPanel[] { new ColorChooserPanel() });
			rootMarker.add(col2);

			JMenu parentMarker = new JMenu("Path to Parent");
			final JColorChooser col3 = new JColorChooser();
			col3.setPreviewPanel(new JPanel());
			col3.getSelectionModel()
					.addChangeListener(
							new ColorizeListener(EnumSet
									.of(EdgeColorizations.IN_EDGE)));
			col3
					.setChooserPanels(new AbstractColorChooserPanel[] { new ColorChooserPanel() });
			parentMarker.add(col3);

			JMenu childMarker = new JMenu("Path to Children");
			final JColorChooser col4 = new JColorChooser();
			col4.setPreviewPanel(new JPanel());
			col4.getSelectionModel().addChangeListener(
					new ColorizeListener(EnumSet
							.of(EdgeColorizations.OUT_EDGES)));
			col4
					.setChooserPanels(new AbstractColorChooserPanel[] { new ColorChooserPanel() });
			childMarker.add(col4);

			//menu.addSeparator();
			menu.add(subtreeMarker);
			menu.add(rootMarker);
			menu.add(parentMarker);
			menu.add(childMarker);

			/*menu.addSeparator();
			menu.add(new JMenuItem(getActions().get("selectSubtree")));
			menu.add(new JMenuItem(getActions().get("selectPathToRoot")));
			menu.add(new JMenuItem(getActions().get("selectAll")));*/
		}

		menu.show(parent.getView(), e.getX(), e.getY());
	}

	protected void selectNodes(Rectangle r) {
		handleSelection(parent.getView().searchNodeComponents(r));
	}

	public void handleSelection(Object[] objects) {
		if (objects == null || objects.length == 0) {
			if (!shiftDown && !altDown)
				parent.getView().getSelectionManager().setSelection(null);
		} else {
			ArrayList<NodeComponent> l = new ArrayList<NodeComponent>(
					objects.length);
			for (int i = 0; i < objects.length; i++) {
				l.add((NodeComponent) objects[i]);
			}
			if (shiftDown) {
				parent.getView().getSelectionManager().addToSelection(l);
			} else if (altDown) {
				parent.getView().getSelectionManager().removeFromSelection(l);
			} else {
				parent.getView().getSelectionManager().setSelection(l);
			}
		}
	}
	@Action(selectedProperty="nodeSelectionEnabled")
	//@ToolbarAction
	public void nodeSelection(ActionEvent e){}
	public void setNodeSelectionEnabled(boolean nodeSelectionEnabled) {
		boolean old = this.nodeSelectionEnabled;
		this.nodeSelectionEnabled = nodeSelectionEnabled;
		firePropertyChange("nodeSelectionEnabled", old, this.nodeSelectionEnabled);
		
		if(nodeSelectionEnabled){
//			if (defaultCursor == null) {
//				defaultCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);// getParentPP().getToolkit().createCustomCursor(ResourceManager.getImage("epos.ui.view.treeview.cross"),
//																	// new
//																	// Point(8,8),"Cross
//																	// Cursor");
//				crossPlusCursor = getParentPP().getToolkit().createCustomCursor(
//						ResourceManager
//								.getImage("epos.ui.view.treeview.cross_plus"),
//						new Point(8, 8), "Cross Plus");
//				crossMinusCursor = getParentPP().getToolkit().createCustomCursor(
//						ResourceManager
//								.getImage("epos.ui.view.treeview.cross_minus"),
//						new Point(8, 8), "Cross Minus");
//			}
			//oldCursor = parent.getView().getCursor();
			//parent.getView().setCursor(defaultCursor);
			getParentPP().activateController(getNodeSelectController());
			getParentPP().activateRenderer(getNodeSelectRenderer());
		}else{
			//parent.getView().setCursor(oldCursor);
			parent.deactivateController(getNodeSelectController());
			parent.deactivateRenderer(getNodeSelectRenderer());
		}
	}
	
	public boolean isNodeSelectionEnabled(){
		return nodeSelectionEnabled;
	}

	private Renderer<TreeView, TreeContent> getNodeSelectRenderer() {
		if(nodeSelectRenderer == null) nodeSelectRenderer = new SelectRenderer();
		return nodeSelectRenderer;
	}

	private Controller getNodeSelectController() {
		if(nodeSelectController == null) nodeSelectController = new SelecteController();
		return nodeSelectController;
	}

	class SelectRenderer extends AbstractRenderer<TreeView, TreeContent> {

		public SelectRenderer() {
			super(getParentPP());
		}

		@Override
		public boolean conflicts(ControlledUnit otherUnit) {
			return true;
		}

		@Override
		public void disable() {
			setNodeSelectionEnabled(false);
		}

		/**
		 * Paint a rectangle if there was a mouse click and the mouse is still
		 * pressed
		 * 
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		public void render(Graphics parentGraphics) {
			Graphics2D g = (Graphics2D) parentGraphics;
			Color old = g.getColor();
			g.setColor(parent.getView().getColorManager().getColor(
					Colors.foreground));
			if (clickPoint != null) {
				Graphics2D g2d = (Graphics2D) g;
				int x = clickPoint.x > movePoint.x ? movePoint.x : clickPoint.x;
				int y = clickPoint.y > movePoint.y ? movePoint.y : clickPoint.y;
				int w = Math.abs(clickPoint.x - movePoint.x);
				int h = Math.abs(clickPoint.y - movePoint.y);
				g2d.drawRect(x, y, w, h);
			}
			g.setColor(old);
		}

	}

	class SelecteController extends AbstractController<TreeView, TreeContent> {
		private final KeyStroke SHIFT_PRESSED =KeyStroke.getKeyStroke("shift pressed SHIFT");
		private final KeyStroke SHIFT_RELEASED =KeyStroke.getKeyStroke("released SHIFT");
		private final KeyStroke ALT_PRESSED =KeyStroke.getKeyStroke("alt pressed ALT");
		private final KeyStroke ALT_RELEASED =KeyStroke.getKeyStroke("released ALT");
		
		private List<KeyStroke> strokes;
		
		public SelecteController() {
			super(getParentPP());
		}
		
		

		@Override
		public List<KeyStroke> getKeyStrokes() {
			if(strokes == null){
				strokes = new ArrayList<KeyStroke>();
				strokes.add(null);
				strokes.add(SHIFT_PRESSED);
				strokes.add(SHIFT_RELEASED);
				strokes.add(ALT_RELEASED);
				strokes.add(ALT_PRESSED);				
			}
			return strokes;
		}



		@Override
		public void disable() {
			setNodeSelectionEnabled(false);
		}

		@Override
		public int getMouseDisableActions() {
			return BUTTON1 | DRAG | MOVE | PRESSED | RELEASED;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (clickPoint != null) {
				movePoint = e.getPoint();
				parent.getView().repaint();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				clickPoint = e.getPoint();
				movePoint = e.getPoint();
				parent.getView().repaint();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (nextNodes != null) {
					handleSelection(new NodeComponent[] { nextNodes });
				} else {
					handleSelection(null);
				}
				clickPoint = null;
				movePoint = null;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (movePoint.equals(clickPoint))
					return;
				int x = clickPoint.x > movePoint.x ? movePoint.x : clickPoint.x;
				int y = clickPoint.y > movePoint.y ? movePoint.y : clickPoint.y;
				int w = Math.abs(clickPoint.x - movePoint.x);
				int h = Math.abs(clickPoint.y - movePoint.y);

				Rectangle r = new Rectangle(x, y, w, h);
				selectNodes(r);

				clickPoint = null;
				movePoint = null;
				parent.getView().repaint();
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(KeyStroke.getKeyStrokeForEvent(e).equals(SHIFT_PRESSED)){
				shiftDown = true;
				getParentPP().getView().setCursor(crossPlusCursor);
			}else if(KeyStroke.getKeyStrokeForEvent(e).equals(ALT_PRESSED)){
				altDown = true;
				getParentPP().getView().setCursor(crossMinusCursor);
			}else{
				shiftDown = false;
				altDown = false;
				getParentPP().getView().setCursor(defaultCursor);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(KeyStroke.getKeyStrokeForEvent(e).equals(SHIFT_RELEASED)){
				shiftDown = false;
				getParentPP().getView().setCursor(defaultCursor);
			}else if(KeyStroke.getKeyStrokeForEvent(e).equals(ALT_RELEASED)){
				altDown = false;
				getParentPP().getView().setCursor(defaultCursor);
			}else{
				shiftDown = false;
				altDown = false;
				getParentPP().getView().setCursor(defaultCursor);
			} 
		}
		
		

	}
	@Action(selectedProperty="mouseOverSelectionEnabled")
	public void mouseOverNodeSelection(ActionEvent e){}
	public void setMouseOverSelectionEnabled(boolean enabled){
		boolean old = this.mouseOverSelectionEnabled;
		this.mouseOverSelectionEnabled= enabled;
		firePropertyChange("mouseOverSelectionEnabled", old, this.mouseOverSelectionEnabled);
		if(enabled){
			getParentPP().activateController(getOverController());
			getParentPP().activateRenderer(getOverRenderer());
		}else{
			getParentPP().deactivateController(getOverController());
			getParentPP().deactivateRenderer(getOverRenderer());
		}
	}
	private Renderer<TreeView, TreeContent> getOverRenderer() {
		if(overRenderer == null) overRenderer = new OverRenderer();
		return overRenderer;
	}

	public boolean isMouseOverSelectionEnabled(){
		return mouseOverSelectionEnabled;
	}
	private Controller getOverController() {
		if(overController == null) overController = new OverNodeController();
		return overController;
	}


	class OverRenderer extends AbstractRenderer<TreeView, TreeContent>{

		public OverRenderer() {
			super(getParentPP());
		}

		@Override
		public boolean conflicts(ControlledUnit<TreeView, TreeContent> otherUnit) {
			return false;
		}

		@Override
		public void disable() {
			setMouseOverSelectionEnabled(false);
		}

		@Override
		public void render(Graphics g) {
			if (nextNodes != null) {
				Color old = g.getColor();
				g.setColor(Color.blue);
				g.drawArc(nextNodes.getX() - 5, nextNodes.getY() - 5, 10,
						10, 0, 360);
				g.setColor(old);
			}
			
		}
		
	}
	class OverNodeController extends AbstractController<TreeView, TreeContent> {

		public OverNodeController() {
			super(getParentPP());
		}

		@Override
		public void disable() {
		}

		@Override
		public int getMouseDisableActions() {
			return BUTTON2;
		}

		public void mouseMoved(MouseEvent e) {
			int s = (searchSpace / 2);
			searchRectangle.x = e.getX() - s;
			searchRectangle.y = e.getY() - s;
			NodeComponent oldNext = nextNodes;
			Object[] nearNodes = parent.getView().searchNodeComponents(
					new Rectangle(e.getX() - s, e.getY() - s, searchSpace,
							searchSpace));
			if (nearNodes.length == 0) {
				nextNodes = null;
			} else if (nearNodes.length == 1) {
				nextNodes = (NodeComponent) nearNodes[0];
				if (!nextNodes.isVisible())
					nextNodes = null;
			} else {
				nextNodes = null;
				for (Object o : nearNodes) {
					NodeComponent n = (NodeComponent) o;
					if (!n.isVisible())
						continue;
					if (nextNodes == null) {
						nextNodes = n;
					} else {
						if (nextNodes.getLocation().distance(e.getPoint()) > (n)
								.getLocation().distance(e.getPoint())) {
							nextNodes = (NodeComponent) o;
						}
					}
				}
			}
			if (oldNext == null && nextNodes == null)
				return;
			if (nextNodes != oldNext)
				parent.getView().repaint();
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			//Korilog: selection on the node label
			Iterator<NodeComponent> iter = parent.getView().getComponentManager().getNodeComponentsIterator();
			NodeComponent           nc;
			AffineTransform         rot;
			Rectangle2D             bounds;
			Shape                   sh;
			Font                    fnt;
			while(iter.hasNext()){
				nc = iter.next();
				if (!nc.getNode().isLeaf())
					continue;
				bounds = nc.getBounds();
				fnt = parent.getView().getComponentManager().getLabelFont();
				bounds.setRect(
						bounds.getX(), 
						bounds.getY()-Math.max(1.0,(fnt.getSize2D() / 2.0)), 
						bounds.getWidth(), 
						Math.max(1.0,fnt.getSize2D()));
				if (nc.getAngle()!=0.0){
					rot = AffineTransform.getRotateInstance(nc.getAngle(), nc.getX(), nc.getY());
					sh = rot.createTransformedShape(bounds);
				}
				else{
					sh = bounds;
				}
				if (sh.contains(e.getPoint())){
					nextNodes = nc;
					break;
				}
			}
			//Korilog-end
			handleMouseClick(e);
		}

//		public void mouseReleased(MouseEvent e) {
//			handleMouseClick(e);
//		}
//
//		public void mousePressed(MouseEvent e) {
//			handleMouseClick(e);
//		}

		protected void handleMouseClick(MouseEvent e) {
			// select node as temporary selection
			if(MouseHelper.isLeftClick(e)){
				if (nextNodes != null) {
					ArrayList<NodeComponent> nodes = new ArrayList<NodeComponent>();
					nodes.add(nextNodes);
					parent.getView().getSelectionManager().setTemoprarySelection(nodes);
					parent.getView().getSelectionManager().setSelection(nodes);
					tempSelection = nodes;
				} else {
					tempSelection = null;
					parent.getView().getSelectionManager().setTemoprarySelection(null);
					getParent().getView().getSelectionManager().setSelection(null);
				}
			}
			if (MouseHelper.isRightClick(e)) {
				if (nextNodes != null) {
					ArrayList<NodeComponent> nodes = new ArrayList<NodeComponent>();
					nodes.add(nextNodes);
					//System.out.println("set temp " + nodes);
					parent.getView().getSelectionManager().setTemoprarySelection(nodes);
					tempSelection = nodes;
					showPopupMenu(e);
				} else {
					tempSelection = null;
					parent.getView().getSelectionManager().setTemoprarySelection(null);
					getParent().getView().getSelectionManager().setSelection(null);
				}
				
			}
		}

	}

	
	class ColorizeListener implements ChangeListener {
		private EnumSet<EdgeColorizations> colorizations;

		public ColorizeListener(EnumSet<EdgeColorizations> colorizations) {
			super();
			this.colorizations = colorizations;
		}

		public void stateChanged(ChangeEvent e) {
			Collection<NodeComponent> nodes = tempSelection;
			if (nodes == null) {
				nodes = parent.getView().getSelectionManager()
						.getSelectedNodes();
			}
			if (nodes == null || nodes.size() == 0)
				return;

			// / check for a mutable color style
			if (parent.getView().getColorManager().getColorStyle()
					.isImmutable()) {
				Logger.getLogger(getClass()).info(
						"Creating custom color style!");
				parent.getView().getColorManager().setColorStyle(
						"Custom colors");
			}
			try {
				for (Iterator iter = nodes.iterator(); iter.hasNext();) {
					NodeComponent node = (NodeComponent) iter.next();
					parent.getView().getColorManager().setEdgeColor(
							node,
							((ColorSelectionModel) e.getSource())
									.getSelectedColor(), colorizations);
				}

				// store property
				parent.getView().getColorManager().storeColorSet(
						parent.getView().getColorManager().getColorStyle());
				parent.getView().repaint();
			} catch (ImmutableException e1) {
			}
		}

	}

	
}
