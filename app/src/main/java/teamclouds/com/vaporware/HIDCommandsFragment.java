package teamclouds.com.vaporware;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import teamclouds.com.vaporware.communication.UsbService;
import teamclouds.com.vaporware.hid.ApRom;
import teamclouds.com.vaporware.hid.DataFlash;


public class HIDCommandsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HIDCommandsFragment";

    public Command[] mCommands = new Command[] {
              new Command("Reset HID", View.generateViewId(), new Runnable() {
                  @Override
                  public void run() {
                      mUsbService.hidReset();
                  }
              }),
            new Command("Read data flash", View.generateViewId(), new Runnable() {
                @Override
                public void run() {
                    final DataFlash dataFlash = mUsbService.hidRead();
                    Log.d(TAG, "read dataflash: " + dataFlash);
                }
            }),
            new Command("Write Stock ROM v3.03", View.generateViewId(), new Runnable() {
                @Override
                public void run() {
                    final ApRom rom = getApRom(R.raw.evic_vtc_mini_v3_03);
                    if (rom != null) {
                        rom.convert();
                        mUsbService.writeApRom(rom.getData());
                    }
                }
            }),
            new Command("Write VaporWare ROM", View.generateViewId(), new Runnable() {
                @Override
                public void run() {
                    final ApRom rom = getApRom(R.raw.atomizer);
                    if (rom != null) {
                        rom.convert();
                        mUsbService.writeApRom(rom.getData());
                    }
                }
            }),

    };
    private UsbService mUsbService;

    public HIDCommandsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        mUsbService = mainActivity.getUsbService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hidcommands, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.container);
        for (Command command : mCommands) {
            Button btn = new Button(getActivity());
            btn.setId(command.mButtonId);
            btn.setText(command.mDesc);
            btn.setOnClickListener(this);
            viewGroup.addView(btn);
        }
    }

    @Override
    public void onClick(View view) {
        for (Command command : mCommands) {
            if (view.getId() == command.mButtonId) {
                command.mAction.run();
                return;
            }
        }
    }

    private ApRom getApRom(int which) {
        try {
            final InputStream inputStream = getResources().openRawResource(which);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int read;
            while ((read = inputStream.read()) != -1) {
                bos.write(read);
            }
            return new ApRom(bos.toByteArray());
        } catch (IOException e) {
            Log.e(TAG, "failed opening AP ROM", e);
            return null;
        }
    }

    public static class Command {
        String mDesc;
        int mButtonId;
        Runnable mAction;

        public Command(String desc, int mButtonId, Runnable mAction) {
            this.mDesc = desc;
            this.mButtonId = mButtonId;
            this.mAction = mAction;
        }
    }
}
