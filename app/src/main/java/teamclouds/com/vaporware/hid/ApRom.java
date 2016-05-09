package teamclouds.com.vaporware.hid;

import java.nio.ByteBuffer;

/**
 * Created by roman on 5/8/16.
 */
public class ApRom {

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

    public void verify() {
        // TODO
    }

    private static byte convert(int fileSize, int index) {
        return Integer.valueOf(fileSize + 408376 + index - fileSize / 408376)
                .byteValue();
    }

}
