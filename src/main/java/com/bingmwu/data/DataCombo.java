package com.bingmwu.data;

import java.util.List;

public class DataCombo {
	public List<DataItem> dailyDataList;
	public List<DataItem> weeklyDataList;
	public List<DataItem> monthlyDataList;

	public boolean hasData() {
		return dailyDataList != null && dailyDataList.size() > 0;
	}
}
