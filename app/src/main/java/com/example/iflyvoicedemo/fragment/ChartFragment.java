package com.example.iflyvoicedemo.fragment;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iflyvoicedemo.R;
import com.example.iflyvoicedemo.bean.VoiceResult;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PAGE_TYPE= "type";
    @BindView(R.id.chart_err)
    BarChart chartErr;
    Unbinder unbinder;
    @BindView(R.id.line_chart_err)
    LineChart lineChartErr;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mType;

    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    private ArrayList<BarEntry> yVals;
    private ArrayList<Entry> yOfflineVals;

    public ChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PAGE_TYPE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mType = getArguments().getString(ARG_PAGE_TYPE);
            initData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        unbinder = ButterKnife.bind(this, view);

        initLineChart();
        initBarChart();


        return view;
    }

    private void initLineChart() {
        lineChartErr.setDrawGridBackground(true);
        lineChartErr.setDescription(null);
        lineChartErr.setNoDataText(getString(R.string.speak_result_null));
        lineChartErr.setTouchEnabled(true);
        lineChartErr.setDragEnabled(true);
        lineChartErr.setScaleEnabled(true);

        lineChartErr.setPinchZoom(true);
        lineChartErr.setBackgroundColor(Color.WHITE);

//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(accuracyChart); // For bounds control
//        accuracyChart.setMarker(mv); // Set the marker to the chart
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 1f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = lineChartErr.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(0);
        xAxis.setAvoidFirstLastClipping(true);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        LimitLine bestLine = new LimitLine(0.9f, "优");
        bestLine.setLineWidth(3f);
        bestLine.enableDashedLine(10f, 10f, 0f);
        bestLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        bestLine.setTextSize(10f);
        bestLine.setTypeface(tf);

        LimitLine goodLine = new LimitLine(0.5f, "平均值");
        goodLine.setLineWidth(3f);
        goodLine.enableDashedLine(10f, 10f, 0f);
        goodLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        goodLine.setTextSize(10f);
        goodLine.setTypeface(tf);

        LimitLine badLine = new LimitLine(0.1f, "差");
        badLine.setLineWidth(3f);
        badLine.enableDashedLine(10f, 10f, 0f);
        badLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        badLine.setTextSize(10f);
        badLine.setTypeface(tf);

        YAxis leftAxis = lineChartErr.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(bestLine);
        leftAxis.addLimitLine(goodLine);
        leftAxis.addLimitLine(badLine);
        leftAxis.setAxisMaximum(1.0f);
        leftAxis.setAxisMinimum(0.0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        lineChartErr.getAxisRight().setEnabled(false);
        lineChartErr.animateX(2500);

        Legend offlineLegend = lineChartErr.getLegend();

        offlineLegend.setForm(Legend.LegendForm.LINE);
        initLineData();
    }

    private void initBarChart() {
        chartErr.setDescription(null);
        chartErr.setNoDataText(getString(R.string.speak_result_null));
        chartErr.setNoDataTextColor(Color.BLACK);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chartErr.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chartErr.setPinchZoom(false);

        chartErr.setDrawBarShadow(false);
        chartErr.setDrawGridBackground(false);

        XAxis xAxis = chartErr.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        chartErr.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        chartErr.animateY(2500);

        chartErr.getLegend().setEnabled(false);
        initBarData();
    }

    private void initLineData() {
        LineDataSet offlineSet;
        if (lineChartErr.getData() != null &&
                lineChartErr.getData().getDataSetCount() > 0) {
            offlineSet = (LineDataSet)lineChartErr.getData().getDataSetByIndex(0);
            offlineSet.setValues(yOfflineVals);

            lineChartErr.getData().notifyDataChanged();
            lineChartErr.notifyDataSetChanged();
        } else {
            /***    Offline     ***/
            offlineSet = new LineDataSet(yOfflineVals, "准确率");
            offlineSet.setDrawIcons(true);
            offlineSet.setColor(ContextCompat.getColor(getActivity(), R.color.colorMediumAquamaine));
            offlineSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.colorMediumAquamaine));
            offlineSet.setLineWidth(1f);
            offlineSet.setCircleRadius(3f);
            offlineSet.setDrawCircleHole(true);
            offlineSet.setValueTextSize(9f);
            offlineSet.setDrawFilled(true);
            offlineSet.setFormLineWidth(1f);
            offlineSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            offlineSet.setFormSize(15.f);
            offlineSet.setFillDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.shape_fade_blue));

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(offlineSet);

            LineData data = new LineData(dataSets);
            lineChartErr.setData(data);
        }

    }

    private void initBarData() {
        BarDataSet barSet;
        if (chartErr.getData() != null &&
                chartErr.getData().getDataSetCount() > 0) {
            barSet = (BarDataSet) chartErr.getData().getDataSetByIndex(0);
            barSet.setValues(yVals);
            chartErr.getData().notifyDataChanged();
            chartErr.notifyDataSetChanged();
        } else {
            barSet = new BarDataSet(yVals, "Data Set");
            barSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barSet.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(barSet);

            BarData data = new BarData(dataSets);
            chartErr.setData(data);
            chartErr.setFitBars(true);
        }

        chartErr.invalidate();
    }

    private void initData() {
        yVals = new ArrayList<>();
        yOfflineVals = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<VoiceResult> results = realm.where(VoiceResult.class)
                                                            .equalTo("type", mType).findAll();
            if (results != null) {
                for (int i = 0; i < results.size(); i++) {
                    VoiceResult result = results.get(i);
                    if (result != null) {
                        yVals.add(new BarEntry(i, result.getErrs()));
                        yOfflineVals.add(new Entry(i, result.getAccuracy()));
                        Log.d(TAG, result.toString());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
