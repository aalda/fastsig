package org.rice.crosby.historytree.storage;

import java.util.ArrayList;

import org.rice.crosby.historytree.NodeCursor;

public class ArrayStore<A,V> extends ArrayStoreBase<A, V> 
		 {

	public ArrayStore() {
		super();
		this.aggvalid = new ArrayList<Boolean>(5);
	}

	@Override
	public boolean isAggValid(NodeCursor<A, V> node) {
		return aggvalid.get(node.computeIndex()).booleanValue();
	}

	@Override
	public void markValid(NodeCursor<A, V> node) {
		assert node.index <= time : "Prob: "+ node.index + " " +time;
		aggvalid.set(node.computeIndex(), new Boolean(true));
	}

	@Override
	public void updateTime(int time) {
		assert (time > this.time);
		this.time = time;		

		
		while (time+1 > valstore.size())
			valstore.add(null);
		while (4*time+1 > aggstore.size()) {
			aggstore.add(null);
			aggvalid.add(new Boolean(false));
		}
	}

	private ArrayList<Boolean>  aggvalid;

}

