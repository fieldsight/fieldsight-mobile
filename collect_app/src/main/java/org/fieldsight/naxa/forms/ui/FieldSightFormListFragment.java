package org.fieldsight.naxa.forms.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.BaseFormListFragment;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.login.model.Site;

import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_ID;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class FieldSightFormListFragment extends BaseFormListFragment {

    private Site loadedSite;
    private String formType;

    public static FieldSightFormListFragment newInstance(String loadFormType, Site loadedSite) {
        FieldSightFormListFragment fragment = new FieldSightFormListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.EXTRA_OBJECT, loadedSite);
        bundle.putString(Constant.EXTRA_ID, loadFormType);
        fragment.setArguments(bundle);
        fragment.setArguments(bundle);
        return fragment;
    }

    private FieldSightFormListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
            formType = getArguments().getString(EXTRA_ID);
        }
        if (loadedSite == null || formType == null) {
            requireActivity().onBackPressed();
            SnackBarUtils.showFlashbar(requireActivity(), getString(R.string.dialog_unexpected_error_title));
            return;
        }

        setToolbarText(loadedSite.getName(), loadedSite.getName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getViewModel().loadForm(formType, loadedSite.getProject(), loadedSite.getId())
                .observe(this, fieldSightForms -> {
                    Timber.i(fieldSightForms.toString());
                    updateList(fieldSightForms, loadedSite.getId());
                });
    }
}
