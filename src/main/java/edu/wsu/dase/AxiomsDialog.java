package edu.wsu.dase;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

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
	final String disJointAxiomTypeText = "Disjoint Classes Axiom";
	final String classAssertionAxiomTypeText = "Class (Type) Assertion Axiom";
	final String otherAxiomTypeText = "Other Axioms";
	final String infoText = "  Select axioms which you want to integrate.";

	private JPanel mainPnl;
	// private JSplitPane splitPane;
	private JPanel bottomPnl;
	private JLabel infoLbl;
	private JPanel existingAxiomsPnl;
	private JPanel newAxiomsPnl;
	private JScrollPane existingAxiomsScroll;
	private JScrollPane newAxiomsScroll;
	// private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.5;
	OWLOntology activeOntology;
	private DefaultMutableTreeNode existingAxiomsRoot;
	private DefaultMutableTreeNode newAxiomsRoot;
	private final ArrayList<OWLAxiom> selectedExistingAxioms;
	private final ArrayList<OWLAxiom> selectedNewAxioms;
	private JCheckBoxTree newAxiomsTree;
	// private JCheckBoxTree existingAxiomsTree;
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

		// for sorting rendering is accomplished
		ManchesterOWLSyntaxPrefixNameShortFormProvider shortFormProvider = new ManchesterOWLSyntaxPrefixNameShortFormProvider(
				this.intgOntWProtege.getActiveOntology());
		rendering.setShortFormProvider(shortFormProvider);

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

		infoLbl = new JLabel();
		infoLbl.setText(infoText);

		JPanel pnl1 = new JPanel();
		pnl1.setLayout(new BorderLayout());
		pnl1.add(infoLbl, BorderLayout.WEST);
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

		mainPnl.add(getNewAxiomsPnl());

		this.add(mainPnl, BorderLayout.CENTER);
		this.add(bottomPnl, BorderLayout.SOUTH);

		// set All other axioms as Selected
		if (existingAxiomsRoot != null) {
			if (existingAxiomsRoot.getChildCount() > 0) {
				newAxiomsTree.setSelectedOtherAxioms(new TreePath(newAxiomsRoot.getPath()));
			}
		}
	}

	private void showUI() {
		this.setModal(true);
		this.setVisible(true);
	}

	private void extractSelectedAxioms() {
		TreePath[] paths;

		// new axioms
		paths = newAxiomsTree.getCheckedPaths();
		for (TreePath tp : paths) {
			DefaultMutableTreeNode eachNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
			if (eachNode.getUserObject() instanceof UserObjectforTreeView) {

				UserObjectforTreeView objTV = (UserObjectforTreeView) eachNode.getUserObject();
				if (objTV.isAxiom()) {
					selectedNewAxioms.add(objTV.getAxiom());
				}
			}
		}

	}

	private JPanel getNewAxiomsPnl() {
		newAxiomsPnl = new JPanel();
		newAxiomsPnl.setLayout(new BorderLayout());

		JLabel lblNewAxioms = new JLabel(newAxiomsLblText);
		lblNewAxioms.setBorder(BorderFactory.createLineBorder(Color.orange, 2));
		lblNewAxioms.setHorizontalAlignment(SwingConstants.CENTER);
		newAxiomsPnl.add(lblNewAxioms, BorderLayout.NORTH);

		// just for checking
		new JCheckBoxTree().activeontologyAxioms = intgOntWProtege.owlEditorKit.getOWLModelManager().getActiveOntology()
				.getAxioms();

		newAxiomsTree = new JCheckBoxTree(getNewAxiomsRoot(), intgOntWProtege.owlEditorKit);
		newAxiomsScroll = new JScrollPane(newAxiomsTree);

		newAxiomsPnl.add(newAxiomsScroll, BorderLayout.CENTER);

		return newAxiomsPnl;
	}

	static ManchesterOWLSyntaxOWLObjectRendererImpl rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

	public DefaultMutableTreeNode getNewAxiomsRoot() {
		newAxiomsRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Select All"));
		DefaultMutableTreeNode subRoot;
		DefaultMutableTreeNode childNode;

		// SubClassOf Axiom
		if (intgOntWProtege.getSubClassOfAxioms() != null && !intgOntWProtege.getSubClassOfAxioms().isEmpty()) {
			subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, subClassOfAxiomTypeText));

			for (OWLAxiom axiom : intgOntWProtege.getSubClassOfAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
			newAxiomsRoot.add(subRoot);
		}

		// DisjointOF Axiom
		if (intgOntWProtege.getDisJointOfAxioms() != null && !intgOntWProtege.getDisJointOfAxioms().isEmpty()) {
			subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, disJointAxiomTypeText));
			for (OWLAxiom axiom : intgOntWProtege.getDisJointOfAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
			newAxiomsRoot.add(subRoot);
		}

		// Domain and RangeAxiom
		if (intgOntWProtege.getDomainAndRangeAxioms() != null && !intgOntWProtege.getDomainAndRangeAxioms().isEmpty()) {
			subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, domainandRangeAxiomTypeText));
			for (OWLAxiom axiom : intgOntWProtege.getDomainAndRangeAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
			newAxiomsRoot.add(subRoot);
		}

		// Existential Axiom
		if (intgOntWProtege.getExistentialAxioms() != null && (!intgOntWProtege.getExistentialAxioms().isEmpty())) {
			subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, existentialAxiomTypeText));
			for (OWLAxiom axiom : intgOntWProtege.getExistentialAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
			newAxiomsRoot.add(subRoot);
		}

		// Cardinality Axiom
		if (intgOntWProtege.getCardinalityAxioms() != null && !intgOntWProtege.getCardinalityAxioms().isEmpty()) {
			subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, cardinalityAxiomTypeText));
			for (OWLAxiom axiom : intgOntWProtege.getCardinalityAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
			newAxiomsRoot.add(subRoot);
		}

		// Class Assertion Axiom
		if (intgOntWProtege.getClassAssertionAxioms() != null && !intgOntWProtege.getClassAssertionAxioms().isEmpty()) {
			subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, classAssertionAxiomTypeText));
			for (OWLAxiom axiom : intgOntWProtege.getClassAssertionAxioms()) {
				childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
				subRoot.add(childNode);
			}
			newAxiomsRoot.add(subRoot);
		}

		// Existing Axioms as other Axioms
		if (intgOntWProtege.getActiveOntology() != null) {
			if (intgOntWProtege.getActiveOntology().getAxioms().size() > 0) {
				existingAxiomsRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, otherAxiomTypeText));

				// sort existing axioms before showing
				ArrayList<OWLAxiom> existingAxioms = new ArrayList<>();
				existingAxioms.addAll(intgOntWProtege.getActiveOntology().getAxioms());
				Collections.sort(existingAxioms, new Comparator<OWLAxiom>() {

					@Override
					public int compare(OWLAxiom o1, OWLAxiom o2) {
						String a1 = rendering.render(o1);
						String a2 = rendering.render(o2);

						return a1.compareToIgnoreCase(a2);
					}
				});

				for (OWLAxiom axiom : existingAxioms) {

					// if not contain in existing list only then add here---
					if (!isAlreadyListed(axiom)) {
						childNode = new DefaultMutableTreeNode(new UserObjectforTreeView(true, axiom));
						existingAxiomsRoot.add(childNode);
					}

				}
				if (existingAxiomsRoot.getChildCount() > 0) {
					newAxiomsRoot.add(existingAxiomsRoot);
				}
			}
		}
		return newAxiomsRoot;
	}

	private boolean isAlreadyListed(OWLAxiom axiom) {

		if (intgOntWProtege.getClassAssertionAxioms().contains(axiom))
			return true;
		if (intgOntWProtege.getSubClassOfAxioms().contains(axiom))
			return true;
		if (intgOntWProtege.getCardinalityAxioms().contains(axiom))
			return true;
		if (intgOntWProtege.getDomainAndRangeAxioms().contains(axiom))
			return true;
		if (intgOntWProtege.getDisJointOfAxioms().contains(axiom))
			return true;
		if (intgOntWProtege.getExistentialAxioms().contains(axiom))
			return true;

		return false;
	}

	private boolean isContainedInActiveOntology(OWLAxiom axiom, OWLOntology _activeOntology) {

		if (_activeOntology.containsAxiomIgnoreAnnotations(axiom, true))
			return true;

		return false;

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

		final JCheckBoxTree cbt = new JCheckBoxTree(root, intgOntWProtege.owlEditorKit);

		DefaultMutableTreeNode inferredroot = (DefaultMutableTreeNode) cbt.getModel().getRoot();
		Enumeration e = inferredroot.breadthFirstEnumeration();

		initUI();

		cbt.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {

			public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
				// System.out.println("event");
				TreePath[] paths = cbt.getCheckedPaths();
				for (TreePath tp : paths) {
					for (Object pathPart : tp.getPath()) {
						DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
						if (parentNode.getUserObject() instanceof UserObjectforTreeView) {
							// System.out.println("outside: can be done");
							// System.out.println(((UserObjectforTreeView)
							// parentNode.getUserObject()).isAxiom());
						} // else
							// System.out.println("outside: not posible");

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
