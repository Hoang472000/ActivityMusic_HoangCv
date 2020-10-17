package com.out.activitymusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

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
    private FavoriteSongsFragment mFavoriteSongsFragment;
    private boolean mStatus = false;

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
    private UpdateUI mUpdateUI;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    private boolean isFavorite = false;
    private ListAdapter listAdapter = new ListAdapter();


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlaybackService.LocalBinder binder = (MediaPlaybackService.LocalBinder) service;
            mediaPlaybackService = binder.getService();
           if(mListSong!=null) mediaPlaybackService.setListSong(mListSong);
            Log.d("HoangCV", "onServiceConnected: "+mediaPlaybackService.getMediaPlayer().getCurrentPosition());
            iConnectActivityAndBaseSong.connectActivityAndBaseSong();
            serviceBound = true;
            Log.d("Hoanafs1gCV", "onServiceConnected: " + mediaPlaybackService);
            allSongsFragment.setService(mediaPlaybackService);
            if (isLandScape()) {
                allSongsFragment.setService(mediaPlaybackService);

                Log.d("Hoanafs1gCV", "onServiceConnected: " + mediaPlaybackService);
                Log.d("Hoanafs1gCV", "onServiceConnected: " + mediaPlaybackFragment);
                mediaPlaybackService.setmMediaPlaybackFragment(mediaPlaybackFragment);
                mediaPlaybackFragment.setService(mediaPlaybackService);
            }
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


    public void FileSong(Song song) {
        song.getFile();
    }

    @Override
    protected void onStart() {
        Log.d("HoangCVonStart", "onStart:111 ");
        startService();
        super.onStart();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HoangCVonStart", "onCreate: ");

        if (savedInstanceState != null) {
            isFavorite = savedInstanceState.getBoolean("IS_FAVORITE");
        }
        setContentView(R.layout.activity_main);
        initPermission();
        mUpdateUI = new UpdateUI(this);
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
        allSongsFragment = new AllSongsFragment(this, this, this.mediaPlaybackFragment);
        mFavoriteSongsFragment = new FavoriteSongsFragment(mediaPlaybackService, mediaPlaybackFragment, this);
        allSongsFragment.setAllSong(mFavoriteSongsFragment);
        if (!isLandScape()) {
            if (isFavorite) {
                //    mFavoriteSongsFragment = new FavoriteSongsFragment(mediaPlaybackService, mediaPlaybackFragment, this);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentSongOne, mFavoriteSongsFragment).commit();
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                FragmentManager manager = this.getSupportFragmentManager();
                //allSongsFragment.setBoolean(true);
                manager.beginTransaction().replace(R.id.fragmentSongOne, allSongsFragment).commit();
            }
        } else {
            if (!isFavorite) {
                //  allSongsFragment.setBoolean(false);
                FragmentManager manager = this.getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.fragmentSongOne, allSongsFragment).commit();
                FragmentManager manager1 = this.getSupportFragmentManager();
                manager1.beginTransaction().replace(R.id.fragmentMediaTwo, mediaPlaybackFragment).commit();
            } else {
                // mFavoriteSongsFragment = new FavoriteSongsFragment( mediaPlaybackService, mediaPlaybackFragment, this);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentSongOne, mFavoriteSongsFragment).commit();
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                FragmentManager manager = this.getSupportFragmentManager();
                //  allSongsFragment.setBoolean(true);
                manager.beginTransaction().addToBackStack(null).replace(R.id.fragmentMediaTwo, mediaPlaybackFragment).commit();
            }
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
    public void startService() {
        Intent intent = new Intent(this, MediaPlaybackService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    @Override
    public void onclickDisplay(Song song) {
        mediaPlaybackFragment = new MediaPlaybackFragment().newInstance(song);
        FragmentManager manager1 = this.getSupportFragmentManager();
        manager1.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentSongOne, mediaPlaybackFragment)
                .commit();
        mediaPlaybackFragment.setListSong(mListSong);
        mediaPlaybackFragment.setService(mediaPlaybackService);
        mediaPlaybackFragment.updateUI();
        if (mediaPlaybackService != null) mediaPlaybackFragment.updateTime();
        mediaPlaybackService.setmMediaPlaybackFragment(mediaPlaybackFragment);
        mediaPlaybackService.setListSong(mListSong);
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
            isFavorite = true;
/*
            if (isLandScape()) {
                Toast.makeText(this, "favorite", Toast.LENGTH_SHORT).show();
                mStatus = true;
                mFavoriteSongsFragment = new FavoriteSongsFragment(mediaPlaybackService.getListsong(), mediaPlaybackService, mediaPlaybackFragment, this);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentSongOne, mFavoriteSongsFragment).commit();
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                FragmentManager manager1 = this.getSupportFragmentManager();
                manager1.beginTransaction().replace(R.id.fragmentMediaTwo, mediaPlaybackFragment).commit();
            } else */
            {
                //    mStatus = true;
                Log.d("HoangCV", "onNavigationItemSelected: " + mediaPlaybackService);

                mFavoriteSongsFragment = new FavoriteSongsFragment(mediaPlaybackService, mediaPlaybackFragment, this);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentSongOne, mFavoriteSongsFragment).commit();
                mDrawerLayout = findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }

        } else if (id == R.id.List_music) {
            isFavorite = false;
            Log.d("HoangCVmStatus", "onNavigationItemSelected:service " + mediaPlaybackService);
            Log.d("HoangCVmStatus", "onNavigationItemSelected:listsong " + mediaPlaybackService.getListSong());
            //        mStatus = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentSongOne, allSongsFragment).commit();
            mDrawerLayout = findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        //     Log.d("HoangCVmStatus", "onNavigationItemSelected: " + mediaPlaybackService.getListsong());
        //      mUpdateUI.UpdateStatus(mStatus);
        //     mUpdateUI.UpdateArray(String.valueOf(mediaPlaybackService.getListsong()));
        return true;

    }

    //Bkav Nhungltk
    interface IConnectActivityAndBaseSong {
        void connectActivityAndBaseSong();
    }

    public boolean isLandScape() {
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else return false;
    }

    private IConnectActivityAndBaseSong iConnectActivityAndBaseSong;

    public void setiConnectActivityAndBaseSong(IConnectActivityAndBaseSong iConnectActivityAndBaseSong) {
        this.iConnectActivityAndBaseSong = iConnectActivityAndBaseSong;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permision Write File is Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permision Write File is Denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("HoangCVonStart", "onBackPressed: ");
        getSupportActionBar().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HoangCVonStart", "onResume: ");
    }

    @Override
    protected void onPause() {
        Log.d("HoangCVonStart", "onPause: ");
        super.onPause();
        setService(mediaPlaybackService);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("IS_FAVORITE", isFavorite);
        super.onSaveInstanceState(outState);
    }
}