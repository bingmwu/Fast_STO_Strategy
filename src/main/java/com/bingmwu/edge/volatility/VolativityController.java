package com.bingmwu.edge.volatility;

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
import com.bingmwu.analyzer.AssetPriceStrengthAnalyzer;
import com.bingmwu.data.DataItem;
import com.bingmwu.data.FileSystemDataLoader;
import com.bingmwu.prediction.Prediction;
import com.bingmwu.prediction.SimpleRatePredictor;

@RestController
@RequestMapping(value = ApplicationMain.BASE_RELATIVE_API_URL + "/volativity")
@Validated
public class VolativityController {
	private static final Logger logger = LoggerFactory.getLogger(VolativityController.class);

	@RequestMapping(value = "/vxvbyvix", method = RequestMethod.GET)
	public Prediction getEdge(HttpServletRequest request,
			@RequestParam(value = "waterMark", required = true) float waterMark,
			@RequestParam(value = "rangeInDays", required = false, defaultValue = "3") int rangeInDays) {
		logger.info(" waterMark = {}, rangeInDays = {}", waterMark, rangeInDays);

		String vixInputDataFile = "C:\\dev-app\\genotick\\yahoo_vix_data\\vix_d.csv";
		FileSystemDataLoader vixDataLoader = new FileSystemDataLoader(vixInputDataFile);
		List<DataItem> vixDataList = vixDataLoader.loadData();

		String vxvInputDataFile = "C:\\dev-app\\genotick\\yahoo_vxv_data\\vxv_d.csv";
		FileSystemDataLoader vxvDataLoader = new FileSystemDataLoader(vxvInputDataFile);
		List<DataItem> vxvDataList = vxvDataLoader.loadData();

		String spyInputDataFile = "C:\\dev-app\\genotick\\yahoo_spy_data\\spy_for_vix_d.csv";
		FileSystemDataLoader spyDataLoader = new FileSystemDataLoader(spyInputDataFile);
		List<DataItem> spyDataList = spyDataLoader.loadData();

		AssetPriceStrengthAnalyzer analyzer = new AssetPriceStrengthAnalyzer();

		SimpleRatePredictor predictor = new SimpleRatePredictor();
		return predictor.predict(analyzer.analyze(vxvDataList, vixDataList), spyDataList, waterMark, rangeInDays);
	}

}
