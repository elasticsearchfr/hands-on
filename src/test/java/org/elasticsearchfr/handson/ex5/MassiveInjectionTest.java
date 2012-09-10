package org.elasticsearchfr.handson.ex5;

import junit.framework.Assert;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearchfr.handson.StartNode;
import org.elasticsearchfr.handson.beans.Beer;
import org.elasticsearchfr.handson.beans.BeerHelper;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * We want to inject many many many documents.
 */
public class MassiveInjectionTest extends StartNode {
	protected final ESLogger logger = ESLoggerFactory.getLogger(this.getClass().getName());

	public void setUp() throws Exception {
	}

	/**
	 * We inject many docs 1k per 1k
	 * @throws Exception
	 */
	@Test
	public void injectMany() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		try {
			node.client().admin().indices().create(
					new CreateIndexRequest("massive")
						.settings("{\"index\" : {\"number_of_shards\" : 10,\"number_of_replicas\" : 0}}")
					).actionGet();
		} catch (IndexAlreadyExistsException e) {
			logger.info("Index already exists... Ignoring...");
		}
		
		// We wait now for the yellow (or green) status
		node.client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet(); 

		for (int i = 0; i < 1000; i++) {
			BulkRequestBuilder brb = node.client().prepareBulk();

			for (int j = 0; j < 1000; j++) {
				Beer beer = BeerHelper.generate();
				IndexRequest irq = new IndexRequest("massive", "beer", "beer_" + i + "_" + j);
				String jsonString = mapper.writeValueAsString(beer);
				irq.source(jsonString);
				brb.add(irq);
			}

			BulkResponse br = brb.execute().actionGet();
			Assert.assertFalse(br.hasFailures());
		}
	}
}
