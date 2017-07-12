package com.bingmwu.edge.sto.fast;

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
import com.bingmwu.analyzer.FastStochAnalyzer;
import com.bingmwu.data.DataCombo;
import com.bingmwu.data.FileSystemDataLoader;
import com.bingmwu.prediction.FastStochPredictor;
import com.bingmwu.prediction.Prediction;
import com.bingmwu.prediction.STOCK_MOVE_DIRECTION;

@RestController
@RequestMapping(value = ApplicationMain.BASE_RELATIVE_API_URL)
@Validated
public class FastStoController {
	private static final Logger logger = LoggerFactory.getLogger(FastStoController.class);

	@RequestMapping(value = "/{direction}", method = RequestMethod.GET)
	public Prediction getEdge(HttpServletRequest request, @PathVariable(value = "direction") String direction,
			@RequestParam(value = "waterMark", required = true) float waterMark,
			@RequestParam(value = "minimumK", required = false, defaultValue = "20.0f") float minimumK,
			@RequestParam(value = "rangeInDays", required = false, defaultValue = "3") int rangeInDays) {
		logger.info("direction = {}, minimumK = {}, rangeInDays = {}", direction, minimumK, rangeInDays);

		FileSystemDataLoader dataLoader = new FileSystemDataLoader("C:\\dev-app\\genotick\\yahoo_spx_data\\spx_d.csv");
		DataCombo dataCombo = dataLoader.loadData();
		return new FastStochAnalyzer(FastStochAnalyzer.STOCHASTIC_PERIOD_14_DAYS_BY_DEFAULT, dataCombo.dailyDataList)
				.analyze(new FastStochPredictor(
						("UP".equalsIgnoreCase(direction)) ? STOCK_MOVE_DIRECTION.UP : STOCK_MOVE_DIRECTION.DOWN,
						minimumK, rangeInDays, waterMark));
	}
}