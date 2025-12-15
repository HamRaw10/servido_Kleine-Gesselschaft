package utilidades.network;

import java.net.InetAddress;

public class Client {

    private String id;
    private int num;
    private InetAddress ip;
    private int port;
    private volatile long lastSeenMs = System.currentTimeMillis();

    public Client(int num, InetAddress ip, int port) {
        this.num = num;
        this.id = ip.toString() + ":" + port;
        this.ip = ip;
        this.port = port;
        this.lastSeenMs = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getNum() {
        return num;
    }

    public long getLastSeenMs() {
        return lastSeenMs;
    }

    public void touch() {
        lastSeenMs = System.currentTimeMillis();
    }
}
