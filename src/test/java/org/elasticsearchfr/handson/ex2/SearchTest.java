package org.elasticsearchfr.handson.ex2;

import junit.framework.Assert;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearchfr.handson.StartNode;
import org.elasticsearchfr.handson.beans.Beer;
import org.elasticsearchfr.handson.beans.BeerHelper;
import org.elasticsearchfr.handson.beans.Colour;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * We want to test search methods.
 * <br>When starting tests, we initialize Elasticsearch cluster with
 * 1000 beers.
 * <br>After tests, we remove all beers.
 * @see http://www.elasticsearch.org/guide/reference/java-api/search.html
 */
public class SearchTest extends StartNode {
	protected final ESLogger logger = ESLoggerFactory.getLogger(this.getClass().getName());

	/**
	 * When we start a test, we index 1000 beers with random data
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		BulkRequestBuilder brb = node.client().prepareBulk();

		for (int i = 0; i < 1000; i++) {
			Beer beer = BeerHelper.generate();
			IndexRequest irq = new IndexRequest("meal", "beer", "beer_" + i);
			String jsonString = mapper.writeValueAsString(beer);
			irq.source(jsonString);
			brb.add(irq);
		}

		BulkResponse br = brb.execute().actionGet();
		Assert.assertFalse(br.hasFailures());

		// todo remove for exercise
		node.client().admin().indices().prepareRefresh().execute().actionGet();
	}

	/**
	 * When we stop a test, we remove all data
	 */
	@After
	public void tearDown() {
		BulkRequestBuilder brb = node.client().prepareBulk();

		for (int i = 0; i < 1000; i++) {
			DeleteRequest dr = new DeleteRequest("meal", "beer", "beer_" + i);
			brb.add(dr);
		}

		BulkResponse br = brb.execute().actionGet();
		Assert.assertFalse(br.hasFailures());
	}

	/**
	 * We want to build a matchAll Query
	 * <br>We should have 1000 results
	 * <br>We want to display the _source content of the first Hit.
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/match-all-query.html
	 */
	@Test
	public void matchAllSearch() throws Exception {
		QueryBuilder qb = QueryBuilders.matchAllQuery();

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch()
				.setQuery(qb).execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertEquals(1000, sr.getHits().getTotalHits());

		String jsonFirstHit = sr.getHits().getHits()[0].getSourceAsString();
		logger.info("Your first is : {}", jsonFirstHit);
	}

	/**
	 * We want to build a termQuery Query to find "Heineken" beers.
	 * <br>But we won't have any result. Could you explain why?
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/term-query.html
	 */
	@Test
	public void termSearch_not_working() throws Exception {
		QueryBuilder qb = QueryBuilders.termQuery("brand", "HeineKen");

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch()
				.setQuery(qb)
				.execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		
		// We have 0 hit. Why?
		Assert.assertEquals(0, sr.getHits().getTotalHits());

		logger.info("We found {} beer. Why?", sr.getHits().totalHits());
	}

	/**
	 * We want to build a termQuery Query
	 * <br>We should have some results (or we are really unlucky!). So fix the previous test {@link #termSearch_not_working()}
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/term-query.html
	 */
	@Test
	public void termSearch() throws Exception {
		QueryBuilder qb = QueryBuilders.termQuery("brand", "heineken");

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch()
				.setQuery(qb)
				.execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertTrue(sr.getHits().getTotalHits() > 0);

		logger.info("We found {} beers", sr.getHits().totalHits());
	}

	/**
	 * We want to build a textQuery Query
	 * <br>We should have some results (or we are really unlucky!).
	 * <br>Note that we can search "HEINEKEN is a beer"
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/match-query.html
	 */
	@Test
	public void textSearch() throws Exception {
		QueryBuilder qb = QueryBuilders.matchQuery("brand", "HEINEKEN");

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch()
				.setQuery(qb)
				.execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertTrue(sr.getHits().getTotalHits() > 0);

		logger.info("We found {} beers", sr.getHits().totalHits());
	}

	/**
	 * We want to build a queryString Query
	 * <br>We should have some results (or we are really unlucky!).
	 * <br>Note that we can search "HEINEKEN is a beer". Note that you can use a Lucene syntax.
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/query-string-query.html
	 * @see http://lucene.apache.org/core/3_6_1/queryparsersyntax.html
	 * 
	 */
	@Test
	public void queryStringSearch() throws Exception {
		QueryBuilder qb = QueryBuilders.queryString("HEINEKEN");

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch()
				.setQuery(qb).execute()
				.actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertTrue(sr.getHits().getTotalHits() > 0);
		
		logger.info("We found {} beers", sr.getHits().totalHits());
	}

	/**
	 * We want to build a rangeQuery Query on price field to get all beers
	 * with price between 5 and 10.
	 * <br>We should have some results (or we are really unlucky!).
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/range-query.html
	 * 
	 */
	@Test
	public void rangeSearch() throws Exception {
		QueryBuilder qb = QueryBuilders.rangeQuery("price").from(5).to(10);

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch()
				.setQuery(qb)
				.execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertTrue(sr.getHits().getTotalHits() > 0);

		logger.info("We found {} beers", sr.getHits().totalHits());
	}

