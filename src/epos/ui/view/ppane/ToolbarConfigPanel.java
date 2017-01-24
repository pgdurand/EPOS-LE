package epos.ui.view.ppane;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

//import epos.ui.util.ResourceManager;

public class ToolbarConfigPanel extends JPanel implements ActionListener{

	private ToolList inList;
	private ToolList outList;
	private ToolList activeList;
	
	private JPanel buttons;
	private ControllerPanel controller;
	protected JButton ok;
	protected JButton cancel;
	protected JButton apply;
	protected JButton seperator;
	
	protected JCheckBox showText;
	
	
	public ToolbarConfigPanel(List<Action> tools) {
		this(new ArrayList<Action>(), tools);
	}
	
	public ToolbarConfigPanel(List<Action> inTools, List<Action> outTools) {
		super();
		inList = new ToolList(inTools);
		inList.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e) {}		
			public void focusGained(FocusEvent e) {
				controller.getUp().setEnabled(true);
				controller.getDown().setEnabled(true);
				controller.getLeft().setEnabled(true);
				controller.getRight().setEnabled(false);
				activeList = inList;
			}		
		});
		outList = new ToolList(outTools);
		outList.addFocusListener(new FocusListener() {		
			public void focusLost(FocusEvent e) {}		
			public void focusGained(FocusEvent e) {
				controller.getUp().setEnabled(false);
				controller.getDown().setEnabled(false);
				controller.getLeft().setEnabled(false);
				controller.getRight().setEnabled(true);
				activeList = outList;
			}		
		});

		init();
	}	
	
	private void init() {
		buttons = new JPanel();
		controller = new ControllerPanel(this);		
		ok = new JButton("Ok");
		cancel = new JButton("Cancel");
		apply = new JButton("Apply");		
		showText = new JCheckBox("Show Text");
		seperator = new JButton("Add Seperator");
		seperator.setActionCommand("SEPERATOR");
		seperator.addActionListener(this);
		
		ButtonBarBuilder bb = new ButtonBarBuilder(buttons);
		bb.addGlue();
		bb.addGriddedButtons(new JButton[] {ok,cancel,apply});
		
		ButtonBarBuilder bbs = new ButtonBarBuilder();
		bbs.addGlue();
		bbs.addGridded(showText);
		bbs.addGriddedButtons(new JButton[] {seperator});
		

		
		setLayout(new FormLayout("4dlu,f:100dlu:g,2dlu,p,2dlu,f:100dlu:g,4dlu",
								 "4dlu,p,2dlu,p,f:p:g,2dlu,p,4dlu,p,4dlu"));
		CellConstraints cc = new CellConstraints();
		
		add(new JLabel("Available Tools"), cc.xy(2, 4));
		add(new JLabel("Toolbar"), cc.xy(6, 4));
		add(new JScrollPane(outList), cc.xy(2, 5));
		add(new JScrollPane(inList), cc.xy(6, 5));
		
		add(controller, cc.xy(4, 5));
		add(bbs.getPanel(), cc.xy(6, 7));
		add(buttons, cc.xyw(2, 9, 6));		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("UP")) {			
			int index = activeList.getSelectedIndex();
			if(index == -1 || index == 0) return;
			DefaultListModel m = (DefaultListModel) activeList.getModel();
			Object o = m.remove(index);
			m.add(index -1, o);
			inList.setSelectedIndex(index-1);
			
		}else if(e.getActionCommand().equals("DOWN")) {
			int index = activeList.getSelectedIndex();
			if(index == -1 ) return;
			DefaultListModel m = (DefaultListModel) activeList.getModel();
			if(index == m.getSize()-1) return;			
			Object o = m.remove(index);
			m.add(index +1, o);
			inList.setSelectedIndex(index+1);
		}else if(e.getActionCommand().equals("LEFT")) {
			int i1 = inList.getSelectedIndex();
			if(i1 == -1) return;
			DefaultListModel inM = (DefaultListModel) inList.getModel();
			DefaultListModel outM = (DefaultListModel) outList.getModel();
			Object o = inM.remove(i1);
			outM.addElement(o);			
		}else if(e.getActionCommand().equals("RIGHT")) {
			int i1 = outList.getSelectedIndex();
			if(i1 == -1) return;
			DefaultListModel inM = (DefaultListModel) inList.getModel();
			DefaultListModel outM = (DefaultListModel) outList.getModel();
			Object o = outM.remove(i1);
			inM.addElement(o);						
		}else if(e.getActionCommand().equals("SEPERATOR")) {
			Action seperator = new Seperator();
			((DefaultListModel)inList.getModel()).add(inList.getSelectedIndex() != -1 ? inList.getSelectedIndex() : inList.getModel().getSize(), seperator);
		}
	}

	public static void showDialog(List<Action> in, List<Action> out, final PowerPane pane, final ToolbarConfiguration config) {
		final ToolbarConfigPanel p =new ToolbarConfigPanel(in, out);
		final JDialog d = new JDialog((Frame) SwingUtilities.getAncestorOfClass(JFrame.class, pane));
		d.setTitle("Configure Toolbar");
		d.setContentPane(p);
		d.pack();
		d.setLocationRelativeTo(pane);
		p.getOk().addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				config.setUsedActions(p.getUsedActions());
				d.dispose();
			}
		});
		p.getApply().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				config.setUsedActions(p.getUsedActions());
			}
		});
		p.getCancel().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				d.dispose();
			}
		});

		d.setVisible(true);
		

	}
	
	protected List<String> getUsedActions() {
		return inList.getValues();
	}

	protected List<Tool> getInTools() {
		ArrayList<Tool> in = new ArrayList<Tool>();
		DefaultListModel m = (DefaultListModel) inList.getModel();
		for (int i = 0; i < m.size(); i++) {
			in.add((Tool) m.getElementAt(i));
		}
		return in;
	}


	class ControllerPanel extends JPanel{
		private JButton up;
		private JButton down;
		private JButton left;
		private JButton right;
		
		public ControllerPanel(ActionListener action) {
			super();
			init(action);	
		}
		private void init(ActionListener action) {
			up = new JButton(/*ResourceManager.getIcon("epos.ui.view.up", this)*/);
			up.setActionCommand("UP");
			up.addActionListener(action);
			down = new JButton(/*ResourceManager.getIcon("epos.ui.view.down", this)*/);
			down.setActionCommand("DOWN");
			down.addActionListener(action);
			left = new JButton(/*ResourceManager.getIcon("epos.ui.view.left", this)*/);
			left.setActionCommand("LEFT");
			left.addActionListener(action);
			right = new JButton(/*ResourceManager.getIcon("epos.ui.view.right", this)*/);
			right.setActionCommand("RIGHT");
			right.addActionListener(action);
			
			setLayout(new FormLayout("p,2dlu,p,2dlu,p","f:p:g,c:p,2dlu,p,2dlu,p,f:p:g"));
			CellConstraints cc = new CellConstraints();
			add(up, cc.xy(3, 2));
			add(down, cc.xy(3, 6));
			add(left, cc.xy(1, 4));
			add(right, cc.xy(5, 4));
		}
		public JButton getDown() {
			return down;
		}
		public JButton getLeft() {
			return left;
		}
		public JButton getRight() {
			return right;
		}
		public JButton getUp() {
			return up;
		}
	}


	public JButton getApply() {
		return apply;
	}

	public JButton getCancel() {
		return cancel;
	}

	public JButton getOk() {
		return ok;
	}

	public static class Seperator extends AbstractAction{
		public Seperator(){
			this.putValue(Action.NAME, ToolbarConfiguration.SEPERATOR);			
		}
		public void actionPerformed(ActionEvent e) {
		}
		
	}
}
