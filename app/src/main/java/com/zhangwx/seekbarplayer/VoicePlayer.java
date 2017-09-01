package com.zhangwx.seekbarplayer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangweixiong on 2017/9/1.
 */

public class VoicePlayer implements MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        ProximitySensor.OnSensorEventListener {

    private List<IVoicePlayActionListener> VoicePlayActionListeners = Collections.synchronizedList(new ArrayList<IVoicePlayActionListener>());
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private ProximitySensor mSensor;
    private Activity mContext;
    private Timer mTimer;

    public VoicePlayer(Context context) {
        mContext = (Activity) context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mSensor = new ProximitySensor(context);
        mSensor.registryEventListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        final int duration = mp.getDuration();
        for (IVoicePlayActionListener listener : VoicePlayActionListeners) {
            listener.onPrepared(duration);
        }
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        cancelTimer();
        for (IVoicePlayActionListener listener : VoicePlayActionListeners) {
            listener.onCompletion();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        for (IVoicePlayActionListener listener : VoicePlayActionListeners) {
            listener.onSeekComplete();
        }
    }

    @Override
    public void onSensorChange(boolean isCloseActive) {
        if (isCloseActive) {
            setSpeakerphoneOn(false);
        } else {
            setSpeakerphoneOn(true);
        }
    }

    public void initPlay(String path) {
        if (mMediaPlayer == null) {
            return;
        }
        cancelTimer();
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            startTimer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentPos() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekToPos(int sec) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(sec);
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        cancelTimer();
        VoicePlayActionListeners.clear();
        if (mSensor != null) {
            mSensor.unRegistryEventListener();
        }
    }

    public void startTimer() {
        mTimer = new Timer("VoicePlayer");
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (IVoicePlayActionListener listener : VoicePlayActionListeners) {
                    listener.onPlaying(getCurrentPos());
                }
            }
        }, 50, 100);
    }

    public void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    public void addVoicePlayListener(IVoicePlayActionListener listener) {
        if (listener == null) {
            return;
        }
        if (!VoicePlayActionListeners.contains(listener)) {
            VoicePlayActionListeners.add(listener);
        }
    }

    public void removeVoicePlayListener(IVoicePlayActionListener listener) {
        if (listener == null) {
            return;
        }
        if (VoicePlayActionListeners.contains(listener)) {
            VoicePlayActionListeners.remove(listener);
        }
    }

    public interface IVoicePlayActionListener {
        void onPrepared(int duration);

        void onPlaying(int position);

        void onSeekComplete();

        void onCompletion();
    }

    public static long getDuration(String path) {
        if (TextUtils.isEmpty(path) || !Utils.isFileExist(path)) {
            return 0;
        }
        final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        final String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        retriever.release(); //释放
        if (!TextUtils.isEmpty(duration)) {
            return Long.parseLong(duration) / 1000;
        } else {
            return 0;
        }
    }

    private void setSpeakerphoneOn(boolean enable) {
        if (mContext == null || mAudioManager == null) {
            return;
        }
        if (enable) {
            mContext.setVolumeControlStream(AudioManager.STREAM_SYSTEM);
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            mAudioManager.setSpeakerphoneOn(true);
        } else {
            mContext.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            mAudioManager.setSpeakerphoneOn(false);
            mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), AudioManager.FLAG_PLAY_SOUND);
        }
    }
}
