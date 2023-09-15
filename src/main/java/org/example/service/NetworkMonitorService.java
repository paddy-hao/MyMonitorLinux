package org.example.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NetworkMonitorService {

    private static final String NET_DEV_FILE = "/proc/net/dev";

    public static Map<String, Long> getNetworkSpeeds(String iface) throws IOException, InterruptedException {
        Map<String, Long> firstMeasure = readNetworkStats(iface);
        if (firstMeasure == null || !firstMeasure.containsKey("rx") || !firstMeasure.containsKey("tx")) {
            throw new RuntimeException("Unable to read network stats (first measure)");
        }

        TimeUnit.SECONDS.sleep(1);

        Map<String, Long> secondMeasure = readNetworkStats(iface);
        if (secondMeasure == null || !secondMeasure.containsKey("rx") || !secondMeasure.containsKey("tx")) {
            throw new RuntimeException("Unable to read network stats (second measure)");
        }

        long rxBytesPerSecond = (secondMeasure.get("rx") - firstMeasure.get("rx"));
        long txBytesPerSecond = (secondMeasure.get("tx") - firstMeasure.get("tx"));

        Map<String, Long> networkSpeeds = new HashMap<>();
        networkSpeeds.put("Download", rxBytesPerSecond);
        networkSpeeds.put("Upload", txBytesPerSecond);

        return networkSpeeds;
    }

    private static Map<String, Long> readNetworkStats(String iface) throws IOException {
        Map<String, Long> stats = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(NET_DEV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(iface)) {
                    String[] fields = line.trim().split("\\s+");
                    stats.put("rx", Long.parseLong(fields[1]));
                    stats.put("tx", Long.parseLong(fields[9]));
                    break;
                }
            }
        }
        return stats;
    }

    public static List<String> getNetworkInterfaces() throws IOException {
        List<String> interfaces = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "ip addr | grep -oP '(?<=^[0-9]: ).*(?=:)'");
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                interfaces.add(line.trim());
            }
        }

        return interfaces;
    }
}
