package org.elasticsearchfr.handson.ex1;

import java.io.IOException;

import junit.framework.Assert;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearchfr.handson.StartNode;
import org.elasticsearchfr.handson.beans.Beer;
import org.elasticsearchfr.handson.beans.Colour;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
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
			JsonGenerationException, JsonMappingException, IOException {

		//instance a json mapper
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse

		Beer beer = new Beer("Heineken", Colour.PALE, 0.33f, 3f);
		IndexResponse ir=null;
		//generate a json content
		String jsonString = mapper.writeValueAsString(beer);


//		IndexResponse ir = node.client().prepareIndex("meal", "beer")
//				.setSource(jsonString).execute().actionGet();

		Assert.assertNotNull(ir);
		Assert.assertNotNull(ir.id());


		System.out.println(node);

	}

}
