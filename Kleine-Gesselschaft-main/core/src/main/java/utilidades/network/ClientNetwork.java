package utilidades.network;

import utilidades.interfaces.GameController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * Cliente UDP simple para sincronizar 2 jugadores.
 */
public class ClientNetwork extends Thread {
    private final GameController controller;
    private final InetAddress serverIp;
    private final int serverPort;
    private DatagramSocket socket;
    private volatile boolean running = true;
    private volatile boolean connected = false;
    private int playerId = -1;

    public ClientNetwork(GameController controller, String host, int port) throws IOException {
        this.controller = controller;
        this.serverIp = InetAddress.getByName(host);
        this.serverPort = port;
        try {
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(0);
        } catch (SocketException e) {
            throw new IOException("No se pudo crear socket cliente", e);
        }
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running && socket != null && !socket.isClosed()) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                socket.receive(packet);
                processMessage(packet);
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        }
    }

    private void processMessage(DatagramPacket packet) {
        String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
        String[] parts = msg.split(":");
        if (parts.length == 0) return;
        switch (parts[0]) {
            case "Connected":
                if (parts.length >= 2) {
                    playerId = Integer.parseInt(parts[1]);
                    connected = true;
                    controller.connect(playerId);
                }
                break;
            case "Start":
                controller.startGame();
                break;
            case "Move":
                if (parts.length >= 4) {
                    int pid = Integer.parseInt(parts[1]);
                    float x = Float.parseFloat(parts[2]);
                    float y = Float.parseFloat(parts[3]);
                    controller.move(pid, x, y);
                }
                break;
            case "Disconnect":
                controller.backToMenu();
                break;
            default:
                break;
        }
    }

    public void sendConnect() {
        send("Connect");
    }

    public void sendMove(float x, float y) {
        if (!connected) return;
        send("Move:" + x + ":" + y);
    }

    private void send(String msg) {
        if (socket == null || socket.isClosed()) return;
        byte[] data = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, serverIp, serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        running = false;
        if (socket != null && !socket.isClosed()) socket.close();
        interrupt();
    }

    public boolean isConnected() {
        return connected;
    }

    public int getPlayerId() {
        return playerId;
    }
}
