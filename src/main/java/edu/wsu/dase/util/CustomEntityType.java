package edu.wsu.dase.util;

import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.OWL_ANNOTATION_PROPERTY;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.OWL_CLASS;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.OWL_DATA_PROPERTY;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.OWL_NAMED_INDIVIDUAL;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.OWL_OBJECT_PROPERTY;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.RDFS_DATATYPE;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.RDFS_LITERAL;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.RDFS_SUBCLASS_OF;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.RDF_TYPE;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.HasPrefixedName;
import org.semanticweb.owlapi.model.HasShortForm;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * Represents the different types of OWL 2 Entities.
 * 
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0
 * @param <E>
 *        entity type
 */
@SuppressWarnings("unused")
public final class CustomEntityType<E extends OWLEntity> implements Serializable,
        HasShortForm, HasPrefixedName, HasIRI {

    private static final long serialVersionUID = 40000L;
    //@formatter:off
    /** class entity */                 public static final CustomEntityType<OWLClass> CLASS = new CustomEntityType<OWLClass>( "Class");
    /** object property entity */       public static final CustomEntityType<OWLObjectProperty> OBJECT_PROPERTY = new CustomEntityType<OWLObjectProperty>( "Object Property");
    /** data property entity */         public static final CustomEntityType<OWLDataProperty> DATA_PROPERTY = new CustomEntityType<OWLDataProperty>( "Data Property");
    /** annotation property entity*/    public static final CustomEntityType<OWLAnnotationProperty> ANNOTATION_PROPERTY = new CustomEntityType<OWLAnnotationProperty>( "Annotation Property");
    /** named individual entity */      public static final CustomEntityType<OWLNamedIndividual> NAMED_INDIVIDUAL = new CustomEntityType<OWLNamedIndividual>("Named Individual");
    /** datatype entity */              public static final CustomEntityType<OWLDatatype> DATATYPE = new CustomEntityType<OWLDatatype>( "Datatype");
    /** RDF_TYPE */         		    public static final CustomEntityType<OWLDatatype> LITERAL = new CustomEntityType<OWLDatatype>( "Literal");
    /** LITERAL */          		    public static final CustomEntityType<OWLDatatype> RDFTYPE = new CustomEntityType<OWLDatatype>( "rdf:type");
    /** RDFS_SUBCLASS_OF */             public static final CustomEntityType<OWLDatatype> RDFSSUBCLASS_OF = new CustomEntityType<OWLDatatype>( "rdfs:subClassOf");
  //  private static final List<CustomEntityType<?>> VALUES = Collections.<CustomEntityType<?>> unmodifiableList(Arrays.asList(CLASS, OBJECT_PROPERTY, DATA_PROPERTY, ANNOTATION_PROPERTY, NAMED_INDIVIDUAL, DATATYPE));
  //@formatter:on
    
    private  String name;
    
    public CustomEntityType(){
    	this(null);
    }

    public CustomEntityType(String name) {
    	if(name == null){
    		this.name = "";
    	}else
    		this.name = name;
    }

    /** @return toe vocabulary enum corresponding to this entity */
    /*public OWLRDFVocabulary getVocabulary() {
        return vocabulary;
    }*/

    /** @return this entity tipe name */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }


   

    @Override
    public String getShortForm() {
        return name;
    }

	@Override
	public IRI getIRI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPrefixedName() {
		// TODO Auto-generated method stub
		return null;
	}

   
}

