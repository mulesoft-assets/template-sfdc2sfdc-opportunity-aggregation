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

/**
 * The test validates that the {@link SortOpportunitiesList} properly order a
 * list of maps based on it internal criteria.
 * 
 * @author damiansima
 */
@RunWith(MockitoJUnitRunner.class)
public class SortOpportunitiesListTest {

	@Mock
	private MuleContext muleContext;

	@Test
	@SuppressWarnings("unchecked")
	public void testSort() throws TransformerException {
		List<Map<String, String>> originalList = createOriginalList();
		MuleMessage message = new DefaultMuleMessage(originalList, muleContext);

		SortOpportunitiesList transformer = new SortOpportunitiesList();
		List<Map<String, String>> sortedList = (List<Map<String, String>>) transformer.transform(message, "UTF-8");

		Assert.assertEquals("The merged list obtained is not as expected", createExpectedList(), sortedList);

	}

	private List<Map<String, String>> createExpectedList() {
		Map<String, String> record0 = createEmptyMergedRecord(0);
		record0.put("IDInA", "0");

		Map<String, String> record1 = createEmptyMergedRecord(1);
		record1.put("IDInA", "1");
		record1.put("IDInB", "1");

		Map<String, String> record2 = createEmptyMergedRecord(2);
		record2.put("IDInB", "2");

		Map<String, String> record3 = createEmptyMergedRecord(3);
		record3.put("IDInA", "3");
		record3.put("IDInB", "3");

		List<Map<String, String>> expectedMergedList = new ArrayList<Map<String, String>>();
		expectedMergedList.add(record0);
		expectedMergedList.add(record2);
		expectedMergedList.add(record1);
		expectedMergedList.add(record3);

		return expectedMergedList;
	}

	private List<Map<String, String>> createOriginalList() {
		Map<String, String> record0 = createEmptyMergedRecord(0);
		record0.put("IDInA", "0");

		Map<String, String> record1 = createEmptyMergedRecord(1);
		record1.put("IDInA", "1");
		record1.put("IDInB", "1");

		Map<String, String> record2 = createEmptyMergedRecord(2);
		record2.put("IDInB", "2");

		Map<String, String> record3 = createEmptyMergedRecord(3);
		record3.put("IDInA", "3");
		record3.put("IDInB", "3");

		List<Map<String, String>> recordList = new ArrayList<Map<String, String>>();
		recordList.add(record0);
		recordList.add(record1);
		recordList.add(record2);
		recordList.add(record3);

		return recordList;
	}

	private Map<String, String> createEmptyMergedRecord(Integer secuense) {
		Map<String, String> mergedRecord = new HashMap<String, String>();
		mergedRecord.put("Name", "SomeName_" + secuense);
		mergedRecord.put("IDInA", "");
		mergedRecord.put("AmountInA", "");
		mergedRecord.put("IDInB", "");
		mergedRecord.put("AmountInB", "");

		return mergedRecord;
	}

}
