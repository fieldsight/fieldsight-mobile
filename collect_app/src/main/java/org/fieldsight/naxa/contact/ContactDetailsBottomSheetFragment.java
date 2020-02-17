package org.fieldsight.naxa.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.GlideApp;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.v3.project.Users;


public class ContactDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private View rootView;
    private Users contactDetail;
    private TextView fullname, username, role, address, gender, email, skype, twitter, tango, hike, qq, googletalk, viber, whatsapp, wechat;

    public static ContactDetailsBottomSheetFragment newInstance() {
        return new ContactDetailsBottomSheetFragment();
    }

    public void setContact(Users contactDetail) {
        this.contactDetail = contactDetail;
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.bottom_sheet_contact, container, false);

        ContactDetailToViews();

        return rootView;
    }

    private void ContactDetailToViews() {
        ImageView profilePicture = rootView.findViewById(R.id.iv_contactdetail_image);
        if (contactDetail.profilePicture != null) {
            GlideApp.with(this)
                    .load(contactDetail.profilePicture)
                    .centerCrop()
                    .into(profilePicture);
        }

        BindAndSetOrHide(fullname, R.id.tv_contactdetail_fullname, contactDetail.fullName);


        BindAndSetOrHide(role, R.id.tv_contactdetail_role, contactDetail.role);

        BindAndSetOrHide(address, R.id.tv_contactdetail_address, contactDetail.address);

        BindAndSetOrHide(gender, R.id.tv_contactdetail_gender, contactDetail.gender);


        BindAndSetOrHide(skype, R.id.tv_contactdetail_skype, contactDetail.skype, R.id.iv_skype_icon);

        BindAndSetOrHide(twitter, R.id.tv_contactdetail_twitter, contactDetail.twitter, R.id.iv_twitter_icon);

        BindAndSetOrHide(tango, R.id.tv_contactdetail_tango, contactDetail.tango, R.id.iv_tango_icon);

        BindAndSetOrHide(hike, R.id.tv_contactdetail_hike, contactDetail.hike, R.id.iv_hike_icon);

        BindAndSetOrHide(qq, R.id.tv_contactdetail_qq, contactDetail.qq, R.id.iv_qq_icon);

        BindAndSetOrHide(googletalk, R.id.tv_contactdetail_googletalk, contactDetail.googleTalk, R.id.iv_googletalk_icon);

        BindAndSetOrHide(viber, R.id.tv_contactdetail_viber, contactDetail.viber, R.id.iv_viber_icon);

        BindAndSetOrHide(whatsapp, R.id.tv_contactdetail_whatsapp, contactDetail.whatsApp, R.id.iv_whatsapp_icon);

        BindAndSetOrHide(wechat, R.id.tv_contactdetail_wechat, contactDetail.weChat, R.id.iv_wechat_icon);

        boolean isValidPhoneNumber = !TextUtils.isEmpty(contactDetail.phone);

        rootView.findViewById(R.id.btn_phone_call).setVisibility(isValidPhoneNumber ? View.VISIBLE : View.GONE);

        rootView.findViewById(R.id.btn_phone_call)
                .setOnClickListener(v -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactDetail.phone, null));
                    if (canDeviceHandleCall(callIntent)) {
                        startActivity(callIntent);
                    }

                });

    }

    private boolean canDeviceHandleCall(Intent callIntent) {
        if (callIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            return true;
        }

        SnackBarUtils.showFlashbar(requireActivity(), "Device does not support phone calls");
        return false;
    }

    private void BindAndSetOrHide(TextView textView, int viewId, String string) {
        textView = rootView.findViewById(viewId);
        if (TextUtils.isEmpty(string)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(string);
        }
    }

    private void BindAndSetOrHide(TextView textView, int viewId, String string, int iconId) {
        textView = rootView.findViewById(viewId);
        if (TextUtils.isEmpty(string)) {
            textView.setVisibility(View.GONE);
            rootView.findViewById(iconId).setVisibility(View.GONE);
        } else {
            textView.setText(string);
        }
    }


}