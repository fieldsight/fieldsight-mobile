package org.fieldsight.naxa.generalforms.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.fieldsight.naxa.common.BaseRemoteDataSource;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.database.FieldSightConfigDatabase;
import org.fieldsight.naxa.common.database.SiteOveride;
import org.fieldsight.naxa.common.event.DataSyncEvent;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.network.ApiInterface;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.onboarding.XMLForm;
import org.fieldsight.naxa.onboarding.XMLFormBuilder;
import org.fieldsight.naxa.project.data.ProjectLocalSource;
import org.greenrobot.eventbus.EventBus;
import org.odk.collect.android.application.Collect;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.fieldsight.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.fieldsight.naxa.common.event.DataSyncEvent.EventStatus.EVENT_ERROR;
import static org.fieldsight.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;

public class GeneralFormRemoteSource implements BaseRemoteDataSource<GeneralForm> {

    private static GeneralFormRemoteSource generalFormRemoteSource;
    private final ProjectLocalSource projectLocalSource;

    public static GeneralFormRemoteSource getInstance() {
        if (generalFormRemoteSource == null) {
            generalFormRemoteSource = new GeneralFormRemoteSource();
        }
        return generalFormRemoteSource;
    }


    public GeneralFormRemoteSource() {
        this.projectLocalSource = ProjectLocalSource.getInstance();

    }


    private Observable<ArrayList<GeneralForm>> fetchGeneralFromOneUrl(XMLForm xmlForm) {
        String createdFromProject = XMLForm.toNumeralString(xmlForm.isCreatedFromProject());
        String creatorsId = xmlForm.getFormCreatorsId();


        return ServiceGenerator
                .getRxClient()
                .create(ApiInterface.class)
                .getGeneralFormsObservable(createdFromProject, creatorsId)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(final Observable<Throwable> throwableObservable) {
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) {
                                if (throwable instanceof IOException) {
                                    return throwableObservable;
                                }

                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ArrayList<GeneralForm>> fetchGeneralFormByProjectId(String projectId) {
        Observable<List<XMLForm>> siteODKForms = FieldSightConfigDatabase.getDatabase(Collect.getInstance())
                .getSiteOverideDAO()
                .getSiteOverideFormIds(projectId)
                .map((Function<SiteOveride, LinkedList<String>>) siteOveride -> {
                    Type type = new TypeToken<LinkedList<String>>() {
                    }.getType();//todo use typeconvertor
                    return new Gson().fromJson(siteOveride.getGeneralFormIds(), type);
                }).flattenAsObservable((Function<LinkedList<String>, Iterable<String>>) siteIds -> siteIds)
                .map(siteId -> new XMLFormBuilder()
                        .setFormCreatorsId(siteId)
                        .setIsCreatedFromProject(false)
                        .createXMLForm())
                .toList()
                .toObservable();


        Observable<ArrayList<XMLForm>> projectODKForms = ProjectLocalSource.getInstance()
                .getProjectByIdAsSingle(projectId)
                .map(new Function<Project, ArrayList<XMLForm>>() {
                    @Override
                    public ArrayList<XMLForm> apply(Project project) throws Exception {

                        return new ArrayList<XMLForm>() {{
                            add(new XMLFormBuilder()
                                    .setFormCreatorsId(project.getId())
                                    .setIsCreatedFromProject(true)
                                    .createXMLForm()
                            );
                        }};
                    }
                }).toObservable();

        return Observable.concat(projectODKForms, siteODKForms)
                .flatMapIterable((Function<List<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap((Function<XMLForm, ObservableSource<ArrayList<GeneralForm>>>) this::fetchGeneralFromOneUrl)
                .map(generalForms -> {
                    for (GeneralForm generalForm : generalForms) {
                        String deployedFrom = generalForm.getProjectId() != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;
                        generalForm.setFormDeployedFrom(deployedFrom);
                    }

                    return generalForms;
                })
                .toList()
                .map((List<ArrayList<GeneralForm>> arrayLists) -> {
                    ArrayList<GeneralForm> generalForms = new ArrayList<>(0);

                    for (ArrayList<GeneralForm> generalFormList : arrayLists) {
                        generalForms.addAll(generalFormList);
                    }
                    GeneralFormLocalSource.getInstance().updateAll(generalForms);
                    return generalForms;
                });

    }

    public Single<ArrayList<GeneralForm>> fetchAllGeneralForms() {
        Single<List<XMLForm>> siteODKForms = FieldSightConfigDatabase
                .getDatabase(Collect.getInstance())
                .getSiteOverideDAO()
                .getAll()
                .map((Function<SiteOveride, LinkedList<String>>) siteOveride -> {
                    Type type = new TypeToken<LinkedList<String>>() {
                    }.getType();//todo use typeconvertor
                    return new Gson().fromJson(siteOveride.getGeneralFormIds(), type);
                }).flattenAsObservable((Function<LinkedList<String>, Iterable<String>>) siteIds -> siteIds)
                .map(siteId -> new XMLFormBuilder()
                        .setFormCreatorsId(siteId)
                        .setIsCreatedFromProject(false)
                        .createXMLForm())
                .toList();


        Single<List<XMLForm>> projectODKForms = projectLocalSource
                .getProjectsMaybe()
                .flattenAsObservable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .map(project -> new XMLFormBuilder()
                        .setFormCreatorsId(project.getId())
                        .setIsCreatedFromProject(true)
                        .createXMLForm())
                .toList();


        return Single.concat(siteODKForms, projectODKForms)
                .toObservable()
                .flatMapIterable((Function<List<XMLForm>, Iterable<XMLForm>>) xmlForms -> xmlForms)
                .flatMap((Function<XMLForm, ObservableSource<ArrayList<GeneralForm>>>) this::fetchGeneralFromOneUrl)
                .map(generalForms -> {
                    for (GeneralForm generalForm : generalForms) {
                        String deployedFrom = generalForm.getProjectId() != null ? Constant.FormDeploymentFrom.PROJECT : Constant.FormDeploymentFrom.SITE;
                        generalForm.setFormDeployedFrom(deployedFrom);
                    }

                    return generalForms;
                })
                .toList()
                .map((List<ArrayList<GeneralForm>> arrayLists) -> {
                    ArrayList<GeneralForm> generalForms = new ArrayList<>(0);

                    for (ArrayList<GeneralForm> generalFormList : arrayLists) {
                        generalForms.addAll(generalFormList);
                    }
                    GeneralFormLocalSource.getInstance().updateAll(generalForms);
                    return generalForms;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public void getAll() {

        fetchAllGeneralForms()
                .subscribe(new SingleObserver<ArrayList<GeneralForm>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_START));
                    }

                    @Override
                    public void onSuccess(ArrayList<GeneralForm> generalForms) {
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_END));
                        Timber.i("%s general FORMS downloaded successfully ", generalForms.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                        EventBus.getDefault().post(new DataSyncEvent(Constant.DownloadUID.GENERAL_FORMS, EVENT_ERROR));
                    }
                });


    }


}
