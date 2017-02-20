package com.tchip.autoplayer;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;

import com.tchip.autoplayer.model.Video;
import com.tchip.autoplayer.util.MyLog;
import com.tchip.autoplayer.view.VolumeView;
import com.tchip.autoplayer.view.VideoView;

public class MainActivity extends Activity {

    private boolean isOnline = false;
    private boolean isChangedVideo = false;

    public static LinkedList<Video> videoLinkedList;

    private Uri videoListUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    private static int position;
    private static boolean backFromAD = false;
    private int playedTime;

    /**
     * 视频视图
     */
    private VideoView videoView;
    private SeekBar seekBar;
    /**
     * 视频总时间
     */
    private TextView textTimeTotal;
    /**
     * 已播放时间
     */
    private TextView textTimePlay;
    private GestureDetector mGestureDetector = null;
    private AudioManager mAudioManager = null;

    private int maxVolume = 0;
    private int currentVolume = 0;

    /**
     * 视频列表按钮
     */
    private ImageButton btnList;
    /**
     * 上一部按钮
     */
    private ImageButton btnPrevious;
    /**
     * 播放/暂停
     */
    private ImageButton btnState;
    /**
     * 下一部按钮
     */
    private ImageButton btnNext;
    /**
     * 调节音量按钮
     */
    private ImageButton btnVolume;

    private View controlView = null;
    private PopupWindow popupControl;

    /**
     * 音量视图
     */
    private VolumeView volumeView;
    private PopupWindow popupVolume;

    private View extralView;
    private PopupWindow extralWindow = null;

    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static int controlHeight = 0;

    private final static int TIME = 6868;

    private boolean isControllerShow = true;
    private boolean isPaused = false;
    private boolean isFullScreen = false;
    private boolean isSilent = false;
    private boolean isSoundShow = false;

