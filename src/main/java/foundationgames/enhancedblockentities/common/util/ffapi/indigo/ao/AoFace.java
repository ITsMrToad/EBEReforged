package foundationgames.enhancedblockentities.common.util.mfrapi.indigo.ao;

import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.work.quad.OpenQuadLookup;
import net.minecraft.Util;
import net.minecraft.core.Direction;

public enum AoFace {

    AOF_DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, (q, i) -> AoCalculator.CLAMP_FUNC.apply(q.y(i)), (q, i, w) -> {
        float u = AoCalculator.CLAMP_FUNC.apply(q.x(i));
        float v = AoCalculator.CLAMP_FUNC.apply(q.z(i));
        w[0] = (1 - u) * v;
        w[1] = (1 - u) * (1 - v);
        w[2] = u * (1 - v);
        w[3] = u * v;
    }),
    AOF_UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, (q, i) -> 1 - AoCalculator.CLAMP_FUNC.apply(q.y(i)), (q, i, w) -> {
        float u = AoCalculator.CLAMP_FUNC.apply(q.x(i));
        float v = AoCalculator.CLAMP_FUNC.apply(q.z(i));
        w[0] = u * v;
        w[1] = u * (1 - v);
        w[2] = (1 - u) * (1 - v);
        w[3] = (1 - u) * v;
    }),
    AOF_NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, (q, i) -> AoCalculator.CLAMP_FUNC.apply(q.z(i)), (q, i, w) -> {
        float u = AoCalculator.CLAMP_FUNC.apply(q.y(i));
        float v = AoCalculator.CLAMP_FUNC.apply(q.x(i));
        w[0] = u * (1 - v);
        w[1] = u * v;
        w[2] = (1 - u) * v;
        w[3] = (1 - u) * (1 - v);
    }),
    AOF_SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, (q, i) -> 1 - AoCalculator.CLAMP_FUNC.apply(q.z(i)), (q, i, w) -> {
        float u = AoCalculator.CLAMP_FUNC.apply(q.y(i));
        float v = AoCalculator.CLAMP_FUNC.apply(q.x(i));
        w[0] = u * (1 - v);
        w[1] = (1 - u) * (1 - v);
        w[2] = (1 - u) * v;
        w[3] = u * v;
    }),
    AOF_WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, (q, i) -> AoCalculator.CLAMP_FUNC.apply(q.x(i)), (q, i, w) -> {
        float u = AoCalculator.CLAMP_FUNC.apply(q.y(i));
        float v = AoCalculator.CLAMP_FUNC.apply(q.z(i));
        w[0] = u * v;
        w[1] = u * (1 - v);
        w[2] = (1 - u) * (1 - v);
        w[3] = (1 - u) * v;
    }),
    AOF_EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, (q, i) -> 1 - AoCalculator.CLAMP_FUNC.apply(q.x(i)), (q, i, w) -> {
        float u = AoCalculator.CLAMP_FUNC.apply(q.y(i));
        float v = AoCalculator.CLAMP_FUNC.apply(q.z(i));
        w[0] = (1 - u) * v;
        w[1] = (1 - u) * (1 - v);
        w[2] = u * (1 - v);
        w[3] = u * v;
    });


    public final Direction[] neighbors;
    public final WeightFunction weightFunc;
    public final Vertex2Float depthFunc;

    private static final AoFace[] VALUES = Util.make(new AoFace[6], (neighborData) -> {
        neighborData[Direction.DOWN.get3DDataValue()] = AOF_DOWN;
        neighborData[Direction.UP.get3DDataValue()] = AOF_UP;
        neighborData[Direction.NORTH.get3DDataValue()] = AOF_NORTH;
        neighborData[Direction.SOUTH.get3DDataValue()] = AOF_SOUTH;
        neighborData[Direction.WEST.get3DDataValue()] = AOF_WEST;
        neighborData[Direction.EAST.get3DDataValue()] = AOF_EAST;
    });

    AoFace(Direction[] faces, Vertex2Float depthFunc, WeightFunction weightFunc) {
        this.neighbors = faces;
        this.depthFunc = depthFunc;
        this.weightFunc = weightFunc;
    }

    public static AoFace get(Direction direction) {
        return VALUES[direction.get3DDataValue()];
    }

    @FunctionalInterface
    public interface WeightFunction {
        void apply(OpenQuadLookup q, int vertexIndex, float[] out);
    }

    @FunctionalInterface
    public interface Vertex2Float {
        float apply(OpenQuadLookup q, int vertexIndex);
    }
}
