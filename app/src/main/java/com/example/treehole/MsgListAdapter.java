package com.example.treehole;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;


public class MsgListAdapter extends RecyclerView.Adapter<MsgViewHolder> {
    private final LayoutInflater inflater;

    private dot_list data_list;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    //public final MainActivity mainPage;
    public Context context;


    public MsgListAdapter(Context context, dot_list _dot_list){
        inflater=LayoutInflater.from(context);
        data_list=_dot_list;
        //this.mainPage=main_page;
        this.context=context;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View mItemView=inflater.inflate(R.layout.msg_item,parent,false);
        return new MsgViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {

        if(position%2==0){

            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(200);
            lp.setMarginStart(20);
            holder.constraintLayout.setLayoutParams(lp);


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(holder.constraintLayout);
            constraintSet.connect(R.id.msg_card, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.msg_card, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(R.id.msg_card, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.clear(R.id.msg_card, ConstraintSet.END);

            constraintSet.applyTo(holder.constraintLayout);

            holder.cardView.setCardBackgroundColor(Color.rgb(245,245,245));


        }else{

            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(20);
            lp.setMarginStart(200);

            holder.constraintLayout.setLayoutParams(lp);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(holder.constraintLayout);
            constraintSet.connect(R.id.msg_card, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.msg_card, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(R.id.msg_card, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            constraintSet.clear(R.id.msg_card, ConstraintSet.START);

            constraintSet.applyTo(holder.constraintLayout);

            holder.cardView.setCardBackgroundColor(Color.rgb(255,192,203));


        }

        holder.textView.setText(data_list.get(position).getText());

    }


    @Override
    public int getItemCount() {
        return data_list.size();
        //return 0;
    }

    @Override
    public void onViewRecycled(MsgViewHolder holder) {
        // 清除 ViewHolder 中的内存
        //holder.photo1.setImageBitmap(null);
    }

}

class MsgViewHolder extends RecyclerView.ViewHolder{

    public TextView textView;
    public ConstraintLayout constraintLayout;
    public CardView cardView;

    public MsgViewHolder(@NonNull View itemView, MsgListAdapter chatListAdapter) {
        super(itemView);

        textView=itemView.findViewById(R.id.msg_text);
        constraintLayout=itemView.findViewById(R.id.msg_layout);
        cardView=itemView.findViewById(R.id.msg_card);
    }
}