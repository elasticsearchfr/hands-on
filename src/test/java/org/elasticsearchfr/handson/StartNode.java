package org.elasticsearchfr.handson;

import java.io.File;

import junit.framework.Assert;

import org.elasticsearch.common.io.FileSystemUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Before;
import org.junit.BeforeClass;

public class StartNode {
	

	/**
	 * Elasticsearch node
	 */
	protected static Node node;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (node == null) {
			// We remove old data before launching tests
			removeOldDataDir();
			
			// Then we start our node for tests
			node = NodeBuilder.nodeBuilder().node();

			// We wait now for the yellow (or green) status
			node.client().admin().cluster().prepareHealth()
					.setWaitForYellowStatus().execute().actionGet();

			Assert.assertNotNull(node);
			Assert.assertFalse(node.isClosed());
		}
	}

	private static void removeOldDataDir() throws Exception {
		Settings settings = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch.yml").build();

		// First we delete old datas...
		File dataDir = new File(settings.get("path.data"));
		if (dataDir.exists()) {
			FileSystemUtils.deleteRecursively(dataDir, true);
		}
	}
	

}
