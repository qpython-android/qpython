package org.qpython.qpy.main.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityNewsDetailBinding;
import org.qpython.qpy.main.adapter.NewsDetailAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.listener.LoadmoreListener;
import org.qpython.qpy.main.server.gist.GistEvent;
import org.qpython.qpy.main.server.gist.detailScreen.DetailControler;
import org.qpython.qpy.main.server.gist.detailScreen.DetailView;
import org.qpython.qpy.main.server.gist.response.CommentBean;
import org.qpython.qpy.main.server.gist.response.GistBean;
import org.qpython.qpy.main.widget.CodeReviewDialog;
import org.qpython.qpy.main.widget.ShareDialog;
import org.qpython.qpy.texteditor.EditorActivity;
import org.qpython.qpy.utils.OpenWebUtil;

import java.util.List;

import static org.qpython.qpy.main.server.gist.GistEvent.ADD_COMMENT;
import static org.qpython.qpy.main.server.gist.GistEvent.CODE_REVIEW_EVENT;
import static org.qpython.qpy.main.server.gist.GistEvent.COMMENT_EVENT;
import static org.qpython.qpy.main.server.gist.GistEvent.DIVIDER;
import static org.qpython.qpy.main.server.gist.GistEvent.FAVORITE_REQUEST;
import static org.qpython.qpy.main.server.gist.GistEvent.FORK;
import static org.qpython.qpy.main.server.gist.GistEvent.REPLY_EVENT;
import static org.qpython.qpy.main.server.gist.GistEvent.RUN_NOTEBOOK;
import static org.qpython.qpy.main.server.gist.GistEvent.RUN_SCRIPT;


/**
 * 文 件 名: GistDetailActivity
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/12/26 10:12
 * 邮   箱: qq798435167@gmail.com
 * 修改时间：
 * 修改备注：
 */

public class GistDetailActivity extends BaseActivity implements DetailView {

    private static final String GIST_ID = "gist_id";
    //    public static final  String IS_EDITABLE = "is_editable";
    private String                    id;
    private String                    gistUserName;
    private ActivityNewsDetailBinding mBinding;
    private NewsDetailAdapter         mNewsDetailAdapter;
    private ShareDialog               mShareDialog;

    private CodeReviewDialog mCodeReviewDialog;

    //    public static final String UPDATE_SHARE = "UPDATE_SHARE";
    private DetailControler mDetailControler;
    private String          toId;// reply comment
    private String          commentUserName;//reply user name

    public static void startNewsDetail(Context context, String id/*, boolean isEditable*/) {
        Intent intent = new Intent(context, GistDetailActivity.class);
        intent.putExtra(GIST_ID, id);
//        intent.putExtra(IS_EDITABLE, isEditable);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_news_detail);
        initToolbar();
        initView();
        EventBus.getDefault().register(this);
        mDetailControler = new DetailControler(this);
        id = getIntent().getStringExtra(GIST_ID);
        refresh(id);
    }

    private void initView() {
        mNewsDetailAdapter = new NewsDetailAdapter(this);

        mBinding.newsRv.setLayoutManager(new LinearLayoutManager(this));
        mBinding.newsRv.setAdapter(mNewsDetailAdapter);

        mCodeReviewDialog = new CodeReviewDialog(this);
        mShareDialog = new ShareDialog(this);

        initListener();
    }

    private void initListener() {
        mBinding.newsRv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    mBinding.commentLayout.setVisibility(View.GONE);
                    showSoftInput(false);
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        mBinding.newsRv.addOnScrollListener(new LoadmoreListener() {
            @Override
            public void OnLoadmore() {
                loadMore();
            }
        });
        mBinding.refreshLayou.setOnRefreshListener(this::refresh);
        mBinding.sendTv.setOnClickListener(v -> {
            sendComment(mBinding.commentEdit.getText().toString());
            showSoftInput(false);
            mBinding.commentLayout.setVisibility(View.GONE);
            mBinding.commentEdit.setText("");
            toId = null;
        });

        mShareDialog.setOnClickListener(index -> {
            switch (index) {
                case ShareDialog.FACEBOOK: {
                    OpenWebUtil.open(this, "https://www.facebook.com/qpython/");
                    break;
                }
                case ShareDialog.TWITTER: {
                    OpenWebUtil.open(this, "https://twitter.com/qpython");
                    break;
                }
                case ShareDialog.COPY_LINK: {
                    OpenWebUtil.open(this, "http://gist.qpy.io/share/"+getIntent().getStringExtra(GIST_ID));
                    break;
                }
            }
        });

    }

