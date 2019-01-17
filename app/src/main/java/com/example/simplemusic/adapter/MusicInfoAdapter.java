package com.example.simplemusic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.simplemusic.R;
import com.example.simplemusic.bean.dbMusicInfo;
import com.example.simplemusic.bean.qqMusicInfo;
import com.example.simplemusic.util.TimeUtil;
import com.example.simplemusic.widget.RecyclerExtras.OnItemClickListener;

import java.util.ArrayList;

public class MusicInfoAdapter extends BaseAdapter implements OnClickListener{

    private LayoutInflater mInflater;
    private Context mContext;
    private int mLayoutId;
    private ArrayList<qqMusicInfo> mMusicList;

    public MusicInfoAdapter(Context context, int layout_id, ArrayList<qqMusicInfo> Music_list){
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mLayoutId = layout_id;
        mMusicList = Music_list;
    }

    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mMusicList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(mLayoutId, null);
            holder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_music_info);
            holder.music_name = (TextView) convertView.findViewById(R.id.music_name);
            holder.music_detail = (TextView) convertView.findViewById(R.id.music_detail);
            holder.music_time = (TextView) convertView.findViewById(R.id.music_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        qqMusicInfo music = mMusicList.get(position);
        holder.music_name.setText(music.mMusicName);
        String detail = music.mMusicSinger;
        if(!music.mMusicAlbum.equals("")){
            detail += "   专辑:《" + music.mMusicAlbum+ "》";
        }
        holder.music_detail.setText(detail);
        String time;
        time = TimeUtil.getTimeMMss(music.mMusicTime);
        if(time.equals("0:00")) time = "";
        holder.music_time.setText(time);
        holder.ll_item.setId(music.mIndex);
        holder.ll_item.setOnClickListener(this);
        return convertView;
    }

    public final class ViewHolder{
        private LinearLayout ll_item;
        public TextView music_name;
        public TextView music_detail;
        public TextView music_time;
    }



    @Override
    public void onClick(View v){
        if (mOnItemClickListener != null) {
            Log.d("musicInfoAdapter", String.valueOf(v.getId()));
            mOnItemClickListener.onItemClick(v, v.getId());
        }
        //点击后播放

    }


    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}

