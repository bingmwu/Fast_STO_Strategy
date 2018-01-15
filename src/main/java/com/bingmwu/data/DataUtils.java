package com.bingmwu.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataUtils {
	private static Logger logger = LoggerFactory.getLogger(DataUtils.class);

	public static List<String> listFiles(final String extension, final String... paths) {
		List<String> list = new ArrayList<>();
		for (String path : paths) {
			list.addAll(namesFromPath(path, extension));
		}
		return list;
	}

	private static List<String> namesFromPath(String path, final String extension) {
		List<String> list = new ArrayList<>();
		File file = new File(path);
		if (file.isDirectory()) {
			list.addAll(getFullPaths(path, extension));
		} else {
			list.add(path);
		}
		return list;
	}

	private static List<String> getFullPaths(String path, final String extension) {
		File file = new File(path);
		String[] names = file.list((file1, name) -> name.endsWith(extension));
		List<String> list = new ArrayList<>();
		for (String name : names) {
			list.add(path + File.separator + name);
		}
		return list;
	}

	public static List<DataItem> createLineList(BufferedReader br) throws IOException {
		List<DataItem> list = new ArrayList<>();
		buildLines(br, list);
		return list;
	}

	private static void buildLines(BufferedReader br, List<DataItem> list) throws IOException {
		String line;
		line = br.readLine();
		processFirstLine(line, list);
		while ((line = br.readLine()) != null) {
			DataItem row = null;
			row = processLine(line);
			list.add(row);
		}
	}

	private static void processFirstLine(String line, List<DataItem> list) {
		try {
			DataItem row = processLine(line);
			list.add(row);
		} catch (NumberFormatException ignore) {
			/*
			 * If it's the first line then it's probably just a heading. Let's
			 * ignore NFE
			 */
		}
	}

	public static DataItem processLine(String line) {
		DataItem dataItem = new DataItem();
		String separator = ",";
		String[] fields = line.split(separator);
		dataItem.endDate = parseDate(fields[0]);
		dataItem.open = Float.valueOf(fields[1]);
		dataItem.high = Float.valueOf(fields[2]);
		dataItem.low = Float.valueOf(fields[3]);
		dataItem.close = Float.valueOf(fields[4]);
		dataItem.volume = Long.valueOf(fields[5]);

		// assume the input is daily data
		dataItem.periodType = DATA_PERIOD_TYPE.DAYLY;
		return dataItem;
	}

	public static LocalDate parseDate(String field) {
		return LocalDate.parse(field, yahooDateFormat);
	}

	public static final DateTimeFormatter yahooDateFormat = DateTimeFormatter.ofPattern("M/d/yyyy");
}
