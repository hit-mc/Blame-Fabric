package com.keuin.blame.util;

import net.minecraft.world.World;

public class MinecraftUtil {

    public static String worldToString(World world) {
        return world.getRegistryKey().getValue().toString();
    }

}
