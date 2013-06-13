The RDFindex
========

This project defines:

* A high-level vocabulary on top of the RDF Data Cube Vocabulary to specify indexes such as "The Webindex", "The CSMIC", "The Shanghai Ranking" or "The H-index"
* A computation process vocabulary to aggregate the different entities and data of an index
* A process of the vocabulary and computation process


## Definitions

* Index: this is the class of indexes that aggregates pieces of data in a quantitative value.
*  An index is comprised of n dimensions and 1 measure that is generated aggregating n dimensions coming from components 
with a specific OWA (Ordered weighted averaging aggregation operator).
* Component: a particular abstraction of an index that aggregates indicators.
* Indicator: a bag of observations defined by (n dimensions, 1 measure, n attributes).
* Slice: a view of an indicator. More information in the RDF Data Cube Vocabulary.
* Observation: a measure belonging to a slice of an indicator.  More information in the RDF Data Cube Vocabulary.
* Dimension: the concepts we are measuring e.g. area, time, sex, age, etc.  More information in the RDF Data Cube Vocabulary.
* Measure:  the value that is presented in the observation. More information in the RDF Data Cube Vocabulary.
* Attribute:  More information in the RDF Data Cube Vocabulary.

### Existing problems
Existing indexes are completely closed:

* How can I know which components/indicators have been used and their weight?
* How data has been gathered? Data providers?
* What kind of indicators has been used? qualitative or quantitative?
* Have the observation values been normalized or estimated?
* How data has been aggregated?
* Can I calculate the same index?
* Can I consume the index, indicators, etc. values from my application?
* Which is the cost of adding a new indicator?
* Multilingual explanations
* ...

Currently, this information can be only obtained reading the typical PDF report but a lot of valuable data and information is completely missing and you 
must do an act of faith. That is why applying semantics and linked data the reuse of this information can be boosted.

For instance, the WorldBank indicators or the Webindex observations are currently available as Open Data and Linked Open Data, nevertheless the information 
about the computation process and a more fine-grained information (in terms of concepts not strings) about the indicators, etc. 
is still missing.



### Motivating Example

Imagine we want to define a dummy index called "The Longest Life Country". This index comprises two components (with their indicators):
* Health
 * Life Expectancy 
* Public Spending
 * Health Public Spending
 

This dummy index (the full example is at apps/rdfindex-core/src/test/resources) must be calculated using an average between the life expectancy (withouth taking into account the sex) and the public spending. Furthermore 
we have the next observations (values are not real):

* Life expectancy

| Year  | Region        | Life Expectancy Male  | Life Expectancy Female |
| ------|-------------- | ---------------------:|-----------------------:|
| 2005  |Spain          | 78|80|
| 2006  |Spain          | 78|81|
| 2005  |Turkey          | 76|79|
| 2006  |Turkey         | 76|80|



* Health Public Spending

| Year  | Region        | Value  | 
| ------|-------------- | ---------------------:|
| 2005  |Spain          | 2000 |
| 2006  |Spain          | 3000 |
| 2005  |Turkey         | 1000 |
| 2006  |Turkey         |  1500 |


Thus we have to follow the next steps:
* Dimensions
 * Area or country
 * Date
 * Sex
* Measures
 * A double value
* Aggregate the indicator "Life Expectancy" using the AVG of ages to generate "Aggregated Life Expectancy".
* Aggregate the indicators in each component withouth any OWA due to it is not necessary (it is just one indicator)
* Aggregate the components in the index using the AVG operator. For instance, in Spain 2005 we would have:
 * Aggregated Life Expectancy = 79
 * Health = 79
 * Public Spending = 2000
 * The Longest Life Country = (79 * 2000) / 2


## Specification

* Description of the "Life Expectancy" indicator

