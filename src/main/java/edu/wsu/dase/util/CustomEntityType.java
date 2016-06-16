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
public class CustomEntityType  {

    private static final long serialVersionUID = 40000L;
    //@formatter:off
    /** class entity */                 public static final CustomEntityType CLASS = new CustomEntityType( "Class");
    /** object property entity */       public static final CustomEntityType OBJECT_PROPERTY = new CustomEntityType( "Object Property");
    /** data property entity */         public static final CustomEntityType DATA_PROPERTY = new CustomEntityType( "Data Property");
    /** annotation property entity*/    public static final CustomEntityType ANNOTATION_PROPERTY = new CustomEntityType( "Annotation Property");
    /** named individual entity */      public static final CustomEntityType NAMED_INDIVIDUAL = new CustomEntityType("Named Individual");
    /** datatype entity */              public static final CustomEntityType DATATYPE = new CustomEntityType( "Datatype");
    /** RDF_TYPE */         		    public static final CustomEntityType LITERAL = new CustomEntityType( "Literal");
    /** LITERAL */          		    public static final CustomEntityType RDFTYPE = new CustomEntityType( "rdf:type");
    /** RDFS_SUBCLASS_OF */             public static final CustomEntityType RDFSSUBCLASS_OF = new CustomEntityType( "rdfs:subClassOf");
  //  private static final List<CustomEntityType<?>> VALUES = Collections.<CustomEntityType<?>> unmodifiableList(Arrays.asList(CLASS, OBJECT_PROPERTY, DATA_PROPERTY, ANNOTATION_PROPERTY, NAMED_INDIVIDUAL, DATATYPE));
  //@formatter:on
    
    private  String Name;
    
    public CustomEntityType(){
    	//this(null);
    }

    public CustomEntityType(String name) {
    	
    		this.Name = name;
    }

    /** @return toe vocabulary enum corresponding to this entity */
    /*public OWLRDFVocabulary getVocabulary() {
        return vocabulary;
    }*/

    /** @return this entity tipe name */
    public String getName() {
        return Name;
    }
    
    /** @return this entity tipe name */
    public void setName(String Name) {
        this.Name = Name;
    }
    
    @Override
    public String toString(){
    	return Name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
    	 if (this == obj)
             return true;
         if (getClass() != obj.getClass())
             return false;
         if(this.toString() == obj.toString())
        	 return true;
         return false;
    }

   public static void main(String[] args){
	   CustomEntityType clas1 = new CustomEntityType("a ");
	   CustomEntityType clas2 = new CustomEntityType("a");
	   CustomEntityType clas3 = new CustomEntityType("a");
	   String s1 = new String( "s");
	   String s2 = new String("s");
	   
	   
	   if(clas1.equals(clas2)){
		   System.out.println("equal---------");
	   }
	   else{
		   System.out.println(clas1.toString() +"\n"+ clas2.toString()+ " not equal");
	   }
   }
}

