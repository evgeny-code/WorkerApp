package com.linksrussia.tsup.workerapp.util;

import android.util.Log;

import com.linksrussia.tsup.workerapp.dto.DeviceData;

import java.util.ArrayList;
import java.util.List;

public class DataParser {
    private StringBuffer mainBuffer = new StringBuffer();
    private StringBuffer numberBuffer = new StringBuffer();

    private List<DeviceData> dataBuffer = new ArrayList<>();
    private boolean hasNewData = false;

    private boolean readingGJ = false;
    private boolean readingCG = false;

    private double gj;
    private double cg;

    public void putData(String part) {
        for (char c : part.toCharArray()) {
            mainBuffer.append(c);

            if (3 > mainBuffer.length())
                continue;

            String token = mainBuffer.substring(mainBuffer.length() - 3);
            if ("GJ:".equals(token)) {
                readingGJ = true;
                continue;
            }
            if ("CG:".equals(token)) {
                readingCG = true;
                continue;
            }

            if (readingGJ && ',' == c) {
                try {
                    gj = Double.parseDouble(numberBuffer.toString().trim());
                } catch (NumberFormatException nfe) {
                    Log.e("Parser", "NumberFormatException", nfe);
                    gj = Double.MIN_VALUE;
                }

                numberBuffer.delete(0, numberBuffer.length());
                readingGJ = false;
            }
            if (readingCG && 'G' == c) {
                try {
                    cg = Double.parseDouble(numberBuffer.toString().trim());
                } catch (NumberFormatException nfe) {
                    Log.e("Parser", "NumberFormatException", nfe);
                    cg = Double.MIN_VALUE;
                }

                numberBuffer.delete(0, numberBuffer.length());
                readingCG = false;

                dataBuffer.add(new DeviceData(gj, cg));
                hasNewData = true;
                mainBuffer.delete(0, mainBuffer.length() - 1);
                readingGJ = false;
                readingCG = false;
            }

            if (readingCG || readingGJ) {
                numberBuffer.append(c);
            }
        }

        tryToGrabRemainder();
    }

    public void tryToGrabRemainder() {
        String remainder = mainBuffer.toString();
        if (remainder.contains("GJ:") && remainder.contains("CG:")) {
            double gj = 0;
            double cg = 0;
            for (String part : remainder.split(",")) {
                part = part.trim();
                if (part.contains("GJ:")) {
                    gj = Double.parseDouble(part.substring(3).trim());
                } else if (part.contains("CG:")) {
                    cg = Double.parseDouble(part.substring(3).trim());
                }
            }
            dataBuffer.add(new DeviceData(gj, cg));
            hasNewData = true;

            mainBuffer.delete(0, mainBuffer.length());
            readingGJ = false;
            readingCG = false;
        }
    }

    public boolean hasNewData() {
        return hasNewData;
    }

    public synchronized List<DeviceData> getData() {
        try {
            return new ArrayList<>(dataBuffer);
        } finally {
            dataBuffer.clear();
            hasNewData = false;
        }
    }
}