	/**
	 * We want to build a complex Query based on brand name and price fields.
	 * <br>We must find Heineken beers with price between 5 and 10.
	 * <br>We should have some results (or we are really unlucky!).
	 * <br>We will also get the first 10 hits and convert them into
	 * Beer javabeans to check that Elasticsearch has really return
	 * what we were looking for.
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/match-query.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/range-query.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/bool-query.html
	 */
	@Test
	public void bool_text_and_range_Search() throws Exception {
		QueryBuilder qb = QueryBuilders
				.boolQuery()
					.must(
						QueryBuilders.matchQuery("brand", "HEINEKEN")
					)
					.must(
						QueryBuilders.rangeQuery("price").from(5).to(10)
					);

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch().setQuery(qb)
				.execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertTrue(sr.getHits().getTotalHits() > 0);

		logger.info("We found {} beers", sr.getHits().totalHits());

		for (SearchHit hit : sr.getHits()) {
			Beer beer = BeerHelper.toBeer(hit.getSourceAsString());
			Assert.assertEquals("Heineken", beer.getBrand());
			Assert.assertTrue(beer.getPrice()>5 && beer.getPrice()<10);
		}
	}
	
	/**
	 * We want to build a complex Query based on brand name and price fields and we want to filter results
	 * to have only 1L or more beers.
	 * <br>We must find Heineken beers with price between 5 and 10 and size more than 1.
	 * <br>We should have some results (or we are really unlucky!).
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/match-query.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/range-query.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/bool-query.html
	 */
	@Test
	public void query_and_filter_Search() throws Exception {
		QueryBuilder query = QueryBuilders
				.boolQuery()
					.must(
						QueryBuilders.matchQuery("brand", "HEINEKEN")
					)
					.must(
						QueryBuilders.rangeQuery("price").from(5).to(10)
					);
		FilterBuilder filter = FilterBuilders.rangeFilter("size").from(1);
		QueryBuilder qb = QueryBuilders.filteredQuery(query, filter);

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch().setQuery(qb)
				.execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertTrue(sr.getHits().getTotalHits() > 0);

		logger.info("We found {} beers", sr.getHits().totalHits());
		
		for (SearchHit hit : sr.getHits()) {
			Beer beer = BeerHelper.toBeer(hit.getSourceAsString());
			Assert.assertEquals("Heineken", beer.getBrand());
			Assert.assertTrue(beer.getPrice()>5 && beer.getPrice()<10);
			Assert.assertTrue(beer.getSize()>1);
		}

		logger.info("Full json result is: {}", sr.toString());
	}

	/**
	 * We want to search like google and see how scoring works on different documents.
	 * <br>We will ask for the 100 first documents.
	 * <br>We should have some results (or we are really unlucky!).
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/query-string-query.html
	 * @see http://www.elasticsearch.org/guide/reference/api/search/from-size.html
	 */
	@Test
	public void google_Search() throws Exception {
		QueryBuilder qb = QueryBuilders.queryString("HEINEKEN pale");

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch().setQuery(qb)
				.setSize(100)
				.execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertTrue(sr.getHits().getTotalHits() > 0);

		logger.info("We found {} beers", sr.getHits().totalHits());
		
		int nbHeineken = 0;
		int nbPale = 0;
		for (SearchHit hit : sr.getHits()) {
			Beer beer = BeerHelper.toBeer(hit.getSourceAsString());

			if ("Heineken".equals(beer.getBrand())) nbHeineken++;
			if (Colour.PALE.equals(beer.getColour())) nbPale++;
		}
		
		logger.info("For the first 100 beers, we have {} Heineken and {} pale beers", nbHeineken, nbPale);
		logger.info("Full json result is: {}", sr.toString());
		

	}

	/**
	 * We want to search like google and see how scoring works on different documents.
	 * <br>We will boost the colour as colour is more important than brand.
	 * <br>We will ask for the 100 first documents.
	 * <br>We will highlight fields brand and colour
	 * <br>We should have some results (or we are really unlucky!).
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/query-string-query.html
	 * @see http://www.elasticsearch.org/guide/reference/api/search/from-size.html
	 * @see http://www.elasticsearch.org/guide/reference/api/search/highlighting.html
	 */
	@Test
	public void google_with_boost_Search() throws Exception {
		QueryBuilder qb = QueryBuilders.queryString("HEINEKEN pale^3");

		logger.info("Your query is : {}", qb);

		SearchResponse sr = node.client().prepareSearch().setQuery(qb)
				.setSize(100)
				.addHighlightedField("brand")
				.addHighlightedField("colour")
				.execute().actionGet();

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertTrue(sr.getHits().getTotalHits() > 0);

		logger.info("We found {} beers", sr.getHits().totalHits());
		
		int nbHeineken = 0;
		int nbPale = 0;
		for (SearchHit hit : sr.getHits()) {
			Beer beer = BeerHelper.toBeer(hit.getSourceAsString());

			if ("Heineken".equals(beer.getBrand())) nbHeineken++;
			if (Colour.PALE.equals(beer.getColour())) nbPale++;
		}
		
		logger.info("For the first 100 beers, we have {} Heineken and {} pale beers", nbHeineken, nbPale);
		
		// We expect to have more or equals pale beers than heineken
		Assert.assertTrue(nbPale >= nbHeineken);
		
		logger.info("Full json result is: {}", sr.toString());
		

	}

	
}
