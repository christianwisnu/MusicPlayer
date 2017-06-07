package com.example.project.musicplayer;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by christian on 03/04/17.
 */

public class MusicPlayer extends Activity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageView back;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    // Media Player
    private MediaPlayer mp;
    private Utilities utils;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private static final int READ_REQUEST_CODE = 42;
    private GifImageView gifImageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music);
        gifImageView = (GifImageView) findViewById(R.id.GifImageView);
        gifImageView.setGifImageResource(R.drawable.animasi);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        back = (ImageView)findViewById(R.id.ImbBack);
        btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        mp = new MediaPlayer();
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important

        playSong();

        songProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                seekChange(view);
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.release();
                finish();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });

        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });
    }

    private void seekChange(View v){
        if(mp.isPlaying()){
            SeekBar sb = (SeekBar)v;
            mp.seekTo(sb.getProgress());
        }
    }

    public void  playSong(){
        // Play song
        try {
            mp.reset();
            mp = MediaPlayer.create(this, R.raw.lagu);
            mp.start();
            // Displaying Song title
            //songTitleLabel.setText("TESTER");

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
         playSong();
    }

    /*@Override
    public void onDestroy(){
        super.onDestroy();
        mp.release();
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            mp.release();
            finish();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }

    }
}
