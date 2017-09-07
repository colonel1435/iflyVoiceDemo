package com.example.iflyvoicedemo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.iflyvoicedemo.R;
import com.example.iflyvoicedemo.bean.VoiceResult;
import com.example.iflyvoicedemo.bean.chart.ChartConst;
import com.example.iflyvoicedemo.utils.StringUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BarChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarChartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.chart_err)
    BarChart chartErr;
    Unbinder unbinder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final String TAG = this.getClass().getSimpleName() + "@wumin";
    private int charType;
    private ArrayList<BarEntry> yVals;

    public BarChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BarChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BarChartFragment newInstance(String param1, String param2) {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            charType = getArguments().getInt(ChartConst.TYPE_CHART_KEY);
            initData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        unbinder = ButterKnife.bind(this, view);

        chartErr.getDescription().setEnabled(false);

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

        chartErr .getLegend().setEnabled(false);

        BarDataSet barSet;
        if (chartErr.getData() != null &&
                chartErr.getData().getDataSetCount() > 0) {
            barSet = (BarDataSet)chartErr.getData().getDataSetByIndex(0);
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

        return view;
    }

    private void initData() {
        yVals = new ArrayList<>();
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<VoiceResult> results = realm.where(VoiceResult.class).findAll();
            for (int i = 0; i < results.size(); i++) {
                VoiceResult result = results.get(i);
                yVals.add(new BarEntry(i, result.getErrs()));
                Log.d(TAG, result.toString());
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
