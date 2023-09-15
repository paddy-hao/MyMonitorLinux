package org.example.service;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiskMonitorService {

    private static HashMap<FileStore, List<Long>> res = new HashMap<>();

    public static HashMap<FileStore, List<Long>> getDiskInfo(){
        Iterable<Path> rootPaths = FileSystems.getDefault().getRootDirectories();

        for (Path rootPath : rootPaths) {
            try {
                FileStore store = Files.getFileStore(rootPath);
                List<Long> diskInfo = new ArrayList<>();

                long totalSpace = store.getTotalSpace() / (1024 * 1024 * 1024); // GB
                long usableSpace = store.getUsableSpace() / (1024 * 1024 * 1024); // GB
                long usedSpace = totalSpace - usableSpace;

                diskInfo.add(totalSpace);
                diskInfo.add(usableSpace);
                diskInfo.add(usedSpace);

                res.put(store, diskInfo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return res;
    }

}
