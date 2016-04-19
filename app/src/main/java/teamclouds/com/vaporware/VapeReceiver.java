package teamclouds.com.vaporware;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

/**
 * Created by bird on 4/18/16.
 */
public class VapeReceiver extends BroadcastReceiver implements UsbSerialInterface.UsbReadCallback {

    public static final String ACTION_USB_PERMISSION = "com.teamcloud.vapeware.USB_PERMISSION";

    Context mContext;

    @Override
    public void onReceivedData(byte[] bytes) {
        String data = null;
        try {
            data = new String(bytes, "UTF-8");
            data.concat("/n");
            Message message = Vape.getHandler().obtainMessage();
            message.obj = data;
            message.sendToTarget();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
            boolean granted =
                    intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
            Toast.makeText(context, "GOT BROADCAST", Toast.LENGTH_SHORT).show();
            if (granted) {
                UsbDeviceConnection connection = Vape.getManager(context).openDevice(
                        Vape.getDevice());
                UsbSerialDevice serialPort = UsbSerialDevice.createUsbSerialDevice(
                        Vape.getDevice(), connection);
                if (serialPort != null) {
                    if (serialPort.open()) { //Set Serial Connection Parameters.
                        serialPort.setBaudRate(9600);
                        serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                        serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                        serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                        serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                        serialPort.read(this);
                    } else {
                        Toast.makeText(context, "port not open", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "port is null", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "PERM NOT GRANTED", Toast.LENGTH_LONG).show();
            }
        }
    }

}
