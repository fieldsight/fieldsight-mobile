package org.bcss.collect.naxa.educational;

import android.arch.persistence.room.EmptyResultSetException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;
import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.activities.FormEntryActivity;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.ViewUtils;
import org.bcss.collect.naxa.generalforms.data.Em;
import org.bcss.collect.naxa.generalforms.data.EmImage;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;
import org.bcss.collect.naxa.previoussubmission.model.ScheduledFormAndSubmission;
import org.bcss.collect.naxa.previoussubmission.model.SubStageAndSubmission;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_MESSAGE;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class EducationalMaterialActivity extends CollectAbstractActivity implements ViewPager.OnPageChangeListener {


    private ArrayList<String> fsFormIds;
    private int defaultPagerPosition = 0;


    private PagerAdapter mPagerAdapter;
    @BindView(R.id.view_pager)
    public ViewPager viewPager;

    @BindView(R.id.tab_layout)
    public TabLayout tabLayout;

    @BindView(R.id.title)
    public TextView subStageTitle;

    List<Fragment> fragments = new Vector<>();
    private ArrayList<GeneralForm> generalFormList;
    private String fsFormId, id, deployedFrom, formType;


    private static Single<List<String>> getFsFormIdsFromGeneral(ArrayList<GeneralFormAndSubmission> list) {

        return Observable.just(list)
                .flatMapIterable((Function<ArrayList<GeneralFormAndSubmission>, Iterable<GeneralFormAndSubmission>>) generalForms -> generalForms)
                .map(generalFormAndSubmission -> generalFormAndSubmission.getGeneralForm().getFsFormId())
                .toList();
    }

    private static Single<List<String>> getFsFormIdsFromScheduled(ArrayList<ScheduledFormAndSubmission> list) {

        return Observable.just(list)
                .flatMapIterable((Function<ArrayList<ScheduledFormAndSubmission>, Iterable<ScheduledFormAndSubmission>>) scheduleForms -> scheduleForms)
                .map(generalFormAndSubmission -> generalFormAndSubmission.getScheduleForm().getFsFormId())
                .toList();
    }

    private static Single<List<String>> getFsFormIdsFromSubStage(ArrayList<SubStageAndSubmission> list) {

        return Observable.just(list)
                .flatMapIterable((Function<ArrayList<SubStageAndSubmission>, Iterable<SubStageAndSubmission>>) substage -> substage)
                .map(generalFormAndSubmission -> generalFormAndSubmission.getSubStage().getFsFormId())
                .toList();
    }

    public static void startFromGeneral(Context context, ArrayList<GeneralFormAndSubmission> list, int pos) {
        WeakReference<Context> weakReference = new WeakReference<Context>(context);


        getFsFormIdsFromGeneral(list)
                .subscribe(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(List<String> fsFormIds) {
                        Intent intent = new Intent(weakReference.get(), EducationalMaterialActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, pos);
                        intent.putStringArrayListExtra(EXTRA_OBJECT, (ArrayList<String>) fsFormIds);
                        weakReference.get().startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShortToast("Failed to load Education Material");
                    }
                });


    }

    public static void startFromScheduled(Context context, ArrayList<ScheduledFormAndSubmission> list, int pos) {
        WeakReference<Context> weakReference = new WeakReference<Context>(context);


        getFsFormIdsFromScheduled(list)
                .subscribe(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(List<String> fsFormIds) {
                        Intent intent = new Intent(weakReference.get(), EducationalMaterialActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, pos);
                        intent.putStringArrayListExtra(EXTRA_OBJECT, (ArrayList<String>) fsFormIds);
                        weakReference.get().startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShortToast("Failed to load Education Material");
                    }
                });


    }


    public static void startFromSubstage(Context context, ArrayList<SubStageAndSubmission> list, int pos) {
        WeakReference<Context> weakReference = new WeakReference<Context>(context);


        getFsFormIdsFromSubStage(list)
                .subscribe(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(List<String> fsFormIds) {
                        Intent intent = new Intent(weakReference.get(), EducationalMaterialActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, pos);
                        intent.putStringArrayListExtra(EXTRA_OBJECT, (ArrayList<String>) fsFormIds);
                        weakReference.get().startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShortToast("Failed to load Education Material");
                    }
                });


    }

    public static void start(FormEntryActivity formEntryActivity, String id) {
        Intent intent = new Intent(formEntryActivity, EducationalMaterialActivity.class);

        ArrayList<String> fsFormIds = new ArrayList<>();
        fsFormIds.add(id);

        intent.putExtra(EXTRA_MESSAGE, 0);
        intent.putStringArrayListExtra(EXTRA_OBJECT, (ArrayList<String>) fsFormIds);
        formEntryActivity.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_material);
        ButterKnife.bind(this);

        fsFormIds = getIntent().getStringArrayListExtra(EXTRA_OBJECT);
        defaultPagerPosition = getIntent().getIntExtra(EXTRA_MESSAGE, 0);


        setupViewPager();
        generateFragments();
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


        Single<List<Fragment>> observable = Observable.just(fsFormIds)
                .flatMapIterable((Function<ArrayList<String>, Iterable<String>>) strings -> strings)
                .flatMap(new Function<String, Observable<Em>>() {
                    @Override
                    public Observable<Em> apply(String fsFormId) throws Exception {
                        return EducationalMaterialsLocalSource.getInstance().getByFsFormId(fsFormId).toObservable();
                    }
                })
                .map(new Function<Em, Fragment>() {
                    @Override
                    public Fragment apply(Em educationMaterial) throws Exception {
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
                        e.printStackTrace();
                        if (e instanceof EmptyResultSetException) {
                            ToastUtils.showLongToast("No education materials present for this form");
                        }else {
                            ToastUtils.showLongToast("Failed to load Education Material");
                        }
                        finish();
                    }
                });


    }


    private void setPageTitle() {


    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //not implemented
    }

    @Override
    public void onPageSelected(int position) {
        setPageTitle();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //not implemented
    }
}
