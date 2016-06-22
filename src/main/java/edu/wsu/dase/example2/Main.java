package edu.wsu.dase.example2;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import edu.wsu.dase.example2.AbstractCellEditor;
import edu.wsu.dase.example2.JTreeTable.TreeTableCellEditor;
import edu.wsu.dase.example2.JTreeTable.TreeTableCellRenderer;
import edu.wsu.dase.example2.MyTreeTableModel.Entry;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Vector;
  
public class Main extends JFrame {
   protected MyTreeTableModel  model;
   protected MyJTreeTable        treeTable;
  
   public Main() {
      super("Main");
  
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent we) {
            System.exit(0);
         }
      });
  
      model     = new MyTreeTableModel();
      treeTable = new MyJTreeTable(model);
  
      getContentPane().add(new JScrollPane(treeTable));
  
      try {
        // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
         SwingUtilities.updateComponentTreeUI(this);
         System.out.println("row: "+treeTable.getRowCount());
      }
      catch(Exception e) {
         e.printStackTrace();
      }
  
      pack();
      show();
   }
  
   public static void main(String[] args) {
      new Main();
   }
}

class MyTreeTableModel extends AbstractTreeTableModel {
	  
	   // Names of the columns.
	   static protected String[] cNames = {"Column 1", "Column 2",
	                                       "Column 3", "Column 4", "Col5"};
	   // Types of the columns.
	   static protected Class[]  cTypes = { TreeTableModel.class, String.class,
	                                        String.class, String.class, Boolean.class };
	   static Entry rootEntry; 
	  
	   /******** Entry class represents the parent and leaf nodes **********/
	   static class Entry {
	      private String name;
	      private boolean isLeaf;
	      private Vector children = new Vector();
	  
	      public Entry(String name, boolean isLeaf) {
	         this.name = name;
	         this.isLeaf = isLeaf;
	      }
	  
	      public Vector getChildren() {
	         return children;
	      }
	  
	      public boolean isLeaf() {
	         return isLeaf;
	      }
	  
	      public String getName() {
	         return name;
	      }
	  
	      public String toString() {
	         return name;
	      }
	   }
	  
	   static 
	   {
	      rootEntry = new Entry("rootentry", false);
	      rootEntry.getChildren().addElement(new Entry("test1", true));
	      rootEntry.getChildren().addElement(new Entry("test2", true));
	 
	      Entry subEntry1 = new Entry("subentry1", false);
	      subEntry1.getChildren().addElement(new Entry("test3", true));
	      subEntry1.getChildren().addElement(new Entry("test4", true));
	      Entry subEntry2 = new Entry("subentry2", false);
	      subEntry2.getChildren().addElement(new Entry("test5", true));
	      subEntry2.getChildren().addElement(new Entry("test6", true));
	      rootEntry.getChildren().addElement(subEntry1);
	      rootEntry.getChildren().addElement(subEntry2);
	   }
	   /*************************************/
	  
	   public MyTreeTableModel() {
	      super(rootEntry);
	   }
	  
	   public int getChildCount(Object node) {
	      if (!((Entry) node).isLeaf()) {
	         return ((Entry) node).getChildren().size();
	      }
	      return 0;
	   }
	  
	   public Object getChild(Object node, int i) {
	      return ((Entry) node).getChildren().elementAt(i);
	   }
	  
	   public boolean isLeaf(Object node) {
	      return ((Entry) node).isLeaf();
	   }
	  
	   public int getColumnCount() {
	      return cNames.length;
	   }
	  
	   public String getColumnName(int column) {
	      return cNames[column];
	   }
	  
	   public Class getColumnClass(int column) {
	      return cTypes[column];
	   }
	  
	   public Object getValueAt(Object node, int column) {
	      switch(column) {
	         case 0:
	            return node;
	         case 1:
	            return "value1";
	         case 2:
	            return "value2";
	         case 3:
	            return "value3";
	      }
	    
	      return null;
	   }
	}

class MyJTreeTable extends JTable {
	
	protected TreeTableCellRenderer treeTableCellRenderer;

	public MyJTreeTable(TreeTableModel treeTableModel) {
		super();

		System.out.println("called");
		// Create the treeTableCellRenderer. It will be used as a renderer and editor.
		treeTableCellRenderer = new TreeTableCellRenderer(treeTableModel);

		// Install a tableModel representing the visible rows in the treeTableCellRenderer.
		super.setModel(new TreeTableModelAdapter(treeTableModel, treeTableCellRenderer));

		// Force the JTable and JTree to share their row selection models.
		treeTableCellRenderer.setSelectionModel(new DefaultTreeSelectionModel() {
			// Extend the implementation of the constructor, as if:
			/* public this() */ {
				setSelectionModel(listSelectionModel);
			}
		});
		
		// Make the treeTableCellRenderer and table row heights the same.
		treeTableCellRenderer.setRowHeight(getRowHeight());

		// Install the treeTableCellRenderer editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, treeTableCellRenderer);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

		setShowGrid(true);
		setIntercellSpacing(new Dimension(0,0));
	}

	/*
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to paint the
	 * renderers and editors and overriding setBounds() below is not the right
	 * thing to do for an editor. Returning -1 for the editing row in this case,
	 * ensures the editor is never painted.
	 */
	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
	}

	//
	// The renderer used to display the treeTableCellRenderer nodes, a JTree.
	//

	public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected int visibleRow;

		public TreeTableCellRenderer(TreeModel model) {
			super(model);
			 System.out.println("inside constructor: visibleRow: "+visibleRow);
		}

		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, MyJTreeTable.this.getHeight());
		}

		public void paint(Graphics g) {
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);

	         System.out.println("visibleRow: "+visibleRow);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (isSelected)
				setBackground(table.getSelectionBackground());
			else
				setBackground(table.getBackground());

			visibleRow = row;
			System.out.println("getTableCellRendererComponent : visibleRow: "+visibleRow);
			return this;
		}
	}

	//
	// The editor used to interact with treeTableCellRenderer nodes, a JTree.
	//

	public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
			return treeTableCellRenderer;
		}
	}

}
