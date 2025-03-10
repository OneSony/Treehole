package com.example.treehole.paging;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.treehole.R;
import com.example.treehole.utils.WebUtils;
import com.example.treehole.activity.InfoActivity;
import com.example.treehole.activity.PersonActivity;
import com.example.treehole.room.Moment;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

import io.noties.markwon.Markwon;

public class MomentPagingAdapter extends PagingDataAdapter<Moment, MomentPagingViewHolder> {


    private boolean photoClickable=true;


    Context context;

    public MomentPagingAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Moment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
                //return oldItem.getId().equals(newItem.getId());
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
                //Log.d("SAME?",String.valueOf(oldItem.getText().equals(newItem.getText())&&oldItem.topic.equals(newItem.topic)));
                //return oldItem.getText().equals(newItem.getText())&&oldItem.getTopic().equals(newItem.getTopic())&&oldItem.getLikes_num()==newItem.getLikes_num()&&oldItem.getFavourite_num()==newItem.getFavourite_num();
                return false;
            }
        });

        this.context=context;

    }

    public MomentPagingAdapter(Context context,boolean photoClickable) {
        super(new DiffUtil.ItemCallback<Moment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
                //return oldItem.getId().equals(newItem.getId());
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
                //Log.d("SAME?",String.valueOf(oldItem.getText().equals(newItem.getText())&&oldItem.topic.equals(newItem.topic)));
                //return oldItem.getText().equals(newItem.getText())&&oldItem.getTopic().equals(newItem.getTopic())&&oldItem.getLikes_num()==newItem.getLikes_num()&&oldItem.getFavourite_num()==newItem.getFavourite_num();
                return false;
            }
        });

        this.context=context;

        this.photoClickable=photoClickable;


    }


    @NonNull
    @Override
    public MomentPagingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mItemView=inflater.inflate(R.layout.moment_item,parent,false);
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
            String profile_photo_url = "https://rickyvu.pythonanywhere.com/users/profile_picture?id="+moment.getUser_id();
            Log.d("PROFILE URL",profile_photo_url);
            Glide.with(holder.itemView.getContext()).load(profile_photo_url).into(holder.profile_box);
            holder.auth_box.setText(moment.getUsername());
            holder.date_box.setText(moment.getDate());
            holder.topic_box.setText(moment.getTopic());
            holder.main_box.setText(moment.getText());


            holder.profile_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(photoClickable) {
                        Intent intent = new Intent(context, PersonActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("USERNAME", moment.getUsername());
                        bundle.putString("USER_ID", moment.getUser_id());
                        intent.putExtra("BUNDLE_DATA", bundle);
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context,"已进入该用户主页",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if(moment.isLiked()){
                holder.like_icon.setImageResource(R.drawable.like_true);
            }else{
                holder.like_icon.setImageResource(R.drawable.like_false);
            }

            if(moment.isFavourite()){
                holder.collect_icon.setImageResource(R.drawable.collect_true);
            }else{
                holder.collect_icon.setImageResource(R.drawable.collect_false);
            }



            holder.itemView.findViewById(R.id.like_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int like_num=Integer.parseInt(holder.like_box.getText().toString());
                    if(moment.isLiked()){
                        like_num--;
                        moment.likes_num_minus();
                        moment.setLiked(false);
                        holder.like_icon.setImageResource(R.drawable.like_false);
                    }else{
                        like_num++;
                        moment.likes_num_add();
                        moment.setLiked(true);
                        holder.like_icon.setImageResource(R.drawable.like_true);
                    }
                    holder.like_box.setText(String.valueOf(like_num));


                    JSONObject json = new JSONObject();
                    try {
                        json.put("id", moment.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    WebUtils.sendPost("/posts/like/", true, json, new WebUtils.WebCallback() {
                        @Override
                        public void onSuccess(JSONObject json) {
                            try {
                                Log.d("SUCCESS", json.getString("message"));
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
                    });
                }
            });

            holder.itemView.findViewById(R.id.collect_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int favourite_num=Integer.parseInt(holder.collect_box.getText().toString());
                    if(moment.isFavourite()){
                        favourite_num--;
                        moment.favourite_num_minus();
                        moment.setFavourite(false);
                        holder.collect_icon.setImageResource(R.drawable.collect_false);
                    }else{
                        favourite_num++;
                        moment.favourite_num_add();
                        moment.setFavourite(true);
                        holder.collect_icon.setImageResource(R.drawable.collect_true);
                    }
                    holder.collect_box.setText(String.valueOf(favourite_num));


                        JSONObject json = new JSONObject();
                        try {
                            json.put("id", moment.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        WebUtils.sendPost("/posts/favourite/", true, json, new WebUtils.WebCallback() {
                            @Override
                            public void onSuccess(JSONObject json) {
                                try {
                                    Log.d("SUCCESS", json.getString("message"));
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
                        });

                }
            });



            Log.d("PHOTO_SIZE",String.valueOf(moment.getImages().size()));

            int photo_num=moment.getImages().size();

            if(photo_num==0){
                holder.photos.setVisibility(View.GONE);
            }else{
                holder.photos.setVisibility(View.VISIBLE);

                for(int i=0;i<photo_num;i++){
                    holder.constraintLayouts[i].setVisibility(View.VISIBLE);
                    holder.imageViewArray[i].setVisibility(View.VISIBLE);
                    int finalI = i;
                    holder.imageViewArray[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                            dialog.setContentView(R.layout.dialog_photo);

                            // 创建动画对象
                            Animation animation = new AlphaAnimation(0.0f, 1.0f);
                            animation.setDuration(150); // 设置动画持续时间

// 将动画应用到对话框的窗口

                            Window window = dialog.getWindow();
                            if (window != null) {
                                window.setWindowAnimations(android.R.style.Animation_Dialog); // 设置窗口动画样式
                                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            }



                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    PhotoView photoView=dialog.findViewById(R.id.dialog_photo);
                                    Glide.with(holder.itemView.getContext()).load(moment.getImages().get(finalI)).into(photoView);
                                    photoView.setVisibility(View.VISIBLE);

                                    Button button=dialog.findViewById(R.id.dialog_button);
                                    if (button != null) {
                                        button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss(); // 点击对话框根布局，关闭对话框
                                            }
                                        });
                                    }
                                }
                            });


                            // 显示对话框
                            dialog.show();
                        }
                    });


                    Log.d("PHOTO_URL",String.valueOf(i)+moment.getImages().get(i));
                    Glide.with(holder.itemView.getContext()).load(moment.getImages().get(i)).into(holder.imageViewArray[i]);

                }



                if(photo_num<=3) {
                    for (int i = photo_num; i < 3; i++) {
                        holder.constraintLayouts[i].setVisibility(View.GONE);
                    }

                    for (int i = 3; i < 9; i++) {
                        holder.constraintLayouts[i].setVisibility(View.GONE);
                    }
                }else if(photo_num<=6){
                    for (int i = photo_num; i < 6; i++) {
                        holder.constraintLayouts[i].setVisibility(View.INVISIBLE);
                    }

                    for (int i = 6; i < 9; i++) {
                        holder.constraintLayouts[i].setVisibility(View.GONE);
                    }
                }else if(photo_num<=9){
                    for (int i = photo_num; i < 9; i++) {
                        holder.constraintLayouts[i].setVisibility(View.INVISIBLE);
                    }
                }
            }

            if(moment.getVideos().size()==0){
                holder.video.setVisibility(View.GONE);
            }else{
                holder.video.setVisibility(View.VISIBLE);

                holder.player = new ExoPlayer.Builder(context).build();
                holder.video.setPlayer(holder.player);
                // Build the media item.
                MediaItem mediaItem = MediaItem.fromUri(moment.getVideos().get(0));
                holder.player.setMediaItem(mediaItem);
                holder.player.prepare();
            }



            if(!moment.getLocation().equals("null")){
                View tagView = LayoutInflater.from(context).inflate(R.layout.location_item, holder.tag_layout, false);
                TextView textView = tagView.findViewById(R.id.location_item_text);
                textView.setText(moment.getLocation());
                holder.tag_layout.addView(tagView);
            }

            for (String tag : moment.getTags()) {
                View tagView = LayoutInflater.from(context).inflate(R.layout.tag_item, holder.tag_layout, false);
                TextView textView = tagView.findViewById(R.id.tag_item_text);
                textView.setText(tag);
                holder.tag_layout.addView(tagView);
            }



            holder.like_box.setText(String.valueOf(moment.getLikes_num()));
            holder.comment_box.setText(String.valueOf(moment.getComment_num()));
            holder.collect_box.setText(String.valueOf(moment.getFavourite_num()));

            if(moment.getText_type().equals("markdown")){
                Markwon markwon = Markwon.create(context);
                //markdownTextView.setMovementMethod(new ScrollingMovementMethod()); // 启用滚动
                markwon.setMarkdown(holder.main_box,moment.getText());//先把已经有的放进去
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 在这里处理项目点击事件
                    // 显示项目ID，可以通过Toast或者其他方式展示
                    //Toast.makeText(view.getContext(), "position: " + position, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, InfoActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("MOMENT",moment);
                    intent.putExtra("BUNDLE_DATA",bundle);

                    context.startActivity(intent);
                }
            });
        }




    }


    @Override
    public void onViewRecycled(@NonNull MomentPagingViewHolder holder) {
        if(holder.player!=null){
            holder.player.release();
        }

        if(holder.tag_layout!=null) {
            holder.tag_layout.removeAllViews();
        }
        super.onViewRecycled(holder);
    }






    /*

    private static final DiffUtil.ItemCallback<Moment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Moment>() {
        @Override
        public boolean areItemsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Moment oldItem, @NonNull Moment newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };*/

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


    public final ImageView[] imageViewArray;

    public final ImageView like_icon;
    public final ImageView comment_icon;
    public final ImageView collect_icon;
    public final ConstraintLayout[] constraintLayouts;

    public final ConstraintLayout like_layout;
    public final ConstraintLayout comment_layout;
    public final ConstraintLayout collect_layout;



    public final LinearLayout photos;
    public final PlayerView video;

    public final FlexboxLayout tag_layout;

    ExoPlayer player;

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

        imageViewArray = new ImageView[9];
        imageViewArray[0]=itemView.findViewById(R.id.moment_photo1);
        imageViewArray[1]=itemView.findViewById(R.id.moment_photo2);
        imageViewArray[2]=itemView.findViewById(R.id.moment_photo3);

        imageViewArray[3]=itemView.findViewById(R.id.moment_photo4);
        imageViewArray[4]=itemView.findViewById(R.id.moment_photo5);
        imageViewArray[5]=itemView.findViewById(R.id.moment_photo6);

        imageViewArray[6]=itemView.findViewById(R.id.moment_photo7);
        imageViewArray[7]=itemView.findViewById(R.id.moment_photo8);
        imageViewArray[8]=itemView.findViewById(R.id.moment_photo9);

        constraintLayouts = new ConstraintLayout[9];
        constraintLayouts[0]=itemView.findViewById(R.id.moment_photo_layout1);
        constraintLayouts[1]=itemView.findViewById(R.id.moment_photo_layout2);
        constraintLayouts[2]=itemView.findViewById(R.id.moment_photo_layout3);
        constraintLayouts[3]=itemView.findViewById(R.id.moment_photo_layout4);
        constraintLayouts[4]=itemView.findViewById(R.id.moment_photo_layout5);
        constraintLayouts[5]=itemView.findViewById(R.id.moment_photo_layout6);
        constraintLayouts[6]=itemView.findViewById(R.id.moment_photo_layout7);
        constraintLayouts[7]=itemView.findViewById(R.id.moment_photo_layout8);
        constraintLayouts[8]=itemView.findViewById(R.id.moment_photo_layout9);

        like_icon=itemView.findViewById(R.id.like_icon);
        comment_icon=itemView.findViewById(R.id.comment_icon);
        collect_icon=itemView.findViewById(R.id.collect_icon);

        like_layout=itemView.findViewById(R.id.like_layout);
        comment_layout=itemView.findViewById(R.id.comment_layout);
        collect_layout=itemView.findViewById(R.id.collect_layout);




        photos=itemView.findViewById(R.id.moment_photos);
        video=itemView.findViewById(R.id.moment_video);

        tag_layout=itemView.findViewById(R.id.tag_layout);
    }

}

