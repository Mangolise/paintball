package net.mangolise.paintball.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModelImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModelWrapper extends GenericModelImpl {

    private final String id;
    private final float initialScale;

    public ModelWrapper(String id, float initialScale) {
        this.id = id;
        this.initialScale = initialScale;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void init(@Nullable Instance instance, @NotNull Pos position) {
        super.init(instance, position, initialScale);
    }
}
