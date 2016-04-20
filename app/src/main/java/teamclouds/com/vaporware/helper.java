package teamclouds.com.vaporware;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by bird on 4/18/16.
 */
public class helper {



    public static void findVapeDevice(Context context) {
        HashMap<String, UsbDevice> usbDevices = Vape.getManager(context).getDeviceList();
        if (!usbDevices.isEmpty()) {
            for (UsbDevice device : usbDevices.values()) {
                int deviceVID = device.getVendorId();
                if (deviceVID == 1046) // VAPE VAPE VAPE
                {
                    Toast.makeText(context, "Device ID gotten", Toast.LENGTH_LONG).show();
                    Vape.setDevice(device);
                    PendingIntent pi = PendingIntent.getBroadcast(context, 0,
                            new Intent(VapeReceiver.ACTION_USB_PERMISSION), 0);
                    Vape.getManager(context).requestPermission(device, pi);
                }
            }
        }
    }
}
