package org.bcss.collect.naxa.educational;

import android.os.Environment;

import org.apache.commons.io.FilenameUtils;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.utilities.FileUtils;
import org.bcss.collect.naxa.common.BaseRemoteDataSource;
import org.bcss.collect.naxa.common.FieldSightDatabase;
import org.bcss.collect.naxa.common.RxDownloader.RxDownloader;
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
import org.bcss.collect.naxa.sync.DisposableManager;
import org.bcss.collect.naxa.sync.SyncLocalSource;
import org.bcss.collect.naxa.sync.SyncRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
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

        Observable.merge(scheduledFormEducational(), generalFormEducational(), substageFormEducational())
                .map(new Function<Em, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> apply(Em em) throws Exception {
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
                    public boolean test(String s) throws Exception {
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
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        DisposableManager.add(d);
                        SyncRepository.getInstance().showProgress(EDU_MATERIALS);

                        SyncLocalSource.getINSTANCE().markAsRunning(EDU_MATERIALS);
                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        SyncRepository.getInstance().setSuccess(EDU_MATERIALS);
                        Timber.i("%s has been downloaded", strings.toString());

                        SyncLocalSource.getINSTANCE().markAsCompleted(EDU_MATERIALS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        SyncRepository.getInstance().setError(EDU_MATERIALS);
                        e.printStackTrace();
                        Timber.e(e);

                        SyncLocalSource.getINSTANCE().markAsFailed(EDU_MATERIALS);
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

    private Observable<Em> scheduledFormEducational() {
        return ProjectLocalSource.getInstance()
                .getProjectsMaybe()
                .toObservable()
                .flatMapIterable((Function<List<Project>, Iterable<Project>>) projects -> projects)
                .flatMap((Function<Project, ObservableSource<ArrayList<ScheduleForm>>>) project -> ServiceGenerator.getRxClient().create(ApiInterface.class).getScheduleForms("1", project.getId()))
                .flatMapIterable((Function<ArrayList<ScheduleForm>, Iterable<ScheduleForm>>) scheduleForms -> scheduleForms)
                .filter(new Predicate<ScheduleForm>() {
                    @Override
                    public boolean test(ScheduleForm scheduleForm) throws Exception {
                        return scheduleForm.getEm() != null;
                    }
                })
                .map(new Function<ScheduleForm, Em>() {
                    @Override
                    public Em apply(ScheduleForm scheduleForm) throws Exception {
                        Em em = scheduleForm.getEm();
                        if (em != null) {
                            em.setFsFormId(scheduleForm.getFsFormId());
                            EducationalMaterialsLocalSource.getInstance().save(em);
                        }
                        return em;
                    }
                });
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
                .filter(new Predicate<SubStage>() {
                    @Override
                    public boolean test(SubStage subStage) throws Exception {
                        return subStage.getEm() != null;
                    }
                })
                .map(new Function<SubStage, Em>() {
                    @Override
                    public Em apply(SubStage subStage) throws Exception {
                        Em em = subStage.getEm();
                        if (em != null) {
                            em.setFsFormId(subStage.getFsFormId());
                            EducationalMaterialsLocalSource.getInstance().save(em);
                        }

                        return em;
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
                .filter(new Predicate<GeneralForm>() {
                    @Override
                    public boolean test(GeneralForm generalForm) throws Exception {
                        return generalForm.getEm() != null;
                    }
                })
                .map(new Function<GeneralForm, Em>() {
                    @Override
                    public Em apply(GeneralForm generalForm) throws Exception {
                        Em em = generalForm.getEm();
                        if (em != null) {
                            em.setFsFormId(generalForm.getFsFormId());
                            EducationalMaterialsLocalSource.getInstance().save(em);
                        }

                        return em;
                    }
                });


    }


    public static String getSafeString(String string, String defaultValue) {
        if (string != null && string.length() > 0) {
            return string;
        }

        return defaultValue;
    }

}
