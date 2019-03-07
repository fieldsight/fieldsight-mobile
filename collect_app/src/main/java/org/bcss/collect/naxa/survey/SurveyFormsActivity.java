package org.bcss.collect.naxa.survey;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.provider.FormsProviderAPI;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.RecyclerViewEmptySupport;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.login.model.Project;
import org.odk.collect.android.activities.CollectAbstractActivity;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.AnimationUtils.runLayoutAnimation;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.bcss.collect.naxa.common.SharedPreferenceUtils.isFormSaveCacheSafe;

public class SurveyFormsActivity extends CollectAbstractActivity implements TitleDescAdapter.OnCardClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;



    @BindView(R.id.appbar_general)
    AppBarLayout appbarGeneral;

    @BindView(R.id.recycler_survey_form_list)
    RecyclerViewEmptySupport recyclerSurveyFormList;

    private ActionBar actionBar;

    private Project loadedProject;
    private TitleDescAdapter adapter;
    private SurveyFormViewModel surveyFormViewModel;

    public static void start(Context context, Project loadedProject) {
        Intent intent = new Intent(context, SurveyFormsActivity.class);
        intent.putExtra(EXTRA_OBJECT, loadedProject);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_forms);
        ButterKnife.bind(this);


        try {
            loadedProject = getIntent().getParcelableExtra(EXTRA_OBJECT);
        } catch (NullPointerException e) {
            ToastUtils.showLongToast(getString(R.string.dialog_unexpected_error_title));
            finish();
        }

        setupViewModel();
        setupToolbar();
        setupRecyclerView();

        surveyFormViewModel.getByProjectId(loadedProject.getId()).observe(this,
                new Observer<List<SurveyForm>>() {
                    @Override
                    public void onChanged(@Nullable List<SurveyForm> surveyForms) {
                        if (surveyForms == null) return;

                        adapter.clear();
                        adapter.addAll(surveyForms);
                        adapter.notifyDataSetChanged();
                        runLayoutAnimation(recyclerSurveyFormList);
                    }
                });


    }


    private void setupToolbar() {

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SurveyFormsActivity.super.onBackPressed();
            }
        });

        if (actionBar != null) {
            actionBar.setTitle(loadedProject.getName());
            actionBar.setSubtitle("Survey Form");

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setupRecyclerView() {
        adapter = new TitleDescAdapter();
        adapter.setOnCardClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerSurveyFormList.setLayoutManager(linearLayoutManager);
        recyclerSurveyFormList.setItemAnimator(new DefaultItemAnimator());
        recyclerSurveyFormList.setEmptyView(findViewById(R.id.root_layout_empty_layout), getString(R.string.empty_message,"survey form(s)"), () -> {

        });

        recyclerSurveyFormList.setAdapter(adapter);

    }

    @Override
    public void onCardClicked(SurveyForm surveyForm) {

        String projectIdForSurveyForm = "0";
        String submissionUrl = generateSubmissionUrl(PROJECT, projectIdForSurveyForm, surveyForm.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_SITE_ID, projectIdForSurveyForm);

        if(isFormSaveCacheSafe(submissionUrl,projectIdForSurveyForm)){
            fillODKForm(surveyForm.getIdString());
        }
    }

    private void setupViewModel() {
        ViewModelFactory factory = ViewModelFactory.getInstance(this.getApplication());
        surveyFormViewModel = ViewModelProviders.of(this, factory).get(SurveyFormViewModel.class);
    }

    protected void fillODKForm(String idString) {
        try {
            long formId = getFormId(idString);
            Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, formId);
            String action = getIntent().getAction();

            if (Intent.ACTION_PICK.equals(action)) {
                // caller is waiting on a picked form
                setResult(RESULT_OK, new Intent().setData(formUri));
            } else {
                // caller wants to view/edit a form, so launch formentryactivity
                Intent toFormEntry = new Intent(Intent.ACTION_EDIT, formUri);
                toFormEntry.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(toFormEntry);

            }
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            DialogFactory.createGenericErrorDialog(this, e.getMessage()).show();
            Timber.e("Failed to load xml form %s", e.getMessage());
        } catch (CursorIndexOutOfBoundsException e) {
            DialogFactory.createGenericErrorDialog(this, getString(R.string.msg_form_not_present)).show();
            Timber.e("Failed to load xml form  %s", e.getMessage());
        }
    }

    protected String generateSubmissionUrl(String formDeployedFrom, String creatorsId, String fsFormId) {
        return InstancesDao.generateSubmissionUrl(formDeployedFrom, creatorsId, fsFormId);
    }


    protected long getFormId(String jrFormId) throws CursorIndexOutOfBoundsException, NullPointerException, NumberFormatException {

        String[] projection = new String[]{FormsProviderAPI.FormsColumns._ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH};
        String selection = FormsProviderAPI.FormsColumns.JR_FORM_ID + "=? AND " + "("+FormsProviderAPI.FormsColumns.IS_TEMP_DOWNLOAD + " =? OR " + FormsProviderAPI.FormsColumns.IS_TEMP_DOWNLOAD + " IS NULL)";
        String[] selectionArgs = new String[]{jrFormId, "0"};
        String sortOrder = FormsProviderAPI.FormsColumns._ID + " DESC LIMIT 1";

        Cursor cursor = getContentResolver().query(FormsProviderAPI.FormsColumns.CONTENT_URI,
                projection,
                selection, selectionArgs, sortOrder);

        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(FormsProviderAPI.FormsColumns._ID);
        long formId = Long.parseLong(cursor.getString(columnIndex));

        cursor.close();

        return formId;
    }


}
