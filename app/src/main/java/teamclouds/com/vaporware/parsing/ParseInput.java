package teamclouds.com.vaporware.parsing;

import static teamclouds.com.vaporware.parsing.PidData.*;
import static teamclouds.com.vaporware.parsing.Settings.getSettings;
import static teamclouds.com.vaporware.parsing.Settings.putToSettings;

/**
 * Created by bird on 4/23/16.
 */
public class ParseInput {

    private static final int TYPE = 0;

    // Types
    private static final String PID = "PID";
    private static final String SETTING = "setting";
    private static final String ATOMINFO = "atomInfo";

    private class Data {
        final static int TYPE = 1;
        final static int DATA = 2;
    }

    public static VapeInput inputString(String data) {
        String[] info = data.split(",");
        switch(info[TYPE]) {
            case PID:
                // PID is unique in that every bit of data is in each line
                return pidData(info);
            case SETTING:
                putToSettings(info[Data.TYPE], cleanInt(info[Data.DATA]));
                return getSettings();
            case ATOMINFO:
                break;
        }
        return null;
    }

    public static int cleanInt(String data) {
        return Integer.parseInt(data.replaceAll("[^\\d.]", ""));
    }

}