    private ListView listVideo;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialLayout();
    }


    @Override
    protected void onResume() {
        if (!isChangedVideo) {
            videoView.seekTo(playedTime);
            videoView.start();
        } else {
            isChangedVideo = false;
        }

        // if(videoView.getVideoHeight()!=0){
        if (videoView.isPlaying()) {
            btnState.setImageResource(R.drawable.pause);
            hideControllerDelay();
        }

        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        if (popupControl.isShowing()) {
            popupControl.dismiss();
            extralWindow.dismiss();
        }
        if (popupVolume.isShowing()) {
            popupVolume.dismiss();
        }

        myHandler.removeMessages(PROGRESS_CHANGED);
        myHandler.removeMessages(HIDE_CONTROLLER);

        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }

        videoLinkedList.clear();

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {

            videoView.stopPlayback();

            int result = data.getIntExtra("CHOOSE", -1);
            Log.d("RESULT", "" + result);
            if (result != -1) {
                isOnline = false;
                isChangedVideo = true;
                videoView.setVideoPath(videoLinkedList.get(result).getPath());
                position = result;
            } else {
                String url = data.getStringExtra("CHOOSE_URL");
                if (url != null) {
                    videoView.setVideoPath(url);
                    isOnline = true;
                    isChangedVideo = true;
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initialLayout() {
        listVideo = (ListView) findViewById(R.id.listVideo);
        videoLinkedList = new LinkedList<Video>();

        Looper.myQueue().addIdleHandler(new IdleHandler() {

            @Override
            public boolean queueIdle() {
                if (popupControl != null && videoView.isShown()) {
                    popupControl.showAtLocation(videoView, Gravity.BOTTOM, 0, 0);
                    // controler.update(screenWidth, controlHeight);
                    popupControl.update(0, 0, screenWidth, controlHeight);
                }

                if (extralWindow != null && videoView.isShown()) {
                    extralWindow.showAtLocation(videoView, Gravity.TOP, 0, 0);
                    extralWindow.update(0, 25, screenWidth, 60);
                }

                // myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
                return false;
            }
        });

        controlView = getLayoutInflater().inflate(R.layout.controler, null);
        popupControl = new PopupWindow(controlView);
        textTimeTotal = (TextView) controlView.findViewById(R.id.textTimeTotal);
        textTimePlay = (TextView) controlView.findViewById(R.id.textTimePlay);

        volumeView = new VolumeView(this);
        volumeView.setOnVolumeChangeListener(new VolumeView.OnVolumeChangedListener() {

            @Override
            public void setYourVolume(int index) {

                cancelDelayHide();
                updateVolume(index);
                hideControllerDelay();
            }
        });

        popupVolume = new PopupWindow(volumeView);

        extralView = getLayoutInflater().inflate(R.layout.extral, null);
        extralWindow = new PopupWindow(extralView);

        ImageButton btnBack = (ImageButton) extralView
                .findViewById(R.id.btnBack);

        position = -1;

        btnBack.setOnClickListener(myOnClickListener);


        btnList = (ImageButton) controlView.findViewById(R.id.btnList);
        btnPrevious = (ImageButton) controlView.findViewById(R.id.btnPrevious);
        btnState = (ImageButton) controlView.findViewById(R.id.btnState);
        btnNext = (ImageButton) controlView.findViewById(R.id.btnNext);
        btnVolume = (ImageButton) controlView.findViewById(R.id.btnVolume);

        videoView = (VideoView) findViewById(R.id.videoView);

        videoView.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                videoView.stopPlayback();
                isOnline = false;

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error")
                        .setMessage("?")
                        .setPositiveButton("Confirm",
                                new AlertDialog.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        videoView.stopPlayback();
                                    }

                                }).setCancelable(false).show();

                return false;
            }

        });

        Uri uri = getIntent().getData();
        if (uri != null) {
            videoView.stopPlayback();
            videoView.setVideoURI(uri);
            isOnline = true;

            btnState.setImageResource(R.drawable.pause);
        } else {
            btnState.setImageResource(R.drawable.play);
        }

        getVideoFile(videoLinkedList, new File("/sdcard/"));

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            Cursor cursor = getContentResolver()
                    .query(videoListUri,
                            new String[]{"_display_name", "_data"}, null,
                            null, null);
            int n = cursor.getCount();
            cursor.moveToFirst();
            LinkedList<Video> playList2 = new LinkedList<Video>();
            for (int i = 0; i != n; ++i) {
                Video video = new Video(cursor.getString(cursor
                        .getColumnIndex("_display_name")), cursor.getString(cursor.getColumnIndex("_data")));
                playList2.add(video);
                cursor.moveToNext();
            }

            if (playList2.size() > videoLinkedList.size()) {
                videoLinkedList = playList2;
            }
        }

        videoView.setMySizeChangeLinstener(new VideoView.MySizeChangeLinstener() {

            @Override
            public void doMyThings() {
                setVideoScale(SCREEN_DEFAULT);
            }

        });

        btnList.setAlpha(0xBB);
        btnPrevious.setAlpha(0xBB);
        btnState.setAlpha(0xBB);
        btnNext.setAlpha(0xBB);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        btnVolume.setAlpha(getAlphaByVolume());

        btnList.setOnClickListener(myOnClickListener);

        btnNext.setOnClickListener(myOnClickListener);

        btnState.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelDelayHide();
                if (isPaused) {
                    videoView.start();
                    btnState.setImageResource(R.drawable.pause);
                    hideControllerDelay();
                } else {
                    videoView.pause();
                    btnState.setImageResource(R.drawable.play);
                }
                isPaused = !isPaused;

            }

        });

        btnPrevious.setOnClickListener(myOnClickListener);

        btnVolume.setOnClickListener(myOnClickListener);

        btnVolume.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                if (isSilent) {
                    btnVolume.setImageResource(R.drawable.soundenable);
                } else {
                    btnVolume.setImageResource(R.drawable.sounddisable);
                }
                isSilent = !isSilent;
                updateVolume(currentVolume);
                cancelDelayHide();
                hideControllerDelay();
                return true;
            }

        });

        seekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekbar, int progress,
                                          boolean fromUser) {
                if (fromUser) {

                    if (!isOnline) {
                        videoView.seekTo(progress);
                    }

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                myHandler.removeMessages(HIDE_CONTROLLER);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, TIME);
            }
        });

        getScreenSize();

        mGestureDetector = new GestureDetector(new SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isFullScreen) {
                    setVideoScale(SCREEN_DEFAULT);
                } else {
                    setVideoScale(SCREEN_FULL);
                }
                isFullScreen = !isFullScreen;
                MyLog.d("onDoubleTap");

                if (isControllerShow) {
                    showController();
                }
                // return super.onDoubleTap(e);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!isControllerShow) {
                    showController();
                    hideControllerDelay();
                } else {
                    cancelDelayHide();
                    hideController();
                }
                // return super.onSingleTapConfirmed(e);
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (isPaused) {
                    videoView.start();
                    btnState.setImageResource(R.drawable.pause);
                    cancelDelayHide();
                    hideControllerDelay();
                } else {
                    videoView.pause();
                    btnState.setImageResource(R.drawable.play);
                    cancelDelayHide();
                    showController();
                }
                isPaused = !isPaused;
                // super.onLongPress(e);
            }
        });

        // vv.setVideoPath("http://202.108.16.171/cctv/video/A7/E8/69/27/A7E86927D2BF4D2FA63471D1C5F97D36/gphone/480_320/200/0.mp4");

        videoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer arg0) {
                setVideoScale(SCREEN_DEFAULT);
                isFullScreen = false;
                if (isControllerShow) {
                    showController();
                }

                int i = videoView.getDuration();
                Log.d("onCompletion", "" + i);
                seekBar.setMax(i);
                i /= 1000;
                int minute = i / 60;
                int hour = minute / 60;
                int second = i % 60;
                minute %= 60;
                textTimeTotal.setText(String.format("%02d:%02d:%02d", hour,
                        minute, second));

				/*
                 * controler.showAtLocation(vv, Gravity.BOTTOM, 0, 0);
				 * controler.update(screenWidth, controlHeight);
				 * myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
				 */

                videoView.start();
                btnState.setImageResource(R.drawable.pause);
                hideControllerDelay();
                myHandler.sendEmptyMessage(PROGRESS_CHANGED);
            }
        });

        videoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                int n = videoLinkedList.size();
                isOnline = false;
                if (++position < n) {
                    videoView.setVideoPath(videoLinkedList.get(position).getPath());
                } else {
                    videoView.stopPlayback();
                    MainActivity.this.finish();
                }
            }
        });
    }

    private final static int PROGRESS_CHANGED = 0;
    private final static int HIDE_CONTROLLER = 1;

    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PROGRESS_CHANGED:

                    int i = videoView.getCurrentPosition();
                    seekBar.setProgress(i);

                    if (isOnline) {
                        int j = videoView.getBufferPercentage();
                        seekBar.setSecondaryProgress(j * seekBar.getMax() / 100);
                    } else {
                        seekBar.setSecondaryProgress(0);
                    }

                    i /= 1000;
                    int minute = i / 60;
                    int hour = minute / 60;
                    int second = i % 60;
                    minute %= 60;
                    textTimePlay.setText(String.format("%02d:%02d:%02d", hour,
                            minute, second));

                    sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
                    break;

                case HIDE_CONTROLLER:
                    hideController();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);

        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {

				/*
                 * if(!isControllerShow){ showController();
				 * hideControllerDelay(); }else { cancelDelayHide();
				 * hideController(); }
				 */
            }
            result = super.onTouchEvent(event);
        }

        return result;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getScreenSize();
        if (isControllerShow) {
            cancelDelayHide();
            hideController();
            showController();
            hideControllerDelay();
        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        playedTime = videoView.getCurrentPosition();
        videoView.pause();
        btnState.setImageResource(R.drawable.play);
        super.onPause();
    }

    private void getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        screenHeight = display.getHeight();
        screenWidth = display.getWidth();
        controlHeight = screenHeight / 4;

        // adView = (AdView) extralView.findViewById(R.id.ad);
        // LayoutParams lp = adView.getLayoutParams();
        // lp.width = screenWidth*3/5;
    }

    private void hideController() {
        if (popupControl.isShowing()) {
            popupControl.update(0, 0, 0, 0);
            extralWindow.update(0, 0, screenWidth, 0);
            isControllerShow = false;
        }
        if (popupVolume.isShowing()) {
            popupVolume.dismiss();
            isSoundShow = false;
        }
    }

    private void hideControllerDelay() {
        myHandler.sendEmptyMessageDelayed(HIDE_CONTROLLER, TIME);
    }

    private void showController() {
        popupControl.update(0, 0, screenWidth, controlHeight);
        if (isFullScreen) {
            extralWindow.update(0, 0, screenWidth, 60);
        } else {
            extralWindow.update(0, 25, screenWidth, 60);
        }

        isControllerShow = true;
    }

    private void cancelDelayHide() {
        myHandler.removeMessages(HIDE_CONTROLLER);
    }

    private final static int SCREEN_FULL = 0;
    private final static int SCREEN_DEFAULT = 1;

    private void setVideoScale(int flag) {
        LayoutParams lp = videoView.getLayoutParams();

        switch (flag) {
            case SCREEN_FULL:
                MyLog.d("screenWidth: " + screenWidth + " screenHeight: "
                        + screenHeight);
                videoView.setVideoScale(screenWidth, screenHeight);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                break;

            case SCREEN_DEFAULT:
                int videoWidth = videoView.getVideoWidth();
                int videoHeight = videoView.getVideoHeight();
                int mWidth = screenWidth;
                int mHeight = screenHeight - 25;

                if (videoWidth > 0 && videoHeight > 0) {
                    if (videoWidth * mHeight > mWidth * videoHeight) {
                        // Log.i("@@@", "image too tall, correcting");
                        mHeight = mWidth * videoHeight / videoWidth;
                    } else if (videoWidth * mHeight < mWidth * videoHeight) {
                        // Log.i("@@@", "image too wide, correcting");
                        mWidth = mHeight * videoWidth / videoHeight;
                    } else {

                    }
                }
                videoView.setVideoScale(mWidth, mHeight);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
        }
    }

    private int getAlphaByVolume() {
        if (mAudioManager != null) {
            // int currentVolume =
            // mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int alpha = currentVolume * (0xCC - 0x55) / maxVolume + 0x55;
            return alpha;
        } else {
            return 0xCC;
        }
    }

    private void updateVolume(int index) {
        if (mAudioManager != null) {
            if (isSilent) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            } else {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index,
                        0);
            }
            currentVolume = index;
            btnVolume.setAlpha(getAlphaByVolume());
        }
    }

    private void getVideoFile(final LinkedList<Video> videoList, File file) {

        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                String name = file.getName();
                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".3gp")) {

                        Video video = new Video(file.getName(), file.getAbsolutePath());
                        videoList.add(video);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    getVideoFile(videoList, file);
                }
                return false;
            }
        });
    }


    private MyOnClickListener myOnClickListener = new MyOnClickListener();

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnBack: {
                    MainActivity.this.finish();
                }
                break;

                case R.id.btnList: {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,
                            VideoChooseActivity.class);
                    MainActivity.this.startActivityForResult(intent, 0);
                    cancelDelayHide();
                }
                break;

                case R.id.btnPrevious: {
                    isOnline = false;
                    if (--position >= 0) {
                        videoView.setVideoPath(videoLinkedList.get(position).getPath());
                        cancelDelayHide();
                        hideControllerDelay();
                    } else {
                        MainActivity.this.finish();
                    }
                }
                break;

                case R.id.btnNext: {
                    int n = videoLinkedList.size();
                    isOnline = false;
                    if (++position < n) {
                        videoView.setVideoPath(videoLinkedList.get(position).getPath());
                        cancelDelayHide();
                        hideControllerDelay();
                    } else {
                        MainActivity.this.finish();
                    }
                }
                break;

                case R.id.btnVolume: {
                    cancelDelayHide();
                    if (isSoundShow) {
                        popupVolume.dismiss();
                    } else {
                        if (popupVolume.isShowing()) {
                            popupVolume.update(15, 0, VolumeView.MY_WIDTH,
                                    VolumeView.MY_HEIGHT);
                        } else {
                            popupVolume.showAtLocation(videoView, Gravity.RIGHT
                                    | Gravity.CENTER_VERTICAL, 15, 0);
                            popupVolume.update(15, 0, VolumeView.MY_WIDTH,
                                    VolumeView.MY_HEIGHT);
                        }
                    }
                    isSoundShow = !isSoundShow;
                    hideControllerDelay();
                }
                break;

                default:
                    break;
            }
        }
    }
}