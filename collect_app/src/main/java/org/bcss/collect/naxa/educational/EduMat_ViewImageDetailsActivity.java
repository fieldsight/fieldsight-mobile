package org.bcss.collect.naxa.educational;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.GlideApp;

import java.io.File;

/**
 * Created by susan on 7/18/2017.
 */

public class EduMat_ViewImageDetailsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edu_mat_view_image_detail_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.image_url);
        TextView imageTitle = (TextView) findViewById(R.id.image_title);
        TextView imageDesc = (TextView) findViewById(R.id.image_desc);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String image_url_on = bundle.getString("IMAGE_URL_ON");
            String image_url_off = bundle.getString("IMAGE_URL_OFF");
            String image_title = bundle.getString("IMAGE_TITLE");
            String image_desc = bundle.getString("IMAGE_DESC");

            getSupportActionBar().setTitle(image_title);


            if (!image_url_off.equals("") || image_url_off != null) {
                File f = new File(image_url_off);
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                imageView.setImageBitmap(bmp);
            }else {
                GlideApp.with(EduMat_ViewImageDetailsActivity.this)
                        .load( image_url_on)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }

//            usingSimpleImage(imageView);

            imageTitle.setText(image_title);
            imageDesc.setText(image_desc);
        }
    }


//    public void usingSimpleImage(ImageView imageView) {
//        ImageAttacher mAttacher = new ImageAttacher(imageView);
//        ImageAttacher.MAX_ZOOM = 3.0f; // Triple the current Size
//        ImageAttacher.MIN_ZOOM = 0.5f; // Half the current Size
//        MatrixChangeListener mMaListener = new MatrixChangeListener();
//        mAttacher.setOnMatrixChangeListener(mMaListener);
//        PhotoTapListener mPhotoTap = new PhotoTapListener();
//        mAttacher.setOnPhotoTapListener(mPhotoTap);
//    }
//
//    private class PhotoTapListener implements ImageAttacher.OnPhotoTapListener {
//
//        @Override
//        public void onPhotoTap(View view, float x, float y) {
//
//        }
//    }
//
//    private class MatrixChangeListener implements ImageAttacher.OnMatrixChangedListener {
//
//        @Override
//        public void onMatrixChanged(RectF rect) {
//
//        }
//    }
}