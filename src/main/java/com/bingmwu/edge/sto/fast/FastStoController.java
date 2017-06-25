package com.bingmwu.edge.sto.fast;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bingmwu.ApplicationMain;

@RestController
@RequestMapping(value = ApplicationMain.BASE_RELATIVE_API_URL)
@Validated
public class FastStoController {
	private static final Logger logger = LoggerFactory.getLogger(FastStoController.class);

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getIntel(HttpServletRequest request,
			@PathVariable(value = "id") int id) {
		logger.info("intel={}", id);
		return "test";

	}
}