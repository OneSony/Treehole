package com.example.treehole.paging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.activity.InfoActivity;
import com.example.treehole.R;
import com.example.treehole.activity.MsgActivity;
import com.example.treehole.room.Moment;

import java.util.Objects;

public class MomentPagingAdapter extends PagingDataAdapter<Moment, MomentPagingViewHolder> {

    Context context;

    public MomentPagingAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Moment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
                return oldItem.id.equals(newItem.id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
                Log.d("SAME?",String.valueOf(oldItem.text.equals(newItem.text)&&oldItem.topic.equals(newItem.topic)));
                return oldItem.text.equals(newItem.text)&&oldItem.topic.equals(newItem.topic);
            }
        });

        this.context=context;
    }


    @NonNull
    @Override
    public MomentPagingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mItemView=inflater.inflate(R.layout.item,parent,false);
        return new MomentPagingViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentPagingViewHolder holder, int position) {
        Moment moment = getItem(position);
        // Note that item may be null. ViewHolder must support binding a
        // null item as a placeholder.
        if(moment==null){
            holder.topic_box.setText("loading");
        }else{
            holder.topic_box.setText(moment.topic);
            holder.main_box.setText(moment.text);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 在这里处理项目点击事件
                    // 显示项目ID，可以通过Toast或者其他方式展示
                    Toast.makeText(view.getContext(), "position: " + position, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, InfoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("MOMENT",moment);
                    intent.putExtra("BUNDLE_DATA",bundle);

                    context.startActivity(intent);
                }
            });
        }
    }

    private static final DiffUtil.ItemCallback<Moment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Moment>() {
        @Override
        public boolean areItemsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
            return oldItem.m_index == newItem.m_index;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

}

class MomentPagingViewHolder extends RecyclerView.ViewHolder{

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

    public MomentPagingViewHolder(@NonNull View itemView) {
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

