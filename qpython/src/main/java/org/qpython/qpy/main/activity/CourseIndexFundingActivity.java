package org.qpython.qpy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.quseit.util.ImageDownLoader;
import com.quseit.util.VeDate;
import com.quseit.util.NAction;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ActivityCourseCrowdfundingBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.MySubscriber;
import org.qpython.qpy.main.server.model.CourseModel;
import org.qpython.qpy.main.server.model.PayStatusModel;
import org.qpython.qpy.main.widget.scheduleview.DotObj;
import org.qpython.qpy.utils.UpdateHelper;

import java.util.Map;


public class CourseIndexFundingActivity extends AppCompatActivity {
    private static final int SUPPORT_LOGIN_REQUEST_CODE = 2053;
    private static final int COURSE_LOGIN_REQUEST_CODE  = 2030;

    private static final String START_TO_LEARN_TYPE = "course_free";
    private static final String COMMENT_HTML        = "http://edu.qpython.org/comments/%s.html?from=app";
    private static final String STATUS              = "status";
    private static final String COURSE              = "COURSE";

    private ActivityCourseCrowdfundingBinding binding;

    private CourseModel course;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void start(Activity context, Pair<View, String>[] pairs, CourseModel courseModel, int status) {
        Intent starter = new Intent(context, CourseIndexFundingActivity.class);
        starter.putExtra(COURSE, courseModel);
        starter.putExtra(STATUS, status);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(context, pairs);
        context.startActivity(starter, options.toBundle());
    }

