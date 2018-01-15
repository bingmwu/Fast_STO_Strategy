package com.bingmwu.analyzer;

import java.util.ArrayList;
import java.util.List;

import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DataItem;
import com.bingmwu.prediction.Prediction;
import com.bingmwu.prediction.Predictor;

public class StochAnalyzer implements Analyzer {
	int daysInPeriod;
	List<DataItem> tradingDataList;

	public StochAnalyzer(int daysInPeriod, List<DataItem> tradingDataList) {
		this.daysInPeriod = daysInPeriod;
		this.tradingDataList = tradingDataList;
	}

	@Override
	public List<StochasticData> prepareFastStochData() {
		return calculateFastStock();
	}
	
	@Override
	public List<StochasticData> prepareSlowStochData() {
		return calculateSlowStock();
	}
	
	@Override
	public Prediction analyzeByFastStoch(Predictor predictor) {
		List<StochasticData> stochDataList = calculateFastStock();

		return predictor.predict(tradingDataList, stochDataList);
	}

	@Override
	public Prediction analyzeBySlowStoch(Predictor predictor) {
		List<StochasticData> stochDataList = calculateSlowStock();

		return predictor.predict(tradingDataList, stochDataList);
	}

	protected List<StochasticData> calculateFastStock() {
		List<StochasticData> stochDataList = calculateFastK();

		// calculate %D (3-day SMA of %K)
		for (int i = 2; i < stochDataList.size(); i++) {
			stochDataList.get(i).percentageD = (stochDataList.get(i - 2).percentageK
					+ stochDataList.get(i - 1).percentageK + stochDataList.get(i).percentageK) / 3;
		}
		
		return stochDataList;
	}

	protected List<StochasticData> calculateSlowStock() {
		List<StochasticData> stochDataList = calculateFastK();

		// calculate %K of slow Stoch (3-day SMA of %K)
		for (int i = 2; i < stochDataList.size(); i++) {
			stochDataList.get(i).percentageD = (stochDataList.get(i - 2).percentageK
					+ stochDataList.get(i - 1).percentageK + stochDataList.get(i).percentageK) / 3;
		}
		
		// copy %D value to %K, as the basic %k for slow stock;
		stochDataList.parallelStream().forEach(d -> d.percentageK = d.percentageD);
		
		// calculate %D (3-day SMA of slow %K)
		for (int i = 2; i < stochDataList.size(); i++) {
			stochDataList.get(i).percentageD = (stochDataList.get(i - 2).percentageK
					+ stochDataList.get(i - 1).percentageK + stochDataList.get(i).percentageK) / 3;
		}
		
		return stochDataList;
	}

	private List<StochasticData> calculateFastK() {
		List<StochasticData> stochDataList = new ArrayList<StochasticData>();
		
		// calculate %K
		for (int i = 0; i < this.tradingDataList.size(); i++) {
			StochasticData stochData = new StochasticData();
			if (i < this.daysInPeriod - 1) {
				stochDataList.add(stochData);
				continue;
			}
			
			HighLowInThePeriod howAndLowInThePeriod = findHighAndLowInThePeriod(tradingDataList, i);
			stochData.percentageK = (this.tradingDataList.get(i).close - howAndLowInThePeriod.lowest)
					/ (howAndLowInThePeriod.highest - howAndLowInThePeriod.lowest) * 100;

			stochData.date = this.tradingDataList.get(i).endDate;

			stochDataList.add(stochData);
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
