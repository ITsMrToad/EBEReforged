package foundationgames.enhancedblockentities.client.resource.template;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateProvider {

    private final TemplateLoader loader;
    private final TemplateDefinitions.Impl definitions = new TemplateDefinitions.Impl();
    private final Deque<String> loaded = Queues.newArrayDeque();

    public TemplateProvider(TemplateLoader loader) {
        this.loader = loader;
    }

    public String load(String templatePath, Consumer<TemplateDefinitions> definitions) throws IOException {
        this.definitions.push();
        definitions.accept(this.definitions);
        try {
            Map<String, String> substitutions = Maps.newHashMap();
            for (var entry : this.definitions) {
                substitutions.put(entry.getKey(), entry.getValue().getAndApplyTemplate(this));
            }

            CharSequence templateRaw = this.loader.getOrLoadRaw(templatePath);
            Matcher matcher = Pattern.compile("!\\[(" + String.join("|", substitutions.keySet()) + ")]").matcher(templateRaw);

            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                matcher.appendReplacement(result, substitutions.get(matcher.group(1)));
            }
            matcher.appendTail(result);
            this.definitions.pop();
            return result.toString();
        } catch (IOException ex) {
            this.definitions.pop();
            throw ex;
        }
    }

    @FunctionalInterface
    public interface TemplateApplyingFunction {
        String getAndApplyTemplate(TemplateProvider templates) throws IOException;
    }
}