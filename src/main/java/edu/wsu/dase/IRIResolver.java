package edu.wsu.dase;

//import org.checkerframework.checker.nullness.qual.NonNull;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Optional;

/**
 * For simplicity, SWRL rule engine implementations will typically use the prefixed names of OWL entities to name
 * their representation of those objects. This interface provides resolving services for IRI to prefix name mapping
 * and vice versa.
 *
 * @see org.swrlapi.core.SWRLAPIOWLOntology
 */
public interface IRIResolver
{
  void reset();

  /**
   * @param prefixedName A prefixed name
   * @return The IRI resolved from the prefixed name
   */
   Optional< IRI> prefixedName2IRI( String prefixedName);

  /**
   * @param iri An IRI
   * @return The prefixed form of the IRI
   */
   Optional< String> iri2PrefixedName( IRI iri);


  /**
   * @param iri An OWL entity IRI
   * @return The short form of the IRI
   */
   Optional< String> iri2ShortForm( IRI iri);

  /**
   *
   * @param ontology The ontology from which to extract prefixes
   */
  void updatePrefixes( OWLOntology ontology);

  /**
   *
   * @param prefix A prefix
   * @param namespace A namespace
   */
  void setPrefix( String prefix,  String namespace);
}
