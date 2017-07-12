package com.bingmwu.prediction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Prediction implements Serializable {

	private static final long serialVersionUID = 3921337231939450260L;

	public Prediction() {
		this.successList = new ArrayList<Date>();
		this.failedList = new ArrayList<Date>();
	}

	public float successRate;
	public int numberOfSuccessPrediction;
	public int numberOfFailedPrediction;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public List<Date> successList;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public List<Date> failedList;
}
