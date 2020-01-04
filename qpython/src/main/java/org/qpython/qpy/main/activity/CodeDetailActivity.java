package org.qpython.qpy.main.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import org.qpython.qpy.R;
import org.qpython.qpy.codeshare.pojo.Gist;
import org.qpython.qpy.databinding.ActivityCodeDetailBinding;
import org.qpython.qpy.main.adapter.CodeCommentAdapter;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.widget.CodeSendComment;
import org.qpython.qpy.texteditor.EditorActivity;

import java.util.ArrayList;
import java.util.List;


public class CodeDetailActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private static final String GIST_ID      = "gist_id";
    private static final String IS_PROJ      = "is_proj";
    private static final int    SCROOL_RANGE = 30;
    private ActivityCodeDetailBinding binding;
    private List<Gist.CommentBean>    comments;
    private CodeCommentAdapter        adapter;
    private CodeSendComment           sendDialog;
    private String                    gistId;
    private boolean                   isProj;

    public static void start(Context context, String gistId, boolean isProj) {
        Intent starter = new Intent(context, CodeDetailActivity.class);
        starter.putExtra(GIST_ID, gistId);
        starter.putExtra(IS_PROJ, isProj);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gistId = getIntent().getStringExtra(GIST_ID);
        isProj = getIntent().getBooleanExtra(IS_PROJ, false);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_code_detail);
        setSupportActionBar(binding.lt.toolbar);
        setTitle(R.string.community);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(v -> finish());
        if (App.getUser() == null) {
            return;
        }
        sendDialog = CodeSendComment.newInstance(gistId, isProj);
        sendDialog.setCallback(comment -> {
            if (binding.emptyHint.getVisibility() == View.VISIBLE) {
                binding.emptyHint.setVisibility(View.GONE);
                comments = new ArrayList<>();
                adapter = new CodeCommentAdapter(comments);
                adapter.setItemClickListener((to, reComment) -> sendDialog.setReply(to, reComment));
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(CodeDetailActivity.this));
                binding.recyclerView.setAdapter(adapter);
            }
            comments.add(0, comment);
            adapter.notifyItemInserted(0);
            binding.commentCount.setText(Integer.parseInt(binding.commentCount.getText().toString()) + 1 + "");
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        sendDialog.setCancelable(false);
        sendDialog.show(ft, "dialog");
    }

    private void initData() {
//        ShareCodeUtil.getInstance().getGistDetail(gist_id, isProj, gist -> {
//            binding.name.setText(gist.getAuthor());
//            binding.title.setText(gist.getTitle());
//            binding.description.setText(gist.getDescribe());
//            binding.bookmarkCount.setText(gist.getBookmarkerCount());
//            binding.commentCount.setText(gist.getCommentCount());
//            binding.code.setText(gist.getLastCommitCode());
//            ImageDownLoader.setImageFromUrl(CodeDetailActivity.this, binding.avatar, gist.getAvatar());
//
//            if (gist.getFrom_content() != null) {
//                comments = new ArrayList<>();
//                comments.addAll(gist.getFrom_content());
//                adapter = new CodeCommentAdapter(comments);
//                adapter.setItemClickListener((to, reComment) -> sendDialog.setReply(to, reComment));
//                binding.recyclerView.setLayoutManager(new LinearLayoutManager(CodeDetailActivity.this));
//                binding.recyclerView.setAdapter(adapter);
//                binding.recyclerView.setNestedScrollingEnabled(false);
//            } else {
//                binding.emptyHint.setVisibility(View.VISIBLE);
//            }
//        });
    }

    private void initListener() {
        binding.btnCopy.setOnClickListener(v -> EditorActivity.start(this, binding.code.getText().toString()));
        binding.bookmarkCount.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
//            if (v.isSelected()) {
//                ShareCodeUtil.getInstance().bookmark(gist_id);
//                binding.bookmarkCount.setText(Integer.parseInt(binding.bookmarkCount.getText().toString()) + 1 + "");
//                binding.bookmarkCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_com_liked, 0, 0, 0);
//            } else {
//                ShareCodeUtil.getInstance().cancelBookmark(gist_id);
//                binding.bookmarkCount.setText(Integer.parseInt(binding.bookmarkCount.getText().toString()) - 1 + "");
//                binding.bookmarkCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_com_like, 0, 0, 0);
//            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        finish();
    }
}
