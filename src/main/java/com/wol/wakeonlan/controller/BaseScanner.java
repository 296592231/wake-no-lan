package com.wol.wakeonlan.controller;

import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author jl
 * @date 2021/9/28 10:31
 */
public abstract class BaseScanner implements IScanner  {
    protected abstract void Ping(InetAddress target) throws IOException;

    private static ArrayList<InterfaceAddress> getMyAddresses() throws IOException {
        ArrayList<InterfaceAddress> res = new ArrayList<InterfaceAddress>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface i = interfaces.nextElement();
            if (i.isLoopback() || !i.isUp())
                continue;
            res.addAll(i.getInterfaceAddresses());
        }
        return res;
    }

    public void Scan(String ipPattern) throws IOException {
        for (InterfaceAddress ia : getMyAddresses()) {
            if (!ia.getAddress().isSiteLocalAddress())
                continue;
            String ias = ia.getAddress().getHostAddress();
            if (ipPattern != null && !ias.matches(ipPattern))
                continue;
            SubnetUtils subnet = new SubnetUtils(ia.getAddress().getHostAddress() + "/" + ia.getNetworkPrefixLength());
            for (String as : subnet.getInfo().getAllAddresses()) {
                InetAddress a = InetAddress.getByName(as);
                Ping(a);
            }
        }
    }
}
