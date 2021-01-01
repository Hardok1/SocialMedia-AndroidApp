package com.example.socialmediaapp.posts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.comments.activities.CommentActivity;
import com.example.socialmediaapp.posts.models.PostModel;
import com.example.socialmediaapp.profiles.activities.ProfileActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<PostModel> postModels;
    private final Context context;
    private final boolean isOwner;
    private final PostAPI postAPI;
    private final String token;
    private int idOfClickedPost;

    public PostAdapter(Context context, List<PostModel> postModels, boolean isOwner, PostAPI postAPI, String token) {
        this.context = context;
        this.postModels = postModels;
        this.isOwner = isOwner;
        this.postAPI = postAPI;
        this.token = token;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.post_row, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.postCreatedAt.setText(postModels.get(position).getCreatedAt());
        holder.postContent.setText(postModels.get(position).getContent());
        holder.postRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("isOwner", isOwner);
                intent.putExtra("postId", (int) postModels.get(position).getId());
                context.startActivity(intent);
            }
        });
        holder.postRowLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                idOfClickedPost = (int) postModels.get(position).getId();
                return false;
            }
        });


    }

    @Override
    public void onViewRecycled(@NonNull PostViewHolder holder) {
        holder.postRowLayout.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }


    @Override
    public int getItemCount() {
        return postModels.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnLongClickListener {

        TextView postCreatedAt;
        TextView postContent;
        ConstraintLayout postRowLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postCreatedAt = itemView.findViewById(R.id.postCreatedAtText);
            postContent = itemView.findViewById(R.id.postContentText);
            postRowLayout = itemView.findViewById(R.id.postRowLayout);
            if (isOwner) {
                itemView.setOnLongClickListener(this);
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem editMenuItem = contextMenu.add(R.string.edit);
            editMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    EditText postContentInput = new EditText(context);
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("Edit post")
                            .setMessage("Type your post content")
                            .setView(postContentInput)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PostModel postModel = new PostModel();
                                    postModel.setContent(postContentInput.getText().toString());
                                    Call<Void> call = postAPI.editPost(token, idOfClickedPost, postModel);
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(context, "Post edited with success, reload if you want to see change", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create();
                    alertDialog.show();
                    return true;
                }
            });

            MenuItem deleteMenuItem = contextMenu.add(R.string.delete);
            deleteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Call<Void> call = postAPI.deletePost(token, idOfClickedPost);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()){
                                Toast.makeText(context, "Post deleted with success, reload if you want to see changes", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                    return true;
                }
            });
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }
}
