package foundationgames.enhancedblockentities.common.util;

import net.minecraft.util.Tuple;

public class DefaultedTuple<A, B> extends Tuple<A, B> {

    private final A defaultA;
    private final B defaultB;

    public DefaultedTuple(A a, B b) {
        super(a, b);
        this.defaultA = a;
        this.defaultB = b;
    }

    public boolean isDefault() {
        return this.getA() == this.defaultA || this.getB() == this.defaultB;
    }
}