    public static void start(Context context, CourseModel courseModel, int status) {
        Intent starter = new Intent(context, CourseIndexFundingActivity.class);
        starter.putExtra(COURSE, courseModel);
        starter.putExtra(STATUS, status);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_crowdfunding);
        course = (CourseModel) getIntent().getSerializableExtra(COURSE);
        binding.setCourse(course);
        initToolbar();
        initViewContent();
        initListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_comment:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(COMMENT_HTML, course.getSmodule()))));
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initToolbar() {
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
    }

    private void initViewContent() {
        binding.tvLevel.setText(getString(R.string.level, course.getLevel() + ""));
        binding.supportBtn.setText(binding.schedule.getVisibility() == View.VISIBLE ? R.string.support : R.string.buy_course);
        ImageDownLoader.setImageFromUrl(this, binding.authorAvatar, course.getAuth_avatar());
        ImageDownLoader.setImageFromUrl(this, binding.ivTheme, course.getLogo());

        ArrayAdapter<CourseModel.CoursesBean> adapter = new ArrayAdapter<>(this, R.layout.item_course_funding_index, course.getCourses());
        binding.rvIndex.setAdapter(adapter);

        if (getIntent().getIntExtra(STATUS, 0) == 0 || getIntent().getIntExtra(STATUS, 0) == 4) {
            //[0,不进行众筹/1,初级课程众筹/2, 中级课程众筹/3,高级课程众筹/4, 众筹完毕]
            binding.schedule.setVisibility(View.GONE);
            binding.explanation.setVisibility(View.GONE);
            binding.more.setVisibility(View.GONE);
            binding.supportBtn.setVisibility(View.GONE);

            binding.thumbBtn.setVisibility(View.VISIBLE);
            binding.supportPercent.setText(R.string.support_explanation);
            binding.rvIndex.setOnItemClickListener((parent, view, position, id) ->
                    QWebViewActivity.start(this, course.getCourses().get(position).getTitle(), course.getCourses().get(position).getLink()));
        } else {
            binding.thumbBtn.setVisibility(View.GONE);
            binding.schedule.setDotObj(DotObj.getDefaultList(this));
            binding.schedule.setPercent(course.getFunding_process());

            String[] fundingPrice = getResources().getStringArray(R.array.funding_price);
            binding.moreText.setText(getString(R.string.explanation_more, fundingPrice[0],
                    fundingPrice[1],
                    fundingPrice[2],
                    fundingPrice[3]));
            initSupportCount();
        }
    }

    private void initSupportCount() {
        App.getService().getArticleSupportNum(course.getSmodule(), new MySubscriber<Object>() {
            @Override
            public void onNext(Object o) {
                String num = ((Double) ((Map) o).get("number")).intValue() + "";
                if (TextUtils.isEmpty(num)) {
                    binding.supportPercent.setVisibility(View.GONE);
                } else {
                    binding.supportPercent.setVisibility(View.VISIBLE);
                    binding.supportPercent.setText(getString(R.string.support_count, num));
                }
            }
        });
    }

    View.OnClickListener recordNIndex = (v -> {
        if (App.getUser() != null) {
            uploadMyCourse();
            QWebViewActivity.start(this, course.getTitle(), course.getLink());
        } else {
            startActivityForResult(new Intent(this, SignInActivity.class), COURSE_LOGIN_REQUEST_CODE);
        }
    });

    View.OnClickListener backThisProjectUnlogin = (v -> {

        startActivityForResult(new Intent(this, SignInActivity.class), SUPPORT_LOGIN_REQUEST_CODE);
    });

    private void initListener() {
        binding.more.setOnClickListener(v -> {
            binding.explanationMore.toggle();
            binding.more.setText(binding.explanationMore.isExpanded() ? R.string.more : R.string.hide);
        });

        //binding.ivTheme.setOnClickListener(v -> QWebViewActivity.start(this, course.getTitle(), course.getLink()));

        binding.supportBtn.setOnClickListener(v -> {
            if (App.getUser() != null) {
                FundingPurchaseActivity.startSupport(this, course.getSmodule(), course.getFunding_process());
            } else {
                startActivityForResult(new Intent(this, SignInActivity.class), SUPPORT_LOGIN_REQUEST_CODE);
            }
        });

        binding.thumbBtn.setOnClickListener(v -> PurchaseActivity.start(this, course.getSmodule()));

        switch (getIntent().getIntExtra(STATUS, 0)) {
            //[0,不进行众筹/1,初级课程众筹/2, 中级课程众筹/3,高级课程众筹/4, 众筹完毕]
            case 0:
                binding.btnStartLearn.setOnClickListener(recordNIndex);
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                checkIsPayed();
                break;
        }
    }

    private void checkIsPayed() {
        if (App.getUser() != null) {
            App.getService().getPayStatus(App.getUser().getEmail(), course.getSmodule(), new
                    MySubscriber<PayStatusModel>() {
                        @Override
                        public void onNext(PayStatusModel o) {
                            if (o.getPayed() > 0) {
                                binding.btnStartLearn.setText(R.string.start_learn);
                                binding.btnStartLearn.setOnClickListener(recordNIndex);
                            } else {
                                binding.btnStartLearn.setText(R.string.back_course);
                                binding.btnStartLearn.setOnClickListener(v -> FundingPurchaseActivity
                                        .startSupport(CourseIndexFundingActivity.this, course
                                                .getSmodule(), course
                                                .getFunding_process()));
                            }
                        }
                    });
        } else {
            binding.btnStartLearn.setText(R.string.back_course);
            binding.btnStartLearn.setOnClickListener(backThisProjectUnlogin);


            //startActivityForResult(new Intent(this, SignInActivity.class), SUPPORT_LOGIN_REQUEST_CODE);
        }
    }

    private void uploadMyCourse() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", START_TO_LEARN_TYPE);
            jsonObject.put("time", VeDate.getStringDateHourAsInt());
            jsonObject.put("crowdfunding", 0);//0 非众筹/ 1: 0-100 /2: 100-500/3 500-2000
            jsonObject.put("articleId", course.getSmodule());
            jsonObject.put("account", App.getUser().getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UpdateHelper.submitIAPLog(CourseIndexFundingActivity.this, NAction.getUserNoId(this),
                App.getGson().toJson(jsonObject));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SUPPORT_LOGIN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    FundingPurchaseActivity.startSupport(this, course.getSmodule(), course.getFunding_process());
                }
                break;
            case COURSE_LOGIN_REQUEST_CODE:
                // do nothing
                break;
        }
    }
}
