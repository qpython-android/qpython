package org.qpython.qpy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.quseit.util.ImageDownLoader;

import org.qpython.qpy.BR;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityCourseIndexBinding;
import org.qpython.qpy.main.server.model.CourseModel;

import java.util.List;


public class CourseIndexActivity extends AppCompatActivity {
    private static final String COURSE = "COURSE";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void start(Activity context, Pair<View, String>[] pairs, CourseModel courseModel) {
        Intent starter = new Intent(context, CourseIndexActivity.class);
        starter.putExtra(COURSE, courseModel);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(context, pairs);
        context.startActivity(starter, options.toBundle());
    }

    public static void start(Context context, CourseModel courseModel) {
        Intent starter = new Intent(context, CourseIndexActivity.class);
        starter.putExtra(COURSE, courseModel);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCourseIndexBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_course_index);
        CourseModel course = (CourseModel) getIntent().getSerializableExtra(COURSE);
        binding.setCourse(course);
        binding.item.free.setVisibility(View.GONE);

        setSupportActionBar(binding.tl.toolbar);
        setTitle(course.getTitle());
        binding.tl.toolbar.setNavigationIcon(R.drawable.ic_back);
        binding.tl.toolbar.setNavigationOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        });

        binding.item.tvLevel.setText(getString(R.string.level, course.getLevel() + ""));
        binding.item.tvView.setText(String.valueOf(course.getDownloads()));
        binding.item.tvTitle.setText(course.getTitle());
        binding.item.tvDate.setText(course.getRdate());
        ImageDownLoader.setImageFromUrl(this, binding.item.ivTheme, course.getLogo());

        ArrayAdapter<CourseModel.CoursesBean> adapter = new ArrayAdapter<>(this, R.layout.item_course_index, course.getCourses());
        binding.rvIndex.setAdapter(adapter);
        binding.rvIndex.setOnItemClickListener((parent, view, position, id) ->
                QWebViewActivity.start(this, course.getCourses().get(position).getTitle(), course.getCourses().get(position).getLink()));

        //binding.item.ivTheme.setOnClickListener(v -> QWebViewActivity.start(this, course.getTitle(), course.getLink()));
    }
}
