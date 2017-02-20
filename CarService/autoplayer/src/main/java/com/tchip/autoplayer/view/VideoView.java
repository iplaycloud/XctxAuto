package com.tchip.autoplayer.view;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

import java.io.IOException;

/**
 * Displays a video file.  The VideoView class
 * can load images from various sources (such as resources or content
 * providers), takes care of computing its measurement from the video so that
 * it can be used in any layout manager, and provides various display options
 * such as scaling and tinting.
 */
public class VideoView extends SurfaceView implements MediaPlayerControl {
    private String TAG = "VideoView";

    private Context mContext;

    // settable by the client
    private Uri mUri;
    private int mDuration;

    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer mediaPlayer = null;
    private boolean isPrepared;
    private int videoWidth;
    private int videoHeight;
    private int surfaceWidth;
    private int surfaceHeight;
    private MediaController mediaController;
    private OnCompletionListener myOnCompletionListener;
    private MediaPlayer.OnPreparedListener myOnPreparedListener;
    private int currentBufferPercentage;
    private OnErrorListener myOnErrorListener;
    private boolean startWhenPrepared;
    private int seekWhenPrepared;

    private MySizeChangeLinstener mMyChangeLinstener;

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoScale(int width, int height) {
        LayoutParams lp = getLayoutParams();
        lp.height = height;
        lp.width = width;
        setLayoutParams(lp);
    }

    public interface MySizeChangeLinstener {
        public void doMyThings();
    }

    public void setMySizeChangeLinstener(MySizeChangeLinstener l) {
        mMyChangeLinstener = l;
    }

    public VideoView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure");
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        /*if (mVideoWidth > 0 && mVideoHeight > 0) {
            if ( mVideoWidth * height  > width * mVideoHeight ) {
                //Log.i("@@@", "image too tall, correcting");
                height = width * mVideoHeight / mVideoWidth;
            } else if ( mVideoWidth * height  < width * mVideoHeight ) {
                //Log.i("@@@", "image too wide, correcting");
                width = height * mVideoWidth / mVideoHeight;
            } else {
                //Log.i("@@@", "aspect ratio is correct: " +
                        //width+"/"+height+"="+
                        //mVideoWidth+"/"+mVideoHeight);
            }
        }*/
        //Log.i("@@@@@@@@@@", "setting size: " + width + 'x' + height);
        setMeasuredDimension(width, height);
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                 * than max size imposed on ourselves.
                 */
                result = desiredSize;
                break;

