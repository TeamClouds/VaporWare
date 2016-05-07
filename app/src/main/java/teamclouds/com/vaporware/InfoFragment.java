package teamclouds.com.vaporware;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import teamclouds.com.vaporware.communication.SendData;
import teamclouds.com.vaporware.communication.UsbService;
import teamclouds.com.vaporware.parsing.PidData;
import teamclouds.com.vaporware.parsing.Settings;
import teamclouds.com.vaporware.parsing.VapeInput;

import java.util.HashMap;

/**
 * Created by bird on 4/23/16.
 */
public class InfoFragment extends Fragment {

    private TextView selected, actual, resistance, watts;
    private LineChart mChart;
    HashMap<Long, PidData> mPidData = new HashMap<>();

    public void onChanged(VapeInput data) {
        PidData pid = (PidData)data;
        selected.setText(String.valueOf(pid.mSelectedTemp));
        actual.setText(String.valueOf(pid.mActualTemp));
        watts.setText(String.valueOf(pid.mWatts));
        resistance.setText(String.valueOf(pid.mResistance));
        long time = System.currentTimeMillis();
        mPidData.put(time, pid);
        addEntry(time, pid.mActualTemp, pid.mSelectedTemp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View datalayout = inflater.inflate(R.layout.content_main, container, false);

        selected = (TextView) datalayout.findViewById(R.id.selected);
        actual = (TextView) datalayout.findViewById(R.id.actual);
        resistance = (TextView) datalayout.findViewById(R.id.resitance);
        watts = (TextView) datalayout.findViewById(R.id.watts);


        Button pidbutton = (Button) datalayout.findViewById(R.id.enablePidDump);
        pidbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity)getActivity();
                UsbService usbService = mainActivity.getUsbService();
                SendData.enableDumpPid(usbService);
            }
        });

        Button hidbutton = (Button) datalayout.findViewById(R.id.writeHid);
        hidbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity)getActivity();
                UsbService usbService = mainActivity.getUsbService();
                usbService.writeHID(null);
            }
        });

        mChart = (LineChart) datalayout.findViewById(R.id.chart1);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        //setData(20,40);
        mChart.setData(new LineData());

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        return datalayout;
    }

    private LineDataSet createSet(int color) {

        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(2.5f);
        set.setDrawCircles(false);
        set.setColor(color);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawValues(false);

        return set;
    }

    private void addEntry(long time, int temp, int selectedTemp) {

        LineData data = mChart.getData();

        if(data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);

            if (set == null) {
                set = createSet(Color.rgb(240, 99, 99));
                data.addDataSet(set);
            }
            if (set2 == null) {
                set2 = createSet(Color.rgb(0,0,255));
                data.addDataSet(set2);
            }

            // add a new x-value first
            data.addXValue(String.valueOf(time));

            data.addEntry(new Entry(temp, set.getEntryCount()), 0);
            data.addEntry(new Entry(selectedTemp, set.getEntryCount()), 1);

            mChart.setVisibleXRangeMaximum(5000);
            // move to the latest entry
            mChart.moveViewToX(data.getXValCount() - 5001);

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            mChart.moveViewTo(data.getXValCount()-7, selectedTemp, YAxis.AxisDependency.LEFT);
        }
    }
}
