package com.example.treehole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ChatListAdapter extends RecyclerView.Adapter<ChatViewHolder> {
    private final LayoutInflater inflater;

    private dot_list data_list;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    //public final MainActivity mainPage;
    public Context context;


    public ChatListAdapter(Context context, dot_list _dot_list){
        inflater=LayoutInflater.from(context);
        data_list=_dot_list;
        //this.mainPage=main_page;
        this.context=context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mItemView=inflater.inflate(R.layout.chat_item,parent,false);
        return new ChatViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.chat_user_box.setText(data_list.get(position).getAuth());
        holder.chat_text_box.setText(data_list.get(position).getText());
        holder.profile_photo.setImageResource(data_list.get(position).getProfile_index());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Log.d("MainActivity",String.valueOf(getLayoutPosition()));
                //Intent intent=new Intent(context,InfoActivity.class);
                //context.startActivity(intent);
                mOnItemClickListener.onItemClick(holder.itemView, position);
            }
        });

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return data_list.size();
        //return 0;
    }

    @Override
    public void onViewRecycled(ChatViewHolder holder) {
        // 清除 ViewHolder 中的内存
        //holder.photo1.setImageBitmap(null);
    }

}


class ChatViewHolder extends RecyclerView.ViewHolder{

    public TextView chat_user_box;
    public TextView chat_text_box;
    public ImageView profile_photo;

    public ChatViewHolder(@NonNull View itemView, ChatListAdapter chatListAdapter) {
        super(itemView);

        chat_user_box=itemView.findViewById(R.id.chat_user_box);
        chat_text_box=itemView.findViewById(R.id.chat_text_box);
        profile_photo=itemView.findViewById(R.id.chat_profile);


    }
}