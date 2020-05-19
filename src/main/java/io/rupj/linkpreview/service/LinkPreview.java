package io.rupj.linkpreview.service;

import io.rupj.linkpreview.models.Preview;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
@SpringBootConfiguration
public class LinkPreview {

    public Preview generateLinkPreview(String originalLink) {
        Document htmlDoc = null;
        try {
            htmlDoc = Jsoup.connect(originalLink).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (htmlDoc == null) return null;
        String url = originalLink;
        String title = getTitle(htmlDoc);
        String description = getDescription(htmlDoc);
        String domain = getDomainName(htmlDoc, originalLink);
        return new Preview(url, title, description, domain);
    }

    /*
     * Extract a possible Title for the given html document.
     * If unable to extract, an empty string is returned.
     * */
    private static String getTitle(Document document) {
        // Check if Open Graph(OG), 'title', meta tag is available and use that.
        Element metaTag = getHtmlTag.getTag(document, "meta[property=\"og:title\"]");
        // If OG is unavailable look for the twitter meta tag
        if (metaTag == null) metaTag = getHtmlTag.getTag(document, "meta[name=\"twitter:title\"]");
        String title = metaTag.attr("content");
        // If meta tags are unavailable fallback to either the first H1 tag or H2.
        title = !title.isEmpty() ? title : getHtmlTag.getTag(document, "h1").text();
        title = !title.isEmpty() ? title : getHtmlTag.getTag(document, "h2").text();
        return title;
    }

    /*
     * Extract a possible Description for the given html document.
     * If unable to extract, an empty String is returned.
     * */
    // TODO: Implement a safer fallback. Maybe a <P> tag in case the meta properties are unavailable.
    private static String getDescription(Document document) {
        Element metaTag = getHtmlTag.getTag(document, "meta[property=\"og:description\"]");
        if (metaTag == null) metaTag = getHtmlTag.getTag(document, "meta[name=\"twitter:description\"]");
        if (metaTag == null) metaTag = getHtmlTag.getTag(document, "meta[name=\"description\"]");
        String description = metaTag.attr("content");
        return description;
    }

    private static String getDomainName(Document document, String originalLink) {
        Element linkTag = getHtmlTag.getTag(document, "link[rel=canonical]");
        if (linkTag == null) linkTag = getHtmlTag.getTag(document, "meta[property=\"og:url\"]");
        String domain = linkTag != null ? linkTag.attr("href") : originalLink;
        return extractDomain(domain);
    }

    // TODO: WIP. Figure out where to extract the correct resolution images from.
    private static String getimageUrl(Document document) {
        Element metaTag = getHtmlTag.getTag(document, "link[rel=canonical]");
        if (metaTag == null) metaTag = getHtmlTag.getTag(document, "meta[property=\"og:url\"]");
        return metaTag.attr("content");
    }

    // Lambda helper to extract the required html tag based on the query selector.
    private static HtmlTagSearch getHtmlTag = (document, query) -> document.selectFirst(query);

    private static String extractDomain(String link) {
        String domain = "";
        try {
            URI uri = new URI(link);
            String temp = uri.getHost();
            if (temp != null)
                domain = (temp.startsWith("www.")) ? temp.substring(4) : temp;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return domain;
    }

}

@FunctionalInterface
interface HtmlTagSearch {
    Element getTag(Document document, String query);
}
