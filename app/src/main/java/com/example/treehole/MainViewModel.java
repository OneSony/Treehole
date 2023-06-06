package com.example.treehole;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.example.treehole.paging.MomentPagingSource;
import com.example.treehole.room.Moment;

import java.util.List;
import java.util.concurrent.ExecutionException;

import kotlinx.coroutines.CoroutineScope;

public class MainViewModel extends AndroidViewModel {


    private MomentRepository mMomentRepository;

    private LiveData<List<Moment>> AllMoment;

    MutableLiveData<PagingData<Moment>> momentMutableLiveData=new MutableLiveData<>();
    PagingConfig pagingConfig=new PagingConfig(3,1,false,3);//初始化配置,可以定义最大加载的数据量

    public MainViewModel(@NonNull Application application) {
        super(application);
        mMomentRepository=new MomentRepository(application);
        //AllMoment=mMomentRepository.getAllMoment();
    }

    public LiveData<List<Moment>> getAllMoment(){
        return AllMoment;
    }

    public void insert(Moment moment){
        mMomentRepository.insert(moment);
    }

    public void deleteAll(){mMomentRepository.deleteAll();}

    public LiveData<PagingData<Moment>> getPaging(){
        CoroutineScope viewModelScope= ViewModelKt.getViewModelScope(this);
        //Pager<Integer, Moment> pager = new Pager<Integer, Moment>(pagingConfig, ()->new MomentPagingSource(MomentDatabase.getDatabase(getApplication().getApplicationContext()).momentDao()));//构造函数根据自己的需要来调整
        Pager<String, Moment> pager = new Pager<String, Moment>(pagingConfig, ()->new MomentPagingSource());//构造函数根据自己的需要来调整


        return PagingLiveData.cachedIn(PagingLiveData.getLiveData(pager),viewModelScope);
    }



    public int getMomentCount() throws ExecutionException, InterruptedException {
        return mMomentRepository.getMomentCount();
    }
}
