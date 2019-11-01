package org.fieldsight.naxa.site.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.db.SiteLocalSource;

public class SiteListViewModel extends ViewModel {


    public LiveData<PagedList<Site>> getPagedSites(String projectId) {
        DataSource.Factory<Integer, Site> localSource = SiteLocalSource.getInstance().getPagedSites(projectId);
        return new LivePagedListBuilder<>(
                localSource, 50).build();
    }

    public SiteListViewModel() {

    }

}
