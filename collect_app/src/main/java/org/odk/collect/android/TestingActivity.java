package org.odk.collect.android;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.login.model.SiteBuilder;
import org.fieldsight.naxa.project.ProjectListActivity;
import org.fieldsight.naxa.project.data.ProjectLocalSource;
import org.fieldsight.naxa.site.db.SiteLocalSource;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.PROJECT;

public class TestingActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectListActivity.start(TestingActivity.this);
            }
        });
    }


    public void addOfflineSiteOnFirstProject(View view) {
        ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .subscribeOn(Schedulers.io())
                .toObservable()
                .map(new Function<List<Project>, Site>() {
                    @Override
                    public Site apply(List<Project> projects) {
                        String projId = projects.get(0).getId();
                        Site site = new SiteBuilder()
                                .setGeneralFormDeployedFrom(PROJECT)
                                .setId(Site.getMockedId())
                                .setProject(projId)
                                .setScheduleFormDeployedForm(PROJECT)
                                .setStagedFormDeployedFrom(PROJECT)
                                .setIdentifier(getSaltString())
                                .setIsSiteVerified(Constant.SiteStatus.IS_OFFLINE)
                                .setName("site" + getSaltString())
                                .setLatitude("31.333332")
                                .setLongitude("35.499998")
                                .createSite();

                        SiteLocalSource.getInstance().save(site);

                        return site;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Site>() {
                    @Override
                    public void onNext(Site site) {
                        ToastUtils.showLongToast(site.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        ToastUtils.showLongToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    protected String getSaltString() {
         String saltchars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 4) { // length of the random string.
            int index = (int) (rnd.nextFloat() * saltchars.length());
            salt.append(saltchars.charAt(index));
        }
        return salt.toString();

    }
}
