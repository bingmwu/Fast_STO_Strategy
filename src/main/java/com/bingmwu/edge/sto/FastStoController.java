package com.bingmwu.edge.sto;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bingmwu.ApplicationMain;
import com.bingmwu.analyzer.StochAnalyzer;
import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DATA_PERIOD_TYPE;
import com.bingmwu.data.DataItem;
import com.bingmwu.data.FileSystemDataLoader;
import com.bingmwu.prediction.FastStochCombinedPredictor;
import com.bingmwu.prediction.StochPredictor;
import com.bingmwu.prediction.Prediction;
import com.bingmwu.prediction.STOCK_MOVE_DIRECTION;

@RestController
@RequestMapping(value = ApplicationMain.BASE_RELATIVE_API_URL + "/faststoch")
@Validated
public class FastStoController {
	private static final Logger logger = LoggerFactory.getLogger(FastStoController.class);

	@RequestMapping(value = "/{direction}", method = RequestMethod.GET)
	public Prediction getEdge(HttpServletRequest request, @PathVariable(value = "direction") String direction,
			@RequestParam(value = "period", required = false, defaultValue = "DAYLY") DATA_PERIOD_TYPE period,
			@RequestParam(value = "waterMark", required = true) float waterMark,
			@RequestParam(value = "minimumK", required = false, defaultValue = "20.0f") float minimumK,
			@RequestParam(value = "rangeInDays", required = false, defaultValue = "3") int rangeInDays) {
		logger.info("direction = {}, period = {}, minimumK = {}, rangeInDays = {}", direction, period, minimumK,
				rangeInDays);

		String inputDataFile = (period == DATA_PERIOD_TYPE.DAYLY) ? "C:\\dev-app\\genotick\\yahoo_spx_data\\spx_d.csv"
				: "C:\\dev-app\\genotick\\yahoo_spx_weekly\\spx_w.csv";
		FileSystemDataLoader dataLoader = new FileSystemDataLoader(inputDataFile);
		List<DataItem> dataList = dataLoader.loadData();
		return new StochAnalyzer(StochAnalyzer.STOCHASTIC_PERIOD_14_DAYS_BY_DEFAULT, dataList)
				.analyzeByFastStoch(new StochPredictor(
						("UP".equalsIgnoreCase(direction)) ? STOCK_MOVE_DIRECTION.UP : STOCK_MOVE_DIRECTION.DOWN,
						minimumK, rangeInDays, waterMark));
	}

	/**
	 * Combined Daily with Weekly data, to have a better edge
	 * 
	 * @param request
	 * @param direction
	 * @param period
	 * @param waterMark
	 * @param minimumK
	 * @param rangeInDays
	 * @return
	 */
	@RequestMapping(value = "/combined/{direction}", method = RequestMethod.GET)
	public Prediction getCombinedEdge(HttpServletRequest request, @PathVariable(value = "direction") String direction,
			@RequestParam(value = "period", required = false, defaultValue = "WEEKLY") DATA_PERIOD_TYPE period,
			@RequestParam(value = "waterMark", required = true) float waterMark,
			@RequestParam(value = "minimumK", required = false, defaultValue = "20.0f") float minimumK,
			@RequestParam(value = "rangeInDays", required = false, defaultValue = "3") int rangeInDays) {
		logger.info("direction = {}, period = {}, minimumK = {}, rangeInDays = {}", direction, minimumK, rangeInDays);

		String dailyDataFile = "C:\\dev-app\\genotick\\yahoo_spx_data\\spx_d.csv";
		String combinedDataFile = null;
		if (period == DATA_PERIOD_TYPE.WEEKLY) {
			combinedDataFile = "C:\\dev-app\\genotick\\yahoo_spx_weekly\\spx_w.csv";
		} else {
			combinedDataFile = "C:\\dev-app\\genotick\\yahoo_spx_monthly\\spx_m.csv";
		}

		FileSystemDataLoader dailyDataLoader = new FileSystemDataLoader(dailyDataFile);
		List<DataItem> dailyDataList = dailyDataLoader.loadData();
		List<StochasticData> dailyFastStochDataList = new StochAnalyzer(
				StochAnalyzer.STOCHASTIC_PERIOD_14_DAYS_BY_DEFAULT, dailyDataList).prepareFastStochData();

		FileSystemDataLoader combinedDataLoader = new FileSystemDataLoader(combinedDataFile);
		List<DataItem> combinedDataList = combinedDataLoader.loadData();
		List<StochasticData> longerTermFastStochDataList = new StochAnalyzer(
				StochAnalyzer.STOCHASTIC_PERIOD_14_DAYS_BY_DEFAULT, combinedDataList).prepareFastStochData();

		return new FastStochCombinedPredictor(
				("UP".equalsIgnoreCase(direction)) ? STOCK_MOVE_DIRECTION.UP : STOCK_MOVE_DIRECTION.DOWN, period,
				minimumK, rangeInDays, waterMark).predict(dailyDataList, dailyFastStochDataList,
						longerTermFastStochDataList);
	}

}