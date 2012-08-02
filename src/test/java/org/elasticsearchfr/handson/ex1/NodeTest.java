package org.elasticsearchfr.handson.ex1;

import junit.framework.Assert;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearchfr.handson.StartNode;
import org.junit.Test;

/**
 * Exercice 1: we are indexing beers
 * @author LAYA
 *
 */
public class NodeTest extends StartNode {

	@Test
	public void testNode() throws InterruptedException {

		// Index
		System.out.println(node);
	
	}
}
