package foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.ModelResolver;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ModelLoadingPluginContext implements ModelLoadingPlugin.Context {

    public final List<ModelResolver> resolvers = Lists.newArrayList();
    public final Set<ResourceLocation> extraModels = Sets.newLinkedHashSet();

    public final List<UnbakedModel> resolvedModels;

    public ModelLoadingPluginContext(ModelResolver.Context ctx) {
        if (!this.resolver().isEmpty()) {
            this.resolvedModels = ImmutableList.copyOf(this.resolver().stream().map(r -> {
                try {
                    UnbakedModel model = r.resolveModel(ctx);
                    if (model != null) {
                        return model;
                    }
                } catch (Exception e) {
                    EBE.LOGGER.error(EBE.FFAPI, "Cannot resolve model", e);
                }
                return null;
            }).filter(Objects::nonNull).toList());
        } else {
            this.resolvedModels = Collections.emptyList();
        }
    }

    @Override
    public List<ModelResolver> resolver() {
        return this.resolvers;
    }

    @Override
    public void addModels(ResourceLocation... rl) {
        Collections.addAll(this.extraModels, rl);
    }

    @Override
    public void addModels(Collection<? extends ResourceLocation> c) {
        this.extraModels.addAll(c);
    }
}
