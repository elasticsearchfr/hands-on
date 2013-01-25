package org.elasticsearchfr.handson.ex0;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Exercice 0: we just want to start a Node
 * @author LAYA
 *
 */
public class NodeTest{

	@Test
	public void testNode() throws InterruptedException {
		Node node = null;

		// Then we start our node for tests
		node = NodeBuilder.nodeBuilder().node();

		Assert.assertNotNull(node);
		Assert.assertFalse(node.isClosed());
	}
}
