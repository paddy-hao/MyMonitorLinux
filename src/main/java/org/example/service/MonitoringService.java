package org.example.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitoringService {
    private final ScheduledExecutorService monitoringExecutorService = Executors.newScheduledThreadPool(1);
    private final List<float[]> cpuLoads = new ArrayList<>();
    private final List<float[]> memoryLoads = new ArrayList<>();
    private final List<float[]> networkLoads = new ArrayList<>();

    public void startMonitoring(long period, TimeUnit unit) {
        monitoringExecutorService.scheduleAtFixedRate(this::monitor, 0, period, unit);
    }

    private void monitor() {
        try {
            float[] cpuLoad = CPUMonitorService.getCPULoad();
            cpuLoads.add(cpuLoad);

            long[] memInfoLongs = MemoryMonitorService.getMemInfo();
            float[] memInfoFloats = new float[memInfoLongs.length];

            for (int i = 0; i < memInfoLongs.length; i++) {
                memInfoFloats[i] = (float) memInfoLongs[i];
            }

            memoryLoads.add(memInfoFloats);

            try {
                List<String> networkInterfaces = NetworkMonitorService.getNetworkInterfaces();

                long totalDownloadSpeed = 0;
                long totalUploadSpeed = 0;
                float[] temp = new float[2];
                for (String iface : networkInterfaces) {
                    Map<String, Long> networkSpeeds = NetworkMonitorService.getNetworkSpeeds(iface);
                    totalDownloadSpeed += networkSpeeds.get("Download");
                    totalUploadSpeed += networkSpeeds.get("Upload");
                }
                temp[0] = (float) totalDownloadSpeed;
                temp[1] = (float) totalUploadSpeed;
                networkLoads.add(temp);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonObject stopMonitoringAndGenerateJSONResult() {
        monitoringExecutorService.shutdown();

        JsonArray cpuLoadsArray = new JsonArray();
        for (float[] cpuLoad : cpuLoads) {
            JsonArray cpuLoadArray = new JsonArray();
            for (float value : cpuLoad) {
                System.out.println("cpuLoad: "+value);
                cpuLoadArray.add(value);
            }
            cpuLoadsArray.add(cpuLoadArray);
        }

        JsonArray memoryLoadsArray = new JsonArray();
        for (float[] memoryLoad : memoryLoads) {
            JsonArray memoryLoadArray = new JsonArray();
            for (float value : memoryLoad) {
                memoryLoadArray.add(value);
            }
            memoryLoadsArray.add(memoryLoadArray);
        }

        JsonArray networkLoadsArray = new JsonArray();
        for (float[] networkLoad : networkLoads) {
            JsonArray networkLoadArray = new JsonArray();
            for (float value : networkLoad) {
                networkLoadArray.add(value);
            }
            networkLoadsArray.add(networkLoadArray);
        }

        JsonObject result = new JsonObject();
        result.add("cpuLoads", cpuLoadsArray);
        result.add("memoryLoads", memoryLoadsArray);
        result.add("networkLoads", networkLoadsArray);

        return result;

    }


}
