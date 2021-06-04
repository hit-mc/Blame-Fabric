package com.keuin.blame.data;

import java.util.Objects;

public class WorldPos {

    // immutable

    private String world = "";
    private double x = 0;
    private double y = 0;
    private double z = 0;

    public static final WorldPos NULL_POS = new WorldPos("", 0, 0, 0);

    public WorldPos(String world, double x, double y, double z) {
        if (world == null)
            throw new IllegalArgumentException("world string must not be null");
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
//        System.out.printf("%s, %f, %f, %f%n", world, x, y, z);
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

    @Override
    public String toString() {
        return String.format("(%s, %s, %s, %s)", prettyDouble(x), prettyDouble(y), prettyDouble(z), world);
    }

    private String prettyDouble(double d) {
        if ((d - (int) d) < 1e-3)
            return String.valueOf((int) d);
        else
            return String.format("%.3f", d);
    }
}
