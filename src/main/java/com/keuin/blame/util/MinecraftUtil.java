package com.keuin.blame.util;

import com.keuin.blame.data.WorldPos;
import net.minecraft.world.World;

public class MinecraftUtil {

    public static String worldToString(World world) {
        return world.getRegistryKey().getValue().toString();
    }

    public static double getRadius(WorldPos objectPos) {
        return Math.sqrt(Math.pow(objectPos.getX(), 2) + Math.pow(objectPos.getY(), 2) + Math.pow(objectPos.getZ(), 2));
    }
}
