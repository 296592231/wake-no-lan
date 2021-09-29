package com.wol.wakeonlan.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * @author jl
 * @date 2021/9/28 10:25
 */
public class ScannerWin extends BaseScanner {
    protected void Ping(InetAddress target) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("ping", "-w", "1", "-n", "1", target.getHostAddress());
        pb.start();
    }

    public ArrayList<String> Parse(String macPattern) throws IOException {
        //执行命令行参数
        ProcessBuilder pb = new ProcessBuilder("arp", "-a");
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(),"UTF-8"));
        String line;
        ArrayList res = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("  "))
                continue;
            if (line.matches(".*Internet Address.*"))
                continue;
            String[] s = line.trim().split(" +");
            String ipAddress = s[0];
            String macAddress = s[1].toUpperCase();
            if (macAddress.contains(":")) {
                macAddress = macAddress.replaceAll(":","-");
            }
            if (macAddress.matches(macPattern)) {
                res.add(ipAddress);
                return res;
            }
        }
        return res;
    }
}
