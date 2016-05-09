package teamclouds.com.vaporware.hid;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HidUtils {

    private static final String TAG = "HidUtils";
    private static final boolean DEBUG = true;

    private static byte[] HID_SIGNATURE = new byte[]{'H', 'I', 'D', 'C'};

    public static final int DATA_FLASH_SIZE = 2048;

    private static final int TRANSFER_TIMEOUT = 1000;

    private static final int END_POINT_READ = 0;
    private static final int END_POINT_WRITE = 1;


    /**
     * Reset the device
     * @param connection
     * @param writeEndpoint
     * @return the result
     */
    public static int reset(UsbDeviceConnection connection, UsbEndpoint writeEndpoint) {
        final byte[] reset = HidUtils.generateCmd((byte) 0xB4, 0, 0);
        return connection.bulkTransfer(writeEndpoint, reset, reset.length, TRANSFER_TIMEOUT);
    }

    /**
     * Reset data flash
     * @param connection
     * @param writeEndpoint
     * @return result
     */
    public static int resetDataFlash(UsbDeviceConnection connection, UsbEndpoint writeEndpoint) {
        final byte[] reset = HidUtils.generateCmd((byte) 0x7C, 0, 0);
        return connection.bulkTransfer(writeEndpoint, reset, reset.length, TRANSFER_TIMEOUT);
    }

    /**
     * Reads the device data flash
     * @param connection
     * @param usbInterface
     * @return the device parsed DataFlash object, or null if parsing failed.
     */
    public static DataFlash readDataFlash(UsbDeviceConnection connection, UsbInterface usbInterface) {
        final byte[] readCmd = HidUtils.generateCmd((byte) 0x35, 0, DATA_FLASH_SIZE);
        int result = connection.bulkTransfer(usbInterface.getEndpoint(END_POINT_WRITE), readCmd,
                readCmd.length, TRANSFER_TIMEOUT);
        log("read data commmand result: " + result);

        if (result < 0) {
            return null;
        }

        byte[] dataBuffer = new byte[DATA_FLASH_SIZE];
        result = connection.bulkTransfer(usbInterface.getEndpoint(END_POINT_READ), dataBuffer,
                dataBuffer.length, TRANSFER_TIMEOUT);
        log("read data result: " + result);

        if (result < 0) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(dataBuffer);
        buffer.order(ByteOrder.nativeOrder());
        final int checksum = buffer.getInt();
        buffer = buffer.slice();

        final int localChecksum = checkSum(buffer);
        if (localChecksum == checksum) {
            return new DataFlash(buffer);
        } else {
            Log.e(TAG, "checksum mismatch, local checksum is: " + localChecksum +
                    " and read checksum: " + checksum);
        }
        return null;
    }

    /**
     * Write the device's data flash
     * @param connection
     * @param usbInterface
     * @param data data to write
     * @return number of bytes written
     */
    public static int writeDataFlash(UsbDeviceConnection connection, UsbInterface usbInterface,
            byte[] data) {
        final byte[] writeCmd = HidUtils.generateCmd((byte) 0x53, 0, DATA_FLASH_SIZE);
        int result = connection.bulkTransfer(usbInterface.getEndpoint(END_POINT_WRITE), writeCmd,
                writeCmd.length, TRANSFER_TIMEOUT);

        log("write command result: " + result);
        if (result < 0) {
            return result;
        }

        ByteBuffer buffer = ByteBuffer.allocate(DATA_FLASH_SIZE);
        buffer.putInt(checkSum(data));
        buffer.put(data);

        final byte[] sendData = buffer.array();
        return connection.bulkTransfer(usbInterface.getEndpoint(END_POINT_WRITE), sendData,
                sendData.length, TRANSFER_TIMEOUT);
    }

    /**
     * writes AP ROM to the device
     * @param connection
     * @param usbInterface
     * @param data the AP ROM data
     * @return number of bytes written
     */
    public static int writeApRom(UsbDeviceConnection connection, UsbInterface usbInterface,
            byte[] data) {
       return writeFlash(connection, usbInterface, data, 0);
    }

    /**
     * writes logo data to the device
     * @param connection
     * @param usbInterface
     * @param data the logo data
     * @return number of bytes written
     */
    public static int writeLogo(UsbDeviceConnection connection, UsbInterface usbInterface,
            byte[] data) {
        return writeFlash(connection, usbInterface, data, 102400);
    }

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

    /**
     * @param bytes
     * @return the sum of contents in the byte array
     */
    public static final int checkSum(byte[] bytes) {
        int sum = 0;
        for (byte b : bytes) {
            sum += (0xFF & b);
        }
        return sum;
    }

    /**
     * @param buffer
     * @return the sum of contents in teh buffer
     */
    public static final int checkSum(ByteBuffer buffer) {
        buffer.rewind();
        int sum = 0;
        while (buffer.hasRemaining()) {
            sum += buffer.get() & 0xFF;
        }
        buffer.rewind();
        return sum;
    }

    /**
     * Writes raw data to the device
     * @param connection
     * @param usbInterface
     * @param raw the data to write
     * @return the number of bytes written
     */
    private static int write(UsbDeviceConnection connection, UsbInterface usbInterface,
            byte[] raw) {
        ByteBuffer data = ByteBuffer.wrap(raw);
        data.order(ByteOrder.LITTLE_ENDIAN);

        byte[] buffer = new byte[16384];
        int written = 0;
        int read =0;
        while (written < raw.length) {
            read = Math.min(data.remaining(), buffer.length);
            data.get(buffer, 0, read);
            int result = connection.bulkTransfer(usbInterface.getEndpoint(END_POINT_WRITE),
                    buffer, read, TRANSFER_TIMEOUT);
            if (result > 0) {
                written += result;
                log("wrote " + written + " bytes, result: " + result + " at position: "
                        + data.position());
            } else {
                log("failed writing " + read + " with result: " + result);
                break;
            }
        }
        return written;
    }

    /**
     * Write to device flash
     * @param connection
     * @param usbInterface
     * @param data  what to write
     * @param startPos where to start writing it
     * @return
     */
    private static int writeFlash(UsbDeviceConnection connection, UsbInterface usbInterface,
            byte[] data, int startPos) {
        final byte[] writeCmd = HidUtils.generateCmd((byte) 0xC3, startPos, data.length);

        int result = connection.bulkTransfer(usbInterface.getEndpoint(END_POINT_WRITE),
                writeCmd, writeCmd.length, TRANSFER_TIMEOUT);
        if (result < 0) {
            log("fail sending write command, result was: " + result);
            return result;
        }

        log("write command result: " + result);
        if (result < 0) {
            return result;
        }
        return write(connection, usbInterface, data);
    }

    private static void log(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
