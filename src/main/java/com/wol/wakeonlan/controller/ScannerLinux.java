package com.wol.wakeonlan.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * @author jl
 * @date 2021/9/28 11:20
 */
public class ScannerLinux extends BaseScanner  {
    protected void Ping(InetAddress target) throws IOException {
        target.isReachable(1);
    }

    public ArrayList<String> Parse(String macPattern) throws IOException {
        ProcessBuilder pb2 = new ProcessBuilder("/usr/local/sbin/fping", "-asg","192.168.1.0/24");
        pb2.start();
        ProcessBuilder pb1 = new ProcessBuilder("arp", "-na");
        Process p1 = pb1.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
        String line;
        ArrayList res = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("?"))
                return null;
            String[] s = line.split(" ");
            String macAddress = s[3].toUpperCase();
            if (macAddress.contains(":")) {
                macAddress = macAddress.replaceAll(":","-");
            }
            if (macAddress.matches(macPattern)) {
                res.add(s[1].substring(1, s[1].length() - 1));
            }
        }
        return res;
    }
}
