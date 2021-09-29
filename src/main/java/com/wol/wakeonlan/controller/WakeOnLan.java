package com.wol.wakeonlan.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jl
 * @date 2021/9/27 17:18
 */
@Slf4j
@RestController
@RequestMapping(value = "/app/wakeOnLan/")
public class WakeOnLan {

    public static final int PORT = 9;


    @RequestMapping(value = "openTheMachine.html")
    @ResponseBody
    public Object open(@RequestParam String macAddress) {
        Map<String,Object> map = new HashMap<>();
        map.put("respCode","0000");
        map.put("respMesg","");
        if (StringUtils.isBlank(macAddress)) {
            map.put("respCode","0001");
            map.put("respMesg","Mac地址不能为空");
            return map;
        }
        String ipStr = "";
        try {
            ArrayList<String> ipStrs = execute(macAddress);
            if (ipStrs != null && ipStrs.size() > 0) {
                ipStr = ipStrs.get(0);
            }
        } catch (Exception e) {
            map.put("respCode","0001");
            map.put("respMesg","根据MAC地址获取ip失败");
            return map;
        }

        if (StringUtils.isBlank(ipStr)) {
            map.put("respCode","0001");
            map.put("respMesg",",没有获取到可用地址");
            return map;
        }
        String macStr = macAddress;

        try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            System.out.println("Wake-on-LAN packet sent.");
        }
        catch (Exception e) {
            System.out.println("Failed to send Wake-on-LAN packet: + e");
            System.exit(1);
        }
        return map;
    }

    /**
     * 封装报文
     * @param
     * @return
     * Created by jl on 2021/9/28 10:09
     */
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

    public static ArrayList<String> execute(String macPattern) throws IOException {
        IScanner scanner;
        if (SystemUtils.IS_OS_WINDOWS)
            scanner = new ScannerWin();
        else
            scanner = new ScannerLinux();

        return scanner.Parse(macPattern);
    }

}
