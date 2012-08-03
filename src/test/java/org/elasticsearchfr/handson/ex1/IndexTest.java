package org.elasticsearchfr.handson.ex1;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearchfr.handson.StartNode;
import org.elasticsearchfr.handson.beans.Beer;
import org.elasticsearchfr.handson.beans.BeerHelper;
import org.elasticsearchfr.handson.beans.Colour;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Exercice 1: we are indexing beers
 * 
 * @author LAYA
 * 
 */
public class IndexTest extends StartNode {

	@Test
	public void indexOneBeer() throws InterruptedException,
			JsonGenerationException, JsonMappingException, IOException,
			ExecutionException {

		// instance a json mapper
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse

		Beer beer = new Beer("Heineken", Colour.PALE, 0.33, 3);
		IndexResponse ir = null;

		// generate a json content
		String jsonString = mapper.writeValueAsString(beer);

		// indexing document
		ir = node.client().prepareIndex("meal", "beer").setSource(jsonString)
				.execute().actionGet();

		Assert.assertNotNull(ir);
		Assert.assertNotNull(ir.id());

		GetResponse gr = node.client().prepareGet("meal", "beer", ir.id())
				.execute().actionGet();

		Assert.assertNotNull(gr);
		Assert.assertNotNull(gr.id());

		Assert.assertEquals(ir.id(), gr.id());

		Beer indexedBeer = mapper.readValue(gr.getSourceAsBytes(), Beer.class);

		Assert.assertNotNull(indexedBeer);
		Assert.assertEquals(beer, indexedBeer);

		// delete document
		DeleteResponse dr = node.client()
				.prepareDelete("meal", "beer", gr.id()).execute().get();

		Assert.assertNotNull(dr);
		Assert.assertFalse(dr.notFound());

		gr = node.client().prepareGet("meal", "beer", dr.id()).execute()
				.actionGet();

		Assert.assertNotNull(gr);
		Assert.assertFalse(gr.exists());

	}

	@Test
	public void indexOneThousandBeers() throws InterruptedException,
			JsonGenerationException, JsonMappingException, IOException,
			ExecutionException {

		ObjectMapper mapper = new ObjectMapper(); // create once, reuse

		BulkRequestBuilder brb = node.client().prepareBulk();

		for (int i = 0; i < 1000; i++) {

			Beer beer = BeerHelper.generate();
			IndexRequest irq = new IndexRequest("meal", "beer", "beer_"+i);
			String jsonString = mapper.writeValueAsString(beer);
			irq.source(jsonString);
			brb.add(irq);

		}

		BulkResponse br = brb.execute().actionGet();

		Assert.assertFalse(br.hasFailures());

		 brb = node.client().prepareBulk();
		
		for (int i = 0; i < 1000; i++) {

			DeleteRequest dr = new DeleteRequest("meal", "beer", "beer_"+i);
			brb.add(dr);

		}

		
		br = brb.execute().actionGet();
		
		for (BulkItemResponse bulkItemResponse : br) {
			
			Assert.assertTrue(bulkItemResponse.response() instanceof DeleteResponse);
			Assert.assertFalse(((DeleteResponse)bulkItemResponse.response()).notFound());
		}
			
	}
		
}
