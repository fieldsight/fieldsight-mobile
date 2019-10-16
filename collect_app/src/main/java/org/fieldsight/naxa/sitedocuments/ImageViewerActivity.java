package org.fieldsight.naxa.sitedocuments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.BaseActivity;
import org.fieldsight.naxa.common.TouchImageView;
import org.fieldsight.naxa.common.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fieldsight.naxa.common.Constant.EXTRA_MESSAGE;

public class ImageViewerActivity extends BaseActivity {


    @BindView(R.id.iv_image_viewer)
    TouchImageView ivImageViewer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


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

        String url = getIntent().getExtras().getString(EXTRA_MESSAGE);
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
