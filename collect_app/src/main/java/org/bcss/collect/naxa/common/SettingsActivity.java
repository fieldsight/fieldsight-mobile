package org.bcss.collect.naxa.common;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.webkit.URLUtil;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.network.ServiceGenerator;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends CollectAbstractActivity {

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
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    @OnClick(R.id.btn_save)
    public void saveUrl() {
        String url = textInputLayoutBaseUrl.getEditText().getText().toString();
        if (isValidUrl(url)) {
            FieldSightUserSession.setServerUrl(this, url);
            FlashBarUtils.showFlashbar(this, "server url has been changed");
            ServiceGenerator.clearInstance();
        } else {
            textInputLayoutBaseUrl.getEditText().setError("This url is invalid");
        }
    }

    @OnClick(R.id.btn_default)
    public void restoreDefault() {
        FieldSightUserSession.setServerUrl(this, APIEndpoint.BASE_URL);
        textInputLayoutBaseUrl.getEditText().setText(FieldSightUserSession.getServerUrl(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

}
