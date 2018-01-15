package com.bingmwu.prediction;

import java.util.List;

import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DATA_PERIOD_TYPE;
import com.bingmwu.data.DataItem;

public class StochPredictor implements Predictor {
	STOCK_MOVE_DIRECTION direction;
	// the lowest value of %K indicator when predicting UP,
	// or the highest value of %K indicator when predicting DOWN
	float percentageK;
	// number of days in the prediction, for example, the stock moves higher
	// after 2 days (3 day range)
	int dayRangeInPrediction;
	// For UP prediction, if %K > waterMark, count the day for prediction, even
	// if %D > %K
	// For DOWN prediction, if %K < waterMark, count the day for prediction,
	// even if %D < %K
	float waterMark;

	public StochPredictor(STOCK_MOVE_DIRECTION direction, float percentageK, int dayRangeInPrediction,
			float waterMark) {
		this.direction = direction;
		this.percentageK = percentageK;
		this.dayRangeInPrediction = dayRangeInPrediction;
		this.waterMark = waterMark;
	}

	@Override
	public STOCK_MOVE_DIRECTION getDirection() {
		return this.direction;
	}

	@Override
	public DATA_PERIOD_TYPE getTimePeriod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Prediction predict(List<DataItem> tradingDataList, List<StochasticData> stochDataList) {
		Prediction prediction = new Prediction();

		for (int i = dayRangeInPrediction - 1; i < stochDataList.size(); i++) {
			if (direction == STOCK_MOVE_DIRECTION.UP && this.percentageK < stochDataList.get(i).percentageK) {
				if ((stochDataList.get(i).percentageK >= stochDataList.get(i).percentageD
						|| stochDataList.get(i).percentageK > this.waterMark)
						&& i < stochDataList.size() - dayRangeInPrediction + 1) {
					if (tradingDataList.get(i).close < tradingDataList.get(i + dayRangeInPrediction - 1).close) {
						prediction.successList.add(java.sql.Date.valueOf(stochDataList.get(i).date));
						prediction.numberOfSuccessPrediction++;
					} else {
						prediction.failedList.add(java.sql.Date.valueOf(stochDataList.get(i).date));
						prediction.numberOfFailedPrediction++;
					}
				}
			} else if (direction == STOCK_MOVE_DIRECTION.DOWN && this.percentageK > stochDataList.get(i).percentageK) {
				if ((stochDataList.get(i).percentageK <= stochDataList.get(i).percentageD
						|| stochDataList.get(i).percentageK < this.waterMark)
						&& i < stochDataList.size() - dayRangeInPrediction + 1) {
					if (tradingDataList.get(i).close > tradingDataList.get(i + dayRangeInPrediction - 1).close) {
						prediction.successList.add(java.sql.Date.valueOf(stochDataList.get(i).date));
						prediction.numberOfSuccessPrediction++;
					} else {
						prediction.failedList.add(java.sql.Date.valueOf(stochDataList.get(i).date));
						prediction.numberOfFailedPrediction++;
					}
				}
			}
		}
		prediction.successRate = 100.0f * prediction.numberOfSuccessPrediction
				/ (prediction.numberOfSuccessPrediction + prediction.numberOfFailedPrediction);

		return prediction;
	}

	@Override
	public Prediction predict(List<DataItem> tradingDataList, List<StochasticData> dailyStochDataList,
			List<StochasticData> weeklyStochDataList) {
		// TODO Auto-generated method stub
		return null;
	}
}
