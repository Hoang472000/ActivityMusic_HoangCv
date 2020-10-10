package com.out.activitymusic;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.out.activitymusic.database.FavoriteSongsProvider;
import java.util.ArrayList;
import Service.MediaPlaybackService;

public class FavoriteSongsFragment extends BaseSongListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID=1;
    private ArrayList<Song> mListAllSong;
    private ListAdapter mListAdapter;

    public FavoriteSongsFragment(ArrayList<Song> mListAllSong, MediaPlaybackService service){
        this.mListAllSong=new ArrayList<>();
        this.mListAllSong=mListAllSong;
        this.mediaPlaybackService=service;
    }

    public FavoriteSongsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID,null,this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String URL = "content://com.bkav.provider";
        Uri uriSongs = Uri.parse(URL);
        String selection= FavoriteSongsProvider.FAVORITE+"==2";
        return new CursorLoader(getContext(),uriSongs, null, selection, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ArrayList<Song> mListFavoriteSongs = new ArrayList<>();
        Song song =null;
        int dem=0;
        if (data.moveToFirst()) {
            do {

                for(int i=0;i<mListAllSong.size();i++){
                    //   Log.d("SONG size ","//"+mListAllSong.size());
                    if(mListAllSong.get(i).getID()== data.getInt(data.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))){
                        Log.d("song F", data.getInt(data.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))+"//"+mListAllSong.get(i).getID());
                        song = new Song( dem, mListAllSong.get(i).getTitle(), mListAllSong.get(i).getFile(),mListAllSong.get(i).getAlbum(), mListAllSong.get(i).getArtist(), mListAllSong.get(i).getDuration());
                        dem++;
                        mListFavoriteSongs.add(song);
                        mListAdapter=new ListAdapter(getContext(),songs,this);
                        Log.d("favorite", "onLoadFinished: "+mListFavoriteSongs.size());
                        setListSongs(mListFavoriteSongs);
                    }
                }
            } while (data.moveToNext());

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

}