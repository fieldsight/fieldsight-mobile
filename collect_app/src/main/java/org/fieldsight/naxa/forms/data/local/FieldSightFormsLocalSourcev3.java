package org.fieldsight.naxa.forms.data.local;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import org.fieldsight.naxa.common.BaseLocalDataSourceRX;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.FieldSightDatabase;
import org.fieldsight.naxa.common.GSONInstance;
import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

public class FieldSightFormsLocalSourcev3 implements BaseLocalDataSourceRX<FieldsightFormDetailsv3> {

    private static FieldSightFormsLocalSourcev3 INSTANCE;
    private FieldSightFormDetailDAOV3 dao;

    private FieldSightFormsLocalSourcev3() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getFieldSightFOrmDAOV3();
    }

    public static FieldSightFormsLocalSourcev3 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FieldSightFormsLocalSourcev3();
        }
        return INSTANCE;
    }

    public LiveData<List<FieldsightFormDetailsv3>> getFormByType(String formType, String projectId, String siteId) {

        MediatorLiveData<List<FieldsightFormDetailsv3>> mediator = new MediatorLiveData<>();
        LiveData<List<FieldsightFormDetailsv3>> formSource = dao.getFormByType(formType, projectId, siteId);

        mediator.addSource(formSource, forms -> {
            if (TextUtils.equals(formType, Constant.FormType.STAGED)) {
                getSortedStages(forms)
                        .subscribe(new DisposableObserver<List<FieldsightFormDetailsv3>>() {
                            @Override
                            public void onNext(List<FieldsightFormDetailsv3> sortedStages) {
                                mediator.removeSource(formSource);
                                mediator.setValue(sortedStages);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e);
                                mediator.removeSource(formSource);
                                mediator.setValue(new ArrayList<>(0));
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            } else {
                mediator.removeSource(formSource);
                mediator.setValue(forms);
            }
        });

        return mediator;
    }

    private Observable<List<FieldsightFormDetailsv3>> getSortedStages(List<FieldsightFormDetailsv3> forms) {
        return Observable.just(forms)
                .map(new Function<List<FieldsightFormDetailsv3>, List<FieldsightFormDetailsv3>>() {
                    @Override
                    public List<FieldsightFormDetailsv3> apply(List<FieldsightFormDetailsv3> fieldsightFormDetailsv3s) {
                        Collections.shuffle(fieldsightFormDetailsv3s);
                        return fieldsightFormDetailsv3s;
                    }
                })
                .map(new Function<List<FieldsightFormDetailsv3>, List<FieldsightFormDetailsv3>>() {
                    @Override
                    public List<FieldsightFormDetailsv3> apply(List<FieldsightFormDetailsv3> formDetailsv3s) throws Exception {
                        return null;
                    }
                })
                .map(new Function<List<FieldsightFormDetailsv3>, List<FieldsightFormDetailsv3>>() {
                    @Override
                    public List<FieldsightFormDetailsv3> apply(List<FieldsightFormDetailsv3> formDetailsv3) {
                        Collections.sort(formDetailsv3, new Comparator<FieldsightFormDetailsv3>() {
                            @Override
                            public int compare(FieldsightFormDetailsv3 t1, FieldsightFormDetailsv3 t2) {
                                StageSubStage stage1 = GSONInstance.getInstance().fromJson(t1.getMetaAttributes(), StageSubStage.class);
                                StageSubStage stage2 = GSONInstance.getInstance().fromJson(t2.getMetaAttributes(), StageSubStage.class);

                                Double stageOneOrder = Double.parseDouble(stage1.getStageOrder() + "." + stage1.getSubstageOrder());
                                Double stageTwoOrder = Double.parseDouble(stage2.getStageOrder() + "." + stage2.getSubstageOrder());

                                return stageOneOrder.compareTo(stageTwoOrder);
                            }
                        });


                        return formDetailsv3;
                    }
                });
    }

    public void saveForms(FieldsightFormDetailsv3... fieldSightForm) {
        dao.insert(fieldSightForm);
    }

    @Override
    public LiveData<List<FieldsightFormDetailsv3>> getAll() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public Completable save(FieldsightFormDetailsv3... items) {
        return Completable.fromAction(() -> dao.insert(items));
    }

    @Override
    public void save(ArrayList<FieldsightFormDetailsv3> items) {
        dao.insert(items);
    }


    @Override
    public void updateAll(ArrayList<FieldsightFormDetailsv3> items) {

    }

    public List<FieldsightFormDetailsv3> getEducationMaterial(String projectId) {
        return dao.getEducationMaterailByProjectIds(projectId);
    }
}
