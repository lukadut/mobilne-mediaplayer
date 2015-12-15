package com.mediaplayer.mediaplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mediaplayer.mediaplayer.Exceptions.NegativeIndexException;
import com.mediaplayer.mediaplayer.Exceptions.OverflowIndexException;

import java.lang.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ServiceConnection
{
    private ImageView nextSong, playPause, previousSong;
    private ListView listView;

    private MediaPlayerService mediaPlayerService;
    private Intent intent;

    static ArrayList<Song> songs;
    //private MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent=new Intent(getApplicationContext(),MediaPlayerService.class);
        startService(intent);

        //buttony
        playPause = (ImageView) findViewById(R.id.buttonPlayPause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayerService.isPlaying()) {
                    playPause.setImageResource(R.drawable.play);
                    mediaPlayerService.pause();
                    Log.d("serwis", "is playing serwis");
                }
                else {
                    if(mediaPlayerService.getSongIndex()>=0) {
                        playPause.setImageResource(R.drawable.pause);
                        mediaPlayerService.play();
                    }
                    else{
                        mediaPlayerService.nextSong();
                    }
                    Log.d("serwis", "is not playing serwis");
                }
            }
        });

        nextSong = (ImageView) findViewById(R.id.buttonNext);
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerService.nextSong();
                Log.d("button","nastepna piosenka");
            }
        });

        previousSong = (ImageView) findViewById(R.id.buttonPrevious);
        previousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayerService.previousSong();
                Log.d("button","poprzednia piosenka");
            }
        });
    }

    public static Song getSong(int index) throws OverflowIndexException, NullPointerException, NegativeIndexException {
        if(songs.size()==0){
            throw new NullPointerException();
        }
        if(index >= songs.size()){
            throw new OverflowIndexException();
        }
        if(index < 0){
            throw new NegativeIndexException();
        }
        return songs.get(index);
    }
    public static int getSongsSize(){
        return songs.size();
    }

    protected void addSongsToList(){
        ArrayList<String> listItems = new ArrayList<String>();


        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listItems);
        listView = (ListView) findViewById(R.id.listView);
        adapter.clear();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String path = songs.get(position).getPath();
                mediaPlayerService.setSongIndex(position);
                mediaPlayerService.play(path);
                playPause.setImageResource(R.drawable.pause);
            }
        });
        getSongList();
        int songIndex = 1;
        for (Song song : songs)
        {
            adapter.add(songIndex + ". " + song.getArtist() + " - " + song.getTitle());
            songIndex++;
        }
    }

    protected void getSongList()
    {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        songs = new ArrayList<>();

        if (musicCursor != null && musicCursor.moveToFirst())
        {
            String[] columns = musicCursor.getColumnNames();

            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);

            do
            {
                long thisId = musicCursor.getLong(idColumn);

                songs.add(new Song(thisId, columns, musicCursor));
            }
            while (musicCursor.moveToNext());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        System.out.println("Wykonano resume");
        addSongsToList();


        Intent bindIntent = new Intent(this, MediaPlayerService.class);
        bindService(bindIntent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        System.out.println("Wykonano onPause");
        if (mediaPlayerService != null)
        {
            unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder)
    {
        mediaPlayerService = ((MediaPlayerService.LocalBinder) iBinder).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        mediaPlayerService = null;
    }
}
