package org.bcss.collect.naxa.educational;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.bcss.collect.android.R;
import org.odk.collect.android.activities.CollectAbstractActivity;

/**
 * Created by susan on 7/18/2017.
 */

public class EduMat_ViewTextTitleDetailsActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edu_mat_view_texttitle_detail_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvTitle = (TextView) findViewById(R.id.text_title);
        TextView tvDesc = (TextView) findViewById(R.id.text_desc);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String text_title = bundle.getString("TEXT_TITLE");
            String text_desc = bundle.getString("TEXT_DESC");
            getSupportActionBar().setTitle(text_title);

            tvTitle.setText(text_title);
            tvDesc.setText(text_desc);
        }
    }
}