            case MeasureSpec.AT_MOST:
                /* Parent says we can be as big as we want, up to specSize.
                 * Don't be larger than specSize, and don't be larger than
                 * the max size imposed on ourselves.
                 */
                result = Math.min(desiredSize, specSize);
                break;

            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    private void initVideoView() {
        videoWidth = 0;
        videoHeight = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        startWhenPrepared = false;
        seekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        // Tell the music playback service to pause
        // TODO: these constants need to be published somewhere in the framework.
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(mPreparedListener);
            mediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            isPrepared = false;
            Log.v(TAG, "reset duration to -1 in openVideo");
            mDuration = -1;
            mediaPlayer.setOnCompletionListener(mCompletionListener);
            mediaPlayer.setOnErrorListener(mErrorListener);
            mediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            currentBufferPercentage = 0;
            mediaPlayer.setDataSource(mContext, mUri);
            mediaPlayer.setDisplay(mSurfaceHolder);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepareAsync();
            attachMediaController();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        }
    }

    public void setMediaController(MediaController controller) {
        if (mediaController != null) {
            mediaController.hide();
        }
        mediaController = controller;
        attachMediaController();
    }

    private void attachMediaController() {
        if (mediaPlayer != null && mediaController != null) {
            mediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View) this.getParent() : this;
            mediaController.setAnchorView(anchorView);
            mediaController.setEnabled(isPrepared);
        }
    }

    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new MediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    videoWidth = mp.getVideoWidth();
                    videoHeight = mp.getVideoHeight();

                    if (mMyChangeLinstener != null) {
                        mMyChangeLinstener.doMyThings();
                    }

                    if (videoWidth != 0 && videoHeight != 0) {
                        getHolder().setFixedSize(videoWidth, videoHeight);
                    }
                }
            };

    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            // briefly show the mediacontroller
            isPrepared = true;
            if (myOnPreparedListener != null) {
                myOnPreparedListener.onPrepared(mediaPlayer);
            }
            if (mediaController != null) {
                mediaController.setEnabled(true);
            }
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
                getHolder().setFixedSize(videoWidth, videoHeight);
                if (surfaceWidth == videoWidth && surfaceHeight == videoHeight) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (seekWhenPrepared != 0) {
                        mediaPlayer.seekTo(seekWhenPrepared);
                        seekWhenPrepared = 0;
                    }
                    if (startWhenPrepared) {
                        mediaPlayer.start();
                        startWhenPrepared = false;
                        if (mediaController != null) {
                            mediaController.show();
                        }
                    } else if (!isPlaying() &&
                            (seekWhenPrepared != 0 || getCurrentPosition() > 0)) {
                        if (mediaController != null) {
                            // Show the media controls when we're paused into a video and make 'em stick.
                            mediaController.show(0);
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (seekWhenPrepared != 0) {
                    mediaPlayer.seekTo(seekWhenPrepared);
                    seekWhenPrepared = 0;
                }
                if (startWhenPrepared) {
                    mediaPlayer.start();
                    startWhenPrepared = false;
                }
            }
        }
    };

    private OnCompletionListener mCompletionListener =
            new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    if (mediaController != null) {
                        mediaController.hide();
                    }
                    if (myOnCompletionListener != null) {
                        myOnCompletionListener.onCompletion(mediaPlayer);
                    }
                }
            };

    private OnErrorListener mErrorListener =
            new OnErrorListener() {
                public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    if (mediaController != null) {
                        mediaController.hide();
                    }

            /* If an error handler has been supplied, use it and finish. */
                    if (myOnErrorListener != null) {
                        if (myOnErrorListener.onError(mediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }

            /* Otherwise, pop up an error dialog so the user knows that
             * something bad has happened. Only try and pop up the dialog
             * if we're attached to a window. When we're going away and no
             * longer have a window, don't bother showing the user an error.
             */
                    if (getWindowToken() != null) {
                        Resources r = mContext.getResources();
                        int messageId;

/*                if (framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                    messageId = com.android.internal.R.string.VideoView_error_text_invalid_progressive_playback;
                } else {
                    messageId = com.android.internal.R.string.VideoView_error_text_unknown;
                }

                new AlertDialog.Builder(mContext)
                        .setTitle(com.android.internal.R.string.VideoView_error_title)
                        .setMessage(messageId)
                        .setPositiveButton(com.android.internal.R.string.VideoView_error_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                         If we get here, there is no onError listener, so
                                         * at least inform them that the video is over.
                                         
                                        if (mOnCompletionListener != null) {
                                            mOnCompletionListener.onCompletion(mMediaPlayer);
                                        }
                                    }
                                })
                        .setCancelable(false)
                        .show();*/
                    }
                    return true;
                }
            };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    currentBufferPercentage = percent;
                }
            };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        myOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        myOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        myOnErrorListener = l;
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int w, int h) {
            surfaceWidth = w;
            surfaceHeight = h;
            if (mediaPlayer != null && isPrepared && videoWidth == w && videoHeight == h) {
                if (seekWhenPrepared != 0) {
                    mediaPlayer.seekTo(seekWhenPrepared);
                    seekWhenPrepared = 0;
                }
                mediaPlayer.start();
                if (mediaController != null) {
                    mediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            openVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            if (mediaController != null) mediaController.hide();
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isPrepared && mediaPlayer != null && mediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isPrepared && mediaPlayer != null && mediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isPrepared &&
                keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL &&
                mediaPlayer != null &&
                mediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mediaPlayer.isPlaying()) {
                    pause();
                    mediaController.show();
                } else {
                    start();
                    mediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    && mediaPlayer.isPlaying()) {
                pause();
                mediaController.show();
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            mediaController.show();
        }
    }

    public void start() {
        if (mediaPlayer != null && isPrepared) {
            mediaPlayer.start();
            startWhenPrepared = false;
        } else {
            startWhenPrepared = true;
        }
    }

    public void pause() {
        if (mediaPlayer != null && isPrepared) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        startWhenPrepared = false;
    }

    public int getDuration() {
        if (mediaPlayer != null && isPrepared) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null && isPrepared) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        if (mediaPlayer != null && isPrepared) {
            mediaPlayer.seekTo(msec);
        } else {
            seekWhenPrepared = msec;
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer != null && isPrepared) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public int getBufferPercentage() {
        if (mediaPlayer != null) {
            return currentBufferPercentage;
        }
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
