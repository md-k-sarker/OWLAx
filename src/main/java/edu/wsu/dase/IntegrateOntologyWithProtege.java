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
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
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

public class IntegrateOntologyWithProtege {

	private String SAVING_COMPLETE_TITLE = "Ontology Generated";
	private String SAVING_COMPLETE_MESSAGE = "Changes Integrated with Protege successfully.";
	private String SAVING_ERROR_TITLE = "Ontology Generated";
	private String SAVING_ERROR_MESSAGE = "Changes Integrated with Protege successfully.";
	private String ENTITY_WITH_NO_NAME_TITLE = "Entity Without Name";
	private String ENTITY_WITH_NO_NAME_MESSAGE = "Can not save enityty wihtout name. Entity must have a name.";

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

	public IntegrateOntologyWithProtege(BasicGraphEditor editor) {
		this.editor = editor;
		this.graph = editor.getGraphComponent().getGraph();
		this.model = (mxGraphModel) graph.getModel();
		this.root = graph.getDefaultParent();

		initilizeProtegeDataFactory();
	}

	public void generateOntology() {

		cleanActiveOntology();

		changes = new ArrayList<OWLOntologyChange>();
		changes.clear();

		Object[] v = graph.getChildVertices(graph.getDefaultParent());
		Object[] e = graph.getChildEdges(graph.getDefaultParent());

		if (makeDeclarations(v)) {
			if (!changes.isEmpty()) {
				owlOntologyManager.applyChanges(changes);
				changes.clear();
				if (makeDeclarations(e)) {
					if (!changes.isEmpty()) {
						owlOntologyManager.applyChanges(changes);
						changes.clear();
						if (createOWLAxioms(e)) {
							if (saveOWLAxioms()) {
								JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), SAVING_COMPLETE_MESSAGE,
										SAVING_COMPLETE_TITLE, JOptionPane.PLAIN_MESSAGE);
								return;
							} else {

							}
						}
						JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), SAVING_COMPLETE_MESSAGE,
								SAVING_COMPLETE_TITLE, JOptionPane.PLAIN_MESSAGE);
						return;
					}
					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), SAVING_COMPLETE_MESSAGE,
							SAVING_COMPLETE_TITLE, JOptionPane.PLAIN_MESSAGE);
					return;
				}
				JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), SAVING_COMPLETE_MESSAGE,
						SAVING_COMPLETE_TITLE, JOptionPane.PLAIN_MESSAGE);
				return;
			}
		}
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

			// System.out.println("base uri: " + ontologyBaseURI);
		}

	}

	private void cleanActiveOntology() {
		Set<OWLAxiom> axiomsToRemove;
		for (OWLOntology o : activeOntology.getImportsClosure()) {
			axiomsToRemove = new HashSet<OWLAxiom>();
			for (OWLAxiom ax : o.getAxioms()) {
				axiomsToRemove.add(ax);
				// System.out.println("to remove from " +
				// o.getOntologyID().getOntologyIRI() + ": " + ax);
			}
			// System.out.println("Before: " + o.getAxiomCount());
			owlOntologyManager.removeAxioms(o, axiomsToRemove);
			// System.out.println("After: " + o.getAxiomCount());
		}
	}

	private boolean saveOWLAxioms() {
		if (changes != null) {
			if (ChangeApplied.SUCCESSFULLY == owlOntologyManager.applyChanges(changes)) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	private void createOWLLiteral(String name) {

		// don't need to create OWLLiteral

		/*
		 * OWLLiteral literal = owlDataFactory.getOWLLiteral(name);
		 * 
		 * OWLAxiom declareaxiom =
		 * owlDataFactory.getOWLDeclarationAxiom(literal);
		 * 
		 * AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);
		 * 
		 * owlOntologyManager.applyChange(addaxiom);
		 */

		/*
		 * for (OWLAnnotationProperty cls :
		 * activeOntology.getAnnotationPropertiesInSignature()) {
		 * System.out.println("OWLAnnotationProperty: " + cls.getIRI()); }
		 */

	}

	private AddAxiom createOWLAnnotationProperty(String name) {

		OWLAnnotationProperty annoprop = owlDataFactory.getOWLAnnotationProperty(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(annoprop);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		return addaxiom;
		/*
		 * owlOntologyManager.applyChange(addaxiom);
		 * 
		 * for (OWLAnnotationProperty cls :
		 * activeOntology.getAnnotationPropertiesInSignature()) {
		 * //System.out.println("OWLAnnotationProperty: " + cls.getIRI()); }
		 */
	}

	private AddAxiom createOWLDataProperty(String name) {

		OWLDataProperty dataprop = owlDataFactory.getOWLDataProperty(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(dataprop);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		return addaxiom;
		/*
		 * owlOntologyManager.applyChange(addaxiom);
		 * 
		 * for (OWLDataProperty cls :
		 * activeOntology.getDataPropertiesInSignature()) {
		 * //System.out.println("OWLDataProperty: " + cls.getIRI()); }
		 */
	}

	private AddAxiom createOWLObjectProperty(String name) {

		OWLObjectProperty objprop = owlDataFactory.getOWLObjectProperty(name, pm);
		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(objprop);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		return addaxiom;
		/*
		 * owlOntologyManager.applyChange(addaxiom);
		 * 
		 * for (OWLObjectProperty cls :
		 * activeOntology.getObjectPropertiesInSignature()) {
		 * //System.out.println("OWLObjectProperty: " + cls.getIRI()); }
		 */
	}

	private AddAxiom createOWLClass(String name) {

		OWLClass newClass = owlDataFactory.getOWLClass(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(newClass);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		return addaxiom;

		/*
		 * owlOntologyManager.applyChange(addaxiom);
		 * 
		 * for (OWLClass cls : activeOntology.getClassesInSignature()) {
		 * //System.out.println("class: " + cls.getIRI()); }
		 */
	}

	private AddAxiom createOWLNamedIndividual(String name) {

		OWLNamedIndividual newIndividual = owlDataFactory.getOWLNamedIndividual(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(newIndividual);

		AddAxiom addaxiom = new AddAxiom(activeOntology, declareaxiom);

		return addaxiom;
		/*
		 * owlOntologyManager.applyChange(addaxiom);
		 * 
		 * for (OWLNamedIndividual ind :
		 * activeOntology.getIndividualsInSignature()) { //System.out.println(
		 * "ind: " + ind.getIRI()); }
		 */
	}

	@SuppressWarnings("unused")
	private boolean makeDeclarations(Object[] VerticesOrEdges) {

		for (Object vertexOrEdge : VerticesOrEdges) {
			if (vertexOrEdge instanceof mxCell) {
				mxCell cell = (mxCell) vertexOrEdge;
				if (cell.getValue().toString().length() > 0) {
					CustomEntityType CustomEntityType = cell.getEntityType();
					String cellLabel = cell.getValue().toString().trim().replace(" ", "_");

					if (CustomEntityType == CustomEntityType.CLASS) {
						changes.add(createOWLClass(cellLabel));
					} else if (CustomEntityType == CustomEntityType.NAMED_INDIVIDUAL) {
						changes.add(createOWLNamedIndividual(cellLabel));
					} else if (CustomEntityType == CustomEntityType.DATATYPE) {
						// if not existing datatype then create
					} else if (CustomEntityType == CustomEntityType.LITERAL) {
						// changes.add(createOWLLiteral(cell.getValue().toString()));
					} else if (CustomEntityType == CustomEntityType.OBJECT_PROPERTY) {
						String[] multValues = getCellValues(cellLabel);
						for (String val : multValues) {
							changes.add(createOWLObjectProperty(val));
						}
					} else if (CustomEntityType == CustomEntityType.DATA_PROPERTY) {
						String[] multValues = getCellValues(cellLabel);
						for (String val : multValues) {
							changes.add(createOWLDataProperty(val));
						}
					} else if (CustomEntityType == CustomEntityType.ANNOTATION_PROPERTY) {
						// although it is not required but implemented
						changes.add(createOWLAnnotationProperty(cellLabel));
					}
				} else {

					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), ENTITY_WITH_NO_NAME_MESSAGE,
							ENTITY_WITH_NO_NAME_TITLE, JOptionPane.ERROR_MESSAGE);
					changes.clear();
					return false;
				}
			}
		}
		if (!changes.isEmpty()) {

			return true;
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private OWLDatatype getCustomOWLDataType(String value) {
		// if custom datatype what will happen ?
		OWLDatatype dt = owlDataFactory.getOWLDatatype(value, pm);
		System.out.println(dt);
		return dt;
	}

	/**
	 * createOWLAxioms iterate for each edge
	 * 
	 * @param edges
	 * @return boolean
	 */
	private boolean createOWLAxioms(Object[] edges) {

		for (Object edge : edges) {
			if (edge instanceof mxCell) {
				mxCell edgeCell = (mxCell) edge;

				if (edgeCell.getValue().toString().length() > 0) {

					mxCell src = (mxCell) graph.getModel().getTerminal(edge, true);
					mxCell trg = (mxCell) graph.getModel().getTerminal(edge, false);

					if (src != null && trg != null) {
						List<OWLAxiom> tmpAxioms = createOWLAxiom(src, edgeCell, trg);
						for (OWLAxiom tmpAxiom : tmpAxioms) {
							OWLOntologyChange change = new AddAxiom(activeOntology, tmpAxiom);
							changes.add(change);
						}
					}

				} else {
					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), ENTITY_WITH_NO_NAME_MESSAGE,
							ENTITY_WITH_NO_NAME_TITLE, JOptionPane.ERROR_MESSAGE);
					changes.clear();
					return false;
				}

			}
		}
		return true;
	}

	/**
	 * For each edge create a set of axioms based on edge type
	 * 
	 * @param src
	 * @param edge
	 * @param dest
	 * @return
	 */
	private List<OWLAxiom> createOWLAxiom(mxCell src, mxCell edge, mxCell dest) {
		List<OWLAxiom> axioms = new ArrayList<OWLAxiom>();

		OWLAxiom axiom = null;

		if (edge.getEntityType().equals(CustomEntityType.OBJECT_PROPERTY)) {

			String[] multValues = getCellValues(edge.getValue().toString());
			for (String val : multValues) {

				OWLObjectProperty objprop = owlDataFactory.getOWLObjectProperty(val, pm);
				if (src.getEntityType().equals(CustomEntityType.CLASS)
						&& dest.getEntityType().equals(CustomEntityType.CLASS)) {

					axioms.addAll(getClass2ObjectProperty2ClassAxioms(
							owlDataFactory.getOWLClass(src.getValue().toString(), pm), objprop,
							owlDataFactory.getOWLClass(dest.getValue().toString(), pm)));

				} else if (src.getEntityType().equals(CustomEntityType.CLASS)
						&& dest.getEntityType().equals(CustomEntityType.NAMED_INDIVIDUAL)) {

					axioms.addAll(getClass2ObjectProperty2IndividualAxioms(
							owlDataFactory.getOWLClass(src.getValue().toString(), pm), objprop,
							owlDataFactory.getOWLNamedIndividual(dest.getValue().toString(), pm)));

				} else {
					// error. it can't occur. validation should be done
				}
			}
		} else if (edge.getEntityType().equals(CustomEntityType.DATA_PROPERTY)) {
			OWLDataProperty dataprop = owlDataFactory.getOWLDataProperty(edge.getValue().toString(), pm);

			if (src.getEntityType().equals(CustomEntityType.CLASS)
					&& dest.getEntityType().equals(CustomEntityType.LITERAL)) {

				axioms.addAll(
						getClass2DataProperty2LiteralAxioms(owlDataFactory.getOWLClass(src.getValue().toString(), pm),
								dataprop, owlDataFactory.getOWLLiteral(dest.getValue().toString(), "")));
			} else if (src.getEntityType().equals(CustomEntityType.CLASS)
					&& dest.getEntityType().equals(CustomEntityType.DATATYPE)) {

				// get OWLDataType.. from getCustomOWLDataType
				OWLDatatype owlDatatype = getCustomOWLDataType(dest.getValue().toString());
				axioms.addAll(getClass2DataProperty2DataTypeAxioms(
						owlDataFactory.getOWLClass(src.getValue().toString(), pm), dataprop, owlDatatype));
			}

		} else if (edge.getEntityType().equals(CustomEntityType.RDFTYPE)) {
			if (src.getEntityType().equals(CustomEntityType.NAMED_INDIVIDUAL)
					&& dest.getEntityType().equals(CustomEntityType.CLASS)) {
				axioms.addAll(getInvdividual2RDFType2ClassAxioms(
						owlDataFactory.getOWLNamedIndividual(src.getValue().toString(), pm),
						owlDataFactory.getOWLClass(dest.getValue().toString(), pm)));
			} else {
				// error. it can't occur. validation should be done
			}

		} else if (edge.getEntityType().equals(CustomEntityType.RDFSSUBCLASS_OF)) {
			if (src.getEntityType().equals(CustomEntityType.CLASS)
					&& dest.getEntityType().equals(CustomEntityType.CLASS)) {
				axioms.addAll(
						getClass2RDFSSubClassOf2ClassAxioms(owlDataFactory.getOWLClass(src.getValue().toString(), pm),
								owlDataFactory.getOWLClass(dest.getValue().toString(), pm)));
			} else {
				// error. it can't occur. validation should be done
			}
		} else {
			// error. it can't occur. validation should be done
		}

		return axioms;
	}

	private OWLDatatype getOWLDataType(String value) {
		OWLDatatype odt = owlDataFactory.getOWLDatatype(value, pm);
		// System.out.println("odt: " + odt.toString());
		return odt;
	}

	private String[] getCellValues(String cellVal) {
		if (cellVal.length() > 0) {
			cellVal = cellVal.trim();
			return cellVal.split(",");
		}
		return null;
	}

	/**
	 * create axioms for class--objectproperty----class relation.
	 */
	private Set<OWLAxiom> getClass2ObjectProperty2ClassAxioms(OWLClass src, OWLObjectProperty objprop, OWLClass dest) {

		Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		OWLObjectSomeValuesFrom owlObjectSomeValuesFrom;
		OWLObjectAllValuesFrom owlObjectAllValuesFrom;
		OWLObjectMinCardinality owlObjectMinCardinality;

		// set domain and range
		// scoped domain
		owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
		tmpaxioms.add(axiom);
		// general domain
		owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, owlDataFactory.getOWLThing());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
		tmpaxioms.add(axiom);
		// scoped range
		owlObjectAllValuesFrom = owlDataFactory.getOWLObjectAllValuesFrom(objprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectAllValuesFrom);
		tmpaxioms.add(axiom);
		// general range
		owlObjectAllValuesFrom = owlDataFactory.getOWLObjectAllValuesFrom(objprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectAllValuesFrom);
		tmpaxioms.add(axiom);

		// set functionality restriction
		// source functionality
		owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectSomeValuesFrom);
		tmpaxioms.add(axiom);
		// destination functionality
		// inverse property need to confirm ----------
		owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop.getInverseProperty(), src);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(dest, owlObjectSomeValuesFrom);
		tmpaxioms.add(axiom);

		// set cardinality restriction
		// for objectProperty
		// max or min need to confirm
		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop, owlDataFactory.getOWLThing());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop, owlDataFactory.getOWLThing());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		// for inverse objectProperty
		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop.getInverseProperty(), dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop.getInverseProperty(), dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop.getInverseProperty(),
				owlDataFactory.getOWLThing());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop.getInverseProperty(),
				owlDataFactory.getOWLThing());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		// need to implement custom cardinality axiom

		/*
		 * axiom = owlDataFactory.getOWLObjectPropertyDomainAxiom(objprop,
		 * owlDataFactory.getOWLClass(src.getValue().toString(), pm));
		 * tmpaxioms.add(axiom); axiom =
		 * owlDataFactory.getOWLObjectPropertyRangeAxiom(objprop,
		 * owlDataFactory.getOWLClass(dest.getValue().toString(), pm));
		 */

		System.out.println(axiom.toString());
		tmpaxioms.add(axiom);

		return tmpaxioms;
	}

	/**
	 * create axioms for class--dataproperty----datatype relation.
	 */
	// be sure for OWLDatatype vs OWL2DataType
	private Set<OWLAxiom> getClass2DataProperty2DataTypeAxioms(OWLClass src, OWLDataProperty dataprop,
			OWLDatatype dest) {

		Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		OWLDataSomeValuesFrom owlDataSomeValuesFrom;
		OWLDataAllValuesFrom owlDataAllValuesFrom;
		OWLDataMinCardinality owlDataMinCardinality;

		// set domain and range
		// scoped domain
		owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
		tmpaxioms.add(axiom);
		// need to confirm whether --owlDataFactory.getOWLThing() or
		// owlDataFactory.getTopDatatype()
		owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, owlDataFactory.getTopDatatype());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
		tmpaxioms.add(axiom);
		// scoped range
		owlDataAllValuesFrom = owlDataFactory.getOWLDataAllValuesFrom(dataprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataAllValuesFrom);
		tmpaxioms.add(axiom);
		// need to confirm whether --owlDataFactory.getOWLThing() or
		// owlDataFactory.getTopDatatype()
		owlDataAllValuesFrom = owlDataFactory.getOWLDataAllValuesFrom(dataprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataAllValuesFrom);
		tmpaxioms.add(axiom);

		// set functionality restriction
		// source functionality
		owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataSomeValuesFrom);
		tmpaxioms.add(axiom);
		// destination functionality
		// inverse property need to confirm ----------
		// dataproperty doesn't have inverse property
		// owlDataSomeValuesFrom =
		// owlDataFactory.getowlDataSomeValuesFrom(dataprop.getInverseProperty(),
		// src);
		// axiom = owlDataFactory.getOWLSubClassOfAxiom(dest,
		// owlDataSomeValuesFrom);
		// tmpaxioms.add(axiom);

		// set cardinality restriction
		// max or min need to confirm
		owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1, dataprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMinCardinality);
		tmpaxioms.add(axiom);

		owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1, dataprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataMinCardinality);
		tmpaxioms.add(axiom);

		owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1, dataprop, owlDataFactory.getTopDatatype());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataMinCardinality);
		tmpaxioms.add(axiom);

		owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1, dataprop, owlDataFactory.getTopDatatype());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMinCardinality);
		tmpaxioms.add(axiom);

		// for inverse dataProperty
		// inverse property need to confirm ----------
		// dataproperty doesn't have inverse property
		// owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1,
		// dataprop.getInverseProperty(), dest);
		// axiom = owlDataFactory.getOWLSubClassOfAxiom(src,
		// owlDataMinCardinality);
		// tmpaxioms.add(axiom);
		//
		// owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1,
		// dataprop.getInverseProperty(), dest);
		// axiom =
		// owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(),
		// owlDataMinCardinality);
		// tmpaxioms.add(axiom);
		//
		// owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1,
		// dataprop.getInverseProperty(),
		// owlDataFactory.getOWLThing());
		// axiom =
		// owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(),
		// owlDataMinCardinality);
		// tmpaxioms.add(axiom);
		//
		// owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1,
		// dataprop.getInverseProperty(),
		// owlDataFactory.getOWLThing());
		// axiom = owlDataFactory.getOWLSubClassOfAxiom(src,
		// owlDataMinCardinality);
		// tmpaxioms.add(axiom);

		// need to implement custom cardinality axiom

		// axiom = owlDataFactory.getOWLObjectPropertyDomainAxiom(objprop,
		// owlDataFactory.getOWLClass(src.getValue().toString(), pm));
		// tmpaxioms.add(axiom); axiom =
		// owlDataFactory.getOWLObjectPropertyRangeAxiom(objprop,
		// owlDataFactory.getOWLClass(dest.getValue().toString(), pm));

		System.out.println(axiom.toString());
		tmpaxioms.add(axiom);

		return tmpaxioms;
	}

	/**
	 * create axioms for class--objectproperty----individual relation.
	 * 
	 * @param src
	 * @param objprop
	 * @param dest
	 * @return
	 */
	private Set<OWLAxiom> getClass2ObjectProperty2IndividualAxioms(OWLClass src, OWLObjectProperty objprop,
			OWLIndividual dest) {
		Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		OWLObjectSomeValuesFrom owlObjectSomeValuesFrom;
		OWLObjectHasValue owlLObjectHasValue;
		OWLObjectMinCardinality owlObjectMinCardinality;
		OWLObjectOneOf owlObjectOneOf;

		// set domain and range
		// scoped domain
		owlLObjectHasValue = owlDataFactory.getOWLObjectHasValue(objprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlLObjectHasValue, src);
		tmpaxioms.add(axiom);

		owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, owlDataFactory.getOWLThing());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
		tmpaxioms.add(axiom);

		// set functionality restriction
		owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop.getInverseProperty(), src);
		/// need to confirm-------
		owlObjectOneOf = owlDataFactory.getOWLObjectOneOf(dest);
		owlDataFactory.getOWLSubClassOfAxiom(owlObjectOneOf, owlObjectSomeValuesFrom);
		tmpaxioms.add(axiom);

		// set cardinality restriction
		// max or min need to confirm
		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop, owlDataFactory.getOWLThing());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		owlObjectMinCardinality = owlDataFactory.getOWLObjectMinCardinality(1, objprop, owlDataFactory.getOWLThing());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMinCardinality);
		tmpaxioms.add(axiom);

		return tmpaxioms;
	}

	/**
	 * create axioms for class--dataproperty----literal relation.
	 * 
	 * @param src
	 * @param dataprop
	 * @param dest
	 * @return
	 */
	private Set<OWLAxiom> getClass2DataProperty2LiteralAxioms(OWLClass src, OWLDataProperty dataprop, OWLLiteral dest) {
		Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		OWLDataSomeValuesFrom owlDataSomeValuesFrom;
		OWLDataHasValue owlLDataHasValue;
		OWLDataMinCardinality owlDataMinCardinality;
		OWLDataOneOf owldataOneOf;

		// set domain and range
		// scoped domain
		owlLDataHasValue = owlDataFactory.getOWLDataHasValue(dataprop, dest);
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlLDataHasValue, src);
		tmpaxioms.add(axiom);

		owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, owlDataFactory.getTopDatatype());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
		tmpaxioms.add(axiom);

		// @formatter:off
		// set functionality restriction... not working

		// owlDataSomeValuesFrom =
		// owlDataFactory.getOWLDataSomeValuesFrom(dataprop, src); owldataOneOf
		// = owlDataFactory.getOWLDataOneOf(dest);
		// owlDataFactory.getOWLSubClassOfAxiom(owldataOneOf,
		// owlDataSomeValuesFrom); tmpaxioms.add(axiom);

		// set cardinality restriction ....not working
		// max or min need to confirm

		// owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1,
		// dataprop, owlDataFactory.getTopDatatype()); axiom =
		// owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getTopDatatype(),
		// owlDataMinCardinality); tmpaxioms.add(axiom);

		// @formatter:on

		owlDataMinCardinality = owlDataFactory.getOWLDataMinCardinality(1, dataprop, owlDataFactory.getTopDatatype());
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMinCardinality);
		tmpaxioms.add(axiom);

		return tmpaxioms;
	}

	/**
	 * create axioms for individual----rdftype----class relation.
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	private Set<OWLAxiom> getClass2RDFSSubClassOf2ClassAxioms(OWLClass src, OWLClass dest) {
		Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, dest);

		tmpaxioms.add(axiom);
		return tmpaxioms;
	}

	private Set<OWLAxiom> getInvdividual2RDFType2ClassAxioms(OWLIndividual src, OWLClass dest) {
		Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		axiom = owlDataFactory.getOWLClassAssertionAxiom(dest, src);
		tmpaxioms.add(axiom);
		return tmpaxioms;
	}

	// @formatter:off
	/*
	 * information
	 * 
	 *
	 * class(A)------objectProperty(P)-------class(B) allowed
	 ** 
	 * someValues of P.B < subclass of A A subclass of allValues of P.B
	 **
	 * 
	 * 1.1 class----objectproperty-----individual ? discuss tomorrow allowed 1.2
	 * class----dataProperty-----literal ? discuss tomorrow allowed 1.3
	 * class----objectProperty-----literal ? discuss tomorrow -- not allowed
	 * 
	 * 2. individual --------------- not allowed 2.1
	 * ind----objectproperty[must]---ind ? not allowed 2.2 ind---dataProperty
	 * --literal ? not allowed 2.3 ind ---objectproperty---class ? not allowed
	 * 2.4 ind------rdf-type-----class ? allowed [////
	 * getowlclassassertionaxiom]
	 * 
	 * 
	 * 3. data type ----------------datatype [not allowed]
	 * 
	 * 4. literal --------------------literal not allowed ?
	 * 
	 * 5. literal------------any thing not allowed 6. data type ---------any
	 * thing not allowed
	 */
	// @formatter:on
}
