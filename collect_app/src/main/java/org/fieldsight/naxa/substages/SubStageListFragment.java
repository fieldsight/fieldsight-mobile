package org.fieldsight.naxa.substages;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.reflect.TypeToken;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.Constant;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.common.GSONInstance;
import org.fieldsight.naxa.common.OnFormItemClickListener;
import org.fieldsight.naxa.common.RecyclerViewEmptySupport;
import org.fieldsight.naxa.common.SharedPreferenceUtils;
import org.fieldsight.naxa.common.ViewModelFactory;
import org.fieldsight.naxa.forms.ui.EducationalMaterialListActivity;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.previoussubmission.model.SubStageAndSubmission;
import org.fieldsight.naxa.stages.data.SubStage;
import org.fieldsight.naxa.submissions.PreviousSubmissionListActivity;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.utilities.ToastUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static org.fieldsight.naxa.common.Constant.EXTRA_FORM_DEPLOYED_FORM;
import static org.fieldsight.naxa.common.Constant.EXTRA_ID;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.fieldsight.naxa.common.Constant.EXTRA_POSITION;
import static org.fieldsight.naxa.common.SharedPreferenceUtils.isFormSaveCacheSafe;
import static org.fieldsight.naxa.helpers.FSInstancesDao.generateSubmissionUrl;

public class SubStageListFragment extends Fragment implements OnFormItemClickListener<SubStage> {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.android_list)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;

    @BindView(R.id.root_layout_empty_layout)
    LinearLayout emptyLayout;

    private SubStageListAdapter listAdapter;
    private Site loadedSite;
    private String stagePosition;


    private Unbinder unbinder;


    private String substages;

    public static SubStageListFragment newInstance(@NonNull Site loadedSite, @NonNull String stageId, @NonNull String stagePosition, @NonNull String deployedFrom) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        bundle.putString(EXTRA_ID, stageId);
        bundle.putString(EXTRA_POSITION, stagePosition);
        bundle.putString(EXTRA_FORM_DEPLOYED_FORM, deployedFrom);
        SubStageListFragment subStageListFragment = new SubStageListFragment();
        subStageListFragment.setArguments(bundle);
        return subStageListFragment;

    }


    public static SubStageListFragment newInstance(Site loadedSite, List<SubStage> subStageList, String stagePosition) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        bundle.putString("substages", GSONInstance.getInstance().toJson(subStageList));
//        bundle.putString(EXTRA_ID, stageId);
        bundle.putString(EXTRA_POSITION, stagePosition);
        SubStageListFragment subStageListFragment = new SubStageListFragment();
        subStageListFragment.setArguments(bundle);
        return subStageListFragment;

    }

    public SubStageListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);

        stagePosition = getArguments().getString(EXTRA_POSITION);

        substages = getArguments().getString("substages");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        setToolbarText();

        return rootView;
    }


    private void setToolbarText() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_substages);
        toolbar.setSubtitle(loadedSite.getName());
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();

        parseSubstage().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subStageAndSubmissions -> {
                    listAdapter.updateList(subStageAndSubmissions);
                }, Timber::e);
    }

    private Observable<List<SubStageAndSubmission>> parseSubstage() {
        return Observable.fromCallable(new Callable<List<SubStageAndSubmission>>() {
            @Override
            public List<SubStageAndSubmission> call() throws Exception {
                Type type = new TypeToken<ArrayList<SubStage>>() {
                }.getType();
                List<SubStage> substageList = GSONInstance.getInstance().fromJson(substages, type);

                List<SubStageAndSubmission> subStageAndSubmissions = new ArrayList<>();
                SubStageAndSubmission subStageAndSubmission;
                for (SubStage subStage : substageList) {

                    subStageAndSubmission = new SubStageAndSubmission();
                    subStageAndSubmission.setSubStage(subStage);

                    subStageAndSubmissions.add(subStageAndSubmission);
                }
                return subStageAndSubmissions;
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupListAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(emptyLayout, getString(R.string.empty_message, "staged FORMS"), () -> {

        });
        listAdapter = new SubStageListAdapter(new ArrayList<>(0), stagePosition, this);
        recyclerView.setAdapter(listAdapter);


    }


    @Override
    public void onGuideBookButtonClicked(SubStage subStage, int position) {
        EducationalMaterialListActivity.start(requireActivity(), subStage.getFsFormId());
    }

    @Override
    public void onFormItemClicked(SubStage subStage, int position) {

        String formDeployedFrom = subStage.getFormDeployedFrom();
        Timber.i("Nishon %s", formDeployedFrom);
        String submissionUrl = generateSubmissionUrl(formDeployedFrom, loadedSite.getId(), subStage.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_SITE_ID, loadedSite.getId());

        if (isFormSaveCacheSafe(submissionUrl, loadedSite.getId())) {
            fillODKForm(subStage.getJrFormId());
        }

    }

    protected void fillODKForm(String idString) {
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

        } catch (CursorIndexOutOfBoundsException e) {
            DialogFactory.createGenericErrorDialog(getActivity(), getString(R.string.msg_form_not_present)).show();
            Timber.e("Failed to load xml form with id %s %s", idString, e.getMessage());
            Timber.e(e);
        } catch (NullPointerException | NumberFormatException e) {
            Timber.e(e);
            DialogFactory.createGenericErrorDialog(getActivity(), e.getMessage()).show();
            Timber.e("Failed to load xml form %s", e.getMessage());
        }
    }

    private long getFormId(String jrFormId) throws CursorIndexOutOfBoundsException, NullPointerException, NumberFormatException {

        String[] projection = new String[]{FormsProviderAPI.FormsColumns._ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH};
        String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=? AND " + "(" + FormsProviderAPI.FormsColumns.IS_TEMP_DOWNLOAD + " =? OR " + FormsProviderAPI.FormsColumns.IS_TEMP_DOWNLOAD + " IS NULL)";
        String[] selectionArgs = new String[]{jrFormId, "0"};
        String sortOrder = FormsProviderAPI.FormsColumns._ID + " DESC LIMIT 1";

        Cursor cursor = requireActivity().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                projection,
                selection, selectionArgs, sortOrder);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID);
        long formId = Long.parseLong(cursor.getString(columnIndex));

        cursor.close();

        return formId;
    }

    @Override
    public void onFormItemLongClicked(SubStage subStage) {
        ToastUtils.showShortToastInMiddle("Not implemented");
    }

    @Override
    public void onFormHistoryButtonClicked(SubStage subStage) {

        PreviousSubmissionListActivity.start(getActivity(),
                subStage.getFsFormId(),
                subStage.getName(),
                subStage.getName(),
                null,
                loadedSite.getId(),
                null,
                Constant.FormType.STAGED
        );
    }


}
