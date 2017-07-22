package me.vittorio_io.openhostseditor.model;

import android.os.NetworkOnMainThreadException;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * Created by vittorio on 17/07/17.
 */

public class HostRule {
    private final InetAddress ip;
    private final String url;

    public HostRule(InetAddress ip, String url) {
        this.ip = ip;
        this.url = url;
    }

    public static HostRule fromHostLine(String hostLine) throws MalformedURLException, UnknownHostException {
        hostLine = hostLine.trim();

        if (hostLine.indexOf('#') == 0)
            return null;

        // Removes whatever its after the comment
        hostLine = hostLine.split("#", 1)[0];


        String[] split = hostLine.split("\\s+");

        if (split.length < 2) return null;

        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(split[0]);
        } catch (NetworkOnMainThreadException e) {
            System.out.println(">>>>> " + hostLine);
        }
        String url = split[1];

        return new HostRule(ip, url);
    }

    public String getUrl() {
        return url;
    }

    public InetAddress getIp() {
        return ip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (obj instanceof HostRule) {
            HostRule obj1 = (HostRule) obj;

            return obj1.getIp().equals(this.getIp()) && obj1.getUrl().equals(this.getUrl());
        } else return false;
    }

    @Override
    public String toString() {
        return this.getIp().toString().substring(1) + " " + this.getUrl();
    }
}
