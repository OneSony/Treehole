package com.example.treehole;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoViewHolder>{

    List<Uri> uris;

    public PhotoListAdapter(){
        uris= new ArrayList<>();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.textView.setText(uris.get(position).toString());
    }

    @Override
    public int getItemCount() {
        if(uris==null){
            return 0;
        }else{
            return uris.size();
        }
    }

    public void setUris(List<Uri> urls){
        this.uris=urls;
        notifyDataSetChanged();
    }

    public void addUris(Uri uri){
        this.uris.add(uri);
        notifyDataSetChanged();
    }

    public void onItemMove(int fromPosition, int toPosition) {
        // 处理项目移动逻辑
        uris.add(toPosition, uris.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }
}

class PhotoViewHolder extends RecyclerView.ViewHolder{

    public TextView textView;

    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);

        textView=itemView.findViewById(R.id.test_textview2);

    }
}

