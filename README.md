# OWLAx
OWL Axiomatizer

For details please visit: http://dase.cs.wright.edu/content/ontology-axiomatization-support

Ontology Design Pattern Plugin for Desktop Protege 5.0+


###Installation
<ol>
<li>Click <b>Check for plugins</b> from <b>File</b> Menu </li>
	</br>
		<img src="https://github.com/md-k-sarker/ROWL/blob/master/plugin/doc/screenshot/ProtegeFileMenu.png"></img> 
	
	</br>
	</br>
<p>You will a see list of plugin. </p>

</br>
<img src="https://github.com/md-k-sarker/ROWL/blob/master/plugin/doc/screenshot/ProtegePluginsList.png"></img>
</br>
<li>Select OWLAx: OWL Axiomatizer and Click <b>Install</b></li>
</br>

		<img src="https://github.com/md-k-sarker/ROWL/blob/master/plugin/doc/screenshot/InstallOWLAx.png"></img>
		
<br>

</ol>


##Usage
1. Start Protege
2. Select OWLAx Tab from
	 Window -> Tabs -> OWLAx
	 
	 ![Alt Click on OWLAx to Select](https://github.com/md-k-sarker/OWLAx/blob/master/plugin/docs/ScreenShots/SelectOWLAxTab.png)
	 
3. Start Using OWLAx Plugin

###How to Use
See the video <a href="https://github.com/md-k-sarker/OWLAx/blob/master/plugin/docs/Video/howToUseOWLAx.mov?raw=true" title="How to use OWLAx"> Using OWLAx</a>


##Capabilities of OWLAx
<ol>
<li> Gives user a graphical approach(rather than using whiteboard or flipcharts) to first design a conceptual overview of ontology modules in the form of class diagram. 
<br>
<li> While creating class diagrams user can save and open the diagram as png file.
<li> It give options to specify below mentioned triples as graphical user interface--
<ul>	<li>class(A)-----------objectProperty(P)----------class(B)
	<li>class(A)-----------objectProperty(P)----------individual(B)
	<li>class(A)-----------dataProperty(P)------------literal(B)
	<li>class(A)-----------dataProperty(P)------------datatype(B)
	<li>class(A)-----------rdfs:subclassof------------class(B)
	<li>individual(A)------rdf:type-------------------class(B)
	</ul>
<li> It generates following type of axioms from the graph(diagram).
<ul>
	<li>  Scoped Domain and Range
	<li>  Existential 
	<li>  Cardinality
	<li>  Disjointof
	<li>  subClassof
	<li>  Class Assertion
	</ul>
<li> After Creating Axioms it shows the candidate axioms and existing axioms(if any) of the active ontology to the user.
<li> User can choose which axioms he want to generate. 
<li> After selecting the axioms only selected axioms will be generated and be integrated with protege. 
</ol>

Other features:
* It supports custom data type
* It supports specifying prefix.  
 	Steps : 
<ol><li>First define a prefix in protege
 <li>Then write entity name as prefixName : entityName 
 </ol>

     
##Current Limitations:
1. It can't create complex axioms.
2. It can't create axioms from reflexivity, transitional relation etc.
3. It doesn't support custom cardinality. Currently it creates maxCardinality 1.

###Acknowledgement
This work was supported by the National Science Foundation under award 1017225 III: Small: TROn – Tractable Reasoning with Ontologies.


