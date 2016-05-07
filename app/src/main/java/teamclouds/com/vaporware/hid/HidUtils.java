package teamclouds.com.vaporware.hid;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HidUtils {

    private static final String TAG = "HidUtils";

    public static byte[] HID_SIGNATURE = new byte[]{'H', 'I', 'D', 'C'};

    /**
     * @param cmd  A byte long HID command.
     * @param arg1 First HID command argument.
     * @param arg2 Second HID command argument.
     * @return A bytearray containing the full HID command.
     */
    public static byte[] generateCmd(Byte cmd, Integer arg1, Integer arg2) {
        if (cmd == null || arg1 == null || arg2 == null) {
            throw new NullPointerException("Must not have null inputs");
        }

        final ByteBuffer command = ByteBuffer.allocate(18);
        command.order(ByteOrder.nativeOrder());
        command.put(cmd); // command
        command.put((byte) 14); // length
        command.putInt(arg1); // arg1
        command.putInt(arg2); // arg2
        command.put(HID_SIGNATURE); // signature
        command.putInt(checkSum(command.array()));

        return command.array();
    }

    private static final short checkSum(byte[] bytes) {
        short sum = 0;
        for (byte b : bytes) {
            sum += (short) (0xFF & b);
        }
        return sum;
    }


}
