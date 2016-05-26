package edu.wsu.dase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

//import org.checkerframework.checker.nullness.qual.NonNull;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

import edu.wsu.dase.IRIResolver;
import edu.wsu.dase.ProtegeIRIResolver;

import com.mxgraph.analysis.mxAnalysisGraph;
import com.mxgraph.analysis.mxGraphAnalysis;
import com.mxgraph.analysis.mxGraphStructure;
import com.mxgraph.analysis.mxICostFunction;
import com.mxgraph.analysis.mxTraversal;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraph.mxICellVisitor;

import edu.wsu.dase.swing.GraphEditor;
import edu.wsu.dase.swing.editor.BasicGraphEditor;

public class GenerateOntology {

	mxGraph graph;
	Object root;
	mxGraphModel model;
	BasicGraphEditor editor;
	List<OWLOntologyChange> changes;
	OWLOntologyID owlOntologyID;
	String ontologyBaseURI;
	OWLDataFactory owlDataFactory;
	OWLModelManager owlModelManager;
	OWLOntologyManager owlOntologyManager;
	OWLOntology activeOntology;
	ProtegeIRIResolver iriResolver;

	public GenerateOntology(BasicGraphEditor editor) {
		this.editor = editor;
		this.graph = editor.getGraphComponent().getGraph();
		this.model = (mxGraphModel) graph.getModel();
		this.root = graph.getDefaultParent();

		initilizeProtegeDataFactory();
	}

	public OWLOntology saveOntology() {

		changes = new ArrayList<OWLOntologyChange>();
		Object[] v = graph.getChildVertices(graph.getDefaultParent());
		Object[] e = graph.getChildEdges(graph.getDefaultParent());

		makeDeclarations(v);
		createOWLAxioms(e);

		return null;
	}

	public void initilizeProtegeDataFactory() {
		owlModelManager = editor.getProtegeOWLModelManager();
		owlDataFactory = owlModelManager.getOWLDataFactory();
		owlOntologyManager = owlModelManager.getOWLOntologyManager();
		changes = null;

		activeOntology = owlModelManager.getActiveOntology();

		if (activeOntology != null) {
			owlOntologyID = activeOntology.getOntologyID();
			ontologyBaseURI = owlOntologyID.getOntologyIRI().get().getNamespace();
			iriResolver = new ProtegeIRIResolver(owlModelManager.getOWLEntityFinder(),
					owlModelManager.getOWLEntityRenderer());

			iriResolver.updatePrefixes(activeOntology);

			System.out.println(ontologyBaseURI);
		}

	}

	private void saveOWLAxioms() {
		if (changes != null) {
			if (ChangeApplied.SUCCESSFULLY == owlOntologyManager.applyChanges(changes)) {
				JOptionPane.showInternalMessageDialog(editor.getParent(), "Changes Integrated with Protege.",
						"Changes Saved", 1);
			}
		}
	}

	private void createOWLClass(String name) {

		Optional<IRI> newIRI = iriResolver.prefixedName2IRI(name); // IRI.create("http://swat.cse.lehigh.edu/onto/univ-bench.owl#",
																	// name);

		OWLClass newClass = owlDataFactory.getOWLClass(newIRI.get());

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(newClass);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		owlOntologyManager.applyChange(addaxiom);

		for (OWLClass cls : activeOntology.getClassesInSignature()) {
			// System.out.println(cls.getIRI());
		}
	}

	private void createOWLNamedIndividual(String name) {
		Optional<IRI> newIRI = iriResolver.prefixedName2IRI(name);
		OWLNamedIndividual newIndividual = owlDataFactory.getOWLNamedIndividual(newIRI.get());

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(newIndividual);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		owlOntologyManager.applyChange(addaxiom);

		for (OWLClass cls : activeOntology.getClassesInSignature()) {
			// System.out.println(cls.getIRI());
		}
	}

	@SuppressWarnings("unused")
	private boolean makeDeclarations(Object[] vertices) {
		for (Object vertex : vertices) {
			if (vertex instanceof mxCell) {
				mxCell cell = (mxCell) vertex;
				if (cell.getValue().toString().length() > 0) {
					if (cell.isOWLClass()) {
						createOWLClass(cell.getValue().toString());
					} else if (cell.isOWLNamedIndividual()) {
						createOWLNamedIndividual(cell.getValue().toString());
					}
				} else {
					JOptionPane.showInternalMessageDialog(editor.getParent(),
							"Entity has no name. \nEntity must have a name.", "Entity Without Name", 1);
					return false;
				}

			}
		}
		return true;
	}

	private AxiomType getAxiomType(String role) {
		String trimmedRole = role.replace("_", "");
		for (AxiomType at : AxiomType.AXIOM_TYPES) {
			System.out.println("given: " + role + " trimmed: " + trimmedRole + " .matched: " + at.toString());
			if (at.getName().toLowerCase().equals(role.toLowerCase())
					|| at.getName().toLowerCase().equals(trimmedRole.toLowerCase())) {
				return at;
			}
		}
		return null;

	}

