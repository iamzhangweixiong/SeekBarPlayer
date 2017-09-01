package com.zhangwx.seekbarplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.zhangwx.permission.EasyPermissions;
import com.zhangwx.permission.PermissionUtils;
import com.zhangwx.permission.bridge.PermissionRequestBridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.seeker);
        seekBar.setPadding(10,0,0,0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(MainActivity.this, PermissionUtils.PERMISSIONS_STORAGE_GROUP)) {
                    play();
                } else {
                    PermissionRequestBridge.getInstance(MainActivity.this).request(
                            true,
                            PermissionUtils.REQUEST_STROAGE_CODE,
                            PermissionUtils.PERMISSIONS_STORAGE_GROUP,
                            getString(R.string.permission_storage_title),
                            getString(R.string.rationale_storage), new PermissionRequestBridge.RequestCallBack() {
                                @Override
                                public void onRequestResult(boolean isGranted) {
                                    if (isGranted) {
                                        play();
                                    }
                                }
                            }
                    );
                }
            }
        });

        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });

        mediaPlayer = new MediaPlayer();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final int posi = mediaPlayer.getCurrentPosition();
                Log.e("zhang", "run: posi = " + posi);
                seekBar.setProgress(posi);
            }
        }, 500, 500);
    }

    private void play() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource("/sdcard/test.mp3");
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setProgress(0);
                seekBar.setMax(mp.getDuration());
                Log.e("zhang", "onPrepared: mp.getDuration() = " + mp.getDuration());
                mp.start();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
