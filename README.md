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


## Specification


## T-BOX 

A component can aggregate: indicators or slices but always one measure:
* ...aggregates indicators, in that case the indicator is comprised of only one slice and only one measure. (One structure for any indicator).
* ...aggregates slices of indicators (the structure of the indicator is reused).

`C = a * I1 + b * I2+...+ m * In`
`Ii = f(interval, aggregation-function) where interval (t1, t2) or interval(first 100) or interval (last 50)`





## SPARQL Queries


## References

* http://www.w3.org/TR/vocab-data-cube/


