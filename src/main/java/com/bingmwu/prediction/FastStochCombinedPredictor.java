package com.bingmwu.prediction;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DATA_PERIOD_TYPE;
import com.bingmwu.data.DataItem;

/**
 * Combined Daily And Weekly Fast Stoch, for better prediction
 */
public class FastStochCombinedPredictor implements Predictor {
	STOCK_MOVE_DIRECTION direction;
	DATA_PERIOD_TYPE longerTermPeriod;
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

	public FastStochCombinedPredictor(STOCK_MOVE_DIRECTION direction, DATA_PERIOD_TYPE period, float percentageK,
			int dayRangeInPrediction, float waterMark) {
		this.direction = direction;
		this.longerTermPeriod = period;
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
		return null;
	}

	@Override
	public Prediction predict(List<DataItem> tradingDataList, List<StochasticData> dailyStochDataList,
			List<StochasticData> weeklyStochDataList) {
		Prediction prediction = new Prediction();

		Map<Long, StochasticData> weeklyStochDataMap = new HashMap<Long, StochasticData>();

		weeklyStochDataList.parallelStream().forEach(weeklyData -> {
			if (weeklyData.date != null)
				weeklyStochDataMap.put(weeklyData.date.getLong(ChronoField.EPOCH_DAY), weeklyData);
		});

		for (int i = dayRangeInPrediction - 1; i < dailyStochDataList.size(); i++) {
			if (dailyStochDataList.get(i).date == null) {
				// No stoch being calculated yet at the begining
				continue;
			}

			if (direction == STOCK_MOVE_DIRECTION.UP && this.percentageK < dailyStochDataList.get(i).percentageK) {
				if ((dailyStochDataList.get(i).percentageK <= dailyStochDataList.get(i).percentageD
						|| dailyStochDataList.get(i).percentageK > this.waterMark)
						&& isCombinedStochInSync(weeklyStochDataMap, dailyStochDataList.get(i).date)
						&& i < dailyStochDataList.size() - dayRangeInPrediction + 1) {
					if (tradingDataList.get(i).close < tradingDataList.get(i + dayRangeInPrediction - 1).close) {
						prediction.successList.add(java.sql.Date.valueOf(dailyStochDataList.get(i).date));
						prediction.numberOfSuccessPrediction++;
					} else {
						prediction.failedList.add(java.sql.Date.valueOf(dailyStochDataList.get(i).date));
						prediction.numberOfFailedPrediction++;
					}
				}
			} else if (direction == STOCK_MOVE_DIRECTION.DOWN
					&& this.percentageK < dailyStochDataList.get(i).percentageK) {
				if ((dailyStochDataList.get(i).percentageK <= dailyStochDataList.get(i).percentageD
						|| dailyStochDataList.get(i).percentageK < this.waterMark)
						&& isCombinedStochInSync(weeklyStochDataMap, dailyStochDataList.get(i).date)
						&& i < dailyStochDataList.size() - dayRangeInPrediction + 1) {
					if (tradingDataList.get(i).close > tradingDataList.get(i + dayRangeInPrediction - 1).close) {
						prediction.successList.add(java.sql.Date.valueOf(dailyStochDataList.get(i).date));
						prediction.numberOfSuccessPrediction++;
					} else {
						prediction.failedList.add(java.sql.Date.valueOf(dailyStochDataList.get(i).date));
						prediction.numberOfFailedPrediction++;
					}
				}
			}
		}
		prediction.successRate = 100.0f * prediction.numberOfSuccessPrediction
				/ (prediction.numberOfSuccessPrediction + prediction.numberOfFailedPrediction);

		return prediction;
	}

	// When trending up, weekly stock is in sync with daily, if %K > %D for both
	// when trending down, weekly stock is in sync with daily, if %K < %D for
	// both
	private boolean isCombinedStochInSync(Map<Long, StochasticData> weeklyStochDataMap, LocalDate tradingDate) {
		LocalDate firstDayOfLongerPeriod = (longerTermPeriod == DATA_PERIOD_TYPE.WEEKLY)
				? tradingDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
				: tradingDate.with(TemporalAdjusters.firstDayOfMonth());
		StochasticData weeklyStochData = weeklyStochDataMap.get(firstDayOfLongerPeriod.getLong(ChronoField.EPOCH_DAY));

		if (weeklyStochData == null) {
			// should be the beginning weeks when the weekly stoch data are not
			// ready
			System.out.println(firstDayOfLongerPeriod);
			return false;
		}

		if ((weeklyStochData.percentageK > weeklyStochData.percentageD || weeklyStochData.percentageK >= waterMark)
				&& this.direction == STOCK_MOVE_DIRECTION.UP) {
			return true;
		} else if ((weeklyStochData.percentageK < weeklyStochData.percentageD
				|| weeklyStochData.percentageK <= waterMark) && this.direction == STOCK_MOVE_DIRECTION.DOWN) {
			return true;
		} else {
			return false;
		}
	}
}
