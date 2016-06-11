/**
 * Copyright (c) 2006-2012, JGraph Ltd */
package edu.wsu.dase;

import java.awt.Color;
import java.awt.Point;
import java.net.URL;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import edu.wsu.dase.swing.editor.BasicGraphEditor;
import edu.wsu.dase.swing.editor.EditorMenuBar;
import edu.wsu.dase.swing.editor.EditorPalette;
import edu.wsu.dase.util.CustomEntityType;

public class GraphEditor extends BasicGraphEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4601740824088314699L;

	/**
	 * Holds the shared number formatter.
	 * 
	 * @see NumberFormat#getInstance()
	 */
	public static final NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Holds the URL for the icon to be used as a handle for creating new
	 * connections. This is currently unused.
	 */
	public static URL url = null;

	private int edgeWidth = 60;
	private int edgeHeight = 80;
	private int vertexWidth = 60;
	private int vertexHeight = 50;
	private int edgeStrokeWidth = 3;
	private int edgEendSize = 8;

	// GraphEditor.class.getResource("/images/connector.gif");

	public GraphEditor(OWLModelManager protegeOWLModelManager) {
		this(protegeOWLModelManager, "DaseGraph Editor", new CustomGraphComponent(new CustomGraph()));
	}

	/**
	 * 
	 */
	public GraphEditor(OWLModelManager protegeOWLModelManager, String appTitle, mxGraphComponent component) {
		super(protegeOWLModelManager, appTitle, component);

		final mxGraph graph = graphComponent.getGraph();

		// Creates the shapes palette
		EditorPalette shapesPalette = insertPalette(mxResources.get("shapes"));
		/*
		 * EditorPalette imagesPalette =
		 * insertPalette(mxResources.get("images")); EditorPalette
		 * symbolsPalette = insertPalette(mxResources.get("symbols"));
		 */

		// Sets the edge template to be used for creating new edges if an edge
		// is clicked in the shape palette
		shapesPalette.addListener(mxEvent.SELECT, new mxIEventListener() {
			public void invoke(Object sender, mxEventObject evt) {
				Object tmp = evt.getProperty("transferable");

				if (tmp instanceof mxGraphTransferable) {
					mxGraphTransferable t = (mxGraphTransferable) tmp;
					Object cell = t.getCells()[0];

					if (graph.getModel().isEdge(cell)) {
						((CustomGraph) graph).setEdgeTemplate(cell);
					}
				}
			}

		});

		shapesPalette.addTemplate(CustomEntityType.CLASS.getName(),
				new ImageIcon(GraphEditor.class.getResource("/images/rectangle.png")), "rectangle", vertexWidth,
				vertexHeight, "");

		shapesPalette.addTemplate(CustomEntityType.NAMED_INDIVIDUAL.getName(),
				new ImageIcon(GraphEditor.class.getResource("/images/ellipse.png")),
				"ellipse;shape=ellipse;fillColor=white;gradientColor=white", vertexWidth, vertexHeight, "");

		shapesPalette.addTemplate(CustomEntityType.DATATYPE.getName(),
				new ImageIcon(GraphEditor.class.getResource("/images/rounded.png")),
				"rounded=1;fillColor=#FFFA01;gradientColor=#FFFA01", vertexWidth, vertexHeight, "");

		shapesPalette.addTemplate(CustomEntityType.LITERAL.getName(),
				new ImageIcon(GraphEditor.class.getResource("/images/doublerectangle.png")),
				"rectangle;shape=doubleRectangle;fillColor=white;gradientColor=white", vertexWidth, vertexHeight, "");

		shapesPalette.addEdgeTemplate(CustomEntityType.OBJECT_PROPERTY.getName(),
				new ImageIcon(GraphEditor.class.getResource("/images/arrowblack.png")),
				"edgeStyle=mxEdgeStyle.OrthConnector;strokeWidth=3;strokeColor=black;endArrow=classic;endSize=8",
				edgeWidth, edgeHeight, "");

		shapesPalette.addEdgeTemplate(CustomEntityType.DATA_PROPERTY.getName(),
				new ImageIcon(GraphEditor.class.getResource("/images/dataproperty .png")),
				"edgeStyle=mxEdgeStyle.OrthConnector;strokeWidth=3;strokeColor=#999999;endArrow=block;endSize=8",
				edgeWidth, edgeHeight, "");

		shapesPalette.addEdgeTemplate(CustomEntityType.RDFTYPE.getName(),
				new ImageIcon(GraphEditor.class.getResource("/images/connect.png")), null, edgeWidth, edgeHeight,
				CustomEntityType.RDFTYPE.getName());

		shapesPalette.addEdgeTemplate(CustomEntityType.RDFSSUBCLASS_OF.getName(),
				new ImageIcon(GraphEditor.class.getResource("/images/connect.png")), null, edgeWidth, edgeHeight,
				CustomEntityType.RDFSSUBCLASS_OF.getName());

		/*
		 * shapesPalette.addTemplate("Named Individual", new
		 * ImageIcon(GraphEditor.class.getResource("/images/rhombus.png")),
		 * "rhombus", 100, 80, "");
		 */

		// Adds some template cells for dropping into the graph
		/*
		 * shapesPalette.addTemplate("Container", new
		 * ImageIcon(GraphEditor.class.getResource("/images/swimlane.png")),
		 * "swimlane", 280, 280, "Container"); shapesPalette.addTemplate("Icon",
		 * new ImageIcon(GraphEditor.class.getResource("/images/rounded.png")),
		 * "icon;image=/images/wrench.png", 70, 70, "Icon");
		 * shapesPalette.addTemplate("Label", new
		 * ImageIcon(GraphEditor.class.getResource("/images/rounded.png")),
		 * "label;image=/images/gear.png", 130, 50, "Label");
		 */

		/*
		 * shapesPalette.addTemplate("Individual", new
		 * ImageIcon(GraphEditor.class.getResource("/images/triangle.png")),
		 * "triangle", 120, 160, "");
		 */

		/*
		 * shapesPalette.addTemplate("Ellipse", new
		 * ImageIcon(GraphEditor.class.getResource("/images/ellipse.png")),
		 * "ellipse", 160, 160, ""); shapesPalette.addTemplate("Double Ellipse",
		 * new
		 * ImageIcon(GraphEditor.class.getResource("/images/doubleellipse.png"))
		 * , "ellipse;shape=doubleEllipse", 160, 160, "");
		 */

		/*
		 * shapesPalette.addTemplate("Horizontal Line", new
		 * ImageIcon(GraphEditor.class.getResource("/images/hline.png")),
		 * "line", 160, 10, ""); shapesPalette.addTemplate("Hexagon", new
		 * ImageIcon(GraphEditor.class.getResource("/images/hexagon.png")),
		 * "shape=hexagon", 160, 120, ""); shapesPalette.addTemplate("Cylinder",
		 * new ImageIcon(GraphEditor.class.getResource("/images/cylinder.png")),
		 * "shape=cylinder", 120, 160, ""); shapesPalette.addTemplate("Actor",
		 * new ImageIcon(GraphEditor.class.getResource("/images/actor.png")),
		 * "shape=actor", 120, 160, ""); shapesPalette.addTemplate("Cloud", new
		 * ImageIcon(GraphEditor.class.getResource("/images/cloud.png")),
		 * "ellipse;shape=cloud", 160, 120, "");
		 */

		/*
		 * shapesPalette.addEdgeTemplate("Straight", new
		 * ImageIcon(GraphEditor.class.getResource("/images/straight.png")),
		 * "edgeStyle=mxEdgeStyle.OrthConnector;strokeWidth=3;strokeColor=black;endArrow=block;endSize=5",
		 * 120, 120, "");
		 * 
		 * shapesPalette.addEdgeTemplate("Vertical Connector", new
		 * ImageIcon(GraphEditor.class.getResource("/images/vertical.png")),
		 * "vertical", 100, 100, ""); shapesPalette.addEdgeTemplate(
		 * "Entity Relation", new
		 * ImageIcon(GraphEditor.class.getResource("/images/entity.png")),
		 * "entity", 100, 100, "");
		 */

		/*
		 * shapesPalette.addEdgeTemplate("AnnotationProperty", new
		 * ImageIcon(GraphEditor.class.getResource("/images/arrow.png")),
		 * "arrow", 70, 70, "");
		 */

		/*
		 * imagesPalette.addTemplate("Bell", new
		 * ImageIcon(GraphEditor.class.getResource("/images/bell.png")),
		 * "image;image=/images/bell.png", 50, 50, "Bell");
		 * imagesPalette.addTemplate("Box", new
		 * ImageIcon(GraphEditor.class.getResource("/images/box.png")),
		 * "image;image=/images/box.png", 50, 50, "Box");
		 * imagesPalette.addTemplate("Cube", new
		 * ImageIcon(GraphEditor.class.getResource("/images/cube_green.png")),
		 * "image;image=/images/cube_green.png", 50, 50, "Cube");
		 * imagesPalette.addTemplate("User", new
		 * ImageIcon(GraphEditor.class.getResource("/images/dude3.png")),
		 * "roundImage;image=/images/dude3.png", 50, 50, "User");
		 * imagesPalette.addTemplate("Earth", new
		 * ImageIcon(GraphEditor.class.getResource("/images/earth.png")),
		 * "roundImage;image=/images/earth.png", 50, 50, "Earth");
		 * imagesPalette.addTemplate("Gear", new
		 * ImageIcon(GraphEditor.class.getResource("/images/gear.png")),
		 * "roundImage;image=/images/gear.png", 50, 50, "Gear");
		 * imagesPalette.addTemplate("Home", new
		 * ImageIcon(GraphEditor.class.getResource("/images/house.png")),
		 * "image;image=/images/house.png", 50, 50, "Home");
		 * imagesPalette.addTemplate("Package", new
		 * ImageIcon(GraphEditor.class.getResource("/images/package.png")),
		 * "image;image=/images/package.png", 50, 50, "Package");
		 * imagesPalette.addTemplate("Printer", new
		 * ImageIcon(GraphEditor.class.getResource("/images/printer.png")),
		 * "image;image=/images/printer.png", 50, 50, "Printer");
		 * imagesPalette.addTemplate("Server", new
		 * ImageIcon(GraphEditor.class.getResource("/images/server.png")),
		 * "image;image=/images/server.png", 50, 50, "Server");
		 * imagesPalette.addTemplate("Workplace", new
		 * ImageIcon(GraphEditor.class.getResource("/images/workplace.png")),
		 * "image;image=/images/workplace.png", 50, 50, "Workplace");
		 * imagesPalette.addTemplate("Wrench", new
		 * ImageIcon(GraphEditor.class.getResource("/images/wrench.png")),
		 * "roundImage;image=/images/wrench.png", 50, 50, "Wrench");
		 * 
		 * symbolsPalette.addTemplate("Cancel", new
		 * ImageIcon(GraphEditor.class.getResource("/images/cancel_end.png")),
		 * "roundImage;image=/images/cancel_end.png", 80, 80, "Cancel");
		 * symbolsPalette.addTemplate("Error", new
		 * ImageIcon(GraphEditor.class.getResource("/images/error.png")),
		 * "roundImage;image=/images/error.png", 80, 80, "Error");
		 * symbolsPalette.addTemplate("Event", new
		 * ImageIcon(GraphEditor.class.getResource("/images/event.png")),
		 * "roundImage;image=/images/event.png", 80, 80, "Event");
		 * symbolsPalette.addTemplate("Fork", new
		 * ImageIcon(GraphEditor.class.getResource("/images/fork.png")),
		 * "rhombusImage;image=/images/fork.png", 80, 80, "Fork");
		 * symbolsPalette.addTemplate("Inclusive", new
		 * ImageIcon(GraphEditor.class.getResource("/images/inclusive.png")),
		 * "rhombusImage;image=/images/inclusive.png", 80, 80, "Inclusive");
		 * symbolsPalette.addTemplate("Link", new
		 * ImageIcon(GraphEditor.class.getResource("/images/link.png")),
		 * "roundImage;image=/images/link.png", 80, 80, "Link");
		 * symbolsPalette.addTemplate("Merge", new
		 * ImageIcon(GraphEditor.class.getResource("/images/merge.png")),
		 * "rhombusImage;image=/images/merge.png", 80, 80, "Merge");
		 * symbolsPalette.addTemplate("Message", new
		 * ImageIcon(GraphEditor.class.getResource("/images/message.png")),
		 * "roundImage;image=/images/message.png", 80, 80, "Message");
		 * symbolsPalette.addTemplate("Multiple", new
		 * ImageIcon(GraphEditor.class.getResource("/images/multiple.png")),
		 * "roundImage;image=/images/multiple.png", 80, 80, "Multiple");
		 * symbolsPalette.addTemplate("Rule", new
		 * ImageIcon(GraphEditor.class.getResource("/images/rule.png")),
		 * "roundImage;image=/images/rule.png", 80, 80, "Rule");
		 * symbolsPalette.addTemplate("Terminate", new
		 * ImageIcon(GraphEditor.class.getResource("/images/terminate.png")),
		 * "roundImage;image=/images/terminate.png", 80, 80, "Terminate");
		 * symbolsPalette.addTemplate("Timer", new
		 * ImageIcon(GraphEditor.class.getResource("/images/timer.png")),
		 * "roundImage;image=/images/timer.png", 80, 80, "Timer");
		 */
	}

	/**
	 * 
	 */
	public static class CustomGraphComponent extends mxGraphComponent {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6833603133512882012L;

		/**
		 * 
		 * @param graph
		 */
		public CustomGraphComponent(mxGraph graph) {
			super(graph);

			// Sets switches typically used in an editor
			setCenterPage(true);
			setPageVisible(true);
			setGridVisible(true);
			setToolTips(true);

			// creating annonying auto copying target
			// getConnectionHandler().setCreateTarget(true);
			// get rid from annonying auto copying
			getConnectionHandler().setEnabled(false);

			// Loads the defalt stylesheet from an external file
			mxCodec codec = new mxCodec();
			Document doc = mxUtils
					.loadDocument(GraphEditor.class.getResource("/resources/default-style.xml").toString());
			codec.decode(doc.getDocumentElement(), graph.getStylesheet());

			// Sets the background to white
			getViewport().setOpaque(true);
			getViewport().setBackground(Color.WHITE);

		}

		/**
		 * Overrides drop behaviour to set the cell style if the target is not a
		 * valid drop target and the cells are of the same type (eg. both
		 * vertices or both edges).
		 */
		public Object[] importCells(Object[] cells, double dx, double dy, Object target, Point location) {
			if (target == null && cells.length == 1 && location != null) {
				target = getCellAt(location.x, location.y);

				if (target instanceof mxICell && cells[0] instanceof mxICell) {
					mxICell targetCell = (mxICell) target;
					mxICell dropCell = (mxICell) cells[0];

					if (targetCell.isVertex() == dropCell.isVertex() || targetCell.isEdge() == dropCell.isEdge()) {
						// mxIGraphModel model = graph.getModel();
						// model.setStyle(target, model.getStyle(cells[0]));
						graph.setSelectionCell(target);
						return null;
					}
				}
			}
			// show dataTypes as list. will not do this.
			// add dataType
			for (Object cell : cells) {
				mxCell currentCell = (mxCell) cell;
				if (currentCell != null) {
					if (currentCell.getEntityType() == CustomEntityType.DATATYPE) {
						this.labelChanged(currentCell, cellDataTypeValue, null);
					}else if(currentCell.getEntityType() == CustomEntityType.LITERAL){ 
						String cellValue = "\"" + "\"" + "^^" + cellDataTypeValue;
						currentCell.setLiteralDataType(cellDataTypeValue);
						this.labelChanged(currentCell, cellValue, null);
					}
				}
			}
			return super.importCells(cells, dx, dy, target, location);
		}

	}

	/**
	 * A graph that creates new edges from a given template edge.
	 */
	public static class CustomGraph extends mxGraph {
		/**
		 * Holds the edge to be used as a template for inserting new edges.
		 */
		protected Object edgeTemplate;

		/**
		 * Custom graph that defines the alternate edge style to be used when
		 * the middle control point of edges is double clicked (flipped).
		 */
		public CustomGraph() {
			// setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
			
			String abc = "\"kamal\"\"karim^^xsd:String";
			Pattern pattern  = Pattern.compile("\\^\\^(.*?)$");
			Matcher matcher = pattern.matcher(abc);
			while(matcher.find()){
				System.out.println(matcher.group(1));
				OWLDataFactory factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();
				
				OWLDatatype odt = factory.getOWLDatatype(matcher.group(1), new DefaultPrefixManager());
				OWLLiteral literal = factory.getOWLLiteral("karim",odt);
				System.out.println(literal.toString() + "\t "+ literal.getLiteral());
			}
		}

		/**
		 * Sets the edge template to be used to inserting edges.
		 */
		public void setEdgeTemplate(Object template) {
			edgeTemplate = template;
		}

		/**
		 * Prints out some useful information about the cell in the tooltip.
		 */
		public String getToolTipForCell(Object cell) {
			// have to change
			String headTip = "<html>";
			mxGeometry geo = getModel().getGeometry(cell);
			mxCellState state = getView().getState(cell);

			mxCell src = (mxCell) getModel().getTerminal(cell, true);
			mxCell trg = (mxCell) getModel().getTerminal(cell, false);
			mxCell thiscell = (mxCell) cell;

			headTip = headTip + "<h4>Entity Type: " + thiscell.getEntityType().getName() + "</h4>" + "<p>";

			String paragraphTip = thiscell.getValue().toString();
			// tip = tip + "<p>" + thiscell.getValue().toString();

			if (getModel().isEdge(thiscell)) {
				if (src != null) {
					if (src.getValue().toString().length() > 0)
						paragraphTip = src.getValue().toString() + " -> " + paragraphTip;
				}
				if (trg != null) {
					if (trg.getValue().toString().length() > 0)
						paragraphTip = paragraphTip + " -> " + trg.getValue().toString();
				}
			} else {

			}
			String tip = headTip + paragraphTip + "</p></html>";

			return tip;
		}

		/**
		 * Overrides the method to use the currently selected edge template for
		 * new edges.
		 * 
		 * @param graph
		 * @param parent
		 * @param id
		 * @param value
		 * @param source
		 * @param target
		 * @param style
		 * @return
		 */
		public Object createEdge(Object parent, String id, Object value, Object source, Object target, String style) {
			if (edgeTemplate != null) {
				mxCell edge = (mxCell) cloneCells(new Object[] { edgeTemplate })[0];
				edge.setId(id);

				return edge;
			}

			return super.createEdge(parent, id, value, source, target, style);
		}

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
		mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";

		GraphEditor editor = new GraphEditor(null);
		// IntegrateOntologyWithProtege ontologyobj = new
		// IntegrateOntologyWithProtege(editor);
		// editor.createFrame(new EditorMenuBar(editor)).setVisible(true);

		JFrame frame = new JFrame("Dase editor");
		frame.add(editor);
		frame.setJMenuBar(new EditorMenuBar(editor));
		frame.setSize(800, 600);
		frame.setVisible(true);
		/*
		 * String base = "http://example.com/owl/families/#";
		 * 
		 * OWLDataFactory df = OWLManager.getOWLDataFactory();
		 * 
		 * OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		 * 
		 * PrefixManager pm = new DefaultPrefixManager();
		 * pm.setDefaultPrefix(base);
		 * 
		 * pm.getDefaultPrefix(); // Get reference to the :Person class (the
		 * full IRI: http://example.com/owl/families/Person) OWLClass person =
		 * df.getOWLClass("Person", pm); System.out.println("iri "+
		 * person.getIRI()); // Get reference to the :Mary class (the full IRI:
		 * <http://example.com/owl/families/Mary>) OWLNamedIndividual mary =
		 * df.getOWLNamedIndividual(":Mary", pm);
		 */
	}
}
