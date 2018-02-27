package com.example.anil.aplayer;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class SongPlayer extends AppCompatActivity {
    MediaPlayer player;
    ImageView iv;
    TextView song;
    int position=0;
    ArrayList<HashMap<String,String>> songsdata;
    SeekBar seekBar;
    Thread  UpdateSeekbar;
    TextView current_postion,max_position;
    int max_dur,cut_dur;
    LinearLayout ll;
    boolean play_flag=true;
    ImageView play_image;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);
        position=getIntent().getIntExtra("pos",0);
        songsdata= (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("songslist");
        song=(TextView)findViewById(R.id.song_ti);
        iv=(ImageView)findViewById(R.id.image);
        play_image=(ImageView)findViewById(R.id.play);
        seekBar=(SeekBar)findViewById(R.id.seekbar);
        current_postion=(TextView)findViewById(R.id.current_duration);
        max_position=(TextView)findViewById(R.id.max_duration);
        //ll=(LinearLayout)findViewById(R.id.liner);
        Toast.makeText(this, ""+position+"  "+songsdata.size(), Toast.LENGTH_SHORT).show();
        setSongsdata(position);
        preferences=getSharedPreferences("shuffle",MODE_PRIVATE);
    }

    private void setSongsdata(int postion) {
        song.setText(songsdata.get(postion).get("songTitle"));

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art = null;
        BitmapFactory.Options bfo=new BitmapFactory.Options();

        mmr.setDataSource(this, Uri.parse(songsdata.get(postion).get("songPath")));
        rawArt = mmr.getEmbeddedPicture();

// if rawArt is null then no cover art is embedded in the file or is not
// recognized as such.
        if (null != rawArt)
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        iv.setImageBitmap(art);
        /*Palette palette = Palette.generate(art);
        ll.setBackgroundColor(palette.getDarkMutedSwatch().getPopulation());*/
        player=MediaPlayer.create(this,Uri.parse(songsdata.get(postion).get("songPath")));
        UpdateSeekbar=new Thread(){
            @Override
            public void run() {
                int totladuration=player.getDuration();
                int currentposition=0;
                while (currentposition<totladuration){
                    try{
                        sleep(500);
                        currentposition=player.getCurrentPosition();
                        seekBar.setProgress(currentposition);

                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }


                }
                //super.run();
            }
        };
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
            }
        });
        UpdateSeekbar.start();
        seekBar.setMax(player.getDuration());
        max_dur=player.getDuration();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(max_dur);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(max_dur);
        long s=seconds-(minutes*60);
        max_position.setText(String.valueOf(minutes)+":"+String.valueOf(s));

        cut_dur=player.getCurrentPosition();
        current_postion.setText(String.valueOf(cut_dur));
        player.start();
    }


    public void songs(View view) {

        switch (view.getId()){
            case R.id.play:

                if(play_flag){
                    player.pause();
                    play_flag=false;
                    play_image.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp);
                }else {
                    //setSongsdata(position);
                    player.start();
                    play_flag=true;
                    play_image.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                }
                break;
            case R.id.next:
                if(position<songsdata.size()){
                player.stop();
                //position++;
                    /*if(preferences.getBoolean("shuffle",false)) {
                        //position--;
                        int randomNum = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            randomNum = ThreadLocalRandom.current().nextInt(0, songsdata.size());
                        }
                        position = randomNum;
                    }else{
                        position++;
                    }*/
                    position++;
                setSongsdata(position);
                play_image.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
            }
                break;
            case R.id.previous:
                if(position>0) {
                    player.stop();
                    /*if(preferences.getBoolean("shuffle",false)) {
                        //position--;
                        int randomNum = 0;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            randomNum = ThreadLocalRandom.current().nextInt(0, songsdata.size());
                        }
                        position = randomNum;
                    }else{
                        position--;
                    }*/
                    position--;
                    setSongsdata(position);
                    play_image.setImageResource(R.drawable.ic_play_circle_outline_black_24dp);
                }
                break;
            case R.id.shuffle:
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("shuffle",true);
                editor.apply();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.pause();
    }
}
