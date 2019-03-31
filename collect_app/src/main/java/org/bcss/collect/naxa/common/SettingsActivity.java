package org.bcss.collect.naxa.common;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.BaseActivity;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.text_input_layout_base_url)
    TextInputLayout textInputLayoutBaseUrl;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        textInputLayoutBaseUrl.getEditText().setText(FieldSightUserSession.getServerUrl(this));
        setupToolbar();

        textInputLayoutBaseUrl.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayoutBaseUrl.getEditText().setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Server");
        initBack();
    }

    @OnClick(R.id.btn_save)
    public void saveUrl() {
        String url = textInputLayoutBaseUrl.getEditText().getText().toString();
        if (isValidUrl(url)) {
            FieldSightUserSession.setServerUrl(this, url);
            ToastUtils.showLongToast("Server Changed");
            ServiceGenerator.clearInstance();
            onBackClicked(false);
        } else {
            textInputLayoutBaseUrl.getEditText().setError("This url is invalid");
        }
    }

    @OnClick(R.id.btn_default)
    public void restoreDefault() {
        FieldSightUserSession.setServerUrl(this, APIEndpoint.BASE_URL);
        textInputLayoutBaseUrl.getEditText().setText(FieldSightUserSession.getServerUrl(this));
    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    @Override
    public void onBackClicked(boolean isHome) {
        this.finish();
    }
}
