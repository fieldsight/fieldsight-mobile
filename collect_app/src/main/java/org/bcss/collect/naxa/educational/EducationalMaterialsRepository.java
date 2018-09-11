package org.bcss.collect.naxa.educational;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.bcss.collect.naxa.common.BaseRepository;
import org.bcss.collect.naxa.generalforms.data.Em;


import java.util.ArrayList;
import java.util.List;

public class EducationalMaterialsRepository implements BaseRepository<Em> {

    private static EducationalMaterialsRepository INSTANCE = null;
    private final EducationalMaterialsLocalSource localSource;
    private final EducationalMaterialsRemoteSource remoteSource;

    public static EducationalMaterialsRepository getInstance(EducationalMaterialsLocalSource localSource, EducationalMaterialsRemoteSource remoteSource) {
        if (INSTANCE == null) {
            synchronized (EducationalMaterialsRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EducationalMaterialsRepository(localSource, remoteSource);
                }
            }
        }
        return INSTANCE;
    }


    private EducationalMaterialsRepository(@NonNull EducationalMaterialsLocalSource localSource, @NonNull EducationalMaterialsRemoteSource remoteSource) {
        this.localSource = localSource;
        this.remoteSource = remoteSource;
    }


    @Override
    public LiveData<List<Em>> getAll(boolean forceUpdate) {
        return null;
    }

    @Override
    public void save(Em... items) {
        localSource.save(items);
    }

    @Override
    public void save(ArrayList<Em> items) {
        localSource.save(items);
    }

    @Override
    public void updateAll(ArrayList<Em> items) {

    }


}
