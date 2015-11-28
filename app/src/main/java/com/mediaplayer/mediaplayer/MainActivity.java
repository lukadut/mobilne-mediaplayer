package com.mediaplayer.mediaplayer;

import android.content.ContentResolver;
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

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ImageView rewindLeft, playPause, rewindRight;
    private ListView listView;
    ArrayList<Song> songList;
    private MediaPlayer mp = new MediaPlayer();

    // Stany przycisku buttonPlayPause
    public enum PlayPauseStates
    {
        PLAY, PAUSE
    }

    // Ustawiony stan przycisku buttonPlayPause
    private PlayPauseStates playPauseState;

    public void setPlayPauseState(PlayPauseStates state)
    {
        playPauseState = state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPlayPauseState(PlayPauseStates.PLAY);

        playPause = (ImageView) findViewById(R.id.buttonPlayPause);
        playPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (playPauseState == PlayPauseStates.PLAY)
                {
                    playPause.setImageResource(R.drawable.pause);
                    setPlayPauseState(PlayPauseStates.PAUSE);
                    // TODO: działanie przycisku
                }
                else
                {
                    playPause.setImageResource(R.drawable.play);
                    setPlayPauseState(PlayPauseStates.PLAY);
                    // TODO: działanie przycisku
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
                onListItemClick( parent,  view,  position,  id);
                Log.d("position", position + "");
                Log.d("id", id+"");
                Log.d("click", songList.get(position).attributes.size()+"");
                Log.d("click", songList.get(position).attributes.get("_data")+"");
            }
        });
        getSongList();
        for (Song song:songList){
            adapter.add(song.id + ". " + song.getArtist() + " - " + song.getTitle());
        }
    }


    protected void onListItemClick(AdapterView<?> list, View view, int position, long id){
        Log.d("position", position+"");
        Log.d("id", id+"");
       /*
        try {

            mp.reset();
            mp.setDataSource(SD_PATH + songs.get(position));
            mp.prepare();
            mp.start();

        } catch(IOException e){
            Log.v(getString(R.string.app_name), e.getMessage());
        }
        */
    }



    public void getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        songList = new ArrayList<>();

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            String[] columns = musicCursor.getColumnNames();
            for(String s:columns){
                Log.d("kolumna",s);
                try {
                    int ind = musicCursor.getColumnIndex(s);
                    String o = musicCursor.getString(ind);
                    Log.d("debug "+ ind , "w kolumnie " + s + " jest wartosc " + (o == null ? "null" : o));
                }
                catch (Exception e){

                }
            }
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            int i=1;

            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                Log.d("value", musicCursor.getString(1));

                songList.add(new Song(i, thisId, thisTitle, thisArtist,columns, musicCursor));
                i++;
            }
            while (musicCursor.moveToNext());
        }
        // sciezka do pliku do _data, index 1
    }

}
