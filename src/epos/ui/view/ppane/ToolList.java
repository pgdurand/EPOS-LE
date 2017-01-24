package epos.ui.view.ppane;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JList;

public class ToolList extends JList{
	public ToolList() {
		super(new DefaultListModel());
		setCellRenderer(new CellRenderer());
	}
	
	public ToolList(List<Action> ts) {
		this();
		for (Action action : ts) {
			((DefaultListModel)getModel()).addElement(action);
		}
	}
	
	class CellRenderer extends DefaultListCellRenderer{

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);

			Action t = (Action) value;
			if(t == null){
				setText("Null action!??");
			}else{
				setIcon((Icon) t.getValue(Action.SMALL_ICON));			
				setText((String) t.getValue(Action.NAME));
			}
			return this;
		}		
	}

	public List<String> getValues() {
		ArrayList<String> values = new ArrayList<String>();
		for(int i=0; i< ((DefaultListModel)getModel()).getSize();i++){
			Action a = (Action) ((DefaultListModel)getModel()).getElementAt(i);
			if(a != null) {
				String name = (String) a.getValue(Action.NAME);
				if(name == null || name.equals("")) name = a.toString();
				values.add(name);				
			}
		}
		return values;
	}		
}
