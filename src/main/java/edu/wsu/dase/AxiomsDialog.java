package edu.wsu.dase;

import java.awt.BorderLayout;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

public class AxiomsDialog extends JFrame {

	private static final long serialVersionUID = 4648172894076113183L;

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
		setSize(500, 500);
		setTitle("TreeWithCheckBox");
		this.getContentPane().setLayout(new BorderLayout());

		DefaultMutableTreeNode root = getRoot();

		final JCheckBoxTree cbt = new JCheckBoxTree(root);

		DefaultMutableTreeNode inferredroot = (DefaultMutableTreeNode) cbt.getModel().getRoot();
		Enumeration e = inferredroot.breadthFirstEnumeration();


		// this.getContentPane().add(cbt,BorderLayout.NORTH);
		this.getContentPane().add(cbt, BorderLayout.CENTER);

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
		 this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
