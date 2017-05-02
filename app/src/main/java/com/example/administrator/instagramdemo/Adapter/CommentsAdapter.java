package com.example.administrator.instagramdemo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.instagramdemo.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2017/4/18.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String mCommentString;

    private int itemsCount = 10;

    public CommentsAdapter(Context context,String commentString){
        this.mContext = context;
        this.mCommentString = commentString;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentViewHolder viewHolder = (CommentViewHolder) holder;
        if(position<10){
            switch(position%3){
                case 0:
                    viewHolder.tvComment.setText("comment1");
                    break;
                case 1:
                    viewHolder.tvComment.setText("comment2");
                    break;
                case 2:
                    viewHolder.tvComment.setText("comment3");
                    break;
            }
        }else{
            viewHolder.tvComment.setText(mCommentString);//更新评论字符串
        }

        Glide.with(mContext)
                .load(R.drawable.ic_launcher)
                .centerCrop()
                .into(viewHolder.ivUserAvatar);
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public void updateItems() {//更新RecyclerView的Item时的动画效果
        itemsCount = 10;
        notifyDataSetChanged();
    }

    public void addItem(String commentString) {
        this.mCommentString = commentString;
        itemsCount++;
        notifyItemInserted(itemsCount - 1);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivUserAvatar)
        ImageView ivUserAvatar;
        @InjectView(R.id.tvComment)
        TextView tvComment;



        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
