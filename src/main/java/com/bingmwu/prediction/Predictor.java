package com.bingmwu.prediction;

import java.util.List;

import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DATA_PERIOD_TYPE;
import com.bingmwu.data.DataItem;

public interface Predictor {
	public STOCK_MOVE_DIRECTION getDirection();

	public DATA_PERIOD_TYPE getTimePeriod();

	public Prediction predict(List<DataItem> tradingDataList, List<StochasticData> stochDataList);
}
