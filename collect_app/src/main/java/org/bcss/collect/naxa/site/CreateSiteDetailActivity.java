package org.bcss.collect.naxa.site;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;

public class CreateSiteDetailActivity extends CollectAbstractActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail_change);
    }
}
