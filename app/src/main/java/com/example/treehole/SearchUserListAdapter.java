package com.example.treehole;

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

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchUserListAdapter extends RecyclerView.Adapter<SearchUserViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(String user_id,String username) throws ExecutionException, InterruptedException;
    }

    private OnItemClickListener mOnItemClickListener;

    List<SearchUserResult> searchUserResults;
    Context context;

    public SearchUserListAdapter(Context context){
        this.context=context;
    }

    @NonNull
    @Override
    public SearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_result_item, parent, false);
        return new SearchUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserViewHolder holder, int position) {
        holder.textView.setText(searchUserResults.get(position).getUsername());

        String profile_photo_url = "https://rickyvu.pythonanywhere.com/users/profile_picture?id="+searchUserResults.get(position).getUser_id();
        Log.d("PROFILE URL",profile_photo_url);
        Glide.with(holder.itemView.getContext()).load(profile_photo_url).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    mOnItemClickListener.onItemClick(searchUserResults.get(position).getUser_id(),searchUserResults.get(position).getUsername());
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
        if(searchUserResults==null){
            return 0;
        }else{
            return searchUserResults.size();
        }
    }

    public void setSearchUserResults(List<SearchUserResult> searchUserResults) {
        this.searchUserResults = searchUserResults;
    }

    public void setOnItemClickListener(SearchUserListAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}


class SearchUserViewHolder extends RecyclerView.ViewHolder{

    public TextView textView;
    public ImageView imageView;

    public SearchUserViewHolder(@NonNull View itemView) {
        super(itemView);

        textView=itemView.findViewById(R.id.search_user_result_username);
        imageView=itemView.findViewById(R.id.search_user_result_photo);
    }
}