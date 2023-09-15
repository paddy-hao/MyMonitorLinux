package org.example.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CPUMonitorService {

    public static float[] getCPULoad() throws IOException, InterruptedException, FileNotFoundException {
        String procStatFile = "/proc/stat";

        BufferedReader reader1 = new BufferedReader(new FileReader(procStatFile));
        String[] procStat1 = reader1.readLine().split("\\s+");
        reader1.close();

        Thread.sleep(1000);

        BufferedReader reader2 = new BufferedReader(new FileReader(procStatFile));
        String[] procStat2 = reader2.readLine().split("\\s+");
        reader2.close();

        long[] procTime1 = getProcTime(procStat1);
        long[] procTime2 = getProcTime(procStat2);

        long idle1 = procTime1[0];
        long user1 = procTime1[1];
        long sys1 = procTime1[2];
        long total1 = procTime1[3];
        long idle2 = procTime2[0];
        long user2 = procTime2[1];
        long sys2 = procTime2[2];
        long total2 = procTime2[3];

        long idleDelta = idle2 - idle1;
        long userDelta = user2 - user1;
        long sysDelta = sys2 - sys1;
        long totalDelta = total2 - total1;

        float userUsage = (1000f * userDelta / totalDelta + 5) / 10;
        float systemUsage = (1000f * sysDelta / totalDelta + 5) / 10;
        float totalUsage = (1000f * (totalDelta - idleDelta) / totalDelta + 5) / 10;

        return new float[]{userUsage, systemUsage, totalUsage};
    }

    private static long[] getProcTime(String[] procStat) {
        long idleTime = Long.parseLong(procStat[4]);
        long userTime = Long.parseLong(procStat[1]);
        long sysTime = Long.parseLong(procStat[3]);
        long totalTime = 0;
        for (int i = 1; i < procStat.length; i++) {
            totalTime += Long.parseLong(procStat[i]);
        }
        return new long[]{idleTime, userTime, sysTime, totalTime};
    }

}
