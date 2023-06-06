package com.example.treehole;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.treehole.room.Message;

import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private List<Message> messages;
    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    private AdapterCallback adapterCallback;

    public interface AdapterCallback {
        void onDataEmpty(boolean isEmpty);
    }

    public void setAdapterCallback(AdapterCallback callback) {
        this.adapterCallback = callback;
    }

    private OnItemClickListener mOnItemClickListener;



    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        adapterCallback.onDataEmpty(getItemCount()==0);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.chat_user_box.setText(messages.get(position).getUsername());

        int size=messages.get(position).getNodes().size();
        if(size!=0){
            holder.chat_text_box.setText(messages.get(position).getNodes().get(size-1).getText());
        }
        //holder.profile_photo.setImageResource(data_list.get(position).getProfile_index());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                int index = messages.get(position).getIndex();
                mOnItemClickListener.onItemClick(index);

            }
        });

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        if(messages==null){
            return 0;
        }else {
            return messages.size();
        }
    }

    public int getIndex(int position){
        return messages.get(position).getIndex();
    }

    public void deleteItem(int position){
        messages.remove(position);
        adapterCallback.onDataEmpty(getItemCount()==0);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,getItemCount());
    }


}


class ChatViewHolder extends RecyclerView.ViewHolder{

    public TextView chat_user_box;
    public TextView chat_text_box;
    public ImageView profile_photo;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        chat_user_box=itemView.findViewById(R.id.chat_user_box);
        chat_text_box=itemView.findViewById(R.id.chat_text_box);
        profile_photo=itemView.findViewById(R.id.chat_profile);


    }
}