    private void refresh() {
        mDetailControler.getDetail(id);
    }

    private void loadMore() {
        mDetailControler.loadMoreComment(id);
    }

    private void initToolbar() {
        setSupportActionBar(mBinding.lt.toolbar);
        mBinding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        mBinding.lt.toolbar.setNavigationOnClickListener(v -> finish());
//        setTitle(title);
    }

    @Subscribe
    public void onEvent(GistEvent event) {
        switch (event.name) {
            case FORK:
                ifLogin(this::fork);
                break;
            case COMMENT_EVENT: {
                ifLogin(() -> {
                    mBinding.commentLayout.setVisibility(View.VISIBLE);
                    mBinding.commentEdit.requestFocus();
                    showSoftInput(true);
                });
                break;
            }

            case FAVORITE_REQUEST: {
                ifLogin(() -> mDetailControler.favoriteGist(id));
                break;
            }

            case RUN_SCRIPT: {
                String[] titleCode = event.content.split(DIVIDER);
                EditorActivity.start(this, titleCode[1], titleCode[0]);
                break;
            }

            case RUN_NOTEBOOK: {
                // TODO: 2018/3/12 打开notebook
                break;
            }

            case CODE_REVIEW_EVENT: {
                mCodeReviewDialog.show();
                break;
            }

            case REPLY_EVENT: {
                ifLogin(() -> {
                    toId = event.content.split(",")[0];
                    commentUserName = event.content.split(",")[1];
                    mBinding.commentLayout.setVisibility(View.VISIBLE);
                    mBinding.commentEdit.requestFocus();
                    showSoftInput(true);
                });
                break;
            }

        }

    }

    /**
     * 发送评论
     */
    private void sendComment(String s) {
        if (s.isEmpty()) {
            Toast.makeText(this, R.string.empty_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        mDetailControler.comment(id, s, toId);
    }

    private void showSoftInput(boolean show) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            if (commentUserName != null) {
                mBinding.commentEdit.setHint(getString(R.string.reply_to, commentUserName));
            }
            imm.showSoftInput(mBinding.commentEdit, 0);
        } else {
            commentUserName = null;
            toId = null;
            mBinding.commentEdit.setHint("");
            imm.hideSoftInputFromWindow(mBinding.commentEdit.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu, menu);

//        if (!getIntent().getBooleanExtra(IS_EDITABLE, false)) {
        MenuItem item = menu.findItem(R.id.news_edit);
        if (App.getUser() != null && App.getUser().getUserName().equals(gistUserName)) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.news_share:
                mShareDialog.show();
                mBinding.commentLayout.setVisibility(View.GONE);
                break;
            case R.id.news_edit:
                mNewsDetailAdapter.startEdit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mDetailControler.onDestroy();
    }

    @Override
    public void showLoading() {
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mBinding.progressBar.setVisibility(View.GONE);
        mBinding.refreshLayou.setRefreshing(false);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setData(GistBean gistBean) {
        setTitle(gistBean.getTitle());
        mNewsDetailAdapter.setData(gistBean);
        mCodeReviewDialog.setContent(gistBean.getSource());
        gistUserName = gistBean.getUser().getUserName();
        invalidateOptionsMenu();
    }

    @Override
    public void loadMoreComments(List<CommentBean> list) {
        mNewsDetailAdapter.loadMoreComment(list);
    }

    @Override
    public void favorite(boolean is) {
        mNewsDetailAdapter.favorite(is);
    }

    @Override
    public void addComment(CommentBean comment) {
        mNewsDetailAdapter.addComment(comment);
        EventBus.getDefault().post(new GistEvent(ADD_COMMENT, id));
    }

    @Override
    public void fork() {
        mDetailControler.forkGist(id);
    }

    @Override
    public void forkSuccess() {
        MyGistActivity.startMyShare(this);
//        Toast.makeText(this, R.string.fork_suc, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refresh(String id) {
        this.id = id;
        refresh();
    }
}
