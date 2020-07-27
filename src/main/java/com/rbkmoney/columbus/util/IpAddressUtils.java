package com.rbkmoney.columbus.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpAddressUtils {
    public static boolean isValid(String ip) {
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            return false;
        }
        return true;
    }

    // ip should be checked via isValid before, or null will be returned
    public static InetAddress convert(String ip) {
        try {
            return InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
        }
        return null;
    }
}
