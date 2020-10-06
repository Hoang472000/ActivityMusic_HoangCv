package com.out.activitymusic;

import android.content.ContentUris;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Service.ServiceMediaPlay;

import static android.content.Context.MODE_PRIVATE;


public class AllSongsFragment extends BaseSongListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String SHARED_PREFERENCES_NAME = "1";
    private ListAdapter mListAdapter;
    private SharedPreferences mSharePreferences;
    ArrayList<Song> songs;
    ServiceMediaPlay serviceMediaPlay;
    DataFragment dataFragment;
    DisplayMediaFragment displayMediaFragment;
    MediaPlaybackFragment mediaPlaybackFragment;
    private Boolean IsBoolean = false;

    public void setBoolean(Boolean aBoolean) {
        IsBoolean = aBoolean;
    }

    public Boolean getIsBoolean() {
        return IsBoolean;
    }

    public void setService(ServiceMediaPlay service) {
        this.serviceMediaPlay = service;
    }

    public AllSongsFragment(DataFragment dataFragment,DisplayMediaFragment displayMediaFragment,MediaPlaybackFragment mediaPlaybackFragment) {
        super(displayMediaFragment, mediaPlaybackFragment);
        this.dataFragment = dataFragment;
        this.displayMediaFragment=displayMediaFragment;
    }

    public AllSongsFragment() {
    }


    UpdateUI UpdateUI;
    int index;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("nhungltk12", "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LoaderManager.getInstance(this).initLoader(1, null, this);
      //  clickLinearLayout(displayMediaFragment);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d("nhungltk12", "onCreateLoader: ");
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("nhungltk12", "onCreate: ");
        //dataFragment.onclickData(songs);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d("nhungltk12", "onLoadFinished: ");
        mSharePreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        songs = new ArrayList<>();
        boolean isCreate = mSharePreferences.getBoolean("create_db", false);
        int id = 0;
        String title = "";
        String file = "";
        String album = "";
        String artist = "";
        String duration = "";
        Song song = new Song(id, title, file, album, artist, duration);
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            do {
                id++;
                song.setID(id);
                song.setTitle(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                song.setFile(data.getString(data.getColumnIndex(MediaStore.Audio.Media.DATA)));
                song.setAlbum(data.getString(data.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                song.setArtist(data.getString(data.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                song.setDuration(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                title = song.getTitle();
                file = song.getFile();
                album = song.getAlbum();
                artist = song.getArtist();
                duration = song.getDuration();
                songs.add(new Song(id, title, file, album, artist, duration));
                //    Log.d("nhungltk12", "onLoadFinished: " + title);


               /* if (isCreate == false) {
                    ContentValues values = new ContentValues();
                    values.put(FavoriteSongsProvider.ID_PROVIDER, id);
                    values.put(FavoriteSongsProvider.FAVORITE, 0);
                    values.put(FavoriteSongsProvider.COUNT, 0);
                    Uri uri = getActivity().getContentResolver().insert(Uri.parse(FavoriteSongsProvider.CONTENT_URI), values);
                    mSharePreferences = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSharePreferences.edit();
                    editor.putBoolean("create_db", true);
                    editor.commit();
                }*/

            } while (data.moveToNext());
        }
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            setListSongs(songs);
        else   setListSongs(songs);
        dataFragment.onclickData(songs);
        mListAdapter=new ListAdapter(getContext(),songs,this);
        mListAdapter.setmListSong(songs);
        setAdapter(mListAdapter);
        setListAdapter(mListAdapter);
        setService(serviceMediaPlay);

        Log.d("HoangCV4444", "onLoadFinished:+ songs " + songs);


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

   }




