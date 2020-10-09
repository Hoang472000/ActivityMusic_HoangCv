package com.out.activitymusic;

import android.content.ContentUris;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import Service.MediaPlaybackService;

public class MediaPlaybackFragment extends Fragment implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {
    static TextView txtView;
    TextView mNameSong, time2, time1;
    ImageView img;
    RelativeLayout imgBig;
    private ImageView image;
    private ImageView mPlayPauseMedia;
    private MediaPlaybackService mediaPlaybackService;
    private Song song;
    private ImageView mLike, mDisLike, mQueue;
    private ImageView mPlayReturn, mPlayNext, mShuffle, mRepeat;
    private SeekBar mSeekBar;
    private TextView mArtist;

    MediaPlayer mediaPlayer;
    private ArrayList<Song> mListSong;
    private int mSongCurrentDuration;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String TITLE_KEY = "title";
    String IMAGE_KEY = "image";
    String DURATION_KEY = "duration";


    public MediaPlaybackFragment newInstance(Song song) {
        MediaPlaybackFragment fragment = new MediaPlaybackFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("audio", song);
        bundle.putString("song", song.getTitle());
        bundle.putString("song1", getDurationTime1(song.getDuration()));
        bundle.putString("song2", String.valueOf(queryAlbumUri(song.getAlbum())));
        Log.d("HoangC1V", "newInstance: " + bundle);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setListSong(ArrayList mListSong) {
        this.mListSong = mListSong;
    }

    public void setService(MediaPlaybackService service) {
        this.mediaPlaybackService = service;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public MediaPlaybackFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    UpdateUI mUpdateUI;
    boolean shuffler;
    boolean isShuff = true;
    int repeat = 1;
    int isRepeat = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("nhungltkk", "onCreateView: " + "oncreate");
        View view = inflater.inflate(R.layout.mediaplaybackfragment, container, false);
        mNameSong = view.findViewById(R.id.song1);
        time2 = view.findViewById(R.id.Time2);
        time1 = view.findViewById(R.id.Time1);
        img = view.findViewById(R.id.picture_small);
        imgBig = view.findViewById(R.id.picture_big);
        image = view.findViewById(R.id.mnMedia);
        mQueue = view.findViewById(R.id.queue_music);
        mPlayPauseMedia = view.findViewById(R.id.play_pause_media);
        mLike = view.findViewById(R.id.like);
        mPlayReturn = view.findViewById(R.id.play_return);
        mDisLike = view.findViewById(R.id.dislike);
        mPlayNext = view.findViewById(R.id.play_next);
        mSeekBar = view.findViewById(R.id.seekBar);
        mShuffle = view.findViewById(R.id.shuffle);
        mRepeat = view.findViewById(R.id.repeat);
      /*  mSeekBar.setMax((int) (serviceMediaPlay.getDuration())/1000);
        mSeekBar.setProgress((int) (serviceMediaPlay.getCurrentStreamPosition())/1000);*/
       /* ((MainActivity) getActivity()).setiConnectActivityAndBaseSong(new MainActivity.IConnectActivityAndBaseSong() {
            @Override
            public void connectActivityAndBaseSong() {
                if (((MainActivity) getActivity()).serviceMediaPlay != null) {
                    serviceMediaPlay=((MainActivity) getActivity()).serviceMediaPlay;
                    Log.d("nhungltkk", "onCreateView:+media "+serviceMediaPlay);//khong chay vao day
                }
            }
        });*/
        Log.d("nhungltkk", "onCreateView:+media " + mediaPlaybackService);
        if (isPortraint()) {
            if (mQueue.getVisibility() == View.VISIBLE)
                mQueue.setVisibility(View.INVISIBLE);
        }
        mUpdateUI = new UpdateUI(getContext());
/*        shuffler = mUpdateUI.getShuffle();
        repeat = mUpdateUI.getRepeat();*/
        //       if(!serviceMediaPlay.isPlaying())
        mNameSong.setText(mUpdateUI.getTitle());
        img.setImageURI(Uri.parse(mUpdateUI.getAlbum()));
        imgBig.setBackground(setImgBig(mUpdateUI.getAlbum()));
        time2.setText(getDurationTime1(String.valueOf(mUpdateUI.getDuration())));

        Log.d("HoangCV1234567", "onCreateView: " + song);
        if (mediaPlaybackService != null) {
            if (mediaPlaybackService.getShuffle() == true) {
                mShuffle.setImageResource(R.drawable.ic_play_shuffle_orange);
            } else mShuffle.setImageResource(R.drawable.ic_shuffle_white);
            if (mediaPlaybackService.getRepeat() == 1) {
                mRepeat.setImageResource(R.drawable.ic_repeat_one_song_dark);
            } else if (mediaPlaybackService.getRepeat() == 0) {
                mRepeat.setImageResource(R.drawable.ic_repeat_dark_selected);
            } else {
                mRepeat.setImageResource(R.drawable.ic_repeat_white);
            }
        }
/*
        if (!serviceMediaPlay.isPlaying())
           mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
        else  mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);*/
        if (getArguments() != null) {
            setText(getArguments());
        }
        Popmenu();
//        mSeekBar.setMax(serviceMediaPlay.getDuration());
        mSeekBar.setMax(mUpdateUI.getDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlaybackService.seekToPos(progress);
                }
                time1.setText(getDurationTime1(String.valueOf(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlaybackService.getmMediaPlayer().seekTo(seekBar.getProgress());
            }
        });
        if (isPortraint()) {
            if (mediaPlaybackService != null) {
                mSeekBar.setMax(mUpdateUI.getDuration());
                updateTime();
            }
        } else if (mediaPlaybackService != null) {
            updateTime();
        }
        return view;
    }

    public void getText(Song song) {
        if (mediaPlaybackService != null)
            time1.setText(getDurationTime1(String.valueOf(mUpdateUI.getCurrentPossision())));
        this.song = song;
        mNameSong.setText(song.getTitle());
        time2.setText(getDurationTime1(song.getDuration()));
        img.setImageURI(queryAlbumUri(song.getAlbum()));
        String pathName = String.valueOf(queryAlbumUri(song.getAlbum()));
        imgBig.setBackground(setImgBig(pathName));
        mLike.setOnClickListener(this);
        mPlayReturn.setOnClickListener(this);
        mPlayPauseMedia.setOnClickListener(this);
        mPlayNext.setOnClickListener(this);
        mDisLike.setOnClickListener(this);
        mShuffle.setOnClickListener(this);
        mRepeat.setOnClickListener(this);

        //      mSeekBar.setMax(serviceMediaPlay.getDuration());
    }

    public void setText(Bundle bundle) {

        mNameSong.setText(bundle.getString("song"));
        time2.setText(bundle.getString("song1"));
        img.setImageURI(Uri.parse(bundle.getString("song2")));
        this.song = (Song) bundle.getSerializable("audio");
        String pathName = bundle.getString("song2");
        imgBig.setBackground(setImgBig(pathName));
        mLike.setOnClickListener(this);
        mPlayReturn.setOnClickListener(this);
        mPlayPauseMedia.setOnClickListener(this);
        mPlayNext.setOnClickListener(this);
        mDisLike.setOnClickListener(this);
        mShuffle.setOnClickListener(this);
        mRepeat.setOnClickListener(this);
        // mSeekBar.setMax(serviceMediaPlay.getDuration());

    }

    public Drawable setImgBig(String pathName) {
        Uri uri = Uri.parse(pathName);
        Drawable yourDrawable = null;
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            yourDrawable = Drawable.createFromStream(inputStream, pathName);
        } catch (IOException e) {
            yourDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
            e.printStackTrace();
        }
        return yourDrawable;
    }

