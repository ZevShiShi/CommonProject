package com.tencent.liteav.play.controller;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.liteav.play.R;
import com.tencent.liteav.play.SuperPlayerConst;
import com.tencent.liteav.play.bean.TCPlayImageSpriteInfo;
import com.tencent.liteav.play.bean.TCPlayKeyFrameDescInfo;
import com.tencent.liteav.play.bean.TCVideoQuality;
import com.tencent.liteav.play.utils.TCTimeUtil;
import com.tencent.liteav.play.utils.TCVideoGestureUtil;
import com.tencent.liteav.play.view.TCPointSeekBar;
import com.tencent.liteav.play.view.TCVideoProgressLayout;
import com.tencent.liteav.play.view.TCVolumeBrightnessProgressLayout;

import java.util.List;

/**
 * 窗口模式播放控件
 * <p>
 * 除基本播放控制外，还有手势控制快进快退、手势调节亮度音量等
 * <p>
 * 1、点击事件监听{@link #onClick(View)}
 * <p>
 * 2、触摸事件监听{@link #onTouchEvent(MotionEvent)}
 * <p>
 * 2、进度条事件监听{@link #onProgressChanged(TCPointSeekBar, int, boolean)}
 * {@link #onStartTrackingTouch(TCPointSeekBar)}
 * {@link #onStopTrackingTouch(TCPointSeekBar)}
 */
