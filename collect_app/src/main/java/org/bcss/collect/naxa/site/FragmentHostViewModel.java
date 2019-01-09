package org.bcss.collect.naxa.site;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.bcss.collect.naxa.login.model.Site;

public class FragmentHostViewModel extends ViewModel {
    private final MutableLiveData<Site> loadedSite = new MutableLiveData<Site>();

    public void select(Site item) {
        loadedSite.setValue(item);
    }

    public LiveData<Site> getSelected() {
        return loadedSite;
    }
}
