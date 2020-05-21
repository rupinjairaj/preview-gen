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
    // Title queries
    final static String[] metaTagTitleQueries = {"meta[property=\"og:title\"]", "meta[name=\"twitter:title\"]"};
    final static String[] fallBackTitleQueries = {"h1", "h2"};
    // Description queries
    final static String[] metaTagDescriptionQueries = {
            "meta[property=\"og:description\"]",
            "meta[name=\"twitter:description\"]",
            "meta[name=\"description\"]"};
    final static String fallBackDescriptionQuery = "p";
    // Domain queries
    final static String[] domainQueries = {"link[rel=canonical]", "meta[property=\"og:url\"]"};

    /*
     * API to provide with a completely generated Preview object
     * Parameters:
     *  String originalLink: The http link you wish to generate a preview for
     * Return:
     *  Preview
     * */
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
        Element tag;
        String title = "";
        // Check if Open Graph(OG), 'title', meta tag is available and use that.
        // If OG is unavailable look for the twitter meta tag
        for (String metaTagTitleQuery : metaTagTitleQueries) {
            tag = getHtmlTag.getTag(document, metaTagTitleQuery);
            if (tag != null) {
                title = tag.attr("content");
                return title;
            }
        }

        // If meta tags are unavailable fallback to either the first h1 tag or h2.
        for (String fallBackTitleQuery : fallBackTitleQueries) {
            tag = getHtmlTag.getTag(document, fallBackTitleQuery);
            if (tag != null) {
                title = tag.text();
                return title;
            }
        }
        return title;
    }

    /*
     * Extract a possible Description for the given html document.
     * If unable to extract, an empty String is returned.
     * */
    private static String getDescription(Document document) {
        String description = "";
        Element tag;
        // Check if Open Graph(OG), 'description', meta tag is available and use that.
        // If OG is unavailable look for the twitter description meta tag or the standard description meta tag
        for (String metaTagDescriptionQuery : metaTagDescriptionQueries) {
            tag = getHtmlTag.getTag(document, metaTagDescriptionQuery);
            if (tag != null) {
                description = tag.attr("content");
                return description;
            }
        }

        // if none of the universal meta tags are available this fallback will pick
        // the first <p> tag it finds.
        tag = getHtmlTag.getTag(document, fallBackDescriptionQuery);
        description = tag != null ? tag.text() : description;
        return description;
    }

    private static String getDomainName(Document document, String originalLink) {
        String domain = "";
        Element tag;
        for (String domainQuery : domainQueries) {
            tag = getHtmlTag.getTag(document, domainQuery);
            if (tag != null) {
                domain = tag.attr("href");
                return extractDomain(domain);
            }
        }
        domain = extractDomain(originalLink);
        return domain;
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