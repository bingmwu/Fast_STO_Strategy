package com.bingmwu.edge.sto.fast;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bingmwu.ApplicationMain;
import com.bingmwu.analyzer.FastStochAnalyzer;
import com.bingmwu.analyzer.data.StochasticData;
import com.bingmwu.data.DataCombo;
import com.bingmwu.data.FileSystemDataLoader;

@RestController
@RequestMapping(value = ApplicationMain.BASE_RELATIVE_API_URL)
@Validated
public class FastStoController {
	private static final Logger logger = LoggerFactory.getLogger(FastStoController.class);

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getEdge(HttpServletRequest request, @PathVariable(value = "id") int id) {
		logger.info("intel={}", id);

		FileSystemDataLoader dataLoader = new FileSystemDataLoader("C:\\dev-app\\genotick\\yahoo_spx_data\\spx_d.csv");
		DataCombo dataCombo = dataLoader.loadData();
		new FastStochAnalyzer(
				FastStochAnalyzer.STOCHASTIC_PERIOD_14_DAYS_BY_DEFAULT, dataCombo.dailyDataList).analyze();
		return "test";

	}
}