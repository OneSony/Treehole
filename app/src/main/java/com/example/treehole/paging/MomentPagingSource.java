package com.example.treehole.paging;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.example.treehole.room.Moment;
import com.example.treehole.room.MomentDao;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class MomentPagingSource extends ListenableFuturePagingSource<Integer, Moment> {
    //需要用到线程池
    private ListeningExecutorService executorService= MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    String query;
    MomentDao myDao;
    /*public MomentPagingSource(String query){
        this.query=query;//查询内容所需参数
    }*/

    public MomentPagingSource(MomentDao myDao) {
        this.myDao = myDao;
    }

    @NotNull
    @Override
    public ListenableFuture<LoadResult<Integer, Moment>> loadFuture(@NotNull LoadParams<Integer> params) {

        Integer nextPageNumber = params.getKey();
        Log.d("KEY",String.valueOf(nextPageNumber));
        if (nextPageNumber == null) {
            nextPageNumber = 1;
        }

        Log.d("NEXTPAGE", nextPageNumber.toString());





        SettableFuture<LoadResult<Integer, Moment>> future = SettableFuture.create();

        Integer finalNextPageNumber = nextPageNumber;

        /*ListenableFuture<List<Moment>> momentsFuture = executorService.submit(() -> {
            return myDao.getMomentsFromIndex(finalNextPageNumber);
        });*/

        ListenableFuture<List<Moment>> momentsFuture = executorService.submit(() -> {
            List<Moment> moments = myDao.getNextMoments(finalNextPageNumber);
            boolean reachedLastPage = moments.isEmpty(); // 检查是否已经到达最后一页
            if (reachedLastPage) {
                return Collections.emptyList(); // 返回空的列表表示没有更多的数据
            } else {
                return moments;
            }
        });

        Futures.addCallback(momentsFuture, new FutureCallback<List<Moment>>() {
            @Override
            public void onSuccess(@Nullable List<Moment> moments) {
                LoadResult<Integer, Moment> result;

                if (moments.isEmpty()) {
                    result = new LoadResult.Page<>(Collections.emptyList(), null, null);
                }else {

                    result = new LoadResult.Page<>(
                            moments, // 当前页的数据
                            null,
                            moments.get(moments.size() - 1).m_index,
                            LoadResult.Page.COUNT_UNDEFINED,
                            LoadResult.Page.COUNT_UNDEFINED
                    );
                }
                future.set(result);
            }

            @Override
            public void onFailure(Throwable t) {
                future.setException(t);
            }
        }, executorService);

        return future;

        // 在这里进行网络请求，从服务器获取Moment结构体数据
        // 这里假设你使用的是某个网络库或框架进行网络请求

        /*

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
        };*/
        //使用网络！！
        //WebUtils.sendGet(,,);
    }


    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Moment> pagingState) {
        return null;
    }
}