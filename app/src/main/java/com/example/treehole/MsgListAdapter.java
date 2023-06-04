package com.example.treehole;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
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

import com.example.treehole.room.MessageNode;

import java.util.List;


public class MsgListAdapter extends RecyclerView.Adapter<MsgViewHolder> {

    List<MessageNode> messageNodes;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //public final MainActivity mainPage;
    public Context context;


    public MsgListAdapter(Context context){
        this.context=context;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {

        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();

        int res;
        res = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
        theme.resolveAttribute(res, typedValue, true);
        int colorPrimary = typedValue.data;

        res = context.getResources().getIdentifier("colorOnPrimary", "attr", context.getPackageName());
        theme.resolveAttribute(res, typedValue, true);
        int colorOnPrimary = typedValue.data;

        res = context.getResources().getIdentifier("colorSecondary", "attr", context.getPackageName());
        theme.resolveAttribute(res, typedValue, true);
        int colorSecondary = typedValue.data;

        res = context.getResources().getIdentifier("colorOnSecondary", "attr", context.getPackageName());
        theme.resolveAttribute(res, typedValue, true);
        int colorOnSecondary = typedValue.data;

        if(messageNodes.get(position).getUser()==0){

            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(200);
            lp.setMarginStart(20);
            holder.constraintLayout.setLayoutParams(lp);


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(holder.constraintLayout);
            constraintSet.clear(R.id.msg_card, ConstraintSet.END);
            constraintSet.connect(R.id.msg_card, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.msg_card, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            constraintSet.connect(R.id.msg_card, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraintSet.clear(R.id.msg_card, ConstraintSet.END);

            constraintSet.applyTo(holder.constraintLayout);

            holder.cardView.setCardBackgroundColor(colorSecondary);

            TextView textView = holder.cardView.findViewById(R.id.msg_text);
            textView.setTextColor(colorOnSecondary);


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

            holder.cardView.setCardBackgroundColor(colorPrimary);

            TextView textView = holder.cardView.findViewById(R.id.msg_text);
            textView.setTextColor(colorOnPrimary);

        }

        holder.textView.setText(messageNodes.get(position).getText());

    }

    public void setMessageNodes(List<MessageNode> messageNodes){
        this.messageNodes=messageNodes;
    }


    @Override
    public int getItemCount() {
        if(messageNodes==null){
            return 0;
        }else{
            return messageNodes.size();
        }
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

    public MsgViewHolder(@NonNull View itemView) {
        super(itemView);

        textView=itemView.findViewById(R.id.msg_text);
        constraintLayout=itemView.findViewById(R.id.msg_layout);
        cardView=itemView.findViewById(R.id.msg_card);
    }
}