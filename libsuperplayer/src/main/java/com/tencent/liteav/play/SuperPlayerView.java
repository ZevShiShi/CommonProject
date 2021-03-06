package com.tencent.liteav.play;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.liteav.play.bean.TCVideoQuality;
import com.tencent.liteav.play.controller.IControllerCallback;
import com.tencent.liteav.play.controller.TCControllerFloat;
import com.tencent.liteav.play.controller.TCControllerFullScreen;
import com.tencent.liteav.play.controller.TCControllerWindow;
import com.tencent.liteav.play.net.TCLogReport;
import com.tencent.liteav.play.protocol.IPlayInfoProtocol;
import com.tencent.liteav.play.protocol.IPlayInfoRequestCallback;
import com.tencent.liteav.play.protocol.TCPlayInfoParams;
import com.tencent.liteav.play.protocol.TCPlayInfoProtocolV2;
import com.tencent.liteav.play.utils.TCImageUtil;
import com.tencent.liteav.play.utils.TCNetWatcher;
import com.tencent.liteav.play.utils.TCUrlUtil;
import com.tencent.liteav.play.utils.TCVideoQualityUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXBitrateItem;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liyuejiao on 2018/7/3.
 * <p>
 * ???????????????view
 * <p>
 * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
 * <p>
 * ??????????????????????????????????????????????????????????????????????????????????????????{@link #playWithModel(SuperPlayerModel)}??????{@link SuperPlayerModel}????????????????????????
 * <p>
 * 1???????????????{@link #playWithModel(SuperPlayerModel)}
 * <p>
 * 2???????????????{@link #setPlayerViewCallback(OnSuperPlayerViewCallback)}
 * <p>
 * 3????????????????????????????????????{@link #initVodPlayer(Context)}?????????????????????{@link #onPlayEvent(TXVodPlayer, int, Bundle)}???
 * ??????????????????{@link #onNetStatus(TXVodPlayer, Bundle)}
 * <p>
 * 4????????????????????????????????????{@link #initLivePlayer(Context)}?????????????????????{@link #onPlayEvent(int, Bundle)}???
 * ??????????????????{@link #onNetStatus(Bundle)}
 * <p>
 * 5???controller????????????{@link #mControllerCallback}
 * <p>
 * 5???????????????????????????{@link #resetPlayer()}
 */

public class SuperPlayerView extends RelativeLayout implements ITXVodPlayListener, ITXLivePlayListener {
    private static final String TAG = "SuperPlayerView";

    private enum PLAYER_TYPE {
        PLAYER_TYPE_NULL,
        PLAYER_TYPE_VOD,
        PLAYER_TYPE_LIVE
    }

    private Context mContext;
    // UI
    private ViewGroup mRootView;                      // SuperPlayerView??????view
    private TXCloudVideoView mTXCloudVideoView;              // ?????????????????????view
    private TCControllerFullScreen mControllerFullScreen;          // ??????????????????view
    private TCControllerWindow mControllerWindow;              // ??????????????????view
    private TCControllerFloat mControllerFloat;               // ?????????????????????view
    //    private TCDanmuView mDanmuView;                     // ??????
    private ViewGroup.LayoutParams mLayoutParamWindowMode;         // ???????????????SuperPlayerView???????????????
    private ViewGroup.LayoutParams mLayoutParamFullScreenMode;     // ???????????????SuperPlayerView???????????????
    private LayoutParams mVodControllerWindowParams;     // ??????controller???????????????
    private LayoutParams mVodControllerFullScreenParams; // ??????controller???????????????

    private WindowManager mWindowManager;                 // ????????????????????????
    private WindowManager.LayoutParams mWindowParams;                  // ?????????????????????

    private SuperPlayerModel mCurrentModel;                  // ???????????????model
    private IPlayInfoProtocol mCurrentProtocol;               // ???????????????????????????

    private TXVodPlayer mVodPlayer;                     // ???????????????
    private TXVodPlayConfig mVodPlayConfig;                 // ?????????????????????
    private TXLivePlayer mLivePlayer;                    // ???????????????
    private TXLivePlayConfig mLivePlayConfig;                // ?????????????????????

    private OnSuperPlayerViewCallback mPlayerViewCallback;            // SuperPlayerView??????
    private TCNetWatcher mWatcher;                       // ?????????????????????
    private String mCurrentPlayVideoURL;           // ???????????????url
    private int mCurrentPlayType;               // ??????????????????
    private int mCurrentPlayMode = SuperPlayerConst.PLAYMODE_WINDOW;    // ??????????????????
    private int mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING; // ??????????????????
    private boolean mIsMultiBitrateStream;          // ??????????????????url??????
    private boolean mIsPlayWithFileId;              // ??????????????????fileId??????
    private long mReportLiveStartTime = -1;      // ?????????????????????????????????????????????
    private long mReportVodStartTime = -1;       // ?????????????????????????????????????????????
    private boolean mDefaultQualitySet;             // ?????????????????????url??????????????????????????????
    private boolean mLockScreen = false;            // ??????????????????
    private boolean mChangeHWAcceleration;          // ?????????????????????????????????????????????????????????
    private int mSeekPos;                       // ????????????????????????????????????
    private long mMaxLiveProgressTime;           // ???????????????????????????
    private PLAYER_TYPE mCurPlayType = PLAYER_TYPE.PLAYER_TYPE_NULL;    //??????????????????
    private boolean showFullButton = true;

    private final int OP_SYSTEM_ALERT_WINDOW = 24;    // ??????TYPE_TOAST??????????????????API??????

    public SuperPlayerView(Context context) {
        super(context);
        initView(context);
    }

    public SuperPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SuperPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * ?????????view
     *
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        mRootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.super_vod_player_view, null);
        mTXCloudVideoView = (TXCloudVideoView) mRootView.findViewById(R.id.cloud_video_view);
        mControllerFullScreen = (TCControllerFullScreen) mRootView.findViewById(R.id.controller_large);
        mControllerWindow = (TCControllerWindow) mRootView.findViewById(R.id.controller_small);
        mControllerFloat = (TCControllerFloat) mRootView.findViewById(R.id.controller_float);
//        mDanmuView = (TCDanmuView) mRootView.findViewById(R.id.danmaku_view);

        mVodControllerWindowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mVodControllerFullScreenParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mControllerFullScreen.setCallback(mControllerCallback);
        mControllerWindow.setCallback(mControllerCallback);
        mControllerFloat.setCallback(mControllerCallback);


        removeAllViews();
//        mRootView.removeView(mDanmuView);
        mRootView.removeView(mTXCloudVideoView);
        mRootView.removeView(mControllerWindow);
        mRootView.removeView(mControllerFullScreen);
        mRootView.removeView(mControllerFloat);

        addView(mTXCloudVideoView);
        if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
            addView(mControllerFullScreen);
            mControllerFullScreen.hide();
        } else if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
            addView(mControllerWindow);
            mControllerWindow.hide();
        }
//        addView(mDanmuView);

        post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
                    mLayoutParamWindowMode = getLayoutParams();
                }
                try {
                    // ????????????Parent???LayoutParam??????????????????????????????fullscreen????????????LayoutParam
                    Class parentLayoutParamClazz = getLayoutParams().getClass();
                    Constructor constructor = parentLayoutParamClazz.getDeclaredConstructor(int.class, int.class);
                    mLayoutParamFullScreenMode = (ViewGroup.LayoutParams) constructor.newInstance(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TCLogReport.getInstance().setAppName(context);
        TCLogReport.getInstance().setPackageName(context);
    }

    /**
     * ????????????????????????
     *
     * @param context
     */
    private void initVodPlayer(Context context) {
        if (mVodPlayer != null)
            return;
        mVodPlayer = new TXVodPlayer(context);
        SuperPlayerGlobalConfig config = SuperPlayerGlobalConfig.getInstance();
//        // ?????????????????????
//        config.enableFloatWindow = true;
//        //???????????????????????????????????????
//        SuperPlayerGlobalConfig.TXRect rect = new SuperPlayerGlobalConfig.TXRect();
//        rect.x = 0;
//        rect.y = 0;
//        rect.width = 810;
//        rect.height = 540;
//        config.floatViewRect = rect;
        mVodPlayConfig = new TXVodPlayConfig();
//        mVodPlayConfig.setCacheFolderPath(context.getCacheDir().getAbsolutePath() + "/txcache");
        mVodPlayConfig.setMaxCacheItems(config.maxCacheItem);
        mVodPlayer.setConfig(mVodPlayConfig);
        mVodPlayer.setRenderMode(config.renderVideoMode);
        mVodPlayer.setVodListener(this);
        mVodPlayer.enableHardwareDecode(config.enableHWAcceleration);
    }

    /**
     * ????????????????????????
     *
     * @param context
     */
    private void initLivePlayer(Context context) {
        if (mLivePlayer != null)
            return;
        mLivePlayer = new TXLivePlayer(context);
        SuperPlayerGlobalConfig config = SuperPlayerGlobalConfig.getInstance();
        mLivePlayConfig = new TXLivePlayConfig();
        mLivePlayer.setConfig(mLivePlayConfig);

//        if (context instanceof Activity) {
//            if (((Activity) context).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                mLivePlayer.setRenderMode(SuperPlayerGlobalConfig.getInstance().renderVideoMode);
//            } else {
//                mLivePlayer.setRenderMode(SuperPlayerGlobalConfig.getInstance().renderLiveMode);
//            }
//        } else {
//        }
        mLivePlayer.setRenderMode(config.renderLiveMode);
        mLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mLivePlayer.setPlayListener(this);
        mLivePlayer.enableHardwareDecode(config.enableHWAcceleration);
//        mLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
    }

    public void setRenderMode(int orientation) {
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mLivePlayer.setRenderMode(SuperPlayerGlobalConfig.getInstance().renderVideoMode);
        } else {
            mLivePlayer.setRenderMode(SuperPlayerGlobalConfig.getInstance().renderLiveMode);
        }
    }


    /**
     * ????????????
     *
     * @param model
     */
    public void playWithModel(final SuperPlayerModel model) {
        mCurrentModel = model;
        stopPlay();
        initLivePlayer(getContext());
        initVodPlayer(getContext());
        // ????????????????????????????????????
        mControllerFullScreen.updateImageSpriteInfo(null);
        mControllerFullScreen.updateKeyFrameDescInfo(null);
        TCPlayInfoParams params = new TCPlayInfoParams();
        params.appId = model.appId;
        if (model.videoId != null) {
            params.fileId = model.videoId.fileId;
            params.timeout = model.videoId.timeout;
            params.us = model.videoId.us;
            params.exper = model.videoId.exper;
            params.sign = model.videoId.sign;
        }
        mCurrentProtocol = new TCPlayInfoProtocolV2(params);
        if (model.videoId != null) { // ??????FileId??????
            mCurrentProtocol.sendRequest(new IPlayInfoRequestCallback() {
                @Override
                public void onSuccess(IPlayInfoProtocol protocol, TCPlayInfoParams param) {
                    TXCLog.i(TAG, "onSuccess: protocol params = " + param.toString());
                    mReportVodStartTime = System.currentTimeMillis();
                    mVodPlayer.setPlayerView(mTXCloudVideoView);
                    playModeVideo(mCurrentProtocol);
                    updatePlayType(SuperPlayerConst.PLAYTYPE_VOD);
                    String title = !TextUtils.isEmpty(model.title) ? model.title :
                            (mCurrentProtocol.getName() != null && !TextUtils.isEmpty(mCurrentProtocol.getName())) ? mCurrentProtocol.getName() : "";
                    updateTitle(title);
                    updateVideoProgress(0, 0);
                    mControllerFullScreen.updateImageSpriteInfo(mCurrentProtocol.getImageSpriteInfo());
                    mControllerFullScreen.updateKeyFrameDescInfo(mCurrentProtocol.getKeyFrameDescInfo());
                }

                @Override
                public void onError(int errCode, String message) {
                    TXCLog.i(TAG, "onFail: errorCode = " + errCode + " message = " + message);
                    Toast.makeText(SuperPlayerView.this.getContext(), "???????????????????????? code = " + errCode + " msg = " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else { // ??????URL??????
            String videoURL = null;
            List<TCVideoQuality> videoQualities = new ArrayList<>();
            TCVideoQuality defaultVideoQuality = null;
            if (model.multiURLs != null && !model.multiURLs.isEmpty()) {// ?????????URL??????
                int i = 0;
                for (SuperPlayerModel.SuperPlayerURL superPlayerURL : model.multiURLs) {
                    if (i == model.playDefaultIndex) {
                        videoURL = superPlayerURL.url;
                    }
                    videoQualities.add(new TCVideoQuality(i++, superPlayerURL.qualityName, superPlayerURL.url));
                }
                defaultVideoQuality = videoQualities.get(model.playDefaultIndex);
            } else if (!TextUtils.isEmpty(model.url)) { // ??????URL????????????
                videoQualities.add(new TCVideoQuality(0, model.qualityName, model.url));
                defaultVideoQuality = videoQualities.get(0);
                videoURL = model.url;
            }

            if (TextUtils.isEmpty(videoURL)) {
                Toast.makeText(this.getContext(), "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TCUrlUtil.isRTMPPlay(videoURL)) { // ????????????????????????RTMP?????????
                mReportLiveStartTime = System.currentTimeMillis();
                mLivePlayer.setPlayerView(mTXCloudVideoView);
                playLiveURL(videoURL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
            } else if (TCUrlUtil.isFLVPlay(videoURL)) { // ????????????????????????FLV?????????
                mReportLiveStartTime = System.currentTimeMillis();
                mLivePlayer.setPlayerView(mTXCloudVideoView);
                playTimeShiftLiveURL(model);
                if (model.multiURLs != null && !model.multiURLs.isEmpty()) {
                    startMultiStreamLiveURL(videoURL);
                }
            } else { // ????????????????????????????????????
                mReportVodStartTime = System.currentTimeMillis();
                mVodPlayer.setPlayerView(mTXCloudVideoView);
                playVodURL(videoURL);
            }
            boolean isLivePlay = (TCUrlUtil.isRTMPPlay(videoURL) || TCUrlUtil.isFLVPlay(videoURL));
            updatePlayType(isLivePlay ? SuperPlayerConst.PLAYTYPE_LIVE : SuperPlayerConst.PLAYTYPE_VOD);
            updateTitle(model.title);
            updateVideoProgress(0, 0);
            mControllerFullScreen.setVideoQualityList(videoQualities);
            mControllerFullScreen.updateVideoQuality(defaultVideoQuality);
        }
    }

    /**
     * ??????FileId??????
     *
     * @param protocol
     */
    private void playModeVideo(IPlayInfoProtocol protocol) {
        playVodURL(protocol.getUrl());
        List<TCVideoQuality> videoQualityArrayList = protocol.getVideoQualityList();
        if (videoQualityArrayList != null)
            mControllerFullScreen.setVideoQualityList(videoQualityArrayList);
        TCVideoQuality defaultVideoQuality = protocol.getDefaultVideoQuality();
        if (defaultVideoQuality != null)
            mControllerFullScreen.updateVideoQuality(defaultVideoQuality);
    }

    /**
     * ????????????URL
     */
    private void playLiveURL(String url, int playType) {
        mCurrentPlayVideoURL = url;
        if (mLivePlayer != null) {
            mLivePlayer.setPlayListener(this);
            int result = mLivePlayer.startPlay(url, playType); // result????????????0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
            if (result != 0) {
                TXCLog.e(TAG, "playLiveURL videoURL:" + url + ",result:" + result);
            } else {
                mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING;
                mCurPlayType = PLAYER_TYPE.PLAYER_TYPE_LIVE;
                TXCLog.e(TAG, "playLiveURL mCurrentPlayState:" + mCurrentPlayState);
                mControllerFullScreen.setVideoType(TCControllerFullScreen.LIVE);
                mControllerWindow.setVideoType(TCControllerWindow.LIVE);
                mControllerWindow.setShowFullButton(false);
            }
        }
    }

    public void setShowFullButton(boolean showFullButton) {
        this.showFullButton = showFullButton;
    }

    /**
     * ????????????url
     */
    private void playVodURL(String url) {
        if (url == null || "".equals(url)) return;
        mCurrentPlayVideoURL = url;
        if (url.contains(".m3u8")) {
            mIsMultiBitrateStream = true;
        }
        if (mVodPlayer != null) {
            mDefaultQualitySet = false;
            mVodPlayer.setAutoPlay(true);
            mVodPlayer.setVodListener(this);
            int ret = mVodPlayer.startPlay(url);
            if (ret == 0) {
                mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING;
                mCurPlayType = PLAYER_TYPE.PLAYER_TYPE_VOD;
                TXCLog.e(TAG, "playVodURL mCurrentPlayState:" + mCurrentPlayState);
                mControllerFullScreen.setVideoType(TCControllerFullScreen.VIDEO);
                mControllerWindow.setVideoType(TCControllerWindow.VIDEO);
                mControllerWindow.setShowFullButton(true);
            }
        }
        mIsPlayWithFileId = false;
    }

//    public void showUi() {
//        mControllerWindow.show();
//        mControllerFullScreen.show();
//    }
//
//    public void hideUi() {
//        mControllerWindow.hide();
//        mControllerFullScreen.hide();
//    }


    /**
     * ??????????????????url
     */
    private void playTimeShiftLiveURL(final SuperPlayerModel model) {
        final String liveURL = model.url;
        final String bizid = liveURL.substring(liveURL.indexOf("//") + 2, liveURL.indexOf("."));
        final String domian = SuperPlayerGlobalConfig.getInstance().playShiftDomain;
        final String streamid = liveURL.substring(liveURL.lastIndexOf("/") + 1, liveURL.lastIndexOf("."));
        final int appid = model.appId;
        TXCLog.i(TAG, "bizid:" + bizid + ",streamid:" + streamid + ",appid:" + appid);
        playLiveURL(liveURL, TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        try {
            int bizidNum = Integer.valueOf(bizid);
            mLivePlayer.prepareLiveSeek(domian, bizidNum);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            TXCLog.e(TAG, "playTimeShiftLiveURL: bizidNum ?????? = %s " + bizid);
        }
    }

    /**
     * ???????????????url
     *
     * @param url
     */
    private void startMultiStreamLiveURL(String url) {
        mLivePlayConfig.setAutoAdjustCacheTime(false);
        mLivePlayConfig.setMaxAutoAdjustCacheTime(5);
        mLivePlayConfig.setMinAutoAdjustCacheTime(5);
        mLivePlayer.setConfig(mLivePlayConfig);
        if (mWatcher == null) mWatcher = new TCNetWatcher(mContext);
        mWatcher.start(url, mLivePlayer);
    }

    /**
     * ????????????
     *
     * @param title ????????????
     */
    private void updateTitle(String title) {
        mControllerWindow.updateTitle(title);
        mControllerFullScreen.updateTitle(title);
    }

    /**
     * ??????????????????
     *
     * @param current  ??????????????????(???)
     * @param duration ?????????(???)
     */
    private void updateVideoProgress(long current, long duration) {
        mControllerWindow.updateVideoProgress(current, duration);
        mControllerFullScreen.updateVideoProgress(current, duration);
    }

    /**
     * ??????????????????
     *
     * @param playType
     */
    private void updatePlayType(int playType) {
        mCurrentPlayType = playType;
        mControllerWindow.updatePlayType(playType);
        mControllerFullScreen.updatePlayType(playType);
    }

    /**
     * ??????????????????
     *
     * @param playState
     */
    private void updatePlayState(int playState) {
        mCurrentPlayState = playState;
        mControllerWindow.updatePlayState(playState);
        mControllerFullScreen.updatePlayState(playState);
    }

    /**
     * resume??????????????????
     */
    public void onResume() {
//        if (mDanmuView != null && mDanmuView.isPrepared() && mDanmuView.isPaused()) {
//            mDanmuView.resume();
//        }
        resume();
    }

    private void resume() {
        if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD && mVodPlayer != null) {
            mVodPlayer.resume();
        }
    }

    /**
     * pause??????????????????
     */
    public void onPause() {
//        if (mDanmuView != null && mDanmuView.isPrepared()) {
//            mDanmuView.pause();
//        }
        pause();
    }


    private void pause() {
        if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD && mVodPlayer != null) {
            mVodPlayer.pause();
        }
    }

    /**
     * ???????????????
     */
    public void resetPlayer() {
//        if (mDanmuView != null) {
//            mDanmuView.release();
//            mDanmuView = null;
//        }
        if (mControllerWindow != null) {
            mControllerWindow.stopHandler();
        }
        if (mControllerFullScreen != null) {
            mControllerFullScreen.stopHandler();
        }
        stopPlay();
    }

    /**
     * ????????????
     */
    private void stopPlay() {
        if (mVodPlayer != null) {
            mVodPlayer.setVodListener(null);
            mVodPlayer.stopPlay(false);
        }
        if (mLivePlayer != null) {
            mLivePlayer.setPlayListener(null);
            mLivePlayer.stopPlay(false);
            mTXCloudVideoView.removeVideoView();
        }
        if (mWatcher != null) {
            mWatcher.stop();
        }
        mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PAUSE;
        TXCLog.e(TAG, "stopPlay mCurrentPlayState:" + mCurrentPlayState);
        reportPlayTime();
    }


    /**
     * ??????????????????
     */
    private void reportPlayTime() {
        if (mReportLiveStartTime != -1) {
            long reportEndTime = System.currentTimeMillis();
            long diff = (reportEndTime - mReportLiveStartTime) / 1000;
            TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_LIVE_TIME, diff, 0);
            mReportLiveStartTime = -1;
        }
        if (mReportVodStartTime != -1) {
            long reportEndTime = System.currentTimeMillis();
            long diff = (reportEndTime - mReportVodStartTime) / 1000;
            TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_VOD_TIME, diff, mIsPlayWithFileId ? 1 : 0);
            mReportVodStartTime = -1;
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param callback
     */
    public void setPlayerViewCallback(OnSuperPlayerViewCallback callback) {
        mPlayerViewCallback = callback;
    }

    /**
     * ????????????????????????
     */
    private void fullScreen(boolean isFull) {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            if (screenCallback != null)
                screenCallback.full(isFull);
            if (isFull) {
                //?????????????????????????????????
                View decorView = activity.getWindow().getDecorView();
                if (decorView == null) return;
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                    decorView.setSystemUiVisibility(View.GONE);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
//                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            } else {
                View decorView = activity.getWindow().getDecorView();
                if (decorView == null) return;
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                    decorView.setSystemUiVisibility(View.VISIBLE);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
        }
    }

    private ScreenCallback screenCallback;

    public void setScreenCallback(ScreenCallback screenCallback) {
        this.screenCallback = screenCallback;
    }

    public interface ScreenCallback {
        void full(boolean isFull);
    }

    /**
     * ?????????controller??????
     */
    private IControllerCallback mControllerCallback = new IControllerCallback() {
        @Override
        public void onSwitchPlayMode(int requestPlayMode) {
            if (mCurrentPlayMode == requestPlayMode) return;
            if (mLockScreen) return;
            if (requestPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                fullScreen(true);
            } else {
                fullScreen(false);
            }
            mControllerFullScreen.hide();
            mControllerWindow.hide();
            mControllerFloat.hide();
            //??????????????????
            if (requestPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                if (mLayoutParamFullScreenMode == null)
                    return;
                removeView(mControllerWindow);
                addView(mControllerFullScreen, mVodControllerFullScreenParams);
                setLayoutParams(mLayoutParamFullScreenMode);
                rotateScreenOrientation(SuperPlayerConst.ORIENTATION_LANDSCAPE);
                if (mPlayerViewCallback != null) {
                    mPlayerViewCallback.onStartFullScreenPlay();
                }
            }
            // ??????????????????
            else if (requestPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
                // ??????????????????
                if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_FLOAT) {
                    try {
                        Context viewContext = SuperPlayerView.this.getContext();
                        Intent intent = null;
                        if (viewContext instanceof Activity) {
                            intent = new Intent(SuperPlayerView.this.getContext(), viewContext.getClass());
                        } else {
                            Toast.makeText(viewContext, "??????????????????", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mContext.startActivity(intent);
                        pause();
                        if (mLayoutParamWindowMode == null)
                            return;
                        mWindowManager.removeView(mControllerFloat);

                        if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                            mVodPlayer.setPlayerView(mTXCloudVideoView);
                        } else {
                            mLivePlayer.setPlayerView(mTXCloudVideoView);
                        }
                        resume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // ?????????????????????
                else if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                    if (mLayoutParamWindowMode == null)
                        return;
                    removeView(mControllerFullScreen);
                    addView(mControllerWindow, mVodControllerWindowParams);
                    setLayoutParams(mLayoutParamWindowMode);
                    rotateScreenOrientation(SuperPlayerConst.ORIENTATION_PORTRAIT);
                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback.onStopFullScreenPlay();
                    }
                }
            }
            //?????????????????????
            else if (requestPlayMode == SuperPlayerConst.PLAYMODE_FLOAT) {
                TXCLog.i(TAG, "requestPlayMode Float :" + Build.MANUFACTURER);
                SuperPlayerGlobalConfig prefs = SuperPlayerGlobalConfig.getInstance();
                if (!prefs.enableFloatWindow) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0???????????????????????????
                    if (!Settings.canDrawOverlays(mContext)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivity(intent);
                        return;
                    }
                } else {
                    if (!checkOp(mContext, OP_SYSTEM_ALERT_WINDOW)) {
                        Toast.makeText(mContext, "????????????????????????,??????????????????????????????", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                pause();

                mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                mWindowParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowParams.format = PixelFormat.TRANSLUCENT;
                mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;

                SuperPlayerGlobalConfig.TXRect rect = prefs.floatViewRect;
                mWindowParams.x = rect.x;
                mWindowParams.y = rect.y;
                mWindowParams.width = rect.width;
                mWindowParams.height = rect.height;
                try {
                    mWindowManager.addView(mControllerFloat, mWindowParams);
                } catch (Exception e) {
                    Toast.makeText(SuperPlayerView.this.getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }

                TXCloudVideoView videoView = mControllerFloat.getFloatVideoView();
                if (videoView != null) {
                    if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                        mVodPlayer.setPlayerView(videoView);
                    } else {
                        mLivePlayer.setPlayerView(videoView);
                    }
                    resume();
                }
                // ???????????????
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_FLOATMOE, 0, 0);
            }
            mCurrentPlayMode = requestPlayMode;
        }

        @Override
        public void onBackPressed(int playMode) {
            switch (playMode) {
                case SuperPlayerConst.PLAYMODE_FULLSCREEN:// ???????????????????????????????????????????????????
                    onSwitchPlayMode(SuperPlayerConst.PLAYMODE_WINDOW);
                    break;
                case SuperPlayerConst.PLAYMODE_WINDOW:// ?????????????????????????????????????????????
                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback.onClickSmallReturnBtn();
                    }
                    if (mCurrentPlayState == SuperPlayerConst.PLAYSTATE_PLAYING) {
                        onSwitchPlayMode(SuperPlayerConst.PLAYMODE_FLOAT);
                    }
                    break;
                case SuperPlayerConst.PLAYMODE_FLOAT:// ???????????????????????????
                    mWindowManager.removeView(mControllerFloat);
                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback.onClickFloatCloseBtn();
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onFloatPositionChange(int x, int y) {
            mWindowParams.x = x;
            mWindowParams.y = y;
            mWindowManager.updateViewLayout(mControllerFloat, mWindowParams);
        }

        @Override
        public void onPause() {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.pause();
                }
            } else {
                if (mLivePlayer != null) {
                    mLivePlayer.pause();
                }
                if (mWatcher != null) {
                    mWatcher.stop();
                }
            }
            updatePlayState(SuperPlayerConst.PLAYSTATE_PAUSE);
        }

        @Override
        public void onResume() {
            if (mCurrentPlayState == SuperPlayerConst.PLAYSTATE_END) { //??????
                if (mCurPlayType == PLAYER_TYPE.PLAYER_TYPE_LIVE) {   // ??????
                    if (TCUrlUtil.isRTMPPlay(mCurrentPlayVideoURL)) {
                        playLiveURL(mCurrentPlayVideoURL, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
                    } else if (TCUrlUtil.isFLVPlay(mCurrentPlayVideoURL)) {
                        playTimeShiftLiveURL(mCurrentModel);
                        if (mCurrentModel.multiURLs != null && !mCurrentModel.multiURLs.isEmpty()) {
                            startMultiStreamLiveURL(mCurrentPlayVideoURL);
                        }
                    }
                } else {
                    playVodURL(mCurrentPlayVideoURL);  // ??????
                }
            } else if (mCurrentPlayState == SuperPlayerConst.PLAYSTATE_PAUSE) { //????????????
                if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                    if (mVodPlayer != null) {
                        mVodPlayer.resume();
                    }
                } else {
                    if (mLivePlayer != null) {
                        mLivePlayer.resume();
                    }
                }
            }
            updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
        }

        @Override
        public void onSeekTo(int position) {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.seek(position);
                }
            } else {
                updatePlayType(SuperPlayerConst.PLAYTYPE_LIVE_SHIFT);
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_TIMESHIFT, 0, 0);
                if (mLivePlayer != null) {
                    mLivePlayer.seek(position);
                }
                if (mWatcher != null) {
                    mWatcher.stop();
                }
            }
        }

        @Override
        public void onResumeLive() {
            if (mLivePlayer != null) {
                mLivePlayer.resumeLive();
            }
            updatePlayType(SuperPlayerConst.PLAYTYPE_LIVE);
        }

        @Override
        public void onDanmuToggle(boolean isOpen) {
//            if (mDanmuView != null) {
//                mDanmuView.toggle(isOpen);
//            }
        }

        @Override
        public void onSnapshot() {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.snapshot(new TXLivePlayer.ITXSnapshotListener() {
                        @Override
                        public void onSnapshot(Bitmap bmp) {
                            showSnapshotWindow(bmp);
                        }
                    });
                }
            } else if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
                Toast.makeText(getContext(), "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            } else {
                if (mLivePlayer != null) {
                    mLivePlayer.snapshot(new TXLivePlayer.ITXSnapshotListener() {
                        @Override
                        public void onSnapshot(Bitmap bmp) {
                            showSnapshotWindow(bmp);
                        }
                    });
                }
            }
        }

        @Override
        public void onQualityChange(TCVideoQuality quality) {
            mControllerFullScreen.updateVideoQuality(quality);
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    if (quality.index == -1) {
                        // ???????????????bitrate???m3u8?????????????????????seek
                        float currentTime = mVodPlayer.getCurrentPlaybackTime();
                        mVodPlayer.stopPlay(true);
                        TXCLog.i(TAG, "onQualitySelect quality.url:" + quality.url);
                        mVodPlayer.setStartTime(currentTime);
                        mVodPlayer.startPlay(quality.url);
                    } else {
                        TXCLog.i(TAG, "setBitrateIndex quality.index:" + quality.index);
                        // ????????????bitrate???m3u8????????????????????????seek
                        mVodPlayer.setBitrateIndex(quality.index);
                    }
                }
            } else {
                if (mLivePlayer != null && !TextUtils.isEmpty(quality.url)) {
                    int result = mLivePlayer.switchStream(quality.url);
                    if (result < 0) {
                        Toast.makeText(getContext(), "??????" + quality.title + "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "???????????????" + quality.title + "...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            //???????????????
            TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_CHANGE_RESOLUTION, 0, 0);
        }

        @Override
        public void onSpeedChange(float speedLevel) {
            if (mVodPlayer != null) {
                mVodPlayer.setRate(speedLevel);
            }
            //??????????????????
            TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_CHANGE_SPEED, 0, 0);
        }

        @Override
        public void onMirrorToggle(boolean isMirror) {
            if (mVodPlayer != null) {
                mVodPlayer.setMirror(isMirror);
            }
            if (isMirror) {
                //????????????
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_MIRROR, 0, 0);
            }
        }

        @Override
        public void onHWAccelerationToggle(boolean isAccelerate) {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                mChangeHWAcceleration = true;
                if (mVodPlayer != null) {
                    mVodPlayer.enableHardwareDecode(isAccelerate);
                    mSeekPos = (int) mVodPlayer.getCurrentPlaybackTime();
                    TXCLog.i(TAG, "save pos:" + mSeekPos);
                    stopPlay();
                    playModeVideo(mCurrentProtocol);
                }
            } else {
                if (mLivePlayer != null) {
                    mLivePlayer.enableHardwareDecode(isAccelerate);
                    playWithModel(mCurrentModel);
                }
            }
            // ??????????????????
            if (isAccelerate) {
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_HW_DECODE, 0, 0);
            } else {
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_SOFT_DECODE, 0, 0);
            }
        }
    };


    /**
     * ??????????????????
     *
     * @param bmp
     */
    private void showSnapshotWindow(final Bitmap bmp) {
        if (bmp == null) return;
        final PopupWindow popupWindow = new PopupWindow(mContext);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_new_vod_snap, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_snap);
        imageView.setImageBitmap(bmp);
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(mRootView, Gravity.TOP, 1800, 300);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TCImageUtil.save2MediaStore(mContext, bmp);
            }
        });
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        }, 3000);
    }

    /**
     * ??????????????????
     *
     * @param orientation
     */
    private void rotateScreenOrientation(int orientation) {
        switch (orientation) {
            case SuperPlayerConst.ORIENTATION_LANDSCAPE:
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case SuperPlayerConst.ORIENTATION_PORTRAIT:
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    /**
     * ?????????????????????
     *
     * ??????????????????????????????https://cloud.tencent.com/document/product/881/20216
     *
     * @param player
     * @param event  ??????id.id??????????????? {@linkplain TXLiveConstants#PLAY_EVT_CONNECT_SUCC ??????????????????}.
     * @param param
     */
    /**
     * ?????????????????????
     * <p>
     * ??????????????????????????????https://cloud.tencent.com/document/product/881/20216
     *
     * @param player
     * @param event  ??????id.id??????????????? {@linkplain TXLiveConstants#PLAY_EVT_CONNECT_SUCC ??????????????????}.
     * @param param
     */
    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        if (event != TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            String playEventLog = "TXVodPlayer onPlayEvent event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
            TXCLog.d(TAG, playEventLog);
        }
        switch (event) {
            case TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED://??????????????????
                mControllerWindow.hideBackground();
                updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
                if (mIsMultiBitrateStream) {
                    List<TXBitrateItem> bitrateItems = mVodPlayer.getSupportedBitrates();
                    if (bitrateItems == null || bitrateItems.size() == 0)
                        return;
                    Collections.sort(bitrateItems); //masterPlaylist????????????????????????????????????????????????
                    List<TCVideoQuality> videoQualities = new ArrayList<>();
                    int size = bitrateItems.size();
                    for (int i = 0; i < size; i++) {
                        TXBitrateItem bitrateItem = bitrateItems.get(i);
                        TCVideoQuality quality = TCVideoQualityUtil.convertToVideoQuality(bitrateItem, i);
                        videoQualities.add(quality);
                    }
                    if (!mDefaultQualitySet) {
                        TXBitrateItem defaultItem = bitrateItems.get(bitrateItems.size() - 1);
                        mVodPlayer.setBitrateIndex(defaultItem.index); //???????????????????????????
                        TXBitrateItem bitrateItem = bitrateItems.get(bitrateItems.size() - 1);
                        TCVideoQuality defaultVideoQuality = TCVideoQualityUtil.convertToVideoQuality(bitrateItem, bitrateItems.size() - 1);
                        mControllerFullScreen.updateVideoQuality(defaultVideoQuality);
                        mDefaultQualitySet = true;
                    }
                    mControllerFullScreen.setVideoQualityList(videoQualities);
                }
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                if (mChangeHWAcceleration) { //?????????????????????????????????seek??????
                    TXCLog.i(TAG, "seek pos:" + mSeekPos);
                    mControllerCallback.onSeekTo(mSeekPos);
                    mChangeHWAcceleration = false;
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                updatePlayState(SuperPlayerConst.PLAYSTATE_END);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS);
                int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS);
                updateVideoProgress(progress / 1000, duration / 1000);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN: {
                updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
                break;
            }
            default:
                break;
        }
        if (event < 0) {// ????????????????????????
            mVodPlayer.stopPlay(true);
            updatePlayState(SuperPlayerConst.PLAYSTATE_PAUSE);
            Toast.makeText(mContext, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {

    }

    public interface PlayCallback {
        void onLiveOver();
    }

    private PlayCallback playCallback;
    private boolean isLive = false;

    public void setPlayCallback(PlayCallback playCallback) {
        this.playCallback = playCallback;
    }

    /**
     * ?????????????????????
     * <p>
     * ??????????????????????????????https://cloud.tencent.com/document/product/881/20217
     *
     * @param event ??????id.id??????????????? {@linkplain TXLiveConstants#PUSH_EVT_CONNECT_SUCC ??????????????????}.
     * @param param
     */
    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (event != TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            String playEventLog = "TXLivePlayer onPlayEvent event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
            TXCLog.d(TAG, playEventLog);
        }
        switch (event) {
            case TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED: //??????????????????
                updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
                if (mWatcher != null) mWatcher.exitLoading();
                // TODO: 2020/5/20 ????????????
                TXCLog.d(TAG, "PLAY_EVT_PLAY_BEGIN==========");
//                isLive = true;
                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                TXCLog.d(TAG, "PLAY_EVT_PLAY_END==========");
                // TODO: 2020/5/20 ????????????
                if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {  // ?????????????????????????????????
                    mControllerCallback.onResumeLive();
                    Toast.makeText(mContext, "????????????,????????????", Toast.LENGTH_SHORT).show();
                    updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
                } else {
                    stopPlay();
                    updatePlayState(SuperPlayerConst.PLAYSTATE_END);
                    if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                        Toast.makeText(mContext, "???????????????,????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
                    }
//                    if (isLive && playCallback != null) {
//                        playCallback.onLiveOver();
//                        isLive = false;
//                    }
                    if (playCallback != null) {
                        playCallback.onLiveOver();
                    }

                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
            case TXLiveConstants.PLAY_WARNING_RECONNECT:
                updatePlayState(SuperPlayerConst.PLAYSTATE_LOADING);
                if (mWatcher != null) mWatcher.enterLoading();
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                break;
            case TXLiveConstants.PLAY_EVT_STREAM_SWITCH_SUCC:
                Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_SHORT).show();
                break;
            case TXLiveConstants.PLAY_ERR_STREAM_SWITCH_FAIL:
                Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_SHORT).show();
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS);
                mMaxLiveProgressTime = progress > mMaxLiveProgressTime ? progress : mMaxLiveProgressTime;
                updateVideoProgress(progress / 1000, mMaxLiveProgressTime / 1000);
                break;
            default:
                break;
        }
    }


    @Override
    public void onNetStatus(Bundle status) {

    }

    /**
     * ??????????????????
     *
     * @param playMode
     */
    public void requestPlayMode(int playMode) {
        if (playMode == SuperPlayerConst.PLAYMODE_WINDOW) {
            if (mControllerCallback != null) {
                mControllerCallback.onSwitchPlayMode(SuperPlayerConst.PLAYMODE_WINDOW);
            }
        } else if (playMode == SuperPlayerConst.PLAYMODE_FLOAT) {
            if (mPlayerViewCallback != null) {
                mPlayerViewCallback.onStartFloatWindowPlay();
            }
            if (mControllerCallback != null) {
                mControllerCallback.onSwitchPlayMode(SuperPlayerConst.PLAYMODE_FLOAT);
            }
        }
    }

    /**
     * ?????????????????????
     * <p>
     * API <18?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * API >= 19 ????????????????????????????????????
     * API >=23????????????manifest???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * API >25???TYPE_TOAST ?????????????????????????????????????????????????????????
     */
    private boolean checkOp(Context context, int op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                TXCLog.e(TAG, Log.getStackTraceString(e));
            }
        }
        return true;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public int getPlayMode() {
        return mCurrentPlayMode;
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public int getPlayState() {
        return mCurrentPlayState;
    }

    /**
     * SuperPlayerView???????????????
     */
    public interface OnSuperPlayerViewCallback {

        /**
         * ??????????????????
         */
        void onStartFullScreenPlay();

        /**
         * ??????????????????
         */
        void onStopFullScreenPlay();

        /**
         * ???????????????????????????x??????
         */
        void onClickFloatCloseBtn();

        /**
         * ????????????????????????????????????
         */
        void onClickSmallReturnBtn();

        /**
         * ?????????????????????
         */
        void onStartFloatWindowPlay();
    }

    public void release() {
        if (mControllerWindow != null) {
            mControllerWindow.release();
        }
        if (mControllerFullScreen != null) {
            mControllerFullScreen.release();
        }
        if (mControllerFloat != null) {
            mControllerFloat.release();
        }
        removeAllViews();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            release();
        } catch (Exception e) {
            TXCLog.e(TAG, Log.getStackTraceString(e));
        } catch (Error e) {
            TXCLog.e(TAG, Log.getStackTraceString(e));
        }
    }
}
