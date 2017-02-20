package com.tchip.autoplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tchip.autoplayer.R;
import com.tchip.autoplayer.model.Video;
import com.tchip.autoplayer.view.VideoView;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Administrator on 2016/10/31.
 */
public class VideoAdapter extends BaseAdapter {

    private Context context;
    private LinkedList<Video> videoLinkedList;
    private LayoutInflater layoutInflater;


    public VideoAdapter(Context context, LinkedList<Video> videoLinkedList) {
        this.context = context;
        this.videoLinkedList = videoLinkedList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return videoLinkedList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoLinkedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final VideoViewHolder videoViewHolder;
        if (convertView == null) {
            videoViewHolder = new VideoViewHolder();
            convertView = layoutInflater.inflate(R.layout.video_list_item, null);
            videoViewHolder.textName = (TextView) convertView.findViewById(R.id.textName);
            videoViewHolder.textPath = (TextView) convertView.findViewById(R.id.textPath);

            convertView.setTag(videoViewHolder);
        } else {
            videoViewHolder = (VideoViewHolder) convertView.getTag();
        }
        Video video = videoLinkedList.get(position);
        videoViewHolder.textName.setText(video.getName());
        videoViewHolder.textPath.setText(video.getPath());

        return convertView;
    }

    class VideoViewHolder {
        TextView textName;
        TextView textPath;
    }
}
