package com.ebay.app.raptor.cache.redis.property;

import java.util.Comparator;

public class PropertyEvaluatorComparator implements
		Comparator<PropertyEvaluator> {

	@Override
	public int compare(PropertyEvaluator e1, PropertyEvaluator e2) {
		if (e1.getSequence() < e2.getSequence()) {
			return 1;
		} else if (e1.getSequence() == e2.getSequence()) {
			return 0;
		} else {
			return -1;
		}
	}

}
