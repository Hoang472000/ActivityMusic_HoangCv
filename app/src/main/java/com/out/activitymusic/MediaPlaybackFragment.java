package com.out.activitymusic;

import android.content.ContentUris;
import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Service.ServiceMediaPlay;

public class MediaPlaybackFragment extends Fragment implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {
    static TextView txtView;
    TextView tv, time;
    ImageView img;
    RelativeLayout imgBig;
    private ImageView image;
    private ImageView mPlayPauseMedia;
    private ServiceMediaPlay serviceMediaPlay;
    private Song song;
    private ImageView mLike, mDisLike;
    private ImageView mPlayReturn, mPlayNext;
    private SeekBar mSeekBar;
    private UpdateSeekBarThread updateSeekBarThread;

    private ArrayList<Song> mListSong;
    private int mSongCurrentDuration;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String TITLE="title";


    public MediaPlaybackFragment newInstance(Song song) {
        Log.d("HoangC1V", "newInstance: ");
        MediaPlaybackFragment fragment = new MediaPlaybackFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("audio", song);
        bundle.putString("song", song.getTitle());
        bundle.putString("song1", getDurationTime(song.getDuration()));
        bundle.putString("song2", String.valueOf(queryAlbumUri(song.getAlbum())));
        Log.d("HoangC1V", "newInstance: " + bundle);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListSong(ArrayList mListSong) {
        this.mListSong = mListSong;
    }

    public void setService(ServiceMediaPlay service) {
        this.serviceMediaPlay = service;
    }

    public MediaPlaybackFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HoangCV", "onCreateView: " + "oncreate");
        View view = inflater.inflate(R.layout.mediaplaybackfragment, container, false);
        tv = view.findViewById(R.id.song1);
        time = view.findViewById(R.id.Time2);
        img = view.findViewById(R.id.picture_small);
        imgBig = view.findViewById(R.id.picture_big);
        image = view.findViewById(R.id.mnMedia);
        mPlayPauseMedia = view.findViewById(R.id.play_pause_media);
        mLike = view.findViewById(R.id.like);
        mPlayReturn = view.findViewById(R.id.play_return);
        mDisLike = view.findViewById(R.id.dislike);
        mPlayNext = view.findViewById(R.id.play_next);
        mSeekBar=view.findViewById(R.id.seekBar);
      /*  mSeekBar.setMax((int) (serviceMediaPlay.getDuration())/1000);
        mSeekBar.setProgress((int) (serviceMediaPlay.getCurrentStreamPosition())/1000);*/
        ((MainActivity) getActivity()).setiConnectActivityAndBaseSong(new MainActivity.IConnectActivityAndBaseSong() {
            @Override
            public void connectActivityAndBaseSong() {
                if (((MainActivity) getActivity()).player != null) {
                    Log.d("nhungltk", "onCreateView: " + "not null");
                    setService((((MainActivity) getActivity()).player));
                }
            }
        });

        if (getArguments() != null) {
            setText(getArguments());
        }
        Popmenu();
     //   mSeekBar.setMax(serviceMediaPlay.getDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                serviceMediaPlay.getmMediaPlayer().seekTo(seekBar.getProgress());
            }
        });

        return view;
    }

    public void getText(Song song) {

        this.song = song;
        tv.setText(song.getTitle());
        time.setText(getDurationTime(song.getDuration()));
        img.setImageURI(queryAlbumUri(song.getAlbum()));
        String pathName = String.valueOf(queryAlbumUri(song.getAlbum()));
        Uri uri = Uri.parse(pathName);
        Drawable yourDrawable = null;
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            yourDrawable = Drawable.createFromStream(inputStream, pathName);
        } catch (IOException e) {
            yourDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
            e.printStackTrace();
        }
        imgBig.setBackground(yourDrawable);
        mLike.setOnClickListener(this);
        mPlayReturn.setOnClickListener(this);
        mPlayPauseMedia.setOnClickListener(this);
        mPlayNext.setOnClickListener(this);
        mDisLike.setOnClickListener(this);
        mSeekBar.setMax(serviceMediaPlay.getDuration());
    }

    public void setText(Bundle bundle) {

        tv.setText(bundle.getString("song"));
        time.setText(bundle.getString("song1"));
        img.setImageURI(Uri.parse(bundle.getString("song2")));
        this.song = (Song) bundle.getSerializable("audio");
        String pathName = bundle.getString("song2");
        Uri uri = Uri.parse(pathName);
        Drawable yourDrawable = null;
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            yourDrawable = Drawable.createFromStream(inputStream, pathName);
        } catch (IOException e) {
            yourDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
            e.printStackTrace();
        }
        imgBig.setBackground(yourDrawable);
        mLike.setOnClickListener(this);
        mPlayReturn.setOnClickListener(this);
        mPlayPauseMedia.setOnClickListener(this);
        mPlayNext.setOnClickListener(this);
        mDisLike.setOnClickListener(this);
        mSeekBar.setMax(serviceMediaPlay.duration());

    }

    public void setImgBig(Bundle bundle) {
        String pathName = bundle.getString("song2");
        Uri uri = Uri.parse(pathName);
        Drawable yourDrawable = null;
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            yourDrawable = Drawable.createFromStream(inputStream, pathName);
        } catch (IOException e) {
            yourDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
            e.printStackTrace();
        }
        imgBig.setBackground(yourDrawable);
    }

    private String getDurationTime(String str) {
        int mili = Integer.parseInt(str) / 1000;
        int phut = mili / 60;
        int giay = mili % 60;
        String duration = String.valueOf(phut) + ":" + String.valueOf(giay);
        return duration;
    }

    public Uri queryAlbumUri(String imgUri) {

        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, Long.parseLong(imgUri));//noi them mSrcImageSong vao artworkUri
    }

    public void Popmenu() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.setOnMenuItemClickListener(MediaPlaybackFragment.this);
                popup.inflate(R.menu.poupup_menu);
                popup.show();
            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Toast.makeText(getActivity(), "Hoang" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        switch (menuItem.getItemId()) {
            case R.id.mail:

                return true;
            case R.id.upload:

                return true;
            case R.id.share:

                return true;
            default:
                return false;

        }
    }

    public int getIDSong(Song song) {
        return song.getID();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.like:
                Toast.makeText(getActivity(), "liked", Toast.LENGTH_SHORT).show();
                Log.d("HoangCV123", "onClick: "+mListSong);
                break;
            case R.id.play_return: {
                Toast.makeText(getActivity(), "return", Toast.LENGTH_SHORT).show();
                serviceMediaPlay.returnMedia();

                getText(mListSong.get(serviceMediaPlay.getPossision()));
                break;

            }
            case R.id.play_pause_media:
            {
                if (serviceMediaPlay.isPlaying()) {
                    serviceMediaPlay.pauseMedia();
                    mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);

                } else {
                    serviceMediaPlay.resumeMedia();
                    mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                }
                break;
            }

            case R.id.play_next: {
                Toast.makeText(getActivity(), "next", Toast.LENGTH_SHORT).show();
                serviceMediaPlay.nextMedia();
                getText(mListSong.get(serviceMediaPlay.getPossision()));
                break;
            }
            case R.id.dislike:
                Toast.makeText(getActivity(), "disliked", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }


    }
    public class UpdateSeekBarThread extends Thread {
        private Handler handler;

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            handler = new Handler();
            Looper.loop();
        }

        public void updateSeekBar() {
            Log.d("HoangCV7", "updateSeekBar: ");
            if (serviceMediaPlay != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "runis: " + serviceMediaPlay.isPlaying());
                        if (serviceMediaPlay.isPlaying()) {
                            while (serviceMediaPlay.getPlayer() != null) {
                                try {
                                    long current = -1;
                                    try {
                                        current = serviceMediaPlay.getCurrentStreamPosition();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                    if (getActivity() != null) {
                                        final long finalCurrent = current;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mSeekBar.setMax((int) (serviceMediaPlay.getDuration() / 1000));
                                                mSeekBar.setProgress((int) (finalCurrent / 1000));

                                            }
                                        });
                                    }
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        }

        public void exit() {
            handler.getLooper().quit();
        }
    }


   /* @Override
    public void DataList(ArrayList arrayList) {
        this.mListSong=arrayList;

    }*/
}
