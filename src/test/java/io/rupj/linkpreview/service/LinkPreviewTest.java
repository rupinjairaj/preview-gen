package io.rupj.linkpreview.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LinkPreviewTest {

    @Autowired
    private LinkPreview linkPreview;

    @Test
    public void generatePreview() {
        var preview = linkPreview.generateLinkPreview("https://spring.io/guides/gs/multi-module/#scratch");
        assertThat(preview.description).isNotEmpty();
        assertThat(preview.url).isNotEmpty();
        assertThat(preview.title).isNotEmpty();
        assertThat(preview.domain).isNotEmpty();
    }
}
