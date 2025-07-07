package net.mangolise.paintball;

import net.minestom.server.MinecraftServer;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

public class DimensionTypes {

    public static final RegistryKey<DimensionType> FULLBRIGHT;

    static {
        FULLBRIGHT = MinecraftServer.getDimensionTypeRegistry().register("fullbright", DimensionType.builder()
                .ambientLight(1f)
                .fixedTime(6000L)
                .build());
    }
}
