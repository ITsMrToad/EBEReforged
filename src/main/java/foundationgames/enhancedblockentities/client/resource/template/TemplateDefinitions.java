package foundationgames.enhancedblockentities.client.resource.template;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import org.jetbrains.annotations.NotNull;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

public interface TemplateDefinitions {

    default TemplateDefinitions def(String k, Object v) {
        return this.def(k, templates -> String.valueOf(v));
    }

    TemplateDefinitions def(String k, TemplateProvider.TemplateApplyingFunction v);

    class Impl implements TemplateDefinitions, Iterable<Map.Entry<String, TemplateProvider.TemplateApplyingFunction>> {
        private final Deque<Map<String, TemplateProvider.TemplateApplyingFunction>> stack = Queues.newArrayDeque();

        public void push() {
            this.stack.addLast(Maps.newHashMap());
        }

        public void pop() {
            this.stack.removeLast();
        }

        public TemplateDefinitions def(String k, String v) {
            return this.def(k, templates -> v);
        }

        public TemplateDefinitions def(String k, TemplateProvider.TemplateApplyingFunction v) {
            this.stack.getLast().put(k, v);
            return this;
        }

        @NotNull
        @Override
        public Iterator<Map.Entry<String, TemplateProvider.TemplateApplyingFunction>> iterator() {
            return this.stack.getLast().entrySet().iterator();
        }
    }
}
