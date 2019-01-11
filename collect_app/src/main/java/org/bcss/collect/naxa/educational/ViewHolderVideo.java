package org.bcss.collect.naxa.educational;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.bcss.collect.android.R;

/**
 * Created by susan on 7/5/2017.
 */

public class ViewHolderVideo extends RecyclerView.ViewHolder{

    public static VideoView videoView;
    public static TextView vTitle, vDesc;
    public static LinearLayout linearClick;

    public ViewHolderVideo(View itemView) {
        super(itemView);
        videoView = itemView.findViewById(R.id.video_src);
        vTitle = itemView.findViewById(R.id.video_title);
        linearClick = itemView.findViewById(R.id.linear_layout_click);
//        vDesc = (TextView)itemView.findViewById(R.id.video_desc);
    }

    public VideoView getVideoView() {
        return videoView;
    }

    public void setVideoView(VideoView videoView) {
        ViewHolderVideo.videoView = videoView;
    }

    public TextView getvTitle() {
        return vTitle;
    }

    public void setvTitle(TextView vTitle) {
        ViewHolderVideo.vTitle = vTitle;
    }
}
