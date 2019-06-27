package com.mywbut.project.desiplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button next,previous,pause;
    TextView songTextLabel;
    SeekBar songSeekbar;
    static MediaPlayer myMediaPlayer;
    int position;
    ArrayList<File>mysongs;
    Thread updateSeekBar;
    String sName;



    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        next=(Button)findViewById(R.id.next);
        previous=(Button)findViewById(R.id.previous);
        pause=(Button)findViewById(R.id.pause);

        songTextLabel=(TextView)findViewById(R.id.SongLabel);

        songSeekbar=(SeekBar)findViewById(R.id.seekBar);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateSeekBar=new Thread(){
            @Override
            public void run() {

                int totalDuration=myMediaPlayer.getDuration();
                int currentPosition=0;
                while (currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition=myMediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        };

        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();

        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mysongs=(ArrayList)bundle.getParcelableArrayList("songs");
        sName=mysongs.get(position).getName().toString();
        String songName=i.getStringExtra("songname");
        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

        position= bundle.getInt("pos",0);
        Uri u=Uri.parse(mysongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);
        myMediaPlayer.start();
        songSeekbar.setMax(myMediaPlayer.getDuration());
        updateSeekBar.start();
        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);
        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songSeekbar.setMax(myMediaPlayer.getDuration());

                if(myMediaPlayer.isPlaying()){
                    pause.setBackgroundResource(R.drawable.icon_play);
                    myMediaPlayer.pause();

                }
                else {
                    pause.setBackgroundResource(R.drawable.icon_pause);
                    myMediaPlayer.start();
                }
            }
        });
     next.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             myMediaPlayer.stop();
             myMediaPlayer.release();
             position=((position+1)%mysongs.size());

             Uri u =Uri.parse(mysongs.get(position).toString());
             myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

             sName=mysongs.get(position).getName().toString();
             songTextLabel.setText(sName);
             myMediaPlayer.start();
         }
     });
     previous.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             myMediaPlayer.stop();
             myMediaPlayer.release();
             position=((position-1)<0)?(mysongs.size()-1):(position-1);

             Uri u=Uri.parse(mysongs.get(position).toString());
             myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

             sName=mysongs.get(position).getName().toString();
             songTextLabel.setText(sName);
             myMediaPlayer.start();


         }
     });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
