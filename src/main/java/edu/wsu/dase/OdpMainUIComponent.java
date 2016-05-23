package edu.wsu.dase;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import edu.wsu.dase.swing.GraphEditor;

import edu.wsu.dase.swing.HelloWorld;

public class OdpMainUIComponent extends AbstractOWLClassViewComponent {

	private static final long serialVersionUID = 1L;

	@Override
	public void initialiseClassView() throws Exception {
		// TODO Auto-generated method stub
		HelloWorld frame = new HelloWorld();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 320);
		frame.setVisible(true);

		add(frame);
		setToolTipText("SWRLTab");
		setLayout(new BorderLayout());
		
		GraphEditor editor = new GraphEditor();
		add(editor);
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
