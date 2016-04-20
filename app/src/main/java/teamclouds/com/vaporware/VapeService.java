package teamclouds.com.vaporware;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

/**
 * Created by bird on 4/19/16.
 */
public class VapeService extends IntentService implements UsbSerialInterface.UsbReadCallback {

    public static final String ACTION_PERMISSION_GRANTED = "permission_granted";
    private static final String TAG = VapeService.class.getSimpleName();

    public VapeService() {
        super(TAG);
    }

    private void openUSBConnection(Context context) {
        UsbDeviceConnection connection = Vape.getManager(context).openDevice(Vape.getDevice());
        UsbSerialDevice serialPort = UsbSerialDevice.createUsbSerialDevice(Vape.getDevice(), connection);
        if (serialPort != null) {
            if (serialPort.open()) { //Set Serial Connection Parameters.
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
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("BIRD", "got data");
        if (intent.getAction().equals(ACTION_PERMISSION_GRANTED)) {
            Log.v("BIRD", "got permission");
            openUSBConnection(getApplicationContext());
        }
    }

    @Override
    public void onReceivedData(byte[] bytes) {
        String data = null;
        try {
            data = new String(bytes, "UTF-8");
            VapeData vapeData = VapeData.parseString(data);
            Bundle bundle = new Bundle();
            bundle.putParcelable(VapeData.VAPE_DATA, vapeData);
            Intent i = new Intent(VapeData.ACTION_DATA_RECEIVED);
            i.putExtras(bundle);
            getApplicationContext().sendBroadcast(i);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
