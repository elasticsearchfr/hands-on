package org.elasticsearchfr.handson.ex1;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearchfr.handson.StartNode;
import org.elasticsearchfr.handson.beans.Beer;
import org.elasticsearchfr.handson.beans.BeerHelper;
import org.elasticsearchfr.handson.beans.Colour;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Exercice 1: we are indexing beers
 */
public class IndexTest extends StartNode {

	/**
	 * We index one beer in index meal, type beer: Heineken, PALE, 0.33 L, 3 EUROS
	 * <br>We will use Jackson to generate JSon from object
	 * <br>We will get back the generated ID for this document
	 * <br>We will ask Elasticsearch to get the document back
	 * <br>We will deserialize it from JSon to javabean Beer
	 * <br>We will check that sent and indexed objects are equals {@link Beer#equals(Object)}
	 * <br>We will remove the beer document and check that it does not exist anymore
	 * @throws InterruptedException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ExecutionException
	 * @see http://www.elasticsearch.org/guide/reference/java-api/index_.html
	 * @see http://www.elasticsearch.org/guide/reference/java-api/get.html
	 * @see http://www.elasticsearch.org/guide/reference/java-api/delete.html
	 */
	@Test
	public void indexOneBeer() throws InterruptedException,
			JsonGenerationException, JsonMappingException, IOException,
			ExecutionException {

		// instance a json mapper
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse

		Beer beer = new Beer("Heineken", Colour.PALE, 0.33, 3);
		IndexResponse ir = null;

		// generate a json content
		String jsonString = null;
		// TODO Serialize Beer to json

		// indexing document
		// TODO index the beer in meal index, beer type

		Assert.assertNotNull(ir);
		Assert.assertNotNull(ir.id());

		GetResponse gr = null;
		// TODO get the beer we have just indexed 

		Assert.assertNotNull(gr);
		Assert.assertNotNull(gr.id());

		// We check that id are equals
		Assert.assertEquals(ir.id(), gr.id());

		Beer indexedBeer = null;
		
		// TODO Deserialize json indexed beer into a beer object

		Assert.assertNotNull(indexedBeer);
		Assert.assertEquals(beer, indexedBeer);

		// delete document
		DeleteResponse dr = null;
		
		// TODO Remove from elasticsearch the indexed beer

		Assert.assertNotNull(dr);
		Assert.assertFalse(dr.notFound());

		// TODO get the beer we have just removed 

		Assert.assertNotNull(gr);
		// Beer should not exist anymore
		Assert.assertFalse(gr.exists());
	}

	/**
	 * We index 1000 random beers in index meal, type beer
	 * <br>We will use Bulk
	 * <br>We will remove all beers
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @see http://www.elasticsearch.org/guide/reference/java-api/index_.html
	 * @see http://www.elasticsearch.org/guide/reference/java-api/get.html
	 * @see http://www.elasticsearch.org/guide/reference/java-api/delete.html
	 */
	@Test
	public void indexOneThousandBeers() throws JsonGenerationException, JsonMappingException, 
			IOException {

		ObjectMapper mapper = new ObjectMapper(); // create once, reuse

		BulkRequestBuilder brb = null;
		
		// TODO Create the bulk
		brb = node.client().prepareBulk();
		for (int i = 0; i < 1000; i++) {
			Beer beer = BeerHelper.generate();
			IndexRequest irq =  null; 
			
			// TODO Add the beer to meal index, type beer and set id = "beer_"+i
		}
		BulkResponse br = null;
		
		// TODO Execute the bulk

		Assert.assertFalse(br.hasFailures());

		// TODO and now remove all beers using bulk

		// We will check that all beers were found before removal
		for (BulkItemResponse bulkItemResponse : br) {
			Assert.assertTrue(bulkItemResponse.response() instanceof DeleteResponse);
			Assert.assertFalse(((DeleteResponse)bulkItemResponse.response()).notFound());
		}
	}
}
