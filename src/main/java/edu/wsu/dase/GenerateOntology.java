package edu.wsu.dase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

//import org.checkerframework.checker.nullness.qual.NonNull;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

import edu.wsu.dase.swing.editor.BasicGraphEditor;
import edu.wsu.dase.util.CustomEntityType;

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
	// ProtegeIRIResolver iriResolver;
	PrefixManager pm;

	public GenerateOntology(BasicGraphEditor editor) {
		this.editor = editor;
		this.graph = editor.getGraphComponent().getGraph();
		this.model = (mxGraphModel) graph.getModel();
		this.root = graph.getDefaultParent();

		initilizeProtegeDataFactory();
	}

	public OWLOntology saveOntology() {

		cleanActiveOntology();

		changes = new ArrayList<OWLOntologyChange>();

		Object[] v = graph.getChildVertices(graph.getDefaultParent());
		makeDeclarations(v);
		Object[] e = graph.getChildEdges(graph.getDefaultParent());
		makeDeclarations(e);

		createOWLAxioms(e);
		saveOWLAxioms();
		return null;
	}

	public void initilizeProtegeDataFactory() {
		owlModelManager = editor.getProtegeOWLModelManager();
		owlDataFactory = owlModelManager.getOWLDataFactory();
		owlOntologyManager = owlModelManager.getOWLOntologyManager();
		changes = null;

		activeOntology = owlModelManager.getActiveOntology();

		pm = new DefaultPrefixManager();

		if (activeOntology != null) {
			owlOntologyID = activeOntology.getOntologyID();
			ontologyBaseURI = owlOntologyID.getOntologyIRI().get().toQuotedString();
			ontologyBaseURI = ontologyBaseURI.substring(1, ontologyBaseURI.length() - 1) + "#";
			/*
			 * iriResolver = new
			 * ProtegeIRIResolver(owlModelManager.getOWLEntityFinder(),
			 * owlModelManager.getOWLEntityRenderer());
			 * 
			 * iriResolver.updatePrefixes(activeOntology);
			 */

			// String base = "http://example.com/owl/families/#";
			pm.setDefaultPrefix(ontologyBaseURI);

			//System.out.println("base uri: " + ontologyBaseURI);
		}

	}

	private void cleanActiveOntology() {
		Set<OWLAxiom> axiomsToRemove;
		for (OWLOntology o : activeOntology.getImportsClosure()) {
			axiomsToRemove = new HashSet<OWLAxiom>();
			for (OWLAxiom ax : o.getAxioms()) {
				axiomsToRemove.add(ax);
				//System.out.println("to remove from " + o.getOntologyID().getOntologyIRI() + ": " + ax);
			}
			//System.out.println("Before: " + o.getAxiomCount());
			owlOntologyManager.removeAxioms(o, axiomsToRemove);
			//System.out.println("After: " + o.getAxiomCount());
		}
	}

	private void saveOWLAxioms() {
		if (changes != null) {
			if (ChangeApplied.SUCCESSFULLY == owlOntologyManager.applyChanges(changes)) {
				JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), "Changes Integrated with Protege.",
						"Changes Saved", 1);
			}
		}
	}

	private void createOWLLiteral(String name) {

		// don't need to create OWLLiteral
		/*
		 * OWLLiteral annoprop = owlDataFactory.getOWLLiteral(name);
		 * 
		 * OWLAxiom declareaxiom =
		 * owlDataFactory.getOWLDeclarationAxiom(annoprop);
		 * 
		 * AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);
		 * 
		 * owlOntologyManager.applyChange(addaxiom);
		 * 
		 * for (OWLAnnotationProperty cls :
		 * activeOntology.getAnnotationPropertiesInSignature()) {
		 * System.out.println("OWLAnnotationProperty: " + cls.getIRI()); }
		 */
	}

	private void createOWLAnnotationProperty(String name) {

		OWLAnnotationProperty annoprop = owlDataFactory.getOWLAnnotationProperty(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(annoprop);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		owlOntologyManager.applyChange(addaxiom);

		for (OWLAnnotationProperty cls : activeOntology.getAnnotationPropertiesInSignature()) {
			//System.out.println("OWLAnnotationProperty: " + cls.getIRI());
		}
	}

	private void createOWLDataProperty(String name) {

		OWLDataProperty dataprop = owlDataFactory.getOWLDataProperty(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(dataprop);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		owlOntologyManager.applyChange(addaxiom);

		for (OWLDataProperty cls : activeOntology.getDataPropertiesInSignature()) {
			//System.out.println("OWLDataProperty: " + cls.getIRI());
		}
	}

	private void createOWLObjectProperty(String name) {

		OWLObjectProperty objprop = owlDataFactory.getOWLObjectProperty(name, pm);
		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(objprop);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		owlOntologyManager.applyChange(addaxiom);

		for (OWLObjectProperty cls : activeOntology.getObjectPropertiesInSignature()) {
			//System.out.println("OWLObjectProperty: " + cls.getIRI());
		}
	}

	private void createOWLClass(String name) {

		// Optional<IRI> newIRI = iriResolver.prefixedName2IRI(name); //
		// IRI.create("http://swat.cse.lehigh.edu/onto/univ-bench.owl#",
		// name);

		OWLClass newClass = owlDataFactory.getOWLClass(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(newClass);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		owlOntologyManager.applyChange(addaxiom);

		for (OWLClass cls : activeOntology.getClassesInSignature()) {
			//System.out.println("class: " + cls.getIRI());
		}
	}

	private void createOWLNamedIndividual(String name) {
		// Optional<IRI> newIRI = iriResolver.prefixedName2IRI(name);
		OWLNamedIndividual newIndividual = owlDataFactory.getOWLNamedIndividual(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(newIndividual);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		owlOntologyManager.applyChange(addaxiom);

		for (OWLNamedIndividual ind : activeOntology.getIndividualsInSignature()) {
			//System.out.println("ind: " + ind.getIRI());
		}
	}

	@SuppressWarnings("unused")
	private boolean makeDeclarations(Object[] vertices) {
		for (Object vertex : vertices) {
			if (vertex instanceof mxCell) {
				mxCell cell = (mxCell) vertex;
				if (cell.getValue().toString().length() > 0) {
					CustomEntityType CustomEntityType = cell.getEntityType();
					//System.out.println("cell entity type: " + CustomEntityType.toString());

					if (CustomEntityType == CustomEntityType.CLASS) {
						createOWLClass(cell.getValue().toString());
					} else if (CustomEntityType == CustomEntityType.NAMED_INDIVIDUAL) {
						createOWLNamedIndividual(cell.getValue().toString());
					} else if (CustomEntityType == CustomEntityType.OBJECT_PROPERTY) {
						String[] multValues = getCellValues(cell.getValue().toString());
						for (String val : multValues) {
							createOWLObjectProperty(val);
						}
					} else if (CustomEntityType == CustomEntityType.DATA_PROPERTY) {
						createOWLDataProperty(cell.getValue().toString());
					} else if (CustomEntityType == CustomEntityType.ANNOTATION_PROPERTY) {
						createOWLAnnotationProperty(cell.getValue().toString());
					} else if (CustomEntityType == CustomEntityType.LITERAL) {
						createOWLLiteral(cell.getValue().toString());
					}
				} else {

					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(),
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
			// System.out.println("given: " + role + " trimmed: " + trimmedRole
			// + " .matched: " + at.toString());
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

					AxiomType axiomtype = getAxiomType(edgeCell.getValue().toString());
					CustomEntityType CustomEntityType = edgeCell.getEntityType();

					// axiom type matched with predefined axiomtypes
					if (axiomtype != null) {
						if (src != null && trg != null) {
							OWLAxiom tmpAxiom = createOWLAxiom(src.getValue().toString(), axiomtype,
									trg.getValue().toString());
							OWLOntologyChange change = new AddAxiom(activeOntology, tmpAxiom);
							changes.add(change);
							//System.out.println("axiom: " + tmpAxiom.toString());
						} else if (src != null && trg == null) {

						}

					} else { // axiom type doesn't match with predefined
								// axiomtypes
						List<OWLAxiom> tmpAxioms = createOWLAxiom(src, edgeCell, trg);
						for (OWLAxiom tmpAxiom : tmpAxioms) {
							OWLOntologyChange change = new AddAxiom(activeOntology, tmpAxiom);
							changes.add(change);
							//System.out.println("axiom: " + tmpAxiom.toString());
						}
					}

				} else {
					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), "Entity has no name can't save.",
							"Entity Without Name", 1);
					return null;
				}

			}
		}

		return null;
	}

	private OWLAxiom createOWLAxiom(String src, AxiomType role, String dest) {

		OWLAxiom axiom = null;
		if (role == AxiomType.SUBCLASS_OF) {
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLClass(src, pm),
					owlDataFactory.getOWLClass(dest, pm));
		} else if (role == AxiomType.EQUIVALENT_CLASSES) {
			axiom = owlDataFactory.getOWLEquivalentClassesAxiom(owlDataFactory.getOWLClass(src, pm),
					owlDataFactory.getOWLClass(dest, pm));
		} else if (role == AxiomType.DISJOINT_CLASSES) {
			axiom = owlDataFactory.getOWLDisjointClassesAxiom(owlDataFactory.getOWLClass(src, pm),
					owlDataFactory.getOWLClass(dest, pm));
		} else if (role == AxiomType.CLASS_ASSERTION) {

		} else if (role == AxiomType.SAME_INDIVIDUAL) {
			axiom = owlDataFactory.getOWLSameIndividualAxiom(owlDataFactory.getOWLNamedIndividual(src, pm),
					owlDataFactory.getOWLNamedIndividual(dest, pm));
		} else if (role == AxiomType.DIFFERENT_INDIVIDUALS) {
			axiom = owlDataFactory.getOWLDifferentIndividualsAxiom(owlDataFactory.getOWLNamedIndividual(src, pm),
					owlDataFactory.getOWLNamedIndividual(dest, pm));
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

	private List<OWLAxiom> createOWLAxiom(mxCell src, mxCell edge, mxCell dest) {
		List<OWLAxiom> axioms = new ArrayList<OWLAxiom>();

		OWLAxiom axiom = null; // , axiom2 = null, axiom3 = null;

		if (edge.getEntityType().equals(CustomEntityType.OBJECT_PROPERTY)) {

			String[] multValues = getCellValues(edge.getValue().toString());
			for (String val : multValues) {

				OWLObjectProperty objprop = owlDataFactory.getOWLObjectProperty(val, pm);
				if (src.getEntityType().equals(CustomEntityType.CLASS)
						&& dest.getEntityType().equals(CustomEntityType.CLASS)) {

					axiom = owlDataFactory.getOWLObjectPropertyDomainAxiom(objprop,
							owlDataFactory.getOWLClass(src.getValue().toString(), pm));
					axioms.add(axiom);
					axiom = owlDataFactory.getOWLObjectPropertyRangeAxiom(objprop,
							owlDataFactory.getOWLClass(dest.getValue().toString(), pm));

					axioms.add(axiom);
				} else if (src.getEntityType().equals(CustomEntityType.NAMED_INDIVIDUAL)
						&& dest.getEntityType().equals(CustomEntityType.NAMED_INDIVIDUAL)) {
					axiom = owlDataFactory.getOWLObjectPropertyAssertionAxiom(objprop,
							owlDataFactory.getOWLNamedIndividual(src.getValue().toString(), pm),
							owlDataFactory.getOWLNamedIndividual(dest.getValue().toString(), pm));
					axioms.add(axiom);
				}
			}
		} else if (edge.getEntityType().equals(CustomEntityType.DATA_PROPERTY)) {
			OWLDataProperty dataprop = owlDataFactory.getOWLDataProperty(edge.getValue().toString(), pm);

			if (src.getEntityType().equals(CustomEntityType.CLASS)) {
				axiom = owlDataFactory.getOWLDataPropertyDomainAxiom(dataprop,
						owlDataFactory.getOWLClass(src.getValue().toString(), pm));
				axioms.add(axiom);
			}
			if (src.getEntityType().equals(CustomEntityType.CLASS)
					&& dest.getEntityType().equals(CustomEntityType.DATATYPE)) {
				OWLDatatype odt = getOWLDataType(dest.getValue().toString());
				axiom = owlDataFactory.getOWLDataPropertyRangeAxiom(dataprop, odt);
				axioms.add(axiom);
			}
			if (src.getEntityType().equals(CustomEntityType.NAMED_INDIVIDUAL)
					&& dest.getEntityType().equals(CustomEntityType.LITERAL)) {
				OWLLiteral literal = owlDataFactory.getOWLLiteral(dest.getValue().toString());
				OWLNamedIndividual ind = owlDataFactory.getOWLNamedIndividual(src.getValue().toString(), pm);
				axiom = owlDataFactory.getOWLDataPropertyAssertionAxiom(dataprop, ind, literal);
				// System.out.println("inside: axiom: "+axiom);
				axioms.add(axiom);
			}
		}

		return axioms;
	}

	private OWLDatatype getOWLDataType(String value) {
		OWLDatatype odt = owlDataFactory.getOWLDatatype(value, pm);
		//System.out.println("odt: " + odt.toString());
		return odt;
	}

	private String[] getCellValues(String cellVal) {
		if (cellVal.length() > 0) {
			cellVal = cellVal.trim();
			return cellVal.split(",");
		}
		return null;
	}
}
