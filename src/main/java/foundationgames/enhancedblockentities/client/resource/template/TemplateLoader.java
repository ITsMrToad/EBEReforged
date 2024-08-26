package foundationgames.enhancedblockentities.client.resource.template;

import com.google.common.collect.Maps;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class TemplateLoader {

    private Path rootPath;

    private final Map<String, String> loadedTemplates = Maps.newHashMap();

    public void setRoot(Path path) {
        this.rootPath = path;
    }

    public String getOrLoadRaw(String path) throws IOException {
        if (this.rootPath == null) {
            return "";
        }

        if (this.loadedTemplates.containsKey(path)) {
            return this.loadedTemplates.get(path);
        }

        Path file = this.rootPath.resolve(path);
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(file))) {
            String templateRaw = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            this.loadedTemplates.put(path, templateRaw);
            return templateRaw;
        }
    }

}
