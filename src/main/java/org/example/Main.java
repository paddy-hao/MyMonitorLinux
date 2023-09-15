package org.example;

import com.google.gson.JsonObject;
import org.example.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Main {
    private static float[] cpuLoad;

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

        try {
            cpuLoad = CPUMonitorService.getCPULoad();

            System.out.println("User: " + cpuLoad[0] + "%");
            System.out.println("System: " + cpuLoad[1] + "%");
            System.out.println("Total CPU usage is: " + cpuLoad[2] + "%");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        try {
//            List<String> cpuFeq = CPUFeqMonitorService.getCPUFeq();
//
//            for (int i = 0; i < cpuFeq.size(); i++) {
//                System.out.println("CPU"+i+": "+cpuFeq.get(i)+"MHz");
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        try {
            long[] memInfo = MemoryMonitorService.getMemInfo();

            System.out.println("Total memory: " + memInfo[0] + " MB");
            System.out.println("Used memory: " + memInfo[1] + " MB");
            System.out.println("Available memory: " + memInfo[2] + " MB");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            List<String> networkInterfaces = NetworkMonitorService.getNetworkInterfaces();

            long totalDownloadSpeed = 0;
            long totalUploadSpeed = 0;
            for (String iface : networkInterfaces) {
                Map<String, Long> networkSpeeds = NetworkMonitorService.getNetworkSpeeds(iface);
                totalDownloadSpeed += networkSpeeds.get("Download");
                totalUploadSpeed += networkSpeeds.get("Upload");
            }

            System.out.println("Total download speed: " + totalDownloadSpeed + " bytes/s");
            System.out.println("Total upload speed: " + totalUploadSpeed + " bytes/s");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HashMap<FileStore, List<Long>> diskInfo = DiskMonitorService.getDiskInfo();
        for (FileStore store : diskInfo.keySet()) {
            System.out.println("FileStore: " + store);
            List<Long> infoList = diskInfo.get(store);

            for (int i = 0; i < infoList.size(); i++) {
                System.out.println("Info " + (i + 1) + ": " + infoList.get(i));
            }

            System.out.println("---------------------------");
        }

        File staticInfoFile = OtherResourceMonitorService.getStaticInfoFile();
        try {
            Scanner scanner = new Scanner(staticInfoFile);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

//        MonitoringService monitoringService = new MonitoringService();
//        monitoringService.startMonitoring(2, TimeUnit.SECONDS);
//
//        try {
//            Thread.sleep(4 * 1000);
//        } catch (InterruptedException e) {
//            // Handle interruption
//            e.printStackTrace();
//        }
//
//        File resultFile = monitoringService.stopMonitoringAndGenerateJSONResult();
//
//        try {
//            String content = new String(Files.readAllBytes(resultFile.toPath()));
//            System.out.println(content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        JsonObject jsonObject = monitoringService.stopMonitoringAndGenerateJSONResult();
//        System.out.println(jsonObject.toString());
    }
}