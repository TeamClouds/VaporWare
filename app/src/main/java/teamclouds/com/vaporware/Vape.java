package teamclouds.com.vaporware;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by bird on 4/18/16.
 */
public class Vape extends Application {

    private static UsbManager mUsbManager;
    private static UsbDevice mUsbDevice;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        getManager(this);
    }

    public static UsbManager getManager(Context c) {
        if (mUsbManager == null) {
            mUsbManager = (UsbManager) c.getSystemService(USB_SERVICE);
        }
        return mUsbManager;
    }

    static void setHandler(Handler handler) {
        mHandler = handler;
    }

    static Handler getHandler() {
        return mHandler;
    }

    public static void setDevice(UsbDevice device) {
        mUsbDevice = device;
    }

    public static UsbDevice getDevice() {
        return mUsbDevice;
    }
}
