package edu.wsu.dase;

import org.semanticweb.owlapi.model.OWLOntology;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

import edu.wsu.dase.swing.GraphEditor;

public class GenerateOntology {

	mxGraph graph;
	Object root;
	mxGraphModel model;

	public GenerateOntology(GraphEditor editor) {
		this.graph = editor.getGraphComponent().getGraph();
		this.model = (mxGraphModel) graph.getModel();
		this.root = graph.getDefaultParent();
	}

	public OWLOntology saveOntology() {

		mxCell[] cells = (mxCell[]) graph.getChildCells(root);
		
		for(mxCell cell: cells){
			System.out.println("label: "+cell.getValue());
		}
		
		return null;
	}
}
