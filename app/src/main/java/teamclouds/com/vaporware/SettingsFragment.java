package teamclouds.com.vaporware;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;
import teamclouds.com.vaporware.communication.UsbService;
import teamclouds.com.vaporware.parsing.Settings;
import teamclouds.com.vaporware.parsing.VapeInput;

import static teamclouds.com.vaporware.parsing.Settings.getSettings;

/**
 *
 */
public class SettingsFragment extends Fragment {

    private NumberPicker pidP, pidI, pidD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        pidP = (NumberPicker) view.findViewById(R.id.pidP);
        pidI = (NumberPicker) view.findViewById(R.id.pidI);
        pidD = (NumberPicker) view.findViewById(R.id.pidD);

        Toast.makeText(getActivity(), "Getting settings", Toast.LENGTH_SHORT).show();
        MainActivity mainActivity = (MainActivity)getActivity();
        UsbService usbService = mainActivity.getUsbService();
        Settings.getSettingsFromVape(usbService);

        pidP.setMinValue(0);
        pidP.setMaxValue(300);

        pidI.setMinValue(0);
        pidI.setMaxValue(300);

        pidD.setMinValue(0);
        pidD.setMaxValue(300);

        pidP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                MainActivity mainActivity = (MainActivity)getActivity();
                UsbService usbService = mainActivity.getUsbService();
                Settings.sendToVape(usbService, Settings.P, i1);
            }
        });

        pidD.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                MainActivity mainActivity = (MainActivity)getActivity();
                UsbService usbService = mainActivity.getUsbService();
                Settings.sendToVape(usbService, Settings.D, i1);
            }
        });

        pidI.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                MainActivity mainActivity = (MainActivity)getActivity();
                UsbService usbService = mainActivity.getUsbService();
                Settings.sendToVape(usbService, Settings.I, i1);
            }
        });

        return view;
    }

    public void onChanged(VapeInput data) {
        pidP.setValue(getSettings().pidP);
        pidI.setValue(getSettings().pidI);
        pidD.setValue(getSettings().pidD);
    }
}
