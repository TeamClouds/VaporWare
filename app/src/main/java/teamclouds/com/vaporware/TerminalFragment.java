package teamclouds.com.vaporware;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import teamclouds.com.vaporware.communication.SendData;
import teamclouds.com.vaporware.communication.UsbService;

/**
 * Created by bird on 4/23/16.
 */
public class TerminalFragment extends Fragment {

    private EditText commands;
    private TextView mTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View datalayout = inflater.inflate(R.layout.terminal_fragment, container, false);

        ScrollView sv = (ScrollView) datalayout.findViewById(R.id.scrollText);
        sv.fullScroll(View.FOCUS_DOWN);
        mTextView = (TextView) sv.findViewById(R.id.terminal);
        commands = (EditText) datalayout.findViewById(R.id.commandLine);
        commands.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    MainActivity mainActivity = (MainActivity)getActivity();
                    UsbService usbService = mainActivity.getUsbService();
                    SendData.sendData(usbService, v.getText().toString());
                    commands.setText("", null);
                    return true;
                }
                return false;
            }
        });
        return datalayout;
    }

    private void addMessage(String msg) {
        // append the new string
        String text = msg + mTextView.getText().toString();
        mTextView.setText(text);

    }

    public void onChanged(String rawData) {
        addMessage(rawData);
    }

}
