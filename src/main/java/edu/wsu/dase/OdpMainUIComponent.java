package edu.wsu.dase;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//import org.checkerframework.checker.nullness.qual.NonNull;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import edu.wsu.dase.swing.GraphEditor;

import edu.wsu.dase.swing.HelloWorld;
import edu.wsu.dase.swing.editor.EditorMenuBar;

public class OdpMainUIComponent extends AbstractOWLClassViewComponent {

	private static final long serialVersionUID = 1L;
	private OWLModelManager protegeOWLModelManager;
	GraphEditor editor;
	private final ODPTabListener listener = new ODPTabListener();

	@Override
	public void initialiseClassView() throws Exception {
		setLayout(new BorderLayout());
		editor = new GraphEditor();
		
		add(new EditorMenuBar(editor),BorderLayout.EAST);
		add(editor, BorderLayout.CENTER);

		Dimension d = new Dimension(800, 600);
		setPreferredSize(d);
		setSize(d);
		setLocation(100, 50);
		setVisible(true);

		if (getOWLModelManager() != null) {
			getOWLModelManager().addListener(listener);
		}

		update();

	}

	private void update() {

		protegeOWLModelManager = getOWLModelManager();

		if (protegeOWLModelManager != null) {
			editor.setProtegeOWLModelManager(protegeOWLModelManager);
		}

	}

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disposeView() {
		// TODO Auto-generated method stub
		   super.dispose();
		    getOWLModelManager().removeListener(this.listener);

	}

	private class ODPTabListener implements OWLModelManagerListener {
		@Override
		public void handleChange( OWLModelManagerChangeEvent event) {

			if (event.getType() == EventType.ACTIVE_ONTOLOGY_CHANGED) {
				update();
			}
		}
	}

	// private static final Logger log = LoggerFactory.getLogger(SWRLTab.class);

}
