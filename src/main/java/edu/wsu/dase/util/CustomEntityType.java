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
    /** class entity */             @Nonnull    public static final CustomEntityType<OWLClass> CLASS = new CustomEntityType<OWLClass>( "Class", "Class", "Classes", OWL_CLASS);
    /** object property entity */   @Nonnull    public static final CustomEntityType<OWLObjectProperty> OBJECT_PROPERTY = new CustomEntityType<OWLObjectProperty>( "Object Property", "Object property", "Object properties", OWL_OBJECT_PROPERTY);
    /** data property entity */     @Nonnull    public static final CustomEntityType<OWLDataProperty> DATA_PROPERTY = new CustomEntityType<OWLDataProperty>( "Data Property", "Data property", "Data properties", OWL_DATA_PROPERTY);
    /** annotation property entity*/@Nonnull    public static final CustomEntityType<OWLAnnotationProperty> ANNOTATION_PROPERTY = new CustomEntityType<OWLAnnotationProperty>( "Annotation Property", "Annotation property", "Annotation properties", OWL_ANNOTATION_PROPERTY);
    /** named individual entity */  @Nonnull    public static final CustomEntityType<OWLNamedIndividual> NAMED_INDIVIDUAL = new CustomEntityType<OWLNamedIndividual>("Named Individual", "Named individual", "Named individuals", OWL_NAMED_INDIVIDUAL);
    /** datatype entity */          @Nonnull    public static final CustomEntityType<OWLDatatype> DATATYPE = new CustomEntityType<OWLDatatype>( "Datatype", "Datatype", "Datatypes", RDFS_DATATYPE);
    /** RDF_TYPE */         		@Nonnull    public static final CustomEntityType<OWLDatatype> LITERAL = new CustomEntityType<OWLDatatype>( "Literal", "Literal", "Literals", RDFS_LITERAL);
    /** LITERAL */          		@Nonnull    public static final CustomEntityType<OWLDatatype> RDFTYPE = new CustomEntityType<OWLDatatype>( "rdf:Type", "rdf:Type", "rdf:Types", RDF_TYPE);
    /** RDFS_SUBCLASS_OF */         @Nonnull    public static final CustomEntityType<OWLDatatype> RDFSSUBCLASS_OF = new CustomEntityType<OWLDatatype>( "rdfs:subClassOf", "rdfs:subClassOf", "rdfs:subClassesOf", RDFS_SUBCLASS_OF);
    private static final List<CustomEntityType<?>> VALUES = Collections.<CustomEntityType<?>> unmodifiableList(Arrays.asList(CLASS, OBJECT_PROPERTY, DATA_PROPERTY, ANNOTATION_PROPERTY, NAMED_INDIVIDUAL, DATATYPE));
  //@formatter:on
    @Nonnull
    private final String name;
    @Nonnull
    private final OWLRDFVocabulary vocabulary;
    @Nonnull
    private final String printName;
    @Nonnull
    private final String pluralPrintName;

    private CustomEntityType(@Nonnull String name, @Nonnull String print,
            @Nonnull String pluralPrint, @Nonnull OWLRDFVocabulary vocabulary) {
        this.name = name;
        this.vocabulary = vocabulary;
        printName = print;
        pluralPrintName = pluralPrint;
    }

    /** @return toe vocabulary enum corresponding to this entity */
    public OWLRDFVocabulary getVocabulary() {
        return vocabulary;
    }

    /** @return this entity tipe name */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /** @return the list of known entity types */
    public static List<CustomEntityType<?>> values() {
        return VALUES;
    }

    /** @return printable name */
    @Nonnull
    public String getPrintName() {
        return printName;
    }

    /** @return plural printable name */
    @Nonnull
    public String getPluralPrintName() {
        return pluralPrintName;
    }

    @Override
    public String getShortForm() {
        return name;
    }

    @Override
    public String getPrefixedName() {
        return vocabulary.getPrefixedName();
    }

    @Override
    public IRI getIRI() {
        return vocabulary.getIRI();
    }
}

