package com.example.treehole;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.treehole.room.Moment;
import com.example.treehole.room.MomentDao;
import com.example.treehole.room.MomentDatabase;

import java.util.List;

public class MomentRepository {
    private MomentDao mMomentDao;
    private LiveData<List<Moment>> mAllMoment;

    MomentRepository(Application application){
        MomentDatabase db=MomentDatabase.getDatabase(application);
        mMomentDao=db.momentDao();
        mAllMoment=mMomentDao.getAll();
    }

    public LiveData<List<Moment>> getAllMoment(){
        return mAllMoment;
    }

    public void insert(Moment moment){
        new insertAsyncTask(mMomentDao).execute(moment);
    }

    private static class insertAsyncTask extends AsyncTask<Moment,Void,Void>{
        private MomentDao mAsyncTaskDao;

        insertAsyncTask(MomentDao dao){
            mAsyncTaskDao=dao;
        }

        @Override
        protected Void doInBackground(Moment... moments) {
            mAsyncTaskDao.insert(moments[0]);
            return null;
        }
    }

}
