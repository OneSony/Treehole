package com.example.treehole;

/*public class MomentRepository {
    //private MomentDao mMomentDao;
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

    public void deleteAll(){new deleteAllAsyncTask(mMomentDao).execute();}

    public int getMomentCount() throws ExecutionException, InterruptedException {
        return new sizeAsyncTask(mMomentDao).execute().get();
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

    private static class deleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private MomentDao mAsyncTaskDao;

        deleteAllAsyncTask(MomentDao dao){
            mAsyncTaskDao=dao;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class sizeAsyncTask extends AsyncTask<Void,Void, Integer>{
        private MomentDao mAsyncTaskDao;

        sizeAsyncTask(MomentDao dao){
            mAsyncTaskDao=dao;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return mAsyncTaskDao.getMomentCount();
        }
    }

}
*/