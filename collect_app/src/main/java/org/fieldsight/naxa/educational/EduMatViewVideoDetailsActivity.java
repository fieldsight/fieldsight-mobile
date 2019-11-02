package org.fieldsight.naxa.educational;

import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.bcss.collect.android.R;;
import org.odk.collect.android.activities.CollectAbstractActivity;

/**
 * Created by susan on 7/18/2017.
 */

public class EduMatViewVideoDetailsActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edu_mat_view_video_detail_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        VideoView myVideoView = findViewById(R.id.video_url);
        TextView imageTitle = findViewById(R.id.video_title);
        TextView imageDesc = findViewById(R.id.video_desc);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String videoUrl = bundle.getString("VIDEO_URL");
            String videoTitle = bundle.getString("VIDEO_TITLE");
            String videoDesc = bundle.getString("VIDEO_DESC");

//            myVideoView.setVideoPath(thumbnail_url);
            myVideoView.setVideoURI(Uri.parse(videoUrl));
            myVideoView.setMediaController(new MediaController(this));
            myVideoView.requestFocus();
            myVideoView.start();

            imageTitle.setText(videoTitle);
            imageDesc.setText(videoDesc);
        }
    }
}