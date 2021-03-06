package com.bingmwu.analyzer;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DataCombo;
import com.bingmwu.data.DataItem;
import com.bingmwu.data.FileSystemDataLoader;



public class StochasticAnalyzerTest {
	@Test
	public void testCalculateFastStock() {

		List<StochasticData> stochDataList = new StochAnalyzer(
				StochAnalyzer.STOCHASTIC_PERIOD_14_DAYS_BY_DEFAULT, prepareTestData()).calculateFastStock();
		
		Assert.assertTrue(stochDataList.get(13).percentageK > 70.54f && stochDataList.get(13).percentageK < 70.543f);
		Assert.assertTrue(stochDataList.get(29).percentageK > 56.76f && stochDataList.get(29).percentageK < 56.764f);
	}

	private List<DataItem> prepareTestData() {
		FileSystemDataLoader dataLoader = new FileSystemDataLoader("test/resources/fast_stochastic_test_data.csv");
		return dataLoader.loadData();
	}
}
