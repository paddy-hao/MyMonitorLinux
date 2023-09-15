package org.example.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CPUFeqMonitorService {

    private static List<String> res = new ArrayList<>();

    public static List<String> getCPUFeq() throws IOException {
        List<String> res = new ArrayList<>();

        String cpuPresent = Files.readString(Path.of("/sys/devices/system/cpu/present")).trim();
        String[] split = cpuPresent.split("-");
        int minCpu = Integer.parseInt(split[0]);
        int maxCpu = Integer.parseInt(split[1]);

        for (int i = minCpu; i <= maxCpu; i++) {
            String freqFilePath = String.format("/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", i);
            String freq = Files.readString(Path.of(freqFilePath)).trim();
            res.add(freq);
        }

        return res;
    }


}
