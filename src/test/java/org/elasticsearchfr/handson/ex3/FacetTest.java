package org.elasticsearchfr.handson.ex3;

import junit.framework.Assert;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.facet.AbstractFacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearchfr.handson.StartNode;
import org.elasticsearchfr.handson.beans.Beer;
import org.elasticsearchfr.handson.beans.BeerHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * We want to test facets.
 * <br>When starting tests, we initialize Elasticsearch cluster with
 * 1000 beers.
 * <br>After tests, we remove all beers.
 * @see http://www.elasticsearch.org/guide/reference/java-api/search.html
 * @see http://www.elasticsearch.org/guide/reference/api/search/facets/index.html
 */
public class FacetTest extends StartNode {
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
	 * We use a matchAll Query with a Terms Facet on brand
	 * <br>
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/match-all-query.html
	 * @see http://www.elasticsearch.org/guide/reference/api/search/facets/terms-facet.html
	 */
	@Test
	public void brand_termsFacet_matchAllQuery() throws Exception {
		QueryBuilder qb = QueryBuilders.matchAllQuery();
		
		AbstractFacetBuilder byBrandFacet = null;
		// TODO Create the facet

		SearchRequestBuilder srb = null;
		// TODO Create the request
		
		logger.info("Your query is : {}", srb);

		SearchResponse sr = srb.execute().actionGet();

		// logger.info("Response is : {}", sr);

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertEquals(1000, sr.getHits().getTotalHits());

		// TODO Check that facets are not null
		Assert.assertNotNull(null);
		// TODO Check that facet bybrand is not null
		Assert.assertNotNull(null);
		// TODO Check that facet bybrand is "terms" type
		Assert.assertEquals("terms", null);

		// TODO Get the bybrand facet
		TermsFacet bybrand = null;
		
		int nbHeineken = 0;
		int nbGrimbergen = 0;
		int nbKriek = 0;
		
		// TODO For each brand, save the count

		// We have only 3 different beers. A default Term Facet returns 10 terms.
		// So we expect to have 1000 total counts
		Assert.assertEquals(1000, nbHeineken + nbGrimbergen + nbKriek);

		logger.info("We have : {} Heineken, {} Grimbergen and {} Kriek", nbHeineken, nbGrimbergen, nbKriek);
	}
	
	/**
	 * We use a Term Query on "brand" with term "heineken" with a Terms Facet on brand
	 * <br>
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/term-query.html
	 * @see http://www.elasticsearch.org/guide/reference/api/search/facets/terms-facet.html
	 */
	@Test
	public void brand_termsFacet_termQuery() throws Exception {
		QueryBuilder qb = QueryBuilders.termQuery("brand", "heineken");
		AbstractFacetBuilder byBrandFacet = null;
		// TODO Create the facet

		SearchRequestBuilder srb = null;
		// TODO Create the request
		
		logger.info("Your query is : {}", srb);

		SearchResponse sr = srb.execute().actionGet();

		// logger.info("Response is : {}", sr);

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());

		Assert.assertNotNull(sr.getFacets());
		Assert.assertNotNull(sr.getFacets().facet("bybrand"));
		Assert.assertEquals("terms", sr.getFacets().facet("bybrand").getType());

		TermsFacet bybrand = sr.getFacets().facet("bybrand");
		
		int nbHeineken = 0;
		int nbGrimbergen = 0;
		int nbKriek = 0;
		
		for (TermsFacet.Entry entry : bybrand) {
			if ("Heineken".equalsIgnoreCase(entry.getTerm())) nbHeineken = entry.count();
			if ("Grimbergen".equalsIgnoreCase(entry.getTerm())) nbGrimbergen = entry.count();
			if ("Kriek".equalsIgnoreCase(entry.getTerm())) nbKriek = entry.count();
		}

		Assert.assertEquals(0, nbGrimbergen);
		Assert.assertEquals(0, nbKriek);
		
		// We have only Heineken beers. So we expect to have less than 1000 beers.
		Assert.assertTrue(nbHeineken + nbGrimbergen + nbKriek < 1000);

