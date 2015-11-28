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

        private Dictionary<String,String> attributes;
        public Song(long songID,String[] columns, Cursor cursor) {
            attributes = new Hashtable<String,String>();
            int columnsLength = columns.length;
            for(int i=0;i<columnsLength;i++){
                Object attribute = cursor.getString(i);
                attributes.put(columns[i],(attribute==null? "<uknown>": (String)attribute));
            }
            this.songID = songID;
        }
        public long getSongId(){return songID;}
        public String get(String attribute){
            return attributes.get(attribute);
        }
        public String getPath(){return get("_data");}
        public String getTitle(){return get("title");}
        public String getArtist(){return get("artist");}

}
