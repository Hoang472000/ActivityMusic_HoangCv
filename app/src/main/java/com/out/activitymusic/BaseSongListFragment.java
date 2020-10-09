package com.out.activitymusic;

import android.content.ContentUris;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
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

import Service.MediaPlaybackService;


public class BaseSongListFragment extends Fragment implements ItemClickListener, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{
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
    MediaPlaybackService mediaPlaybackService;
    MediaPlaybackFragment mediaPlaybackFragment;
    private boolean Ischeck = false;

    private DisplayMediaFragment displayMediaFragment;
    private ImageView mPlayPause;
    private Boolean IsBoolean = false;
    private ImageView mMusicPop;


    public void setListAdapter(ListAdapter adapter) {
        this.mListAdapter = adapter;
    }

    public void setListSongs(ArrayList<Song> listSongs) {

        this.songs = listSongs;
        Log.d("HoangCVf1f", "setListSongs: " + songs);
    }

    public void setBoolean(Boolean aBoolean) {
        IsBoolean = aBoolean;
    }

    public Boolean getIsBoolean() {
        return IsBoolean;
    }

    public void setService(MediaPlaybackService service) {
        this.mediaPlaybackService = service;
    }


    public BaseSongListFragment(DisplayMediaFragment displayMediaFragment, MediaPlaybackFragment mediaPlaybackFragment) {
        this.displayMediaFragment = displayMediaFragment;
        this.mediaPlaybackFragment = mediaPlaybackFragment;

    }

    public BaseSongListFragment() {

    }

    UpdateUI UpdateUI;
    int index;

    public void setAdapter(ListAdapter adapter) {
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

        //Bkav Nhungltk: doan nay nghia la sao
        ((MainActivity) getActivity()).setiConnectActivityAndBaseSong(new MainActivity.IConnectActivityAndBaseSong() {
            @Override
            public void connectActivityAndBaseSong() {
                if (((MainActivity) getActivity()).mediaPlaybackService != null) {
                    Log.d("nhungltkk", "onCreateView: " + "not null");
                    mediaPlaybackService = ((MainActivity) getActivity()).mediaPlaybackService;
         //           mListAdapter.setService(serviceMediaPlay);
                     mediaPlaybackFragment.setService(mediaPlaybackService);
                    Log.d("nhungltkk", "connectActivityAndBaseSong: " + mediaPlaybackService);
                }
            }
        });

        UpdateUI = new UpdateUI(getContext());
        index = UpdateUI.getIndex();
        Log.d("HoangCV333", "onCreateView: index=" + index);
        title.setText(UpdateUI.getTitle());
        artist.setText(UpdateUI.getArtist());
        img.setImageURI(Uri.parse(UpdateUI.getAlbum()));
        Log.d("HoangCV7f", "onCreateView: "+song);
        Log.d("HoangffwCV", "onCreateView: "+ mediaPlaybackService);
        if(isPortraint()){
            mLinearLayout.setVisibility(View.GONE);
        //    mediaPlaybackFragment.setService(mediaPlaybackService);
//            mediaPlaybackService.setListSong(songs);
            mediaPlaybackFragment.updateTime();
//            mediaPlaybackFragment.getText(songs.get(index));
        }
        else{
            mLinearLayout.setVisibility(View.VISIBLE);
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlaybackService.setListSong(songs);
                    mediaPlaybackFragment.setService(mediaPlaybackService);
                    displayMediaFragment.onclick(song);
                    mediaPlaybackFragment.updateUI();
                }
            });
        }
        Log.d("Hoang123gCV", "onCreateView: " + songs);

        onClickPause();
  //      clickLinearLayout();

        if (mediaPlaybackService !=null) {
            if (mediaPlaybackService.isPlaying())
                mPlayPause.setImageResource(R.drawable.ic_pause_black_large);
            else
                mPlayPause.setImageResource(R.drawable.ic_media_play_light);
        }
       // else   mPlayPause.setImageResource(R.drawable.ic_media_play_light);
        return mInflater;
    }
/*
    private void clickLinearLayout() {
        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMediaFragment.onclick(song);
            }
        });

    }*/

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

        Log.d("HoangCVfd", "onClick: " + songs);
        Log.d("HoangCVfd", "onClick: 123"+ mediaPlaybackService);
        if (isPortraint()){
            mediaPlaybackFragment.setService(mediaPlaybackService);
            mediaPlaybackFragment.setListSong(songs);
            mediaPlaybackFragment.getText(song);
            mediaPlaybackFragment.updateTime();
        }
        if (mediaPlaybackService.isPlaying()) {
            mediaPlaybackService.pauseMedia();
            try {
                mediaPlaybackService.playMedia(song);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mediaPlaybackService.playMedia(song);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        img.setImageURI(queryAlbumUri(song.getAlbum()));
        mediaPlaybackFragment.updateTime();
        Ischeck = true;
        updateUI();
    }
public boolean isPortraint(){
    int orientation = this.getResources().getConfiguration().orientation;
    if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        return true;
    else return false;
}

    public Uri queryAlbumUri(String imgUri) {

        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, Long.parseLong(imgUri));//noi them mSrcImageSong vao artworkUri
    }

    public void onClickPause() {

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlaybackService != null) {
                    Log.d("HoangCV2", "onClick: " + mediaPlaybackService.isPlaying());
                }
                if (mediaPlaybackService.isPlaying()) {
                    mediaPlaybackService.pauseMedia();
                    mPlayPause.setImageResource(R.drawable.ic_media_play_light);
                } else {
                    if (!Ischeck) {
                        try {
                            mediaPlaybackService.playMedia(songs.get(index));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            mediaPlaybackService.playMedia(song);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mPlayPause.setImageResource(R.drawable.ic_pause_black_large);
                    Log.d("HoangCV2", "onClick: " + mediaPlaybackService.isPlaying());

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HoangCV10", "onResume: " + mediaPlaybackService);
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



    public void LinearSmall(final ArrayList<Song> arrayList) {
        if(!Ischeck)
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayMediaFragment.onclick(arrayList.get(index));
                }
            });
    }
    public void updateUI(){

        if(mediaPlaybackService.getmMediaPlayer()!=null){
            img.setImageURI(Uri.parse(UpdateUI.getAlbum()));
            title.setText(mediaPlaybackService.getNameSong());
            artist.setText(mediaPlaybackService.getArtist());
            if(mediaPlaybackService.isPlaying()){
                mPlayPause.setImageResource(R.drawable.ic_pause_black_large);
            }else
                mPlayPause.setImageResource(R.drawable.ic_media_play_light);
            mListAdapter.notifyDataSetChanged();

        }
    }
}











