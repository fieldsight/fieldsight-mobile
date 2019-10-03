package org.fieldsight.naxa.forms.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.fieldsight.naxa.common.BaseFormListFragment;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;

import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_ID;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class FieldSightFormListFragment extends BaseFormListFragment {

    private Site loadedSite;
    private String formType;

    public static FieldSightFormListFragment newInstance(String formType, Site site) {
        return newInstance(formType, site, null);
    }

    public static FieldSightFormListFragment newInstance(String loadFormType, Site site, Project project) {
        FieldSightFormListFragment fragment = new FieldSightFormListFragment();
        Bundle bundle = new Bundle();

        //hacking way to load survey forms - Nishon
        if (site == null) {
            site = new Site();
            site.setName(project.getName());
            site.setProject(project.getId());
        }

        bundle.putParcelable(Constant.EXTRA_OBJECT, site);
        bundle.putString(Constant.EXTRA_ID, loadFormType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private FieldSightFormListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
        formType = getArguments().getString(EXTRA_ID);
        setToolbarText(loadedSite.getName(), loadedSite.getName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getViewModel().loadForm(formType, loadedSite.getProject(), loadedSite.getId(), loadedSite.getTypeId())
                .observe(this, fieldSightForms -> {
                    Timber.i(fieldSightForms.toString());
                    showEmptyLayout(fieldSightForms.isEmpty());
                    updateList(fieldSightForms, loadedSite.getId());
                });
    }

}
