package teamclouds.com.vaporware;

import android.util.Log;

import static teamclouds.com.vaporware.VapeParsing.PID;

/**
 * Created by bird on 4/21/16.
 */
public class SettingsObject {

    int pidP = 0;
    int pidI = 0;
    int pidD = 0;

    public SettingsObject(int P, int I, int D) {
        this.pidP = P;
        this.pidD = D;
        this.pidI = I;
    }

    public static SettingsObject parseString(SettingsObject settings, String data){
        if (data.startsWith("setting")){
            Log.v("BIRD", "Got settings:" + data);
            String[] info = data.split(",");
            String type = info[1];
            String typeData = info[2];
            return writeToSettings(settings, type, typeData);
        } else {
            return null;
        }
    }

    public static SettingsObject writeToSettings(SettingsObject settings,
            String type, String data) {

        int dataInt = Integer.parseInt(data.replaceAll("[^\\d.]", ""));
        if (settings == null) {
            settings = new SettingsObject(0, 0, 0);
        }
        switch(type) {
            case "pidP":
                settings.pidP = dataInt;
                break;
            case "pidI":
                settings.pidI = dataInt;
                break;
            case "pidD":
                settings.pidD = dataInt;
                break;
        }
        return settings;

    }
}