public class TCControllerWindow extends RelativeLayout implements IController, View.OnClickListener,
        TCPointSeekBar.OnSeekBarChangeListener {

    // UI控件
    private LinearLayout llProgressTime;                            // 时间区域
    private LinearLayout llSeek;                                    // seekBar区域
    private RelativeLayout mLayoutTop;                             // 顶部标题栏布局
    private LinearLayout mLayoutBottom;                          // 底部进度条所在布局
    private ImageView mIvPause;                               // 暂停播放按钮
    private ImageView mIvFullScreen;                          // 全屏按钮
    private TextView mTvTitle;                               // 视频名称文本
    private TextView mTvBackToLive;                          // 返回直播文本
    private ImageView mBackground;                            // 背景
    private ImageView mIvWatermark;                           // 水印
    private TextView mTvCurrent;                             // 当前进度文本
    private TextView mTvDuration;                            // 总时长文本
    private TCPointSeekBar mSeekBarProgress;                       // 播放进度条
    private LinearLayout mLayoutReplay;                          // 重播按钮所在布局
    private ProgressBar mPbLiveLoading;                         // 加载圈
    private TCVolumeBrightnessProgressLayout mGestureVolumeBrightnessProgressLayout; // 音量亮度调节布局
    private TCVideoProgressLayout mGestureVideoProgressLayout;            // 手势快进提示布局

    private IControllerCallback mControllerCallback;                    // 播放控制回调
    private HideViewControllerViewRunnable mHideViewRunnable;                      // 隐藏控件子线程
    private GestureDetector mGestureDetector;                       // 手势检测监听器
    private TCVideoGestureUtil mVideoGestureUtil;                      // 手势控制工具

    private boolean isShowing;                              // 自身是否可见
    private boolean mIsChangingSeekBarProgress;             // 进度条是否正在拖动，避免SeekBar由于视频播放的update而跳动
    private int mPlayType;                              // 当前播放视频类型
    private int mCurrentPlayState = -1;                 // 当前播放状态
    private long mDuration;                              // 视频总时长
    private long mLivePushDuration;                      // 直播推流总时长
    private long mProgress;                              // 当前播放进度
    private long mMaxLiveProgressTime;                   // 观看直播最大时长

    private Bitmap mBackgroundBmp;                         // 背景图
    private Bitmap mWaterMarkBmp;                          // 水印图
    private float mWaterMarkBmpX;                         // 水印x坐标
    private float mWaterMarkBmpY;                         // 水印y坐标
    private long mLastClickTime;                         // 上次点击事件的时间
    //    private LinearLayout llPause;
    private boolean showFullButton = true;

    public static int VIDEO = 0;
    public static int LIVE = 1;


    public TCControllerWindow(Context context) {
        super(context);
        init(context);
    }

    public TCControllerWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TCControllerWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setVideoType(int videoType) {
        if (videoType == VIDEO) {
            llProgressTime.setVisibility(View.VISIBLE);
            llSeek.setVisibility(View.VISIBLE);
        } else {
            llProgressTime.setVisibility(View.GONE);
            llSeek.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化控件、手势检测监听器、亮度/音量/播放进度的回调
     */
    private void init(Context context) {
        initView(context);
        mHideViewRunnable = new HideViewControllerViewRunnable(this);
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                togglePlayState();
                show();
                if (mHideViewRunnable != null && getHandler() != null) {
                    getHandler().removeCallbacks(mHideViewRunnable);
                    getHandler().postDelayed(mHideViewRunnable, 7000);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggle();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX, float distanceY) {
                if (downEvent == null || moveEvent == null) {
                    return false;
                }
                if (mVideoGestureUtil != null && mGestureVolumeBrightnessProgressLayout != null) {
                    mVideoGestureUtil.check(mGestureVolumeBrightnessProgressLayout.getHeight(), downEvent, moveEvent, distanceX, distanceY);
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                if (mVideoGestureUtil != null) {
                    mVideoGestureUtil.reset(getWidth(), mSeekBarProgress.getProgress());
                }
                return true;
            }

        });
        mGestureDetector.setIsLongpressEnabled(false);

        mVideoGestureUtil = new TCVideoGestureUtil(getContext());
        mVideoGestureUtil.setVideoGestureListener(new TCVideoGestureUtil.VideoGestureListener() {
            @Override
            public void onBrightnessGesture(float newBrightness) {
                if (mGestureVolumeBrightnessProgressLayout != null) {
                    mGestureVolumeBrightnessProgressLayout.setProgress((int) (newBrightness * 100));
                    mGestureVolumeBrightnessProgressLayout.setImageResource(R.drawable.ic_light_max);
                    mGestureVolumeBrightnessProgressLayout.show();
                }
            }

            @Override
            public void onVolumeGesture(float volumeProgress) {
                if (mGestureVolumeBrightnessProgressLayout != null) {
                    mGestureVolumeBrightnessProgressLayout.setImageResource(R.drawable.ic_volume_max);
                    mGestureVolumeBrightnessProgressLayout.setProgress((int) volumeProgress);
                    mGestureVolumeBrightnessProgressLayout.show();
                }
            }

            @Override
            public void onSeekGesture(int progress) {
                mIsChangingSeekBarProgress = true;
                if (mGestureVideoProgressLayout != null) {

                    if (progress > mSeekBarProgress.getMax()) {
                        progress = mSeekBarProgress.getMax();
                    }
                    if (progress < 0) {
                        progress = 0;
                    }
                    mGestureVideoProgressLayout.setProgress(progress);
                    mGestureVideoProgressLayout.show();

                    float percentage = ((float) progress) / mSeekBarProgress.getMax();
                    float currentTime = (mDuration * percentage);
                    if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE || mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
                        if (mLivePushDuration > SuperPlayerConst.MAX_SHIFT_TIME) {
                            currentTime = (int) (mLivePushDuration - SuperPlayerConst.MAX_SHIFT_TIME * (1 - percentage));
                        } else {
                            currentTime = mLivePushDuration * percentage;
                        }
                        mGestureVideoProgressLayout.setTimeText(TCTimeUtil.formattedTime((long) currentTime));
                    } else {
                        mGestureVideoProgressLayout.setTimeText(TCTimeUtil.formattedTime((long) currentTime) + " / " + TCTimeUtil.formattedTime((long) mDuration));
                    }

                }
                if (mSeekBarProgress != null)
                    mSeekBarProgress.setProgress(progress);
            }
        });
    }

    /**
     * 初始化view
     */
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.vod_controller_window, this);
        llProgressTime = findViewById(R.id.llProgressTime);
        llSeek = findViewById(R.id.llSeek);
        mLayoutTop = (RelativeLayout) findViewById(R.id.layout_top);
        mLayoutTop.setOnClickListener(this);
        mLayoutBottom = (LinearLayout) findViewById(R.id.layout_bottom);
        mLayoutBottom.setOnClickListener(this);
        mLayoutReplay = (LinearLayout) findViewById(R.id.layout_replay);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvPause = (ImageView) findViewById(R.id.iv_pause);
        mTvCurrent = (TextView) findViewById(R.id.tv_current);
        mTvDuration = (TextView) findViewById(R.id.tv_duration);
        mSeekBarProgress = (TCPointSeekBar) findViewById(R.id.seekbar_progress);
        mSeekBarProgress.setProgress(0);
        mSeekBarProgress.setMax(100);
        mIvFullScreen = (ImageView) findViewById(R.id.iv_fullscreen);
        mTvBackToLive = (TextView) findViewById(R.id.tv_backToLive);
        mPbLiveLoading = (ProgressBar) findViewById(R.id.pb_live);
