package Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.out.activitymusic.MainActivity;
import com.out.activitymusic.MediaPlaybackFragment;
import com.out.activitymusic.R;
import com.out.activitymusic.Song;
import com.out.activitymusic.UpdateUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class ServiceMediaPlay extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,

        AudioManager.OnAudioFocusChangeListener {
    private static final String NOTIFICATION_CHANNEL_ID = "1";
    public static final String ACTION_PERVIOUS = "xxx.yyy.zzz.ACTION_PERVIOUS";
    public static final String ACTION_PLAY = "xxx.yyy.zzz.ACTION_PLAY";
    public static final String ACTION_NEXT = "xxx.yyy.zzz.ACTION_NEXT";

    UpdateUI mUpdateUI;
    //MediaSession
    private MediaSessionManager mediaSessionManager;
    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;
    private MediaPlayer mediaPlayer;
    private String mediaFile;
    private int resumePosition;
    private AudioManager audioManager;
    private final IBinder iBinder = new LocalBinder();
    private SeekBar seekBar;
    private MediaPlayer mPlayer;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    MediaPlaybackFragment mediaPlaybackFragment;

    public void setListSong(ArrayList<Song> mListSong) {
        this.ListSong = mListSong;
    }

    private ArrayList<Song> ListSong;

    public int getCurrentPlay() {
        return mCurrentPlay;
    }

    private int mCurrentPlay;
    String mTitle = "";
    String mArtistt = "";
    String mPotoMusic = "";

    public void setMediaPlaybackFragment(MediaPlaybackFragment mediaPlaybackFragment) {
        this.mediaPlaybackFragment = mediaPlaybackFragment;
    }

    @Override
    public void onCreate() {
        // Toast.makeText(this,"onCreate",Toast.LENGTH_SHORT).show();
        Log.d("nhungltk123", "onCreate: ");
        mediaPlayer = new MediaPlayer();

//
        mUpdateUI = new UpdateUI(getApplicationContext());
        mTitle = mUpdateUI.getTitle();
        mArtistt = mUpdateUI.getArtist();

        mPotoMusic = mUpdateUI.getAlbum();
        rand = new Random();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Bkav Nhungltk: khi nao thi nhay vao ham nay?
        Log.d("nhungltk123", "onStartCommand: `");
        try {
            //An audio file is passed to the service through putExtra();
            mediaFile = intent.getExtras().getString("media");
        } catch (NullPointerException e) {
        }
        if (requestAudioFocus() == false) {
        }

        if (mediaFile != null && mediaFile != "")
            initMediaPlayer();
        if ((mediaPlayer != null) && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PERVIOUS:
                    previousMedia();
                    break;

                case ACTION_NEXT:
                    nextMedia();
                    break;

                case ACTION_PLAY:
                    if (mediaPlayer.isPlaying())
                        pauseMedia();
                    else {
                        try {
                            playMedia(ListSong.get(possition));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }


    public void onCompletionSong() throws IOException {
        Log.d("nhungltk123", "onCompletionSong: ");
        mediaPlayer.pause();
        //mediaPlayer.setOnCompletionListener(this);
        if (!shuffle) shuffle = mUpdateUI.getShuffle();
        if (repeat == -1) shuffle = mUpdateUI.getShuffle();
        int rtpos = possition;
        if (repeat != -1) {
            if (repeat == 1)
                possition = rtpos;
            else if (repeat == 0) {
                rtpos++;
                if (rtpos > ListSong.size() - 1)
                    rtpos = 0;
                possition = rtpos;

            }
        } else if (shuffle) {
            int newSong = possition;
            while (newSong == possition) {
                newSong = rand.nextInt(ListSong.size());
            }
            possition = newSong;
        }
        try {
            playMedia(ListSong.get(possition));

        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlaybackFragment.getText(ListSong.get(possition));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //   Toast.makeText(this,"onUnbind",Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }


    @Override
    public void onDestroy() {
        //   Toast.makeText(this,"onDestroy",Toast.LENGTH_SHORT).show();
        Log.d("nhungltk123", "onDestroy: ");
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
    }

    public void start(Song song) throws IOException {

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), File(song.getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    public Uri File(String file) {
        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(file));
        return contentUri;
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;

        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    public MediaPlayer getmMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d("nhungltk123", "onCompletion: ");
        shuffle = mUpdateUI.getShuffle();
        repeat = mUpdateUI.getRepeat();
        int rtpos = possition;
        if (repeat != -1) {
            if (repeat == 1)
                possition = rtpos;
            else if (repeat == 0) {
                rtpos++;
                if (rtpos > ListSong.size() - 1)
                    rtpos = 0;
                possition = rtpos;
            }
        } else if (shuffle) {
            int newSong = possition;
            while (newSong == possition) {
                newSong = rand.nextInt(ListSong.size());
            }
            possition = newSong;
        } else possition++;
        if ((possition > ListSong.size() - 1) && (repeat == -1)) pauseMedia();
        else try {
            playMedia(ListSong.get(possition));

        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlaybackFragment.getText(ListSong.get(possition));
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        switch (i) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + i1);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + i1);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + i1);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
//        try {
//           // playMedia();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {


    }

    public int getCurrentStreamPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekToPos(int i) {
        mediaPlayer.seekTo(i);
    }


    public class LocalBinder extends Binder {
        public ServiceMediaPlay getService() {
            return ServiceMediaPlay.this;
        }


    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(mediaFile);
        } catch (IOException e) {
            e.printStackTrace();
            //stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private int possition;

    public int getPossision() {
        return possition;
    }

    public void initSong(Song song) {

    }

    public void nextMedia() {
        int rtpos = possition;
        if (repeat != -1) {
            if (repeat == 1)
                possition = rtpos;
            else if (repeat == 0) {
                rtpos++;
                if (rtpos > ListSong.size() - 1)
                    rtpos = 0;
                possition = rtpos;
            }
        } else if (shuffle) {
            int newSong = possition;
            while (newSong == possition) {
                newSong = rand.nextInt(ListSong.size());
            }
            possition = newSong;
        } else possition++;
        if ((possition > ListSong.size() - 1) && ((repeat == -1) || (!shuffle))) pauseMedia();
        else
            try {
                playMedia(ListSong.get(possition));
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public MediaPlayer getPlayer() {
        return mPlayer;
    }

    public void previousMedia() {
        int rtpos = possition;
        if (repeat != -1) {
            if (repeat == 1)
                possition = rtpos;
            else if (repeat == 0) {
                rtpos++;
                if (rtpos > ListSong.size() - 1)
                    rtpos = 0;
                possition = rtpos;
            }
        } else if (shuffle) {
            int newSong = possition;
            while (newSong == possition) {
                newSong = rand.nextInt(ListSong.size());
            }
            possition = newSong;
        } else possition--;
        if ((possition < 0) && ((!shuffle) || (repeat != -1))) pauseMedia();
        else try {
            playMedia(ListSong.get(possition));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playMedia(Song song) throws IOException {
        //Bkav Nhungltk: tai sao lai viet nhu nay
        Log.d("nhungltk123", "playMedia: ");
//        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//        }
        //Bkav Nhungltk: day la kich ban choi nhac nhe.
        possition = song.getID() - 1;
        if (mediaPlayer != null)
            mediaPlayer.reset();

        MediaPlayer mMediaPlayer = new MediaPlayer();
        Uri uri = Uri.parse(song.getFile());
        Log.d("nhungltk", "playSong: " + uri);
        mMediaPlayer.setDataSource(getApplicationContext(), uri);
        mMediaPlayer.prepare();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        initSong(song);
        mMediaPlayer.start();
        mediaPlayer = mMediaPlayer;
        mCurrentPlay = song.getID();
        mTitle = song.getTitle();
        mArtistt = song.getArtist();
        mPotoMusic = song.getFile();
        //  buildNotification(PlaybackStatus.PAUSED);

        showNotification(mTitle, mArtistt, mPotoMusic);
        mUpdateUI = new UpdateUI(getApplicationContext());
        mUpdateUI.UpdateTitle(song.getTitle());
        Log.d("HoangCV333", "playMedia: " + possition);
        mUpdateUI.UpdateIndex(possition);
        mUpdateUI.UpdateArtist(song.getArtist());
        mUpdateUI.UpdateFile(song.getFile());
        mUpdateUI.UpdateAlbum(String.valueOf(queryAlbumUri(song.getAlbum())));
        mUpdateUI.UpdateDuration(mediaPlayer.getDuration());
        mUpdateUI.UpdateCurrentPossision(mediaPlayer.getCurrentPosition());
        mUpdateUI.UpdateIsPlaying(mMediaPlayer.isPlaying());
        mMediaPlayer.setOnCompletionListener(this);


    }

    public Uri queryAlbumUri(String imgUri) {

        final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(artworkUri, Long.parseLong(imgUri));//noi them mSrcImageSong vao artworkUri
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
        showNotification(mTitle, mArtistt, mPotoMusic);
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    //    skipToNext();
//    buildNotification(MediaPlaybackStatus.PLAYING);
//        mOnNotificationListener.onUpdate(mAudioIndex, MediaPlaybackStatus.PLAYING);
//        new StorageUtil(getApplicationContext()).storeAudioIndex(mAudioIndex);
//        new StorageUtil(getApplicationContext()).storeAudioIndex(mAudioIndex);
    private boolean shuffle = false;
    private int repeat = -1;
    private Random rand;

    public void setShuffle() {
        if (shuffle) shuffle = false;
        else shuffle = true;
        mUpdateUI.UpdateShuffle(shuffle);
    }

    public void setRepeat() {
        if (repeat == -1) repeat = 0;
        else if (repeat == 0) repeat = 1;
        else repeat = -1;
        mUpdateUI.UpdateRepeat(repeat);

    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    public void showNotification(String nameSong, String nameArtist, String path) {
        createNotificationChanel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, ServiceMediaPlay.class);
        previousIntent.setAction(ACTION_PERVIOUS);
        PendingIntent previousPendingIntent = null;

        Intent playIntent = new Intent(this, ServiceMediaPlay.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = null;

        Intent nextIntent = new Intent(this, ServiceMediaPlay.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            previousPendingIntent = PendingIntent.getForegroundService(getApplicationContext(), 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            playPendingIntent = PendingIntent.getForegroundService(getApplicationContext(), 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nextPendingIntent = PendingIntent.getForegroundService(getApplicationContext(), 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        RemoteViews mSmallNotification = new RemoteViews(getPackageName(), R.layout.notification_small);
        RemoteViews mNotification = new RemoteViews(getPackageName(), R.layout.notification);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.default_cover_art);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setCustomContentView(mSmallNotification);
        builder.setCustomBigContentView(mNotification);
        builder.setContentIntent(pendingIntent);

        mNotification.setTextViewText(R.id.title_ntf, nameSong);
        mNotification.setTextViewText(R.id.artist_ntf, nameArtist);
        mNotification.setOnClickPendingIntent(R.id.previous_ntf, previousPendingIntent);
        mNotification.setOnClickPendingIntent(R.id.next_ntf, nextPendingIntent);
        mNotification.setOnClickPendingIntent(R.id.play_ntf, playPendingIntent);
        mNotification.setImageViewResource(R.id.previous_ntf, R.drawable.ic_rew_dark);
        mNotification.setImageViewResource(R.id.next_ntf, R.drawable.ic_fwd_dark);
        mNotification.setImageViewResource(R.id.play_ntf, isPlaying() ? R.drawable.ic_baseline_pause_circle_filled_24 : R.drawable.ic_baseline_play_circle_filled_24);
        if (getAlbumn(path) != null) {
            mNotification.setImageViewBitmap(R.id.img_ntf, getAlbumn(path));
        } else {
            mNotification.setImageViewResource(R.id.img_ntf, R.drawable.default_cover_art);
        }
        mSmallNotification.setOnClickPendingIntent(R.id.play_smallntf, playPendingIntent);
        mSmallNotification.setOnClickPendingIntent(R.id.previous_smallntf, previousPendingIntent);
        mSmallNotification.setOnClickPendingIntent(R.id.next_smallntf, nextPendingIntent);
        mSmallNotification.setImageViewResource(R.id.previous_ntf, R.drawable.ic_rew_dark);
        mSmallNotification.setImageViewResource(R.id.next_ntf, R.drawable.ic_fwd_dark);
        mSmallNotification.setImageViewResource(R.id.play_smallntf, isPlaying() ? R.drawable.ic_baseline_pause_circle_filled_24 : R.drawable.ic_baseline_play_circle_filled_24);
        if (getAlbumn(path) != null) {
            mSmallNotification.setImageViewBitmap(R.id.img_ntf_small, getAlbumn(path));
        } else {
            mSmallNotification.setImageViewResource(R.id.img_ntf_small, R.drawable.default_cover_art);
        }
        startForeground(1, builder.build());
    }

    public void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "mUSIC SERVICE CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }
    }

    public Bitmap getAlbumn(String path) {
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(path);
        byte[] data = metadataRetriever.getEmbeddedPicture();
        return data == null ? null : BitmapFactory.decodeByteArray(data, 0, data.length);
    }

}
