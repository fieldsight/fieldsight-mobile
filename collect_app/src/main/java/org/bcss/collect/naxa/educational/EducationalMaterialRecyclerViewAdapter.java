package org.bcss.collect.naxa.educational;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.Connectivity;
import org.bcss.collect.naxa.common.GlideApp;

import java.io.File;
import java.util.List;

/**
 * Created by susan on 7/5/2017.
 */

public class EducationalMaterialRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String TAG = "Education Material";
    // The items to display in your RecyclerView
    private List<Object> items;

    private static final int TEXT_VIEW = 0, IMAGE = 1, VIDEO = 2, PDF = 3;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public EducationalMaterialRecyclerViewAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * This method creates different RecyclerView.ViewHolder objects based on the item view type.\
     *
     * @param parent   ViewGroup container for the item
     * @param viewType type of view to be inflated
     * @return viewHolder to be inflated
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TEXT_VIEW:
                View v1 = inflater.inflate(R.layout.viewholder_text, parent, false);
                viewHolder = new ViewHolderText(v1);
                break;
            case IMAGE:
                View v2 = inflater.inflate(R.layout.viewholder_image, parent, false);
                viewHolder = new ViewHolderImage(v2);
                break;
            case VIDEO:
                View v3 = inflater.inflate(R.layout.viewholder_video, parent, false);
                viewHolder = new ViewHolderVideo(v3);
                break;
            case PDF:
                View v4 = inflater.inflate(R.layout.viewholder_pdf, parent, false);
                viewHolder = new ViewHolderPDF(v4);
                break;
            default:
                View v = inflater.inflate(R.layout.viewholder_simple_text, parent, false);
                viewHolder = new ViewHolderSimpleTextOnly(v);
                break;
        }
        return viewHolder;
    }

    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param holder   The type of RecyclerView.ViewHolder to populate
     * @param position Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        switch (holder.getItemViewType()) {
            case TEXT_VIEW:

                ViewHolderText vh1 = (ViewHolderText) holder;
                configureViewHolderText(vh1, position);
                break;
            case IMAGE:
                ViewHolderImage vh2 = (ViewHolderImage) holder;
                configureViewHolderImage(vh2, position);
                break;
            case VIDEO:
                ViewHolderVideo vh3 = (ViewHolderVideo) holder;
                configureViewHolderVideo(vh3, position);
                break;
            case PDF:
                ViewHolderPDF vh4 = (ViewHolderPDF) holder;
                configureViewHolderPDF(vh4, position);
                break;
            default:
                ViewHolderSimpleTextOnly vh = (ViewHolderSimpleTextOnly) holder;
                configureDefaultViewHolder(vh, position);
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Edu_Title_Desc_Model) {
            return TEXT_VIEW;
        } else if (items.get(position) instanceof Edu_Image_Model) {
            return IMAGE;
        } else if (items.get(position) instanceof Edu_Video_Model) {
            return VIDEO;
        } else if (items.get(position) instanceof Edu_PDF_Model) {
            return PDF;
        }
        return -1;
    }

    private void configureDefaultViewHolder(ViewHolderSimpleTextOnly vh, int position) {
        vh.getsTextView().setText((CharSequence) items.get(position));
    }

    //Text View Holder
    private void configureViewHolderText(ViewHolderText vh1, int position) {
        final Edu_Title_Desc_Model user = (Edu_Title_Desc_Model) items.get(position);
        if (user != null) {
            ViewHolderText.label1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, EduMat_ViewTextTitleDetailsActivity.class);
                    bundle.putSerializable("TEXT_TITLE", user.getTitle());
                    bundle.putSerializable("TEXT_DESC", user.getDesc());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            ViewHolderText.label2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, EduMat_ViewTextTitleDetailsActivity.class);
                    bundle.putSerializable("TEXT_TITLE", user.getTitle());
                    bundle.putSerializable("TEXT_DESC", user.getDesc());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            ViewHolderText.linearClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, EduMat_ViewTextTitleDetailsActivity.class);
                    bundle.putSerializable("TEXT_TITLE", user.getTitle());
                    bundle.putSerializable("TEXT_DESC", user.getDesc());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            vh1.getLabel1().setText(user.getTitle());
            vh1.getLabel2().setText(user.getDesc());
        }
    }

    //NotificaitonImage View Holder
    private void configureViewHolderImage(ViewHolderImage vh2, int position) {
        final Edu_Image_Model edu_image_model = (Edu_Image_Model) items.get(position);
        if (edu_image_model != null) {

            Log.d(TAG, "configureViewHolderImage: " + edu_image_model.getThumbImageOn());

            if (edu_image_model.getThumbImageOff() != null || !edu_image_model.getThumbImageOff().equals("")) {
                Log.d(TAG, "configureViewHolderImage: " + edu_image_model.getThumbImageOff());
                File f = new File(edu_image_model.getThumbImageOff());
                ViewHolderImage.imageView.setImageURI(Uri.fromFile(f));
            } else {
                if (Connectivity.isConnected(context)) {
                    GlideApp.with(context.getApplicationContext())
                            .load(edu_image_model.getThumbImageOn())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ViewHolderImage.imageView);
                }
            }


            try {

                ViewHolderImage.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(context, EduMat_ViewImageDetailsActivity.class);
                        bundle.putSerializable("IMAGE_URL_ON", edu_image_model.getThumbImageOn());
                        bundle.putSerializable("IMAGE_URL_OFF", edu_image_model.getThumbImageOff());
                        bundle.putSerializable("IMAGE_TITLE", edu_image_model.getTitle());
//                        bundle.putSerializable("IMAGE_DESC", edu_image_model.getDesc());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });

                ViewHolderImage.linearClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(context, EduMat_ViewImageDetailsActivity.class);
                        bundle.putSerializable("IMAGE_URL_ON", edu_image_model.getThumbImageOn());
                        bundle.putSerializable("IMAGE_URL_OFF", edu_image_model.getThumbImageOff());
                        bundle.putSerializable("IMAGE_TITLE", edu_image_model.getTitle());
//                        bundle.putSerializable("IMAGE_DESC", edu_image_model.getDesc());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });

            } catch (Exception e) {
                e.getLocalizedMessage();
            }

            vh2.getiTitle().setText(edu_image_model.getTitle());
//            vh2.getiDesc().setText(edu_image_model.getDesc());
        }
    }

    //Video View Holder
    private void configureViewHolderVideo(ViewHolderVideo vh3, int position) {

        final Edu_Video_Model edu_video_model = (Edu_Video_Model) items.get(position);
        if (edu_video_model != null) {
            ViewHolderVideo.videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, EduMat_ViewVideoDetailsActivity.class);
                    bundle.putSerializable("VIDEO_THUMB_URL", edu_video_model.getThumbnail_url());
                    bundle.putSerializable("VIDEO_URL", edu_video_model.getVideoFile());
                    bundle.putSerializable("VIDEO_TITLE", edu_video_model.getTitle());
                    bundle.putSerializable("VIDEO_DESC", edu_video_model.getDesc());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            ViewHolderVideo.vTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, EduMat_ViewVideoDetailsActivity.class);
                    bundle.putSerializable("VIDEO_THUMB_URL", edu_video_model.getThumbnail_url());
                    bundle.putSerializable("VIDEO_URL", edu_video_model.getVideoFile());
                    bundle.putSerializable("VIDEO_TITLE", edu_video_model.getTitle());
                    bundle.putSerializable("VIDEO_DESC", edu_video_model.getDesc());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            ViewHolderVideo.linearClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, EduMat_ViewVideoDetailsActivity.class);
                    bundle.putSerializable("VIDEO_THUMB_URL", edu_video_model.getThumbnail_url());
                    bundle.putSerializable("VIDEO_URL", edu_video_model.getVideoFile());
                    bundle.putSerializable("VIDEO_TITLE", edu_video_model.getTitle());
                    bundle.putSerializable("VIDEO_DESC", edu_video_model.getDesc());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            vh3.getvTitle().setText(edu_video_model.getTitle());
