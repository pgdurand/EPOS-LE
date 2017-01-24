package epos.ui.view.treeview.components;

import java.util.EventListener;

public interface SelectionListener extends EventListener{
	public void selectionChanged(SelectionManager manager);
}
