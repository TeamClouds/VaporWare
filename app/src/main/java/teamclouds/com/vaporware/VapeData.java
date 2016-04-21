package teamclouds.com.vaporware;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import static teamclouds.com.vaporware.VapeParsing.PID;

/**
 * Created by bird on 4/19/16.
 */
public class VapeData implements Parcelable {

    public final static String ACTION_DATA_RECEIVED = "com.teamclouds.vaporware.action_data_received";
    public static final String VAPE_DATA = "vape_data";

    String mType;
    int mSelectedTemp;
    int mActualTemp;
    int mWatts;
    long mResistance;

    private final static int TYPE_POS = 0;
    private final static int SELECTEDTEMP_POS = 1;
    private final static int ACTUALTEMP_POS = 2;
    private final static int WATTS_POST = 3;
    private final static int RESISTANCE_POS = 4;

    private VapeData(String type, int selectedTemp, int actualTemp, int watts, int resistance) {
        this.mType = type;
        this.mActualTemp = actualTemp;
        this.mSelectedTemp = selectedTemp;
        this.mResistance = resistance;
        this.mWatts = watts;
    }

    private VapeData(Parcel in) {
        mType = in.readString();
        mSelectedTemp = in.readInt();
        mActualTemp = in.readInt();
        mWatts = in.readInt();
        mResistance = in.readLong();
    }

    public static final Creator<VapeData> CREATOR = new Creator<VapeData>() {
        @Override
        public VapeData createFromParcel(Parcel in) {
            return new VapeData(in);
        }

        @Override
        public VapeData[] newArray(int size) {
            return new VapeData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mType);
        parcel.writeInt(mSelectedTemp);
        parcel.writeInt(mActualTemp);
        parcel.writeInt(mWatts);
        parcel.writeLong(mResistance);
    }

    public static VapeData parseString(String data){
        String[] info = data.split(",");
        Log.v("BIRD", "Got data:" + data);
        if (data.startsWith(PID)) {
            int selectedTemp = Integer.parseInt(info[SELECTEDTEMP_POS]);
            int actualTemp = Integer.parseInt(info[ACTUALTEMP_POS]);
            int watts = Integer.parseInt(info[WATTS_POST]);
            // TODO: find out why we need to strip resistance
            int resistance = Integer.parseInt(info[RESISTANCE_POS].replaceAll("[^\\d.]", ""));

            return new VapeData(info[TYPE_POS], selectedTemp, actualTemp, watts, resistance);
        } else {
            return null;
        }
    }
}
