package epos.ui.view.ppane;

import javax.swing.event.EventListenerList;

/**
 * Default content implementation that manages the event listerners.
 * 
 * @author thasso
 *
 */
public abstract class AbstractContent implements Content{

	protected EventListenerList listenerList = new EventListenerList();

	public void close() {
	}

	public void addContentChangeListener(ContentChangeListener listener) {
		if(listenerList == null)
			listenerList = new EventListenerList();
		listenerList.add(ContentChangeListener.class, listener);
	}

	public void removeContentChangeListener(ContentChangeListener listener) {
		if(listenerList != null)
			listenerList.remove(ContentChangeListener.class, listener);
	}
	
	public void fireContentChangeEvent(ContentChangeEvent event){
	    if(listenerList == null)
	        return;
		Object[] listeners = listenerList.getListenerList();		
		for (int i = listeners.length - 2; i >= 0; i-=2) {
			if(listeners[i] == ContentChangeListener.class){				
				if(event != null && !event.getSource().equals(listeners[i+1]) ){					
					((ContentChangeListener) listeners[i+1]).contentChange(event);
				}				
			}
		}
	}


}
