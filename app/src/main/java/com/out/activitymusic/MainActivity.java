package com.out.activitymusic;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.out.activitymusic.interfaces.DataFragment;
import com.out.activitymusic.interfaces.DisplayMediaFragment;

import java.util.ArrayList;

import Service.MediaPlaybackService;

public class MainActivity extends AppCompatActivity implements DisplayMediaFragment, DataFragment, NavigationView.OnNavigationItemSelectedListener {
    public static IntentFilter Broadcast_PLAY_NEW_AUDIO;
    String PRIVATE_MODE = "color";
    AllSongsFragment allSongsFragment;
    BaseSongListFragment baseSongListFragment;
    MediaPlaybackFragment mediaPlaybackFragment;
    private AppBarConfiguration mAppBarConfiguration;
    private FavoriteSongsFragment mFavoriteSongsFragment;
    private boolean mStatus=false;

    public MediaPlaybackService getPlayer() {
        return mediaPlaybackService;
    }
    public MediaPlaybackService getMediaPlaybackService() {
        return mediaPlaybackService;
    }
    public MediaPlaybackService mediaPlaybackService;
    boolean serviceBound = false;
    private Song song;
    private ArrayList<Song> mListSong;
    SharedPreferences sharedPreferences;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mLinearLayout;
    private ListAdapter listAdapter= new ListAdapter();
    private int possision;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlaybackService.LocalBinder binder = (MediaPlaybackService.LocalBinder) service;
            mediaPlaybackService = binder.getService();
            mediaPlaybackService.setListSong(mListSong);
            iConnectActivityAndBaseSong.connectActivityAndBaseSong();
            serviceBound = true;
            allSongsFragment.setService(mediaPlaybackService);
            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
        }
    }

    public void setSong(Song songs) {
        this.song = songs;
    }

    public void FileSong(Song song) {
        song.getFile();
    }

    @Override
    protected void onStart() {
        startService();
        super.onStart();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);

        mediaPlaybackFragment = new MediaPlaybackFragment();
        allSongsFragment = new AllSongsFragment(this,this,this.mediaPlaybackFragment);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            FragmentManager manager = this.getSupportFragmentManager();
            allSongsFragment.setBoolean(true);
            manager.beginTransaction().replace(R.id.fragmentSongOne, allSongsFragment).commit();
        } else {
            allSongsFragment.setBoolean(false);
            FragmentManager manager = this.getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragmentSongOne, allSongsFragment).commit();
            FragmentManager manager1 = this.getSupportFragmentManager();
            manager1.beginTransaction().replace(R.id.fragmentMediaTwo, mediaPlaybackFragment).commit();
        }

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragmentSongOne);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentSongOne);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
*/
    }
/*    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
 /*   @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home:
                drawer.closeDrawer(GravityCompat.START);
                displayToast(getString(R.string.listnow));
                return true;
            case R.id.nav_gallery:
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_slideshow:
                drawer.closeDrawer(GravityCompat.START);
                return true;

            default:
                return false;
        }
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }*/
public void startService(){
        Intent intent = new Intent(this, MediaPlaybackService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onclick(Song song) {
        Log.d("HoangCgV7", "onSaveInstanceState: " + mediaPlaybackFragment);
        Log.d("HoangCV333", "onclick: "+(song.getID()-1));
        mediaPlaybackFragment = new MediaPlaybackFragment().newInstance(song);
        FragmentManager manager1 = this.getSupportFragmentManager();
        manager1.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentSongOne, mediaPlaybackFragment)
                .commit();
        mediaPlaybackFragment.setListSong(mListSong);
        mediaPlaybackFragment.setService(mediaPlaybackService);
        mediaPlaybackFragment.updateTime();
        mediaPlaybackService.setMediaPlaybackFragment(mediaPlaybackFragment);
        getSupportActionBar().hide();
    }

    @Override
    public void onclickData(ArrayList ListSong) {
        this.mListSong = ListSong;
    }

    public void setService(MediaPlaybackService service) {
        this.mediaPlaybackService = service;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.favorite) {
            Toast.makeText(this, "favorite", Toast.LENGTH_SHORT).show();
            mStatus=true;
            mFavoriteSongsFragment = new FavoriteSongsFragment( mediaPlaybackService.getListsong(), mediaPlaybackService);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentSongOne, mFavoriteSongsFragment).commit();
            mDrawerLayout= findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawer(GravityCompat.START);

        } else if (id == R.id.List_music) {
            mStatus=false;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentSongOne, allSongsFragment).commit();
            mDrawerLayout= findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    //Bkav Nhungltk
    interface IConnectActivityAndBaseSong {
        void connectActivityAndBaseSong();
    }

    private IConnectActivityAndBaseSong iConnectActivityAndBaseSong;

    public void setiConnectActivityAndBaseSong(IConnectActivityAndBaseSong iConnectActivityAndBaseSong) {
        this.iConnectActivityAndBaseSong = iConnectActivityAndBaseSong;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        setService(mediaPlaybackService);
    }

}