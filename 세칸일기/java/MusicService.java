package com.example.mydiary_ver6;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    IBinder binder = new MusicServiceBinder();
    public class MusicServiceBinder extends Binder {
        public com.example.mydiary_ver6.MusicService getService() {
            return com.example.mydiary_ver6.MusicService.this;
        }
    }
    private MediaPlayer mp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mp=MediaPlayer.create(this, R.raw.ily);
        mp.setLooping(true);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "STOP", Toast.LENGTH_SHORT).show();
        mp.stop();
        super.onDestroy();
    }

    public void play(){
        Toast.makeText(getApplicationContext(), "NOW PLAYING - Surf Mesa - ily (feat. Emilee)" , Toast.LENGTH_SHORT).show();
        mp.start();
    }

    public void pause(){
        Toast.makeText(getApplicationContext(), "PAUSE", Toast.LENGTH_SHORT).show();
        mp.pause();
    }
}
