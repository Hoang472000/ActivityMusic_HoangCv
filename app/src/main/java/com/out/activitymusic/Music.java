package com.out.activitymusic;

import android.util.Log;

import java.util.ArrayList;
public class Music {
    public static ArrayList<Song> setListMusic(ArrayList<Song> arrayList, int n){
        
       /* arrayList.add(new Song(1,"music1","src1","picture1",1));
        arrayList.add(new Song(2,"music1","src1","picture1",2));
        arrayList.add(new Song(3,"music1","src1","picture1",3));
        arrayList.add(new Song(1,"music1","src1","picture1",3));
        arrayList.add(new Song(1,"music1","src1","picture1",2));
        arrayList.add(new Song(1,"music1","src1","picture1",3));
        */
        for (int i=0;i<n;i++){
            Song song = (Song) arrayList.get(i);
            arrayList.add(song);

        }
        return arrayList;
    }

}
