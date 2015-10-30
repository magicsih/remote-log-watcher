package app.task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import app.model.Log;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class DatagramPacketHandlingTask implements Runnable {

    private final DatagramPacket packet;
    private final SimpMessagingTemplate template;

    public DatagramPacketHandlingTask(SimpMessagingTemplate template, DatagramPacket packet) {
        super();
        packet.retain();
        this.packet = packet;
        this.template = template;
    }

    @Override
    public void run() {
        String senderIp = packet.sender().getHostName();
        String log = packet.content().toString(CharsetUtil.UTF_8);

        if (IsSerializedJavaObject(packet)) {
            byte[] bytes = new byte[packet.content().readableBytes()];
            int readerIndex = packet.content().readerIndex();
            packet.content().getBytes(readerIndex, bytes);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                try (ObjectInputStream in = new ObjectInputStream(bais)) {
                    Object readObject = in.readObject();

                    if (readObject.getClass().equals(Log4jLogEvent.class)) {
                        handleLog4jLogEvent(senderIp, (Log4jLogEvent) readObject);
                    } else {
                        throw new RuntimeException("Not yet implemented this java class" + readObject.getClass().toString());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (log.startsWith("[")) {
                handleCustomLog(senderIp, log);
            } else {
                throw new RuntimeException("Unknown message");
            }
        }

        this.packet.release();
    }

    /**
     * Handle undefined log messages
     *
     * @param senderIp
     * @param log
     */
    private void handleCustomLog(String senderIp, String log) {
        int seperator = log.indexOf("]");
        String[] infos = log.substring(1, seperator).split(";");
        String appId = infos[0];
        String deviceId = infos[1];
        String logType = infos[2];
        String message = log.substring(seperator + 1, log.length());

        template.convertAndSend("/topic/" + appId, new Log(deviceId, senderIp, logType, message));
    }

    /**
     * Handle log4j event messages
     *
     * @param senderIp
     * @param logEvent
     */
    private void handleLog4jLogEvent(String senderIp, Log4jLogEvent logEvent) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(logEvent.getThreadName());
        sb.append(")");
        sb.append(logEvent.getSource().getClassName());
        sb.append(".");
        sb.append(logEvent.getSource().getMethodName());
        sb.append("(");
        sb.append(logEvent.getSource().getLineNumber());
        sb.append(") - ");
        sb.append(logEvent.getMessage().getFormattedMessage());

        template.convertAndSend("/topic/was", new Log(senderIp, senderIp, logEvent.getLevel().name(), sb.toString()));
    }

    /**
     * Check if packet is a serialized java object.
     *
     * @param packet
     * @return
     */
    private boolean IsSerializedJavaObject(DatagramPacket packet) {
        return packet.content().getByte(0) == -84 && packet.content().getByte(1) == -19;
    }

    /**
     * change byte array to hex string
     *
     * @param ba
     * @return
     */
    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }

}
