package com.out.activitymusic;

import android.content.res.Configuration;
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
import com.out.activitymusic.interfaces.DisplayMediaFragment;

import java.util.ArrayList;
import Service.MediaPlaybackService;

public class FavoriteSongsFragment extends BaseSongListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID=1;
    private ArrayList<Song> mListAllSong;
    private ListAdapter mListAdapter;
    MediaPlaybackFragment mediaPlaybackFragment;
    DisplayMediaFragment displayMediaFragment;

    public FavoriteSongsFragment(ArrayList<Song> mListAllSong, MediaPlaybackService service, MediaPlaybackFragment mediaPlaybackFragment, DisplayMediaFragment displayMediaFragment){
        super(displayMediaFragment,mediaPlaybackFragment);
        this.mListAllSong=new ArrayList<>();
        this.mListAllSong=mListAllSong;
        this.mediaPlaybackService=service;
        this.mediaPlaybackFragment=mediaPlaybackFragment;
    }

    public FavoriteSongsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LoaderManager.getInstance(this).initLoader(LOADER_ID,null,this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String URL = "content://com.out.activitymusic.database.FavoriteSongsProvider";
        Uri uriSongs = Uri.parse(URL);
        String selection= FavoriteSongsProvider.IS_FAVORITE +"==2";
        return new CursorLoader(getContext(),uriSongs, null, selection, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ArrayList<Song> mListFavoriteSongs = new ArrayList<>();

        Song song = null;
        int dem = 0;
        if (data.moveToFirst()) {
            do {
                for (int i = 0; i < mListAllSong.size(); i++) {
                    if (mListAllSong.get(i).getID() == data.getInt(data.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER))) {
                        Log.d("song F", data.getInt(data.getColumnIndex(FavoriteSongsProvider.ID_PROVIDER)) + "//" + mListAllSong.get(i).getID());
                        song = new Song(dem, mListAllSong.get(i).getTitle(), mListAllSong.get(i).getFile(), mListAllSong.get(i).getAlbum(), mListAllSong.get(i).getArtist(), mListAllSong.get(i).getDuration());
                        dem++;
                        mListFavoriteSongs.add(song);
                        mListAdapter = new ListAdapter(getContext(), mListFavoriteSongs, this);
                        Log.d("favorite", "onLoadFinished: " + mListFavoriteSongs.size());
                        setListSongs(mListFavoriteSongs);
                        setAdapter(mListAdapter);
                    }
                }
            } while (data.moveToNext());
        }
        setListSongs(mListFavoriteSongs);
        LinearSmall(mListFavoriteSongs);
        Log.d("HoangCVgasfsdf", "onLoadFinished: " + mediaPlaybackService);
        mediaPlaybackFragment.setListSong(mListFavoriteSongs);
        // dataFragment.onclickData(songs);
        mediaPlaybackFragment.setService(mediaPlaybackService);
        mListAdapter = new ListAdapter(getContext(), mListFavoriteSongs, this);
        setAdapter(mListAdapter);
        setListAdapter(mListAdapter);
        mListAdapter.setService(mediaPlaybackService);
        if (isLandscape()) {
            Log.d("H111oangCV", "onLoadFinished111: " + mediaPlaybackService);
            setListSongs(mListFavoriteSongs);
            // mediaPlaybackService.setmListSong(songs);
            mListAdapter.setService(mediaPlaybackService);
            mediaPlaybackFragment.setService(mediaPlaybackService);
            mediaPlaybackFragment.setListSong(mListFavoriteSongs);
            mediaPlaybackFragment.updateTime();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
    public boolean isLandscape() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else return false;
    }

}