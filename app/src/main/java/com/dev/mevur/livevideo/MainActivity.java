package com.dev.mevur.livevideo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tx_video_view)
    TXCloudVideoView mView;

    @BindView(R.id.edt_url)
    EditText mUrl;

    @BindView(R.id.btn_start)
    Button mStart;

    @BindView(R.id.btn_stop)
    Button mStop;

    @BindView(R.id.process_bar)
    ProgressBar mProgressBar;

    private boolean isFullScreen = false;

    private TXLivePlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init for butter knife view injection
        ButterKnife.bind(this);
        init();

    }
    public void init() {
        //log print the rtmp sdk version info
        String sdkVer = TXLiveBase.getSDKVersionStr();
        Log.i(MainActivity.class.getName(), "lite av sdk version is " + sdkVer);
        //init tencent rtmp sdk log print
        TXLiveBase.setConsoleEnabled(true);
        //init tencent cloud player
        mPlayer = new TXLivePlayer(getApplicationContext());
        mPlayer.setPlayerView(mView);
        TXLivePlayConfig config = new TXLivePlayConfig();
        config.setEnableMessage(true);
        mPlayer.setConfig(config);
        mPlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int i, Bundle bundle) {
                if (i == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        });

        //init ui
        mStop.setText("pause");
    }
    @OnClick(R.id.btn_start)
    public void startPlay(View view) {
        String videoSourceUrl = mUrl.getText().toString().trim();
        if ("".equals(videoSourceUrl)) {
            Toast.makeText(getApplicationContext(), "视频地址为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mPlayer.startPlay(videoSourceUrl, TXLivePlayer.PLAY_TYPE_LIVE_RTMP_ACC);
    }

    @OnClick(R.id.btn_stop)
    public void stopPlay(View view) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.resume();
        }
    }

    @OnClick(R.id.tx_video_view)
    public void fullScreen(View view) {
        mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.stopPlay(true);
        mView.onDestroy();
    }
}
