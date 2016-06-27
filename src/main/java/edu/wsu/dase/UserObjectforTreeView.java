package edu.wsu.dase;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.QNameShortFormProvider;

public class UserObjectforTreeView {

	private boolean isAxiom;
	private OWLAxiom axiom;
	private String lblVal;
	static OWLOntology activeOntology;
	Component parent;

	public String getLblVal() {
		return lblVal;
	}

	public void setLblVal(String lblVal) {
		this.lblVal = lblVal;
	}

	static ManchesterOWLSyntaxOWLObjectRendererImpl rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

	public UserObjectforTreeView(Component parent, OWLOntology activeO) {
		activeOntology = activeO;
		this.parent = parent;
	}

	public UserObjectforTreeView(boolean isAxiom, String lblVal) {

		ManchesterOWLSyntaxPrefixNameShortFormProvider shortFormProvider = new ManchesterOWLSyntaxPrefixNameShortFormProvider(
				activeOntology);
		// QNameShortFormProvider shortFormProvider = new
		// QNameShortFormProvider(activeOntology);
		rendering.setShortFormProvider(shortFormProvider);
//		for (Map.Entry<String, String> forms : shortFormProvider.getPrefixName2PrefixMap().entrySet()) {
//			//JOptionPane.showMessageDialog(parent, "sarker.3 key: " + forms.getKey() + " value: " + forms.getValue());
//			System.err.println("sarker.3 key: " + forms.getKey() + " value: " + forms.getValue());
//		}

		if (!isAxiom) {
			this.isAxiom = false;
			this.lblVal = lblVal;
		}
	}

	public UserObjectforTreeView(boolean isAxiom, OWLAxiom axiom) {
		if (isAxiom) {
			this.isAxiom = true;
			this.axiom = axiom;
		}
	}

	@Override
	public String toString() {

		String value = "";

		if (this.isAxiom) {
			value = rendering.render(this.axiom);
		} else {
			value = lblVal.toString();
			value = "<html><b style=\"color:#624FDB;\">" + value + "</b></html>";
		}

		return value;
	}

	public boolean isAxiom() {
		return isAxiom;
	}

	public void setIsAxiom(boolean isAxiom) {
		this.isAxiom = isAxiom;
	}

	public OWLAxiom getAxiom() {
		return axiom;
	}

	public void setAxiom(OWLAxiom axiom) {
		this.axiom = axiom;
	}

}
