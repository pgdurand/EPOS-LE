package epos.ui.view.ppane;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * Component implementation of a View. This extends JComponent and calls {@link #renderView(Graphics)} during
 * {@link #paintComponent(Graphics)}. Renderers are also managed. Be aware that the {@link #getBufferedImage()} methods
 * returns an image of the actual visible area, not of the complete component.
 * 
 * @author thasso
 *
 * @param <P>
 */
public abstract class AbstractView<V extends View<V,C>, C extends Content> extends JComponent implements View<V,C> {
	protected Logger log = Logger.getLogger(getClass());
	/**
	 * a list of active renderers
	 */
	protected List<Renderer> active_renderer;

	/**
	 * store the size and position of the last renderered buffer
	 */
	protected Rectangle lastBufferRect = new Rectangle();

	/**
	 * a buffered image of teh view
	 */
	protected VolatileImage buffer;

	/**
	 * if true, a buffere repaint is forced
	 */
	protected boolean forceBufferRefresh = false;

	/**
	 * the parent pane
	 */
	protected PowerPane<V, C> parent;
	
	/**
	 * Store the antialiasing settings
	 */
	protected EnumSet<Antialiasing> antialiasing = EnumSet.of(View.Antialiasing.AA_OFF, View.Antialiasing.AA_TEXT_ON);

	/**
	 * Simple comparator that sorts renderers by their layer level
	 */
	private Comparator<Renderer> comparator = new Comparator<Renderer>() {
		public int compare(Renderer o1, Renderer o2) {
			if (o1.getLayerLevel() == o2.getLayerLevel() && o1 == o2) {
				return 0;
			} else if (o1.getLayerLevel() > o2.getLayerLevel()) {
				return 1;
			}
			return -1;
		}
	};

	public AbstractView() {
		super();
		active_renderer = new ArrayList<Renderer>();
	}

	/**
	 * Activate a specific renderer (also forces a repaint on the view)
	 */
	public void enableRenderer(Renderer renderer) {
		addRenderer_impl(renderer);
	}

	/**
	 * deactivate a renderer. (force a repaint on the view)
	 */
	public void disableRenderer(Renderer renderer) {
		removeRenderer_impl(renderer);
	}

	/**
	 * ensures that the list of active renderers is sorted by the renderers
	 * layer level
	 * 
	 * @param renderer
	 */
	private void addRenderer_impl(Renderer renderer) {
		int i = Collections.binarySearch(active_renderer, renderer, comparator);
		log.debug("Activating renderer " + renderer + " at " + i);
		if (i < 0) {
			active_renderer.add(-i - 1, renderer);
			repaint();
		}
	}

	/**
	 * ensures that the list of active renderers stays sorted and uses a binary
	 * search to remove in log(n).
	 * 
	 * @param renderer
	 */
	private void removeRenderer_impl(Renderer renderer) {
		int i = Collections.binarySearch(active_renderer, renderer, comparator);
		log.debug("Deactivating renderer " + renderer+ " at " + i);
		if (i >= 0) {
			active_renderer.remove(i);
			repaint();
		}
	}


	/**
	 * Renders the view. This iterates over the list of active renderers and
	 * paints all renderers and view view in the correct order.
	 */
	public void render(Graphics og) {
		boolean view_rendererd = false;

		/*
		 * create a graphics2d and set antialiasing properties
		 */
		Graphics2D g = (Graphics2D) og.create();

		for (View.Antialiasing aa : antialiasing) {
			if(aa == View.Antialiasing.AA_ON){
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}else if( aa == View.Antialiasing.AA_OFF){
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			}

			if(aa == View.Antialiasing.AA_TEXT_ON){
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}else if( aa == View.Antialiasing.AA_TEXT_OFF){
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}
		}
		
		// first draw the background
		renderBackground(g);
		
		if (active_renderer.size() > 0) {			
			/*
			 * go over all active renderers
			 */
			for (Renderer r : active_renderer) {
				/*
				 * if we reach the view lavel, render the view (once!)
				 * 
				 * check for exclusiveness
				 */
				if (!view_rendererd	&& r.getLayerLevel() >= DEFAULT_VIEW_LEVEL) {
					renderView(g);
					view_rendererd = true;
				}
				/*
				 * render the render
				 */
				r.render(g);
			}
		} else {
			/*
			 * if there was no renderer at all, render the view
			 */
			renderView(g);
		}
		g.dispose();
	}

	protected void renderBackground(Graphics og) {
	}

	/**
	 * Creates a buffered images version of the view. This does only create a
	 * buffered version of the visible rectangle, not the complete view !.
	 */
	public Image getBufferedImage() {
		Rectangle r = getViewComponent().getVisibleRect();
		boolean samePos = r.equals(lastBufferRect);
		boolean validateBuffer = !samePos;

		if (buffer == null || !samePos) {
			validateBuffer = true;
			buffer = getComponent().createVolatileImage(r.width, r.height);			
			lastBufferRect.setFrame(r);
			forceBufferRefresh = false;
		}

		do {
			// First, we validate the back buffer
			int valCode = buffer.validate(getComponent()
					.getGraphicsConfiguration());
			if (valCode == VolatileImage.IMAGE_RESTORED) {
				// memory restored, recreate image!
				validateBuffer = true;
			} else if (valCode == VolatileImage.IMAGE_INCOMPATIBLE) {				
				buffer = getViewComponent().createVolatileImage(r.width, r.height);
				validateBuffer = true;
			}
			// is there somethig to be rendered or is the old buffer still
			// correct
			if (validateBuffer || forceBufferRefresh) {
				// Now we've handled validation, get on with the rendering
				// rendering to the back buffer:
				Graphics2D gBB = (Graphics2D) buffer.getGraphics();
				gBB.translate(-r.x, -r.y);
				render(gBB);
			}
			// Now we are done; or are we? Check contentsLost() and loop as
			// necessary
		} while (buffer.contentsLost());		
		forceBufferRefresh = false;
		lastBufferRect.setFrame(r);		
		return buffer;
	}

	public abstract void renderView(Graphics g);
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		render(g);
		//forceBufferRefresh = true;
		//g.drawImage(getBufferedImage(), 0,0, this);
	}
	public boolean isForceBufferRefresh() {
		return forceBufferRefresh;
	}

	public void setForceBufferRefresh(boolean forceBufferRefresh) {
		this.forceBufferRefresh = forceBufferRefresh;
	}

	public JComponent getComponent() {
		return this;
	}
	
	public JComponent getViewComponent(){
		return this;
	}

	public PowerPane<V,C> getParentPane() {
		return parent;
	}

	public void setParentPane(PowerPane<V,C> parent) {
		this.parent = parent;
	}

	public EnumSet<epos.ui.view.ppane.View.Antialiasing> getAntialiasing() {
		return antialiasing;
	}

	public void setAntialiasing(EnumSet<epos.ui.view.ppane.View.Antialiasing> aa) {
		if(aa == null){
			aa = EnumSet.of(Antialiasing.AA_ON, Antialiasing.AA_TEXT_ON);
		}
		this.antialiasing = aa;
	}
	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#getCursor()
	 * TODO : this is a stupid workaround fix this !
	 */
	@Override
	public Cursor getCursor(){
		return super.getCursor();
	}
	@Override
	public void setCursor(Cursor c){
		parent.getRoot().setCursor(c);
	}
	
	public void setBackendAvailable(boolean backend) {		
	}

}
