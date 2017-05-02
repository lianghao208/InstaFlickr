package com.example.administrator.instagramdemo.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.administrator.instagramdemo.Item.GalleryItem;
import com.example.administrator.instagramdemo.R;
import com.example.administrator.instagramdemo.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2017/4/15.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CellFeedViewHolder> implements View.OnClickListener {

    private static final int ANIMATED_ITEMS_COUNT = 2;//每个屏幕最多装两个items

    private static OnFeedItemClickListener onFeedItemClickListener;

    private Context mContext;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 30;

    private List<GalleryItem> mGalleryItems;
    private static Map<Integer, Integer> likesCount = new HashMap<>();


    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private static ArrayList<Integer> likedPositions = new ArrayList<>();


    public FeedAdapter(Context context,List<GalleryItem> items){
        this.mContext = context;
        this.mGalleryItems = items;
    }
    @Override
    public FeedAdapter.CellFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_feed, parent, false);
        return new CellFeedViewHolder(view);
    }

    private void runEnterAnimation(View view, int position) {
        if(position >= ANIMATED_ITEMS_COUNT - 1){//加载的item数大于等于屏幕所能装下的item数则动画加载完成(第一次加载前两个item采用动画效果)
            return;
        }
        if(position > lastAnimatedPosition){//lastAnimatedPosition初始值为-1，随position增加不断更新最后一个position的值
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(mContext));//起始的Y点坐标为屏幕最下方
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    @Override
    public void onBindViewHolder(CellFeedViewHolder holder, int position) {

        GalleryItem galleryItem = mGalleryItems.get(position);

        runEnterAnimation(holder.itemView,position);

        holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_1);
        Glide.with(mContext)
                .load(galleryItem.getmUrl())
                . placeholder(R.drawable.school)
                .error(R.mipmap.ic_launcher)
                .into(holder.ivFeedCenter);
        holder.tvFeedBottom.setText(galleryItem.getmTitle());
        holder.tvOwnerName.setText(galleryItem.getmOwnername());
        if (position % 2 == 0) {
            //holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_1);
            holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
        } else {
            //holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_2);
            holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_2);
        }
        updateLikesCounter(holder, false);//第一次加载布局，更新点赞数不使用动画
        updateHeartButton(holder, false);
        holder.btnComment.setOnClickListener(this);//设置评论按钮监听器
        holder.btnComment.setTag(position);

        holder.btnLike.setOnClickListener(this);//设置点赞按钮监听器
        holder.btnLike.setTag(holder);

        holder.btnMore.setOnClickListener(this);//设置更多菜单按钮监听器
        holder.btnMore.setTag(position);

        holder.ivFeedBottom.setOnClickListener(this);//设置评论图片按钮监听器
        holder.ivFeedBottom.setTag(position);

        //holder.ivFeedCenter.setOnClickListener(this);//设置图片按钮监听器（无效）
        //holder.ivFeedCenter.setTag(holder);
        if (likeAnimations.containsKey(holder)) {
                        likeAnimations.get(holder).cancel();
        }
        resetLikeAnimationState(holder);
    }

    @Override
    public int getItemCount() {
        return mGalleryItems.size();
        //return itemsCount;
    }

    @Override
    public void onClick(View v) {
        if (onFeedItemClickListener != null) {
            switch (v.getId()){
                case R.id.btnMore:
                    onFeedItemClickListener.onMoreClick(v, (Integer) v.getTag());
                    //Toast.makeText(mContext, "more", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btnComments:
                    onFeedItemClickListener.onCommentsClick(v, (Integer) v.getTag());
                    break;
                case R.id.btnLike:
                    updateLikeView(v);
                    break;

                case R.id.ivFeedBottom:
                    Toast.makeText(mContext, "bottom", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.ivFeedCenter:
                    updateLikeView(v);
                    break;
                default: Toast.makeText(mContext, "no", Toast.LENGTH_SHORT).show();

            }
        }


    }

    private void updateLikeView(View v) {

        CellFeedViewHolder holder = (CellFeedViewHolder) v.getTag();
        if (!likedPositions.contains(holder.getPosition())) {
            likedPositions.add(holder.getPosition());
            updateHeartButton(holder, true);
        }else{
            likedPositions.remove(likedPositions.indexOf(holder.getPosition()));
            updateHeartButton(holder, false);

        }
        updateLikesCounter(holder, true);
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener feedItemClickListener) {
        onFeedItemClickListener = feedItemClickListener;

    }


    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @InjectView(R.id.ivFeedBottom)
        ImageView ivFeedBottom;
        @InjectView(R.id.btnComments)
        ImageButton btnComment;
        @InjectView(R.id.btnLike)
        ImageButton btnLike;
        @InjectView(R.id.btnMore)
        ImageButton btnMore;
        @InjectView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @InjectView(R.id.tvFeedBottom)
        TextView tvFeedBottom;
        @InjectView(R.id.tvOwnerName)
        TextView tvOwnerName;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public void updateItems() {//更新RecyclerView的Item时的动画效果
        itemsCount = 30;
        fillLikesWithRandomValues();
        notifyDataSetChanged();

    }

    private void updateLikesCounter(CellFeedViewHolder holder, boolean animated) {
        int position = holder.getPosition();
        if(position>=itemsCount){
            position = position - itemsCount;
        }
        int currentLikesCount;
        if(likedPositions.contains(holder.getPosition()) && animated) {//判断是点赞还是取消赞还是第一次加载布局
            currentLikesCount = likesCount.get(position) + 1;
        }else if(!likedPositions.contains(holder.getPosition())&& animated){
            currentLikesCount = likesCount.get(position) - 1;
        }else{
            currentLikesCount = likesCount.get(position);
        }

        String likesCountText = mContext.getResources().getQuantityString(//设置量词属性:赞数为1则like，赞数大于一则likes
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );

        if (animated) {
            holder.tsLikesCounter.setText(likesCountText);
        } else {
            holder.tsLikesCounter.setCurrentText(likesCountText);
        }

        likesCount.put(position, currentLikesCount);
    }

    private void fillLikesWithRandomValues() {//赞的初始值为随机数
                for (int i = 0; i < itemsCount; i++) {
                        likesCount.put(i, new Random().nextInt(100));
                    }
    }

    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }
    }

    private void resetLikeAnimationState(CellFeedViewHolder holder) {
        likeAnimations.remove(holder);
        //holder.vBgLike.setVisibility(View.GONE);
        //holder.ivLike.setVisibility(View.GONE);
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);
        void onLikeClick(View v, int position);
        void onMoreClick(View v, int position);
        void onFeedCenterClick(View v, int position);
    }

}
