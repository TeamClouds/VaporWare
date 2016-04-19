package teamclouds.com.vaporware;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by bird on 4/19/16.
 */
public class VapeData {

    String mType = "type";
    int mSelectedTemp;
    int mActualTemp;
    int mWatts;
    long mResistance;

    final static int TYPE_POS = 0;
    final static int SELECTEDTEMP_POS = 1;
    final static int ACTUALTEMP_POS = 2;
    final static int WATTS_POST = 3;
    final static int RESISTANCE_POS = 4;

    interface VapeUpdated {
        public void onVapeUpdated(VapeData vape);
    }

    public VapeData(String type, int selectedTemp, int actualTemp, int watts, long resistance) {
        mType = type;
        mSelectedTemp = selectedTemp;
        mActualTemp = actualTemp;
        mWatts = watts;
        mResistance = resistance;
    }

    public static VapeData parseString(Context context, String data){
        String[] info = data.split(",");
        Log.d("BIRD", "SIZE:" + info.length);
        Toast.makeText(context, "got data:" + info.length, Toast.LENGTH_SHORT).show();

        if (info.length <= 5) {
            int selectedTemp = Integer.parseInt(info[SELECTEDTEMP_POS]);
            int actualTemp = Integer.parseInt(info[ACTUALTEMP_POS]);
            int watts = Integer.parseInt(info[WATTS_POST]);
            //long resistance = Long.parseLong(info[RESISTANCE_POS]);

            return new VapeData(info[TYPE_POS], selectedTemp, actualTemp, watts, 0);
        } else {
            return null;
        }
    }
}
