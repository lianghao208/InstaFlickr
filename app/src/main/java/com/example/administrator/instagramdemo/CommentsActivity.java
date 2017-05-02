package com.example.administrator.instagramdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.administrator.instagramdemo.Adapter.CommentsAdapter;
import com.example.administrator.instagramdemo.View.SendCommentButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CommentsActivity extends AppCompatActivity implements SendCommentButton.OnSendClickListener{

    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.contentRoot)
    LinearLayout contentRoot;
    @InjectView(R.id.rvComment)
    RecyclerView rvComments;
    @InjectView(R.id.llAddComment)
    LinearLayout llAddComment;
    @InjectView(R.id.etComment)
    EditText etComment;
    @InjectView(R.id.btnSendComment)
    SendCommentButton btnSendComment;


    private MenuItem inboxMenuItem;
    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;
    private String mCommentString;

    private boolean pendingIntroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.inject(this);
        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);//得到上一个activity的点击item的位置
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
        setToolbar();
        setComments();
        setupSendCommentButton();
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);
    }

    /**
     * 初始化菜单栏按钮
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        inboxMenuItem = menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        return true;

        //return super.onCreateOptionsMenu(menu);
    }

    /**
     * activity回退时保留toolbar不动
     */
    @Override
    public void onBackPressed() {
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);//设置activity切换无动画，保留了toolbar
                    }
                })
                .start();
    }


    private void startIntroAnimation() {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);//设置reyclerview的父view展开动画的起始位置（中心线）为上一activity的点击位置
        llAddComment.setTranslationY(200);//设置地下发送评论的控件的Y坐标初始位置（隐藏在屏幕底下100dp为起始位置）

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    private void setComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        commentsAdapter = new CommentsAdapter(this,mCommentString);
        rvComments.setAdapter(commentsAdapter);
    }


    private void setToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            commentsAdapter.addItem(etComment.getText().toString());
                        //commentsAdapter.setAnimationsLocked(false);
                        //commentsAdapter.setDelayEnterAnimation(false);
            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());
            etComment.setText(null);
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }
    private boolean validateComment() {
                if (TextUtils.isEmpty(etComment.getText())) {
                        btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
                        return false;
                    }

                        return true;
            }
}
