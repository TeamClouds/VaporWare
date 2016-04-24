package teamclouds.com.vaporware.parsing;

import static teamclouds.com.vaporware.parsing.ParseInput.cleanInt;

/**
 * Created by bird on 4/19/16.
 */
public class PidData extends VapeInput {

    final static int SELECTEDTEMP_POS = 1;
    final static int ACTUALTEMP_POS = 2;
    final static int WATTS_POS = 3;
    final static int RESISTANCE_POS = 4;

    public int mSelectedTemp;
    public int mActualTemp;
    public int mWatts;
    public long mResistance;

    public PidData(int selectedTemp, int actualTemp, int watts, int resistance) {
        this.mActualTemp = actualTemp;
        this.mSelectedTemp = selectedTemp;
        this.mResistance = resistance;
        this.mWatts = watts;
    }

    public static PidData pidData(String[] info){
        int selectedTemp = cleanInt(info[SELECTEDTEMP_POS]);
        int actualTemp = cleanInt(info[ACTUALTEMP_POS]);
        int watts = cleanInt(info[WATTS_POS]);
        int resistance = cleanInt(info[RESISTANCE_POS]);
        return new PidData(selectedTemp, actualTemp, watts, resistance);
    }
}
