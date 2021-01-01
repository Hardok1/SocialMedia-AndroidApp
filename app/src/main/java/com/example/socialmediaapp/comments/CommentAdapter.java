package com.example.socialmediaapp.comments;

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
import com.example.socialmediaapp.comments.models.CommentModel;
import com.example.socialmediaapp.posts.PostAdapter;
import com.example.socialmediaapp.posts.models.PostDetailsModel;
import com.example.socialmediaapp.posts.models.PostModel;
import com.example.socialmediaapp.profiles.activities.ProfileActivity;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{

    private final Context context;
    private final PostDetailsModel postDetailsModel;
    private final String token;
    private final int accountId;
    private int idOfClickedComment;
    CommentAPI commentAPI;

    public CommentAdapter(Context context, PostDetailsModel postDetailsModel, String token, int accountId, CommentAPI commentAPI) {
        this.context = context;
        this.postDetailsModel = postDetailsModel;
        this.token = token;
        this.accountId = accountId;
        this.commentAPI = commentAPI;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.comment_row, parent, false);
        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        String fullName = postDetailsModel.getComments().get(position).getAuthor().getForename()
                + postDetailsModel.getComments().get(position).getAuthor().getSurname();
        holder.name.setText(fullName);
        holder.createdAt.setText(postDetailsModel.getComments().get(position).getCreatedAt());
        holder.content.setText(postDetailsModel.getComments().get(position).getContent());
        holder.name.setTag(postDetailsModel.getComments().get(position).getAuthor().getId().intValue());
        holder.commentRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("id", postDetailsModel.getComments().get(position).getAuthor().getId().intValue());
                context.startActivity(intent);
            }
        });
        if (accountId == postDetailsModel.getComments().get(position).getAuthor().getId()){
            holder.commentRowLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    idOfClickedComment = (int) postDetailsModel.getComments().get(position).getId();
                    return false;
                }
            });
        }
    }

    @Override
    public void onViewRecycled(@NonNull CommentAdapter.CommentViewHolder holder) {
        holder.commentRowLayout.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }


    @Override
    public int getItemCount() {
        return postDetailsModel.getComments().size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnLongClickListener {

        TextView name;
        TextView createdAt;
        TextView content;
        ConstraintLayout commentRowLayout;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.commentorNameText);
            createdAt = itemView.findViewById(R.id.commentCreatedAtText);
            content = itemView.findViewById(R.id.commentContentText);
            commentRowLayout = itemView.findViewById(R.id.commentRowLayout);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem editMenuItem = contextMenu.add(R.string.edit);
            editMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    EditText commentContentInput = new EditText(context);
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("Edit Comment")
                            .setMessage("Type your comment content")
                            .setView(commentContentInput)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    CommentModel commentModel = new CommentModel();
                                    commentModel.setContent(commentContentInput.getText().toString());
                                    Call<Void> call = commentAPI.editComment(token, idOfClickedComment, commentModel);
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(context, "Comment edited with success, reload if you want to see change", Toast.LENGTH_SHORT).show();
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
                    Call<Void> call = commentAPI.deleteComment(token, idOfClickedComment);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()){
                                Toast.makeText(context, "Comment deleted with success, reload if you want to see changes", Toast.LENGTH_SHORT).show();
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
