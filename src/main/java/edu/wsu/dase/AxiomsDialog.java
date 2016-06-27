package edu.wsu.dase;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class AxiomsDialog extends JDialog {

	private static final long serialVersionUID = 4648172894076113183L;
	private Button integrateBtn;
	private Button cancelBtn;
	final String integrateBtnText = "Integrate";
	final String existingAxiomsLblText = "Existing Axioms";
	final String newAxiomsLblText = "Generated Axioms";
	final String cancelBtnText = "Cancel";
	final String declarationAxiomTypeText = "Declaration Axioms";
	final String existentialAxiomTypeText = "Existential Axioms";
	final String cardinalityAxiomTypeText = "Cardinality Axioms";
	final String domainandRangeAxiomTypeText = "Domain-Range Axioms";
	final String subClassOfAxiomTypeText = "SubClassOf Axiom";
	final String classAssertionAxiomTypeText = "Class(Type) Assertion Axiom";

	private JPanel mainPnl;
	private JSplitPane splitPane;
	private JPanel bottomPnl;
	private JPanel existingAxiomsPnl;
	private JPanel newAxiomsPnl;
	private JScrollPane existingAxiomsScroll;
	private JScrollPane newAxiomsScroll;
	private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.5;
	OWLOntology activeOntology;
	private DefaultMutableTreeNode existingAxiomsRoot;
	private DefaultMutableTreeNode newAxiomsRoot;
	private final ArrayList<OWLAxiom> selectedExistingAxioms;
	private final ArrayList<OWLAxiom> selectedNewAxioms;
	private JCheckBoxTree newAxiomsTree;
	private JCheckBoxTree existingAxiomsTree;
	private IntegrateOntologyWithProtege intgOntWProtege;
	private JFrame parent;
	private boolean isClickedOK;

	public boolean isClickedOK() {
		return this.isClickedOK;
	}

	public void setClickedOK(boolean isClickedOK) {
		this.isClickedOK = isClickedOK;
	}

	public AxiomsDialog(IntegrateOntologyWithProtege integrateOntologyWithProtege, JFrame parent) {
		super(parent);
		this.parent = parent;
		this.selectedNewAxioms = new ArrayList<OWLAxiom>();
		this.selectedExistingAxioms = new ArrayList<OWLAxiom>();
		this.intgOntWProtege = integrateOntologyWithProtege;
		this.isClickedOK = false;
		
		new UserObjectforTreeView(parent, integrateOntologyWithProtege.getActiveOntology());
		
		initUI();
		showUI();
	}
	
	public void initUI() {

		setSize(500, 500);
		setLocationRelativeTo(parent);
		setTitle("Select Axioms");
		this.getContentPane().setLayout(new BorderLayout());

		// main panel
		mainPnl = new JPanel();
		mainPnl.setLayout(new BorderLayout());

		// bottom Panel
		bottomPnl = new JPanel();
		bottomPnl.setLayout(new BorderLayout());

		integrateBtn = new Button(integrateBtnText);
		integrateBtn.setSize(new Dimension(100, 30));
		integrateBtn.setPreferredSize(new Dimension(100, 30));
		integrateBtn.setMaximumSize(new Dimension(100, 30));
		integrateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// now integrate with protege
				extractSelectedAxioms();
				setClickedOK(true);
				dispose();
			}
		});
		JPanel pnl1 = new JPanel();
		pnl1.setLayout(new BorderLayout());
		pnl1.add(integrateBtn, BorderLayout.EAST);
		bottomPnl.add(pnl1, BorderLayout.CENTER);

		cancelBtn = new Button(cancelBtnText);
		cancelBtn.setSize(new Dimension(100, 30));
		cancelBtn.setPreferredSize(new Dimension(100, 30));
		cancelBtn.setMaximumSize(new Dimension(100, 30));

		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setClickedOK(false);
				dispose();
			}
		});
		JPanel pnl2 = new JPanel();
		pnl2.setLayout(new BorderLayout());
		pnl2.add(cancelBtn, BorderLayout.EAST);
		bottomPnl.add(pnl2, BorderLayout.EAST);

		// splitPane
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setResizeWeight(SPLIT_PANE_RESIZE_WEIGHT);

		JScrollPane scrollPaneNew = new JScrollPane();
		JScrollPane scrollPaneExisting = new JScrollPane();

		splitPane.setTopComponent(getNewAxiomsPnl());
		splitPane.setBottomComponent(getExistingAxiomsPnl());
		mainPnl.add(splitPane);

		this.add(mainPnl, BorderLayout.CENTER);
		this.add(bottomPnl, BorderLayout.SOUTH);

	}

	private void showUI() {
		this.setModal(true);
		this.setVisible(true);
	}

	private void extractSelectedAxioms() {
		TreePath[] paths;

		// existing axioms
		paths = existingAxiomsTree.getCheckedPaths();
		for (TreePath tp : paths) {
			DefaultMutableTreeNode eachNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
			if (eachNode.getUserObject() instanceof UserObjectforTreeView) {

				UserObjectforTreeView objTV = (UserObjectforTreeView) eachNode.getUserObject();
				if (objTV.isAxiom()) {
					selectedExistingAxioms.add(objTV.getAxiom());
				}
			}

		}

		// new axioms
		paths = newAxiomsTree.getCheckedPaths();
		for (TreePath tp : paths) {
			DefaultMutableTreeNode eachNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
			if (eachNode.getUserObject() instanceof UserObjectforTreeView) {
				//System.out.println("outside: can be done");
				//System.out.println(
				//		eachNode.getUserObject() + "\t" + ((UserObjectforTreeView) eachNode.getUserObject()).isAxiom());
				UserObjectforTreeView objTV = (UserObjectforTreeView) eachNode.getUserObject();
				if (objTV.isAxiom()) {
					selectedNewAxioms.add(objTV.getAxiom());
				}
			}
		}

	}

	private JPanel getExistingAxiomsPnl() {
		existingAxiomsPnl = new JPanel();
		existingAxiomsPnl.setLayout(new BorderLayout());

		JLabel lblExistingAxioms = new JLabel(existingAxiomsLblText);
		lblExistingAxioms.setBorder(BorderFactory.createLineBorder(Color.orange, 2));
		// lblExistingAxioms.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblExistingAxioms.setHorizontalAlignment(SwingConstants.CENTER);
		existingAxiomsPnl.add(lblExistingAxioms, BorderLayout.NORTH);

		existingAxiomsTree = new JCheckBoxTree(getExistingAxiomsRoot());
		existingAxiomsScroll = new JScrollPane(existingAxiomsTree);

		existingAxiomsPnl.add(existingAxiomsScroll, BorderLayout.CENTER);

		return existingAxiomsPnl;
	}

	private JPanel getNewAxiomsPnl() {
		newAxiomsPnl = new JPanel();
		newAxiomsPnl.setLayout(new BorderLayout());

		JLabel lblNewAxioms = new JLabel(newAxiomsLblText);
		lblNewAxioms.setBorder(BorderFactory.createLineBorder(Color.orange, 2));
		lblNewAxioms.setHorizontalAlignment(SwingConstants.CENTER);
		newAxiomsPnl.add(lblNewAxioms, BorderLayout.NORTH);

		newAxiomsTree = new JCheckBoxTree(getNewAxiomsRoot());
		newAxiomsScroll = new JScrollPane(newAxiomsTree);

		newAxiomsPnl.add(newAxiomsScroll, BorderLayout.CENTER);

		return newAxiomsPnl;
	}

	public DefaultMutableTreeNode getExistingAxiomsRoot() {

		existingAxiomsRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Select All"));
		DefaultMutableTreeNode childNode;

		if (intgOntWProtege.getActiveOntology() != null) {

			for (OWLAxiom axiom : intgOntWProtege.getActiveOntology().getAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				existingAxiomsRoot.add(childNode);
			}
		}

		return existingAxiomsRoot;
	}

	public DefaultMutableTreeNode getNewAxiomsRoot() {
		newAxiomsRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Select All"));
		DefaultMutableTreeNode subRoot;
		DefaultMutableTreeNode childNode;

		// Declaration Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, declarationAxiomTypeText));
		if (intgOntWProtege.getDeclarationAxioms() != null) {
			for (OWLAxiom axiom : intgOntWProtege.getDeclarationAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
		}
		newAxiomsRoot.add(subRoot);

		// SubClassOf Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, subClassOfAxiomTypeText));
		if (intgOntWProtege.getSubClassOfAxioms() != null) {
			for (OWLAxiom axiom : intgOntWProtege.getSubClassOfAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
		}
		newAxiomsRoot.add(subRoot);

		// Domain and RangeAxiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, domainandRangeAxiomTypeText));
		if (intgOntWProtege.getDomainAndRangeAxioms() != null) {
			for (OWLAxiom axiom : intgOntWProtege.getDomainAndRangeAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
		}
		newAxiomsRoot.add(subRoot);

		// Existential Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, existentialAxiomTypeText));
		if (intgOntWProtege.getExistentialAxioms() != null) {
			for (OWLAxiom axiom : intgOntWProtege.getExistentialAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
		}
		newAxiomsRoot.add(subRoot);

		// Cardinality Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, cardinalityAxiomTypeText));
		if (intgOntWProtege.getCardinalityAxioms() != null) {
			for (OWLAxiom axiom : intgOntWProtege.getCardinalityAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
		}
		newAxiomsRoot.add(subRoot);

		// Class Assertion Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, classAssertionAxiomTypeText));
		if (intgOntWProtege.getClassAssertionAxiom() != null) {
			for (OWLAxiom axiom : intgOntWProtege.getClassAssertionAxiom()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
		}
		newAxiomsRoot.add(subRoot);

		return newAxiomsRoot;
	}

	public ArrayList<OWLAxiom> getSelectedAxioms() {

		selectedExistingAxioms.addAll(selectedNewAxioms);
		return selectedExistingAxioms;

	}

	// for testing purpose only
	public DefaultMutableTreeNode getRoot() {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Music"));
		DefaultMutableTreeNode category;
		DefaultMutableTreeNode composer;
		DefaultMutableTreeNode style;
		// Classical
		category = new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Classical"));

		// Beethoven
		category.add(composer = new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Beethoven")));

		composer.add(style = new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Concertos")));
		composer.add(style = new DefaultMutableTreeNode("Quartets"));

		style.add(new DefaultMutableTreeNode(new UserObjectforTreeView(false, "No. 1 - C Major")));
		style.add(new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Six String Quartets")));

		root.add(category);

		return root;
	}

	// for testing purpose only
	public AxiomsDialog() {
		super();

		this.selectedNewAxioms = new ArrayList<OWLAxiom>();
		this.selectedExistingAxioms = new ArrayList<OWLAxiom>();

		DefaultMutableTreeNode root = getRoot();

		final JCheckBoxTree cbt = new JCheckBoxTree(root);

		DefaultMutableTreeNode inferredroot = (DefaultMutableTreeNode) cbt.getModel().getRoot();
		Enumeration e = inferredroot.breadthFirstEnumeration();

		// this.getContentPane().add(cbt,BorderLayout.NORTH);
		initUI();
		// getNewAxiomsPnl().add(cbt, BorderLayout.CENTER);
		// getNewAxiomsPnl().repaint();

		cbt.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {

			public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
				// System.out.println("event");
				TreePath[] paths = cbt.getCheckedPaths();
				for (TreePath tp : paths) {
					for (Object pathPart : tp.getPath()) {
						System.out.print(pathPart + ",\t" + tp.getLastPathComponent());
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
						if (parentNode.getUserObject() instanceof UserObjectforTreeView) {
							System.out.println("outside: can be done");
							System.out.println(((UserObjectforTreeView) parentNode.getUserObject()).isAxiom());
						} else
							System.out.println("outside: not posible");

					}
					System.out.println();
				}
			}
		});
		// this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	// for testing purpose only
	public static void main(String args[]) {
		AxiomsDialog m = new AxiomsDialog();
		m.setVisible(true);

	}
}
