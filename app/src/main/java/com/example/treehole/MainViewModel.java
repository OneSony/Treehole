package com.example.treehole;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.treehole.room.Moment;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MomentRepository mMomentRepository;

    private LiveData<List<Moment>> AllMoment;
    public MainViewModel(@NonNull Application application) {
        super(application);
        mMomentRepository=new MomentRepository(application);
        AllMoment=mMomentRepository.getAllMoment();
    }

    public LiveData<List<Moment>> getAllMoment(){
        return AllMoment;
    }

    public void insert(Moment moment){
        mMomentRepository.insert(moment);
    }
}
