package org.bcss.collect.naxa.flagform;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.data.FieldSightNotification;
import org.bcss.collect.naxa.site.CreateSiteDetailViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlaggedInstanceActivityNew extends CollectAbstractActivity {

    private TextView noMessage, tvFormName, tvFormDesc, tvComment, tvFormStatus;
    private RecyclerView recyclerViewImages;
    private ImageButton imbStatus;
    private RelativeLayout relativeStatus;
    private RelativeLayout formBox;
    private Toolbar toolbar;
    private FlaggedFormViewModel viewmodel;

    public static void start(Context context, FieldSightNotification fieldSightNotification) {
        Intent intent = new Intent(context, FlaggedInstanceActivityNew.class);
        intent.putExtra(Constant.EXTRA_OBJECT, fieldSightNotification);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_flag_response);
        bindUI();
        setupViewModel();

        viewmodel.setNotification(getIntent().getParcelableExtra(Constant.EXTRA_OBJECT));
        viewmodel.getNotification().observe(this, this::setupData);
    }

    private void setupViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        viewmodel = ViewModelProviders.of(this, factory).get(FlaggedFormViewModel.class);
    }

    private void bindUI() {

        toolbar = findViewById(R.id.toolbar);
        noMessage = findViewById(R.id.textView6);
        tvFormName = findViewById(R.id.tv_form_name);
        tvFormDesc = findViewById(R.id.tv_form_desc);
        imbStatus = findViewById(R.id.img_btn_status);
        tvFormStatus = findViewById(R.id.tv_form_status);
        tvComment = findViewById(R.id.tv_comments_txt);
        recyclerViewImages = findViewById(R.id.comment_session_rv_images);
        relativeStatus = findViewById(R.id.relativeLayout_status);
        formBox = findViewById(R.id.relative_layout_comment_open_form);
    }


    private void setupData(FieldSightNotification fieldSightNotification) {

        String comment = fieldSightNotification.getComment();
        String formName = fieldSightNotification.getFormName();
        String formStatus = fieldSightNotification.getFormStatus();

        if (TextUtils.isEmpty(fieldSightNotification.getComment())) {
            noMessage.setText(R.string.comments_default_comment);
            noMessage.setVisibility(View.VISIBLE);
            tvComment.setText("");
        } else {
            noMessage.setVisibility(View.GONE);
            tvComment.setText(comment);
        }

        //set values to text view on layout
        tvFormName.setText(formName);
        //  tvFormDesc.setText(jrFormId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imbStatus.setElevation(3);
        }

        if (formStatus != null && formStatus.equals("Approved")) {
            imbStatus.setBackgroundResource(R.color.green_approved);
            relativeStatus.setBackgroundResource(R.color.green_approved);
        } else if (formStatus != null && formStatus.equals("Outstanding")) {
            imbStatus.setBackgroundResource(R.color.grey_outstanding);
            relativeStatus.setBackgroundResource(R.color.grey_outstanding);
        } else if (formStatus != null && formStatus.equals("Flagged")) {
            imbStatus.setBackgroundResource(R.color.yellow_flagged);
            relativeStatus.setBackgroundResource(R.color.yellow_flagged);
        } else if (formStatus != null && formStatus.equals("Rejected")) {
            imbStatus.setBackgroundResource(R.color.red_rejected);
            relativeStatus.setBackgroundResource(R.color.red_rejected);
        }

        tvFormStatus.setText(formStatus);
    }
}
