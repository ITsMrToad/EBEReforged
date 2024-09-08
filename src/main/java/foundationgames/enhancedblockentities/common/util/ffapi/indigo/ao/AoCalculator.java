package foundationgames.enhancedblockentities.common.util.mfrapi.indigo.ao;

import foundationgames.enhancedblockentities.common.util.mfrapi.block.ModernBlockRenderer;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.helper.GeometryHelper;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.work.quad.OpenMutableQuadLookup;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.work.quad.OpenQuadLookup;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public abstract class AoCalculator {

    public static final Float2FloatFunction CLAMP_FUNC = x -> x < 0.0F ? 0.0F : Math.min(x, 1.0F);

    public final AoFaceData tmpFace = new AoFaceData();

    public final BlockPos.MutableBlockPos lightPos = new BlockPos.MutableBlockPos();
    public final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

    public final AoFaceData[] faceData = new AoFaceData[24];
    public final Vector3f vertexNormal = new Vector3f();

    public final ModernBlockRenderer.Info blockInfo;

    public final float[] ao = new float[4];
    public final float[] w = new float[4];

    public final int[] light = new int[4];

    public int completionFlags = 0;

    public AoCalculator(ModernBlockRenderer.Info blockInfo) {
        this.blockInfo = blockInfo;
        for (int i = 0; i < 24; i++) {
            this.faceData[i] = new AoFaceData();
        }
    }

    public abstract float ao(BlockPos pos, BlockState state);

    public abstract int light(BlockPos pos, BlockState state);

    public void compute(OpenMutableQuadLookup quad) {
        switch (quad.geometryFlags()) {
            case GeometryHelper.AXIS_ALIGNED_FLAG | GeometryHelper.CUBIC_FLAG | GeometryHelper.LIGHT_FACE_FLAG:
            case GeometryHelper.AXIS_ALIGNED_FLAG | GeometryHelper.LIGHT_FACE_FLAG:
                vanillaPartialFace(quad, quad.lightFace(), quad.hasShade());
                break;

            case GeometryHelper.AXIS_ALIGNED_FLAG | GeometryHelper.CUBIC_FLAG:
            case GeometryHelper.AXIS_ALIGNED_FLAG:
                this.blendedPartialFace(quad, quad.lightFace(), quad.hasShade());
                break;

            default:
                irregularFace(quad, quad.hasShade());
                break;
        }
    }

    private void vanillaPartialFace(OpenQuadLookup quad, Direction lightFace, boolean shade) {
        AoFaceData faceData = this.computeFace(lightFace, true, shade);
        AoFace.WeightFunction wFunc = AoFace.get(lightFace).weightFunc;
        float[] w = this.w;
        for (int i = 0; i < 4; i++) {
            wFunc.apply(quad, i, w);
            this.light[i] = faceData.weightedCombinedLight(w);
            this.ao[i] = faceData.weigtedAo(w);
        }
    }

    private void blendedPartialFace(OpenQuadLookup quad, Direction lightFace, boolean shade) {
        AoFaceData faceData = this.blendedInsetFace(quad, lightFace, shade);
        AoFace.WeightFunction wFunc = AoFace.get(lightFace).weightFunc;

        for (int i = 0; i < 4; i++) {
            wFunc.apply(quad, i, this.w);
            this.light[i] = faceData.weightedCombinedLight(this.w);
            this.ao[i] = faceData.weigtedAo(this.w);
        }
    }

    private AoFaceData blendedInsetFace(OpenQuadLookup quad, Direction lightFace, boolean shade) {
        final float w1 = AoFace.get(lightFace).depthFunc.apply(quad, 0);
        final float w0 = 1 - w1;
        return AoFaceData.weightedMean(computeFace(lightFace, true, shade), w0, computeFace(lightFace, false, shade), w1, this.tmpFace);
    }

    private AoFaceData computeFace(Direction lightFace, boolean isOnBlockFace, boolean shade) {
        int faceDataIndex = shade ? (isOnBlockFace ? lightFace.get3DDataValue() : lightFace.get3DDataValue() + 6) : (isOnBlockFace ? lightFace.get3DDataValue() + 12 : lightFace.get3DDataValue() + 18);
        int mask = 1 << faceDataIndex;
        AoFaceData result = this.faceData[faceDataIndex];

        if ((this.completionFlags & mask) == 0) {
            this.completionFlags |= mask;
            this.computeFace(result, lightFace, isOnBlockFace, shade);
        }

        return result;
    }

    private void computeFace(AoFaceData result, Direction lightFace, boolean isOnBlockFace, boolean shade) {
        BlockAndTintGetter world = blockInfo.blockView;
        BlockPos pos = blockInfo.blockPos;
        BlockState blockState = blockInfo.blockState;
        BlockPos.MutableBlockPos lightPos = this.lightPos;
        BlockPos.MutableBlockPos searchPos = this.searchPos;
        BlockState searchState;

        if (isOnBlockFace) {
            lightPos.setWithOffset(pos, lightFace);
        } else {
            lightPos.set(pos);
        }

        AoFace aoFace = AoFace.get(lightFace);

        searchPos.setWithOffset(lightPos, aoFace.neighbors[0]);
        searchState = world.getBlockState(searchPos);
        int light0 = light(searchPos, searchState);
        float ao0 = ao(searchPos, searchState);
        boolean em0 = this.hasEmissiveLighting(world, searchPos, searchState);
        searchPos.move(lightFace);
        searchState = world.getBlockState(searchPos);
        boolean isClear0 = !searchState.isViewBlocking(world, searchPos) || searchState.getLightBlock(world, searchPos) == 0;
        searchPos.setWithOffset(lightPos, aoFace.neighbors[1]);
        searchState = world.getBlockState(searchPos);
        int light1 = light(searchPos, searchState);
        float ao1 = ao(searchPos, searchState);
        boolean em1 = this.hasEmissiveLighting(world, searchPos, searchState);
        searchPos.move(lightFace);
        searchState = world.getBlockState(searchPos);
        boolean isClear1 = !searchState.isViewBlocking(world, searchPos) || searchState.getLightBlock(world, searchPos) == 0;
        searchPos.setWithOffset(lightPos, aoFace.neighbors[2]);
        searchState = world.getBlockState(searchPos);
        int light2 = light(searchPos, searchState);
        float ao2 = ao(searchPos, searchState);
        boolean em2 = this.hasEmissiveLighting(world, searchPos, searchState);
        searchPos.move(lightFace);
        searchState = world.getBlockState(searchPos);
        boolean isClear2 = !searchState.isViewBlocking(world, searchPos) || searchState.getLightBlock(world, searchPos) == 0;
        searchPos.setWithOffset(lightPos, aoFace.neighbors[3]);
        searchState = world.getBlockState(searchPos);
        int light3 = light(searchPos, searchState);
        float ao3 = ao(searchPos, searchState);
        boolean em3 = this.hasEmissiveLighting(world, searchPos, searchState);
        searchPos.move(lightFace);
        searchState = world.getBlockState(searchPos);
        boolean isClear3 = !searchState.isViewBlocking(world, searchPos) || searchState.getLightBlock(world, searchPos) == 0;

        int cLight0, cLight1, cLight2, cLight3;
        float cAo0, cAo1, cAo2, cAo3;
        boolean cEm0, cEm1, cEm2, cEm3;

        if (!isClear2 && !isClear0) {
            cAo0 = ao0;
            cLight0 = light0;
            cEm0 = em0;
        } else {
            searchPos.set(lightPos).move(aoFace.neighbors[0]).move(aoFace.neighbors[2]);
            searchState = world.getBlockState(searchPos);
            cAo0 = ao(searchPos, searchState);
            cLight0 = light(searchPos, searchState);
            cEm0 = this.hasEmissiveLighting(world, searchPos, searchState);
        }

        if (!isClear3 && !isClear0) {
            cAo1 = ao0;
            cLight1 = light0;
            cEm1 = em0;
        } else {
            searchPos.set(lightPos).move(aoFace.neighbors[0]).move(aoFace.neighbors[3]);
            searchState = world.getBlockState(searchPos);
            cAo1 = ao(searchPos, searchState);
            cLight1 = light(searchPos, searchState);
            cEm1 = this.hasEmissiveLighting(world, searchPos, searchState);
        }

        if (!isClear2 && !isClear1) {
            cAo2 = ao1;
            cLight2 = light1;
            cEm2 = em1;
        } else {
            searchPos.set(lightPos).move(aoFace.neighbors[1]).move(aoFace.neighbors[2]);
            searchState = world.getBlockState(searchPos);
            cAo2 = ao(searchPos, searchState);
            cLight2 = light(searchPos, searchState);
            cEm2 = this.hasEmissiveLighting(world, searchPos, searchState);
        }

        if (!isClear3 && !isClear1) {
            cAo3 = ao1;
            cLight3 = light1;
            cEm3 = em1;
        } else {
            searchPos.set(lightPos).move(aoFace.neighbors[1]).move(aoFace.neighbors[3]);
            searchState = world.getBlockState(searchPos);
            cAo3 = ao(searchPos, searchState);
            cLight3 = light(searchPos, searchState);
            cEm3 = this.hasEmissiveLighting(world, searchPos, searchState);
        }

        int lightCenter;
        boolean emCenter;
        searchPos.setWithOffset(pos, lightFace);
        searchState = world.getBlockState(searchPos);

        if (isOnBlockFace || !searchState.isSolidRender(world, searchPos)) {
            lightCenter = light(searchPos, searchState);
            emCenter = this.hasEmissiveLighting(world, searchPos, searchState);
        } else {
            lightCenter = light(pos, blockState);
            emCenter = this.hasEmissiveLighting(world, pos, blockState);
        }

        float aoCenter = ao(lightPos, world.getBlockState(lightPos));
        float worldBrightness = world.getShade(lightFace, shade);

        result.a0 = ((ao3 + ao0 + cAo1 + aoCenter) * 0.25F) * worldBrightness;
        result.a1 = ((ao2 + ao0 + cAo0 + aoCenter) * 0.25F) * worldBrightness;
        result.a2 = ((ao2 + ao1 + cAo2 + aoCenter) * 0.25F) * worldBrightness;
        result.a3 = ((ao3 + ao1 + cAo3 + aoCenter) * 0.25F) * worldBrightness;

        result.l0(meanBrightness(light3, light0, cLight1, lightCenter, em3, em0, cEm1, emCenter));
        result.l1(meanBrightness(light2, light0, cLight0, lightCenter, em2, em0, cEm0, emCenter));
        result.l2(meanBrightness(light2, light1, cLight2, lightCenter, em2, em1, cEm2, emCenter));
        result.l3(meanBrightness(light3, light1, cLight3, lightCenter, em3, em1, cEm3, emCenter));
    }

    private void irregularFace(OpenMutableQuadLookup quad, boolean shade) {
        final Vector3f faceNorm = quad.faceNormal();
        Vector3f normal;
        float[] w = this.w;
        float[] aoResult = this.ao;

        for (int i = 0; i < 4; i++) {
            normal = quad.hasNormal(i) ? quad.copyNormal(i, this.vertexNormal) : faceNorm;
            float ao = 0, sky = 0, block = 0, maxAo = 0;
            int maxSky = 0, maxBlock = 0;

            float x = normal.x();

            if (!Mth.equal(0f, x)) {
                final Direction face = x > 0 ? Direction.EAST : Direction.WEST;
                final AoFaceData fd = gatherInsetFace(quad, i, face, shade);
                AoFace.get(face).weightFunc.apply(quad, i, w);
                final float n = x * x;
                final float a = fd.weigtedAo(w);
                final int s = fd.weigtedSkyLight(w);
                final int b = fd.weigtedBlockLight(w);
                ao += n * a;
                sky += n * s;
                block += n * b;
                maxAo = a;
                maxSky = s;
                maxBlock = b;
            }

            final float y = normal.y();

            if (!Mth.equal(0f, y)) {
                final Direction face = y > 0 ? Direction.UP : Direction.DOWN;
                final AoFaceData fd = gatherInsetFace(quad, i, face, shade);
                AoFace.get(face).weightFunc.apply(quad, i, w);
                final float n = y * y;
                final float a = fd.weigtedAo(w);
                final int s = fd.weigtedSkyLight(w);
                final int b = fd.weigtedBlockLight(w);
                ao += n * a;
                sky += n * s;
                block += n * b;
                maxAo = Math.max(maxAo, a);
                maxSky = Math.max(maxSky, s);
                maxBlock = Math.max(maxBlock, b);
            }

            final float z = normal.z();

            if (!Mth.equal(0f, z)) {
                final Direction face = z > 0 ? Direction.SOUTH : Direction.NORTH;
                final AoFaceData fd = gatherInsetFace(quad, i, face, shade);
                AoFace.get(face).weightFunc.apply(quad, i, w);
                final float n = z * z;
                final float a = fd.weigtedAo(w);
                final int s = fd.weigtedSkyLight(w);
                final int b = fd.weigtedBlockLight(w);
                ao += n * a;
                sky += n * s;
                block += n * b;
                maxAo = Math.max(maxAo, a);
                maxSky = Math.max(maxSky, s);
                maxBlock = Math.max(maxBlock, b);
            }

            aoResult[i] = (ao + maxAo) * 0.5f;
            this.light[i] = (((int) ((sky + maxSky) * 0.5f) & 0xF0) << 16) | ((int) ((block + maxBlock) * 0.5f) & 0xF0);
        }
    }

    private AoFaceData gatherInsetFace(OpenQuadLookup quad, int vertexIndex, Direction lightFace, boolean shade) {
        float w1 = AoFace.get(lightFace).depthFunc.apply(quad, vertexIndex);
        if (Mth.equal(w1, 0)) {
            return this.computeFace(lightFace, true, shade);
        } else if (Mth.equal(w1, 1)) {
            return this.computeFace(lightFace, false, shade);
        } else {
            final float w0 = 1 - w1;
            return AoFaceData.weightedMean(this.computeFace(lightFace, true, shade), w0, this.computeFace(lightFace, false, shade), w1, tmpFace);
        }
    }

    private boolean hasEmissiveLighting(BlockAndTintGetter getter, BlockPos pos, BlockState state) {
        return state.emissiveRendering(getter, pos);
    }

    public int meanBrightness(int lightA, int lightB, int lightC, int lightD, boolean emA, boolean emB, boolean emC, boolean emD) {
        if (lightA == 0 || lightB == 0 || lightC == 0 || lightD == 0) {
            int min = nonZeroMin(nonZeroMin(lightA, lightB), nonZeroMin(lightC, lightD));

            lightA = Math.max(lightA, min);
            lightB = Math.max(lightB, min);
            lightC = Math.max(lightC, min);
            lightD = Math.max(lightD, min);
        }

        if (emA) lightA = LightTexture.FULL_BRIGHT;
        if (emB) lightB = LightTexture.FULL_BRIGHT;
        if (emC) lightC = LightTexture.FULL_BRIGHT;
        if (emD) lightD = LightTexture.FULL_BRIGHT;

        return meanInnerBrightness(lightA, lightB, lightC, lightD);
    }

    private static int meanInnerBrightness(int a, int b, int c, int d) {
        return a + b + c + d >> 2 & 0xFF00FF;
    }

    private static int nonZeroMin(int a, int b) {
        if (a == 0) return b;
        if (b == 0) return a;
        return Math.min(a, b);
    }
}
