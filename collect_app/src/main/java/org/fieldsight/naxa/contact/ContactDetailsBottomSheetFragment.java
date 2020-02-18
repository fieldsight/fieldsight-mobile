package org.fieldsight.naxa.contact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.GlideApp;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.v3.project.Users;
import org.odk.collect.android.utilities.ToastUtils;


public class ContactDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private View rootView;
    private Users contactDetail;
    private Button btnCallNow, btnEmailNow;

    public static ContactDetailsBottomSheetFragment newInstance() {
        return new ContactDetailsBottomSheetFragment();
    }

    public void setContact(Users contactDetail) {
        this.contactDetail = contactDetail;
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


        btnEmailNow = rootView.findViewById(R.id.user_profile_email_now);
        boolean hasEmail = !TextUtils.isEmpty(contactDetail.email);
        btnEmailNow.setEnabled(hasEmail);
        btnEmailNow.setOnClickListener(view -> onEmailNow());


        bindAndSetOrHide(R.id.user_profile_skype, contactDetail.skype);
        bindAndSetOrHide(R.id.user_profile_viber, contactDetail.viber);
        bindAndSetOrHide(R.id.user_profile_whatsapp, contactDetail.whatsApp);
        bindAndSetOrHide(R.id.user_profile_wechat, contactDetail.weChat);
        bindAndSetOrHide(R.id.user_profile_google_talk, contactDetail.googleTalk);
        bindAndSetOrHide(R.id.user_profile_tango, contactDetail.tango);
        bindAndSetOrHide(R.id.user_profile_twitter, contactDetail.twitter);
        bindAndSetOrHide(R.id.user_profile_hike, contactDetail.hike);
        bindAndSetOrHide(R.id.user_profile_qq, contactDetail.qq);

        bindAndSetOrHide(R.id.user_profile_phone, contactDetail.phone);
        bindAndSetOrHide(R.id.user_profile_primary_phone, contactDetail.primaryNumber);

        bindAndSetOrHide(R.id.user_profile_location, contactDetail.address);
        bindAndSetOrHide(R.id.user_profile_email, contactDetail.email);

        bindAndSetOrHide(R.id.user_profile_name, contactDetail.fullName);
        bindAndSetOrHide(R.id.user_profile_role, TextUtils.isEmpty(contactDetail.role) ? "Site Supervisor" : contactDetail.role);
    }


    private void onEmailNow() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{contactDetail.email});
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private void onCallNow() {
        boolean hasMultiplePhoneNumbers = !TextUtils.isEmpty(contactDetail.primaryNumber) && !TextUtils.isEmpty(contactDetail.phone);


        if (hasMultiplePhoneNumbers) {
            PopupMenu popupMenu = new PopupMenu(requireActivity(), btnCallNow);
//            popupMenu.getMenu().add(MENU1, MENU_1_ITEM, 0, getText(R.string.menu1));
//            popupMenu.getMenu().add(MENU2, MENU_2_ITEM, 1, getText(R.string.menu2));
//            popupMenu.getMenu().add(MENU2, MENU_2_ITEM, 1, getText(R.string.menu2));
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    return false;
                }
            });
            popupMenu.show();
        } else {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactDetail.phone, null));
            if (canDeviceHandleCall(callIntent)) {
                startActivity(callIntent);
            }
        }


    }

    private boolean canDeviceHandleCall(Intent callIntent) {
        if (callIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            return true;
        }

        SnackBarUtils.showFlashbar(requireActivity(), "Device does not support phone calls");
        return false;
    }

    private void bindAndSetOrHide(int viewId, String string) {
        View view = rootView.findViewById(viewId);
        view.setOnClickListener(view1 -> copyTextToClipboard(requireContext(), string));

        if (TextUtils.isEmpty(string) || TextUtils.equals("null", string)) {
            view.setVisibility(View.GONE);
        } else {
            ((TextView) view).setText(string);
        }
    }

    private void copyTextToClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);

        ToastUtils.showLongToast(String.format("Copied %s to clipboard", text));
    }


}