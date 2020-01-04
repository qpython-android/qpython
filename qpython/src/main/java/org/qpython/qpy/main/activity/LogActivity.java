package org.qpython.qpy.main.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.quseit.util.FileHelper;

import org.qpython.qpy.R;
import org.qpython.qpy.databinding.AboutBinding;
import org.qpython.qpysdk.utils.DateTimeHelper;

/**
 * LogUtil Activity
 * Created by Hmei on 2017-06-22.
 */

public class LogActivity extends BaseActivity {

    public static final String LOG_TITLE = "title";
    public static final String LOG_PATH = "path";

    AboutBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.about);
        setSupportActionBar(binding.lt.toolbar);
        setTitle(R.string.log_title);
        binding.lt.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.lt.toolbar.setNavigationOnClickListener(view -> finish());

        binding.title.setText(getIntent().getStringExtra(LOG_TITLE));
        binding.path.setText(getIntent().getStringExtra(LOG_PATH));
        //binding.time.setText(getString(R.string.date_format, DateTimeHelper.getDate()));
        String content = FileHelper.getFileContents(getIntent().getStringExtra(LOG_PATH));
        binding.content.setText(content);

//        binding.ibMail.setOnClickListener(v -> {
//
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_more:
                binding.path.setText(getIntent().getStringExtra(LOG_PATH));
                String content = FileHelper.getFileContents(getIntent().getStringExtra(LOG_PATH));
                onFeedback(content);
//                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.ui_feedback_mail)));
//                String body = MessageFormat.format(getString(com.quseit.android.R.string.feedback_email_body), Build.PRODUCT,
//                        Build.VERSION.RELEASE, Build.VERSION.SDK_INT, binding.content.getContext());
//                String subject = getString(R.string.feedback_email_subject);
//                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//                intent.putExtra(Intent.EXTRA_TEXT, body);
//                try {
//                    startActivity(Intent.createChooser(intent,
//                            getString(R.string.email_transcript_chooser_title)));
//                } catch (ActivityNotFoundException e) {
//                    Toast.makeText(this,
//                            R.string.email_transcript_no_email_activity_found,
//                            Toast.LENGTH_LONG)
//                            .show();
//                }

        }
        return true;
    }
}