package edu.wsu.dase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

//import org.checkerframework.checker.nullness.qual.NonNull;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.ui.action.ShowUsageAction;
import org.protege.editor.owl.ui.prefix.PrefixMapperTables;
import org.protege.editor.owl.ui.prefix.PrefixUtilities;
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
	private String ONT_GEN_ERROR_TITLE = "Ontology not generated";
	private String ONT_GEN_ERROR_MESSAGE = "Ontology Generation failed";
	private String ONT_INTG_ERROR_TITLE = "Ontology not integrated woth Protege";
	private String ONT_INTG_ERROR_MESSAGE = "Changes can't be Integrated with Protege.";
	private String ENTITY_WITH_NO_NAME_TITLE = "Entity Without Name";
	private String ENTITY_WITH_NO_NAME_MESSAGE = "Can not save entity wihtout name. Entity must have a name.";
	private ArrayList<OWLAxiom> declarationAxioms;
	private ArrayList<OWLAxiom> domainAndRangeAxioms;
	private ArrayList<OWLAxiom> existentialAxioms;
	private ArrayList<OWLAxiom> cardinalityAxioms;
	private ArrayList<OWLAxiom> subClassOfAxioms;
	private ArrayList<OWLAxiom> classAssertionAxiom;
	private ArrayList<OWLAxiom> selectedAxioms;

	// means no error occurred till now.
	private boolean shouldContinue;

	// default prefix
	private String defaultPrefix;
	mxGraph graph;
	Object root;
	mxGraphModel model;
	private BasicGraphEditor editor;

	public BasicGraphEditor getEditor() {
		return this.editor;
	}

	List<OWLOntologyChange> changes;
	OWLOntologyID owlOntologyID;
	String ontologyBaseURI;
	OWLDataFactory owlDataFactory;
	OWLModelManager owlModelManager;
	OWLOntologyManager owlOntologyManager;
	OWLOntology activeOntology;

	public OWLOntology getActiveOntology() {
		return this.activeOntology;
	}

	// ProtegeIRIResolver iriResolver;
	PrefixManager pm;

	public ArrayList<OWLAxiom> getDeclarationAxioms() {
		return declarationAxioms;
	}

	public void setDeclarationAxioms(ArrayList<OWLAxiom> declarationAxioms) {
		this.declarationAxioms = declarationAxioms;
	}

	public ArrayList<OWLAxiom> getDomainAndRangeAxioms() {
		return domainAndRangeAxioms;
	}

	public void setDomainAndRangeAxioms(ArrayList<OWLAxiom> rangeAndDomainAxioms) {
		this.domainAndRangeAxioms = rangeAndDomainAxioms;
	}

	public ArrayList<OWLAxiom> getExistentialAxioms() {
		return existentialAxioms;
	}

	public void setExistentialAxioms(ArrayList<OWLAxiom> existentialAxioms) {
		this.existentialAxioms = existentialAxioms;
	}

	public ArrayList<OWLAxiom> getCardinalityAxioms() {
		return cardinalityAxioms;
	}

	public void setCardinalityAxioms(ArrayList<OWLAxiom> cardinalityAxioms) {
		this.cardinalityAxioms = cardinalityAxioms;
	}

	public ArrayList<OWLAxiom> getSubClassOfAxioms() {
		return subClassOfAxioms;
	}

	public void setSubClassOfAxioms(ArrayList<OWLAxiom> subClassOfAxioms) {
		this.subClassOfAxioms = subClassOfAxioms;
	}

	public ArrayList<OWLAxiom> getClassAssertionAxiom() {
		return classAssertionAxiom;
	}

	public void setClassAssertionAxiom(ArrayList<OWLAxiom> classAssertionAxiom) {
		this.classAssertionAxiom = classAssertionAxiom;
	}

	public IntegrateOntologyWithProtege(BasicGraphEditor editor) {
		this.editor = editor;
		this.graph = editor.getGraphComponent().getGraph();
		this.model = (mxGraphModel) graph.getModel();
		this.root = graph.getDefaultParent();

		initilizeProtegeDataFactory();
	}

	private void initializeDataStructure() {
		declarationAxioms = new ArrayList<OWLAxiom>();
		domainAndRangeAxioms = new ArrayList<OWLAxiom>();
		existentialAxioms = new ArrayList<OWLAxiom>();
		cardinalityAxioms = new ArrayList<OWLAxiom>();
		subClassOfAxioms = new ArrayList<OWLAxiom>();
		classAssertionAxiom = new ArrayList<OWLAxiom>();
		selectedAxioms = new ArrayList<OWLAxiom>();

		changes = new ArrayList<OWLOntologyChange>();

	}

	private boolean checkEntityNameLength() {
		Object[] v = graph.getChildVertices(graph.getDefaultParent());
		Object[] e = graph.getChildEdges(graph.getDefaultParent());

		for (Object entity : v) {
			if (entity instanceof mxCell) {
				if (((mxCell) entity).getValue().toString().length() <= 0) {
					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), ENTITY_WITH_NO_NAME_MESSAGE,
							ENTITY_WITH_NO_NAME_TITLE, JOptionPane.ERROR_MESSAGE);
					initializeDataStructure();
					editor.status("Failed. " + ENTITY_WITH_NO_NAME_MESSAGE);
					return false;
				}
			}
		}
		for (Object entity : e) {
			if (entity instanceof mxCell) {
				if (((mxCell) entity).getValue().toString().length() <= 0) {
					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), ENTITY_WITH_NO_NAME_MESSAGE,
							ENTITY_WITH_NO_NAME_TITLE, JOptionPane.ERROR_MESSAGE);
					initializeDataStructure();
					editor.status("Failed. " + ENTITY_WITH_NO_NAME_MESSAGE);
					return false;
				}
			}
		}

		return true;
	}

	private boolean showAxiomsDialog() {

		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this.editor);
		AxiomsDialog dialog = new AxiomsDialog(this, topFrame);

		if (dialog.isClickedOK()) {
			selectedAxioms = dialog.getSelectedAxioms();
			if (!selectedAxioms.isEmpty()) {
				for (OWLAxiom axiom : selectedAxioms) {
					changes.add(new AddAxiom(activeOntology, axiom));
				}
				return true;
			} else {
				editor.status("Selected Axioms is empty. Nothing to integrate.");
			}
		} else {
			editor.status("Axiom Selection canceled");
		}
		return false;
	}

	public void generateOntology() {

		if (!checkEntityNameLength())
			return;

		initializeDataStructure();

		Object[] v = graph.getChildVertices(graph.getDefaultParent());
		Object[] e = graph.getChildEdges(graph.getDefaultParent());

		shouldContinue = true;
		if (shouldContinue)
			shouldContinue = makeDeclarations(v);

		if (!shouldContinue)
			return;

		shouldContinue = makeDeclarations(e);
		if (!shouldContinue)
			return;

		shouldContinue = commitDeclarations();
		declarationAxioms.clear();
		if (!shouldContinue) {
			//editor.status("Entity creation failed. ");
			//not returned so that axioms can atleast be viewed
			//return;
		}

		shouldContinue = createOWLAxioms(e);
		if (!shouldContinue) {
			editor.status("Entity creation failed. ");
			return;
		}
		// now show dialog to select
		shouldContinue = showAxiomsDialog();
		if (!shouldContinue) {
			return;
		}

		cleanActiveOntology();
		shouldContinue = saveOWLAxioms();
		if (!shouldContinue) {
			return;
		}
		editor.status(SAVING_COMPLETE_MESSAGE);
		editor.setModified(false);
		JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), SAVING_COMPLETE_MESSAGE, SAVING_COMPLETE_TITLE,
				JOptionPane.PLAIN_MESSAGE);
		return;

	}

	public void addPrefix() {
		String uriString = activeOntology.getOntologyID().getDefaultDocumentIRI().get().toString();
		String prefix;
		if (uriString.endsWith("/")) {
			String sub = uriString.substring(0, uriString.length() - 1);
			prefix = sub.substring(sub.lastIndexOf("/") + 1, sub.length());
		} else {
			prefix = uriString.substring(uriString.lastIndexOf('/') + 1, uriString.length());
		}
		if (prefix.endsWith(".owl")) {
			prefix = prefix.substring(0, prefix.length() - 4);
		}
		prefix = prefix.toLowerCase();
		if (!uriString.endsWith("#") && !uriString.endsWith("/")) {
			uriString = uriString + "#";
		}

		pm.setPrefix(prefix, uriString);
		defaultPrefix = prefix + ":";
	}

	private String getCellValueAsOWLCompatibleName(mxCell cell) {

		String name = cell.getValue().toString().trim().replace(" ", "_");

		return getCellValueAsOWLCompatibleName(cell, name);

	}

	private String getCellValueAsOWLCompatibleName(mxCell cell, String cellValue) {

		String name = cellValue.toString().trim().replace(" ", "_");

		if (name.contains(":")) {
			String[] subParts = name.split(":");
			if (subParts.length == 2) {
				return name;
				// prefixValue = pm.getPrefix((subParts[0].replace(":", "")));
			} else {
				shouldContinue = false;
				editor.status(cell.getEntityType() + " " + cell.getValue() + " has Colon(:) " + subParts.length
						+ " time. Operation aborted.");
				return null;
			}
		} else {
			// JOptionPane.showMessageDialog(editor, defaultPrefix);
			return defaultPrefix + name;
		}

	}

	public void initilizeProtegeDataFactory() {
		owlModelManager = editor.getProtegeOWLModelManager();
		owlDataFactory = owlModelManager.getOWLDataFactory();
		owlOntologyManager = owlModelManager.getOWLOntologyManager();
		// OWLEntityFinder finder = owlModelManager.getOWLEntityFinder();
		// finder.getOWLClass("cls");
		changes = null;

		activeOntology = owlModelManager.getActiveOntology();

		// pm = new DefaultPrefixManager();

		if (activeOntology != null) {

			pm = PrefixUtilities.getPrefixOWLOntologyFormat(activeOntology);
			addPrefix();

			owlOntologyID = activeOntology.getOntologyID();
			ontologyBaseURI = owlOntologyID.getOntologyIRI().get().toQuotedString();
			ontologyBaseURI = ontologyBaseURI.substring(1, ontologyBaseURI.length() - 1) + "#";
			editor.getGraphComponent().setOWLFileTitle(ontologyBaseURI);

		}

	}

	public void cleanActiveOntology() {
		Set<OWLAxiom> axiomsToRemove;
		for (OWLOntology o : activeOntology.getImportsClosure()) {
			axiomsToRemove = new HashSet<OWLAxiom>();
			for (OWLAxiom ax : o.getAxioms()) {
				axiomsToRemove.add(ax);
			}

			owlOntologyManager.removeAxioms(o, axiomsToRemove);
			// System.out.println("After: " + o.getAxiomCount());
		}
	}

	private boolean commitDeclarations() {
		editor.status("Integrating Declaration axioms with Protege");
		if (declarationAxioms != null && !declarationAxioms.isEmpty()) {
			// declarationAxioms
			List<OWLOntologyChange> declarations = new ArrayList<OWLOntologyChange>();
			for (OWLAxiom declarationAxiom : declarationAxioms) {

				declarations.add(new AddAxiom(activeOntology, declarationAxiom));

			}
			ChangeApplied changeResult = owlOntologyManager.applyChanges(declarations);
			if (changeResult == ChangeApplied.SUCCESSFULLY) {
				editor.status("Declaration axioms integrated with protege successfully.");
				return true;
			} else if (changeResult == ChangeApplied.UNSUCCESSFULLY) {
				editor.status("Declaration integration with Protege unsuccessfull.");
				return false;
			} else if (changeResult == ChangeApplied.NO_OPERATION) {
				editor.status(
						"Declaration axioms are duplicate. Possible reason: trying to create new OWL Entity which IRI match with existing OWLEntity IRI.");
				return false;
			}
		} else
			return false;

		return false;
	}

	private boolean saveOWLAxioms() {
		editor.status("Integrating axioms with Protege");
		if (changes != null) {
			ChangeApplied changeResult = owlOntologyManager.applyChanges(changes);
			if (changeResult == ChangeApplied.SUCCESSFULLY) {
				editor.status("All axioms integrated with protege successfully.");
				return true;
			} else if (changeResult == ChangeApplied.UNSUCCESSFULLY) {
				editor.status("Axiom integration with Protege unsuccessfull.");
				return false;
			} else if (changeResult == ChangeApplied.NO_OPERATION) {
				editor.status("Selected axioms are duplicate. No operation carried out (change had no effect)");
				return false;
			}
		} else
			return false;

		return false;
	}

	private OWLAxiom createOWLAnnotationProperty(String name) {

		OWLAnnotationProperty annoprop = owlDataFactory.getOWLAnnotationProperty(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(annoprop);

		return declareaxiom;

	}

	private OWLAxiom createOWLDataProperty(String name) {

		OWLDataProperty dataprop = owlDataFactory.getOWLDataProperty(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(dataprop);

		return declareaxiom;

	}

	private OWLAxiom createOWLObjectProperty(String name) {

		OWLObjectProperty objprop = owlDataFactory.getOWLObjectProperty(name, pm);
		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(objprop);

		return declareaxiom;

	}

	private OWLAxiom createOWLClass(String name) {

		OWLClass newClass = owlDataFactory.getOWLClass(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(newClass);

		return declareaxiom;

	}

	private OWLAxiom createOWLNamedIndividual(String name) {

		OWLNamedIndividual newIndividual = owlDataFactory.getOWLNamedIndividual(name, pm);

		OWLAxiom declareaxiom = owlDataFactory.getOWLDeclarationAxiom(newIndividual);

		return declareaxiom;

	}

	private boolean makeDeclarations(Object[] VerticesOrEdges) {

		for (Object vertexOrEdge : VerticesOrEdges) {
			if (vertexOrEdge instanceof mxCell) {
				mxCell cell = (mxCell) vertexOrEdge;
				if (cell.getValue().toString().length() > 0) {
					CustomEntityType CustomEntityType = cell.getEntityType();

					String cellLabel = getCellValueAsOWLCompatibleName(cell);
					if (cellLabel == null)
						return false;
					editor.status("Creating Declaration Axioms with: " + cellLabel);

					if (CustomEntityType.getName().equals(CustomEntityType.CLASS.getName())) {
						declarationAxioms.add(createOWLClass(cellLabel));
					} else if (CustomEntityType.getName().equals(CustomEntityType.NAMED_INDIVIDUAL.getName())) {
						declarationAxioms.add(createOWLNamedIndividual(cellLabel));
					} else if (CustomEntityType.getName().equals(CustomEntityType.DATATYPE.getName())) {
						// if not existing datatype then create
					} else if (CustomEntityType.getName().equals(CustomEntityType.LITERAL.getName())) {
						// declarationAxioms.add(createOWLLiteral(cell.getValue().toString()));
					} else if (CustomEntityType.getName().equals(CustomEntityType.OBJECT_PROPERTY.getName())) {
						String[] multValues = getCellValues(cellLabel);
						for (String val : multValues) {
							declarationAxioms.add(createOWLObjectProperty(val));
						}
					} else if (CustomEntityType.getName().equals(CustomEntityType.DATA_PROPERTY.getName())) {
						String[] multValues = getCellValues(cellLabel);
						for (String val : multValues) {
							declarationAxioms.add(createOWLDataProperty(val));
						}
					} else if (CustomEntityType.getName().equals(CustomEntityType.ANNOTATION_PROPERTY.getName())) {
						// although it is not required but implemented
						declarationAxioms.add(createOWLAnnotationProperty(cellLabel));
					}
				} else {

					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), ENTITY_WITH_NO_NAME_MESSAGE,
							ENTITY_WITH_NO_NAME_TITLE, JOptionPane.ERROR_MESSAGE);
					initializeDataStructure();
					editor.status("Failed. " + ENTITY_WITH_NO_NAME_MESSAGE);
					return false;
				}
			}
		}
		if (!declarationAxioms.isEmpty()) {
			return true;
		} else {
			editor.status("Declaration Axioms empty. Integration terminated.");
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

						createOWLAxiom(src, edgeCell, trg);
						// for (OWLAxiom tmpAxiom : tmpAxioms) {
						// OWLOntologyChange change = new
						// AddAxiom(activeOntology, tmpAxiom);
						// changes.add(change);
						// }
					}

				} else {
					JOptionPane.showMessageDialog(editor.getProtegeMainWindow(), ENTITY_WITH_NO_NAME_MESSAGE,
							ENTITY_WITH_NO_NAME_TITLE, JOptionPane.ERROR_MESSAGE);
					initializeDataStructure();
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
	private void createOWLAxiom(mxCell src, mxCell edge, mxCell dest) {
		editor.status("Creating axioms from " + src.getValue() + " " + edge.getValue() + " " + dest.getValue());
		if (edge.getEntityType().getName().equals(CustomEntityType.OBJECT_PROPERTY.getName())) {

			String[] multValues = getCellValues(edge.getValue().toString().trim().replace(" ", "_"));
			for (String val : multValues) {

				OWLObjectProperty objprop = owlDataFactory
						.getOWLObjectProperty(getCellValueAsOWLCompatibleName(edge, val), pm);
				if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
						&& dest.getEntityType().getName().equals(CustomEntityType.CLASS.getName())) {

					getClass2ObjectProperty2ClassAxioms(
							owlDataFactory.getOWLClass(getCellValueAsOWLCompatibleName(src), pm), objprop,
							owlDataFactory.getOWLClass(getCellValueAsOWLCompatibleName(dest), pm));

				} else if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
						&& dest.getEntityType().getName().equals(CustomEntityType.NAMED_INDIVIDUAL.getName())) {

					getClass2ObjectProperty2IndividualAxioms(
							owlDataFactory.getOWLClass(getCellValueAsOWLCompatibleName(src), pm), objprop,
							owlDataFactory.getOWLNamedIndividual(getCellValueAsOWLCompatibleName(dest), pm));

				} else {
					// error. it can't occur. validation should be done
				}
			}
		} else if (edge.getEntityType().getName().equals(CustomEntityType.DATA_PROPERTY.getName())) {
			OWLDataProperty dataprop = owlDataFactory.getOWLDataProperty(getCellValueAsOWLCompatibleName(edge), pm);

			if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
					&& dest.getEntityType().getName().equals(CustomEntityType.LITERAL.getName())) {

				getClass2DataProperty2LiteralAxioms(
						owlDataFactory.getOWLClass(getCellValueAsOWLCompatibleName(src), pm), dataprop,
						getOWLLiteral(dest));
			} else if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
					&& dest.getEntityType().getName().equals(CustomEntityType.DATATYPE.getName())) {

				// get OWLDataType.. from getCustomOWLDataType
				OWLDatatype owlDatatype = getCustomOWLDataType(getCellValueAsOWLCompatibleName(dest));
				getClass2DataProperty2DataTypeAxioms(
						owlDataFactory.getOWLClass(getCellValueAsOWLCompatibleName(src), pm), dataprop, owlDatatype);
			}

		} else if (edge.getEntityType().getName().equals(CustomEntityType.RDFTYPE.getName())) {
			if (src.getEntityType().getName().equals(CustomEntityType.NAMED_INDIVIDUAL.getName())
					&& dest.getEntityType().getName().equals(CustomEntityType.CLASS.getName())) {
				getInvdividual2RDFType2ClassAxioms(
						owlDataFactory.getOWLNamedIndividual(getCellValueAsOWLCompatibleName(src), pm),
						owlDataFactory.getOWLClass(getCellValueAsOWLCompatibleName(dest), pm));
			} else {
				// error. it can't occur. validation should be done
			}

		} else if (edge.getEntityType().getName().equals(CustomEntityType.RDFSSUBCLASS_OF.getName())) {
			if (src.getEntityType().getName().equals(CustomEntityType.CLASS.getName())
					&& dest.getEntityType().getName().equals(CustomEntityType.CLASS.getName())) {
				getClass2RDFSSubClassOf2ClassAxioms(
						owlDataFactory.getOWLClass(getCellValueAsOWLCompatibleName(src), pm),
						owlDataFactory.getOWLClass(getCellValueAsOWLCompatibleName(dest), pm));
			} else {
				// error. it can't occur. validation should be done
			}
		} else {
			// error. it can't occur. validation should be done
		}

	}

	private String[] getCellValues(String cellVal) {
		if (cellVal.length() > 0) {
			cellVal = cellVal.trim();
			cellVal = cellVal.replace(" ", "_");
			return cellVal.split(",");
		}
		return null;
	}

	private String getLiteralCellValue(mxCell literal) {

		String labelValueOnly = "";
		Pattern pattern = Pattern.compile("\"(.*?)\"");
		String cellVal = literal.getValue().toString().trim().replace(" ", "_");
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

		// N.B: Custom literal has also URI as---
		// owl:Custom

		OWLDatatype odt = owlDataFactory.getOWLDatatype(getLiteralTypeValue(cell), pm);
		OWLLiteral literal = owlDataFactory.getOWLLiteral(getLiteralCellValue(cell), odt);

		return literal;
	}

	/**
	 * create axioms for class--objectproperty----class relation.
	 */
	private void getClass2ObjectProperty2ClassAxioms(OWLClass src, OWLObjectProperty objprop, OWLClass dest) {

		// Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		OWLObjectSomeValuesFrom owlObjectSomeValuesFrom;
		OWLObjectAllValuesFrom owlObjectAllValuesFrom;
		OWLObjectMaxCardinality owlObjectMaxCardinality;

		// set domain and range
		// scoped domain
		if (editor.isGenerateDomainAxiom()) {
			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
			domainAndRangeAxioms.add(axiom);

			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
			domainAndRangeAxioms.add(axiom);
		}
		// scoped range
		if (editor.isGenerateRangeAxiom()) {
			owlObjectAllValuesFrom = owlDataFactory.getOWLObjectAllValuesFrom(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectAllValuesFrom);
			domainAndRangeAxioms.add(axiom);

			owlObjectAllValuesFrom = owlDataFactory.getOWLObjectAllValuesFrom(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectAllValuesFrom);
			domainAndRangeAxioms.add(axiom);
		}

		// set existential functionality
		// source existential functionality
		if (editor.isGenerateExistentialAxiom()) {
			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectSomeValuesFrom);
			existentialAxioms.add(axiom);
			// destination existential functionality
			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop.getInverseProperty(), src);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(dest, owlObjectSomeValuesFrom);
			existentialAxioms.add(axiom);
		}

		// set cardinality restriction
		// for objectProperty
		if (editor.isGenerateCardinalityAxiom()) {
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop,
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop,
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			// for inverse objectProperty
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop.getInverseProperty(), dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop.getInverseProperty(), dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop.getInverseProperty(),
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop.getInverseProperty(),
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);
		}
		// need to implement custom cardinality axiom

		// return tmpaxioms;
	}

	/**
	 * create axioms for class--objectproperty----individual relation.
	 * 
	 * @param src
	 * @param objprop
	 * @param dest
	 * @return
	 */
	private void getClass2ObjectProperty2IndividualAxioms(OWLClass src, OWLObjectProperty objprop, OWLIndividual dest) {
		// Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
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
			domainAndRangeAxioms.add(axiom);

			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop, owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectSomeValuesFrom, src);
			domainAndRangeAxioms.add(axiom);
		}

		// set existential restriction
		if (editor.isGenerateExistentialAxiom()) {
			owlObjectSomeValuesFrom = owlDataFactory.getOWLObjectSomeValuesFrom(objprop.getInverseProperty(), src);
			owlObjectOneOf = owlDataFactory.getOWLObjectOneOf(dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlObjectOneOf, owlObjectSomeValuesFrom);
			existentialAxioms.add(axiom);

			owlLObjectHasValue = owlDataFactory.getOWLObjectHasValue(objprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlLObjectHasValue);
			existentialAxioms.add(axiom);
		}

		// set cardinality restriction
		if (editor.isGenerateCardinalityAxiom()) {
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop,
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop,
					owlDataFactory.getOWLThing());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectOneOf = owlDataFactory.getOWLObjectOneOf(dest);
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop, owlObjectOneOf);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlObjectOneOf = owlDataFactory.getOWLObjectOneOf(dest);
			owlObjectMaxCardinality = owlDataFactory.getOWLObjectMaxCardinality(1, objprop, owlObjectOneOf);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlObjectMaxCardinality);
			cardinalityAxioms.add(axiom);
		}
		// return tmpaxioms;
	}

	/**
	 * create axioms for class--dataproperty----datatype relation.
	 */
	// be sure for OWLDatatype vs OWL2DataType
	private void getClass2DataProperty2DataTypeAxioms(OWLClass src, OWLDataProperty dataprop, OWLDatatype dest) {

		// Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		OWLDataSomeValuesFrom owlDataSomeValuesFrom;
		OWLDataAllValuesFrom owlDataAllValuesFrom;
		OWLDataMaxCardinality owlDataMaxCardinality;

		// set domain and range
		// scoped domain
		if (editor.isGenerateDomainAxiom()) {
			owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
			domainAndRangeAxioms.add(axiom);

			owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
			domainAndRangeAxioms.add(axiom);
		}

		// scoped range
		if (editor.isGenerateRangeAxiom()) {
			owlDataAllValuesFrom = owlDataFactory.getOWLDataAllValuesFrom(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataAllValuesFrom);
			domainAndRangeAxioms.add(axiom);

			owlDataAllValuesFrom = owlDataFactory.getOWLDataAllValuesFrom(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataAllValuesFrom);
			domainAndRangeAxioms.add(axiom);
		}

		// set existential restriction
		// source existential functionality
		if (editor.isGenerateExistentialAxiom()) {
			owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataSomeValuesFrom);
			existentialAxioms.add(axiom);
		}

		// destination existential functionality. dataproperty doesn't have
		// inverse property

		// set cardinality restriction
		if (editor.isGenerateCardinalityAxiom()) {
			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop, dest);
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop,
					owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataMaxCardinality);
			cardinalityAxioms.add(axiom);

			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop,
					owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMaxCardinality);
			cardinalityAxioms.add(axiom);
		}
		// dataproperty doesn't have inverse property

		// System.out.println(axiom.toString());

		// return tmpaxioms;
	}

	/**
	 * create axioms for class--dataproperty----literal relation.
	 * 
	 * @param src
	 * @param dataprop
	 * @param dest
	 * @return
	 */
	private void getClass2DataProperty2LiteralAxioms(OWLClass src, OWLDataProperty dataprop, OWLLiteral dest) {
		// Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
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
			domainAndRangeAxioms.add(axiom);

			owlDataSomeValuesFrom = owlDataFactory.getOWLDataSomeValuesFrom(dataprop, owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataSomeValuesFrom, src);
			domainAndRangeAxioms.add(axiom);
		}

		if (editor.isGenerateCardinalityAxiom()) {
			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop,
					owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(owlDataFactory.getOWLThing(), owlDataMaxCardinality);
			cardinalityAxioms.add(axiom);

			// need to verify with Adila
			owlDataMaxCardinality = owlDataFactory.getOWLDataMaxCardinality(1, dataprop,
					owlDataFactory.getTopDatatype());
			axiom = owlDataFactory.getOWLSubClassOfAxiom(src, owlDataMaxCardinality);
			cardinalityAxioms.add(axiom);
		}
		// return tmpaxioms;
	}

	/**
	 * create axioms for individual----rdftype----class relation.
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	private void getClass2RDFSSubClassOf2ClassAxioms(OWLClass src, OWLClass dest) {
		// Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		axiom = owlDataFactory.getOWLSubClassOfAxiom(src, dest);

		subClassOfAxioms.add(axiom);

	}

	private void getInvdividual2RDFType2ClassAxioms(OWLIndividual src, OWLClass dest) {
		// Set<OWLAxiom> tmpaxioms = new HashSet<OWLAxiom>();
		OWLAxiom axiom;
		axiom = owlDataFactory.getOWLClassAssertionAxiom(dest, src);
		classAssertionAxiom.add(axiom);

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
