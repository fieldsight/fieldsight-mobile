package org.bcss.collect.naxa.site;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.naxa.login.model.Site;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class CreateSiteDetailActivity extends CollectAbstractActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbarGeneral;

    public static void start(Context context, @NonNull Site site) {
        Intent intent = new Intent(context, CreateSiteDetailActivity.class);
        intent.putExtra(EXTRA_OBJECT, site);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail_change);
        ButterKnife.bind(this);

        setupToolbar();
    }

    private void setupToolbar() {
        toolbarGeneral.setTitle(R.string.toolbar_title_offline_site_edit);
        setSupportActionBar(toolbarGeneral);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
