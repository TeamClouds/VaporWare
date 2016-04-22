package teamclouds.com.vaporware.communication;

/**
 * Created by bird on 4/20/16.
 */
public class SendData {

    private static final String DELIMIT = ",";

    private static final String SEND = "S";

    // Types of data to send
    private static final String DUMP_PID = "dumpPids";
    private static final String SETTING = "setting";

    // on/off
    private static final String ON = "1";
    private static final String OFF = "0";


    private static final String PIDP = "pidP";

    public static void setP(UsbService service, int value) {
        String s = SEND + DELIMIT + PIDP + DELIMIT + String.valueOf(value);
        sendData(service, s);
    }

    public static void getSettings(UsbService service) {
        String s = SETTING;
        sendData(service, s);
    }

    public static void enableDumpPid(UsbService service) {
        String s = SEND + DELIMIT + DUMP_PID + DELIMIT + ON;
        sendData(service, s);
    }

    public static void disableDumpPid(UsbService service) {
        String s = SEND + DELIMIT + DUMP_PID + DELIMIT + OFF;
        sendData(service, s);
    }

    public static void sendData(UsbService service, String rawString) {
        byte[] bytes = (rawString + "\r\n").getBytes();
        service.write(bytes);
    }
}
