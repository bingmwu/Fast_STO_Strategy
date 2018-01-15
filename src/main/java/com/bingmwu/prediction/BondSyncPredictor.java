package com.bingmwu.prediction;

import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bingmwu.analyzer.data.BondSyncData;
import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DataItem;

public class BondSyncPredictor {
	public Prediction predict(List<BondSyncData> bondSyncDataList, List<DataItem> tradingDataList,
			int dayRangeInPrediction) {
		Prediction prediction = new Prediction();

		// map to help quickly find the trading data based on date
		Map<Long, Integer> tradingDataMap = new HashMap<Long, Integer>();
		for (int i = 0; i < tradingDataList.size(); i++) {
			tradingDataMap.put(tradingDataList.get(i).endDate.getLong(ChronoField.EPOCH_DAY), i);
		};

		for (int i = 0; i < bondSyncDataList.size(); i++) {
			if (!bondSyncDataList.get(i).isBothUp) {
				// skip it, only want to analyze both up
				continue;
			}
			int tradingDayIndex = tradingDataMap.get(bondSyncDataList.get(i).date.getLong(ChronoField.EPOCH_DAY));
			if (tradingDayIndex + dayRangeInPrediction - 1 > tradingDataList.size() - 1) {
				// out of trading day range
				break;
			}
			//if ((bondSyncDataList.get(i).isBothUp
			//		&& tradingDataList.get(tradingDayIndex + dayRangeInPrediction - 1).close >= tradingDataList
			//				.get(tradingDayIndex).close)
			//		|| (!bondSyncDataList.get(i).isBothUp
			//				&& tradingDataList.get(tradingDayIndex + dayRangeInPrediction - 1).close <= tradingDataList
			//						.get(tradingDayIndex).close)) {
				 if (anySynchronizedPriceMoveInTheRange(tradingDataList, tradingDayIndex , tradingDayIndex + dayRangeInPrediction,
				 bondSyncDataList.get(i).isBothUp))
				 {
				prediction.successList
						.add(java.sql.Date.valueOf(bondSyncDataList.get(i).date));
				prediction.numberOfSuccessPrediction++;
			} else {
				prediction.failedList
						.add(java.sql.Date.valueOf(bondSyncDataList.get(i).date));
				prediction.numberOfFailedPrediction++;
			}

		}
		prediction.successRate = 100.0f * prediction.numberOfSuccessPrediction
				/ (prediction.numberOfSuccessPrediction + prediction.numberOfFailedPrediction);

		return prediction;
	}

	private boolean anySynchronizedPriceMoveInTheRange(List<DataItem> tradingDataList, int startingDayIndex,
			int endDayIndex, boolean isUp) {
		if (endDayIndex >= tradingDataList.size()) {
			System.out.println("endDayIndex exceeds data size: " + endDayIndex);
			endDayIndex = tradingDataList.size() - 1;
		}
		for (int i = startingDayIndex + 1; i <= endDayIndex; i++) {
			if ((isUp && tradingDataList.get(startingDayIndex).close < tradingDataList.get(i).close)
					|| (!isUp && tradingDataList.get(startingDayIndex).close > tradingDataList.get(i).close)) {
				return true;
			}
		}
		return false;
	}
}
