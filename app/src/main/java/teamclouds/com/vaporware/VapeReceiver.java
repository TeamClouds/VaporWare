package teamclouds.com.vaporware;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import static android.hardware.usb.UsbManager.EXTRA_DEVICE;

/**
 * Created by bird on 4/18/16.
 */
public class VapeReceiver extends BroadcastReceiver {

    public static final String ACTION_USB_PERMISSION = "com.teamcloud.vapeware.USB_PERMISSION";

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        switch(action) {
            case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                Bundle bundle = intent.getExtras();
                UsbDevice device = (UsbDevice) bundle.get(EXTRA_DEVICE);
                int deviceVID = device.getVendorId();
                if (deviceVID == 1046) // VAPE VAPE VAPE
                {
                    Toast.makeText(context, "Device ID gotten", Toast.LENGTH_LONG).show();
                    Vape.setDevice(device);

                    if (Vape.getManager(context).hasPermission(device)) {
                        startServiceForPermissionGranted(context);

                    } else {
                        // We need to somehow tell the user that
                        // we need to get this sweet permission
                        // maybe a "Give permissions button?
                        PendingIntent pi = PendingIntent.getBroadcast(context, 0,
                                new Intent(VapeReceiver.ACTION_USB_PERMISSION), 0);
                        Vape.getManager(context).requestPermission(device, pi);
                    }

                }
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED:
                break;
            case ACTION_USB_PERMISSION:
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    startServiceForPermissionGranted(context);
                } else {
                    Toast.makeText(context, "PERM NOT GRANTED", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void startServiceForPermissionGranted(Context context) {
        Intent serviceIntent = getIntentForVapeService(context);
        serviceIntent.setAction(VapeService.ACTION_PERMISSION_GRANTED);
        context.startService(serviceIntent);
    }

    private Intent getIntentForVapeService(Context context) {
        return new Intent(context, VapeService.class);
    }

}
