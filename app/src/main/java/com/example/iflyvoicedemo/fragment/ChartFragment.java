package com.example.iflyvoicedemo.fragment;


import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
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
import com.github.mikephil.charting.components.Description;
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
import com.github.mikephil.charting.formatter.PercentFormatter;
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
        Description desc = lineChartErr.getDescription();
        desc.setText("准确率统计");
        desc.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
        desc.setYOffset(-12);
        lineChartErr.setNoDataText(getString(R.string.speak_result_null));
        lineChartErr.setTouchEnabled(true);
        lineChartErr.setDragEnabled(true);
        lineChartErr.setScaleEnabled(true);
        lineChartErr.setDrawGridBackground(true);
        lineChartErr.setPinchZoom(true);

        XAxis xAxis = lineChartErr.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(1);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setSpaceMin(1f);
        xAxis.setSpaceMax(1f);
        xAxis.setDrawLabels(false);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        LimitLine bestLine = new LimitLine(90f, "优");
        bestLine.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
        bestLine.setLineWidth(2f);
        bestLine.enableDashedLine(10f, 10f, 0f);
        bestLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        bestLine.setTextSize(10f);
        bestLine.setTypeface(tf);

        LimitLine goodLine = new LimitLine(50f, "平均值");
        goodLine.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
        goodLine.setLineWidth(2f);
        goodLine.enableDashedLine(10f, 10f, 0f);
        goodLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        goodLine.setTextSize(10f);
        goodLine.setTypeface(tf);

        LimitLine badLine = new LimitLine(10f, "差");
        badLine.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
        badLine.setLineWidth(2f);
        badLine.enableDashedLine(10f, 10f, 0f);
        badLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        badLine.setTextSize(10f);
        badLine.setTypeface(tf);

        YAxis leftAxis = lineChartErr.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(bestLine);
        leftAxis.addLimitLine(goodLine);
        leftAxis.addLimitLine(badLine);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0.0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);
        leftAxis.setValueFormatter(new PercentFormatter());
        lineChartErr.getAxisRight().setEnabled(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);
        lineChartErr.getAxisRight().setEnabled(false);
        lineChartErr.animateX(2500);

        Legend offlineLegend = lineChartErr.getLegend();
        offlineLegend.setFormSize(5);
        offlineLegend.setForm(Legend.LegendForm.LINE);
        initLineData();
    }

    private void initBarChart() {
        Description desc = chartErr.getDescription();
        desc.setText("错误数量统计");
        desc.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
        desc.setYOffset(-12);
        chartErr.setNoDataText(getString(R.string.speak_result_null));
        chartErr.setNoDataTextColor(Color.BLACK);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chartErr.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chartErr.setPinchZoom(true);

        chartErr.setDrawBarShadow(false);
        chartErr.setDrawGridBackground(true);
        chartErr.setDrawValueAboveBar(true);

        XAxis xAxis = chartErr.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceMin(1.0f);
        xAxis.setSpaceMax(1.0f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLabels(false);

        YAxis yAxis = chartErr.getAxisLeft();
        yAxis.setDrawGridLines(true);
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setAxisMinimum(0f);
        yAxis.setSpaceMin(1f);
        yAxis.setSpaceMax(10f);
        chartErr.getAxisRight().setEnabled(false);
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
            offlineSet.setLineWidth(2f);
            offlineSet.setCircleRadius(3f);
            offlineSet.setDrawCircleHole(true);
            offlineSet.setValueTextSize(12f);
            offlineSet.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.colorforestGreen));
            offlineSet.setDrawFilled(true);
            offlineSet.setFormLineWidth(1f);
            offlineSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            offlineSet.setFormSize(15.f);
            offlineSet.setFillColor(ContextCompat.getColor(getActivity(), R.color.colorMediumAquamaine));

            offlineSet.setValueFormatter(new PercentFormatter());
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
            barSet = new BarDataSet(yVals, "准确率");
            barSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barSet.setDrawValues(true);
            barSet.setFormLineWidth(4);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(barSet);

            BarData data = new BarData(dataSets);
            data.setBarWidth(0.5f);
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
                for (int i = 1; i <= results.size(); i++) {
                    VoiceResult result = results.get(i);
                    if (result != null) {
                        yVals.add(new BarEntry(i, result.getErrs()));
                        yOfflineVals.add(new Entry(i, result.getAccuracy()*100));
                        Log.d(TAG, result.toString());
                    }
                }
            }

            if (yVals.size() == 0) {
                yVals.add(new BarEntry(0, 0));
            }
            if (yOfflineVals.size() == 0) {
                yOfflineVals.add(new Entry(0, 0));
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