//            vh3.getvTitle().setText(edu_video_model.getDesc());
        }
    }

    //PDF View Holder
    private void configureViewHolderPDF(ViewHolderPDF vh4, int position) {

        final Edu_PDF_Model edu_pdf_model = (Edu_PDF_Model) items.get(position);
        if (edu_pdf_model != null) {
            ViewHolderPDF.label1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    File file = new File(edu_pdf_model.getPdfUrlOff());
                    Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                    Log.d("SUSAN", "PDFonTouch: " + path);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    target.setDataAndType(path, "application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

//                    Uri fileUri = FileProvider.getUriForFile(context, Collect.getInstance().getString(R.string.android_file_provider_fieldsight), file);
//                    Log.d(TAG, "PDF_URI: " + fileUri);
//                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    target.setDataAndType(fileUri, "application/pdf");
//                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.dialog_pdf_application_title);
                        builder.setMessage(R.string.dialog_pdf_desc);
                        builder.setPositiveButton(R.string.dialog_pdf_okay,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                        marketIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.pdfviewer&hl=en&rdid=com.google.android.apps.pdfviewer"));
                                        context.startActivity(marketIntent);
                                    }
                                });
                        builder.setNegativeButton(R.string.dialog_pdf_no, null);
                        builder.create().show();
                    }

//                    Bundle bundle = new Bundle();
//                    Intent intent = new Intent(context, EduMat_ViewPDFDetailsActivity.class);
//                    bundle.putSerializable("PDF_URL_ONLINE", edu_pdf_model.getPdfUrlOn());
//                    bundle.putSerializable("PDF_URL_OFFLINE", edu_pdf_model.getPdfUrlOff());
//                    bundle.putSerializable("PDF_TITLE", edu_pdf_model.getTitle());
//                    intent.putExtras(bundle);
//                    context.startActivity(intent);
                }
            });

            ViewHolderPDF.linearClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(edu_pdf_model.getPdfUrlOff());
                    Uri path = Uri.fromFile(file);
                    Log.d("SUSAN", "PDFonTouch: " + path);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(path, "application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

//                    Uri fileUri = FileProvider.getUriForFile(context, Collect.getInstance().getString(R.string.android_file_provider_fieldsight), file);
//                    Log.d(TAG, "PDF_URI: " + fileUri);
//                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    target.setDataAndType(fileUri, "application/pdf");
//                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.dialog_pdf_application_title);
                        builder.setMessage(R.string.dialog_pdf_desc);
                        builder.setPositiveButton(R.string.dialog_pdf_okay,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                                        marketIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.pdfviewer&hl=en&rdid=com.google.android.apps.pdfviewer"));
                                        context.startActivity(marketIntent);
                                    }
                                });
                        builder.setNegativeButton(R.string.dialog_pdf_no, null);
                        builder.create().show();
                    }
                }
            });

            vh4.getLabel1().setText(edu_pdf_model.getTitle());
//            vh3.getvTitle().setText(edu_video_model.getDesc());
        }
    }
}
