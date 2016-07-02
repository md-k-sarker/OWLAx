package edu.wsu.dase;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * The implementation details:
 * 
 * Created a new class that extends JTree
 * 
 * Replaced the 'TreeCellRenderer' by a new class I created, that shows a
 * checkbox and a label. The checkbox selection is changed instead of the label
 * background and border.
 * 
 * Totally terminated the selection mechanism. Replaced the 'Selection Model' by
 * a 'DefaultTreeSelectionModel' overridden inline, that has empty
 * implementation Created new event type for checking of the checkboxes Created
 * special data structures that help to indicate fast the state of each node
 * 
 * @author sarker
 *
 */

public class JCheckBoxTree extends JTree {

	private static final long serialVersionUID = -4194122328392241790L;

	JCheckBoxTree selfPointer = this;

	// Defining data structure that will enable to fast check-indicate the state
	// of each node
	// It totally replaces the "selection" mechanism of the JTree
	private class CheckedNode {
		boolean isSelected;
		boolean hasChildren;
		boolean allChildrenSelected;

		public CheckedNode(boolean isSelected_, boolean hasChildren_, boolean allChildrenSelected_) {
			isSelected = isSelected_;
			hasChildren = hasChildren_;
			allChildrenSelected = allChildrenSelected_;
		}
	}

	HashMap<TreePath, CheckedNode> nodesCheckingState;
	HashSet<TreePath> checkedPaths = new HashSet<TreePath>();

	// Defining a new event type for the checking mechanism and preparing
	// event-handling mechanism
	protected EventListenerList listenerList = new EventListenerList();

	public class CheckChangeEvent extends EventObject {
		private static final long serialVersionUID = -8100230309044193368L;

		public CheckChangeEvent(Object source) {
			super(source);
		}
	}

	public interface CheckChangeEventListener extends EventListener {
		public void checkStateChanged(CheckChangeEvent event);
	}

	public void addCheckChangeEventListener(CheckChangeEventListener listener) {
		listenerList.add(CheckChangeEventListener.class, listener);
	}

	public void removeCheckChangeEventListener(CheckChangeEventListener listener) {
		listenerList.remove(CheckChangeEventListener.class, listener);
	}

