package org.bcss.collect.naxa.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppUpdateActivity extends CollectAbstractActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.act_update_app_btn_update)
    public void openPlayStore(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=org.bcss.collect.android"));
        startActivity(intent);
    }

    @OnClick(R.id.act_update_app_btn_not_now)
    public void closeActivity(){
        super.onBackPressed();;
    }

}
