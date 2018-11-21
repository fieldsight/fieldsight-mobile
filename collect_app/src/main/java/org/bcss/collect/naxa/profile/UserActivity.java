package org.bcss.collect.naxa.profile;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.bcss.collect.android.BuildConfig;
import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.android.utilities.ToastUtils;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.common.GlideApp;
import org.bcss.collect.naxa.common.ImageFileUtils;
import org.bcss.collect.naxa.login.model.User;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.observers.DisposableObserver;

public class UserActivity extends CollectAbstractActivity {

    @BindView(R.id.fab_edit_profile)
    FloatingActionButton fabEditProfile;
    @BindView(R.id.il_name)
    TextInputLayout ilName;
    @BindView(R.id.il_email)
    TextInputLayout ilEmail;
    @BindView(R.id.il_phone)
    TextInputLayout ilPhone;
    @BindView(R.id.il_location)
    TextInputLayout ilLocation;
    @BindView(R.id.il_gender)
    TextInputLayout ilGender;
    @BindView(R.id.il_skype)
    TextInputLayout ilSkype;
    @BindView(R.id.il_primary_number)
    TextInputLayout ilPrimaryNumber;
    @BindView(R.id.il_secondary_number)
    TextInputLayout ilSecondaryNumber;
    @BindView(R.id.il_office_number)
    TextInputLayout ilOfficeNumber;
    @BindView(R.id.il_viber)
    TextInputLayout ilViber;
    @BindView(R.id.il_whatsapp)
    TextInputLayout ilWhatsapp;
    @BindView(R.id.il_wechat)
    TextInputLayout ilWechat;
    @BindView(R.id.il_line)
    TextInputLayout ilLine;
    @BindView(R.id.il_tango)
    TextInputLayout ilTango;
    @BindView(R.id.il_hike)
    TextInputLayout ilHike;
    @BindView(R.id.il_qq)
    TextInputLayout ilQq;
    @BindView(R.id.il_google_talk)
    TextInputLayout ilGoogleTalk;
    @BindView(R.id.il_twitter)
    TextInputLayout ilTwitter;
    @BindView(R.id.il_organization)
    TextInputLayout ilOrganization;
    @BindView(R.id.il_project)
    TextInputLayout ilProject;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.civProfilePic)
    CircleImageView civProfilePic;
    @BindView(R.id.fab_picture_edit)
    FloatingActionButton fabPictureEdit;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_sync)
    Button btnSync;
    @BindView(R.id.progress_sync)
    ProgressBar progressSync;

    private UserProfileViewModel userProfileViewModel;
    private User mUser;
    private File photoToUpload;
    private Uri phototoUploadUri;

    public static void start(Context context) {
        Intent intent = new Intent(context, UserActivity.class);
//        intent.putExtra(EXTRA_OBJECT, );
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);

        initUI();

        setupViewModel();

        userProfileViewModel.setEditProfile(false);

        mUser = userProfileViewModel.getUser().getValue();

        userProfileViewModel.setSyncLiveData(mUser.isSync());

        userProfileViewModel
                .getEditProfile()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean editMode) {
                        if (editMode) {
                            fabPictureEdit.setVisibility(View.VISIBLE);
                            fabEditProfile.setImageResource(R.drawable.ic_check);
                            setInputLayoutEnabledStatus(true);
                            setInputLayoutVisibility(View.VISIBLE);

                            btnSync.setClickable(false);
                            btnSync.setText("Edit Mode");
                            btnSync.setTextColor(getResources().getColor(R.color.bg_action_mode));
                        } else {
                            fabPictureEdit.setVisibility(View.GONE);
                            fabEditProfile.setImageResource(R.drawable.ic_edit);
                            setInputLayoutEnabledStatus(false);

                            userProfileViewModel.setUser(mUser);
                            userProfileViewModel.save();
                        }
                    }
                });

        userProfileViewModel
                .getUser()
                .observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(@Nullable User user) {

                        userProfileViewModel.setSyncLiveData(user.isSync());

                        boolean userProfile = (user.getProfilepic().isEmpty() || (user.getProfilepic() == null));
                        if (!userProfile) {
                            GlideApp.with(UserActivity.this)
                                    .load(user.getProfilepic())
                                    .into(civProfilePic);
                        }

                        setInputLayoutData(ilName, user.getFullName());
                        setInputLayoutData(ilEmail, user.getEmail());
                        setInputLayoutData(ilPhone, user.getPhone());
                        setInputLayoutData(ilLocation, user.getAddress());
                        setInputLayoutData(ilGender, user.getGender());
                        setInputLayoutData(ilSkype, user.getSkype());
                        setInputLayoutData(ilPrimaryNumber, user.getPrimaryNumber());
                        setInputLayoutData(ilSecondaryNumber, user.getSecondaryNumber());
                        setInputLayoutData(ilOfficeNumber, user.getOfficeNumber());
                        setInputLayoutData(ilViber, user.getViber());
                        setInputLayoutData(ilWhatsapp, user.getWhatsApp());
                        setInputLayoutData(ilWechat, user.getWechat());
                        setInputLayoutData(ilLine, user.getLine());
                        setInputLayoutData(ilTango, user.getTango());
                        setInputLayoutData(ilHike, user.getTango());
                        setInputLayoutData(ilQq, user.getQq());
                        setInputLayoutData(ilGoogleTalk, user.getGoogleTalk());
                        setInputLayoutData(ilTwitter, user.getTwitter());
                        setInputLayoutData(ilOrganization, user.getOrganization());
                        setInputLayoutData(ilProject, user.getProject());
                    }
                });

        userProfileViewModel
                .getSyncLiveData()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean sync) {
                        if (sync) {
                            btnSync.setClickable(false);
                            btnSync.setText("Synced");
                            btnSync.setTextColor(getResources().getColor(R.color.green_approved));
                        } else {
                            btnSync.setClickable(true);
                            btnSync.setText("Tap to Sync");
                            btnSync.setTextColor(getResources().getColor(R.color.red_rejected));
                        }
                    }
                });

        userProfileViewModel
                .getProgressBar()
                .observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(@Nullable Boolean progress) {
                        if (progress) {
                            progressSync.setVisibility(View.VISIBLE);
                            btnSync.setVisibility(View.GONE);
                        } else {
                            progressSync.setVisibility(View.GONE);
                            btnSync.setVisibility(View.VISIBLE);
                        }
                    }
                });

        watchText(ilName);
        watchText(ilEmail);
        watchText(ilPhone);
        watchText(ilLocation);
        watchText(ilGender);
        watchText(ilSkype);
        watchText(ilPrimaryNumber);
        watchText(ilSecondaryNumber);
        watchText(ilOfficeNumber);
        watchText(ilViber);
        watchText(ilWhatsapp);
        watchText(ilWechat);
        watchText(ilLine);
        watchText(ilTango);
        watchText(ilHike);
        watchText(ilQq);
        watchText(ilGoogleTalk);
        watchText(ilTwitter);
        watchText(ilOrganization);
        watchText(ilProject);
    }

    private void setInputLayoutData(TextInputLayout inputLayout, String data) {
        if (data == null) {
            inputLayout.setVisibility(View.GONE);
        } else if (data.isEmpty()) {
            inputLayout.setVisibility(View.GONE);
        } else {
            if (inputLayout.getVisibility() == View.GONE) {
                inputLayout.setVisibility(View.VISIBLE);
            }
            inputLayout.getEditText().setText(data);
        }
    }

    private void initUI() {
        View photoHeader = findViewById(R.id.photoHeader);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /* For devices equal or higher than lollipop set the translation above everything else */
            photoHeader.setTranslationZ(6);
            /* Redraw the view to show the translation */
            photoHeader.invalidate();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setupViewModel() {
        userProfileViewModel = new UserProfileViewModel();
    }

    @OnClick({R.id.fab_edit_profile, R.id.fab_picture_edit, R.id.iv_back, R.id.btn_sync})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_edit_profile:
                switch (fabPictureEdit.getVisibility()) {
                    case View.VISIBLE:
                        userProfileViewModel.setEditProfile(false);
                        break;
                    case View.GONE:
                        userProfileViewModel.setEditProfile(true);
                        break;
                }
                break;
            case R.id.fab_picture_edit:
                final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Dismiss"};
                DialogFactory.createListActionDialog(this, "Add photo", items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            photoToUpload = userProfileViewModel.generateImageFile("profile");
                            phototoUploadUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoToUpload);
                            Intent toCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            toCamera.putExtra(MediaStore.EXTRA_OUTPUT, phototoUploadUri);
                            startActivityForResult(toCamera, Constant.Key.RC_CAMERA);
                            break;
                        case 1:
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select site image"), Constant.Key.SELECT_FILE);
                            break;
                        default:
                            break;
                    }
                }).show();
                break;
            case R.id.btn_sync:
                userProfileViewModel.setProgressBar(true);
                userProfileViewModel
                        .upload()
                        .subscribe(new DisposableObserver<User>() {
                            @Override
                            public void onNext(User user) {
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                userProfileViewModel.setProgressBar(false);
                                userProfileViewModel.setSyncLiveData(false);
                                mUser.setSync(false);
                                userProfileViewModel.setUser(mUser);
                                userProfileViewModel.save();
                            }

                            @Override
                            public void onComplete() {
                                userProfileViewModel.setProgressBar(false);
                                userProfileViewModel.setSyncLiveData(true);
                                mUser.setSync(true);
                                userProfileViewModel.setUser(mUser);
                                userProfileViewModel.save();
                            }
                        });
                break;
            case R.id.iv_back:
                super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case Constant.Key.RC_CAMERA:
                userProfileViewModel.getUser().getValue().setProfilepic(photoToUpload.getAbsolutePath());
                GlideApp.with(UserActivity.this)
                        .load(photoToUpload)
                        .into(civProfilePic);
                break;
            case Constant.Key.SELECT_FILE:
                Uri uri = data.getData();
                String path = ImageFileUtils.getPath(this, uri);
                userProfileViewModel.getUser().getValue().setProfilepic(path);
                GlideApp.with(UserActivity.this)
                        .load(path)
                        .into(civProfilePic);
                break;
        }
    }


    public void watchText(TextInputLayout textInputLayout) {
        textInputLayout
                .getEditText()
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mUser.setSync(false);
                        switch (textInputLayout.getId()) {
                            case R.id.il_name:
                                mUser.setFullName(s.toString());
                                break;
                            case R.id.il_email:
                                mUser.setEmail(s.toString());
                                break;
                            case R.id.il_phone:
                                mUser.setPhone(s.toString());
                                break;
                            case R.id.il_location:
                                mUser.setAddress(s.toString());
                                break;
                            case R.id.il_gender:
                                mUser.setGender(s.toString());
                                break;
                            case R.id.il_skype:
                                mUser.setSkype(s.toString());
                                break;
                            case R.id.il_primary_number:
                                mUser.setPrimaryNumber(s.toString());
                                break;
                            case R.id.il_secondary_number:
                                mUser.setSecondaryNumber(s.toString());
                                break;
                            case R.id.il_office_number:
                                mUser.setOfficeNumber(s.toString());
                                break;
                            case R.id.il_viber:
                                mUser.setViber(s.toString());
                                break;
                            case R.id.il_whatsapp:
                                mUser.setWhatsApp(s.toString());
                                break;
                            case R.id.il_wechat:
                                mUser.setWechat(s.toString());
                                break;
                            case R.id.il_line:
                                mUser.setLine(s.toString());
                                break;
                            case R.id.il_tango:
                                mUser.setTango(s.toString());
                                break;
                            case R.id.il_hike:
                                mUser.setHike(s.toString());
                                break;
                            case R.id.il_qq:
                                mUser.setQq(s.toString());
                                break;
                            case R.id.il_google_talk:
                                mUser.setGoogleTalk(s.toString());
                                break;
                            case R.id.il_twitter:
                                mUser.setTwitter(s.toString());
                                break;
                            case R.id.il_organization:
                                mUser.setOrganization(s.toString());
                                break;
                            case R.id.il_project:
                                mUser.setProject(s.toString());
                                break;
                        }
                    }
                });
    }

    private void setInputLayoutEnabledStatus(Boolean status) {
        ilName.setEnabled(status);
        ilEmail.setEnabled(status);
        ilEmail.setEnabled(status);
        ilPhone.setEnabled(status);
        ilLocation.setEnabled(status);
        ilGender.setEnabled(status);
        ilSkype.setEnabled(status);
        ilPrimaryNumber.setEnabled(status);
        ilSecondaryNumber.setEnabled(status);
        ilOfficeNumber.setEnabled(status);
        ilViber.setEnabled(status);
        ilWhatsapp.setEnabled(status);
        ilWechat.setEnabled(status);
        ilLine.setEnabled(status);
        ilTango.setEnabled(status);
        ilHike.setEnabled(status);
        ilQq.setEnabled(status);
        ilGoogleTalk.setEnabled(status);
        ilTwitter.setEnabled(status);
        ilOrganization.setEnabled(status);
        ilProject.setEnabled(status);
    }

    private void setInputLayoutVisibility(int visibility) {
        ilName.setVisibility(visibility);
        ilEmail.setVisibility(visibility);
        ilPhone.setVisibility(visibility);
        ilLocation.setVisibility(visibility);
        ilGender.setVisibility(visibility);
        ilSkype.setVisibility(visibility);
        ilPrimaryNumber.setVisibility(visibility);
        ilSecondaryNumber.setVisibility(visibility);
        ilOfficeNumber.setVisibility(visibility);
        ilViber.setVisibility(visibility);
        ilWhatsapp.setVisibility(visibility);
        ilWechat.setVisibility(visibility);
        ilLine.setVisibility(visibility);
        ilTango.setVisibility(visibility);
        ilHike.setVisibility(visibility);
        ilQq.setVisibility(visibility);
        ilGoogleTalk.setVisibility(visibility);
        ilTwitter.setVisibility(visibility);
        ilOrganization.setVisibility(visibility);
        ilProject.setVisibility(visibility);
    }
}
