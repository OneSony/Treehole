package com.example.treehole.activity;

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

import kotlinx.coroutines.CoroutineScope;

public class MainViewModel extends AndroidViewModel {

    private String default_sortType="date";


    //private MomentRepository mMomentRepository;

    //private LiveData<List<Moment>> AllMoment;

    //MutableLiveData<PagingData<Moment>> momentMutableLiveData=new MutableLiveData<>();
    PagingConfig pagingConfig=new PagingConfig(10,5,false,10);//初始化配置,可以定义最大加载的数据量

    private LiveData<PagingData<Moment>> paging;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }


    public void setDefault_sortType(String default_sortType) {
        this.default_sortType = default_sortType;
    }

    /*
    public LiveData<List<Moment>> getAllMoment(){
        return AllMoment;
    }

    public void insert(Moment moment){
        mMomentRepository.insert(moment);
    }

    public void deleteAll(){mMomentRepository.deleteAll();}*/

    private LiveData<PagingData<Moment>> _getPaging(){
        CoroutineScope viewModelScope= ViewModelKt.getViewModelScope(this);

        //Pager<Integer, Moment> pager = new Pager<Integer, Moment>(pagingConfig, ()->new MomentPagingSource(MomentDatabase.getDatabase(getApplication().getApplicationContext()).momentDao()));//构造函数根据自己的需要来调整
        Pager<String, Moment> pager = new Pager<String, Moment>(pagingConfig, ()->new MomentPagingSource());//构造函数根据自己的需要来调整


        return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewModelScope);
    }

    public LiveData<PagingData<Moment>> getPaging(){
        if(paging==null){
            return getNewPaging(default_sortType);
        }else {
            return paging;
        }
    }

    public LiveData<PagingData<Moment>> getNewPaging(String sortType){
        CoroutineScope viewModelScope= ViewModelKt.getViewModelScope(this);

        //MomentPagingSource pagingSource = new MomentPagingSource(sortType);
        Pager<String, Moment> pager = new Pager<String, Moment>(pagingConfig, ()->new MomentPagingSource(sortType));//构造函数根据自己的需要来调整

        paging=PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewModelScope);
        return paging;
    }

    public LiveData<PagingData<Moment>> getNewPaging(String sortType,String filter){
        CoroutineScope viewModelScope= ViewModelKt.getViewModelScope(this);

        //MomentPagingSource pagingSource = new MomentPagingSource(sortType);
        Pager<String, Moment> pager = new Pager<String, Moment>(pagingConfig, ()->new MomentPagingSource(filter,""));//构造函数根据自己的需要来调整

        paging=PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewModelScope);
        return paging;
    }




/*
    public int getMomentCount() throws ExecutionException, InterruptedException {
        return mMomentRepository.getMomentCount();
    }*/
}
