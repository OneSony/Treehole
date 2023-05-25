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

import com.example.treehole.R;
import com.example.treehole.room.Moment;

import java.util.List;

public class MomentAdapter extends RecyclerView.Adapter<MomentViewHolder> {

    public List<Moment> moment_list;
    private final LayoutInflater inflater;
    public Context context;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private MomentAdapter.OnItemClickListener mOnItemClickListener;

    public MomentAdapter(Context context, List<Moment> moment_list){
        this.inflater=LayoutInflater.from(context);
        this.moment_list = moment_list;
        this.context=context;
    }

    public MomentAdapter(Context context){
        this.inflater=LayoutInflater.from(context);
        this.context=context;
    }

    public void setMoment(List<Moment> moment_list){
        Log.d("ADAPTER",String.valueOf(moment_list.size()));
        this.moment_list=moment_list;
    }

    @NonNull
    @Override
    public MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView=inflater.inflate(R.layout.item,parent,false);
        return new MomentViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentViewHolder holder, int position) {
        holder.topic_box.setText(moment_list.get(position).topic);
        holder.main_box.setText(moment_list.get(position).text);

        holder.auth_box.setText("null");
        holder.date_box.setText("null");
        
        holder.like_box.setText(String.valueOf(0));
        holder.comment_box.setText(String.valueOf(0));
        holder.collect_box.setText(String.valueOf(0));

        holder.profile_box.setImageResource(R.drawable.user1);

        holder.photo1.setVisibility(View.GONE);
        holder.photo2.setVisibility(View.GONE);
        holder.photo3.setVisibility(View.GONE);

        /*
        photo_num = data_list.get(position).getPhoto_num();
        if (photo_num == 1) {
            holder.photo1.setVisibility(View.VISIBLE);
            holder.photo1.setImageResource(data_list.get(position).getPhoto_index(0));
        } else if (photo_num == 2) {
            holder.photo1.setVisibility(View.VISIBLE);
            holder.photo1.setImageResource(data_list.get(position).getPhoto_index(0));

            holder.photo2.setVisibility(View.VISIBLE);
            holder.photo2.setImageResource(data_list.get(position).getPhoto_index(1));
        } else if (photo_num == 3) {
            holder.photo1.setVisibility(View.VISIBLE);
            holder.photo1.setImageResource(data_list.get(position).getPhoto_index(0));

            holder.photo2.setVisibility(View.VISIBLE);
            holder.photo2.setImageResource(data_list.get(position).getPhoto_index(1));

            holder.photo3.setVisibility(View.VISIBLE);
            holder.photo3.setImageResource(data_list.get(position).getPhoto_index(2));

        }
        //Log.d("PHOTO",String.valueOf(data_list.get(position).isPath_flag())+" "+String.valueOf(position));

        if (data_list.get(position).isPath_flag() == true) {
            holder.photo1.setVisibility(View.VISIBLE);

            String url = "https://rickyvu.pythonanywhere.com/static/images/test1.png";
            Glide.with(holder.itemView.getContext()).load(url).into(holder.photo1);
        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(holder.itemView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(moment_list==null){
            return 0;
        }else {
            return moment_list.size();
        }
    }

    public void setOnItemClickListener(MomentAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onViewRecycled(MomentViewHolder holder) {
        // 清除 ViewHolder 中的内存
        holder.photo1.setImageBitmap(null);
    }
}

class MomentViewHolder extends RecyclerView.ViewHolder{

    public final TextView topic_box;
    public final ImageView profile_box;
    public final TextView main_box;
    public final TextView date_box;
    public final TextView auth_box;
    public final TextView like_box;
    public final TextView comment_box;
    public final TextView collect_box;
    public final ImageView photo1;
    public final ImageView photo2;
    public final ImageView photo3;
    public MomentViewHolder(@NonNull View itemView) {
        super(itemView);
        topic_box=itemView.findViewById(R.id.topic_box);
        profile_box=itemView.findViewById(R.id.profile);
        main_box=itemView.findViewById(R.id.main_box);
        auth_box=itemView.findViewById(R.id.auth_box);
        date_box=itemView.findViewById(R.id.date_box);
        like_box =itemView.findViewById(R.id.like_box);
        comment_box=itemView.findViewById(R.id.comment_box);
        collect_box=itemView.findViewById(R.id.collect_box);
        photo1=itemView.findViewById(R.id.photo1);
        photo2=itemView.findViewById(R.id.photo2);
        photo3=itemView.findViewById(R.id.photo3);
    }
}
