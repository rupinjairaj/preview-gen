package io.rupj.linkpreview.models;

public class Preview {
    public String url;
    public String title;
    public String description;
    public String domain;

    public Preview(String url,
                   String title,
                   String description,
                   String domain) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "Preview{ " +
                "title: " + title + " " +
                "description: " + description + " " +
                "domain: " + domain + " " +
                "}";
    }
}