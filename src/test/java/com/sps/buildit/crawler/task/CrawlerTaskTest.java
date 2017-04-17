/**
 * 
 */
package com.sps.buildit.crawler.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * @author Sujith PS
 *
 */
public class CrawlerTaskTest {

	/**
	 * Test method for
	 * {@link com.sps.buildit.crawler.CrawlerTask#fetchAllUrls(java.lang.String)}
	 * .
	 */
	@Test
	public void testFetchAllUrlsString() {
		CrawlerTask task = new CrawlerTask();

		Map<String, Map<String, Set<String>>> result = task.fetchAllUrls("http://wiprodigital.com");

		assertTrue(result.containsKey(CrawlerTask.SAME_DOMAIN_URLS));
		assertTrue(result.containsKey(CrawlerTask.EXTERNAL_DOMAIN_URLS));
	}

	/**
	 * Test method for
	 * {@link com.sps.buildit.crawler.CrawlerTask#fetchAllUrls(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testFetchAllUrlsStringString() {
		CrawlerTask task = new CrawlerTask();
		Map<String, Map<String, Set<String>>> result = task.fetchAllUrls("http://wiprodigital.com","wiprodigital.com");

		assertTrue(result.containsKey(CrawlerTask.SAME_DOMAIN_URLS));
		assertTrue(result.containsKey(CrawlerTask.EXTERNAL_DOMAIN_URLS));
	}

	/**
	 * Test method for
	 * {@link com.sps.buildit.crawler.CrawlerTask#getDocument(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetDocument() {
		CrawlerTask task = new CrawlerTask();
		Document doc= task.getDocument("http://wiprodigital.com");

		assertNotNull(doc);
	}

	/**
	 * Test method for
	 * {@link com.sps.buildit.crawler.CrawlerTask#getDocument(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetDocumentInvalidUrl() {
		CrawlerTask task = new CrawlerTask();
		Document doc= task.getDocument("http://invalid.url");

		assertNull(doc);
	}
	
	/**
	 * Test method for
	 * {@link com.sps.buildit.crawler.CrawlerTask#removeParametersFromURL(java.lang.String)}
	 * .
	 */
	@Test
	public void testRemoveParametersFromURL() {
		CrawlerTask task = new CrawlerTask();
		
		String test1=task.removeParametersFromURL("test?id=123");
		assertEquals("test", test1);
		
		String testHash=task.removeParametersFromURL("test#id123");
		assertEquals("test", testHash);
	}

}
