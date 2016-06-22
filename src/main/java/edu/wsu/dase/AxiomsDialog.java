package edu.wsu.dase;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
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
	private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.5;
	OWLOntology activeOntology;
	private DefaultMutableTreeNode existingAxiomsRoot;
	private DefaultMutableTreeNode newAxiomsRoot;

	public void initUI() {

		setSize(500, 500);
		setTitle("Select Axioms");
		this.getContentPane().setLayout(new BorderLayout());

		// main panel
		mainPnl = new JPanel();
		mainPnl.setLayout(new BorderLayout());

		// bottom Panel
		bottomPnl = new JPanel();
		bottomPnl.setLayout(new BorderLayout());

		integrateBtn = new Button(integrateBtnText);
		integrateBtn.setSize(new Dimension(40, 30));
		integrateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// now integrate with protege

				dispose();
			}
		});
		bottomPnl.add(integrateBtn, BorderLayout.CENTER);

		cancelBtn = new Button(cancelBtnText);
		cancelBtn.setSize(new Dimension(40, 30));
		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		bottomPnl.add(cancelBtn, BorderLayout.EAST);

		// splitPane
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setResizeWeight(SPLIT_PANE_RESIZE_WEIGHT);

		splitPane.setTopComponent(getNewAxiomsPnl());
		splitPane.setBottomComponent(getExistingAxiomsPnl());
		mainPnl.add(splitPane);

		this.add(mainPnl, BorderLayout.CENTER);
		this.add(bottomPnl, BorderLayout.SOUTH);

	}

	private JPanel getExistingAxiomsPnl() {
		existingAxiomsPnl = new JPanel();
		existingAxiomsPnl.setLayout(new BorderLayout());

		JLabel lblExistingAxioms = new JLabel(existingAxiomsLblText);
		existingAxiomsPnl.add(lblExistingAxioms, BorderLayout.NORTH);

		final JCheckBoxTree cbt = new JCheckBoxTree(getExistingAxiomsRoot());
		existingAxiomsPnl.add(cbt, BorderLayout.CENTER);

		return existingAxiomsPnl;
	}

	private JPanel getNewAxiomsPnl() {
		newAxiomsPnl = new JPanel();
		newAxiomsPnl.setLayout(new BorderLayout());

		JLabel lblNewAxioms = new JLabel(newAxiomsLblText);
		newAxiomsPnl.add(lblNewAxioms, BorderLayout.NORTH);

		final JCheckBoxTree cbt = new JCheckBoxTree(getNewAxiomsRoot());
		newAxiomsPnl.add(cbt, BorderLayout.CENTER);

		return newAxiomsPnl;
	}

	public DefaultMutableTreeNode getExistingAxiomsRoot() {

		existingAxiomsRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, "Select All"));
		DefaultMutableTreeNode childNode;

		if (activeOntology != null) {

			for (OWLAxiom axiom : activeOntology.getAxioms()) {
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

		newAxiomsRoot.add(subRoot);

		// SubClassOf Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, subClassOfAxiomTypeText));

		newAxiomsRoot.add(subRoot);

		// Domain and RangeAxiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, domainandRangeAxiomTypeText));

		newAxiomsRoot.add(subRoot);

		// Existential Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, existentialAxiomTypeText));

		newAxiomsRoot.add(subRoot);

		// Cardinality Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, cardinalityAxiomTypeText));

		newAxiomsRoot.add(subRoot);

		// Class Assertion Axiom
		subRoot = new DefaultMutableTreeNode(new UserObjectforTreeView(false, classAssertionAxiomTypeText));

		newAxiomsRoot.add(subRoot);

		return newAxiomsRoot;
	}

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

	public AxiomsDialog() {
		super();

		this.activeOntology = activeOntology;

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

	public static void main(String args[]) {
		AxiomsDialog m = new AxiomsDialog();
		m.setVisible(true);

		// File file;
		//
		// OWLOntologyManager manager;
		// OWLOntology localOntology = null;
		// OWLDataFactory factory;
		//
		// try {
		//
		// // loading the ontology
		// manager = OWLManager.createOWLOntologyManager();
		//
		// try {
		// file = new
		// File(AxiomsDialog.class.getResource("/resources/pizza.owl").getPath());
		// System.out.println("path: "+file.getAbsolutePath());
		// localOntology = manager.loadOntologyFromOntologyDocument(file);
		// } catch (OWLOntologyCreationException e) {
		// e.printStackTrace();
		// }catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		//
		// factory = localOntology.getOWLOntologyManager().getOWLDataFactory();
		//
		// ManchesterOWLSyntaxOWLObjectRendererImpl rendering = new
		// ManchesterOWLSyntaxOWLObjectRendererImpl();
		//
		//
		// for (OWLAxiom ax : localOntology.getAxioms()) {
		// System.out.println("Real Axiom: " + ax.toString());
		// System.out.println("Equivalent manchester Syntax: " +
		// rendering.render(ax)+"\n");
		// }
		//
		//
		// } catch (Exception e) {
		// System.out.println("Could not save ontology: " + e.getMessage());
		// }
	}
}