```
rdfindex:LifeExpectancy a rdfindex:Indicator;
	rdfs:label 	"Life expectancy"@en;
	rdfs:comment 	"Number of years that a person lives since his birth in a certain region and period."@en;
	rdfindex:type 	rdfindex:Quantitative;	
	qb:structure 	rdfindex:LifeExpectancyDSD ;  
	sdmx-attribute:unitMeasure dbpedia-res:Year ;
.
```

* Description of the "Life Expectancy" indicator's structure

```
rdfindex:LifeExpectancyDSD a qb:DataStructureDefinition;
    qb:component    [qb:dimension rdfindex:ref-area],
        [qb:dimension rdfindex:ref-year],
        [qb:dimension sdmx-dimension:sex],
        [qb:measure   rdfindex:value],
        [qb:attribute sdmx-attribute:unitMeasure];
.
```

* We are going to define our aggregated indicator that removes the sex dimension in its structure:

```
rdfindex:AggregatedLifeExpectancy a rdfindex:Indicator;
	rdfs:label 	"Aggregated Life expectancy"@en;
	rdfindex:type 	rdfindex:Quantitative;
	rdfindex:weight  1;
	rdfindex:aggregates [ 
		rdfindex:aggregation-operator rdfindex:Mean;
		rdfindex:part-of rdfindex:LifeExpectancy;  
	];
	qb:structure 	rdfindex:AggregatedLifeExpectancyDSD ;  
.
```

```
rdfindex:AggregatedLifeExpectancyDSD a qb:DataStructureDefinition;
    qb:component    [qb:dimension rdfindex:ref-area],
        [qb:dimension rdfindex:ref-year],
        [qb:measure   rdfindex:value],
        [qb:attribute sdmx-attribute:unitMeasure];
.

```


* Description of the "Health" component:

```
rdfindex:Health a rdfindex:Component;
	rdfs:label 	"Health Value"@en;
	rdfindex:type 	rdfindex:Quantitative;
	rdfindex:weight  1;
	rdfindex:aggregates [ 
		rdfindex:aggregation-operator rdfindex:Min;
		rdfindex:part-of rdfindex:AggregatedLifeExpectancy;
	];
	qb:structure 	rdfindex:HealthValueDSD ;  
.
```

* Description of the index:

```
rdfindex:TheLongestLifeCountry a rdfindex:Index;
	rdfs:label 	"The Longest Life Country"@en;
	rdfindex:type 	rdfindex:Quantitative;
	rdfindex:aggregates [ 		
		rdfindex:aggregation-operator rdfindex:Mean;
		rdfindex:part-of rdfindex:Health ;
		rdfindex:part-of rdfindex:PublicSpending ;
	];
	qb:structure 	rdfindex:TheLongestLifeCountryDSD ;
.
```

An important feature of using the RDF Data Cube vocabulary for modelling indexes lies in the description of 
the dataset's structure that serves as metadata for a processor.

On the other hand, initially we only know observations about indicators that can be extracted using an SPARQL query but 
the keypoint is that every time we are aggregating some element we are generating new observations according 
to the dataset's structure and, as a consequence, we can generate a SPARQL query template to retrieve or 
generate any observation at index, component or indicator level using the different dimensions and measures. 
For instance, the index is generated with the next SPARQL query:

```
SELECT  ?dim0 ?dim1 (avg(?measure) as ?newvalue) WHERE{ 
	?observation <http://purl.org/linked-data/cube#dataSet> ?part . 
	FILTER (  (?part = <http://purl.org/rdfindex/ontology/Health> )  ||  
		(?part = <http://purl.org/rdfindex/ontology/PublicSpending> ) ). 
	?observation <http://purl.org/rdfindex/ontology/value> ?measure . 
	?observation <http://purl.org/rdfindex/ontology/ref-year>  ?dim0. 
	?observation <http://purl.org/rdfindex/ontology/ref-area>  ?dim1. 
} GROUP BY ?dim0 ?dim1


```

