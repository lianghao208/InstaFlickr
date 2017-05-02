package com.example.administrator.instagramdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.administrator.instagramdemo.Adapter.FeedAdapter;
import com.example.administrator.instagramdemo.Item.GalleryItem;
import com.example.administrator.instagramdemo.Manager.FeedContextMenuManager;
import com.example.administrator.instagramdemo.View.FeedContextMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements FeedAdapter.OnFeedItemClickListener
        ,FeedContextMenu.OnFeedContextMenuItemClickListener{

    private DrawerLayout drawerLayout;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;
    @InjectView(R.id.btnCreate)
    ImageButton btnCreate;
    @InjectView(R.id.ivLogo)
    ImageView ivLogo;


    private MenuItem inboxMenuItem;
    private FeedAdapter feedAdapter;


    private boolean pendingIntroAnimation;
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private List<GalleryItem> mItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
        setupToolbar();
        //setupDrawerLayout();
        setupFeed();
        //开启后台线程加载图片
        new FetchItemsTask().execute();



    }

    private void setupToolbar() {
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
    }

    private void setupDrawerLayout() {


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);//侧滑菜单
        int openDrawerContentDescRes = R.string.open;  //菜单显示时的中文描述
        int closeDrawerContentDescRes = R.string.close; //菜单关闭时的中文描述
        ActionBarDrawerToggle arrowBtn = new ActionBarDrawerToggle(this, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        //设置显示条件为true
        ActionBar actionBar = getSupportActionBar();
        //设置显示返回箭头
        actionBar.setDisplayHomeAsUpEnabled(true);
        //设置显示三横杠
        arrowBtn.syncState();
        //添加菜单拖动监听事件  根据菜单的拖动距离 将距离折算成旋转角度
        drawerLayout.addDrawerListener(arrowBtn);
        //处理旋转按钮的点击事件。注意这个控件ID为 android.R.id.home
    }


    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {//预加载
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);
        feedAdapter = new FeedAdapter(this,mItems);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
          }
      });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        inboxMenuItem = menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;

        //return super.onCreateOptionsMenu(menu);
    }

    private void startIntroAnimation() {
        btnCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
        int actionbarSize = Utils.dpToPx(56);//像素转换
        toolbar.setTranslationY(-actionbarSize);//toolbar向上移动56像素
        ivLogo.setTranslationY(-actionbarSize);//toolbar上的标题图片也向上移动56像素
        inboxMenuItem.getActionView().setTranslationY(-actionbarSize);//整个actionbar向上移动56像素

        /**
         * toolbar菜单栏整体下移动画
         * */

        toolbar.animate()
                .translationY(0)//
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        ivLogo.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        inboxMenuItem.getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {//动画结束后执行
                        startContentAnimation();
                    }
                })
                .start();

    }

    private void startContentAnimation() {
        //设置FAB右下角圆按钮的出现动画
        btnCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))//设置运动效果（向前甩再回来）
                .setDuration(ANIM_DURATION_FAB)
                .setStartDelay(300)
                .start();

        //设置主界面RecyclerView出现的动画
        feedAdapter.updateItems();

    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        //创建整数数组接收当前item在屏幕的位置，以便于在该位置实现展开动画进入下一个activity
        int[] startLocation = new int[2];
        v.getLocationOnScreen(startLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION,startLocation[1]);

        startActivity(intent);
        //Disable enter transition for new Acitvity
        overridePendingTransition(0, 0);//第一个activity退出时的动画和第二个activity进入时的动画都设为0，即无动画效果
    }

    @Override
    public void onLikeClick(View v, int position) {

    }

    @Override
    public void onMoreClick(View v, int position) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, this);

    }

    @Override
    public void onFeedCenterClick(View v, int position) {

    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }


    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_inbox:
                Toast.makeText(this,"ok",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }*/

    /**
     * 创建AsyncTask工具内部类
     */
    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            rvFeed.setAdapter(new FeedAdapter(getApplicationContext(),mItems));
            //成功加载则更新UI:刷新进度圈隐藏
            //mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
