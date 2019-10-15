package org.fieldsight.naxa.forms.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.EmptyResultSetException;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.apache.commons.io.FilenameUtils;
import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.ViewUtils;
import org.fieldsight.naxa.educational.DynamicFragment;
import org.fieldsight.naxa.educational.Edu_Image_Model;
import org.fieldsight.naxa.educational.Edu_PDF_Model;
import org.fieldsight.naxa.educational.Edu_Title_Desc_Model;
import org.fieldsight.naxa.educational.PagerAdapter;
import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSourcev3;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.fieldsight.naxa.generalforms.data.Em;
import org.fieldsight.naxa.generalforms.data.EmImage;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.ToastUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_ID;
import static org.fieldsight.naxa.common.Constant.EXTRA_MESSAGE;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class EducationalMaterialListActivity extends CollectAbstractActivity {

    private Em em;
    List<Fragment> fragments = new ArrayList<>();


    private static final int defaultPagerPosition = 0;
    private PagerAdapter mPagerAdapter;
    public ViewPager viewPager;
    public TabLayout tabLayout;
    private String formName;

    public static void start(Context context, String formName, Em em) {
        Intent intent = new Intent(context, EducationalMaterialListActivity.class);
        intent.putExtra(EXTRA_OBJECT, em);
        intent.putExtra(EXTRA_MESSAGE, formName);
        context.startActivity(intent);
    }


    public static void start(Context context, String fsFormId) {
        WeakReference<Context> weakReference = new WeakReference<Context>(context);
        Intent intent = new Intent(weakReference.get(), EducationalMaterialListActivity.class);
        intent.putExtra(EXTRA_ID, fsFormId);
        weakReference.get().startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_material);
        ButterKnife.bind(this);

        em = getIntent().getParcelableExtra(EXTRA_OBJECT);
        String fsFormId = getIntent().getStringExtra(EXTRA_ID);
        formName = getIntent().getStringExtra(EXTRA_MESSAGE);

        if (fsFormId != null && em == null) {
            FieldsightFormDetailsv3 form = FieldSightFormsLocalSourcev3.getInstance().getByFsFormId(fsFormId);
            formName = form.getFormDetails().getFormName();
            em = FieldsightFormDetailsv3.mapStringToEm(form.getEm());
        }

        if (em == null) {
            ToastUtils.showLongToast(getString(R.string.msg_education_material_not_present));
            finish();
            return;
        }


        bindUI();
        setupViewPager();
        generateFragments();
    }

    private void bindUI() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        TextView title = findViewById(R.id.title);

        title.setText(formName);
    }


    private void setupViewPager() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(defaultPagerPosition);
        viewPager.setPageMargin(ViewUtils.dp2px(getApplicationContext(), 16));
        viewPager.setClipToPadding(false);
        viewPager.setPadding(16, 16, 16, 0);
    }


    private void generateFragments() {


        Single<List<Fragment>> observable = Observable.just(em)
                .map(new Function<Em, Fragment>() {
                    @Override
                    public Fragment apply(Em educationMaterial) {
                        ArrayList<Object> itemsListSiteTrue = new ArrayList<>();

                        itemsListSiteTrue.add(
                                new Edu_Title_Desc_Model(
                                        educationMaterial.getTitle(),
                                        educationMaterial.getText()));


                        boolean educationMaterialExist = educationMaterial.getEmImages() != null && educationMaterial.getEmImages().size() > 0;
                        if (educationMaterialExist) {
                            for (EmImage emImage : educationMaterial.getEmImages()) {
                                String imageName = FilenameUtils.getName(emImage.getImage());
                                String path = Collect.IMAGES + File.separator + imageName;

                                itemsListSiteTrue.add(
                                        new Edu_Image_Model(
                                                path,
                                                path,
                                                imageName
                                        ));
                            }
                        }

                        boolean pdfExists = educationMaterial.getPdf() != null && educationMaterial.getPdf().length() > 0;

                        if (pdfExists) {

                            String url = educationMaterial.getPdf();
                            String fileName = FilenameUtils.getName(educationMaterial.getPdf());
                            String path = Collect.PDF + File.separator + fileName;

                            itemsListSiteTrue.add(new Edu_PDF_Model(
                                    url,
                                    path,
                                    fileName));
                        }

                        DynamicFragment dynamicFragment = new DynamicFragment();
                        dynamicFragment.prepareAllFields(itemsListSiteTrue);
                        return dynamicFragment;
                    }
                })
                .toList();

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Fragment>>() {
                    @Override
                    public void onSuccess(List<Fragment> dynamicFragments) {
                        fragments.addAll(dynamicFragments);
                        mPagerAdapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(defaultPagerPosition, false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        if (e instanceof EmptyResultSetException) {
                            ToastUtils.showLongToast("No education materials present for this form");
                        } else {
                            ToastUtils.showLongToast("Failed to load Education Material");
                        }
                        finish();
                    }
                });


    }


}
