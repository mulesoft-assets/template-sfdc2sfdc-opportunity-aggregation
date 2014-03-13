package org.mule.templates.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;

@RunWith(MockitoJUnitRunner.class)
public class SFDCOpportunitiesMergeTest {
	private static final String OPPORTUNITIES_COMPANY_A = "opportunitiesFromOrgA";
	private static final String OPPORTUNITIES_COMPANY_B = "opportunitiesFromOrgB";

	@Mock
	private MuleContext muleContext;

	@Test
	@SuppressWarnings("unchecked")
	public void testMerge() throws TransformerException {
		List<Map<String, String>> opportunitiesA = createOpportunityList("A", 0, 1);
		List<Map<String, String>> opportunitiesB = createOpportunityList("B", 1, 2);

		MuleMessage message = new DefaultMuleMessage(null, muleContext);
		message.setInvocationProperty(OPPORTUNITIES_COMPANY_A, opportunitiesA.iterator());
		message.setInvocationProperty(OPPORTUNITIES_COMPANY_B, opportunitiesB.iterator());

		SFDCOpportunitiesMerge transformer = new SFDCOpportunitiesMerge();
		List<Map<String, String>> mergedList = (List<Map<String, String>>) transformer.transform(message, "UTF-8");

		System.out.println(opportunitiesA);
		System.out.println(opportunitiesB);
		System.out.println(mergedList);

		Assert.assertEquals("The merged list obtained is not as expected", createExpectedList(), mergedList);
	}

	private List<Map<String, String>> createExpectedList() {

		Map<String, String> record0 = createEmptyMergedRecord(0);
		record0.put("IDInA", "0");
		record0.put("Name", "SomeName_0");
		record0.put("AmountInA", "500");

		Map<String, String> record1 = createEmptyMergedRecord(1);
		record1.put("IDInA", "1");
		record1.put("Name", "SomeName_1");
		record1.put("AmountInA", "500");
		record1.put("IDInB", "1");
		record1.put("AmountInB", "500");

		Map<String, String> record2 = createEmptyMergedRecord(2);
		record2.put("IDInB", "2");
		record2.put("Name", "SomeName_2");
		record2.put("AmountInB", "500");

		List<Map<String, String>> expectedList = new ArrayList<Map<String, String>>();
		expectedList.add(record0);
		expectedList.add(record1);
		expectedList.add(record2);

		return expectedList;
	}

	private Map<String, String> createEmptyMergedRecord(Integer secuense) {
		Map<String, String> opportunity = new HashMap<String, String>();
		opportunity.put("Name", "SomeName_" + secuense);
		opportunity.put("IDInA", "");
		opportunity.put("AmountInA", "");
		opportunity.put("IDInB", "");
		opportunity.put("AmountInB", "");
		return opportunity;

	}

	private List<Map<String, String>> createOpportunityList(String orgId, int start, int end) {
		List<Map<String, String>> userList = new ArrayList<Map<String, String>>();
		for (int i = start; i <= end; i++) {
			userList.add(createOpportunity(orgId, i));
		}
		return userList;
	}

	private Map<String, String> createOpportunity(String orgId, int sequence) {
		Map<String, String> account = new HashMap<String, String>();

		account.put("Id", new Integer(sequence).toString());
		account.put("Name", "SomeName_" + sequence);
		account.put("Amount", "500");

		return account;
	}
}
