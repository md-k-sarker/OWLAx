package edu.wsu.dase;

import java.util.HashMap;
import java.util.Map;

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
	


	public String getLblVal() {
		return lblVal;
	}

	public void setLblVal(String lblVal) {
		this.lblVal = lblVal;
	}

	static ManchesterOWLSyntaxOWLObjectRendererImpl rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	
	public UserObjectforTreeView(OWLOntology activeO){
		activeOntology = activeO;
	}

	public UserObjectforTreeView(boolean isAxiom, String lblVal) {
		Map<String,String> prefix2Namespace = new HashMap<String,String>();
		prefix2Namespace.put("pref1", "http://www.semanticweb.org/onto#");
		prefix2Namespace.put("pref2", "http://www.semanticweb.org/onto2#");
		ManchesterOWLSyntaxPrefixNameShortFormProvider provider = new ManchesterOWLSyntaxPrefixNameShortFormProvider(activeOntology);
		//QNameShortFormProvider shortFormProvider = new QNameShortFormProvider(activeOntology);
		rendering.setShortFormProvider(provider);
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
