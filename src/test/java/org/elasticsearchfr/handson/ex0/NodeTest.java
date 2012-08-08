package org.elasticsearchfr.handson.ex0;

import junit.framework.Assert;

import org.elasticsearch.node.Node;
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
		// TODO Build a Node here
		// node = ... ;

		Assert.assertNotNull(node);
		Assert.assertFalse(node.isClosed());
	}
}
