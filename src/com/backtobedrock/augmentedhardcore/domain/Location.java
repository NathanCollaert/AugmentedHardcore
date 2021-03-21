package com.backtobedrock.augmentedhardcore.domain;

public class Location {
    private final String world;
    private final double x;
    private final double y;
    private final double z;

    public Location(String world, double x, double y, double z) {
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
    public String toString() {
        return String.format("x:%.2f, y:%.2f, z:%.2f (%s)", this.getX(), this.getY(), this.getZ(), this.getWorld());
    }
}
