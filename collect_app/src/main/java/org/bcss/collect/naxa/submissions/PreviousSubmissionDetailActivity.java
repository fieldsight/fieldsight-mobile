package org.bcss.collect.naxa.submissions;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.api.client.repackaged.com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.generalforms.data.FormResponse;
import org.bcss.collect.naxa.generalforms.data.GetResponce;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

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
        toolbar.setSubtitle("on " + formatSubmissionDateTime(model.getDate()));
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvFormHistory.setLayoutManager(linearLayoutManager);
        rvFormHistory.setAdapter(adapter);
        rvFormHistory.setItemAnimator(new DefaultItemAnimator());

        rvFormHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        rvFormHistory.setHasFixedSize(true);
        rvFormHistory.setNestedScrollingEnabled(false);
    }

    private void mapJSONtoViewModel(@NonNull final List<GetResponce> submissions) {


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
                Timber.e(e);
            }

            answer = new ViewModel(question.toString(), response.getAnswer(), "id", "id");
            answers.add(answer);

        }


        adapter.updateList(answers);


    }


    private Map<String, String> splitToMap(String in) {
        return Splitter.on(",").withKeyValueSeparator("=").split(in);
    }

    private String formatSubmissionDateTime(String dateTime) {


        String msg = "";
        try {
            DateTime dt = DateTime.parse(dateTime);
            msg = dt.toString(DateTimeFormat.longDateTime());


        } catch (Exception e) {
            e.printStackTrace();
            msg = "Cannot load date time";
        }


        return msg;
    }

    @Override
    public void onCardClicked(ViewModel viewModel) {

    }
}
