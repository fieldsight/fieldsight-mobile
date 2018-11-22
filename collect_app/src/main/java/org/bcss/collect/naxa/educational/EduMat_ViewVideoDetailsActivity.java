package org.bcss.collect.naxa.educational;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.bcss.collect.android.R;

/**
 * Created by susan on 7/18/2017.
 */

public class EduMat_ViewVideoDetailsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edu_mat_view_video_detail_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        VideoView myVideoView = (VideoView) findViewById(R.id.video_url);
        TextView imageTitle = (TextView) findViewById(R.id.video_title);
        TextView imageDesc = (TextView) findViewById(R.id.video_desc);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String video_url = bundle.getString("VIDEO_URL");
            String thumbnail_url = bundle.getString("VIDEO_THUMB_URL");
            String video_title = bundle.getString("VIDEO_TITLE");
            String video_desc = bundle.getString("VIDEO_DESC");

//            myVideoView.setVideoPath(thumbnail_url);
            myVideoView.setVideoURI(Uri.parse(video_url));
            myVideoView.setMediaController(new MediaController(this));
            myVideoView.requestFocus();
            myVideoView.start();

            imageTitle.setText(video_title);
            imageDesc.setText(video_desc);
        }
    }
}