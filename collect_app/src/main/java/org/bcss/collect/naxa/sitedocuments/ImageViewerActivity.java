package org.bcss.collect.naxa.sitedocuments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.BaseActivity;
import org.bcss.collect.naxa.common.GlideApp;
import org.bcss.collect.naxa.common.ImageUtils;
import org.bcss.collect.naxa.common.TouchImageView;
import org.bcss.collect.naxa.common.ViewUtils;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;

public class ImageViewerActivity extends BaseActivity {


    @BindView(R.id.iv_image_viewer)
    TouchImageView ivImageViewer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String url;



    public static void start(Context context, String list) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, list);
        context.startActivity(intent);
    }

    public static void startFromFile(Context context, String list) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(EXTRA_MESSAGE, list);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        setupToolbar();

        url = getIntent().getExtras().getString(EXTRA_MESSAGE);
        ViewUtils.loadRemoteImage(getApplicationContext(), url)
                .into(ivImageViewer);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackClicked(boolean isHome) {
        this.finish();
    }
}
