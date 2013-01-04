package com.ebay.app.raptor.controller;

import java.util.HashMap;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ebay.app.raptor.cache.CacheEngine;
import com.ebay.app.raptor.cache.CacheEngineFactory;
import com.ebay.app.raptor.cache.CacheEngineInitializeException;
import com.ebay.raptor.kernel.context.IRaptorContext;

@Controller
public class SampleRaptorController {

	@Inject
	IRaptorContext raptorCtx;

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public HashMap<String, String> handleRequest() {
		CacheEngine engine = null;
		try {
			engine = CacheEngineFactory.getRedisCacheEngine();
		} catch (CacheEngineInitializeException e) {
			e.printStackTrace();
		}
		engine.set(null, null, "name", "luochen".getBytes());

		HashMap<String, String> model = new HashMap<String, String>();
		model.put("greeting", new String(engine.get(null, null, "name")));
		return model;
	}
}
