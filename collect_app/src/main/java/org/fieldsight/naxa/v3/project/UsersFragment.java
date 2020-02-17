package org.fieldsight.naxa.v3.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.contact.ContactDetailsBottomSheetFragment;
import org.fieldsight.naxa.login.model.User;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UsersFragment extends Fragment {
    @BindView(R.id.rv_users)
    RecyclerView rvUsers;

    @BindView(R.id.tv_error)
    TextView errors;
    private Unbinder unbinder;

    private UsersFragment() {
    }

    public static UsersFragment getInstance(String data) {
        Bundle bundle = new Bundle();
        bundle.putString("users", data);
        UsersFragment usersFragment = new UsersFragment();
        usersFragment.setArguments(bundle);
        return usersFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_users, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String users;
        if (getArguments() != null && getArguments().containsKey("users")) {
            users = getArguments().getString("users");
        } else {
            return;
        }

        if (TextUtils.isEmpty(users) || Users.toList(users).size() == 0) {
            errors.setVisibility(View.VISIBLE);
        } else {
            // show userslist
            List<Users> usersList = Users.toList(users);
            rvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvUsers.setAdapter(new UserAdapter(usersList));
            rvUsers.setHasFixedSize(true);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
        List<Users> userList;

        UserAdapter(List<Users> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            Users user = userList.get(position);
            holder.userName.setText(user.fullName);
            holder.userRole.setText(user.role);
            if (!TextUtils.isEmpty(user.profilePicture)) {
                Glide.with(holder.itemView.getContext()).load(user.profilePicture).apply(RequestOptions.circleCropTransform()).into(holder.userImage);
            }

            holder.rootLayout.setOnClickListener(view -> {
                ContactDetailsBottomSheetFragment contactDetailsBottomSheetFragmentDialog = ContactDetailsBottomSheetFragment.newInstance();
                contactDetailsBottomSheetFragmentDialog.setContact(user);
                contactDetailsBottomSheetFragmentDialog.show(requireFragmentManager(), "Contact Bottom Sheet");
            });
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

class UserViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_user_name)
    TextView userName;

    @BindView(R.id.tv_user_role)
    TextView userRole;

    @BindView(R.id.iv_user)
    ImageView userImage;

    @BindView(R.id.root_layout_user_list_item)
    View rootLayout;

    UserViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }


}