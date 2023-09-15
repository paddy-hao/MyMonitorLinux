package org.example.controller;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.JsonObject;
import org.example.service.MonitoringService;
import org.example.service.OtherResourceMonitorService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@RestController
public class MonitorController {

    private MonitoringService monitoringService;

    @GetMapping("/startMonitoring")
    public ResponseEntity<String> startMonitoring() {
        monitoringService = new MonitoringService();
        monitoringService.startMonitoring(2, TimeUnit.SECONDS);
        return ResponseEntity.ok("Monitoring started.");
    }


    @GetMapping("/getResult")
    public ResponseEntity<String> getMonitoringResult() {
//        File resultFile = monitoringService.stopMonitoringAndGenerateJSONResult();

        JsonObject jsonObject = monitoringService.stopMonitoringAndGenerateJSONResult();

        try {
//            String content = new String(Files.readAllBytes(resultFile.toPath()));
            return ResponseEntity.ok(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error reading monitoring result.");
//            JsonObject errorObject = new JsonObject();
//            errorObject.addProperty("error", "Error reading monitoring result.");
//            return ResponseEntity.status(500).body(errorObject);
        }
    }

    @GetMapping("/getStaticResources")
    public ResponseEntity<Resource> downloadFile() {
        try {
            File staticInfoFile = OtherResourceMonitorService.getStaticInfoFile();

            Path filePath = staticInfoFile.toPath();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