//        llPause = (LinearLayout) findViewById(R.id.ll_pause);
        mTvBackToLive.setOnClickListener(this);
        mIvPause.setOnClickListener(this);
        mIvFullScreen.setOnClickListener(this);
        mLayoutTop.setOnClickListener(this);
        mLayoutReplay.setOnClickListener(this);

        mSeekBarProgress.setOnSeekBarChangeListener(this);

        mGestureVolumeBrightnessProgressLayout = (TCVolumeBrightnessProgressLayout) findViewById(R.id.gesture_progress);

        mGestureVideoProgressLayout = (TCVideoProgressLayout) findViewById(R.id.video_progress_layout);

        mBackground = (ImageView) findViewById(R.id.small_iv_background);
        setBackground(mBackgroundBmp);

        mIvWatermark = (ImageView) findViewById(R.id.small_iv_water_mark);
    }

    public void setShowFullButton(boolean showFullButton) {
        this.showFullButton = showFullButton;
        mIvFullScreen.setVisibility(showFullButton ? View.VISIBLE : View.GONE);
    }

    /**
     * 切换播放状态
     * <p>
     * 双击和点击播放/暂停按钮会触发此方法
     */
    private void togglePlayState() {
        switch (mCurrentPlayState) {
            case SuperPlayerConst.PLAYSTATE_PAUSE:
            case SuperPlayerConst.PLAYSTATE_END:
                if (mControllerCallback != null) {
                    mControllerCallback.onResume();
                }
                break;
            case SuperPlayerConst.PLAYSTATE_PLAYING:
            case SuperPlayerConst.PLAYSTATE_LOADING:
                if (mControllerCallback != null) {
                    mControllerCallback.onPause();
                }
                mLayoutReplay.setVisibility(View.GONE);
                break;
        }
        show();
    }

    /**
     * 切换自身的可见性
     */
    private void toggle() {
        if (isShowing) {
            hide();
        } else {
            show();
            if (mHideViewRunnable != null && getHandler() != null) {
                getHandler().removeCallbacks(mHideViewRunnable);
                getHandler().postDelayed(mHideViewRunnable, 7000);
            }
        }
    }

    /**
     * 设置回调
     *
     * @param callback 回调接口实现对象
     */
    @Override
    public void setCallback(IControllerCallback callback) {
        mControllerCallback = callback;
    }

    /**
     * 设置水印
     *
     * @param bmp 水印图
     * @param x   水印的x坐标
     * @param y   水印的y坐标
     */
    @Override
    public void setWatermark(final Bitmap bmp, float x, float y) {
        mWaterMarkBmp = bmp;
        mWaterMarkBmpX = x;
        mWaterMarkBmpY = y;
        if (bmp != null) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    int width = TCControllerWindow.this.getWidth();
                    int height = TCControllerWindow.this.getHeight();

                    int x = (int) (width * mWaterMarkBmpX) - bmp.getWidth() / 2;
                    int y = (int) (height * mWaterMarkBmpY) - bmp.getHeight() / 2;

                    mIvWatermark.setX(x);
                    mIvWatermark.setY(y);

                    mIvWatermark.setVisibility(VISIBLE);
                    setBitmap(mIvWatermark, bmp);
                }
            });
        } else {
            mIvWatermark.setVisibility(GONE);
        }
    }

    /**
     * 显示控件
     */
    @Override
    public void show() {
        isShowing = true;
        mLayoutTop.setVisibility(View.VISIBLE);
        mLayoutBottom.setVisibility(View.VISIBLE);
//        llPause.setVisibility(VISIBLE);
        if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
            mTvBackToLive.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏控件
     */
    @Override
    public void hide() {
        isShowing = false;
        mLayoutTop.setVisibility(View.GONE);
        mLayoutBottom.setVisibility(View.GONE);
//        llPause.setVisibility(GONE);
        if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
            mTvBackToLive.setVisibility(View.GONE);
        }
    }

    /**
     * 释放控件的内存
     */
    @Override
    public void release() {
    }

    /**
     * 更新播放状态
     *
     * @param playState 正在播放{@link SuperPlayerConst#PLAYSTATE_PLAYING}
     *                  正在加载{@link SuperPlayerConst#PLAYSTATE_LOADING}
     *                  暂停   {@link SuperPlayerConst#PLAYSTATE_PAUSE}
     *                  播放结束{@link SuperPlayerConst#PLAYSTATE_END}
     */
    @Override
    public void updatePlayState(int playState) {
        switch (playState) {
            case SuperPlayerConst.PLAYSTATE_PLAYING:
                mIvPause.setImageResource(R.drawable.ic_vod_pause_normal);
                toggleView(mPbLiveLoading, false);
                toggleView(mLayoutReplay, false);
                break;
            case SuperPlayerConst.PLAYSTATE_LOADING:
                mIvPause.setImageResource(R.drawable.ic_vod_pause_normal);
                toggleView(mPbLiveLoading, true);
                toggleView(mLayoutReplay, false);
                break;
            case SuperPlayerConst.PLAYSTATE_PAUSE:
                mIvPause.setImageResource(R.drawable.ic_vod_play_normal);
                toggleView(mPbLiveLoading, false);
                toggleView(mLayoutReplay, false);
                break;
            case SuperPlayerConst.PLAYSTATE_END:
                mIvPause.setImageResource(R.drawable.ic_vod_play_normal);
                toggleView(mPbLiveLoading, false);
                toggleView(mLayoutReplay, true);
                break;
        }
        mCurrentPlayState = playState;
    }

    /**
     * 设置视频画质信息
     *
     * @param list 画质列表
     */
    @Override
    public void setVideoQualityList(List<TCVideoQuality> list) {
    }

    /**
     * 更新视频名称
     *
     * @param title 视频名称
     */
    @Override
    public void updateTitle(String title) {
        mTvTitle.setText(title);
    }

    /**
     * 更新视频播放进度
     *
     * @param current  当前进度(秒)
     * @param duration 视频总时长(秒)
     */
    @Override
    public void updateVideoProgress(long current, long duration) {
        mProgress = current < 0 ? 0 : current;
        mDuration = duration < 0 ? 0 : duration;
        mTvCurrent.setText(TCTimeUtil.formattedTime(mProgress));

        float percentage = mDuration > 0 ? ((float) mProgress / (float) mDuration) : 1.0f;
        if (mProgress == 0) {
            mLivePushDuration = 0;
            percentage = 0;
        }
        if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE || mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
            mLivePushDuration = mLivePushDuration > mProgress ? mLivePushDuration : mProgress;
            long leftTime = mDuration - mProgress;
            mDuration = mDuration > SuperPlayerConst.MAX_SHIFT_TIME ? SuperPlayerConst.MAX_SHIFT_TIME : mDuration;
            percentage = 1 - (float) leftTime / (float) mDuration;
        }

        if (percentage >= 0 && percentage <= 1) {
            int progress = Math.round(percentage * mSeekBarProgress.getMax());
            if (!mIsChangingSeekBarProgress) {
                if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE) {
                    mSeekBarProgress.setProgress(mSeekBarProgress.getMax());
                } else {
                    mSeekBarProgress.setProgress(progress);
                }
            }
            mTvDuration.setText(TCTimeUtil.formattedTime(mDuration));
        }
    }

    /**
     * 更新播放类型
     *
     * @param type 点播     {@link SuperPlayerConst#PLAYTYPE_VOD}
     *             点播     {@link SuperPlayerConst#PLAYTYPE_LIVE}
     *             直播回看  {@link SuperPlayerConst#PLAYTYPE_LIVE_SHIFT}
     */
    @Override
    public void updatePlayType(int type) {
        mPlayType = type;
        switch (type) {
            case SuperPlayerConst.PLAYTYPE_VOD:
                mTvBackToLive.setVisibility(View.GONE);
                mTvDuration.setVisibility(View.VISIBLE);
                break;
            case SuperPlayerConst.PLAYTYPE_LIVE:
                mTvBackToLive.setVisibility(View.GONE);
                mTvDuration.setVisibility(View.GONE);
                mSeekBarProgress.setProgress(100);
                break;
            case SuperPlayerConst.PLAYTYPE_LIVE_SHIFT:
                if (mLayoutBottom.getVisibility() == VISIBLE)
                    mTvBackToLive.setVisibility(View.VISIBLE);
                mIvPause.setVisibility(VISIBLE);
                mTvDuration.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 设置背景
     *
     * @param bitmap 背景图
     */
    @Override
    public void setBackground(final Bitmap bitmap) {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (bitmap == null) return;
                if (mBackground == null) {
                    mBackgroundBmp = bitmap;
                } else {
                    setBitmap(mBackground, mBackgroundBmp);
                }
            }
        });
    }

    /**
     * 设置目标ImageView显示的图片
     */
    private void setBitmap(ImageView view, Bitmap bitmap) {
        if (view == null || bitmap == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(new BitmapDrawable(getContext().getResources(), bitmap));
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
        }
    }

    /**
     * 显示背景
     */
    @Override
    public void showBackground() {
        this.post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator alpha = ValueAnimator.ofFloat(0.0f, 1);
                alpha.setDuration(500);
                alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        mBackground.setAlpha(value);
                        if (value == 1) {
                            mBackground.setVisibility(VISIBLE);
                        }
                    }
                });
                alpha.start();
            }
        });
    }

    /**
     * 隐藏背景
     */
    @Override
    public void hideBackground() {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (mBackground.getVisibility() != View.VISIBLE) return;
                ValueAnimator alpha = ValueAnimator.ofFloat(1.0f, 0.0f);
                alpha.setDuration(500);
                alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        mBackground.setAlpha(value);
                        if (value == 0) {
                            mBackground.setVisibility(GONE);
                        }
                    }
                });
                alpha.start();
            }
        });
    }

    /**
     * 更新视频播放画质
     *
     * @param videoQuality 画质
     */
    @Override
    public void updateVideoQuality(TCVideoQuality videoQuality) {

    }

    /**
     * 更新略缩图信息
     *
     * @param info 略缩图信息
     */
    @Override
    public void updateImageSpriteInfo(TCPlayImageSpriteInfo info) {

    }

    /**
     * 更新关键帧信息
     *
     * @param list 关键帧信息列表
     */
    @Override
    public void updateKeyFrameDescInfo(List<TCPlayKeyFrameDescInfo> list) {

    }

    /**
     * 重写触摸事件监听，实现手势调节亮度、音量以及播放进度
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null)
            mGestureDetector.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP && mVideoGestureUtil != null && mVideoGestureUtil.isVideoProgressModel()) {
            int progress = mVideoGestureUtil.getVideoProgress();
            if (progress > mSeekBarProgress.getMax()) {
                progress = mSeekBarProgress.getMax();
            }
            if (progress < 0) {
                progress = 0;
            }
            mSeekBarProgress.setProgress(progress);

            int seekTime;
            float percentage = progress * 1.0f / mSeekBarProgress.getMax();
            if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE || mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
                if (mLivePushDuration > SuperPlayerConst.MAX_SHIFT_TIME) {
                    seekTime = (int) (mLivePushDuration - SuperPlayerConst.MAX_SHIFT_TIME * (1 - percentage));
                } else {
                    seekTime = (int) (mLivePushDuration * percentage);
                }
            } else {
                seekTime = (int) (percentage * mDuration);
            }
            if (mControllerCallback != null) {
                mControllerCallback.onSeekTo(seekTime);
            }
            mIsChangingSeekBarProgress = false;
        }
        if (getHandler() != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                this.getHandler().removeCallbacks(mHideViewRunnable);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                this.getHandler().postDelayed(mHideViewRunnable, 7000);
            }
        }
        return true;
    }

    /**
     * 设置点击事件监听
     */
    @Override
    public void onClick(View view) {
        if (System.currentTimeMillis() - mLastClickTime < 300) { //限制点击频率
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        int id = view.getId();
        if (id == R.id.layout_top) { //顶部标题栏
            if (mControllerCallback != null) {
                mControllerCallback.onBackPressed(SuperPlayerConst.PLAYMODE_WINDOW);
            }
        } else if (id == R.id.iv_pause) { //暂停\播放按钮
            togglePlayState();
        } else if (id == R.id.iv_fullscreen) { //全屏按钮
            if (mControllerCallback != null) {
                mControllerCallback.onSwitchPlayMode(SuperPlayerConst.PLAYMODE_FULLSCREEN);
            }
        } else if (id == R.id.layout_replay) { //重播按钮
            if (mControllerCallback != null) {
                mControllerCallback.onResume();
            }
        } else if (id == R.id.tv_backToLive) { //返回直播按钮
            if (mControllerCallback != null) {
                mControllerCallback.onResumeLive();
            }
        }
    }

    @Override
    public void onProgressChanged(TCPointSeekBar seekBar, int progress, boolean fromUser) {
        if (mGestureVideoProgressLayout != null && fromUser) {
            mGestureVideoProgressLayout.show();
            float percentage = ((float) progress) / seekBar.getMax();
            float currentTime = (mDuration * percentage);
            if (mPlayType == SuperPlayerConst.PLAYTYPE_LIVE || mPlayType == SuperPlayerConst.PLAYTYPE_LIVE_SHIFT) {
                if (mLivePushDuration > SuperPlayerConst.MAX_SHIFT_TIME) {
                    currentTime = (int) (mLivePushDuration - SuperPlayerConst.MAX_SHIFT_TIME * (1 - percentage));
                } else {
                    currentTime = mLivePushDuration * percentage;
                }
                mGestureVideoProgressLayout.setTimeText(TCTimeUtil.formattedTime((long) currentTime));
            } else {
                mGestureVideoProgressLayout.setTimeText(TCTimeUtil.formattedTime((long) currentTime) + " / " + TCTimeUtil.formattedTime((long) mDuration));
            }
            mGestureVideoProgressLayout.setProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(TCPointSeekBar seekBar) {
        if (getHandler() != null) {
            getHandler().removeCallbacks(mHideViewRunnable);
        }
    }

    @Override
    public void onStopTrackingTouch(TCPointSeekBar seekBar) {
        int curProgress = seekBar.getProgress();
        int maxProgress = seekBar.getMax();

        switch (mPlayType) {
            case SuperPlayerConst.PLAYTYPE_VOD:
                if (curProgress >= 0 && curProgress <= maxProgress) {
                    // 关闭重播按钮
                    toggleView(mLayoutReplay, false);
                    float percentage = ((float) curProgress) / maxProgress;
                    int position = (int) (mDuration * percentage);
                    if (mControllerCallback != null) {
                        mControllerCallback.onSeekTo(position);
                        mControllerCallback.onResume();
                    }
                }
                break;
            case SuperPlayerConst.PLAYTYPE_LIVE:
            case SuperPlayerConst.PLAYTYPE_LIVE_SHIFT:
                toggleView(mPbLiveLoading, true);
                int seekTime = (int) (mLivePushDuration * curProgress * 1.0f / maxProgress);
                if (mLivePushDuration > SuperPlayerConst.MAX_SHIFT_TIME) {
                    seekTime = (int) (mLivePushDuration - SuperPlayerConst.MAX_SHIFT_TIME * (maxProgress - curProgress) * 1.0f / maxProgress);
                }
                if (mControllerCallback != null) {
                    mControllerCallback.onSeekTo(seekTime);
                }
                break;
        }
        this.getHandler().postDelayed(mHideViewRunnable, 7000);
    }

    public void stopHandler() {
        if (getHandler() != null) {
            getHandler().removeCallbacksAndMessages(null);
        }
    }

    /**
     * 设置控件的可见性
     *
     * @param view      目标控件
     * @param isVisible 显示：true 隐藏：false
     */
    private void toggleView(final View view, final boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