The Java-SPARQL based processor uses the next SPARQL template that is filled with the metainformation:
```
sparqlQuery = "SELECT "+dimensionVars+" "+formatFormula(operator)+" "+
	      "WHERE{ " +
			SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" "+				
				SPARQLFetcherUtils.formatResource(RDFIndexVocabulary.QB_DATASET.getURI())+" "+
					SPARQLFetcherUtils.formatVar(PART_VAR_SPARQL)+" . "+
			SPARQLFetcherUtils.createFilterPartsOf(partsOf)+
			SPARQLFetcherUtils.formatVar(OBSERVATION_VAR_SPARQL)+" "+
				SPARQLFetcherUtils.formatResource(measure)+" "+
					SPARQLFetcherUtils.formatVar(MEASURE_VAR_SPARQL)+" . "+
			createDimensionsBGPs+		
		"} GROUP BY"+dimensionVars;
```


The process to generate an aggregated element is the next one (with a bottom-up approach from indicator to index):
* Fill the SPARQL query template according to element (indicator, component, index) metainformation
* Query existing observations
* Generate new observations
* Promote the new observations to the next level in which new observations are collected before going to step 1
* Repeat until the index level


### Main advantages

* Same SPARQL query to aggregate values
* Automatic generation of filling of the SPARQL query according to index, component or indicator structure
* Currently the definition of weights it is configured in the indicator or component metadata, maybe it would be better to 
set the weight in the property "part-of" instead of the generic metadata.
* Validation of dimensions and measures. To aggregate some dimensions the narrower componens must have 
the same dimensions and kind of measure. (Structure, domain and range validation)
* Generation of human-readable documentation
* Possiblity of distributed processing (the computation of aggregmated indicators, components or indexes can be performed 
in an isolated mode)
* Dynamic configuration
* Visualization capabilities. For instance using Cubeviz...
* Support
 * RDF Data cube dimensions (only resources no literals support)
 * RDF Data cube measures (only numerical values support)
 * RDF Data cube structures
 * RDF Data cube observations 
 * OWA operators of SPARQL 1.1

### TO-DO and restrictions

* It should be possible to define restrictions on dimensions and measures. For instance a filter. To tackle this the SPIN
vocabulary could be used
* It should be posible to use slices as part an OWA operator
* RDF Data cube dimensions as literals
* More than 1 measure
* Use of RDF Data cube attributes
* Custom OWA operators should be implemented
* It is not possible to express some transformation in observation values (measure). For instance, estimation some missing 
value.
* It is not possible to use select modifiers such as "LIMIT, HAVING or ORDER" but taking into account that this 
work is focused on the computation process and the client should be in charge of presenting the results we consider 
it is not an issue to be solved.
* Would it be better to use the property "skos:narrower" instead of "part-of"?
* Improve the taxonomy and the logical model
* Apply to a real use case: Webindex, CSMIC or Worldbank

## Conclusions

This work tries to exploit the information in RDF Data Cube vocabularies to calculate a special kind of statistical 
measure such as an index. In that sense the main contributions are:
* Extensions to the RDF Data Cube vocabulary to generate SPARQL queries (maybe another kind of query could also be 
generated)
* A Java-SPARQL-based interpreter of this vocabulary

## T-BOX 

A component can aggregate: indicators or slices but always one measure:
* ...aggregates indicators, in that case the indicator is comprised of only one slice and only one measure. (One structure for any indicator).
* ...aggregates slices of indicators (the structure of the indicator is reused).

`C = a * I1 + b * I2+...+ m * In`
`Ii = f(interval, aggregation-function) where interval (t1, t2) or interval(first 100) or interval (last 50)`


## References

* http://www.w3.org/TR/vocab-data-cube/
* http://purl.org/linked-data/sdmx/2009/concept# 
* http://purl.org/linked-data/sdmx/2009/dimension# 
* http://purl.org/linked-data/sdmx/2009/attribute#
* http://purl.org/linked-data/sdmx/2009/measure#
* http://purl.org/linked-data/sdmx/2009/metadata#
* http://purl.org/linked-data/sdmx/2009/code#
* http://purl.org/linked-data/sdmx/2009/subject#




