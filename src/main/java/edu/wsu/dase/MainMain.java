package edu.wsu.dase;

import java.awt.BorderLayout;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

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

public class MainMain extends JFrame {

	private static final long serialVersionUID = 4648172894076113183L;

	private class userObject {
		String name;
		int no;
		boolean bool;

		public userObject(String name) {
			this.name = name;
			this.no = 10;
			this.bool = true;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getNo() {
			return no;
		}

		public void setNo(int no) {
			this.no = no;
		}

		public boolean isBool() {
			return bool;
		}

		public void setBool(boolean bool) {
			this.bool = bool;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return name.toString();
		}

	}

	public DefaultMutableTreeNode getRoot() {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new userObject("Music"));
		DefaultMutableTreeNode category;
		DefaultMutableTreeNode composer;
		DefaultMutableTreeNode style;
		DefaultMutableTreeNode album;
		// Classical
		category = new DefaultMutableTreeNode(new userObject("Classical"));

		// Beethoven
		category.add(composer = new DefaultMutableTreeNode("Beethoven"));

		composer.add(style = new DefaultMutableTreeNode("Concertos"));
		composer.add(style = new DefaultMutableTreeNode("Quartets"));

		style.add(new DefaultMutableTreeNode("No. 1 - C Major"));
		style.add(new DefaultMutableTreeNode("Six String Quartets"));

		root.add(category);

		return root;
	}

	public MainMain() {
		super();
		setSize(500, 500);
		setTitle("TreeWithCheckBox");
		this.getContentPane().setLayout(new BorderLayout());

		final JCheckBoxTree cbt = new JCheckBoxTree(getRoot());

		// this.getContentPane().add(cbt,BorderLayout.NORTH);
		this.getContentPane().add(cbt, BorderLayout.CENTER);

		cbt.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {

			public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
				System.out.println("event");
				TreePath[] paths = cbt.getCheckedPaths();
				for (TreePath tp : paths) {
					for (Object pathPart : tp.getPath()) {
						System.out.print(pathPart + ",\t" + tp.getLastPathComponent());
					}
					System.out.println();
				}
			}
		});
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@SuppressWarnings("deprecation")
	public static void main(String args[]) {
		 MainMain m = new MainMain();
		 m.setVisible(true);

		File file;
		
		OWLOntologyManager manager;
		OWLOntology localOntology = null;
		OWLDataFactory factory;

		try {

			// loading the ontology
			manager = OWLManager.createOWLOntologyManager();

			try {
				file = new File(MainMain.class.getResource("/resources/pizza.owl").getPath());
				System.out.println("path: "+file.getAbsolutePath());
				localOntology = manager.loadOntologyFromOntologyDocument(file);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			factory = localOntology.getOWLOntologyManager().getOWLDataFactory();

//
//			OWLDocumentFormat format = manager.getOntologyFormat(localOntology);
//			System.out.println("    format: " + format);
//
//			ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
//			if (format.isPrefixOWLOntologyFormat()) {
//				manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
//			}
//
//			manager.setOntologyFormat(localOntology, manSyntaxFormat);
//			manager.saveOntology(localOntology, manSyntaxFormat);
//			System.out.println("Manchester syntax: --- saved in Manchester.owl");

			ManchesterOWLSyntaxOWLObjectRendererImpl rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

			//OWLClass c1 = factory.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream"));

			for (OWLAxiom ax : localOntology.getAxioms()) {
				System.out.println("Real Axiom: " + ax.toString());
				System.out.println("Equivalent manchester Syntax: " + rendering.render(ax)+"\n");
			}

			// Set<OWLClassExpression> c1eqclasses =
			// c1.getEquivalentClasses(localOntology);
			// for(OWLClassExpression c1e : c1eqclasses)
			// System.out.println("Equivalent: "+rendering.render(c1e));
			//
			// c1eqclasses = c1.getDisjointClasses(localOntology);
			// for(OWLClassExpression c1e : c1eqclasses)
			// System.out.println("Disjoint: "+rendering.render(c1e));
			//
			// c1eqclasses = c1.getSubClasses(localOntology);
			// for(OWLClassExpression c1e : c1eqclasses)
			// System.out.println("Subclass: "+rendering.render(c1e));
			//
			// c1eqclasses = c1.getSuperClasses(localOntology);
			// for(OWLClassExpression c1e : c1eqclasses)
			// System.out.println("Superclass: "+rendering.render(c1e));

		} catch (Exception e) {
			System.out.println("Could not save ontology: " + e.getMessage());
		}
	}
}
