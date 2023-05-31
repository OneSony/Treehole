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
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executors;

public class MomentPagingSource extends ListenableFuturePagingSource<Integer, Moment> {
    //需要用到线程池
    private ListeningExecutorService executorService= MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    String query;
    public MomentPagingSource(String query){
        this.query=query;//查询内容所需参数
    }

    @NotNull
    @Override
    public ListenableFuture<LoadResult<Integer, Moment>> loadFuture(@NotNull LoadParams<Integer> params) {

        Integer nextPageNumber = params.getKey();
        if (nextPageNumber == null) {
            nextPageNumber = 1;
        }

        // 创建一个ListenableFuture的实例，用于异步获取数据
        SettableFuture<LoadResult<Integer, Moment>> future = SettableFuture.create();

        // 在这里进行网络请求，从服务器获取Moment结构体数据
        // 这里假设你使用的是某个网络库或框架进行网络请求

        WebUtils.WebCallback callback = new WebUtils.WebCallback() {
            @Override
            public void onSuccess(JSONObject json) {
                Log.e("OkHttp", "?");

                List<Moment> moments = null;//需要把json翻译成moments

                LoadResult<Integer, Moment> result = new LoadResult.Page<>(
                        moments,  // 当前页的数据
                        null,     // 上一页的键（可选，如果没有上一页则为null）
                        null      // 下一页的键（可选，如果没有下一页则为null），应该要写当前的最后一个的index
                );
                future.set(result); // 设置异步任务的结果

            }

            @Override
            public void onError(Throwable t) {
                future.setException(t);
            }

            @Override
            public void onFailure(JSONObject json) {

            }
        };
        //使用网络！！
        //WebUtils.sendGet(,,);
        return future; // 返回ListenableFuture对象
    }


    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Moment> pagingState) {
        return null;
    }
}