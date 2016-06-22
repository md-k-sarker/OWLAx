package edu.wsu.dase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

//import org.checkerframework.checker.nullness.qual.NonNull;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
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
	private String SAVING_ERROR_TITLE = "Ontology not generated";
	private String SAVING_ERROR_MESSAGE = "Changes can't be Integrated with Protege.";
	private String ENTITY_WITH_NO_NAME_TITLE = "Entity Without Name";
	private String ENTITY_WITH_NO_NAME_MESSAGE = "Can not save entity wihtout name. Entity must have a name.";

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

		if (!editor.isKeepExistingAxiom())
			cleanActiveOntology();

		changes = new ArrayList<OWLOntologyChange>();
		changes.clear();

		Object[] v = graph.getChildVertices(graph.getDefaultParent());
		Object[] e = graph.getChildEdges(graph.getDefaultParent());
		if (e.length > 0) {
			if (makeDeclarations(v) && makeDeclarations(e)) {

				if (!changes.isEmpty()) {
					ChangeApplied ca = owlOntologyManager.applyChanges(changes);
					if (ChangeApplied.NO_OPERATION == ca) {
						editor.status("Generated axioms is empty");
						changes.clear();
						return;
					} else if (ChangeApplied.UNSUCCESSFULLY == ca) {
						editor.status(
								"Generated declaration axioms successfully. But axioms integration with protege not successfull");
						changes.clear();
						return;
					} else if (ChangeApplied.SUCCESSFULLY == ca) {
						editor.status("Generated declaration axioms and integrated with protege successfully.");
						changes.clear();
					}

					if (createOWLAxioms(e)) {
						if (saveOWLAxioms()) {
							editor.status(SAVING_COMPLETE_MESSAGE);
							JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), SAVING_COMPLETE_MESSAGE,
									SAVING_COMPLETE_TITLE, JOptionPane.PLAIN_MESSAGE);
							return;
						} else {
							editor.status("Integrating Axioms with Protege failed.");
							return;
						}
					} else {
						editor.status("Creating Logical Axioms failed.");
						return;
					}
				} else {
					editor.status("Nothing to integrate with Protege.");
					return;
				}
			} else {
				editor.status("Creating Declaration Axioms failed.");
				return;
			}
		} else {
			editor.status("Nothing to integrate with Protege.");
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
			editor.getGraphComponent().setOWLFileTitle(ontologyBaseURI);
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
			/**
			 * see the axioms
			 */
			
			if (ChangeApplied.SUCCESSFULLY == owlOntologyManager.applyChanges(changes)) {
				editor.status(
						"All axioms (Domain, Range, Existential and Cardinality) integrated with protege successfully.");
				return true;
			} else
				return false;
		} else
			return false;
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

	private boolean makeDeclarations(Object[] VerticesOrEdges) {
		editor.status("Creating Axioms");
		for (Object vertexOrEdge : VerticesOrEdges) {
			if (vertexOrEdge instanceof mxCell) {
				mxCell cell = (mxCell) vertexOrEdge;
				if (cell.getValue().toString().length() > 0) {
					CustomEntityType CustomEntityType = cell.getEntityType();
					String cellLabel = cell.getValue().toString().trim().replace(" ", "_");

					if (CustomEntityType.getName().equals(CustomEntityType.CLASS.getName())) {
						changes.add(createOWLClass(cellLabel));
					} else if (CustomEntityType.getName().equals(CustomEntityType.NAMED_INDIVIDUAL.getName())) {
						changes.add(createOWLNamedIndividual(cellLabel));
					} else if (CustomEntityType.getName().equals(CustomEntityType.DATATYPE.getName())) {
						// if not existing datatype then create
					} else if (CustomEntityType.getName().equals(CustomEntityType.LITERAL.getName())) {
						// changes.add(createOWLLiteral(cell.getValue().toString()));
					} else if (CustomEntityType.getName().equals(CustomEntityType.OBJECT_PROPERTY.getName())) {
						String[] multValues = getCellValues(cellLabel);
						for (String val : multValues) {
							changes.add(createOWLObjectProperty(val));
						}
					} else if (CustomEntityType.getName().equals(CustomEntityType.DATA_PROPERTY.getName())) {
						String[] multValues = getCellValues(cellLabel);
						for (String val : multValues) {
							changes.add(createOWLDataProperty(val));
						}
					} else if (CustomEntityType.getName().equals(CustomEntityType.ANNOTATION_PROPERTY.getName())) {
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
		// System.out.println(dt);
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
		editor.status("Generated Domain, Range, Existential and Cardinality axioms successfully");
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

		if (edge.getEntityType().getName().equals(CustomEntityType.OBJECT_PROPERTY.getName())) {

			String[] multValues = getCellValues(edge.getValue().toString().trim().replace(" ", "_"));
			for (String val : multValues) {

				OWLObjectProperty objprop = owlDataFactory.getOWLObjectProperty(val, pm);
				if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
						&& dest.getEntityType().getName().equals(CustomEntityType.CLASS.getName())) {

					axioms.addAll(getClass2ObjectProperty2ClassAxioms(
							owlDataFactory.getOWLClass(src.getValue().toString().trim().replace(" ", "_"), pm), objprop,
							owlDataFactory.getOWLClass(dest.getValue().toString().trim().replace(" ", "_"), pm)));

				} else if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
						&& dest.getEntityType().getName().equals(CustomEntityType.NAMED_INDIVIDUAL.getName())) {

					axioms.addAll(getClass2ObjectProperty2IndividualAxioms(
							owlDataFactory.getOWLClass(src.getValue().toString().trim().replace(" ", "_"), pm), objprop,
							owlDataFactory.getOWLNamedIndividual(dest.getValue().toString().trim().replace(" ", "_"), pm)));

				} else {
					// error. it can't occur. validation should be done
				}
			}
		} else if (edge.getEntityType().getName().equals(CustomEntityType.DATA_PROPERTY.getName())) {
			OWLDataProperty dataprop = owlDataFactory.getOWLDataProperty(edge.getValue().toString().trim().replace(" ", "_"), pm);

			if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
					&& dest.getEntityType().getName().equals(CustomEntityType.LITERAL.getName())) {

				axioms.addAll(getClass2DataProperty2LiteralAxioms(
						owlDataFactory.getOWLClass(src.getValue().toString().trim().replace(" ", "_"), pm), dataprop, getOWLLiteral(dest)));
			} else if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
					&& dest.getEntityType().getName().equals(CustomEntityType.DATATYPE.getName())) {

				// get OWLDataType.. from getCustomOWLDataType
				OWLDatatype owlDatatype = getCustomOWLDataType(dest.getValue().toString().trim().replace(" ", "_"));
				axioms.addAll(getClass2DataProperty2DataTypeAxioms(
						owlDataFactory.getOWLClass(src.getValue().toString().trim().replace(" ", "_"), pm), dataprop, owlDatatype));
			}

		} else if (edge.getEntityType().getName().equals(CustomEntityType.RDFTYPE.getName())) {
			if (src.getEntityType().getName().equals(CustomEntityType.NAMED_INDIVIDUAL.getName())
					&& dest.getEntityType().getName().equals(CustomEntityType.CLASS.getName())) {
				axioms.addAll(getInvdividual2RDFType2ClassAxioms(
						owlDataFactory.getOWLNamedIndividual(src.getValue().toString().trim().replace(" ", "_"), pm),
						owlDataFactory.getOWLClass(dest.getValue().toString().trim().replace(" ", "_"), pm)));
			} else {
				// error. it can't occur. validation should be done
			}

		} else if (edge.getEntityType().getName().equals(CustomEntityType.RDFSSUBCLASS_OF.getName())) {
			if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
					&& dest.getEntityType().getName().equals(CustomEntityType.CLASS.getName())) {
				axioms.addAll(
						getClass2RDFSSubClassOf2ClassAxioms(owlDataFactory.getOWLClass(src.getValue().toString().trim().replace(" ", "_"), pm),
								owlDataFactory.getOWLClass(dest.getValue().toString().trim().replace(" ", "_"), pm)));
			} else {
				// error. it can't occur. validation should be done
			}
		} else {
			// error. it can't occur. validation should be done
		}

		return axioms;
	}

	private String[] getCellValues(String cellVal) {
		if (cellVal.length() > 0) {
			cellVal = cellVal.trim();
			cellVal = cellVal.replace(" ","_");
			return cellVal.split(",");
		}
		return null;
	}

	private String getLiteralCellValue(mxCell literal) {

		String labelValueOnly = "";
		Pattern pattern = Pattern.compile("\"(.*?)\"");
		String cellVal = literal.getValue().toString().trim().replace(" ","_");
		Matcher matcher = pattern.matcher(cellVal);
		while (matcher.find()) {
			labelValueOnly = matcher.group(1);
		}

		return labelValueOnly;
	}

	private String getLiteralTypeValue(mxCell literal) {
		String labelTypeOnly = "";
		Pattern pattern = Pattern.compile("\\^\\^(.*?)$");
		String cellVal = literal.getValue().toString().trim().replace(" ", "_");
		Matcher matcher = pattern.matcher(cellVal);
		while (matcher.find()) {
			labelTypeOnly = matcher.group(1);
		}

		return labelTypeOnly;
	}

	private OWLLiteral getOWLLiteral(mxCell cell) {

		// don't need to create OWLLiteral
		// but need to save the datatype of literal
		OWLDatatype odt = owlDataFactory.getOWLDatatype(getLiteralTypeValue(cell), pm);
		OWLLiteral literal = owlDataFactory.getOWLLiteral(getLiteralCellValue(cell), odt);

		return literal;
	}

	/**
	 * create axioms for class--objectproperty----class relation.
	 */
	private Set<OWLAxiom> getClass2ObjectProperty2ClassAxioms(OWLClass src, OWLObjectProperty objprop, OWLClass dest) {

		Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		OWLObjectSomeValuesFrom owlObjectSomeValuesFrom;
		OWLObjectAllValuesFrom owlObjectAllValuesFrom;
		OWLObjectMaxCardinality owlObjectMaxCardinality;

		// set domain and range
		// scoped domain
		if (editor.isGenerateDomainAxiom()) {
			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
			tmpaxioms.add(axiom);

			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
			tmpaxioms.add(axiom);
		}
		// scoped range
		if (editor.isGenerateRangeAxiom()) {
			owlObjectAllValuesFrom = owlDataFactory.getOWLObjectAllValuesFrom(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectAllValuesFrom);
			tmpaxioms.add(axiom);

			owlObjectAllValuesFrom = owlDataFactory.getOWLObjectAllValuesFrom(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectAllValuesFrom);
			tmpaxioms.add(axiom);
		}

		// set existential functionality
		// source existential functionality
		if (editor.isGenerateExistentialAxiom()) {
			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectSomeValuesFrom);
			tmpaxioms.add(axiom);
			// destination existential functionality
			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop.getInverseProperty(), src);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(dest, owlObjectSomeValuesFrom);
			tmpaxioms.add(axiom);
		}

		// set cardinality restriction
		// for objectProperty
		if (editor.isGenerateCardinalityAxiom()) {
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop,
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop,
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			// for inverse objectProperty
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop.getInverseProperty(), dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop.getInverseProperty(), dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop.getInverseProperty(),
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop.getInverseProperty(),
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			tmpaxioms.add(axiom);
		}
		// need to implement custom cardinality axiom

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
		OWLObjectMaxCardinality owlObjectMaxCardinality;
		OWLObjectOneOf owlObjectOneOf;

		// set domain and range
		// scoped domain
		if (editor.isGenerateDomainAxiom()) {
			owlLObjectHasValue = owlDataFactory.getOWLObjectHasValue(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlLObjectHasValue, src);
			tmpaxioms.add(axiom);

			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
			tmpaxioms.add(axiom);
		}

		// set existential restriction
		if (editor.isGenerateExistentialAxiom()) {
			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop.getInverseProperty(), src);
			owlObjectOneOf = owlDataFactory.getOWLObjectOneOf(dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectOneOf, owlObjectSomeValuesFrom);
			tmpaxioms.add(axiom);

			owlLObjectHasValue = owlDataFactory.getOWLObjectHasValue(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlLObjectHasValue);
			tmpaxioms.add(axiom);
		}

		// set cardinality restriction
		if (editor.isGenerateCardinalityAxiom()) {
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop,
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop,
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectOneOf = owlDataFactory.getOWLObjectOneOf(dest);
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop, owlObjectOneOf);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			tmpaxioms.add(axiom);

			owlObjectOneOf = owlDataFactory.getOWLObjectOneOf(dest);
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop, owlObjectOneOf);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			tmpaxioms.add(axiom);
		}
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
		OWLDataMaxCardinality owlDataMaxCardinality;

		// set domain and range
		// scoped domain
		if (editor.isGenerateDomainAxiom()) {
			owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
			tmpaxioms.add(axiom);

			owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
			tmpaxioms.add(axiom);
		}

		// scoped range
		if (editor.isGenerateRangeAxiom()) {
			owlDataAllValuesFrom = owlDataFactory.getOWLDataAllValuesFrom(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataAllValuesFrom);
			tmpaxioms.add(axiom);

			owlDataAllValuesFrom = owlDataFactory.getOWLDataAllValuesFrom(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataAllValuesFrom);
			tmpaxioms.add(axiom);
		}

		// set existential restriction
		// source existential functionality
		if (editor.isGenerateExistentialAxiom()) {
			owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataSomeValuesFrom);
			tmpaxioms.add(axiom);
		}

		// destination existential functionality. dataproperty doesn't have
		// inverse property

		// set cardinality restriction
		if (editor.isGenerateCardinalityAxiom()) {
			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMaxCardinality);
			tmpaxioms.add(axiom);

			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataMaxCardinality);
			tmpaxioms.add(axiom);

			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop,
					owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataMaxCardinality);
			tmpaxioms.add(axiom);

			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop,
					owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMaxCardinality);
			tmpaxioms.add(axiom);
		}
		// dataproperty doesn't have inverse property

		// System.out.println(axiom.toString());

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
		OWLDataMaxCardinality owlDataMaxCardinality;
		OWLDataOneOf owldataOneOf;

		// set domain and range
		// scoped domain
		if (editor.isGenerateDomainAxiom()) {
			owlLDataHasValue = owlDataFactory.getOWLDataHasValue(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlLDataHasValue, src);
			tmpaxioms.add(axiom);

			owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
			tmpaxioms.add(axiom);
		}

		if (editor.isGenerateCardinalityAxiom()) {
			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop,
					owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataMaxCardinality);
			tmpaxioms.add(axiom);

			// need to verify with Adila
			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop,
					owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMaxCardinality);
			tmpaxioms.add(axiom);
		}
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
	 * Allowed List
	 * 
	 * class(A)-----------objectProperty(P)-------class(B)
	 * class(A)-----------objectProperty(P)-------individual(B)
	 * class(A)-----------dataProperty(P)---------literal(B)
	 * class(A)-----------dataProperty(P)---------datatype(B)
	 * class(A)-----------rdfs:subclassof---------class(B)
	 * individual(A)------rdf:type----------------class(B)
	 * 
	 * other thing is not allowed
	 */
	// @formatter:on
}
