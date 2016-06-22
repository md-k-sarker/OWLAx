package edu.wsu.dase.example.treetable;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

public class RawTree extends JTree{

	public RawTree() {
		// TODO Auto-generated constructor stub
		
		DefaultMutableTreeNode root =  new DefaultMutableTreeNode("root",true);
		DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("child");
		root.add(child1);
		
		DefaultTreeModel dtm = new DefaultTreeModel(root);
		this.setModel(dtm);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RawTree raw = new RawTree();
		
		JFrame frame = new JFrame("Tree");
		frame.add(raw);
		frame.setSize(600, 400);
		frame.setVisible(true);
	}
	
	public class TreeCellRenderer extends DefaultTreeCellRenderer{
		
	}

}
