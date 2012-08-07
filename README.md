Hands On Lab
============

Description
-----------

This repository contains project models for hands on lab sessions about elasticsearch.


How to use it
-------------

### Optional

First, you can download a full packaged version of Elasticsearch with:

* Elasticsearch 0.19.8 (Non modified distribution is [here](https://github.com/downloads/elasticsearch/elasticsearch/elasticsearch-0.19.8.zip) )
* elasticsearch.yml file modified to disable multicast and use handson as a cluster name
* [MOBZ Head Plugin](https://github.com/mobz/elasticsearch-head/zipball/master)
* [Bigdesk Plugin](https://github.com/lukas-vlcek/bigdesk/zipball/master)

### Download the project

     git clone https://github.com/elasticsearchfr/hands-on.git

### Compile the project

     mvn compile

### Run tests

     mvn test

Tests should fail as you have to fill blanks!

Use cases
---------

### Test 0: just start a node

When running [NodeTest](https://github.com/elasticsearchfr/hands-on/blob/master/src/test/java/org/elasticsearchfr/handson/ex0/NodeTest.java),
you should bring to live an Elasticsearch node.

When successful, launch an external Elasticsearch node, using the optional download above and look at logs.
You should see that when running test, both nodes see each other.

You can add the following line at the end of NodeTest:

     Thread.sleep(120000);
     
And open in your browser: [http://localhost:9200/_plugin/head/](http://localhost:9200/_plugin/head/) to see one node, then both nodes and then only one node.
Have a look also at [http://localhost:9200/_plugin/bigdesk/](http://localhost:9200/_plugin/bigdesk/)


### Test 1: Index/Get and Delete some documents

All tests are in [IndexTest.java](https://github.com/elasticsearchfr/hands-on/blob/master/src/test/java/org/elasticsearchfr/handson/ex1/IndexTest.java).

#### Index one Beer - indexOneBeer()

We index one beer in index meal, type beer: Heineken, PALE, 0.33 L, 3 EUROS.
We have defined a Javabean to handle [Beer](https://github.com/elasticsearchfr/hands-on/blob/master/src/test/java/org/elasticsearchfr/handson/beans/Beer.java) documents.

We will:
* use Jackson to generate JSon from object
* index the beer
* get back the generated ID for this document
* ask Elasticsearch to get the document back
* deserialize it from JSon to javabean Beer
* check that sent and indexed objects are equals
* remove the beer document and check that it does not exist anymore


#### Index 1000 Beers (or more) - indexOneThousandBeers()

We index 1 000 beers with the Elasticsearch bulk feature.

We will:
* index all beers
* check that there is no failure
* remove all beers

You can increase the number of beer to see how fast is it on your personal computer.


### Test 2: Searching for documents

Before each test we index 1000 beers. After each test, we remove them.

Tests are in [SearchTest.java](https://github.com/elasticsearchfr/hands-on/blob/master/src/test/java/org/elasticsearchfr/handson/ex2/SearchTest.java).

#### Match All Query - matchAllSearch()

We search for all documents. We will learn to build a very simple query and see how we can retrieve
a single hit from results.

Even if you build well the query, it will failed. Can you explain why and how to fix it?

#### Term Query - termSearch_not_working()

We want to search for term "Heineken" in brand field. We will get 0 hit. Can you explain why?

#### Term Query - termSearch()

We fix the previous test. We should get more than 0 beer.

#### Text Query - textSearch()

We can search for HEINEKEN or HeiNEken in brand field. We should get more than 0 beer.

What is the main difference between term and text queries?

#### Query String Query - queryStringSearch()

We search for HEINEKEN in all documents. 

We can also use here a Lucene syntax.
For example, you can also search for "+heineken -pale" or for "+heineken pale" and see the differences.

#### Range Query - rangeSearch()

We build here a query based on beer price. We want to get beers with price between 5 and 10.

#### Boolean Query with Text and Range queries - bool_text_and_range_Search()

We build a boolean query to get HEINEKEN beers with price between 5 and 10.

We will check the first 10 hits that they answer to this rule.

#### Query with filters - query_and_filter_Search()

We use the same query as the previous exercise but we want to filter results with at least 1 litter beers.

We will check the first 10 hits that they answer to this rule.

#### Search like google - google_Search()

We will ask for the first 100 results of "HEINEKEN pale". Let's see in logger how scoring order 
results.

#### Search like google with boost - google_with_boost_Search()

We will ask for the first 100 results of "HEINEKEN pale^3". Let's see in logger how scoring order 
results. We also want to "highlight" some fields: colour and brand to see how it appears in the JSon raw output.







