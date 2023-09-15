package org.example.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class OtherResourceMonitorService {

    private static File static_file = new File("/static_resource.txt");


    public static File getStaticInfoFile(){
        try {
            getCPUInfo();
            getDiskInfo();
//            getNetworkInfo();
            getOSInfo();
            getHardwareInfo();
            getDeviceInfo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return static_file;
    }

    private static void getCPUInfo() throws IOException, InterruptedException {
        writeToFile(Files.readAllLines(Paths.get("/proc/cpuinfo")));
    }

    private static void getDiskInfo() throws IOException, InterruptedException {
        writeToFile(runCommand("df"));
        writeToFile(runCommand("fdisk -l"));
    }

    private static void getNetworkInfo() throws IOException, InterruptedException {
        writeToFile(runCommand("ip addr"));
    }

    private static void getOSInfo() throws IOException, InterruptedException {
        writeToFile(runCommand("uname -a"));
    }

    private static void getHardwareInfo() throws IOException, InterruptedException {
        writeToFile(runCommand("dmidecode"));
    }

    private static void getDeviceInfo() throws IOException, InterruptedException {
        writeToFile(runCommand("lspci"));
        writeToFile(runCommand("lsusb"));
    }

    private static List<String> runCommand(String command) throws IOException, InterruptedException {
        List<String> result = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        }
        result.add("\n");
        process.waitFor();
        return result;
    }

    private static void writeToFile(List<String> lines) throws IOException {
        if (!static_file.exists()) {
            static_file.createNewFile();
        }
        Files.write(Paths.get(static_file.getPath()), lines, StandardOpenOption.APPEND);
    }

}
