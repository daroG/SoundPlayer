package com.example.soundplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    MediaPlayer mediaPlayer;
    List<Sound> sounds = new ArrayList<>();
    List<Integer> spans = new ArrayList<>();
    long playedLastTime = 0;

    final List<ButtonSound> set = new ArrayList<>();
    Button lastPlayedBtn = null;

    final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        set.add(new ButtonSound(findViewById(R.id.play_c), Sounds.c.getSoud()));
        set.add(new ButtonSound(findViewById(R.id.play_d), Sounds.d.getSoud()));
        set.add(new ButtonSound(findViewById(R.id.play_e), Sounds.e.getSoud()));
        set.add(new ButtonSound(findViewById(R.id.play_f), Sounds.f.getSoud()));
        set.add(new ButtonSound(findViewById(R.id.play_g), Sounds.g.getSoud()));
        set.add(new ButtonSound(findViewById(R.id.play_a), Sounds.a.getSoud()));
        set.add(new ButtonSound(findViewById(R.id.play_h), Sounds.h.getSoud()));
        set.add(new ButtonSound(findViewById(R.id.play_c2), Sounds.c2.getSoud()));



        float den = this.getBaseContext().getResources().getDisplayMetrics().density;
        float width = findViewById(R.id.layout).getMeasuredWidth();

        for (int i = 0; i < set.size(); i++){
            final ButtonSound pair = set.get(i);
            pair.getButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(playedLastTime > 0){
                        long now = System.currentTimeMillis();
                        int span = (int)(now - playedLastTime);
                        spans.add(span);
                        playedLastTime = now;
                        if(spans.get(spans.size() - 1) > 5000){
                            clearSequence();
                        }
                    }else{
                        playedLastTime = System.currentTimeMillis();
                    }

                    playSound(pair.getSound());
                    sounds.add(pair.getSound());

//                    lastPlayedBtn = (Button) v;
                    updateActualSequence();
                }
            });

            pair.getButton().setWidth(Math.round(den * width / set.size()));

        }

        findViewById(R.id.play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSequence();
            }
        });

        findViewById(R.id.load_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSequence();
            }
        });

        findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSequence();
            }
        });

        findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSequence();
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()){
//            case R.id.action_save:
//                saveSequence();
//                break;
//            case R.id.action_open:
//                openSequence();
//                break;
//            case R.id.action_clear:
//                clearSequence();
//                break;
//            case R.id.action_play:
//                playSequence();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void saveSequence(){
        try{
            FileOutputStream fileOutputStream = openFileOutput("datafile.txt", MODE_PRIVATE);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutputStream);

            for (int i = 0; i < sounds.size(); i++){
                if(spans.size() == i){
                    streamWriter.write(sounds.get(i).getName() + ",");
                }else {
                    streamWriter.write(sounds.get(i).getName() + ":" + spans.get(i)+ ",");
                }
            }

            streamWriter.close();

            Toast.makeText(getBaseContext(), "Seqence saved", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(getBaseContext(), "Unable to save", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void openSequence(){
        try{
            FileInputStream fileOutputStream = openFileInput("datafile.txt");
            InputStreamReader streamReader = new InputStreamReader(fileOutputStream);

            char[] inputBuffer = new char[100];
            StringBuilder s = new StringBuilder();
            int charRead;
            while ( (charRead = streamReader.read(inputBuffer)) > 0 ){
                s.append(String.copyValueOf(inputBuffer, 0, charRead));
            }
            streamReader.close();


            ((TextView)findViewById(R.id.sequence)).setText(s.toString());

            loadSequence(s.toString());

        }catch (Exception e){
            Toast.makeText(getBaseContext(), "Unable to read file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void loadSequence(String seq){
        clearSequence();
        String[] parts = seq.split(",");
        String[] part;
        for (int i = 0;i < parts.length;i++){
            part = parts[i].split(":");

            sounds.add(Sounds.get(part[0].toLowerCase()));

            if(part.length > 1){
                spans.add(Integer.valueOf(part[1]));
            }
        }

        for (int i = 0;i<sounds.size();i++){

            Log.i(TAG, sounds.get(i).getName());
            if(i < spans.size()){
                Log.i(TAG, String.valueOf(spans.get(i)));
            }

        }
        Toast.makeText(getBaseContext(), "Sequence loaded", Toast.LENGTH_LONG).show();
    }

    private void clearSequence(){
        sounds.clear();
        spans.clear();

        playedLastTime = 0;

        updateActualSequence();
    }

    private void playSequence(){
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        int sum = 0;
        for (int i = 0; i < sounds.size(); i++){
            final Sound sound = sounds.get(i);

            if (i > 0){
                sum += spans.get(i - 1);
            }

            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    playSound(sound);
                }
            }, sum, TimeUnit.MILLISECONDS);

        }
    }

    public void playSound(Sound sound){
        if(mediaPlayer!= null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(MainActivity.this, sound.getRedId());
        mediaPlayer.start();


        Button btn = null;
        for (int i = 0; i<set.size();i++){
            if(sound.getName() == set.get(i).getName()){
                btn = findViewById(set.get(i).getButton().getId());
                break;
            }
        }


        Log.i(TAG, "btn: " + String.valueOf(btn == null) + " last: " + String.valueOf(lastPlayedBtn == null));

        if(btn != null){
            if(lastPlayedBtn != null){
                findViewById(lastPlayedBtn.getId()).setBackgroundColor(Color.parseColor("#00ff00"));
            }
            Log.i(TAG, "Po ifie");
            lastPlayedBtn = btn;
            findViewById(btn.getId()).setBackgroundColor(Color.parseColor("#ff0000"));
        }


    }

    private void updateActualSequence(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sounds.size(); i++){
            if(spans.size() == i){
                builder.append(sounds.get(i).getName() + ",");
            }else {
                builder.append(sounds.get(i).getName() + ":" + spans.get(i)+ ",");
            }
        }
        TextView as = findViewById(R.id.actual_sequence);
        as.setText(builder.toString());
    }
}