	void fireCheckChangeEvent(CheckChangeEvent evt) {
		Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == CheckChangeEventListener.class) {
				((CheckChangeEventListener) listeners[i + 1]).checkStateChanged(evt);
			}
		}
	}

	// Override
	public void setModel(TreeModel newModel) {
		super.setModel(newModel);
		resetCheckingState();
	}

	// New method that returns only the checked paths (totally ignores original
	// "selection" mechanism)
	public TreePath[] getCheckedPaths() {
		return checkedPaths.toArray(new TreePath[checkedPaths.size()]);
	}

	// Returns true in case that the node is selected, has children but not all
	// of them are selected
	public boolean isSelectedPartially(TreePath path) {
		CheckedNode cn = nodesCheckingState.get(path);
		return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
	}

	private void resetCheckingState() {
		nodesCheckingState = new HashMap<TreePath, CheckedNode>();
		checkedPaths = new HashSet<TreePath>();
		// checkedOWLAxioms = new HashSet<OWLAxiom>();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getModel().getRoot();
		if (node == null) {
			return;
		}
		addSubtreeToCheckingStateTracking(node);
	}

	// Creating data structure of the current model for the checking mechanism
	private void addSubtreeToCheckingStateTracking(DefaultMutableTreeNode node) {
		try {
			TreeNode[] path = node.getPath();
			TreePath tp = new TreePath(path);

			CheckedNode cn = null;
			if (node.getUserObject() instanceof UserObjectforTreeView) {
				UserObjectforTreeView userObject = (UserObjectforTreeView) node.getUserObject();
				if (userObject.isAxiom()) {
					OWLAxiom axiom = userObject.getAxiom();
					if (activeontologyAxioms != null) {
						if (activeontologyAxioms.contains(axiom)) {
							cn = new CheckedNode(true, node.getChildCount() > 0, false);
						}else{
							cn = new CheckedNode(false, node.getChildCount() > 0, false);
						}
					}
				} else {
					cn = new CheckedNode(false, node.getChildCount() > 0, false);
				}
			}
			if (cn != null)
				nodesCheckingState.put(tp, cn);
			for (int i = 0; i < node.getChildCount(); i++) {
				addSubtreeToCheckingStateTracking(
						(DefaultMutableTreeNode) tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(getParent(), e.getMessage());

			JOptionPane.showMessageDialog(getParent(), e.getCause());

			JOptionPane.showMessageDialog(getParent(), e.getStackTrace());
		}
	}

	// Overriding cell renderer by a class that ignores the original "selection"
	// mechanism
	// It decides how to show the nodes due to the checking-mechanism
	private class CheckBoxCellRenderer extends JPanel implements TreeCellRenderer {
		private static final long serialVersionUID = -7341833835878991719L;
		JCheckBox checkBox;

		public CheckBoxCellRenderer(OWLEditorKit owlEditorKit) {
			super();
			this.setLayout(new BorderLayout());
			checkBox = new JCheckBox();
			add(checkBox, BorderLayout.CENTER);
			setOpaque(false);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			TreePath tp = new TreePath(node.getPath());
			CheckedNode cn = nodesCheckingState.get(tp);
			if (cn == null) {
				return this;
			}
			checkBox.setSelected(cn.isSelected);
			Object obj = node.getUserObject();

			if (obj instanceof UserObjectforTreeView) {
				UserObjectforTreeView _obj = (UserObjectforTreeView) obj;
				if (!_obj.isAxiom()) {
					setForeground(new Color(98, 79, 219));
				} else if (activeOntology.containsAxiomIgnoreAnnotations(_obj.getAxiom(), true)) {
					// checkBox.setSelected(true);
					// updatePredecessorsWithCheckMode(tp, true);
					// checkedPaths.add(tp);
				}

			}
			checkBox.setText(obj.toString());
			checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
			return this;
		}
	}

	// take the activeOntology and save in memory
	OWLOntology activeOntology;
	static Set<OWLAxiom> activeontologyAxioms;

	public JCheckBoxTree() {

	}

	public JCheckBoxTree(DefaultMutableTreeNode root, OWLEditorKit editorKit) {
		super(root);

		// super.setModel(model);
		// Disabling toggling by double-click
		this.setToggleClickCount(0);

		// Overriding cell renderer by new one defined above
		CheckBoxCellRenderer cellRenderer = new CheckBoxCellRenderer(editorKit);
		this.setCellRenderer(cellRenderer);
		activeOntology = editorKit.getOWLModelManager().getActiveOntology();

		// Overriding selection model by an empty one
		DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel() {
			private static final long serialVersionUID = -8190634240451667286L;

			// Totally disabling the selection mechanism
			public void setSelectionPath(TreePath path) {
			}

			public void addSelectionPath(TreePath path) {
			}

			public void removeSelectionPath(TreePath path) {
			}

			public void setSelectionPaths(TreePath[] pPaths) {
			}
		};
		// Calling checking mechanism on mouse click
		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent arg0) {
				TreePath tp = selfPointer.getPathForLocation(arg0.getX(), arg0.getY());
				if (tp == null) {
					return;
				}
				boolean checkMode = !nodesCheckingState.get(tp).isSelected;
				checkSubTree(tp, checkMode);
				updatePredecessorsWithCheckMode(tp, checkMode);
				// Firing the check change event
				fireCheckChangeEvent(new CheckChangeEvent(new Object()));
				// Repainting tree after the data structures were updated
				selfPointer.repaint();
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseReleased(MouseEvent arg0) {
			}
		});
		this.setSelectionModel(dtsm);
	}

	// When a node is checked/unchecked, updating the states of the predecessors
	protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
		TreePath parentPath = tp.getParentPath();

		// If it is the root, stop the recursive calls and return
		if (parentPath == null) {
			return;
		}
		CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

		parentCheckedNode.allChildrenSelected = true;
		parentCheckedNode.isSelected = false;
		OWLAxiom axiom = null;

		for (int i = 0; i < parentNode.getChildCount(); i++) {

			TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
			CheckedNode childCheckedNode = nodesCheckingState.get(childPath);
			// It is enough that even one subtree is not fully selected
			// to determine that the parent is not fully selected
			if (!childCheckedNode.allChildrenSelected) {
				parentCheckedNode.allChildrenSelected = false;
			}
			// If at least one child is selected, selecting also the parent
			if (childCheckedNode.isSelected) {
				parentCheckedNode.isSelected = true;
			}
		}
		if (parentCheckedNode.isSelected) {
			checkedPaths.add(parentPath);
		} else {
			checkedPaths.remove(parentPath);
		}
		// Go to upper predecessor
		updatePredecessorsWithCheckMode(parentPath, check);
	}

	// Recursively checks/unchecks a subtree
	protected void checkSubTree(TreePath tp, boolean check) {
		CheckedNode cn = nodesCheckingState.get(tp);
		cn.isSelected = check;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
		for (int i = 0; i < node.getChildCount(); i++) {
			checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check);
		}
		cn.allChildrenSelected = check;
		if (check) {
			checkedPaths.add(tp);
		} else {
			checkedPaths.remove(tp);
		}
	}

	public void setSelectedOtherAxioms(TreePath tp) {
		//checkSubTree(tp, true);
		//updatePredecessorsWithCheckMode(tp, true);
		
		boolean checkMode = nodesCheckingState.get(tp).isSelected;
		checkSubTree(tp, checkMode);
		updatePredecessorsWithCheckMode(tp, checkMode);
	}

}