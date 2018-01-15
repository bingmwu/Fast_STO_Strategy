package com.bingmwu.analyzer;

import java.util.List;

import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.prediction.Prediction;
import com.bingmwu.prediction.Predictor;

public interface Analyzer {
	public List<StochasticData> prepareFastStochData();

	public List<StochasticData> prepareSlowStochData();

	public Prediction analyzeByFastStoch(Predictor predictor);
	
	public Prediction analyzeBySlowStoch(Predictor predictor);
}
