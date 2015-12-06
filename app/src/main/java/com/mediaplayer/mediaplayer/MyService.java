package com.mediaplayer.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class MyService extends Service
{
    public enum State
    {
        PLAY, PAUSE, STOP
    }

    public State getPlayerState()
    {
        return playerState;
    }

    private State playerState = State.STOP;

    public static void set_path(String _path)
    {
        MyService._path = _path;
    }

    private static String _path;

    private MediaPlayer player = null;

    private LocalBinder localBinder = new LocalBinder();

    public MyService()
    {
    }

    public void playerPlay()
    {
        player.start();
        playerState = State.PLAY;
    }

    public void playerPause()
    {
        player.pause();
        playerState = State.PAUSE;
    }

    public class LocalBinder extends Binder
    {
        public MyService getService()
        {
            return MyService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        player = new MediaPlayer();
        try
        {
            player.setDataSource(_path);
            player.prepare();
            player.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return localBinder;
    }

    @Override
    public void onDestroy()
    {
        player.stop();
        player.release();
        super.onDestroy();
    }
}
