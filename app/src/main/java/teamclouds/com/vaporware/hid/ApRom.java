package teamclouds.com.vaporware.hid;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;

public class ApRom {

    private static final String TAG = "ApRom";

    byte[] mData;

    public ApRom(byte[] data) {
        mData = data;
    }

    public void convert() {
        for (int i = 0; i < mData.length; i++) {
            mData[i] = Integer.valueOf(mData[i] ^ convert(mData.length, i))
                    .byteValue();
        }
    }

    public byte[] getData() {
        return mData;
    }

    public void verify(Set<String> products, int hwVersion) throws VerifyException {
        final String stringData = new String(mData);
        if (!stringData.contains("Joyetech APROM")) {
            throw new VerifyException("Firmware manufacturer verification failed.");
        }

        ByteBuffer data = ByteBuffer.wrap(mData);
        data.order(ByteOrder.nativeOrder());

        int maxHwVersion = 0;
        int maxHwVersionIdx = 0;
        int idx = -1;
        for (String productId : products) {
            idx = indexOf(mData, productId.getBytes());
            if (idx > 0) {
                maxHwVersionIdx = idx + productId.getBytes().length;
                maxHwVersion = data.getShort(maxHwVersionIdx);
                Log.d(TAG, "found max hw version: " + maxHwVersion);
            }
        }

        if (idx < 0) {
            throw new VerifyException("Firmware manufacturer verification failed:" +
                    " product not supported");
        }

        if (maxHwVersion < hwVersion) {
            throw new VerifyException("Firmware manufacturer verification failed: " +
                    "bad hardware version, hwVersion: " + hwVersion + ", max: " + maxHwVersion);
        }
    }

    private int indexOf(byte[] outerArray, byte[] smallerArray) {
        for (int i = 0; i < outerArray.length - smallerArray.length + 1; ++i) {
            boolean found = true;
            for (int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i + j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    private static byte convert(int fileSize, int index) {
        return Integer.valueOf(fileSize + 408376 + index - fileSize / 408376)
                .byteValue();
    }

    public static class VerifyException extends Exception {
        public VerifyException(String detailMessage) {
            super(detailMessage);
        }
    }

}
