package com.bingmwu.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bingmwu.analyzer.data.BondSyncData;
import com.bingmwu.data.DataItem;

public class BondPriceAnalyzer {
	private static final Logger logger = LoggerFactory.getLogger(BondPriceAnalyzer.class);

	public List<BondSyncData> analyze(List<DataItem> bondData, List<DataItem> referenceData) {
		List<BondSyncData> bondSyncDataList = new ArrayList<BondSyncData>();

		if (bondData.size() != referenceData.size()) {
			throw new IllegalArgumentException("Asset price data don't match in size!");
		}

		for (int i = 1; i < bondData.size(); i++) {
			// bond price and reference data (usually SPY) moves in the same
			// direction (both UP or DOWN)
			if (bondData.get(i).close > bondData.get(i - 1).close
					&& referenceData.get(i).close > referenceData.get(i - 1).close) {
				BondSyncData syncData = new BondSyncData();
				syncData.date = bondData.get(i).endDate;
				syncData.isBothUp = true;
				bondSyncDataList.add(syncData);
			} else if (bondData.get(i).close < bondData.get(i - 1).close
					&& referenceData.get(i).close < referenceData.get(i - 1).close) {
				BondSyncData syncData = new BondSyncData();
				syncData.date = bondData.get(i).endDate;
				syncData.isBothUp = false;
				bondSyncDataList.add(syncData);
			}
		}
		logger.info("Total number of bond/stock synchronized moving days: " + bondSyncDataList.size());
		return bondSyncDataList;
	}
}
