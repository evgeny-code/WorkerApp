package com.linksrussia.tsup.workerapp.dto;

public class DeviceData {
    private final double GJ;
    private final double CG;

    public DeviceData(double GJ, double CG) {
        this.GJ = GJ;
        this.CG = CG;
    }

    public double getGJ() {
        return GJ;
    }

    public double getCG() {
        return CG;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "GJ=" + GJ +
                ", CG=" + CG +
                '}';
    }
}
