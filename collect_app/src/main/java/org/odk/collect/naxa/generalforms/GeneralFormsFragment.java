package org.odk.collect.naxa.generalforms;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rockerhieu.rvadapter.states.StatesRecyclerViewAdapter;

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.naxa.common.DialogFactory;
import org.odk.collect.naxa.common.OnFormItemClickListener;
import org.odk.collect.naxa.common.SharedPreferenceUtils;
import org.odk.collect.naxa.generalforms.data.GeneralForm;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.network.APIEndpoint;
import org.odk.collect.naxa.site.FragmentHostActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.odk.collect.naxa.common.Constant.FormDeploymentFrom.SITE;

public class GeneralFormsFragment extends Fragment implements OnFormItemClickListener<GeneralForm> {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SyncCommentLifecycleObserver syncCommentLifecycleObserver;

    private GeneralFormViewModel viewModel;

    @BindView(R.id.android_list)
    RecyclerView recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;

    Unbinder unbinder;
    private GeneralFormsAdapter generalFormsAdapter;
    private StatesRecyclerViewAdapter statesRecyclerViewAdapter;
    private Site loadedSite;

    public static GeneralFormsFragment newInstance(Site loadedSite) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        GeneralFormsFragment generalFormsFragment = new GeneralFormsFragment();
        generalFormsFragment.setArguments(bundle);
        return generalFormsFragment;

    }

    public GeneralFormsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        viewModel = FragmentHostActivity.obtainViewModel(getActivity());

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
        viewModel.loadGeneralForms(true, loadedSite.getId())
                .observe(this, generalForms -> {
                    Timber.i("General forms data has been changed");
                    generalFormsAdapter.updateList(generalForms);

                });
    }

    private void setupListAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        generalFormsAdapter = new GeneralFormsAdapter(new ArrayList<>(0), this);
//        View emptyView = LayoutInflater.from(getActivity()).inflate(R.layout.empty_layout,rootLayout);
//       RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);rootLayout.setLayoutParams(lp);
//        statesRecyclerViewAdapter = new StatesRecyclerViewAdapter(generalFormsAdapter, null, emptyView, null);
        recyclerView.setAdapter(generalFormsAdapter);
    }

    @Override
    public void onGuideBookButtonClicked(GeneralForm generalForm, int position) {

    }

    @Override
    public void onFormItemClicked(GeneralForm generalForm) {
        String submissionUrl = generateSubmissionUrl(PROJECT, loadedSite.getProject(), generalForm.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);

        openGeneralForm(generalForm.getIdString());
    }

    @Override
    public void onFormItemLongClicked(GeneralForm generalForm) {

    }

    @Override
    public void onFormHistoryButtonClicked(GeneralForm generalForm) {

    }

    private void openGeneralForm(String idString) {
        try {
            long formId = getFormId(idString);
            Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, formId);
            String action = getActivity().getIntent().getAction();


            if (Intent.ACTION_PICK.equals(action)) {
                // caller is waiting on a picked form
                getActivity().setResult(RESULT_OK, new Intent().setData(formUri));
            } else {
                // caller wants to view/edit a form, so launch formentryactivity
                Intent toFormEntry = new Intent(Intent.ACTION_EDIT, formUri);
                toFormEntry.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(toFormEntry);

            }
        } catch (NullPointerException | NumberFormatException e) {
            DialogFactory.createGenericErrorDialog(getActivity(), e.getMessage()).show();
            Timber.e("Failed to load xml form %s", e.getMessage());
        } catch (CursorIndexOutOfBoundsException e) {
            DialogFactory.createGenericErrorDialog(getActivity(), getString(R.string.form_not_present)).show();
            Timber.e("Failed to load xml form  %s", e.getMessage());
        }


    }

    private String generateSubmissionUrl(String formDeployedFrom, String creatorsId, String fsFormId) {
        String submissionUrl = APIEndpoint.BASE_URL + APIEndpoint.FORM_SUBMISSION_PAGE;

        switch (formDeployedFrom) {
            case PROJECT:
                submissionUrl += "project/" + fsFormId + "/" + creatorsId;
                break;
            case SITE:
                submissionUrl += fsFormId + "/" + creatorsId;
                break;
            default:
                throw new RuntimeException("Unknown form deployed");
        }

        return submissionUrl;

    }

    private long getFormId(String jrFormId) throws CursorIndexOutOfBoundsException, NullPointerException, NumberFormatException {

        String[] projection = new String[]{FormsProviderAPI.FormsColumns._ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH};
        String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
        String[] selectionArgs = new String[]{jrFormId};
        String sortOrder = FormsProviderAPI.FormsColumns.JR_VERSION + " DESC LIMIT 1";

        Cursor cursor = getActivity().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                projection,
                selection, selectionArgs, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID);
        long formId = Long.parseLong(cursor.getString(columnIndex));

        cursor.close();

        return formId;
    }

}
