package org.odk.collect.naxa.stages;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.odk.collect.naxa.stages.data.Stage;

import java.util.List;

public class StageViewModel extends ViewModel {

    private final StageFormRepository repository;


    public StageViewModel(StageFormRepository repository){
        this.repository = repository;
    }

    public LiveData<List<Stage>> loadStages(boolean forceUpdate){
        if(forceUpdate){

        }

        return repository.getAll();
    }


}


