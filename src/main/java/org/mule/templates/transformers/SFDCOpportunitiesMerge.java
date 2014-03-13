package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.google.common.collect.Lists;

/**
 * This transformer will take two lists as input and create a third one that
 * will be the merge of the previous two. The identity of list's element is
 * defined by its Name.
 * 
 * @author damian.sima
 */
public class SFDCOpportunitiesMerge extends AbstractMessageTransformer {
	private static final String IDENTITY_FIELD_KEY = "Name";

	private static final String OPPORTUNITIES_COMPANY_A = "opportunitiesFromOrgA";
	private static final String OPPORTUNITIES_COMPANY_B = "opportunitiesFromOrgB";

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		List<Map<String, String>> mergedUsersList = mergeList(getAccountsList(message, OPPORTUNITIES_COMPANY_A), getAccountsList(message, OPPORTUNITIES_COMPANY_B));

		return mergedUsersList;
	}

	private List<Map<String, String>> getAccountsList(MuleMessage message, String propertyName) {
		Iterator<Map<String, String>> iterator = message.getInvocationProperty(propertyName);
		return Lists.newArrayList(iterator);
	}

	/**
	 * The method will merge the accounts from the two lists creating a new one.
	 * 
	 * @param opportunitiesFromOrgA
	 *            opportunities from organization A
	 * @param opportunitiesFromOrgB
	 *            opportunities from organization B
	 * @return a list with the merged content of the to input lists
	 */
	private List<Map<String, String>> mergeList(List<Map<String, String>> opportunitiesFromOrgA, List<Map<String, String>> opportunitiesFromOrgB) {
		List<Map<String, String>> mergedAccountList = new ArrayList<Map<String, String>>();

		// Put all accounts from A in the merged contactList
		for (Map<String, String> accountFromA : opportunitiesFromOrgA) {
			Map<String, String> mergedAccount = createMergedOpportunity(accountFromA);
			mergedAccount.put("IDInA", accountFromA.get("Id"));
//			mergedAccount.put("NameInA", accountFromA.get("Name"));
			mergedAccount.put("AmountInA", accountFromA.get("Amount"));
			mergedAccountList.add(mergedAccount);
		}

		// Add the new accounts from B and update the exiting ones
		for (Map<String, String> opportunityFromB : opportunitiesFromOrgB) {
			Map<String, String> mergedAccount = findOpportunityInList(opportunityFromB, mergedAccountList);
			if (mergedAccount != null) {
				mergedAccount.put("IDInB", opportunityFromB.get("Id"));
//				mergedAccount.put("NameInB", opportunityFromB.get("Name"));
				mergedAccount.put("AmountInB", opportunityFromB.get("Amount"));
			} else {
				mergedAccount = createMergedOpportunity(opportunityFromB);
				mergedAccount.put("IDInB", opportunityFromB.get("Id"));
//				mergedAccount.put("NameInB", opportunityFromB.get("Name"));
				mergedAccount.put("AmountInB", opportunityFromB.get("Amount"));
				mergedAccountList.add(mergedAccount);
			}

		}
		return mergedAccountList;
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
