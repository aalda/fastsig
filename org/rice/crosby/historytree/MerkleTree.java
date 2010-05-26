package org.rice.crosby.historytree;

import org.rice.crosby.historytree.generated.Serialization.HistNode;

/** DOES NOT WORK WITH APPENDONLYARRAY */
public class MerkleTree<A, V> extends TreeBase<A, V> {	
	/** Make an empty merkle tree with a given aggobj and datastore.  */
	public MerkleTree(AggregationInterface<A,V> aggobj,
	    		   HistoryDataStoreInterface<A,V> datastore) {
	    super(aggobj,datastore);
	}
	
	public void freezeHelper(NodeCursor<A,V> node) {
  		node.markValid();
  		if (!node.isFrozen(time)) {
  			node.forceRight().setAgg(aggobj.emptyAgg()); // Force every right child to be valid, will keep the NULL default agg.
  			node.setAgg(aggobj.aggChildren(node.left().getAgg(), null));
  		} else {
  			node.setAgg(aggobj.aggChildren(node.left().getAgg(), node.right().getAgg()));
  		}	  			
	}
	/** Freeze the tree. After this, nothing else should be added to the Merkle tree. 
	 * Until being frozen, proofs and subtrees must not be built.
	 *	
	 * Take every node in path to leaf and mark it valid and set as a stub with a precomputed agg.
 	 */
	public void freeze() {
	  	isFrozen = true;
	  	if (time == 0)
	  		return;
	  	NodeCursor<A,V> node,leaf=leaf(time);
	  	
	  	node = leaf.getParent(root);
	  	while ((node=node.getParent(root)) != null) {
	  		freezeHelper(node);
	  	}
	}
	
	boolean isFrozen = false;
	
	@Override
	public A agg() {
		if (!isFrozen) 
			throw new Error("Cannot compute agg from unfrozen MerkleTree");
		return root.getAgg();
	}

	@Override
	void parseSubtree(NodeCursor<A, V> node, HistNode in) {
    	if (parseNode(node,in))
    		return; // If its a stub.

    	parseSubtree(node.forceLeft(),in.getLeft());
    	parseSubtree(node.forceRight(),in.getRight());
    	node.markValid();
    	node.setAgg(aggobj.aggChildren(node.left().getAgg(),node.right().getAgg()));
	}
}
