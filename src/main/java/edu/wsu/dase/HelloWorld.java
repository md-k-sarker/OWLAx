package edu.wsu.dase;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class HelloWorld extends JFrame {

	private static final long serialVersionUID = -2707712944901661771L;

	public HelloWorld() {
		super("Dase, World!");

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try {

			Object v1 = graph.insertVertex(parent, null, "Dase", 20, 20, 80, 30);
			Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, 80, 30, "defaultVertex;fillColor=blue");
			graph.insertEdge(parent, null, "Edge", v1, v2);
		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);

		/*JPanel pnl = new JPanel();
		pnl.setLayout(new BorderLayout());
		JLabel btn = new JLabel("Clicke me");
		JLabel btn2 = new JLabel("Click me2");
		
		pnl.add(btn,BorderLayout.NORTH);
		pnl.add(btn2,BorderLayout.CENTER);
		
		if(JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(this, pnl, "Create Class", JOptionPane.PLAIN_MESSAGE,
				JOptionPane.PLAIN_MESSAGE, null, null, null)){
			
		}*/
	

	}

	public static void main(String[] args) {
		HelloWorld frame = new HelloWorld();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 320);
		frame.setVisible(true);

	}

}
