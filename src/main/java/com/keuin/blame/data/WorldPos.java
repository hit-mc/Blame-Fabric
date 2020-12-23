package com.keuin.blame.data;

import java.util.Objects;

public class WorldPos {

    // immutable

    private final String world;
    private final double x;
    private final double y;
    private final double z;

    public static final WorldPos NULL_POS = new WorldPos("", 0, 0, 0);

    public WorldPos(String world, double x, double y, double z) {
        if (world == null)
            throw new IllegalArgumentException("world string must not be null");
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldPos worldPos = (WorldPos) o;
        return Double.compare(worldPos.x, x) == 0 &&
                Double.compare(worldPos.y, y) == 0 &&
                Double.compare(worldPos.z, z) == 0 &&
                world.equals(worldPos.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }
}
