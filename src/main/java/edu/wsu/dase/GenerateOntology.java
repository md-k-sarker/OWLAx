package edu.wsu.dase;

import org.semanticweb.owlapi.model.OWLOntology;

import com.mxgraph.analysis.mxGraphAnalysis;
import com.mxgraph.analysis.mxICostFunction;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

import edu.wsu.dase.swing.GraphEditor;
import edu.wsu.dase.swing.editor.BasicGraphEditor;

public class GenerateOntology {

	mxGraph graph;
	Object root;
	mxGraphModel model;

	public GenerateOntology(BasicGraphEditor editor) {
		this.graph = editor.getGraphComponent().getGraph();
		this.model = (mxGraphModel) graph.getModel();
		this.root = graph.getDefaultParent();
	}

	/**
	 * mxICostFunction cf = mxDistanceCostFunction(); Object[] v =
	 * graph.getChildVertices(graph.getDefaultParent()); Object[] e =
	 * graph.getChildEdges(graph.getDefaultParent()); mxGraphAnalysis mga =
	 * mxGraphAnalysis.getInstance();
	 */

	public OWLOntology saveOntology() {

		Object[] v = graph.getChildVertices(graph.getDefaultParent());
		Object[] e = graph.getChildEdges(graph.getDefaultParent());
		mxGraphAnalysis mga = mxGraphAnalysis.getInstance();
		
		

		Object[] cells = graph.getChildCells(root);

		for (Object cell : cells) {
			if (cell instanceof mxCell) {
				System.out.println("name " + ((mxCell) cell).getValue() + " celltype: " + ((mxCell) cell).isOWLClass());
			}
			// System.out.println("label: "+cell.toString());
		}

		return null;
	}
}
