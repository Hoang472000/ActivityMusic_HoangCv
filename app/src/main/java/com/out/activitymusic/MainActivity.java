package com.out.activitymusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;


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

import android.view.View;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import Service.ServiceMediaPlay;

public class MainActivity extends AppCompatActivity implements DisplayMediaFragment,DataFragment{
    public static IntentFilter Broadcast_PLAY_NEW_AUDIO;
    String PRIVATE_MODE ="color" ;
    AllSongsFragment allSongsFragment;
    MediaPlaybackFragment mediaPlaybackFragment;
    public ServiceMediaPlay getPlayer() {
        return serviceMediaPlay;
    }
    public ServiceMediaPlay serviceMediaPlay;
    boolean serviceBound = false;
    private Song song;
    private ArrayList<Song> mListSong;
    SharedPreferences sharedPreferences;
    private DrawerLayout mDrawerLayout;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServiceMediaPlay.LocalBinder binder = (ServiceMediaPlay.LocalBinder) service;
            serviceMediaPlay = binder.getService();
//            mListSong= new ArrayList<>();
            serviceMediaPlay.setListSong(mListSong);
            Log.d("HoangCV7", "onSaveInstanceState: "+ serviceMediaPlay);

            Log.d("nhungltk", "onServiceConnected: "+ serviceMediaPlay);
            //Bkav Nhungltk: tai sao lai thuc hien connect o day
            iConnectActivityAndBaseSong.connectActivityAndBaseSong();
            serviceBound = true;

                allSongsFragment.setService(serviceMediaPlay);



            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    private int possision;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        UpdateUI updateUI=new UpdateUI(getApplicationContext());
       // updateUI.UpdateSeekbar();
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        possision = savedInstanceState.getInt("possision");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            //player.stopSelf();
        }
    }
    public void setSong(Song songs) {
        this.song = songs;
    }

    public void FileSong(Song song) {
        song.getFile();
    }


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlaybackFragment = new MediaPlaybackFragment();


        //final ListView list = findViewById(R.id.list_view);
        int orientation = this.getResources().getConfiguration().orientation;
        allSongsFragment = new AllSongsFragment(this, this.mediaPlaybackFragment, this);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            FragmentManager manager = this.getSupportFragmentManager();
            allSongsFragment.setBoolean(true);
            manager.beginTransaction()
                    .replace(R.id.fragmentSongOne, allSongsFragment)
                    .commit();
        } else {
            allSongsFragment.setBoolean(false);
            FragmentManager manager = this.getSupportFragmentManager();

            manager.beginTransaction()
                    .replace(R.id.fragmentSongOne, allSongsFragment)
                    .commit();

            FragmentManager manager1 = this.getSupportFragmentManager();

            manager1.beginTransaction()
                    .replace(R.id.fragmentMediaTwo, mediaPlaybackFragment)

                    .commit();
        }
        Intent intent = new Intent(this, ServiceMediaPlay.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        mediaPlaybackFragment.setService(serviceMediaPlay);
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

      /*  mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                });
    *//*    Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.default_cover_art);*//*
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
   }

//    private void setSupportActionBar(Toolbar toolbar) {
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
   }*/
    }


    @Override
    public void onclick(Song song) {
        Log.d("HoangCV7", "onSaveInstanceState: "+ serviceMediaPlay);
         mediaPlaybackFragment = new MediaPlaybackFragment().newInstance(song);
        FragmentManager manager1 = this.getSupportFragmentManager();
        manager1.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragmentSongOne, mediaPlaybackFragment)
                .commit();
        mediaPlaybackFragment.setService(serviceMediaPlay);
        mediaPlaybackFragment.setListSong(mListSong);
        serviceMediaPlay.setMediaPlaybackFragment(mediaPlaybackFragment);
    }

    @Override
    public void onclickData(ArrayList ListSong) {
        this.mListSong=ListSong;

    }
    public void setService(ServiceMediaPlay service){
        this.serviceMediaPlay =service;
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
        Log.d("HoangCV7", "onResume: "+getPlayer());
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("HoangCV7", "onPause: "+ serviceMediaPlay);
        setService(serviceMediaPlay);
    }
}