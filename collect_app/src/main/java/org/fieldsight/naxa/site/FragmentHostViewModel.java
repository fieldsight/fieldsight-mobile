package org.fieldsight.naxa.site;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.fieldsight.naxa.login.model.Site;

public class FragmentHostViewModel extends ViewModel {
    private final MutableLiveData<Site> loadedSite = new MutableLiveData<Site>();

    public void select(Site item) {
        loadedSite.setValue(item);
    }

    public LiveData<Site> getSelected() {
        return loadedSite;
    }
}
