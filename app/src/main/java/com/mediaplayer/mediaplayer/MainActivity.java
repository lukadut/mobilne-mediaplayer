package com.mediaplayer.mediaplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ImageView rewindLeft, playPause, rewindRight;
    private ListView listView;

    private MyService myService;
    private Intent intent;

    ArrayList<Song> songs;
    private MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPause = (ImageView) findViewById(R.id.buttonPlayPause);
        playPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (myService.getPlayerState() == MyService.State.PLAY)
                {
                    if (myService != null)
                    {
                        playPause.setImageResource(R.drawable.pause);
                        myService.playerPause();
                    }
                    else
                    {
                        System.out.println("Brak referencji.");
                    }
                }
                else
                {
                    playPause.setImageResource(R.drawable.play);
                    myService.playerPlay();
                }
            }
        });

        rewindLeft = (ImageView) findViewById(R.id.buttonRewindLeft);
        rewindLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: działanie przycisku
            }
        });

        rewindRight = (ImageView) findViewById(R.id.buttonRewindRight);
        rewindRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: działanie przycisku
            }
        });

        ArrayList<String> listItems=new ArrayList<String>();



        ArrayAdapter<String> adapter;
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        listView=(ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                aonItemClick(parent, view, position, id);
            }
        });
        getSongList();
        int songIndex=1;
        for (Song song: songs){
            adapter.add(songIndex + ". " + song.getArtist() + " - " + song.getTitle());
            songIndex++;
        }
    }


    protected void aonItemClick(AdapterView<?> list, View view, int position, long id){
        Log.d("position", position + "");
        Log.d("id", id + "");
        //serwis.play(songs.get(position).getPath());

        try {

            mp.reset();
            mp.setDataSource(songs.get(position).getPath());
            mp.prepare();
            mp.start();

        } catch(Exception e){
            Log.v(getString(R.string.app_name), e.getMessage());
        }
        Log.d("po E", songs.get(position).getPath());
    }



    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        songs = new ArrayList<>();

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            String[] columns = musicCursor.getColumnNames();
            /*
            for(String s:columns){
                Log.d("kolumna",s);
                try {
                    int ind = musicCursor.getColumnIndex(s);
                    String o = musicCursor.getString(ind);
                    Log.d("debug "+ ind , "w kolumnie " + s + " jest wartosc " + (o == null ? "null" : o));
                }
                catch (Exception e){

                }
            }*/
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);

            //add songs to list

            do {
                long thisId = musicCursor.getLong(idColumn);

                songs.add(new Song( thisId, columns, musicCursor));
            }
            while (musicCursor.moveToNext());
        }
        // sciezka do pliku do _data, index 1
    }

}
