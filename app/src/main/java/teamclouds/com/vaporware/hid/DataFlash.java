package teamclouds.com.vaporware.hid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataFlash {

    private static final String TAG = "DataFlash";

    Integer mHwVersion;
    byte mBootFlag;
    String mProductId;
    Integer mFirmwareVersion;
    Integer mLdRomVersion;

    public DataFlash(ByteBuffer buffer) {
        buffer.order(ByteOrder.nativeOrder());
        if (buffer.remaining() != 2044) {
            throw new UnsupportedOperationException("data flash must be 2044 bytes");
        }

        mHwVersion = buffer.getInt(4);
        mBootFlag = buffer.get(9);

        StringBuilder productId = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            productId.append((char) buffer.get(312 + i));
        }
        mProductId = productId.toString();

        mFirmwareVersion = buffer.getInt(256);
        mLdRomVersion = buffer.getInt(260);
    }

    public boolean verifyChecksum(int checksum, byte[] raw) {
        final int localChecksum = HidUtils.checkSum(raw);
        return localChecksum == checksum;
    }

    @Override
    public String toString() {
        return "DataFlash[ "
                + " mHwVersion: " + mHwVersion
                + ", mBootFlag: " + mBootFlag
                + ", mProductId: " + mProductId
                + ", mFirmwareVersion: " + mFirmwareVersion
                + ", mLdRomVersion: " + mLdRomVersion
                + " ]";
    }

}
