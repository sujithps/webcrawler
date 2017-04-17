package com.sps.buildit.crawler.task;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * CrawlerTask is used to retrieve all internal and external links from a given
 * website URL.
 * 
 * The crawler is limited to one domain. Given a starting URL – say
 * http://wiprodigital.com - it will visit all pages within the domain, but wont
 * follow the links to external sites such as Google or Twitter.
 * 
 * @author Sujith PS
 *
 */
public class CrawlerTask {

	private Set<String> pagesVisited;
	private Map<String, Map<String, Set<String>>> crawlerResultMap;

	protected static final String SAME_DOMAIN_URLS = "SAME_DOMAIN_URLS";
	protected static final String EXTERNAL_DOMAIN_URLS = "EXTERNAL_DOMAIN_URLS";

	public CrawlerTask() {
		init();
	}

	public Map<String, Map<String, Set<String>>> fetchAllUrls(String websiteURL) {
		if (websiteURL == null || websiteURL.isEmpty()) {
			return null;
		}

		init();

		String domainName = websiteURL;
		// Regular expression for domain name without any parameters.
		Pattern pattern = Pattern.compile("(?:https?:\\/\\/)([^\\/?#]*)");
		Matcher matcher = pattern.matcher(websiteURL);

		if (matcher.find()) {
			domainName = matcher.group(1);
		}

		return fetchAllUrls(websiteURL, domainName);
	}

	private void init() {
		this.pagesVisited = new HashSet<>();
		this.crawlerResultMap = new HashMap<>();
		this.crawlerResultMap.put(SAME_DOMAIN_URLS, new HashMap<>());
		this.crawlerResultMap.put(EXTERNAL_DOMAIN_URLS, new HashMap<>());
	}

	protected Map<String, Map<String, Set<String>>> fetchAllUrls(String websiteURL, String domainName) {
		Document doc = getDocument(websiteURL);

		processAllPages(doc, domainName);

		return crawlerResultMap;
	}

	protected Document getDocument(String websiteURL) {
		Document doc = null;
		try {
			doc = Jsoup.connect(websiteURL).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return doc;
	}

	private void processAllPages(Document doc, String domainName) {
		if (doc == null || domainName == null) {
			return;
		}

		// get all links and recursively call the processPage method
		Elements htmlElements = doc.select("a[href]");
		for (Element link : htmlElements) {
			String linkURL = link.absUrl("href");

			// Remove all parameters (parameters with ? symbol in the URL) and
			// hash tags (# symbol)
			linkURL = removeParametersFromURL(linkURL);

			// No need to process already visited pages.
			if (pagesVisited.contains(linkURL)) {
				continue;
			}

			if (link.absUrl("href").contains(domainName)) {
				Set<String> staticContents = fetchStaticContentsFromPage(linkURL);
				// add same domain URLs to SAME_DOMAIN_URLS key.
				crawlerResultMap.get(SAME_DOMAIN_URLS).put(linkURL, staticContents);
			} else {
				// add external domain URLs to EXTERNAL_DOMAIN_URLS key.
				crawlerResultMap.get(EXTERNAL_DOMAIN_URLS).put(linkURL, null);
			}

			pagesVisited.add(linkURL);
		}
	}

	private Set<String> fetchStaticContentsFromPage(String pageURL) {
		Set<String> staticContents = new HashSet<>();

		Document doc = getDocument(pageURL);

		if (doc == null || pageURL == null) {
			return staticContents;
		}

		// get all links and recursively call the processPage method
		Elements mediaElements = doc.select("[src]");
		Elements importElements = doc.select("link[href]");

		fetchAllStaticElementURLs(mediaElements, "src", staticContents);
		fetchAllStaticElementURLs(importElements, "href", staticContents);

		return staticContents;
	}

	private void fetchAllStaticElementURLs(Elements elements, String attribute, Set<String> staticContents) {
		for (Element link : elements) {
			String linkURL = link.absUrl(attribute);
			linkURL = removeParametersFromURL(linkURL);

			// Consider only valid files as media and images
			if (linkURL.matches(".*(\\.jpg|\\.jpeg|\\.png|\\.js|\\.css)")) {
				staticContents.add(linkURL);
			}
		}
	}

	protected String removeParametersFromURL(String linkURL) {
		return linkURL.replaceAll("[?|#].*", "");
	}
}