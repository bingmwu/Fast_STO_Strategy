package com.bingmwu.prediction;

import com.bingmwu.data.DATA_PERIOD_TYPE;

public interface Prediction {
	public float getSuccessRate();
	public boolean isUp();
	public DATA_PERIOD_TYPE getTimePeriod();
}
