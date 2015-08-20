/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.DefaultMuleEvent;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.routing.AggregationContext;
import org.mule.routing.AggregationStrategy;

import com.google.common.collect.Lists;

/**
 * This aggregation strategy will take two lists as input and create a third one that
 * will be the merge of the previous two.
 * 
 */
public class SFDCOpportunitiesMergeAggregationStrategy implements AggregationStrategy {	
	@Override
	public MuleEvent aggregate(AggregationContext context) throws MuleException {
		List<MuleEvent> muleEventsWithoutException = context.collectEventsWithoutExceptions();
		int muleEventsWithoutExceptionCount = muleEventsWithoutException.size();
		
		// data should be loaded from both sources (A and B)
		if (muleEventsWithoutExceptionCount != 2) {
			throw new IllegalStateException("Data from at least one source was not able to be obtained correctly.");
		}
		
		// mule event that will be rewritten
		MuleEvent originalEvent = context.getOriginalEvent();
		// message which payload will be rewritten
		MuleMessage message = originalEvent.getMessage();
		
		// events are ordered so the event index corresponds to the index of each route
		List<Map<String, String>> listA = getAccountsList(muleEventsWithoutException, 0);
		List<Map<String, String>> listB = getAccountsList(muleEventsWithoutException, 1);

		SFDCOpportunitiesMerge sfdcOpportunityMerge = new SFDCOpportunitiesMerge();
		List<Map<String, String>> mergedOpportunityList = sfdcOpportunityMerge.mergeList(listA, listB);

		message.setPayload(mergedOpportunityList.iterator());

		return new DefaultMuleEvent(message, originalEvent);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, String>> getAccountsList(List<MuleEvent> events, int index) {
		Iterator<Map<String, String>> iterator = (Iterator<Map<String, String>>) events.get(index).getMessage().getPayload();
		return Lists.newArrayList(iterator);
	}

}
