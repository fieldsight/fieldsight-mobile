package org.bcss.collect.naxa.education_materials;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.generalforms.data.Em;
import org.bcss.collect.naxa.generalforms.data.EmImage;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.login.model.Project;
import org.bcss.collect.naxa.network.ApiInterface;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.bcss.collect.naxa.project.data.ProjectLocalSource;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.stages.data.Stage;
import org.bcss.collect.naxa.stages.data.SubStage;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class EducationalMaterialsRemoteSource implements BaseRemoteDataSource<EducationalMaterial> {

    private static EducationalMaterialsRemoteSource INSTANCE;
    private final EducationalMaterialsDao dao;

    public static EducationalMaterialsRemoteSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EducationalMaterialsRemoteSource();
        }
        return INSTANCE;
    }

    private EducationalMaterialsRemoteSource() {
        FieldSightDatabase database = FieldSightDatabase.getDatabase(Collect.getInstance());//todo inject context
        this.dao = database.getEducationalMaterialDAO();
    }


    @Override
    public void getAll() {

        Observable.merge(scheduledFormEducational(), generalFormEducational(), substageFormEducational())
                .map(new Function<Em, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> apply(Em em) throws Exception {
                        ArrayList<String> nameAndUrl = new ArrayList<>();
                        if (em.getPdf() != null) {
                            String pdfUrl = em.getPdf();
                            nameAndUrl.add(pdfUrl);
                        }

                        if (em.getEmImages().size() > 0) {
                            for (EmImage emImage : em.getEmImages()) {
                                String imageUrl = emImage.getImage();
                                nameAndUrl.add(imageUrl);
                            }
                        }
                        return nameAndUrl;
                    }
                })
                .flatMapIterable((Function<ArrayList<String>, Iterable<String>>) urls -> urls)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        Timber.i("Downloading %s files as education materials", strings.size());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });

        ;

    }

    private Observable<Em> scheduledFormEducational() {
        return ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .toObservable()
                .flatMapIterable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .flatMap((Function<Project, ObservableSource<ArrayList<ScheduleForm>>>) project -> ServiceGenerator.getRxClient().create(ApiInterface.class).getScheduleForms("1", project.getId()))
                .flatMapIterable((Function<ArrayList<ScheduleForm>, Iterable<ScheduleForm>>) scheduleForms -> scheduleForms)
                .map(ScheduleForm::getEm);
    }

    private Observable<Em> substageFormEducational() {
        return ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .toObservable()
                .flatMapIterable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .flatMap((Function<Project, ObservableSource<ArrayList<Stage>>>) project -> ServiceGenerator.getRxClient().create(ApiInterface.class).getStageSubStage("1", project.getId()))
                .flatMapIterable((Function<ArrayList<Stage>, Iterable<Stage>>) stages -> stages)
                .map(Stage::getSubStage)
                .flatMapIterable((Function<ArrayList<SubStage>, Iterable<SubStage>>) subStages -> subStages)
                .map(new Function<SubStage, Em>() {
                    @Override
                    public Em apply(SubStage subStage) throws Exception {
                        return subStage.getEm();
                    }
                });
    }

    private Observable<Em> generalFormEducational() {

        return ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .toObservable()
                .flatMapIterable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .flatMap((Function<Project, ObservableSource<ArrayList<GeneralForm>>>) project -> ServiceGenerator.getRxClient().create(ApiInterface.class).getGeneralFormsObservable("1", project.getId()))
                .flatMapIterable((Function<ArrayList<GeneralForm>, Iterable<GeneralForm>>) generalForms -> generalForms)
                .map(GeneralForm::getEm);


    }
}
