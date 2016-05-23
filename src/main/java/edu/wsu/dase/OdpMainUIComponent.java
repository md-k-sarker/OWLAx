package edu.wsu.dase;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import edu.wsu.dase.swing.GraphEditor;

import edu.wsu.dase.swing.HelloWorld;

public class OdpMainUIComponent extends AbstractOWLClassViewComponent {

	private static final long serialVersionUID = 1L;

	@Override
	public void initialiseClassView() throws Exception {
		setLayout(new BorderLayout());
		GraphEditor editor = new GraphEditor();
		add(editor, BorderLayout.CENTER);

		Dimension d = new Dimension(800, 600);
		setPreferredSize(d);
		setSize(d);
		setLocation(100, 50);
		setVisible(true);
	}

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disposeView() {
		// TODO Auto-generated method stub

	}

	// private static final Logger log = LoggerFactory.getLogger(SWRLTab.class);

}
