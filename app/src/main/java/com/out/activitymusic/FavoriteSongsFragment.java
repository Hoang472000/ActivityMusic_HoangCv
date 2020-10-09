package com.out.activitymusic;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.ArrayList;

import Service.MediaPlaybackService;

public class FavoriteSongsFragment extends BaseSongListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID=1;
    private ArrayList<Song> mListAllSong;

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
        Log.d("HoangCVfg", "onCreateLoader: ");
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        CursorLoader cursorLoader = new CursorLoader(getContext(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

}