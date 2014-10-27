/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The object of this class will take two lists as input and create a third one that
 * will be the merge of the previous two. The identity of list's element is
 * defined by its Name.
 * 
 * @author damian.sima
 */
public class SFDCOpportunitiesMerge {
	private static final String IDENTITY_FIELD_KEY = "Name";

	/**
	 * The method will merge the accounts from the two lists creating a new one.
	 * 
	 * @param opportunitiesFromOrgA
	 *            opportunities from organization A
	 * @param opportunitiesFromOrgB
	 *            opportunities from organization B
	 * @return a list with the merged content of the to input lists
	 */
	public List<Map<String, String>> mergeList(List<Map<String, String>> opportunitiesFromOrgA, List<Map<String, String>> opportunitiesFromOrgB) {
		List<Map<String, String>> mergedOpportunityList = new ArrayList<Map<String, String>>();

		// Put all opportunities from A in the merged opportunityList
		for (Map<String, String> opportunityFromA : opportunitiesFromOrgA) {
			Map<String, String> mergedOpportunity = createMergedOpportunity(opportunityFromA);
			mergedOpportunity.put("IDInA", opportunityFromA.get("Id"));
			mergedOpportunity.put("AmountInA", opportunityFromA.get("Amount"));
			mergedOpportunityList.add(mergedOpportunity);
		}

		// Add the new opportunities from B and update the exiting ones
		for (Map<String, String> opportunityFromB : opportunitiesFromOrgB) {
			Map<String, String> mergedOpportunity = findOpportunityInList(opportunityFromB, mergedOpportunityList);
			if (mergedOpportunity != null) {
				mergedOpportunity.put("IDInB", opportunityFromB.get("Id"));
				mergedOpportunity.put("AmountInB", opportunityFromB.get("Amount"));
			} else {
				mergedOpportunity = createMergedOpportunity(opportunityFromB);
				mergedOpportunity.put("IDInB", opportunityFromB.get("Id"));
				mergedOpportunity.put("AmountInB", opportunityFromB.get("Amount"));
				mergedOpportunityList.add(mergedOpportunity);
			}

		}
		return mergedOpportunityList;
	}

	private Map<String, String> createMergedOpportunity(Map<String, String> opportunity) {
		Map<String, String> mergedOpportunity = new HashMap<String, String>();
		mergedOpportunity.put("Name", opportunity.get("Name"));
		mergedOpportunity.put("IDInA", "");
		mergedOpportunity.put("AmountInA", "");
		mergedOpportunity.put("IDInB", "");
		mergedOpportunity.put("AmountInB", "");
		return mergedOpportunity;
	}

	private Map<String, String> findOpportunityInList(Map<String, String> opportunityToLookup, List<Map<String, String>> orgList) {
		for (Map<String, String> opportunity : orgList) {
			if (opportunity.get(IDENTITY_FIELD_KEY).equals(opportunityToLookup.get(IDENTITY_FIELD_KEY))) {
				return opportunity;
			}
		}
		return null;
	}
}
