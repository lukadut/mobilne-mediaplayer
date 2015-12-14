package com.mediaplayer.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.mediaplayer.mediaplayer.Exceptions.NegativeIndexException;
import com.mediaplayer.mediaplayer.Exceptions.OverflowIndexException;

import java.io.IOException;

public class MediaPlayerService extends Service
{


    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    private String songPath;

    public int getSongIndex() {
        return songIndex;
    }

    public void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }

    public Song getSong(int index) throws OverflowIndexException, NegativeIndexException {

        Song song = null;
        try {
            song = MainActivity.getSong(index);
        }
        catch (OverflowIndexException e) {
            songIndex=0;
            song=MainActivity.getSong(songIndex);

        }
        catch (NegativeIndexException e){
            songIndex=MainActivity.getSongsSize()-1;
            song = MainActivity.getSong(songIndex);
        }
        catch (NullPointerException e){
            Log.d("player","nic nie ma");
            Toast.makeText(getApplicationContext(),"Nie znaleziono utworów", Toast.LENGTH_SHORT);
        }
        return song;
    }

    public Boolean isPlaying(){
        if(player==null){

                Log.d("serwis", "player nie istnieje serwis");
            return false;
        }
        return player.isPlaying();
    }

    private int songIndex = -1;

    private static MediaPlayer player = null;

    private LocalBinder localBinder = new LocalBinder();

    public MediaPlayerService()
    {
        Log.d("serwis","stworzylem serwis");
    }

    public void nextSong() {
        Log.d("serwis","next serwis");
        songIndex++;
        //String path = MainActivity.getSong(songIndex).getPath();
        try {
            String path = getSong(songIndex).getPath();
            play(path);
        } catch (OverflowIndexException e) {
            e.printStackTrace();
        } catch (NegativeIndexException e) {
            e.printStackTrace();
        }
    }

    public void previousSong(){
        Log.d("serwis","previous serwis");
        songIndex--;
        try {
            String path = getSong(songIndex).getPath();
            play(path);
        } catch (OverflowIndexException e) {
            e.printStackTrace();
        } catch (NegativeIndexException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        Log.d("serwis","play serwis");
        player.start();
    }

    public void pause(){
        Log.d("serwis","pause serwis");
        player.pause();
    }

    public void play(String path){
        try {
            player.reset();
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class LocalBinder extends Binder
    {
        public MediaPlayerService getService()
        {
            Log.d("serwis","localbinder getSerwis serwis");
            return MediaPlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("serwis","moze w koncu odpali");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String a = intent.getAction();
        Log.d("czy", "to sie wywoluje kiedykolwiek?");
        if (player==null) {
            player = new MediaPlayer();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextSong();
                }
            });
        }
        return START_STICKY;
    }

    /*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("czy", "to sie wywoluje kiedykolwiek?");

        try
        {
            player.setDataSource(getSongPath());
            player.prepare();
            player.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return START_STICKY;
    }
    */

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d("serwis","onBind serwis");
        return localBinder;
    }

    @Override
    public void onDestroy()
    {
        Log.d("serwis","zniszczylem serwis");
        if(player!=null) {
            player.stop();
            player.release();
        }
        super.onDestroy();
    }

}
