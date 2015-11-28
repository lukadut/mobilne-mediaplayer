package com.mediaplayer.mediaplayer;


import android.database.Cursor;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Objects;

/**
 * Created by Łukasz on 2015-11-28.
 */
public class Song {

        private long songID;
        public long id;
        private String title;
        private String artist;
        public Dictionary attributes;
        public Song(long id,long songID, String songTitle, String songArtist,String[] columns, Cursor cursor) {
            attributes = new Hashtable<String,Object>();
            int columnsLength = columns.length;
            for(int i=0;i<columnsLength;i++){
                Object attribute = cursor.getString(i);
                attributes.put(columns[i],(attribute==null? "<uknown>": (String)attribute));
            }
            this.songID = songID;
            this.id=id;
            title=songTitle;
            artist=songArtist;
        }
        public long getID(){return id;}
        public String getTitle(){return title;}
        public String getArtist(){return artist;}

}
