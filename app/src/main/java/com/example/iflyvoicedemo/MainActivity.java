package com.example.iflyvoicedemo;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ResourceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "wumin";
    private TextView mVoiceText = null;
    private RecognizerDialog recognizerDialog;
    private SpeechRecognizer mIat;
    private SpeechSynthesizer mTts;
    private Toast mToast;
    private static final String APPID = "=583ba206";
    private static final String WAKEUP_RES = "ivw/583ba206.jet";
    private Context mContext;
    private VoiceWakeuper mIvw;
    // 云端语法文件
    private String mCloudGrammar = null;
    // 云端语法id
    private String mCloudGrammarID;
    // 本地语法id
    private String mLocalGrammarID;
    // 本地语法文件
    private String mLocalGrammar = null;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mVoiceText = (TextView) findViewById(R.id.tv_voice_info);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + APPID);
        mIvw = VoiceWakeuper.createWakeuper(mContext, null);
        mCloudGrammar = readFile(this, "jinglutong_grammar.abnf", "utf-8");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "start voice");
  //              showVoiceRecognizeDialog();
               // startSpeechSynthesizer();
               // startSpeechRecognizer();
                initWakeup();

            }
        });

        //initWakeup();
    }

    GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
                    mCloudGrammarID = grammarId;
                } else {
                    mLocalGrammarID = grammarId;
                }
                showTip("语法构建成功：" + grammarId);
            } else {
                showTip("语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };
    private WakeuperListener mWakeuperListener = new WakeuperListener() {
        public void onResult(WakeuperResult result) {
            String text = result.getResultString();
            JSONObject object = null;
            try {
                object = new JSONObject(text);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append("【RAW】 " + text);
            buffer.append("\n");
            buffer.append("【操作类型】" + object.optString("sst"));
            buffer.append("\n");
            buffer.append("【唤醒词id】" + object.optString("id"));
            buffer.append("\n");
            buffer.append("【得分】" + object.optString("score"));
            buffer.append("\n");
            buffer.append("【前端点】" + object.optString("bos"));
            buffer.append("\n");
            buffer.append("【尾端点】" + object.optString("eos"));
            String resultString = buffer.toString();
            Log.i(TAG, "WakeuperListener result : "+resultString);
            //stopWake();
            //kqwWake();
        }
        public void onError(SpeechError error) {
            Log.i(TAG, "WakeuperListener error");
        }
        public void onBeginOfSpeech() {
            Log.i(TAG, "WakeuperListener error");
        }
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
        //当使用唤醒+识别功能时获取识别结果
        //arg1:是否最后一个结果，1:是，0:否。
                RecognizerResult reslut = ((RecognizerResult)obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                Log.i(TAG, "Recognize result : "+reslut.getResultString());
                String voiceInfo = JsonParser.parseIatResult(reslut.getResultString());
                if (voiceInfo.compareTo("。") == 0)
                    return;
                mVoiceText.setText(voiceInfo);
                speakMsg("您好，你说的是:" + voiceInfo);
            }
        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };
    private String getWakeupResource() {
        return ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, WAKEUP_RES);
    }
    public void initWakeup() {
        //1.加载唤醒词资源，resPath为唤醒资源路径
        int curThresh = 5;
        StringBuffer param =new StringBuffer();
        String resPath = ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, WAKEUP_RES);
        param.append(ResourceUtil.IVW_RES_PATH + "=" + resPath);
        param.append(","+ResourceUtil.ENGINE_START + "=" + SpeechConstant.ENG_IVW);
        SpeechUtility.getUtility().setParameter(ResourceUtil.ENGINE_START,param.toString());

        //mCloudGrammar = readFile(this, "sample.abnf", "utf-8");
        //3.设置唤醒参数
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD,"0:"+curThresh);
            //设置当前业务类型为唤醒
            mIvw.setParameter(SpeechConstant.IVW_SST,"wakeup");
            //设置唤醒一直保持，直到调用stopListening，传入0则完成一次唤醒后，会话立即结束（默认0）
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE,"1");
     //           mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getWakeupResource());
            //4.开始唤醒
            Log.i(TAG,"Start wakeupListener");
            mIvw.startListening(mWakeuperListener);
        } else {
            Toast.makeText(mContext, "唤醒未初始化", Toast.LENGTH_SHORT).show();
        }
        //听写监听器
    }
    private RecognizerDialogListener mRecognizeListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.i(TAG, "Recognize result : "+recognizerResult.getResultString());
            String voiceInfo = JsonParser.parseIatResult(recognizerResult.getResultString());
            if (voiceInfo.compareTo("last") == 0)
                return;
            mVoiceText.setText(voiceInfo);
            speakMsg("您好，你说的是:" + voiceInfo);
        }

        @Override
        public void onError(SpeechError speechError) {
            int errCode = speechError.getErrorCode();
            String msg;
            switch (errCode) {
                case 10118:
                    msg = "您未说话，请重试！";
                    break;
                default:
                    msg = "识别错误，请重试！";
                    break;
            }
            speakMsg(msg);
        }
    };
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            Log.d(TAG, "SpeechRecognizer init() code = " + i);
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(MainActivity.this, "初始化失败，错误码：" + i, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void showVoiceRecognizeDialog() {
        recognizerDialog = new RecognizerDialog(this, mInitListener);
        recognizerDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        recognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        recognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        recognizerDialog.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        recognizerDialog.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        //mAsr.buildGrammar("abnf", mCloudGrammar, grammarListener);
        recognizerDialog.setListener(mRecognizeListener);
        recognizerDialog.show();
    }

    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener(){
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.i(TAG, "Recognize result : "+ results.getResultString());
            String voiceInfo = JsonParser.parseIatResult(results.getResultString());
            if (voiceInfo.compareTo("last") == 0)
                return;
            mVoiceText.setText(voiceInfo);
            speakMsg("您好，你说的是:" + voiceInfo);
        }
        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true); //获取错误码描述
//            speakMsg("识别错误，您未说话或者");
            int errCode = error.getErrorCode();
            String msg;
            switch (errCode) {
                case 10118:
                    msg = "您未说话，请重试！";
                    break;
                default:
                    msg = "识别错误，请重试！";
                    break;
            }
            speakMsg(msg);
            Toast.makeText(MainActivity.this, "Error code : " + error.getErrorCode(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        //开始录音
        public void onBeginOfSpeech() {
            Log.i(TAG, "onBeginOfSpeech...");
        }
        //音量值0~30
        public void onVolumeChanged(int volume){}
        //结束录音
        public void onEndOfSpeech() {
            Log.i(TAG, "onEndOfSpeech...");
        }
        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {}
    };
    public void startSpeechRecognizer() {
        Log.i(TAG, "startSpeechRecognizer...");
        //1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat= SpeechRecognizer.createRecognizer(MainActivity.this, null);
        //2.设置听写参数
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        mIat.buildGrammar("abnf", mCloudGrammar, grammarListener);
        //3.开始听写
        mIat.startListening(mRecoListener);
    }
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            Log.i(TAG, "onSpeakBegin...");
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            Log.i(TAG, "onCompleted...");
//            startSpeechRecognizer();
           // showVoiceRecognizeDialog();
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
    public void startSpeechSynthesizer() {
        mTts= SpeechSynthesizer.createSynthesizer(MainActivity.this, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "vivi");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
       // speakMsg("您好，我是径路通助手sandy,请说打开平面图或者返回平面图");
        //合成监听器
    }

    public void speakMsg(String msg) {
        mTts.startSpeaking(msg, mSynListener);
    }

    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG,"Stop wakeupListener");
        mIvw.stopListening();
        super.onDestroy();
    }
    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}