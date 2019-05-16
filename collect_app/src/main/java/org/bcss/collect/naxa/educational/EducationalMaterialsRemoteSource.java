package org.bcss.collect.naxa.educational;

import android.os.Environment;
import android.support.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.common.RxDownloader.RxDownloader;
import org.bcss.collect.naxa.common.rx.RetrofitException;
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
import org.bcss.collect.naxa.common.DisposableManager;
import org.bcss.collect.naxa.sync.DownloadableItemLocalSource;

import org.odk.collect.android.utilities.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.DownloadUID.EDU_MATERIALS;

public class EducationalMaterialsRemoteSource implements BaseRemoteDataSource<Em> {

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
      getAllProjectEducationalMaterial(null)
              .subscribe(new DisposableSingleObserver<List<String>>() {
                  @Override
                  public void onSuccess(List<String> strings) {
                      //unused
                  }

                  @Override
                  public void onError(Throwable e) {
                      //unused
                  }
              });
    }

    public Single<List<String>> getAllProjectEducationalMaterial(String projectId) {
        return Observable.merge(scheduledFormEducational(projectId), generalFormEducational(projectId), substageFormEducational(projectId))
                .map(new Function<Em, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> apply(Em em) {
                        ArrayList<String> nameAndUrl = new ArrayList<>();
                        if (em.getPdf() != null) {
                            String pdfUrl = em.getPdf();
                            nameAndUrl.add(pdfUrl);
                        }

                        if (em.getEmImages() != null && em.getEmImages().size() > 0) {
                            for (EmImage emImage : em.getEmImages()) {
                                String imageUrl = emImage.getImage();
                                nameAndUrl.add(imageUrl);
                            }
                        }
                        return nameAndUrl;
                    }
                })
                .flatMapIterable((Function<ArrayList<String>, Iterable<String>>) urls -> urls)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        String fileName = FilenameUtils.getName(s);
                        String extension = FilenameUtils.getExtension(s);
                        boolean isFileAlreadyDownloaded;

                        switch (extension) {
                            case "pdf":
                                Timber.i("%s already exists skipping download", s);
                                isFileAlreadyDownloaded = FileUtils.isFileExists(Collect.PDF + File.separator + fileName);
                                break;
                            default:
                                Timber.i("%s already exists skipping download", s);
                                isFileAlreadyDownloaded = FileUtils.isFileExists(Collect.IMAGES + File.separator + fileName);
                                break;
                        }

                        return !isFileAlreadyDownloaded;
                    }
                })
                .flatMap((Function<String, Observable<String>>) url -> {
                    Timber.i("Looking for file on %s", url);

                    final String fileName = FilenameUtils.getName(url);
                    String savePath = getSavePath(url);

                    return new RxDownloader(Collect.getInstance())
                            .download(url, fileName, savePath, "*/*", false);

                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        DisposableManager.add(disposable);
                        DownloadableItemLocalSource.getINSTANCE().markAsRunning(EDU_MATERIALS);
                    }
                })
                .doOnSuccess(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        Timber.i("%s has been downloaded", strings.toString());
                        DownloadableItemLocalSource.getINSTANCE().markAsCompleted(EDU_MATERIALS);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        Timber.e(e);
                        String message;
                        if (e instanceof RetrofitException) {
                            message = ((RetrofitException) e).getKind().getMessage();
                        } else {
                            message = e.getMessage();
                        }

                        DownloadableItemLocalSource.getINSTANCE().markAsFailed(EDU_MATERIALS, message);
                    }
                });
    }


    private String getSavePath(String url) {

        //todo bug RxDownloadmanager is adding /storage/emulated so remove it before we send path
        String savePath = "";
        switch (FileUtils.getFileExtension(url).toLowerCase()) {
            case "pdf":
                savePath = Collect.PDF.replace(Environment.getExternalStorageDirectory().toString(), "");
                break;
            default:
                savePath = Collect.IMAGES.replace(Environment.getExternalStorageDirectory().toString(), "");
                break;
        }

        return savePath;
    }

    public Single<List<String>> getByProjectId(String projectId) {
        return getAllProjectEducationalMaterial(projectId);
    }

    private Observable<String> getProjectObservable(@Nullable String projectId) {

        if (projectId != null) {
            return Observable.just(projectId);
        } else {
            return ProjectLocalSource.getInstance()
                    .getProjectsMaybe()
                    .map(new Function<List<Project>, List<Project>>() {
                        @Override
                        public List<Project> apply(List<Project> projects) throws Exception {
                            if (projects.isEmpty()) {
                                throw new RuntimeException("Download project(s) site(s) first");
                            }
                            return projects;
                        }
                    })
                    .toObservable()
                    .flatMapIterable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                    .map(new Function<Project, String>() {
                        @Override
                        public String apply(Project project) throws Exception {
                            return project.getId();
                        }
                    });
        }

    }

    private Observable<Em> scheduledFormEducational(@Nullable String projectId) {


        return getProjectObservable(projectId)
                .flatMap((Function<String, ObservableSource<ArrayList<ScheduleForm>>>) s -> ServiceGenerator.getRxClient().create(ApiInterface.class).getScheduleForms("1", s))
                .flatMapIterable((Function<ArrayList<ScheduleForm>, Iterable<ScheduleForm>>) scheduleForms -> scheduleForms)
                .filter(new Predicate<ScheduleForm>() {
                    @Override
                    public boolean test(ScheduleForm scheduleForm) {
                        return scheduleForm.getEm() != null;
                    }
                })
                .map(new Function<ScheduleForm, Em>() {
                    @Override
                    public Em apply(ScheduleForm scheduleForm) {
                        Em em = scheduleForm.getEm();
                        if (em != null) {
                            em.setFsFormId(scheduleForm.getFsFormId());
                            EducationalMaterialsLocalSource.getInstance().save(em);
                        }
                        return em;
                    }
                });
    }

    private Observable<Em> substageFormEducational(@Nullable String projectId) {
        return getProjectObservable(projectId)
                .flatMap((Function<String, ObservableSource<ArrayList<Stage>>>) project -> ServiceGenerator.getRxClient().create(ApiInterface.class).getStageSubStage("1", project))
                .flatMapIterable((Function<ArrayList<Stage>, Iterable<Stage>>) stages -> stages)
                .map(Stage::getSubStage)
                .flatMapIterable((Function<ArrayList<SubStage>, Iterable<SubStage>>) subStages -> subStages)
                .filter(new Predicate<SubStage>() {
                    @Override
                    public boolean test(SubStage subStage) {
                        return subStage.getEm() != null;
                    }
                })
                .map(new Function<SubStage, Em>() {
                    @Override
                    public Em apply(SubStage subStage) {
                        Em em = subStage.getEm();
                        if (em != null) {
                            em.setFsFormId(subStage.getFsFormId());
                            EducationalMaterialsLocalSource.getInstance().save(em);
                        }

                        return em;
                    }
                });
    }

    private Observable<Em> generalFormEducational(@Nullable String projectId) {

        return getProjectObservable(projectId)
                .flatMap((Function<String, ObservableSource<ArrayList<GeneralForm>>>) project -> ServiceGenerator.getRxClient().create(ApiInterface.class).getGeneralFormsObservable("1", project))
                .flatMapIterable((Function<ArrayList<GeneralForm>, Iterable<GeneralForm>>) generalForms -> generalForms)
                .filter(new Predicate<GeneralForm>() {
                    @Override
                    public boolean test(GeneralForm generalForm) {
                        return generalForm.getEm() != null;
                    }
                })
                .map(new Function<GeneralForm, Em>() {
                    @Override
                    public Em apply(GeneralForm generalForm) {
                        Em em = generalForm.getEm();
                        if (em != null) {
                            em.setFsFormId(generalForm.getFsFormId());
                            EducationalMaterialsLocalSource.getInstance().save(em);
                        }

                        return em;
                    }
                });


    }
}
