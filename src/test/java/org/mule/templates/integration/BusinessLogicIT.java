/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.templates.builders.SfdcObjectBuilder;

import com.google.common.collect.Lists;
import com.sforce.soap.partner.SaveResult;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Anypoint Template that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	protected static final String TEMPLATE_NAME = "opportunity-aggregation";
	private static List<Map<String, Object>> createdOpportunitiesInA = new ArrayList<Map<String, Object>>();
	private static List<Map<String, Object>> createdOpportunitiesInB = new ArrayList<Map<String, Object>>();

	@Rule
	public DynamicPort port = new DynamicPort("http.port");

	@Before
	public void setUp() throws Exception {
		createTestOpportunitiesInSandBox();
	}

	@After
	public void tearDown() throws Exception {
		deleteTestOpportunitiesFromSandBox(createdOpportunitiesInA, "deleteOpportunityFromAFlow");
		deleteTestOpportunitiesFromSandBox(createdOpportunitiesInB, "deleteOpportunityFromBFlow");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGatherDataFlow() throws Exception {
		MuleEvent event = runFlow("gatherDataFlow");
		List<Map<String, String>> mergedOpportunityList = Lists.newArrayList((Iterator<Map<String, String>>)event.getMessage().getPayload());
		Assert.assertTrue("There should be opportunities from source A or source B.", mergedOpportunityList.size() != 0);
	}

	@Test
	public void testMainFlow() throws Exception {
		MuleEvent event = runFlow("mainFlow");

		Assert.assertTrue("The payload should not be null.", "Please find attached your Opportunities Report".equals(event.getMessage().getPayload()));
	}

	@SuppressWarnings("unchecked")
	private void createTestOpportunitiesInSandBox() throws Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("createOpportunityInAFlow");
		flow.initialise();

		Map<String, Object> opportunity = createOpportunity("A", 0);
		createdOpportunitiesInA.add(opportunity);

		MuleEvent event = flow.process(getTestEvent(createdOpportunitiesInA, MessageExchangePattern.REQUEST_RESPONSE));
		List<SaveResult> results = (List<SaveResult>) event.getMessage().getPayload();
		for (int i = 0; i < results.size(); i++) {
			createdOpportunitiesInA.get(i).put("Id", results.get(i).getId());
		}

		flow = getSubFlow("createOpportunityInBFlow");
		flow.initialise();

		opportunity = createOpportunity("B", 0);
		createdOpportunitiesInB.add(opportunity);

		event = flow.process(getTestEvent(createdOpportunitiesInB, MessageExchangePattern.REQUEST_RESPONSE));
		results = (List<SaveResult>) event.getMessage().getPayload();

		for (int i = 0; i < results.size(); i++) {
			createdOpportunitiesInB.get(i).put("Id", results.get(i).getId());
		}
	}

	private void deleteTestOpportunitiesFromSandBox(List<Map<String, Object>> createdOpportunities, String deleteFlow) throws Exception {
		List<String> idList = new ArrayList<String>();

		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow(deleteFlow);
		flow.initialise();
		for (Map<String, Object> c : createdOpportunities) {
			idList.add((String) c.get("Id"));
		}
		flow.process(getTestEvent(idList, MessageExchangePattern.REQUEST_RESPONSE));
		idList.clear();
	}

	private Map<String, Object> createOpportunity(String orgId, int sequence) {
		// fields Name, StageName and CloseDate are required in SalesForce
		Map<String, Object> opportunity = SfdcObjectBuilder.anOpportunity()
				.with("Name", buildUniqueName(TEMPLATE_NAME, "SomeName_" + orgId + sequence + "_"))
				.with("Amount", "154512.35")
				.with("StageName", "Qualification")
				.with("CloseDate", Calendar.getInstance().getTime())
				.build();
		return opportunity;
	}
	
	private String buildUniqueName(String templateName, String name) {
		String timeStamp = new Long(new Date().getTime()).toString();

		StringBuilder builder = new StringBuilder();
		builder.append(name);
		builder.append(templateName);
		builder.append(timeStamp);

		return builder.toString();
	}

}
