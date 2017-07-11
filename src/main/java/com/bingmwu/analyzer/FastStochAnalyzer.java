package com.bingmwu.analyzer;

import java.util.ArrayList;
import java.util.List;

import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DataItem;
import com.bingmwu.prediction.Prediction;

public class FastStochAnalyzer implements Analyzer {
	int daysInPeriod;
	List<DataItem> tradingDataList;

	public FastStochAnalyzer(int daysInPeriod, List<DataItem> tradingDataList) {
		this.daysInPeriod = daysInPeriod;
		this.tradingDataList = tradingDataList;
	}

	@Override
	public Prediction analyze() {
		List<StochasticData> stochDataList = calculateFastStock();

		return null;
	}

	protected List<StochasticData> calculateFastStock() {
		List<StochasticData> stochDataList = new ArrayList<StochasticData>();

		// calculate %K
		for (int i = this.daysInPeriod - 1; i < this.tradingDataList.size(); i++) {
			HighLowInThePeriod howAndLowInThePeriod = findHighAndLowInThePeriod(tradingDataList, i);
			StochasticData stochData = new StochasticData();
			stochData.percentageK = (this.tradingDataList.get(i).close - howAndLowInThePeriod.lowest)
					/ (howAndLowInThePeriod.highest - howAndLowInThePeriod.lowest) * 100;

			stochData.date = this.tradingDataList.get(i).endDate;

			stochDataList.add(stochData);
		}

		// calculate %D (3-day SMA of %K)
		for (int i = 2; i < stochDataList.size(); i++) {
			stochDataList.get(i).percentageD = (stochDataList.get(i - 2).percentageK
					+ stochDataList.get(i - 1).percentageK + stochDataList.get(i).percentageK) / 3;
		}
		
		return stochDataList;
	}

	private HighLowInThePeriod findHighAndLowInThePeriod(List<DataItem> dataItemList, int endDayOfPeriod) {
		HighLowInThePeriod highAndLow = new HighLowInThePeriod();
		int beginDayOfPeriod = endDayOfPeriod - this.daysInPeriod + 1;
		highAndLow.highest = dataItemList.get(beginDayOfPeriod).high;
		highAndLow.lowest = dataItemList.get(beginDayOfPeriod).low;
		for (int i = beginDayOfPeriod + 1; i <= endDayOfPeriod; i++) {
			if (highAndLow.highest < dataItemList.get(i).high) {
				highAndLow.highest = dataItemList.get(i).high;
			}
			if (highAndLow.lowest > dataItemList.get(i).low) {
				highAndLow.lowest = dataItemList.get(i).low;
			}
		}
		return highAndLow;
	}

	private class HighLowInThePeriod {
		float highest;
		float lowest;
	}

	public static final int STOCHASTIC_PERIOD_14_DAYS_BY_DEFAULT = 14;
}
