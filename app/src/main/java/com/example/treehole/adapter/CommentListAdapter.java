package com.example.treehole.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.treehole.R;
import com.example.treehole.room.Comment;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder>{

    public interface OnItemClickListener{
        void onItemClick(String user_id,String username) throws ExecutionException, InterruptedException;
    }

    private CommentListAdapter.OnItemClickListener mOnItemClickListener;

    List<Comment> commentResults;
    Context context;

    public CommentListAdapter(Context context){
        this.context=context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        /*

        JSONObject json = new JSONObject();
        try {
            json.put("id", commentResults.get(position).getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebUtils.sendGet("/users/username?id="+commentResults.get(position).getUser_id(), false, new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                String username;
                try {
                    JSONObject msg=json.getJSONObject("message");
                    username=msg.getString("username");

                    //run in UI thread
                    holder.username.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.username_str=username;
                            holder.username.setText(username);
                        }
                    });

                    Log.d("SUCCESS", username);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onError(Throwable t) {
                Log.e("ERROR", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                try {
                    Log.e("FAILURE", json.getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });*/


        holder.text.setText(commentResults.get(position).getText());
        holder.username.setText(commentResults.get(position).getUsername());
        holder.date.setText(commentResults.get(position).getDate());


        String profile_photo_url = "https://rickyvu.pythonanywhere.com/users/profile_picture?id="+commentResults.get(position).getUser_id();
        Log.d("PROFILE URL",profile_photo_url);
        Glide.with(holder.itemView.getContext()).load(profile_photo_url).into(holder.imageView);



        holder.imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    mOnItemClickListener.onItemClick(commentResults.get(position).getUser_id(),commentResults.get(position).getUsername());
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        if(commentResults==null){
            return 0;
        }else{
            return commentResults.size();
        }
    }

    public void setSearchUserResults(List<Comment> commentResults) {
        this.commentResults = commentResults;
    }

    public void setOnItemClickListener(CommentListAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}

class CommentViewHolder extends RecyclerView.ViewHolder{

    public TextView username;

    public TextView text;

    public TextView date;
    public ImageView imageView;


    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        username=itemView.findViewById(R.id.comment_user_box);
        text=itemView.findViewById(R.id.comment_text_box);
        imageView=itemView.findViewById(R.id.comment_profile);
        date=itemView.findViewById(R.id.comment_date_box);
    }
}