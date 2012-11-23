package org.elasticsearchfr.handson.plugins;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * We are testing the Suggest Plugin here.
 * To Run this test, you have to launch a local node on localhost:9300 with the 
 * <a href="https://github.com/spinscale/elasticsearch-suggest-plugin">Suggest Plugin</a>.
 * <br>This test is disabled by default
 * @author David Pilato (aka dadoonet)
 */
@Ignore
public class SuggestPluginTest {
	protected final ESLogger logger = ESLoggerFactory.getLogger(this.getClass().getName());

	/**
	 * @throws Exception
	 */
	@Test
	public void createSuggest() throws Exception {
		Client client = null;
		try {
			client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost",9300));
			
			client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
			
			// Create index and mapping
			String mapping = readFileInClasspath("/productmapping.json");
			Assert.assertNotNull(mapping);

			try {
				client.admin().indices().prepareDelete("test").execute().actionGet();
			} catch (org.elasticsearch.indices.IndexMissingException e) {
				// If index does not exist, we should get an IndexMissingException: fine!
			}

			client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
			client.admin().indices().prepareCreate("test").execute().actionGet();
			client.admin().indices().preparePutMapping("test").setType("product").setSource(mapping).execute().actionGet();			
			client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();

			String json = null;
			for (int i = 0; i < 10; i++) {
				json = "{\"ProductId\": \""+ i +"\", \"ProductName\": \"my product "+ i +"\" }";
				client.prepareIndex("test", "product").setSource(json).execute().actionGet();
			}
			
			client.admin().indices().prepareRefresh().execute().actionGet();
			
			List<String> suggestions = new SuggestRequestBuilder(client)
				.field("ProductName")
				.term("my")
				.size(10)
				.similarity(2.0f)
				.execute().actionGet().suggestions();
			
			Assert.assertTrue(suggestions.size() > 0);
		} catch (Exception e) {
			logger.error("Error while running suggest test", e);
		} finally {
			if (client != null) client.close();
		}
	}
	
	private static String readFileInClasspath(String url) throws Exception {
		StringBuffer bufferJSON = new StringBuffer();
		
		try {
			InputStream ips= SuggestPluginTest.class.getResourceAsStream(url); 
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String line;
			
			while ((line=br.readLine())!=null){
				bufferJSON.append(line);
			}
			br.close();
		} catch (Exception e){
			return null;
		}

		return bufferJSON.toString();
	}	
}
