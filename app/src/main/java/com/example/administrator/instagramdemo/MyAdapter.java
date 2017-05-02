package com.example.administrator.instagramdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2017/4/17.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public MyAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_feed,parent,false);
        return new CellMyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CellMyViewHolder cellMyViewHolder = (CellMyViewHolder) holder;
        cellMyViewHolder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_1);
        cellMyViewHolder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class CellMyViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @InjectView(R.id.ivFeedBottom)
        ImageView ivFeedBottom;

        public CellMyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

}
