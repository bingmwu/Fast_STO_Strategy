package com.bingmwu.prediction;

import java.util.List;

import com.bingmwu.analyzer.data.PriceStrengthData;
import com.bingmwu.data.DataItem;

public class SimpleRatePredictor {
	public Prediction predict(List<PriceStrengthData> priceStrengthDataList, List<DataItem> tradingDataList,
			float waterMark, int dayRangeInPrediction) {
		// A quick sanity check: making sure the starting date is the same
		
		if (priceStrengthDataList.get(0).date.getYear() != tradingDataList.get(0).endDate.getYear()
				|| priceStrengthDataList.get(0).date.getDayOfYear() != tradingDataList.get(0).endDate.getDayOfYear()) {
			throw new IllegalStateException("The starting date of data mismatch!");
		}
		
		Prediction prediction = new Prediction();

		for (int i = dayRangeInPrediction - 1; i < priceStrengthDataList.size(); i++) {
			if (priceStrengthDataList.get(i - dayRangeInPrediction + 1).priceRate > waterMark) {
				// if (tradingDataList.get(i).close < tradingDataList.get(i -
				// dayRangeInPrediction + 1).close) {
				if (anyPriceDropInTheRange(tradingDataList, i - dayRangeInPrediction + 1, i + 1)) {
			    //if (anyPriceRiseInTheRange(tradingDataList, i - dayRangeInPrediction + 1, i + 1)) {
					prediction.successList
							.add(java.sql.Date.valueOf(priceStrengthDataList.get(i - dayRangeInPrediction + 1).date));
					prediction.numberOfSuccessPrediction++;
				} else {
					prediction.failedList
							.add(java.sql.Date.valueOf(priceStrengthDataList.get(i - dayRangeInPrediction + 1).date));
					prediction.numberOfFailedPrediction++;
				}
			}
		}
		prediction.successRate = 100.0f * prediction.numberOfSuccessPrediction
				/ (prediction.numberOfSuccessPrediction + prediction.numberOfFailedPrediction);

		return prediction;
	}

	private boolean anyPriceDropInTheRange(List<DataItem> tradingDataList, int startingDayIndex, int endDayIndex) {
		if (endDayIndex >= tradingDataList.size()) {
			System.out.println("endDayIndex exceeds data size: " + endDayIndex);
			endDayIndex = tradingDataList.size() - 1;
		}
		for (int i = startingDayIndex + 1; i <= endDayIndex; i++) {
			if (tradingDataList.get(startingDayIndex).close > tradingDataList.get(i).close) {
				return true;
			}
		}
		return false;
	}
	
	private boolean anyPriceRiseInTheRange(List<DataItem> tradingDataList, int startingDayIndex, int endDayIndex) {
		if (endDayIndex >= tradingDataList.size()) {
			System.out.println("endDayIndex exceeds data size: " + endDayIndex);
			endDayIndex = tradingDataList.size() - 1;
		}
		for (int i = startingDayIndex + 1; i <= endDayIndex; i++) {
			if (tradingDataList.get(startingDayIndex).close < tradingDataList.get(i).close) {
				return true;
			}
		}
		return false;
	}
}
