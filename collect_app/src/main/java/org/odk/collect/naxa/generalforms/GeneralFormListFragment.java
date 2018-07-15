package org.odk.collect.naxa.generalforms;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.Constant;
import org.odk.collect.naxa.common.DialogFactory;
import org.odk.collect.naxa.common.FormSessionManager;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.login.model.Site;

import java.util.ArrayList;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.Constant.EXTRA_PROJECT_ID;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_END;
import static org.odk.collect.naxa.common.event.DataSyncEvent.EventStatus.EVENT_START;


public class GeneralFormListFragment extends Fragment implements DisplayGeneralFormsAdapter.onGeneralFormClickListener {

    private static final String syncMsgKey = "syncmsgkey";
    private AlertDialog mAlertDialog;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private static Integer projectId_FromSiteName;
    private String TAG = "DisplayGeneralForms";
    private ArrayList<GeneralForm> generalFormList;
    private DisplayGeneralFormsAdapter mAdapter;
    private TextView noMessage;
    private SwipeRefreshLayout refreshLayout;
    private CoordinatorLayout coordinatorLayout;


    private Site loadedSite;
    private Toolbar toolbar;
    private Boolean isSiteMocked;
    private ProgressBar toolbarProgressBar;

    public GeneralFormListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        Constant.selectedFragmentId = 8;

        Bundle bundle = getArguments();
        noMessage = (TextView) rootView.findViewById(R.id.no_message);

        if (bundle != null) {
            projectId_FromSiteName = Integer.valueOf(bundle.getString(EXTRA_PROJECT_ID));
            loadedSite = bundle.getParcelable(EXTRA_OBJECT);
            // isSiteMocked = DatabaseHelper.getInstance(getActivity().getApplicationContext()).isThisSiteOffline(loadedSite.getId());
            bindUI(rootView);
            setupToolbar();
            getGeneralForms(loadedSite.getId());
            setupRecyclerView();

        }

        return rootView;
    }

    private void bindUI(View rootView) {
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_general);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.android_list);
        toolbarProgressBar = getActivity().findViewById(R.id.toolbar_progress_bar);

    }

    private void setupToolbar() {
        toolbar.setTitle(R.string.toolbar_general_forms);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            Collect.createODKDirs();
        } catch (RuntimeException e) {
            createErrorDialog(e.getMessage(), true);
            return;
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(syncMsgKey)) {
            TextView tv = (TextView) getActivity().findViewById(R.id.status_text);
            tv.setText(savedInstanceState.getString(syncMsgKey));
        }
    }

    private ArrayList<GeneralForm> getGeneralForms(String siteId) {
        if (isSiteMocked) {
//            generalFormList = ProjectFormRecordHelper.getInstance()
//                    .getGeneralForms(String.valueOf(projectId_FromSiteName));

        } else {
//            generalFormList = FormsRecordsHelper
//                    .getInstance()
//                    .getGeneralForms(siteId);
        }

        showOrHideList(generalFormList.isEmpty());
        return generalFormList;
    }

    private void showOrHideList(boolean isEmpty) {
        if (isEmpty) {
            noMessage.setVisibility(View.VISIBLE);
        } else {
            noMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    private void setupRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAdapter = new DisplayGeneralFormsAdapter(generalFormList, getActivity(), getActivity());
        mAdapter.setGeneralFormClickListener(this);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        Collect.getInstance().getActivityLogger().logOnStart(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSyncEvent(DataSyncEvent event) {
        switch (event.getEvent()) {
            case DataSyncEvent.EventType.GENERAL_FORM_DEPLOYED:
                if (event.getStatus().equals(EVENT_START)) {

                    toolbarProgressBar.setVisibility(View.VISIBLE);

                } else if (event.getStatus().equals(EVENT_END)) {

                    toolbarProgressBar.setVisibility(View.GONE);
                    mAdapter.updateList(getGeneralForms(loadedSite.getId()));

                }
                break;
        }
    }


    /**
     * Creates a dialog with the given message. Will exit the activity when the user preses "ok" if
     * shouldExit is set to true.
     *
     * @param errorMsg
     * @param shouldExit Borrowed form formChooserList
     */

    private void createErrorDialog(String errorMsg, final boolean shouldExit) {

        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog", "show");

        mAlertDialog = new AlertDialog.Builder(getActivity()).create();
        mAlertDialog.setIcon(android.R.drawable.ic_dialog_info);
        mAlertDialog.setMessage(errorMsg);
        DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Collect.getInstance().getActivityLogger().logAction(this, "createErrorDialog",
                                shouldExit ? "exitApplication" : "OK");
                        if (shouldExit) {
                            getActivity().finish();
                        }
                        break;
                }
            }
        };
        mAlertDialog.setCancelable(false);
        mAlertDialog.setButton(getString(R.string.ok), errorListener);
        mAlertDialog.show();
    }


    @Override
    public void onGuideBookButtonClicked(GeneralForm generalForm, int position) {

//        EducationalMaterialSlider.start(getActivity(), generalForm.getFsFormId(),
//                loadedSite.getProjectId(),
//                generalForm.getFormDeployedFrom(),
//                position
//        );

    }

    @Override
    public void onFormItemClicked(GeneralForm generalForm) {
        openGeneralForm(generalForm);
    }

    private void openGeneralForm(GeneralForm viewModel) {
        try {
            long formId = getFormId(viewModel.getJrFormId());
            Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, formId);
            String action = getActivity().getIntent().getAction();


            FormSessionManager formUploadPrefManager = new FormSessionManager(getActivity().getApplicationContext());
            formUploadPrefManager.setFormSession(viewModel.getFsFormId(), loadedSite.getId(), loadedSite.getProject().toString(), viewModel.getFormDeployedFrom());

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
            Timber.e("Failed to load xml form " + e.getMessage());
        } catch (CursorIndexOutOfBoundsException e) {
            DialogFactory.createGenericErrorDialog(getActivity(), getString(R.string.form_not_present)).show();
            Timber.e("Failed to load xml form " + e.getMessage());
        }
    }

    private long getFormId(String jrFormId) throws CursorIndexOutOfBoundsException, NullPointerException, NumberFormatException {

        String[] projection = new String[]{FormsProviderAPI.FormsColumns._ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH};
        String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=?";
        String[] selectionArgs = new String[]{jrFormId};
        String sortOrder = FormsProviderAPI.FormsColumns.JR_VERSION + " DESC LIMIT 1";


        Cursor cursor = getActivity().getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                projection,
                selection, selectionArgs, null);
        cursor.moveToNext();

        int columnIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID);
        long formId = Long.parseLong(cursor.getString(columnIndex));

        cursor.close();

        return formId;
    }


    @Override
    public void onFormStatusClicked() {

    }

    @Override
    public void onFormItemLongClicked(String deployedFrom) {
        ToastUtils.showShortToast(deployedFrom);
    }

    @Override
    public void onFormHistoryButtonClicked(GeneralForm generalForm) {
//        PreviousSubmissionListActivity.start(getActivity(),
//                generalForm.getFsFormId(),
//                generalForm.getFormName(),
//                generalForm.getFormName(),
//                generalForm.getFormResponse(),
//                loadedSite.getSiteId(),
//                generalForm.getResponseCount(),
//                FormsRecordsHelper.TABLE_GENERAL_FORM
//        );
    }


}

