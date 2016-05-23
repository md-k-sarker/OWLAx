package edu.wsu.dase;

import java.awt.BorderLayout;

import javax.swing.JButton;

import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;

import edu.wsu.dase.swing.GraphEditor;

public class OdpMainUIClass extends OWLWorkspaceViewsTab{


	@Override
	public void initialise() {
		// TODO Auto-generated method stub
		super.initialise();
		setToolTipText("SWRLTab");

	      setLayout(new BorderLayout());
	      GraphEditor editor = new GraphEditor();
	    //  add(new JButton("Ali"));
	      add(editor);

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
	}

	private static final long serialVersionUID = 1L;

}
