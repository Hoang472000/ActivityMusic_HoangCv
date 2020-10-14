package com.out.activitymusic;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
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

import com.out.activitymusic.database.FavoriteSongsProvider;
import com.out.activitymusic.interfaces.DisplayMediaFragment;
import com.out.activitymusic.interfaces.ItemClickListener;

import java.io.IOException;
import java.util.ArrayList;

import Service.MediaPlaybackService;


public class BaseSongListFragment extends Fragment implements ItemClickListener, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    private static final String SHARED_PREFERENCES_NAME = "1";
    private ListAdapter mListAdapter;
    private String mURL = "content://com.out.activitymusic.database.FavoriteSongsProvider";
    private Uri mURISong = Uri.parse(mURL);
    private RecyclerView mRecyclerView;
    private Song song;
    public RelativeLayout mLinearLayout, mBottom;
    TextView title, mTitle, mTime;
    TextView artist;
    ImageView img, mImageSmall;
    ArrayList<Song> songs;
    MediaPlaybackService mediaPlaybackService;
    MediaPlaybackFragment mediaPlaybackFragment;
    private boolean Ischeck = false;
    private DisplayMediaFragment displayMediaFragment;
    private ImageView mPlayPause;
    private Boolean IsBoolean = false;
    private ImageView mMusicPop;
    UpdateUI UpdateUI;
    int index;
    private View mInflater;

    public void setListAdapter(ListAdapter adapter) {
        this.mListAdapter = adapter;
    }

    public void setListSongs(ArrayList<Song> listSongs) {
        this.songs = listSongs;
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

    public void setAdapter(ListAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("nhungltk12", "onCreateView: ");
        mInflater = inflater.inflate(R.layout.allsongsfragment, container, false);
        mRecyclerView = mInflater.findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLinearLayout = mInflater.findViewById(R.id.bottom);
        mPlayPause = mInflater.findViewById(R.id.play_pause);
        title = mInflater.findViewById(R.id.title);
        artist = mInflater.findViewById(R.id.artist);
        img = mInflater.findViewById(R.id.picture);
        mTitle = mInflater.findViewById(R.id.song1);
        mTime = mInflater.findViewById(R.id.Time2);
        //   mImageSmall = mInflater.findViewById(R.id.picture_small);
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
                }
            }
        });

        UpdateUI = new UpdateUI(getContext());
        index = UpdateUI.getIndex();
        Log.d("HoangCV333", "onCreateView: index=" + index);
        title.setText(UpdateUI.getTitle());
        artist.setText(UpdateUI.getArtist());
        img.setImageURI(Uri.parse(UpdateUI.getAlbum()));
        if (isLandscape()) {
            mLinearLayout.setVisibility(View.GONE);
//            mediaPlaybackFragment.setService(mediaPlaybackService);
         //khi xoay service null
        } else {
            mLinearLayout.setVisibility(View.VISIBLE);
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HoanghdgfdsCasdV", "onClick: "+mediaPlaybackService);
                    mediaPlaybackService.setmListSong(songs);
                    displayMediaFragment.onclickDisplay(song);
                    mediaPlaybackFragment.updateTime();
                }
            });
        }
        onClickPause();

        if (mediaPlaybackService !=null) {
            updateUI();
        }
        return mInflater;
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
        if (isLandscape()) {
            Log.d("H111oangCV", "onClick: "+mediaPlaybackFragment);
            Log.d("H111oangCV", "onClick: "+mediaPlaybackService);
            mediaPlaybackFragment.setService(mediaPlaybackService);
            mediaPlaybackFragment.setListSong(songs);
            mediaPlaybackFragment.getText(song);
            mediaPlaybackService.setMediaPlaybackFragment(mediaPlaybackFragment);
        }
        if (mediaPlaybackService.getPlaying()) {
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
        Log.d("HoangCVhfghd", "onClick: " + queryAlbumUri(song.getAlbum()));
/*try {
            img.setImageURI(queryAlbumUri(song.getAlbum()));}
catch (Exception e){
    img.setImageDrawable(getResources().getDrawable(R.drawable.default_cover_art));
    e.printStackTrace();
}*/
       /* byte[] songArt = getAlbumArt(song.getAlbum());
        Glide.with(mInflater.getContext()).asBitmap()
                .load(songArt)
                .error(R.drawable.default_cover_art)
                .into(img);*/
        img.setImageURI(queryAlbumUri(song.getAlbum()));
        Ischeck = true;
        try {
            if (mediaPlaybackService.getPlaying()) {
                mediaPlaybackService.getmMediaPlayer().pause();
                mediaPlaybackService.playMedia(song);
            } else
                mediaPlaybackService.playMedia(song);
            String selection = " id_provider =" + song.getID();
            Cursor c = getActivity().managedQuery(mURISong, null, selection, null, null);
            if (c.moveToFirst()) {
                do {
                    if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.IS_FAVORITE)) != 1)
                        if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT_OF_PLAY)) < 2) {
                            ContentValues values = new ContentValues();
                            values.put(FavoriteSongsProvider.COUNT_OF_PLAY, c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT_OF_PLAY)) + 1);
                            getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + song.getID(), null);
                        } else {
                            if (c.getInt(c.getColumnIndex(FavoriteSongsProvider.COUNT_OF_PLAY)) == 2) {
                                ContentValues values = new ContentValues();
                                values.put(FavoriteSongsProvider.COUNT_OF_PLAY, 0);
                                values.put(FavoriteSongsProvider.IS_FAVORITE, 2);
                                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI, values, FavoriteSongsProvider.ID_PROVIDER + "= " + song.getID(), null);
                            }
                        }
                } while (c.moveToNext());
            }
            mediaPlaybackService.showNotification(song.getTitle(), song.getArtist(), song.getFile());
            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLandscape() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else return false;
    }

    public Uri queryAlbumUri(String imgUri) {
        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, Long.parseLong(imgUri));//noi them mSrcImageSong vao artworkUri
    }

    public static byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        byte[] albumArt = mediaMetadataRetriever.getEmbeddedPicture();  // chuyển đổi đường dẫn file media thành đường dẫn file Ảnh
        mediaMetadataRetriever.release();
        return albumArt;
    }

    public void onClickPause() {
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlaybackService != null) {
                    Log.d("HoangCV2", "onClick: " + mediaPlaybackService.getPlaying());
                }
                if (mediaPlaybackService.getPlaying()) {
                    mediaPlaybackService.pauseMedia();
                    mPlayPause.setImageResource(R.drawable.ic_media_play_light);
                    mediaPlaybackService.setPlaying(false);
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
                    mediaPlaybackService.setPlaying(true);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
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
        if (!Ischeck)
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("HoangCgV7eg", "onClick:service " + mediaPlaybackService);
                    Log.d("HoangCgV7eg", "onClick: " + arrayList);
                    Log.d("HoangCgV7eg", "onClick: "+songs);
                    Log.d("HoangCgV7eg", "onClick:media "+mediaPlaybackFragment);
                    mediaPlaybackService.setmListSong(arrayList);
                    mediaPlaybackFragment.setService(mediaPlaybackService);
                    displayMediaFragment.onclickDisplay(arrayList.get(index));
                }
            });
    }

    public void updateUI() {
        Log.d("HoangCff1V", "updateUI:330 " + mediaPlaybackService);
        if (mediaPlaybackService != null)
            Log.d("HoangCff1V", "updateUI: "+mediaPlaybackService.isPlaying());
            if (mediaPlaybackService.getmMediaPlayer() != null) {
                img.setImageURI(Uri.parse(UpdateUI.getAlbum()));
                title.setText(mediaPlaybackService.getNameSong());
                artist.setText(mediaPlaybackService.getArtist());
                if (mediaPlaybackService.isPlaying()) {
                    mPlayPause.setImageResource(R.drawable.ic_pause_black_large);
                } else
                    mPlayPause.setImageResource(R.drawable.ic_media_play_light);
                //mListAdapter.notifyDataSetChanged();

            }
    }
}











