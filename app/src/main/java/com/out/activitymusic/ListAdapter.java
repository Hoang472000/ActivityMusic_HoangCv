package com.out.activitymusic;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.out.activitymusic.interfaces.ItemClickListener;

import Service.MediaPlaybackService;
import es.claucookie.miniequalizerlibrary.EqualizerView;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements Filterable,PopupMenu.OnMenuItemClickListener {
    private ArrayList<Song> mListSong;
    private ArrayList<Song> listSongFull;
    private LayoutInflater mInflater;
    private View playMediaSong;
    RelativeLayout mLinearLayout;
    AllSongsFragment allSongsFragment;
    private Context mContext;
    private ItemClickListener itemClickListener;
    private ImageView image;
    private ImageView mPlayPause;
    private int mPosision,mPos;
    UpdateUI mUpdateUI;
    private MediaPlaybackService mediaPlaybackService;


    public void setService(MediaPlaybackService mediaPlaybackService){
        this.mediaPlaybackService = mediaPlaybackService;
    }

    public ListAdapter(){}
    public ListAdapter(Context context, ArrayList<Song> ListView, ItemClickListener itemClickListener) {
        mInflater = LayoutInflater.from(context);
        this.itemClickListener = itemClickListener;
        this.mListSong = ListView;
        listSongFull=new ArrayList<>(ListView);
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.list_view, parent, false);
        playMediaSong = mInflater.inflate(R.layout.allsongsfragment, parent, false);
        mLinearLayout = playMediaSong.findViewById(R.id.bottom);
        mPlayPause=playMediaSong.findViewById(R.id.play_pause);
        allSongsFragment = new AllSongsFragment();
        image= (ImageView) mItemView.findViewById(R.id.menu_pop);
        mUpdateUI= new UpdateUI(mContext);
        mPos=mUpdateUI.getCurrentPossision();
        Popmenu();
        Log.d("HoangCV6", "onCreateViewHolder: ");
        return new ViewHolder(mItemView, this);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d("HoangCV6", "onBindViewHolder: position"+position);
        Log.d("HoangCV6", "onBindViewHolder: mPosision"+mPosision);
        final Song mCurrent = mListSong.get(position);

        holder.mId.setText((position + 1) + "");
        holder.mTitle.setText(mCurrent.getTitle());
        holder.mDuration.setText(getDurationTime(mCurrent.getDuration()));

//        String mCurrent1=mListSTT.get(position);

        if(position==mPosision) {
            Log.d("HoangCV12345", "onBindViewHolder: holder: "+holder);
            holder.mId.setVisibility(View.INVISIBLE);
            holder.mTitle.setTypeface(null, Typeface.BOLD);
            holder.mEqualizer.animateBars();
            holder.mEqualizer.setVisibility(View.VISIBLE);
        }
        else {
            holder.mId.setVisibility(View.VISIBLE);
            holder.mTitle.setTypeface(null, Typeface.NORMAL);
       //     holder.mEqualizer.animateBars();
            holder.mEqualizer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public Filter getFilter() {

        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Song> filterdList = new ArrayList<>();
            if (constraint== null || constraint.length()==0){
                filterdList.addAll(listSongFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Song item: listSongFull){
                    if (item.getTitle().toLowerCase().contains(filterPattern)){
                        filterdList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values= filterdList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mListSong.clear();
            mListSong.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };



    private String getDurationTime(String str) {
        int mili = Integer.parseInt(str) / 1000;
        int phut = mili / 60;
        int giay = mili % 60;
        if (giay<10)
            return String.valueOf(phut) + ":0" + String.valueOf(giay);
        else return String.valueOf(phut) + ":" + String.valueOf(giay);
    }

    @Override
    public int getItemCount() {
        Log.d("HoangCVff", "getItemCount: "+mListSong);
        return mListSong.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public EqualizerView mEqualizer;
        public TextView mTitle;
        public TextView mDuration;
        final ListAdapter mAdapter;
        public TextView mId;
        private UpdateUI mUpdateUI;

        public ViewHolder(@NonNull View itemView, ListAdapter adapter) {
            super(itemView);
            Log.d("HoangCV6", "ViewHolder: ");
            this.mAdapter = adapter;
            mEqualizer = itemView.findViewById(R.id.equalizer);
            mId=itemView.findViewById(R.id.STT);
            mTitle = itemView.findViewById(R.id.music);
            mDuration = itemView.findViewById(R.id.tvTime);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Log.d("HoangCV62", "onClick: "+mListSong);

            mPosision=Integer.parseInt(String.valueOf(mId.getText()))-1;
            Log.d("HoangCV62", "onClick: "+mListSong.get(mPosision));
            itemClickListener.onClick(mListSong.get(mPosision));
            notifyDataSetChanged();
        }
    }

    public void Popmenu(){
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext,view);
                popup.setOnMenuItemClickListener(ListAdapter.this );
                popup.inflate(R.menu.poupup_menu);
                popup.show();
            }
        });
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Toast.makeText(mContext,"Hoang"+menuItem.getTitle(), Toast.LENGTH_SHORT).show();
        switch (menuItem.getItemId()) {
            case R.id.add_song_favorite:
                // do your code
                return true;
            case R.id.remove_song_favorite:
                // do your code
                return true;
            default:
                return false;
        }
    }



}


