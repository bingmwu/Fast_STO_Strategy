package com.bingmwu.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bingmwu.analyzer.data.PriceStrengthData;
import com.bingmwu.data.DataItem;

public class AssetPriceStrengthAnalyzer {
	private static final Logger logger = LoggerFactory.getLogger(AssetPriceStrengthAnalyzer.class);

	public List<PriceStrengthData> analyze(List<DataItem> assetData1, List<DataItem> assetData2) {
		List<PriceStrengthData> priceStrengthDataList = new ArrayList<PriceStrengthData>();

		if (assetData1.size() != assetData2.size()) {
			throw new IllegalArgumentException("Asset price data don't match in size!");
		}

		for (int i = 0; i < assetData1.size(); i++) {
			if (assetData2.get(i).close == 0.f) {
				logger.error("Asset price data on date {} cannot be zero!", assetData2.get(i).endDate);
				throw new IllegalArgumentException("Asset price data cannot be zero!");
			}

			PriceStrengthData priceStrengthData = new PriceStrengthData();
			priceStrengthData.date = assetData1.get(i).endDate;
			priceStrengthData.priceRate = assetData1.get(i).close / assetData2.get(i).close;

			priceStrengthDataList.add(priceStrengthData);
		}
		return priceStrengthDataList;
	}
}
