package org.odk.collect.naxa.generalforms.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.odk.collect.naxa.common.BaseLocalDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.api.client.util.Preconditions.checkNotNull;

public class GeneralFormRepository implements BaseLocalDataSource<GeneralForm> {

    private static GeneralFormRepository INSTANCE = null;
    private final GeneralFormLocalSource localSource;
    private final GeneralFormRemoteSource remoteSource;

    public static GeneralFormRepository getInstance(GeneralFormLocalSource localSource, GeneralFormRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (GeneralFormRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GeneralFormRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }


    private GeneralFormRepository(@NonNull GeneralFormLocalSource localSource, @NonNull GeneralFormRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }


    @Override
    public LiveData<List<GeneralForm>> getById(@NonNull String id) {
        remoteSource.getAll();
        return localSource.getById(id);
    }

    @Override
    public LiveData<List<GeneralForm>> getAll() {
        return localSource.getAll();
    }

    @Override
    public void save(GeneralForm... items) {
        localSource.save(items);
    }

    @Override
    public void save(ArrayList<GeneralForm> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<GeneralForm> items) {
        localSource.updateAll(items);
    }


    public void deleteAll() {
        localSource.deleteAll();
    }
}
