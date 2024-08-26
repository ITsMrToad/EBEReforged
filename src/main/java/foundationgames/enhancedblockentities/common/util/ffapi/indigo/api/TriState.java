package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api;

import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public enum TriState {

    FALSE,
    DEFAULT,
    TRUE;

    public static TriState of(boolean bool) {
        return bool ? TRUE : FALSE;
    }

    public static TriState of(@Nullable Boolean bool) {
        return bool == null ? DEFAULT : of(bool.booleanValue());
    }

    public boolean get() {
        return this == TRUE;
    }

    @Nullable public Boolean getBoxed() {
        return this == DEFAULT ? null : this.get();
    }

    public boolean orElse(boolean value) {
        return this == DEFAULT ? value : this.get();
    }

    public boolean orElseGet(BooleanSupplier supplier) {
        return this == DEFAULT ? supplier.getAsBoolean() : this.get();
    }

    public <T> Optional<T> map(Boolean2ObjectFunction<? extends @Nullable T> mapper) {
        Objects.requireNonNull(mapper, "Mapper function cannot be null");

        if (this == DEFAULT) {
            return Optional.empty();
        }

        return Optional.ofNullable(mapper.apply(this.get()));
    }

    public <X extends Throwable> boolean orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (this != DEFAULT) {
            return this.get();
        }
        throw exceptionSupplier.get();
    }
}
