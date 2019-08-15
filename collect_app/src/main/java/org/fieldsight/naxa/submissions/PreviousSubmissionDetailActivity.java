package org.fieldsight.naxa.submissions;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.generalforms.data.FormResponse;
import org.fieldsight.naxa.generalforms.data.GetResponce;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class PreviousSubmissionDetailActivity extends CollectAbstractActivity implements MultiViewAdapter.OnCardClickListener {

    Toolbar toolbar;

    RecyclerView rvFormHistory;
    ArrayList<ViewModel> answers = new ArrayList<>();
    private MultiViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_history_detail);
        FormResponse model = getIntent().getParcelableExtra(EXTRA_OBJECT);
        bindUI();
        setupToolBar(model);
        if (model != null && model.getGetResponces() == null) {
            DialogFactory.createGenericErrorDialog(this, R.string.error_occured).show();
            finish();
            return;
        }

        //setQuestionAnswerAsync(model.getGetResponces());
        setupRecyclerView();
        mapJSONtoViewModel(model.getGetResponces());
    }


    private void bindUI() {
        toolbar = findViewById(R.id.toolbar);
        rvFormHistory = findViewById(R.id.form_history_detail_recycler_view);
    }

    private void setupToolBar(FormResponse model) {
        toolbar.setTitle("Submitted by " + model.getSubmittedByUsername());
        toolbar.setSubtitle("on " + formatSubmissionDateToLocal(model.getDate()));
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private void setupRecyclerView() {
        adapter = new MultiViewAdapter();

        adapter.setOnCardClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvFormHistory.setLayoutManager(linearLayoutManager);
        rvFormHistory.setAdapter(adapter);
        rvFormHistory.setItemAnimator(new DefaultItemAnimator());

        rvFormHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        rvFormHistory.setHasFixedSize(true);
        rvFormHistory.setNestedScrollingEnabled(false);
    }

    private void mapJSONtoViewModel(@NonNull final List<GetResponce> submissions) {

        AsyncTask.execute(() -> {
            ViewModel answer;

            for (int i = 0; i < submissions.size(); i++) {
                GetResponce response = submissions.get(i);
                switch (response.getType()) {
                    case "start":
                    case "end":
                    case "calculate":
                    case "submitted_by":
                    case "submittion_time":
                        continue;

                }

                StringBuilder question = new StringBuilder(response.getQuestion().toString());
                try {
                    JSONObject questionJson = new JSONObject(question.toString());
                    Iterator<String> keys = questionJson.keys();
                    if (keys.hasNext()) {
                        question.setLength(0);
                    }
                    while (keys.hasNext()) {
                        String key = keys.next();
                        question.append(questionJson.getString(key));
                        if (keys.hasNext()) {
                            question.append("\n");
                        }
                    }
                } catch (JSONException e) {
                    Timber.i("Failed to parse %s as json. his can be ignored",question);
                }

                answer = new ViewModel(question.toString(), response.getAnswer(), "id", "id");
                answers.add(answer);

            }
            adapter.updateList(answers);
        });
    }

    private String formatSubmissionDateToLocal(String dateTime) {
        String msg;
        try {
            DateTime dt = DateTime.parse(dateTime);
            msg = dt.toLocalDateTime().toString(DateTimeFormat.longDateTime());
        } catch (Exception e) {
            Timber.e(e);
            msg = "Cannot load date time";
        }
        return msg;
    }

    @Override
    public void onCardClicked(ViewModel viewModel) {
        //unused
    }
}
