package org.fieldsight.naxa.educational;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.bcss.collect.android.R;;
import org.odk.collect.android.activities.CollectAbstractActivity;

/**
 * Created by susan on 7/18/2017.
 */

public class EduMatViewTextTitleDetailsActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edu_mat_view_texttitle_detail_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvTitle = findViewById(R.id.text_title);
        TextView tvDesc = findViewById(R.id.text_desc);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String textTitle = bundle.getString("TEXT_TITLE");
            String textDesc = bundle.getString("TEXT_DESC");
            getSupportActionBar().setTitle(textTitle);

            tvTitle.setText(textTitle);
            tvDesc.setText(textDesc);
        }
    }
}