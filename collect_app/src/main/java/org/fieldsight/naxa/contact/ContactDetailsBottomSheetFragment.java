package org.fieldsight.naxa.contact;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.common.GlideApp;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.profile.UserProfileRepository;
import org.fieldsight.naxa.v3.project.Users;
import org.odk.collect.android.utilities.ToastUtils;


public class ContactDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private View rootView;
    private Users contactDetail;
    private Button btnCallNow;
    private boolean isEditEnabled;
    private int intialUserHash;


    public static ContactDetailsBottomSheetFragment newInstance() {
        return new ContactDetailsBottomSheetFragment();
    }

    public void setContact(Users contactDetail) {
        this.contactDetail = contactDetail;

        this.intialUserHash = contactDetail.hashCode();
    }

    public void setEditEnabled() {
        isEditEnabled = true;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        boolean userHasChangedValues = contactDetail.hashCode() != intialUserHash;
        if (userHasChangedValues) {
            uploadChanges(contactDetail);
        }


    }

    private void uploadChanges(Users contactDetail) {
        UserProfileRepository.getInstance().upload(contactDetail);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.bottom_sheet_contact, container, false);
        contactDetailToViews();


        return rootView;
    }

    private void contactDetailToViews() {
        ImageView profilePicture = rootView.findViewById(R.id.user_profile_profile_picture);
        if (contactDetail.profilePicture != null) {
            GlideApp.with(this)
                    .load(contactDetail.profilePicture)
                    .centerCrop()
                    .into(profilePicture);
        }

        btnCallNow = rootView.findViewById(R.id.user_profile_call_now);
        boolean hasPhoneNumber = !TextUtils.isEmpty(contactDetail.primaryNumber) || !TextUtils.isEmpty(contactDetail.phone);
        btnCallNow.setEnabled(hasPhoneNumber);
        btnCallNow.setOnClickListener(view -> onCallNow());


        Button btnEmailNow = rootView.findViewById(R.id.user_profile_email_now);
        boolean hasEmail = !TextUtils.isEmpty(contactDetail.email);
        btnEmailNow.setEnabled(hasEmail);
        btnEmailNow.setOnClickListener(view -> onEmailNow());


        bindAndSetOrHide(R.id.user_profile_skype, contactDetail.skype, updatedValue -> {
            contactDetail.skype = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_viber, contactDetail.viber, updatedValue -> {
            contactDetail.viber = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_whatsapp, contactDetail.whatsApp, updatedValue -> {
            contactDetail.whatsApp = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_wechat, contactDetail.weChat, updatedValue -> {
            contactDetail.weChat = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_google_talk, contactDetail.googleTalk, updatedValue -> {
            contactDetail.googleTalk = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_tango, contactDetail.tango, updatedValue -> {
            contactDetail.tango = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_twitter, contactDetail.twitter, updatedValue -> {
            contactDetail.twitter = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_hike, contactDetail.hike, updatedValue -> {
            contactDetail.hike = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_qq, contactDetail.qq, updatedValue -> {
            contactDetail.qq = updatedValue;
            contactDetailToViews();
        });

        bindAndSetOrHide(R.id.user_profile_phone, contactDetail.phone, updatedValue -> {
            contactDetail.phone = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_primary_phone, contactDetail.primaryNumber, updatedValue -> {
            contactDetail.primaryNumber = updatedValue;
            contactDetailToViews();
        });

        bindAndSetOrHide(R.id.user_profile_location, contactDetail.address, updatedValue -> {
            contactDetail.address = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_email, contactDetail.email, updatedValue -> {
            contactDetail.email = updatedValue;
            contactDetailToViews();
        });

        bindAndSetOrHide(R.id.user_profile_name, contactDetail.fullName, updatedValue -> {
            contactDetail.fullName = updatedValue;
            contactDetailToViews();
        });
        bindAndSetOrHide(R.id.user_profile_role, TextUtils.isEmpty(contactDetail.role) ? "Site Supervisor" : contactDetail.role, null);
    }


    private void onEmailNow() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{contactDetail.email});
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private void onCallNow() {

        PopupMenu popupMenu = new PopupMenu(requireActivity(), btnCallNow);
        if (!TextUtils.isEmpty(contactDetail.primaryNumber)) {
            popupMenu.getMenu().add(contactDetail.primaryNumber);
        }
        if (!TextUtils.isEmpty(contactDetail.phone)) {
            popupMenu.getMenu().add(contactDetail.phone);
        }

        if (!TextUtils.isEmpty(contactDetail.secondaryNumber)) {
            popupMenu.getMenu().add(contactDetail.secondaryNumber);
        }

        if (!TextUtils.isEmpty(contactDetail.officeNumber)) {
            popupMenu.getMenu().add(contactDetail.officeNumber);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            call(item.getTitle().toString());
            return false;
        });
        popupMenu.show();
    }


    private void call(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        if (canDeviceHandleCall(callIntent)) {
            startActivity(callIntent);
        }
    }

    private boolean canDeviceHandleCall(Intent callIntent) {
        if (callIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            return true;
        }

        SnackBarUtils.showFlashbar(requireActivity(), "Device does not support phone calls");
        return false;
    }

    private void bindAndSetOrHide(int viewId, String string, ContentUpdateListener contentUpdateListener) {
        View view = rootView.findViewById(viewId);




        view.setOnClickListener(view1 -> {
            if (isEditEnabled && contentUpdateListener != null) {
                DialogFactory.showInputDialog(requireActivity(), R.layout.layout_text_input, string, contentUpdateListener)
                        .setTitle(String.format("Update %s", string))
                        .show();
            } else {
                copyTextToClipboard(requireContext(), string);
            }
        });

        boolean doesNotHaveValue = TextUtils.isEmpty(string) || TextUtils.equals("null", string);
        if (doesNotHaveValue && !isEditEnabled) {
            view.setVisibility(View.GONE);
        } else {
            ((TextView) view).setText(string);
        }

        if(isEditEnabled && TextUtils.isEmpty(string)){
            ((TextView) view).setText(R.string.not_avaliable);
        }
    }

    public interface ContentUpdateListener {
        void onUpdate(String updatedValue);


    }


    private void copyTextToClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

        ToastUtils.showLongToast(String.format("Copied %s to clipboard", text));
    }


}