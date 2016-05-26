package edu.wsu.dase;

//import org.checkerframework.checker.nullness.qual.NonNull;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.wsu.dase.DefaultIRIResolver;

import java.util.Optional;

public class ProtegeIRIResolver extends DefaultIRIResolver {
	private static final Logger log = LoggerFactory.getLogger(ProtegeIRIResolver.class);

	
	private final OWLEntityFinder entityFinder;
	
	private final OWLModelManagerEntityRenderer entityRender;

	public ProtegeIRIResolver( OWLEntityFinder owlEntityFinder,
			 OWLModelManagerEntityRenderer entityRender) {
		super();
		this.entityFinder = owlEntityFinder;
		this.entityRender = entityRender;
	}

	@Override
	
	public Optional< String> iri2PrefixedName( IRI iri) {
		Optional< String> prefixedName = super.iri2PrefixedName(iri);

		if (prefixedName.isPresent()) {
			// log.warn("iri " + iri + ", prefixed name " + prefixedName);
			return Optional.of(prefixedName.get());
		} else {
			// log.warn("iri " + iri + ", prefixed name " +
			// this.entityRender.render(iri));
			return Optional.of(this.entityRender.render(iri));
		}
	}

	@Override
	
	public Optional< String> iri2ShortForm( IRI iri) {
		// log.warn("iri " + iri + ", short form " +
		// this.entityRender.render(iri));
		return Optional.of(this.entityRender.render(iri));
	}

	@SuppressWarnings("unused")
	@Override
	public Optional< IRI> prefixedName2IRI( String prefixedName) {
		OWLEntity owlEntity = this.entityFinder.getOWLEntity(prefixedName);

		if (owlEntity != null) {
			System.out.println("Entity IRI" + owlEntity.getIRI());
			return Optional.of(owlEntity.getIRI());
		} else {
			
			System.out.println("Entity IRI" + super.prefixedName2IRI(prefixedName));
			return super.prefixedName2IRI(prefixedName);
		}
	}
}