    private String getDurationTime1(String str) {
        String duration;
        int mili = Integer.parseInt(str) / 1000;
        int phut = mili / 60;
        int giay = mili % 60;
        if (giay >= 10)
            duration = String.valueOf(phut) + ":" + String.valueOf(giay);
        else
            duration = String.valueOf(phut) + ":0" + String.valueOf(giay);
        return duration;
    }

    public Uri queryAlbumUri(String imgUri) {

        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, Long.parseLong((imgUri)));//noi them imgUri vao artworkUri
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
            case R.id.add_song_favorite:
                return true;
            case R.id.remove_song_favorite:
                return true;
            default:
                return false;
        }
    }

    public boolean isPortraint() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else return false;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.like:
                Toast.makeText(getActivity(), "liked", Toast.LENGTH_SHORT).show();
                mLike.setImageResource(R.drawable.ic_thumbs_up_selected);
                break;
            case R.id.play_return: {
                mediaPlaybackService.previousMedia();
                getText(mListSong.get(mediaPlaybackService.getPossision()));
                time1.setText(getDurationTime1("0"));
                break;
            }
            case R.id.play_pause_media: {
                if (mediaPlaybackService.isPlaying()) {
                    mediaPlaybackService.pauseMedia();
                    mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                } else {
                    mediaPlaybackService.resumeMedia();
                    mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                }
                break;
            }

            case R.id.play_next: {
                mediaPlaybackService.nextMedia();
                getText(mListSong.get(mediaPlaybackService.getPossision()));
                time1.setText(getDurationTime1("0"));
                break;
            }
            case R.id.dislike:
                Toast.makeText(getActivity(), "disliked", Toast.LENGTH_SHORT).show();
                mDisLike.setImageResource(R.drawable.ic_thumbs_down_selected);
                break;
            case R.id.shuffle: {
                if (mediaPlaybackService.getShuffle() == false) {
                    mShuffle.setImageResource(R.drawable.ic_play_shuffle_orange);
                    mediaPlaybackService.setListSong(mListSong);
                    mediaPlaybackService.setShuffle(true);
                } else {
                    mShuffle.setImageResource(R.drawable.ic_shuffle_white);
                    mediaPlaybackService.setShuffle(false);
                }
                break;
            }
            case R.id.repeat: {
                if (mediaPlaybackService.getRepeat() == 1) {
                    mRepeat.setImageResource(R.drawable.ic_repeat_white);
                    mediaPlaybackService.setRepeat(-1);
                } else if (mediaPlaybackService.getRepeat() == -1) {
                    mRepeat.setImageResource(R.drawable.ic_repeat_dark_selected);
                    mediaPlaybackService.setRepeat(0);
                } else {
                    mRepeat.setImageResource(R.drawable.ic_repeat_one_song_dark);
                    mediaPlaybackService.setRepeat(1);
                }
                break;
            }
            default:
                break;
        }
    }

    private Bitmap getAlbumn(String path) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        byte[] data = metadataRetriever.getEmbeddedPicture();
        return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public void updateUI() {
        if (mediaPlaybackService != null && mSeekBar != null) {
            if (mediaPlaybackService.getmMediaPlayer() != null) {
                updateTime();
                mSeekBar.setMax(mediaPlaybackService.getDuration());
                mNameSong.setText(mediaPlaybackService.getNameSong());
//                mArtist.setText(mediaPlaybackService.getArtist());
                Bitmap bitmap = getAlbumn(mediaPlaybackService.getPotoMusic());
                imgBig.setBackground(setImgBig(mediaPlaybackService.getPotoMusic()));
                img.setImageBitmap(bitmap);
                SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
                time2.setText(formmatTime.format(mediaPlaybackService.getDuration()));
                if (mediaPlaybackService.isPlaying()) {
                    mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);

                } else {
                    mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                }
                if (mediaPlaybackService.getShuffle()) {
                    mShuffle.setBackgroundResource(R.drawable.ic_play_shuffle_orange);
                } else
                    mShuffle.setBackgroundResource(R.drawable.ic_shuffle_white);

                if (mediaPlaybackService.getRepeat() == 0) {
                    mRepeat.setBackgroundResource(R.drawable.ic_repeat_dark_selected);
                } else {
                    if (mediaPlaybackService.getRepeat() == -1) {
                        mRepeat.setBackgroundResource(R.drawable.ic_repeat_white);
                    } else
                        mRepeat.setBackgroundResource(R.drawable.ic_repeat_one_song_dark);
                }
            }
        }
    }


    public void updateTime() {
        final Handler handler = new Handler();
        if (song != null)
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                    time1.setText(formatTime.format(mediaPlaybackService.getCurrentStreamPosition()));
                    Log.d("HoangCV444", "run: " + mediaPlaybackService);
                    mSeekBar.setProgress(mediaPlaybackService.getCurrentStreamPosition());
                    Log.d("HoangCV444", "run: " + mediaPlaybackService.getmMediaPlayer());
                    mediaPlaybackService.getmMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            try {
                                mediaPlaybackService.onCompletionSong();
                                mSeekBar.setMax(mediaPlaybackService.getDuration());
                                //updateUI();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    handler.postDelayed(this, 500);
                }
            }, 100);
    }

}
