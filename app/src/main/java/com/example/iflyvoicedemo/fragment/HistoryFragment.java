package com.example.iflyvoicedemo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.iflyvoicedemo.R;
import com.example.iflyvoicedemo.adapter.RecyclerItemClickSupport;
import com.example.iflyvoicedemo.adapter.RecyclerViewCommonAdapter;
import com.example.iflyvoicedemo.adapter.RecyclerViewHolder;
import com.example.iflyvoicedemo.bean.VoiceResult;
import com.example.iflyvoicedemo.utils.StringUtils;
import com.example.iflyvoicedemo.utils.TipUtils;
import com.example.iflyvoicedemo.view.HistoryPopupWindows;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PAGE_TYPE = "type";
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;
    Unbinder unbinder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mType;
    private RecyclerViewCommonAdapter<VoiceResult> mAdapter;
    private List<VoiceResult> mDatas;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        }

        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);

        mAdapter = new RecyclerViewCommonAdapter<VoiceResult>(getActivity(), R.layout.rv_item_history,
                                                            mDatas) {
            @Override
            public void convert(RecyclerViewHolder holder, VoiceResult voiceResult) {
                holder.setText(R.id.tv_item_text, voiceResult.getText());
                holder.setText(R.id.tv_item_err, String.valueOf(voiceResult.getErrs()));
                holder.setText(R.id.tv_item_accuracy, String.valueOf(voiceResult.getAccuracy()));
            }

            @Override
            public void onBindViewHolder(RecyclerViewHolder holder, int position) {

            }
        };

        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                false));
        rvHistory.setHasFixedSize(true);
        rvHistory.setAdapter(mAdapter);
        RecyclerItemClickSupport.addTo(rvHistory).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final HistoryPopupWindows popupwindows = new HistoryPopupWindows(getActivity());
                popupwindows.setResult(mDatas.get(position));
                popupwindows.show(getActivity(), v);
            }
        });

        RecyclerItemClickSupport.addTo(rvHistory).setOnItemLongClickListener(new RecyclerItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
                final SweetAlertDialog dlg = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(getString(R.string.notification_title))
                        .setContentText(getString(R.string.delete_history_item))
                        .setConfirmText(getString(R.string.delete_history_item_ok))
                        .setCancelText(getString(R.string.notification_cancle));

                dlg.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Realm realm = null;
                        try {
                            realm = Realm.getDefaultInstance();
                            VoiceResult result = realm.where(VoiceResult.class)
                                                        .equalTo("uid", mDatas.get(position).getUid())
                                                        .findFirst();
                            if (result != null) {
                                realm.beginTransaction();
                                result.deleteFromRealm();
                                realm.commitTransaction();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            realm.close();
                        }
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dlg.dismissWithAnimation();
                    }
                }).show();
                return true;
            }
        });
        return view;
    }

    private void initData() {
        mDatas = new ArrayList<>();
        Observable.create(new ObservableOnSubscribe<VoiceResult>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<VoiceResult> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<VoiceResult> results = realm.where(VoiceResult.class)
                        .equalTo("type", mType)
                        .findAll();
                for (VoiceResult result : results) {
                    e.onNext(realm.copyFromRealm(result));
                }

                realm.close();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VoiceResult>() {
                    int i = 0;

                    @Override
                    public void accept(VoiceResult voiceResult) throws Exception {
                        mDatas.add(i, voiceResult);
                        mAdapter.notifyItemInserted(i++);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
