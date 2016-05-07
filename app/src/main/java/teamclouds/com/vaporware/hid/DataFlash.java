package teamclouds.com.vaporware.hid;

import java.nio.ByteBuffer;

/**
 * Created by roman on 5/7/16.
 */
public class DataFlash {

    int mHwVersion;
    int mBootFlag;
    String mProductId;
    int mFirmwareVersion;
    int mUnknown1, mUnknown2;

    public DataFlash(ByteBuffer buffer) {
        mHwVersion = buffer.getInt(4);
        mBootFlag = buffer.get(9);

        byte[] strBuff = new byte[4];
        buffer.get(strBuff, 312, 4);
        mProductId = new String(strBuff);

        mFirmwareVersion = buffer.getInt(256);
        mUnknown1 = buffer.getInt(260);
        mUnknown2 = buffer.getInt(264);
    }

}
