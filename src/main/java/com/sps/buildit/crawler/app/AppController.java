package com.sps.buildit.crawler.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.buildit.crawler.task.CrawlerTask;

@RestController
public class AppController {

	@RequestMapping("/")
	public String process() {
		CrawlerTask task = new CrawlerTask();
		return String.valueOf(task.fetchAllUrls("http://wiprodigital.com"));
	}

}