package com.example.treehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;


public class dot_list_adapter extends RecyclerView.Adapter<dot_view_holder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public final dot_list data_list;
    private final LayoutInflater inflater;
    //public final MainActivity mainPage;
    public Context context;

    private int photo_num;

    public dot_list_adapter(Context context, dot_list _dot_list/*, MainActivity main_page*/){
        inflater=LayoutInflater.from(context);
        this.data_list=_dot_list;
        //this.mainPage=main_page;
        this.context=context;
    }

    @NonNull
    @Override
    public dot_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView=inflater.inflate(R.layout.item,parent,false);
        return new dot_view_holder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull dot_view_holder holder, int position) {
        holder.topic_box.setText(data_list.get(position).getTopic());
        holder.main_box.setText(data_list.get(position).getText());
        holder.auth_box.setText(data_list.get(position).getAuth());
        holder.date_box.setText(data_list.get(position).getDate());


        holder.like_box.setText(String.valueOf(data_list.get(position).getLike_num()));
        holder.comment_box.setText(String.valueOf(data_list.get(position).getComment_num()));
        holder.collect_box.setText(String.valueOf(data_list.get(position).getCollect_num()));

        holder.profile_box.setImageResource(data_list.get(position).getProfile_index());

        holder.photo1.setVisibility(View.GONE);
        holder.photo2.setVisibility(View.GONE);
        holder.photo3.setVisibility(View.GONE);


        photo_num=data_list.get(position).getPhoto_num();
        if(photo_num == 1){
            holder.photo1.setVisibility(View.VISIBLE);
            holder.photo1.setImageResource(data_list.get(position).getPhoto_index(0));
        } else if (photo_num == 2){
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

        if(data_list.get(position).isPath_flag()==true){
            holder.photo1.setVisibility(View.VISIBLE);

            String url = "https://rickyvu.pythonanywhere.com/static/images/test1.png";
            Glide.with(holder.itemView.getContext()).load(url).into(holder.photo1);

            /*File imgFile = new File(data_list.get(position).getPhoto_path());
            if(imgFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.photo1.setImageBitmap(bitmap);
            }*/
            //holder.photo1.setImageResource(R.drawable.photo1);
        }

        /*holder.photo1.setImageResource(R.drawable.photo);
        holder.photo2.setImageResource(R.drawable.photo);
        holder.photo3.setImageResource(R.drawable.photo);*/

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
    }

    @Override
    public void onViewRecycled(dot_view_holder holder) {
        // 清除 ViewHolder 中的内存
        holder.photo1.setImageBitmap(null);
    }

}


class dot_view_holder extends RecyclerView.ViewHolder{
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
    public dot_view_holder(@NonNull View itemView, dot_list_adapter dot_list_adapter) {
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

        /*itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("MainActivity",String.valueOf(getLayoutPosition()));
                //Intent intent=new Intent(dot_list_adapter.mainPage,InfoActivity.class);
                //intent.putExtra(Intent.EXTRA_PACKAGE_NAME,getLayoutPosition());
                //String msg=dot_list_adapter.data_list.get(getLayoutPosition()).getTopic();
                //intent.putExtra(EXTRA_MESSAGE,msg);
                //dot_list_adapter.mainPage.startActivity(intent);
                dot_list_adapter.mainPage.launch_info(getLayoutPosition());
            }
        });*/
    }
}