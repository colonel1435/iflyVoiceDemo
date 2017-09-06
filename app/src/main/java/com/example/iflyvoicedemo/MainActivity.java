package com.example.iflyvoicedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iflyvoicedemo.bean.MsgVoiceEvent;
import com.example.iflyvoicedemo.utils.ColorUtils;
import com.example.iflyvoicedemo.utils.StringUtils;
import com.example.iflyvoicedemo.utils.XmlUtils;
import com.example.iflyvoicedemo.view.VoicePopupWindows;
import com.github.CardSlidePanel.CardAdapter;
import com.github.CardSlidePanel.CardDataItem;
import com.github.CardSlidePanel.CardSlidePanel;
import com.github.CardSlidePanel.CardViewHolder;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "wumin";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvOfflineMsg;
    @BindView(R.id.slide_panel)
    CardSlidePanel slidePanel;
    @BindView(R.id.iv_refresh)
    ImageView ivRefresh;
    @BindView(R.id.iv_speak)
    ImageView ivSpeak;
    private TextView mVoiceText = null;
    private VoiceSynthesizer mVSynthesizer;
    private VoiceRecognizer mVRecognizer;
    private VoiceWakeup mVWakeup;
    private Toast mToast;
    private Context mContext = null;
    private VoicePopupWindows mVoiceWindows;
    private CardSlidePanel.CardSwitchListener cardSwitchListener;
    private List<CardDataItem> mDatas;
    private CardAdapter mCardAdapter;
    private int curPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
        mContext = this;

        initData();
        initView();
    }

    private void initData() {
        mDatas = new ArrayList<>();
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("pre-shift-meeting.xml");
            mDatas.addAll(XmlUtils.parseCardData(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        toolbar.setTitle(getString(R.string.app_title));
        setSupportActionBar(toolbar);

        cardSwitchListener = new CardSlidePanel.CardSwitchListener() {

            @Override
            public void onShow(int index) {
                Log.d("Card", "正在显示-" + mDatas.get(index).getRefText());
                curPosition = index;
            }

            @Override
            public void onCardVanish(int index, int type) {
                Log.d("Card", "正在消失-" + mDatas.get(index).getRefText() + " 消失type=" + type);
//                CardDataItem item = mDatas.get(index);
//                mDatas.add(item);
//                mCardAdapter.notifyDataSetChanged();
            }
        };
        slidePanel.setCardSwitchListener(cardSwitchListener);
        mCardAdapter = new CardAdapter() {
            @Override
            public int getLayoutId() {
                return R.layout.view_card_item;
            }

            @Override
            public int getCount() {
                return mDatas.size();
            }

            @Override
            public void bindView(View view, int index) {
                Object tag = view.getTag();
                CardViewHolder viewHolder;
                if (null != tag) {
                    viewHolder = (CardViewHolder) tag;
                } else {
                    viewHolder = new CardViewHolder(view);
                    view.setTag(viewHolder);
                }

                CardDataItem item = mDatas.get(index);
                item.setColor(ColorUtils.getRandomColor(mContext));
                viewHolder.bindData(item);
            }
        };

        slidePanel.setAdapter(mCardAdapter);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.appid));

        mVSynthesizer = new VoiceSynthesizer(MainActivity.this);
        mVSynthesizer.startSpeechSynthesizer();

        mVRecognizer = new VoiceRecognizer(this);

        mVWakeup = new VoiceWakeup(this);
        mVWakeup.startWakeup();
    }

    public void startVoice(View view) {
        mVoiceWindows = new VoicePopupWindows(mContext);
        mVoiceWindows.setDismissListener(this, new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mVRecognizer.stopSpeechRecognizer();
                mVoiceWindows.dismiss(MainActivity.this);
            }
        });
        mVoiceWindows.show(this, view);
    }

    public void onDebug(View view) {
        mVRecognizer.showVoiceRecognizeDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoiceHandle(MsgVoiceEvent event) {
        int type = event.getType();
        String msg;
        switch (type) {
            case VoiceRecognizer.START_VOICE:
                startVoice(getWindow().getDecorView());
                break;
            case VoiceRecognizer.START_RECOGNIZE:
                mVoiceWindows.showProgress();
                break;
            case VoiceRecognizer.RECOGNIZE_ERROR:
                msg = getString(R.string.recognize_err) + getString(R.string.err_code) + event.getErrCode() + ")";
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.SPEAK_NULL:
                msg = getString(R.string.speak_null);
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.NET_ERROR:
                msg = getString(R.string.net_err);
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.RECOGNIZE_FINISH:
                mVoiceWindows.dismiss();
                if (!event.getMsg().equals("")) {
//                    mVSynthesizer.speakMsg(getString(R.string.synthesier_msg) + event.getMsg());
                    checkResult(event.getMsg());
                }
                break;
            case VoiceRecognizer.OFFLINE_START_VOICE:
                startVoice(getWindow().getDecorView());
                break;
            case VoiceRecognizer.OFFLINE_START_RECOGNIZE:
                mVoiceWindows.showProgress();
                break;
            case VoiceRecognizer.OFFLINE_RECOGNIZE_ERROR:
                msg = getString(R.string.recognize_err) + getString(R.string.err_code) + event.getErrCode() + ")";
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.OFFLINE_SPEAK_NULL:
                msg = getString(R.string.speak_null);
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.OFFLINE_NET_ERROR:
                msg = getString(R.string.net_err);
                mVoiceWindows.showMsg(msg, R.drawable.warning);
                break;
            case VoiceRecognizer.OFFLINE_RECOGNIZE_FINISH:
                mVoiceWindows.dismiss();
                if (!event.getMsg().equals("")) {
//                    mVSynthesizer.speakMsg(getString(R.string.synthesier_msg) + event.getMsg());
                    checkResult(event.getMsg());
                }
                break;
            default:
                mVoiceWindows.dismiss();
                break;
        }
    }

    private void checkResult(String result) {
        CardDataItem item = mDatas.get(curPosition);
        item.setRecogText(result);
        String refText = item.getRefText();
        int sum = refText.length();
        int error = StringUtils.levenshteinCompare(result, refText);
        float similarity = StringUtils.getSimilarityRatio(result, refText);
        item.setSum(sum);
        item.setSimilarity(similarity);
        item.setError(error);
        mCardAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "Stop wakeupListener");
        mVWakeup.stopWakeup();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.iv_refresh, R.id.iv_speak})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_refresh:
                mVRecognizer.showVoiceRecognizeDialog();
                break;
            case R.id.iv_speak:
                mVRecognizer.startSpeechRecognizer();
//                mVRecognizer.showVoiceRecognizeDialog();
//                mVSynthesizer.speakMsg("欢迎使用小七语音助手");
                break;
        }
    }

}