	private Set<OWLAxiom> createOWLAxioms(Object[] edges) {
		for (Object edge : edges) {
			if (edge instanceof mxCell) {
				mxCell edgeCell = (mxCell) edge;
				if (edgeCell.getValue().toString().length() > 0) {

					mxCell src = (mxCell) graph.getModel().getTerminal(edge, true);
					mxCell trg = (mxCell) graph.getModel().getTerminal(edge, false);
					if (src != null && trg != null) {
						AxiomType axiomtype = getAxiomType(edgeCell.getValue().toString());

						if (axiomtype != null) {
							OWLAxiom tmpAxiom = createOWLAxiom(src.getValue().toString(), axiomtype,
									trg.getValue().toString());
							OWLOntologyChange change = new AddAxiom(activeOntology, tmpAxiom);
							changes.add(change);
							System.out.println("axiom: " + tmpAxiom.toString());
						} else {
							System.out.println("Axiom Type does not match with any existing Axiom Types.");

						}
					}

				} else {
					JOptionPane.showInternalMessageDialog(editor.getParent(), "Entity has no name can't save.",
							"Entity Without Name", 1);
					return null;
				}

			}
		}

		return null;
	}

	private OWLAxiom createOWLAxiom(String src, AxiomType role, String dest) {

		IRI srcIRI = IRI.create(src);
		IRI destIRI = IRI.create(dest);

		OWLAxiom axiom = null;
		if (role == AxiomType.SUBCLASS_OF) {
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLClass(srcIRI),
					owlDataFactory.getOWLClass(destIRI));
		} else if (role == AxiomType.EQUIVALENT_CLASSES) {
			axiom = owlDataFactory.getOWLEquivalentClassesAxiom(owlDataFactory.getOWLClass(srcIRI),
					owlDataFactory.getOWLClass(destIRI));
		} else if (role == AxiomType.DISJOINT_CLASSES) {
			axiom = owlDataFactory.getOWLDisjointClassesAxiom(owlDataFactory.getOWLClass(srcIRI),
					owlDataFactory.getOWLClass(destIRI));
		} else if (role == AxiomType.CLASS_ASSERTION) {

		} else if (role == AxiomType.SAME_INDIVIDUAL) {
			axiom = owlDataFactory.getOWLSameIndividualAxiom(owlDataFactory.getOWLNamedIndividual(srcIRI),
					owlDataFactory.getOWLNamedIndividual(destIRI));
		} else if (role == AxiomType.DIFFERENT_INDIVIDUALS) {
			axiom = owlDataFactory.getOWLDifferentIndividualsAxiom(owlDataFactory.getOWLNamedIndividual(srcIRI),
					owlDataFactory.getOWLNamedIndividual(destIRI));
		} else if (role == AxiomType.OBJECT_PROPERTY_ASSERTION) {

		} else if (role == AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION) {

		} else if (role == AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION) {

		} else if (role == AxiomType.OBJECT_PROPERTY_DOMAIN) {

		} else if (role == AxiomType.OBJECT_PROPERTY_RANGE) {

		} else if (role == AxiomType.DISJOINT_OBJECT_PROPERTIES) {

		} else if (role == AxiomType.SUB_OBJECT_PROPERTY) {

		} else if (role == AxiomType.EQUIVALENT_OBJECT_PROPERTIES) {

		} else if (role == AxiomType.INVERSE_OBJECT_PROPERTIES) {

		} else if (role == AxiomType.SUB_PROPERTY_CHAIN_OF) {

		} else if (role == AxiomType.FUNCTIONAL_OBJECT_PROPERTY) {

		} else if (role == AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY) {

		} else if (role == AxiomType.SYMMETRIC_OBJECT_PROPERTY) {

		} else if (role == AxiomType.ASYMMETRIC_OBJECT_PROPERTY) {

		} else if (role == AxiomType.TRANSITIVE_OBJECT_PROPERTY) {

		} else if (role == AxiomType.REFLEXIVE_OBJECT_PROPERTY) {

		} else if (role == AxiomType.IRREFLEXIVE_OBJECT_PROPERTY) {

		} else if (role == AxiomType.DATA_PROPERTY_DOMAIN) {

		} else if (role == AxiomType.DATA_PROPERTY_RANGE) {

		} else if (role == AxiomType.DISJOINT_DATA_PROPERTIES) {

		} else if (role == AxiomType.SUB_DATA_PROPERTY) {

		} else if (role == AxiomType.EQUIVALENT_DATA_PROPERTIES) {

		} else if (role == AxiomType.FUNCTIONAL_DATA_PROPERTY) {

		} else if (role == AxiomType.DATATYPE_DEFINITION) {

		} else if (role == AxiomType.DISJOINT_UNION) {

		} else if (role == AxiomType.DISJOINT_OBJECT_PROPERTIES) {

		} else if (role == AxiomType.DECLARATION) {

		} else if (role == AxiomType.SWRL_RULE) {

		} else if (role == AxiomType.ANNOTATION_ASSERTION) {

		} else if (role == AxiomType.SUB_ANNOTATION_PROPERTY_OF) {

		} else if (role == AxiomType.ANNOTATION_PROPERTY_DOMAIN) {

		} else if (role == AxiomType.HAS_KEY) {

		} else if (role == AxiomType.ANNOTATION_PROPERTY_RANGE) {

		}

		return axiom;
	}

}
