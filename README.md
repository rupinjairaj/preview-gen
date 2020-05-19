![](https://github.com/rupinjairaj/preview-gen/workflows/Link%20Preview/badge.svg)

# Link Preview

- Generates a preview for any HTTP link


## Preview response properties
- URL
- Title
- Description
- Domain

#### WIP preview response properties
- Image URL

## Sample usage in a Spring Boot app

```java
import io.rupj.linkpreview.models.Preview;
import io.rupj.linkpreview.service.LinkPreview;

@SpringBootApplication(scanBasePackages = "io.rupj.linkpreview")
@RestController
public class PreviewController {

private final LinkPreview preview;

    PreviewController(LinkPreview preview) {
        this.preview = preview;
    }
    
    @GetMapping("/preview")
    public Preview preview(@RequestParam(name = "link", required = true) String link) {
        return preview.generateLinkPreview(link);
    }

}
```

## License
[MIT](https://choosealicense.com/licenses/mit/)