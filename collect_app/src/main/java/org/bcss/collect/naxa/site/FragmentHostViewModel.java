package org.bcss.collect.naxa.site;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
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
