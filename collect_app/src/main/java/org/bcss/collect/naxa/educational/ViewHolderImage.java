package org.bcss.collect.naxa.educational;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.bcss.collect.android.R;

/**
 * Created by susan on 7/5/2017.
 */

public class ViewHolderImage extends RecyclerView.ViewHolder{

    public static ImageView imageView;
    private TextView iTitle, iDesc;
    public static LinearLayout linearClick;

    public ViewHolderImage(View itemView) {
        super(itemView);
        imageView = (ImageView)itemView.findViewById(R.id.image_view);
        iTitle = (TextView)itemView.findViewById(R.id.image_title);
        linearClick = (LinearLayout) itemView.findViewById(R.id.linear_layout_click);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getiTitle() {
        return iTitle;
    }

    public void setiTitle(TextView iTitle) {
        this.iTitle = iTitle;
    }

    public TextView getiDesc() {
        return iDesc;
    }

    public void setiDesc(TextView iDesc) {
        this.iDesc = iDesc;
    }
}
