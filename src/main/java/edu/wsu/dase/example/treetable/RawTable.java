package edu.wsu.dase.example.treetable;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class RawTable extends JTable {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		RawTableDataModel model = new RawTableDataModel();
		
		DefaultTableModel dtm = new DefaultTableModel(0,5);
		dtm.addRow(new Vector(
				Arrays.asList(new Object[] { "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) })));
		
		JTable rawTable = new JTable(model);

		model.addRow(new Vector(
				Arrays.asList(new Object[] { "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) })));


		/**
		 * { "Kathy", "Smith", "Snowboarding", new Integer(5), new
		 * Boolean(false) }, { "John", "Doe", "Rowing", new Integer(3), new
		 * Boolean(true) }, { "Sue", "Black", "Knitting", new Integer(2), new
		 * Boolean(false) }, { "Jane", "White", "Speed reading", new
		 * Integer(20), new Boolean(true) }, { "Joe", "Brown", "Pool", new
		 * Integer(10), new Boolean(false) }
		 */
		JScrollPane scrollPane = new JScrollPane(rawTable);

		JFrame frame = new JFrame("Table");
		frame.add(scrollPane);
		frame.setSize(600, 400);
		frame.setVisible(true);

	}

}
