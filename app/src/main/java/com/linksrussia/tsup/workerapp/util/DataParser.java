package com.linksrussia.tsup.workerapp.util;

import com.linksrussia.tsup.workerapp.dto.DeviceData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataParser {
    public static final Pattern OLD_DATA_PATTERN = Pattern.compile("GJ:-?[0-9]+,CG:-?[0-9]+");
    public static final Pattern FULL_DATA_PATTERN = Pattern.compile("GJ:-?[0-9]+\\.[0-9]+,CG:-?[0-9]+\\.[0-9]+");

    public static final String GJ_START = "GJ:";
    public static final String CG_START = "CG:";

    private StringBuffer mainBuffer = new StringBuffer();

    private List<DeviceData> dataBuffer = new ArrayList<>();


    public void putData(String part) {
        mainBuffer.append(part);

        String dataLine = mainBuffer.toString().replaceAll(" ", "");
        System.out.println(dataLine);

        String extracted = tryToExtract(dataLine);

        if (null != extracted) {
            System.out.println("RES = " + extracted);

            String[] parts = extracted.split(",");
            if (2 == parts.length) {
                double gj = 0.0;
                double cg = 0.0;

                if (parts[0].startsWith(GJ_START)) {
                    gj = Double.parseDouble(parts[0].substring(GJ_START.length()));
                }

                if (parts[1].startsWith(CG_START)) {
                    cg = Double.parseDouble(parts[1].substring(CG_START.length()));
                }

                dataBuffer.add(new DeviceData(gj, cg));
            }

            mainBuffer = new StringBuffer();
        }

        // на случай если какая то ошибка и накопилось много хлама
        if (32 < dataLine.length()) {
            String[] parts = dataLine.split("[0-9\\-\\.]+");
            String lastPart = parts[parts.length - 1];

            mainBuffer = new StringBuffer();
            // на сулчай если CG разобьет на C и G
            if (!lastPart.startsWith(","))
                mainBuffer.append(lastPart);
        }
    }

    private String tryToExtract(String dataLine) {
        Matcher oldMatcher = OLD_DATA_PATTERN.matcher(dataLine);
        if (oldMatcher.find()) {
            return oldMatcher.group(0);
        }

        Matcher matcher = FULL_DATA_PATTERN.matcher(dataLine);
        if (matcher.find()) {
            return matcher.group(0);
        }

        return null;
    }

    public synchronized List<DeviceData> getData() {
        try {
            return new ArrayList<>(dataBuffer);
        } finally {
            dataBuffer.clear();
        }
    }

    public synchronized boolean hasNewData() {
        return !dataBuffer.isEmpty();
    }
}
