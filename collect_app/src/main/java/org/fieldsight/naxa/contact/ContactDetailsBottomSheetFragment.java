package org.fieldsight.naxa.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
        ImageView profilePicture = rootView.findViewById(R.id.user_profile_profile_picture);
        if (contactDetail.profilePicture != null) {
            GlideApp.with(this)
                    .load(contactDetail.profilePicture)
                    .centerCrop()
                    .into(profilePicture);
        }

        rootView.findViewById(R.id.user_profile_call_now).setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactDetail.phone, null));
            if (canDeviceHandleCall(callIntent)) {
                startActivity(callIntent);
            }
        });
    }

//    private void buttonPopupMenu_onClick(View view) {
//        PopupMenu popupMenu = new PopupMenu(requireActivity(), buttonPopupMenu);
//        popupMenu.getMenu().add(MENU1, MENU_1_ITEM, 0, getText(R.string.menu1));
//        popupMenu.getMenu().add(MENU2, MENU_2_ITEM, 1, getText(R.string.menu2));
//
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                return false;
//            }
//        });
//        popupMenu.show();
//    }

    private boolean canDeviceHandleCall(Intent callIntent) {
        if (callIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            return true;
        }

        SnackBarUtils.showFlashbar(requireActivity(), "Device does not support phone calls");
        return false;
    }

    private void bindAndSetOrHide(TextView textView, int viewId, String string) {
        textView = rootView.findViewById(viewId);
        if (TextUtils.isEmpty(string) || TextUtils.equals("null", string)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(string);
        }
    }

    private void bindAndSetOrHide(TextView textView, int viewId, String string, int iconId) {
        textView = rootView.findViewById(viewId);
        if (TextUtils.isEmpty(string) || TextUtils.equals("null", string)) {
            textView.setVisibility(View.GONE);
            rootView.findViewById(iconId).setVisibility(View.GONE);
        } else {
            textView.setText(string);
        }
    }


}