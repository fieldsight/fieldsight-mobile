package org.odk.collect.naxa.generalforms;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.generalforms.db.GeneralFormRepository;
import org.odk.collect.naxa.generalforms.db.GeneralFormViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final GeneralFormRepository repository;
    private final Application application;

    public ViewModelFactory(Application application, GeneralFormRepository repository) {
        this.application = application;
        this.repository = repository;
    }

    public static ViewModelFactory getInstance(Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    GeneralFormRepository repo = GeneralFormRepository.getInstance(
                            GeneralFormLocalSource.getInstance(), GeneralFormRemoteSource.getInstance());
                    INSTANCE = new ViewModelFactory(application, repo);
                }
            }
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GeneralFormViewModel.class)) {
            //noinspection unchecked
            return (T) new GeneralFormViewModel(application, repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class" + modelClass.getName());
    }
}
