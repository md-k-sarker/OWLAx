package edu.wsu.dase;

import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.OWLAxiom;

public class UserObjectforTreeView {

	private boolean isAxiom;
	private OWLAxiom axiom;
	private String lblVal;

	public String getLblVal() {
		return lblVal;
	}

	public void setLblVal(String lblVal) {
		this.lblVal = lblVal;
	}

	static ManchesterOWLSyntaxOWLObjectRendererImpl rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

	public UserObjectforTreeView(boolean isAxiom, String lblVal) {
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
			value = "<html><b style=\"color:#624FDB;\">"+value+"</b></html>";
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
