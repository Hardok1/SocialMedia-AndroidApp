package com.example.socialmediaapp.profiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.comments.activities.CommentActivity;
import com.example.socialmediaapp.profiles.activities.ProfileActivity;
import com.example.socialmediaapp.profiles.models.PublicAccountInfoModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {


    private final Context context;
    private final List<PublicAccountInfoModel> profiles;
    private final int accountId;

    public ProfileAdapter(Context context, List<PublicAccountInfoModel> profiles, int accountId) {
        this.context = context;
        this.profiles = profiles;
        this.accountId = accountId;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.profile_row, parent, false);
        return new ProfileAdapter.ProfileViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.profileName.setText("Name: " + profiles.get(position).getForename() + " " + profiles.get(position).getSurname());
        holder.profileCountry.setText("Country: " + profiles.get(position).getCountry());
        holder.profileCity.setText("City: " + profiles.get(position).getCity());
        holder.profileRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                if (accountId != profiles.get(position).getId().intValue()) {
                    intent.putExtra("id", profiles.get(position).getId().intValue());
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }


    class ProfileViewHolder extends RecyclerView.ViewHolder {

        TextView profileName;
        TextView profileCity;
        TextView profileCountry;
        ConstraintLayout profileRowLayout;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.profileNameTextView);
            profileCity = itemView.findViewById(R.id.profileCityTextView);
            profileCountry = itemView.findViewById(R.id.profileCountryTextView);
            profileRowLayout = itemView.findViewById(R.id.profileRowLayout);
        }

    }
}
