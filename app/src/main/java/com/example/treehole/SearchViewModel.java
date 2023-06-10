package com.example.treehole;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.example.treehole.paging.MomentPagingSource;
import com.example.treehole.room.Moment;

import java.util.List;

import kotlinx.coroutines.CoroutineScope;

public class SearchViewModel extends AndroidViewModel {

    PagingConfig pagingConfig=new PagingConfig(10,5,false,10);//初始化配置,可以定义最大加载的数据量

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<PagingData<Moment>> getPaging(String searchType,List<String> searchWords){
        CoroutineScope viewModelScope= ViewModelKt.getViewModelScope(this);
        Pager<String, Moment> pager = new Pager<String, Moment>(pagingConfig, ()->new MomentPagingSource(searchType,searchWords));//构造函数根据自己的需要来调整

        return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewModelScope);
    }

    public LiveData<PagingData<Moment>> getPaging(String searchType){
        CoroutineScope viewModelScope= ViewModelKt.getViewModelScope(this);
        Pager<String, Moment> pager = new Pager<String, Moment>(pagingConfig, ()->new MomentPagingSource(searchType,""));//构造函数根据自己的需要来调整

        return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewModelScope);
    }

}
