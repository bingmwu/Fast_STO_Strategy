package com.bingmwu.analyzer;

import com.bingmwu.prediction.Prediction;
import com.bingmwu.prediction.Predictor;

public interface Analyzer {
	public Prediction analyze(Predictor predictor);
}
