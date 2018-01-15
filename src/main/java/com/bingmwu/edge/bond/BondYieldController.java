package com.bingmwu.edge.bond;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bingmwu.ApplicationMain;
import com.bingmwu.analyzer.BondPriceAnalyzer;
import com.bingmwu.data.DataItem;
import com.bingmwu.data.FileSystemDataLoader;
import com.bingmwu.prediction.BondSyncPredictor;
import com.bingmwu.prediction.Prediction;

@RestController
@RequestMapping(value = ApplicationMain.BASE_RELATIVE_API_URL + "/tlt")
@Validated
public class BondYieldController {
	private static final Logger logger = LoggerFactory.getLogger(BondYieldController.class);

	@RequestMapping(value = "", method = RequestMethod.GET)
	public Prediction getEdge(HttpServletRequest request,
			@RequestParam(value = "rangeInDays", required = false, defaultValue = "3") int rangeInDays) {
		logger.info("rangeInDays = {}", rangeInDays);

		String tltInputDataFile = "C:\\dev-app\\genotick\\yahoo_tlt_data\\tlt_d.csv";
		FileSystemDataLoader tltDataLoader = new FileSystemDataLoader(tltInputDataFile);
		List<DataItem> tltDataList = tltDataLoader.loadData();

		String spyInputDataFile = "C:\\dev-app\\genotick\\yahoo_spy_data\\spy_d.csv";
		FileSystemDataLoader spyDataLoader = new FileSystemDataLoader(spyInputDataFile);
		List<DataItem> spyDataList = spyDataLoader.loadData();

		BondPriceAnalyzer analyzer = new BondPriceAnalyzer();
		BondSyncPredictor predictor = new BondSyncPredictor();
		return predictor.predict(analyzer.analyze(tltDataList, spyDataList), spyDataList, rangeInDays);
	}

}
