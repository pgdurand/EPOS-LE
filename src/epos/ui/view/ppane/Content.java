/*
 * Created on 17.08.2004
 */
package epos.ui.view.ppane;



/**
 * Interface defining a basic Content for a PowerPane
 * 
 * @author Thasso
 */
public interface Content {

    /**
     * Finishe all changes on the content.
     * This is called by the {@link PowerPane} before it
     * disposes.
     *
     */
    public void close();
    
    /**
     * Add a content change listener
     * 
     * @param listener
     */
    public void addContentChangeListener(ContentChangeListener listener);
    /**
     * remove content change listener
     * @param listener
     */
    public void removeContentChangeListener(ContentChangeListener listener);
    /**
     * fire content change event
     * @param event
     */
    void fireContentChangeEvent(ContentChangeEvent event);
}
