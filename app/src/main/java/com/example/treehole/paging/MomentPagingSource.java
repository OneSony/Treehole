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
import com.google.gson.JsonArray;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class MomentPagingSource extends ListenableFuturePagingSource<String, Moment> {
    //需要用到线程池
    private ListeningExecutorService executorService= MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    String query;
    //MomentDao myDao;
    /*public MomentPagingSource(String query){
        this.query=query;//查询内容所需参数
    }*/

    /*public MomentPagingSource(MomentDao myDao) {
        this.myDao = myDao;
    }*/

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
            JsonArray keyWords = new JsonArray();
            keyWords.add("");

            queryData.put("start", nextPageNumber);
            queryData.put("count", 5);
            queryData.put("filter_by", "");
            queryData.put("key_words", keyWords);
            queryData.put("order_by", "date");
            queryData.put("order", "desc");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        WebUtils.sendPost("/posts/retrieve/", false, queryData, new WebUtils.WebCallback() {

            @Override
            public void onSuccess(JSONObject json) {
                JSONArray moments_json = null;
                try {
                    moments_json = json.getJSONArray("message");
                    for (int i = 0; i < moments_json.length(); i++) {
                        JSONObject moment = (JSONObject) moments_json.get(i);
                        String topic = moment.optString("title");
                        String text = moment.getString("text");
                        String id = moment.getString("id");
                        Log.d("NEXTPAGE","id "+id);
                        moments.add(new Moment(id,topic,text));
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
                            String.valueOf(moments.get(moments.size() - 1).id),
                            LoadResult.Page.COUNT_UNDEFINED,
                            LoadResult.Page.COUNT_UNDEFINED
                    );
                }
                future.set(result);
            }

            @Override
            public void onError(Throwable t) {
                Log.d("POSTRETRIEVE", t.getMessage());
            }

            @Override
            public void onFailure(JSONObject json) {
                Log.d("POSTRETRIEVE", json.optString("message", "onFailure"));
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