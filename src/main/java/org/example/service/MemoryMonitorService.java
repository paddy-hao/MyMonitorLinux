package org.example.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MemoryMonitorService {

    public static long[] getMemInfo() throws IOException {
        long memTotal = 0;
        long memFree = 0;
        long memUsed;

        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    memTotal = parseMemInfo(line);
                } else if (line.startsWith("MemFree:")) {
                    memFree = parseMemInfo(line);
                }
            }
        }

        memUsed = memTotal - memFree;

        return new long[]{memTotal, memUsed, memFree};
    }

    private static long parseMemInfo(String line) {
        return Long.parseLong(line.replaceAll("\\D+", "")) / 1024;
    }

}
