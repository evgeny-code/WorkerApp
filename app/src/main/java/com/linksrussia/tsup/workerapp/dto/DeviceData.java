package com.linksrussia.tsup.workerapp.dto;

public class DeviceData {
    private final int GJ;
    private final int CG;

    public DeviceData(int GJ, int CG) {
        this.GJ = GJ;
        this.CG = CG;
    }

    public int getGJ() {
        return GJ;
    }

    public int getCG() {
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
