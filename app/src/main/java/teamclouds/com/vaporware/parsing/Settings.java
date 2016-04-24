package teamclouds.com.vaporware.parsing;

import teamclouds.com.vaporware.communication.UsbService;

import static teamclouds.com.vaporware.communication.SendData.DELIMIT;
import static teamclouds.com.vaporware.communication.SendData.SEND;
import static teamclouds.com.vaporware.communication.SendData.sendData;

public class Settings extends VapeInput {

    public int pidP = 0;
    public int pidI = 0;
    public int pidD = 0;

    public static final String P = "pidP";
    public static final String I = "pidI";
    public static final String D = "pidD";
    public static final String SETTING = "setting";

    private Settings(int P, int I, int D) {
        this.pidP = P;
        this.pidD = D;
        this.pidI = I;
    }

    private static Settings sSettings = null;

    public static Settings getSettings() {
        if (sSettings == null) {
            sSettings = new Settings(0, 0, 0);
        }
        return sSettings;
    }

    static void putToSettings(String type, int dataInt) {
        switch(type) {
            case P:
                getSettings().pidP = dataInt;
                break;
            case I:
                getSettings().pidI = dataInt;
                break;
            case D:
                getSettings().pidD = dataInt;
                break;
        }
    }

    public static void sendToVape(UsbService service, String type, int value) {
        String s = SEND + DELIMIT + type + DELIMIT + String.valueOf(value);
        sendData(service, s);
    }

    public static void getSettingsFromVape(UsbService service) {
        String s = SETTING;
        sendData(service, s);
    }
}