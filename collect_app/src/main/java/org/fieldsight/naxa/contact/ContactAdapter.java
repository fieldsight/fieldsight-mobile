package org.fieldsight.naxa.contact;
/**
 * Add the contact inforamation instance to the recycler view
 *
 * @author Nishon Tandukar
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.GlideApp;

import java.util.List;

import io.reactivex.annotations.NonNull;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private final List<FieldSightContactModel> contactList;
    private final ContactDetailListener contactDetailListener;
    private Context context;


    ContactAdapter(List<FieldSightContactModel> contactList, @NonNull ContactDetailListener contactDetailListener, Context context) {
        this.contactList = contactList;
        this.contactDetailListener = contactDetailListener;
        this.context = context;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFullName, tvUserName;
        private final ImageView ivProfilePicture;

        MyViewHolder(View view) {
            super(view);

            ivProfilePicture = view.findViewById(R.id.imageView2);
            tvFullName = view.findViewById(R.id.contact_name);
            tvUserName = view.findViewById(R.id.contact_username);
            CardView card = view.findViewById(R.id.card_contact_list_item);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    contactDetailListener.onContactClicked(contactList.get(pos));
                }
            });
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        context = parent.getContext();


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( final MyViewHolder holder, int position) {
        final FieldSightContactModel contact = contactList.get(position);
        holder.tvFullName.setText(contact.getFull_name());
        holder.tvUserName.setText(contact.getEmail());
        /*
        //open tvSkype
        holder.tvSkype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Opening Skype", Toast.LENGTH_SHORT).show();
            }
        });


        //open tvFacebook
        holder.tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Opening Facebook", Toast.LENGTH_SHORT).show();

                Uri uri = Uri.parse("fb-messenger://user/100005727815736");
                //uri = ContentUris.withAppendedId(uri,[100005727815736]);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
                try {
                    context.startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }
            }
        });

        //open phone
        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Opening Phone", Toast.LENGTH_SHORT).show();

                //TODO get phone number form localdatabase
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getPhone().toString()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);
            }
        });
        //open tvSkype
        holder.tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Opening Message", Toast.LENGTH_SHORT).show();

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", contact.getPhone().toString());
                context.startActivity(smsIntent);
            }
        });
*/
        String profilePicture = contact.getProfilePicture();

        if (profilePicture != null) {
            GlideApp.with(context)
                    .load(profilePicture)
                    .circleCrop()
                    .thumbnail(0.5f)
                    .into(holder.ivProfilePicture);
        }
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }


    public interface ContactDetailListener {
        void onContactClicked(FieldSightContactModel contactModel);

    }
}