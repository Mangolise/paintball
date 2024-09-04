package net.mangolise.paintball.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.worldseed.multipart.GenericModelImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoldenModel extends GenericModelImpl {

    @Override
    public String getId() {
        return "golden.bbmodel";
    }

    @Override
    public void init(@Nullable Instance instance, @NotNull Pos position) {
        super.init(instance, position, 1.0f);
    }
}
