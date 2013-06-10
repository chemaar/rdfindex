The RDFindex
========

This project defines:

* A high-level vocabulary on top of the RDF Data Cube Vocabulary to specify indexes such as "The Webindex", "The CSMIC", "The Shanghai Ranking" or "The H-index"
* A computation process vocabulary to aggregate the different entities and data of an index
* A process of the vocabulary and computation process


## Definitions

* Index: this is the class of indexes that aggregates pieces of data in a quantitative value.
*  In general, an index is only comprised of components but other indicators and slices can be also part of the index.
* Component: a particular abstraction of an index that aggregates indicators and slices.
* Indicator: a bag of observations defined by (n dimensions, 1 measure, n attributes).
* Slice: a view of an indicator. More information in the RDF Data Cube Vocabulary.
* Observation: a measure belonging to a slice of an indicator.  More information in the RDF Data Cube Vocabulary.
* Dimension: the concepts we are measuring e.g. area, time, sex, age, etc.  More information in the RDF Data Cube Vocabulary.
* Measure:  More information in the RDF Data Cube Vocabulary.
* Attribute:  More information in the RDF Data Cube Vocabulary.


### Motivating Example

I have observations about the "Life Expectancy" in different countries and I want to create an index calle "The Longest Life Country" that 
takes a component called "HealthValue" and it is comprised of the average of life expectancy

| Year  | Region        | Life Expectancy Male  | Life Expectancy Female |
| ------|-------------- | ---------------------:|-----------------------:|
| 2005  |Spain          | 78|80|
| 2006  |Spain          | 78|81|
| 2007  |Spain          | 78|82|
| 2005  |Turkey          | 76|79|
| 2006  |Turkey         | 76|80|
| 2007  |Turkey          | 77|80|

This is an example similar to the presented in the RDF Data Cube vocabulary in which we can define dimensiones, measures, attributes, datasets and 
slices but:

* if I want to create a composed indicator that is the "Life Expectancy" calculated as the average per country and year, how can I define this indicator? how can I define the computation process?
 * Basically the RDF Data Cube vocabulary eases the creation of slices but it does not enable the creation of composed slices, more specifically the slice...


| Year  | Region        | Life Expectancy |
| ------|-------------- | ----------------:|
| 2005  |Spain          | 79
| 2006  |Spain          | 79.5
| 2007  |Spain          | 80
| 2005  |Turkey         | 77.5
| 2006  |Turkey         | 78
| 2007  |Turkey         | 78.5

* Component "HealthValue": FIXME
* Index "The Longest Life Country": FIXME

## Specification


## T-BOX 

A component can aggregate: indicators or slices but always one measure:
* ...aggregates indicators, in that case the indicator is comprised of only one slice and only one measure. (One structure for any indicator).
* ...aggregates slices of indicators (the structure of the indicator is reused).

`C = a * I1 + b * I2+...+ m * In`
`Ii = f(interval, aggregation-function) where interval (t1, t2) or interval(first 100) or interval (last 50)`





## SPARQL Queries

<pre>
PREFIX qb: <http://purl.org/linked-data/cube#>
PREFIX rdfindex: <http://purl.org/rdfindex/ontology/>
SELECT ?year ?area (avg(?value) as ?average) WHERE {
	?slice qb:observation ?obs.
	FILTER ( (?slice=rdfindex:slice1) || (?slice=rdfindex:slice2)).
	?obs rdfindex:life-expectancy ?value.
	?obs rdfindex:ref-year ?year.
	FILTER (?year<=2005 && ?year<=2010).
	?obs rdfindex:ref-area ?area.
} GROUP BY ?year ?area
</pre>

## References

* http://www.w3.org/TR/vocab-data-cube/


