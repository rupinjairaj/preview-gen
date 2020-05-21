package io.rupj.linkpreview.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@FunctionalInterface
public interface HtmlTagSearch {
    Element getTag(Document document, String query);
}
