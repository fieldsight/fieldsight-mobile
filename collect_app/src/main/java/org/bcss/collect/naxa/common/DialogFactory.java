package org.bcss.collect.naxa.common;

/**
 * Created by Nishon Tandukar on 16 Jun 2017 .
 *
 * @email nishon.tan@gmail.com
 */


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.android.fragments.dialogs.SimpleDialog;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.network.APIEndpoint;
import org.bcss.collect.naxa.site.SiteListFragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public final class DialogFactory {


    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createDataSyncErrorDialog(Context context, String message, String code) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(context.getString(R.string.dialog_error_title_sync_failed, code))
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }


    public static Dialog createMessageDialog(final Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }


    public static AlertDialog.Builder createActionDialog(final Context context, String title, String message) {
        return new AlertDialog.Builder(context, R.style.RiseUpDialog)
                .setTitle(title).setCancelable(false)
                .setMessage(message);
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static Dialog createDataSyncErrorDialog(Context context, @StringRes int messageResource, String responseCode) {
        return createDataSyncErrorDialog(context, context.getString(messageResource), responseCode);
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.RiseUpDialog);
        progressDialog.setTitle(message);

        return progressDialog;
    }


    public static ProgressDialog createProgressDialogHorizontal(Context context, String title) {
        ProgressDialog progress = new ProgressDialog(context, R.style.RiseUpDialog);
        progress.setTitle(title);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setProgressNumberFormat(null);
        progress.setProgressPercentFormat(null);

        return progress;
    }


    public static ProgressDialog createProgressBarDialog(Context context, String title, String message) {


        final ProgressDialog progress = new ProgressDialog(context, R.style.RiseUpDialog);

        DialogInterface.OnClickListener buttonListerns =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                progress.dismiss();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:

                                break;
                        }
                    }
                };


        progress.setMessage(message);
        progress.setTitle(title);

        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.dialog_action_hide), buttonListerns);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setCancelable(false);


        return progress;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }

    public static AlertDialog showErrorDialog(Context context, String[] errors) {
        return showListDialog(context, context.getString(R.string.dialog_unexpected_error_title), errors);
    }

    private static AlertDialog showListDialog(Context context, String title, String[] listItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(listItems, null);

        return builder.create();
    }


    public static AlertDialog.Builder createListActionDialog(Context context, String title, CharSequence[] items, DialogInterface.OnClickListener onClickListener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RiseUpDialog);
        builder.setTitle(title).setItems(items, onClickListener);
        return builder;
    }


    public static AlertDialog.Builder createSiteListDialog(Context context, List<Site> items, DialogInterface.OnClickListener listener) {
        ListAdapter adapter = new ArrayAdapter<Site>(context, R.layout.sub_site_list_item, items) {
            class SiteSubsiteViewHolder {
                TextView tv_site_name, tv_subsiteName, icon_text;
                private boolean isSite;
                 SiteSubsiteViewHolder(View view, boolean isSite) {
                    this.isSite = isSite;
                    if(isSite) {
                        tv_site_name = view.findViewById(R.id.tv_site_name);
                    }
                    tv_subsiteName = view.findViewById(R.id.tv_subsite_name);
                    icon_text = view.findViewById(R.id.icon_text);
                }

                public boolean isSite() {
                    return this.isSite;
                }

            }
            HashMap<String, SiteSubsiteViewHolder> holderMap = new HashMap<>();

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                SiteSubsiteViewHolder holder;
                if(position == 0) {
                    if(holderMap.containsKey("site")) {
                        holder = holderMap.get("site");
                    } else {
                        convertView = inflater.inflate(R.layout.subsite_list_header, parent, false);
                         holder = new SiteSubsiteViewHolder(convertView, true);
                        holderMap.put("site", holder);
                        convertView.setTag("site");
                    }
                } else {
                    if(holderMap.containsKey("subsite")) {
                        holder = holderMap.get("subsite");
                    }
                    else {
                        convertView = inflater.inflate(R.layout.sub_site_list_item, parent, false);
                        holder = new SiteSubsiteViewHolder(convertView, false);
                        holderMap.put("subsite", holder);
                        convertView.setTag("subsite");
                    }
                }

                if(holder.isSite()) {
                    holder.tv_site_name.setText(getItem(position).getName());
                }
                holder.tv_subsiteName.setText(getItem(position).getName());
                holder.icon_text.setText(getItem(position).getName().charAt(0));
                return convertView;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.RiseUpDialog);
        builder.setAdapter(adapter, listener);
        return builder;
    }

    private static AlertDialog.Builder showCustomLayoutDialog(Context context, View view) {

        return new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false);
//        Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.setContentView(view);
//        return dialog;
    }


    public static DatePickerDialog createDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener listener) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH);
        int curDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, listener, curYear, curMonth, curDay);
        return datePickerDialog;
    }

    public static AlertDialog.Builder createActionConsentDialog(Context context, String title, String message) {
        View viewInflated = LayoutInflater.from(Collect.getInstance()).inflate(R.layout.dialog_site_project_filter, null, false);
        TextInputLayout textInputLayout = viewInflated.findViewById(R.id.text_input_layout);
        AlertDialog.Builder dialog = showCustomLayoutDialog(context, viewInflated);

        return dialog;
    }
}
