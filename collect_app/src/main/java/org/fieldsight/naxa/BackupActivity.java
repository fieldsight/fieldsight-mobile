package org.fieldsight.naxa;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;;
import org.odk.collect.android.application.Collect;
import org.fieldsight.naxa.common.utilities.ZipUtils;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackupActivity extends CollectAbstractActivity {
    @BindView(R.id.tv_data_info)
    TextView tvDataInfo;

    @BindView(R.id.tv_info)
    TextView tvInfo;

    @BindView(R.id.tv_progress)
    TextView tvProgress;

    @BindView(R.id.zip_progress)
    ProgressBar progressBar;

    @BindView(R.id.tv_progress_message)
    TextView tvProgressMessage;

    @BindView(R.id.btn_backup)
    Button backup;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btn_share)
    Button btnShare;

    ZipUtils zipUtils;
    StringBuilder builder;
    Handler handler;
    String destination = Environment.getExternalStorageDirectory() + File.separator + "fieldsight_compressed";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Backup all data");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        zipUtils = new ZipUtils();
        builder = new StringBuilder();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            StrictMode.VmPolicy.Builder modeBuilder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(modeBuilder.build());
        }

        HashMap<String, String> folderInfo = zipUtils.getAllInfo(Collect.ODK_ROOT);
        String infoMessage = String.format("type :  %s \n " +
                        "Name:  %s \n" +
                        "Total files: %s \n" +
                        "size = %s", folderInfo.get("type"), folderInfo.get("name"),
                folderInfo.get("total_file"), folderInfo.get("size"));

        tvDataInfo.setText(infoMessage);
        zipUtils.setZipProgressListener(new ZipUtils.ZipProgressListener() {
            @Override
            public void onZipping(String message, int progress) {
                builder.append(message + " ===> " + progress + "%\n");
                progressBar.setProgress(progress);
                tvProgress.setText(progress + "%");
            }

            @Override
            public void onComplete() {
                handler = new Handler();
                handler.post(runnable);
                btnShare.setEnabled(true);
                tvProgressMessage.setText("Project backup complete");
                tvInfo.setText("Loading zip info..");
            }
        });

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (builder.length() > 0) {
                tvInfo.setText(builder.toString());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_share)
    void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(destination)));

        sendIntent.setType("application/zip");
        startActivity(sendIntent);
    }

    @OnClick(R.id.btn_backup)
    void createZip() {
        File file = new File(destination);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Toast.makeText(this, "Deleted old backup file", Toast.LENGTH_SHORT).show();
            }
        }
        tvDataInfo.setVisibility(View.GONE);
        btnShare.setEnabled(false);
        tvProgressMessage.setText("");
        zipUtils.zipFileAtPath(Collect.ODK_ROOT, destination);
    }
}
