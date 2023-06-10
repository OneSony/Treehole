package com.example.treehole.paging;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.example.treehole.WebUtils;
import com.example.treehole.room.Moment;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class MomentPagingSource extends ListenableFuturePagingSource<String, Moment> {

    private List<String> searchWords=Collections.emptyList();


    //需要用到线程池
    private ListeningExecutorService executorService= MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    String query;
    String searchType="";

    String sortType="date";
    //MomentDao myDao;
    /*public MomentPagingSource(String query){
        this.query=query;//查询内容所需参数
    }*/

    public MomentPagingSource() {

    }

    public MomentPagingSource(String searchType,List<String> searchWords) {
        this.searchType=searchType;
        this.searchWords = searchWords;
    }

    public MomentPagingSource(String sortType) {
        this.sortType=sortType;
    }

    public MomentPagingSource(String searchType,String justForOverload) {
        this.searchType=searchType;
    }

    @NotNull
    @Override
    public ListenableFuture<LoadResult<String, Moment>> loadFuture(@NotNull LoadParams<String> params) {

        String nextPageNumber = params.getKey();
        Log.d("KEY",String.valueOf(nextPageNumber));
        if (nextPageNumber == null) {
            nextPageNumber = "";
        }

        Log.d("NEXTPAGE","next is "+nextPageNumber);

        SettableFuture<LoadResult<String, Moment>> future = SettableFuture.create();

        List<Moment> moments= new ArrayList<>();


        JSONObject queryData = new JSONObject();
        try {
            Log.d("NEXTPAGE","sending "+nextPageNumber);
            JSONArray keyWords = new JSONArray();


            if(searchWords.size() == 0) {
                keyWords.put("");
            }else{
                //Log.d("Search Size","search words "+searchWords.get(0));
                for(String word : searchWords) {
                    keyWords.put(word);
                }
            }

            queryData.put("filter_by", searchType);
            queryData.put("start", nextPageNumber);
            queryData.put("count", 10);
            queryData.put("key_words", keyWords);
            queryData.put("order_by", sortType);
            queryData.put("order", "desc");

            Log.d("Search Size",queryData.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        WebUtils.sendPost("/posts/retrieve/", false, queryData, new WebUtils.WebCallback() {

            @Override
            public void onSuccess(JSONObject json) {
                Log.d("PAGING",json.toString());
                try {
                    JSONArray moments_json = null;
                    //String size_json=json.getString("size");

                    //Log.d("MOMENT SIZE",size_json);
                    moments_json = json.getJSONArray("message");
                    for (int i = 0; i < moments_json.length(); i++) {
                        JSONObject moment = (JSONObject) moments_json.get(i);
                        String id = moment.getString("id");
                        String user_id = moment.getString("user_id");
                        String username = moment.getString("user");
                        String topic = moment.optString("title");
                        String text = moment.getString("text");
                        String text_type = moment.getString("text_type");
                        String date = moment.getString("pub_date");
                        Boolean is_liked = moment.getBoolean("is_liked");
                        Boolean is_favourited = moment.getBoolean("is_favourited");



                        Log.d("NEXTPAGE","id "+id);

                        Moment tempMoment=new Moment(id,user_id,username,topic,text,date);

                        int likes_num = moment.getInt("likes");
                        int favourites_num = moment.getInt("favourites");
                        tempMoment.setNum(likes_num,favourites_num);

                        {
                            Object imagesObject = moment.get("images");
                            List<String> imagesList = new ArrayList<>();
                            if (imagesObject instanceof JSONArray) {
                                JSONArray nestedArray = (JSONArray) imagesObject;

                                // 处理列表中的数据
                                for (int ii = 0; ii < nestedArray.length(); ii++) {
                                    Object listItem = nestedArray.get(ii);
                                    // 将列表项转换为字符串并添加到 List<String> 中
                                    imagesList.add(String.valueOf(listItem));
                                }
                            }


                            Object videoObject = moment.get("videos");
                            List<String> videosList = new ArrayList<>();
                            if (videoObject instanceof JSONArray) {
                                JSONArray nestedArray = (JSONArray) videoObject;

                                // 处理列表中的数据
                                for (int ii = 0; ii < nestedArray.length(); ii++) {
                                    Object listItem = nestedArray.get(ii);
                                    // 将列表项转换为字符串并添加到 List<String> 中
                                    videosList.add(String.valueOf(listItem));
                                }
                            }

                            tempMoment.setMedias(imagesList,videosList);
                        }


                        {

                            Object tagsObject = moment.get("tags");
                            List<String> tagsList = new ArrayList<>();
                            if (tagsObject instanceof JSONArray) {
                                JSONArray nestedArray = (JSONArray) tagsObject;

                                // 处理列表中的数据
                                for (int ii = 0; ii < nestedArray.length(); ii++) {
                                    Object listItem = nestedArray.get(ii);
                                    // 将列表项转换为字符串并添加到 List<String> 中
                                    tagsList.add(String.valueOf(listItem));
                                }
                            }

                            tempMoment.setTags(tagsList);
                        }


                        String location = moment.getString("pub_location");
                        tempMoment.setLocation(location);

                        tempMoment.setLiked(is_liked,is_favourited);
                        tempMoment.setText_type(text_type);
                        Log.d("TYPE","type is "+text_type);



                        moments.add(tempMoment);
                    }
                    Log.d("NEXTPAGE","we got "+moments_json.length());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


                LoadResult<String, Moment> result;

                if (moments.isEmpty()) {
                    result = new LoadResult.Page<>(Collections.emptyList(), null, null);
                }else {

                    result = new LoadResult.Page<>(
                            moments, // 当前页的数据
                            null,
                            String.valueOf(moments.get(moments.size() - 1).getId()),
                            LoadResult.Page.COUNT_UNDEFINED,
                            LoadResult.Page.COUNT_UNDEFINED
                    );
                }
                future.set(result);
            }

            @Override
            public void onError(Throwable t) {
                Log.d("NEXTPAGE","error");
                Log.d("POSTRETRIEVE", t.getMessage());

                LoadResult<String, Moment> result=new LoadResult.Page<>(Collections.emptyList(), null, null);;
                future.set(result);
            }

            @Override
            public void onFailure(JSONObject json) {
                Log.d("POSTRETRIEVE", json.optString("message", "onFailure"));

                LoadResult<String, Moment> result=new LoadResult.Page<>(Collections.emptyList(), null, null);;
                future.set(result);
            }
        });

        /*
        //List<Moment> moments = myDao.getNextMoments(finalNextPageNumber);
        boolean reachedLastPage = moments.isEmpty(); // 检查是否已经到达最后一页
        if (reachedLastPage) {
            return Collections.emptyList(); // 返回空的列表表示没有更多的数据
        } else {
            return moments;
        }*/

        return future;
    }


    @Nullable
    @Override
    public String getRefreshKey(@NonNull PagingState<String, Moment> pagingState) {
        return null;
    }

}