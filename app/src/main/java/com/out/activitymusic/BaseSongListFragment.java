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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import Service.ServiceMediaPlay;



public class BaseSongListFragment extends Fragment implements  ItemClickListener, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private static final String SHARED_PREFERENCES_NAME = "1";
    private ListAdapter mListAdapter;

    private RecyclerView mRecyclerView;
    private Song song;

    public RelativeLayout mLinearLayout, mBottom;
    TextView title, mTitle, mTime;
    TextView artist;
    ImageView img, mImageSmall;
    private SharedPreferences mSharePreferences;
    ArrayList<Song> songs;
    ServiceMediaPlay serviceMediaPlay;
    MediaPlaybackFragment mediaPlaybackFragment;
    private boolean Ischeck = false;

    private DisplayMediaFragment displayMediaFragment;
    private ImageView mPlayPause;
    private Boolean IsBoolean = false;
    private ImageView mMusicPop;



    public void setListAdapter(ListAdapter adapter){
        this.mListAdapter=adapter;
    }
    public void setListSongs(ArrayList<Song> listSongs){

        this.songs=listSongs;
        Log.d("HoangCVff", "setListSongs: "+songs);
    }
    public void setBoolean(Boolean aBoolean) {
        IsBoolean = aBoolean;
    }

    public Boolean getIsBoolean() {
        return IsBoolean;
    }

    public void setService(ServiceMediaPlay service) {
        this.serviceMediaPlay = service;
    }


    public BaseSongListFragment(DisplayMediaFragment displayMediaFragment, MediaPlaybackFragment mediaPlaybackFragment) {
        this.displayMediaFragment = displayMediaFragment;
        this.mediaPlaybackFragment = mediaPlaybackFragment;
    }
    public BaseSongListFragment() {

    }

    UpdateUI UpdateUI;
    int index;

    public void setAdapter(ListAdapter adapter){
        mRecyclerView.setAdapter(adapter);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("nhungltk12", "onCreateView: ");
        View mInflater = inflater.inflate(R.layout.allsongsfragment, container, false);
        mRecyclerView = mInflater.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLinearLayout = mInflater.findViewById(R.id.bottom);
        mPlayPause = mInflater.findViewById(R.id.play_pause);
        title = mInflater.findViewById(R.id.title);
        artist = mInflater.findViewById(R.id.artist);
        img = mInflater.findViewById(R.id.picture);
        mTitle = mInflater.findViewById(R.id.song1);
        mTime = mInflater.findViewById(R.id.Time2);
        mImageSmall = mInflater.findViewById(R.id.picture_small);
        mMusicPop = mInflater.findViewById(R.id.music_pop);
        mRecyclerView.setHasFixedSize(true);


        Log.d("HoangCVff", "onCreateView: "+songs);
        //Bkav Nhungltk: doan nay nghia la sao
        ((MainActivity) getActivity()).setiConnectActivityAndBaseSong(new MainActivity.IConnectActivityAndBaseSong() {
            @Override
            public void connectActivityAndBaseSong() {
                if (((MainActivity) getActivity()).serviceMediaPlay != null) {
                    Log.d("nhungltk", "onCreateView: " + "not null");
                    setService((((MainActivity) getActivity()).serviceMediaPlay));
                    mListAdapter.setService(serviceMediaPlay);

                }
            }
        });

        UpdateUI = new UpdateUI(getContext());
        index = UpdateUI.getIndex();
        Log.d("HoangCV333", "onCreateView: index=" + index);
        title.setText(UpdateUI.getTitle());
        artist.setText(UpdateUI.getArtist());
        img.setImageURI(Uri.parse(UpdateUI.getAlbum()));

        final Song updateSong = new Song(UpdateUI.getIndex(), UpdateUI.getTitle(), UpdateUI.getFile(), UpdateUI.getAlbum(), UpdateUI.getArtist(), String.valueOf(UpdateUI.getDuration()));
        Log.d("Hoang123CV", "onCreateView: " + songs);
      //  if(!Ischeck)

    /*   else
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayMediaFragment.onclick(songs.get(index));
                }
            });*/

        onClickPause();
        clickLinearLayout();

        if(serviceMediaPlay!=null)
            if(serviceMediaPlay.isPlaying())
                mPlayPause.setImageResource(R.drawable.ic_pause_black_large);
            else
                mPlayPause.setImageResource(R.drawable.ic_media_play_light);
        return mInflater;
    }

    private void clickLinearLayout() {
        Log.d("HoangCV", "clickLinearLayout: "+displayMediaFragment);
       mLinearLayout.setOnClickListener(new View.OnClickListener() {
           @Override
          public void onClick(View view) {
               Log.d("HoangCV12ff", "clickLinearLayout: "+song);

                Log.d("HoangCV", "onClick: "+song);
                displayMediaFragment.onclick(song);
        }
      });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("nhungltk12", "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("nhungltk12", "onCreate: ");
        setHasOptionsMenu(true);
    }

    @Override
    public void onClick(Song song) {
        this.song = song;
        Log.d("HoangCVfd", "onClick: "+songs);
        Log.d("HoangCV", "onClick: 123");
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mediaPlaybackFragment.setListSong(songs);
            mediaPlaybackFragment.getText(song);
            mediaPlaybackFragment.updateTime();
        }
        if (serviceMediaPlay != null) {
            Log.d("nhungltk", "onClick: " + "playMusic");
            try {
                serviceMediaPlay.playMedia(song);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        img.setImageURI(queryAlbumUri(song.getAlbum()));
        Log.d("Hoang1111CV", "onClick: " + queryAlbumUri(song.getAlbum()));
        Ischeck = true;
    }


    public Uri queryAlbumUri(String imgUri) {

        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, Long.parseLong(imgUri));//noi them mSrcImageSong vao artworkUri
    }
    public void onClickPause() {

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceMediaPlay != null) {
                    Log.d("HoangCV2", "onClick: " + serviceMediaPlay.isPlaying());
                }
                if (serviceMediaPlay!= null) {
                    serviceMediaPlay.pauseMedia();
                    mPlayPause.setImageResource(R.drawable.ic_media_play_light);
                } else {
                    if(!Ischeck) {
                        try {
                            serviceMediaPlay.playMedia(songs.get(index));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            serviceMediaPlay.playMedia(song);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mPlayPause.setImageResource(R.drawable.ic_pause_black_large);
                    Log.d("HoangCV2", "onClick: " + serviceMediaPlay.isPlaying());

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HoangCV10", "onResume: " + serviceMediaPlay);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        Log.d("HoangCV3", "onCreateOptionsMenu: " + searchItem);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        Log.d("HoangCV3", "onCreateOptionsMenu: " + searchView);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mListAdapter.getFilter().filter(s);
        return false;
    }


}











