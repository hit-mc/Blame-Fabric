package com.keuin.blame.util;

import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.Objects;

public class MinecraftUtil {

    public static String worldToString(World world) {
        return Objects.requireNonNull(DimensionType.getId(world.dimension.getType())).toString();
    }

}
