package edu.wsu.dase;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class OdpMainUITab extends OWLWorkspaceViewsTab {

	
	
	@Override
	public void initialise() {
		// TODO Auto-generated method stub
		super.initialise();
		setToolTipText("ODP Protege Plugin");
		setLayout(new BorderLayout());
		/*
		GraphEditor editor = new GraphEditor();
		add(new EditorMenuBar(editor));
		add(editor);*/
		
		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try
		{
			Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
					30);
			Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
					80, 30);
			graph.insertEdge(parent, null, "Edge", v1, v2);
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		add(graphComponent,BorderLayout.CENTER);
		
		 //FileDragDemo mainPanel = new FileDragDemo();
		 //add(mainPanel,BorderLayout.NORTH);
		 
		//setDragEnabled(true);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	private static final long serialVersionUID = 1L;

}