		logger.info("We have : {} Heineken, {} Grimbergen and {} Kriek", nbHeineken, nbGrimbergen, nbKriek);
	}	
	
	/**
	 * We use a Term Filter on "brand" with term "heineken" with a Terms Facet on brand
	 * <br>
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/term-query.html
	 * @see http://www.elasticsearch.org/guide/reference/api/search/facets/terms-facet.html
	 */
	@Test @Ignore
	public void brand_termsFacet_termFilter() throws Exception {
		QueryBuilder qb = QueryBuilders.matchAllQuery();
		FilterBuilder filter = FilterBuilders.termFilter("brand", "heineken");
		AbstractFacetBuilder byBrandFacet = null;
		// TODO Create the facet

		SearchRequestBuilder srb = null;
		// TODO Create the request
		
		logger.info("Your query is : {}", srb);

		SearchResponse sr = srb.execute().actionGet();

		// logger.info("Response is : {}", sr);

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());

		Assert.assertNotNull(sr.getFacets());
		Assert.assertNotNull(sr.getFacets().facet("bybrand"));
		Assert.assertEquals("terms", sr.getFacets().facet("bybrand").getType());

		TermsFacet bybrand = sr.getFacets().facet("bybrand");
		
		int nbHeineken = 0;
		int nbGrimbergen = 0;
		int nbKriek = 0;
		
		for (TermsFacet.Entry entry : bybrand) {
			if ("Heineken".equalsIgnoreCase(entry.getTerm())) nbHeineken = entry.count();
			if ("Grimbergen".equalsIgnoreCase(entry.getTerm())) nbGrimbergen = entry.count();
			if ("Kriek".equalsIgnoreCase(entry.getTerm())) nbKriek = entry.count();
		}

		// We have only Heineken beers.
		// So why do we get 1000 beers as facet count?
		Assert.assertEquals(1000, nbHeineken + nbGrimbergen + nbKriek);

		logger.info("We have : {} Heineken, {} Grimbergen and {} Kriek", nbHeineken, nbGrimbergen, nbKriek);
	}
	
	
	/**
	 * We use a Term Query on "brand" with term "heineken" with a Filtered Terms Facet on brand
	 * <br>
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/term-query.html
	 * @see http://www.elasticsearch.org/guide/reference/api/search/facets/terms-facet.html
	 */
	@Test @Ignore
	public void brand_termsFacet_withFilter_termFilter() throws Exception {
		QueryBuilder qb = QueryBuilders.matchAllQuery();
		FilterBuilder filter = FilterBuilders.termFilter("brand", "heineken");
		AbstractFacetBuilder byBrandFacet = null;
		// TODO Create the facet

		SearchRequestBuilder srb = null;
		// TODO Create the request
		
		logger.info("Your query is : {}", srb);

		SearchResponse sr = srb.execute().actionGet();

		// logger.info("Response is : {}", sr);

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());

		Assert.assertNotNull(sr.getFacets());
		Assert.assertNotNull(sr.getFacets().facet("bybrand"));
		Assert.assertEquals("terms", sr.getFacets().facet("bybrand").getType());

		TermsFacet bybrand = sr.getFacets().facet("bybrand");
		
		int nbHeineken = 0;
		int nbGrimbergen = 0;
		int nbKriek = 0;
		
		for (TermsFacet.Entry entry : bybrand) {
			if ("Heineken".equalsIgnoreCase(entry.getTerm())) nbHeineken = entry.count();
			if ("Grimbergen".equalsIgnoreCase(entry.getTerm())) nbGrimbergen = entry.count();
			if ("Kriek".equalsIgnoreCase(entry.getTerm())) nbKriek = entry.count();
		}

		// We have only Heineken beers. And we have filtered facet with Heineken beers.
		// So we won't have 1000 total count (unless we are unlucky).
		Assert.assertTrue(nbHeineken + nbGrimbergen + nbKriek < 1000);

		logger.info("We have : {} Heineken, {} Grimbergen and {} Kriek", nbHeineken, nbGrimbergen, nbKriek);
	}	
	
	/**
	 * We use a matchAll Query with a Range Facet on price. We want to see beers with
	 * <ul>
	 * <li>price < 3
	 * <li>price >= 3 and price < 6
	 * <li>price >= 6
	 * </ul>
	 * @throws Exception
	 * @see http://www.elasticsearch.org/guide/reference/java-api/query-dsl.html
	 * @see http://www.elasticsearch.org/guide/reference/query-dsl/match-all-query.html
	 * @see http://www.elasticsearch.org/guide/reference/api/search/facets/range-facet.html
	 */
	@Test
	public void brand_rangeFacet_matchAllQuery() throws Exception {
		QueryBuilder qb = QueryBuilders.matchAllQuery();
		AbstractFacetBuilder byPriceFacet = null;
		// TODO Create the facet

		SearchRequestBuilder srb = null;
		// TODO Create the request
		
		logger.info("Your query is : {}", srb);

		SearchResponse sr = srb.execute().actionGet();

		logger.info("Response is : {}", sr);

		Assert.assertNotNull(sr);
		Assert.assertNotNull(sr.getHits());
		Assert.assertEquals(1000, sr.getHits().getTotalHits());

		Assert.assertNotNull(sr.getFacets());
		Assert.assertNotNull(sr.getFacets().facet("byprice"));
		Assert.assertEquals("range", sr.getFacets().facet("byprice").getType());

		// TODO Get the byprice facet
		RangeFacet byprice = null;
		
		long nbTo3 = 0;
		long nbFrom3To6 = 0;
		long nbFrom6 = 0;
		
		// TODO verify that the total of each count is 1000

		Assert.assertEquals(1000, nbTo3 + nbFrom3To6 + nbFrom6);

		logger.info("We have : {} lower than 3, {} lower than 6 and {} upper than 6", nbTo3, nbFrom3To6, nbFrom6);
	}	
}
