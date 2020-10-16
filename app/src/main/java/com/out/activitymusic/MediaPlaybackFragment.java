package com.out.activitymusic;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.out.activitymusic.database.FavoriteSongsProvider;

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
    BaseSongListFragment baseSongListFragment;
    MediaPlayer mediaPlayer;
    private ArrayList<Song> mListSong;
    private boolean ischeck=false,isIscheck1=false,isLike=false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String TITLE_KEY = "title";
    String IMAGE_KEY = "image";
    String DURATION_KEY = "duration";


    public MediaPlaybackFragment newInstance(Song song) {
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        MediaPlaybackFragment fragment = new MediaPlaybackFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("audio", song);
        bundle.putString("song", song.getTitle());
        bundle.putString("artist1", song.getArtist());
        bundle.putString("song1", formatTime.format(Integer.valueOf(song.getDuration())));
        bundle.putString("song2", String.valueOf(queryAlbumUri(song.getAlbum())));
        fragment.setArguments(bundle);
        return fragment;
    }
    public void setCheck(boolean check)
    {
        this.isIscheck1=check;
    }

    public boolean getcheck(){return isIscheck1;}

    public void setListSong(ArrayList mListSong) {
        this.mListSong = mListSong;
    }

    public void setService(MediaPlaybackService service) {
        this.mediaPlaybackService = service;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
    public  void setBaseSongListFragment(BaseSongListFragment baseSongListFragment){
        this.baseSongListFragment=baseSongListFragment;
    }

    public MediaPlaybackFragment() {
    }

    private MainActivity getActivityMusic() {
        if (getActivity() instanceof MainActivity) {
            return (MainActivity) getActivity();
        }
        return null;
    }

    public void setData() {
        Log.d("SetDATA", "okokok" + getActivityMusic().getMediaPlaybackService());
        if (mediaPlaybackService != null) {
            mediaPlaybackService = getActivityMusic().getMediaPlaybackService();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    UpdateUI mUpdateUI;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setData();
        Log.d("MediaOnCreate", "onCreateView: " + mediaPlaybackService);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mediaplaybackfragment, container, false);
        mNameSong = view.findViewById(R.id.song1);
        mArtist = view.findViewById(R.id.artist1);
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

        mLike.setOnClickListener(this);
        mPlayReturn.setOnClickListener(this);
        mPlayPauseMedia.setOnClickListener(this);
        mPlayNext.setOnClickListener(this);
        mDisLike.setOnClickListener(this);
        mShuffle.setOnClickListener(this);
        mRepeat.setOnClickListener(this);

        mUpdateUI = new UpdateUI(getContext());
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        mUpdateUI = new UpdateUI(getContext());
        mNameSong.setText(mUpdateUI.getTitle());
        mArtist.setText(mUpdateUI.getArtist());
        img.setImageURI(Uri.parse(mUpdateUI.getAlbum()));
        imgBig.setBackground(setImgBig(mUpdateUI.getAlbum()));
        time2.setText(formatTime.format(mUpdateUI.getDuration()));

        if (getArguments() != null) {
            setText(getArguments());
        }
        Popmenu();
        mSeekBar.setMax(mUpdateUI.getDuration());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlaybackService.seekToPos(progress);
                }
                SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                time1.setText(formatTime.format(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlaybackService.getmMediaPlayer().seekTo(seekBar.getProgress());
            }
        });

        if (mediaPlaybackService != null) {
            mSeekBar.setMax(mUpdateUI.getDuration());
            updateUI();
            updateTime();
            mediaPlaybackService.showNotification(mediaPlaybackService.getNameSong(),mediaPlaybackService.getArtist(),mediaPlaybackService.getFile());
        }
        if (isLandscape()) {
            if (mQueue.getVisibility() == View.VISIBLE)
                mQueue.setVisibility(View.INVISIBLE);
            updateTime();
            updateUI();}
        return view;
    }

    public void getText(Song song) {
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        this.song = song;
        mNameSong.setText(song.getTitle());
        mArtist.setText(song.getArtist());
        time2.setText(formatTime.format(Integer.valueOf(song.getDuration())));
        img.setImageURI(queryAlbumUri(song.getAlbum()));
        String pathName = String.valueOf(queryAlbumUri(song.getAlbum()));
        imgBig.setBackground(setImgBig(pathName));
        mSeekBar.setMax(mediaPlaybackService.getDuration());
    }

    public void setText(Bundle bundle) {
        mNameSong.setText(bundle.getString("song"));
        mArtist.setText(bundle.getString("artist1"));
        time2.setText(bundle.getString("song1"));
        img.setImageURI(Uri.parse(bundle.getString("song2")));
        String pathName = bundle.getString("song2");
        imgBig.setBackground(setImgBig(pathName));
    }
    public Drawable setImgBig(String pathName) {
        Uri uri = Uri.parse(pathName);
        Drawable yourDrawable = null;
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            yourDrawable = Drawable.createFromStream(inputStream, pathName);
        } catch (IOException e) {
            yourDrawable = getResources().getDrawable(R.drawable.default_cover_art);
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
        return ContentUris.withAppendedId(artworkUri, Long.parseLong(imgUri));//noi them imgUri vao artworkUri
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

    public boolean isLandscape() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else return false;
    }


    @Override
    public void onClick(View view) {
        mediaPlaybackService.setmListSong(mListSong);
        mediaPlaybackService.setPossition(mUpdateUI.getIndex());
        switch (view.getId()) {
            case R.id.like:
                isLike=true;
                mLike.setImageResource(R.drawable.ic_thumbs_up_selected);
                mDisLike.setImageResource(R.drawable.ic_thumbs_down_default);
                ContentValues values = new ContentValues();
                values.put(FavoriteSongsProvider.IS_FAVORITE,2);
                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,FavoriteSongsProvider.ID_PROVIDER +"= "+ mediaPlaybackService.getPossision(),null);
                Toast.makeText(getContext(),  "like song //"+ mediaPlaybackService.getNameSong(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.play_return: {
                mediaPlaybackService.previousMedia();
                getText(mListSong.get(mediaPlaybackService.getPossision()));
                break;
            }
            case R.id.play_pause_media: {
                if (mediaPlaybackService.getPlaying()) {
                    mediaPlaybackService.pauseMedia();
                    mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    mediaPlaybackService.setPlaying(false);
                } else {
                    mediaPlaybackService.setPlaying(true);
                    if ( (mediaPlaybackService.getCurrentPossion()==0)/*&& (!baseSongListFragment.getcheck())*/ ) {
                        try {
                            mediaPlaybackService.playMedia(mListSong.get(mUpdateUI.getIndex()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if((mediaPlaybackService.getCurrentPossion()>0))
                    { mediaPlaybackService.resumeMedia();}
                    mPlayPauseMedia.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
//                    baseSongListFragment.setCheck(true);
                    setCheck(true);
                }
                break;
            }
            case R.id.play_next: {
                mediaPlaybackService.nextMedia();
                getText(mListSong.get(mediaPlaybackService.getPossision()));
                break;
            }
            case R.id.dislike:
                isLike=false;
                mDisLike.setImageResource(R.drawable.ic_thumbs_down_selected);
                mLike.setImageResource(R.drawable.ic_thumbs_up_default);
                values = new ContentValues();
                values.put(FavoriteSongsProvider.IS_FAVORITE,1);
                getActivity().getContentResolver().update(FavoriteSongsProvider.CONTENT_URI,values,FavoriteSongsProvider.ID_PROVIDER +"= "+ mediaPlaybackService.getPossision(),null);
                Log.d("H1111oangCV", "onClick: "+FavoriteSongsProvider.ID_PROVIDER +"= "+ mediaPlaybackService.getPossision() );
                Toast.makeText(getContext(),  "dislike song //"+ mediaPlaybackService.getNameSong(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.shuffle: {
                if (!mediaPlaybackService.getShuffle()) {
                    mShuffle.setImageResource(R.drawable.ic_play_shuffle_orange);
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
        isIscheck1=true;
        mediaPlaybackService.showNotification(mediaPlaybackService.getNameSong(),mediaPlaybackService.getArtist(),mediaPlaybackService.getFile());
        updateUI();
    }

   /* private Bitmap getAlbum(String path) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        byte[] data = metadataRetriever.getEmbeddedPicture();
        return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
    }*/

    public void updateUI() {

        Log.d("HoangCVmSeekBar", "updateUI: "+mediaPlaybackService);
        Log.d("HoangCVmSeekBar", "updateUI: "+mSeekBar);
       // Log.d("HoangCVmSeekBar", "updateUI: "+mediaPlaybackService.getmMediaPlayer() );
        if (mediaPlaybackService != null && mSeekBar != null) {
          //  if (mediaPlaybackService.getmMediaPlayer() != null)
            {
                updateTime();
                Log.d("okokokok1", "updateUI"+mediaPlaybackService);
                mSeekBar.setMax(mediaPlaybackService.getDuration());
                mNameSong.setText(mediaPlaybackService.getNameSong());
                mArtist.setText(mediaPlaybackService.getArtist());
                Log.d("HoangCVgasfsdf", "updateUI:List "+mListSong);
                try {
                    img.setImageURI(queryAlbumUri(mediaPlaybackService.getPotoMusic()));
                    imgBig.setBackground(setImgBig(String.valueOf(queryAlbumUri(mediaPlaybackService.getPotoMusic()))));
                }
                catch (Exception e){
                    //khong chạy vao day
                    Uri uri= Uri.parse(mediaPlaybackService.getPotoMusic());
                    img.setImageURI(uri);
                    imgBig.setBackground(setImgBig(mediaPlaybackService.getPotoMusic()));
                    e.printStackTrace();
                }
                SimpleDateFormat formmatTime = new SimpleDateFormat("mm:ss");
                if(ischeck)
                time2.setText(formmatTime.format(mediaPlaybackService.getDuration()));
                ischeck=true;
                if (mediaPlaybackService.getPlaying()) {
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
                if(isLike) {mLike.setImageResource(R.drawable.ic_thumbs_up_selected);
                mDisLike.setImageResource(R.drawable.ic_thumbs_down_default);}
                else {mLike.setImageResource(R.drawable.ic_thumbs_up_default);
                mDisLike.setImageResource(R.drawable.ic_thumbs_down_selected);}
            }

        }
    }

    public void updateTime() {
        Log.d("HoangCgV7e", "updateTime"+mediaPlaybackService);
        final Handler handler = new Handler();
        if (song != null || mListSong!=null)
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                    Log.d("HoangsdfCgffV7e", "updateTime"+formatTime.format(mediaPlaybackService.getCurrentStreamPosition())+" "+mediaPlaybackService.getPossision());
                    time1.setText(formatTime.format(mediaPlaybackService.getCurrentStreamPosition()));
                    mSeekBar.setProgress(mediaPlaybackService.getCurrentStreamPosition());
                    mediaPlaybackService.getmMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            try {
                                if(mediaPlaybackService.getPlaying()==false){
                                    Log.d("HofffangCV", "onCompletion: "+mediaPlaybackService.getPlaying());
                                    }
                                else mediaPlaybackService.onCompletionSong();
                                mSeekBar.setMax(mediaPlaybackService.getDuration());
                                updateUI();
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
