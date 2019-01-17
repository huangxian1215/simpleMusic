package com.example.simplemusic.task;

import android.os.AsyncTask;
import android.os.Environment;

import com.example.simplemusic.bean.dbMusicInfo;
import com.example.simplemusic.database.UserDBHelper;

import java.io.File;
import java.util.ArrayList;

public class SearchLocalMusicTask extends AsyncTask<String, Integer, String> {
    private String[] ext={"mp3", "wav", "ogg", "flac"};//定义我们要查找的文件格式
    private UserDBHelper mHelper;

    public SearchLocalMusicTask(){
        super();
        mHelper = UserDBHelper.getmHelper();
    }

    @Override
    protected String doInBackground(String... params){
        //清理db里面被删除的歌曲
        ArrayList<dbMusicInfo> dbinfo =  mHelper.query_tab("1=1");
        for(int i = 0; i < dbinfo.size() ; i++){
            File file = new File(dbinfo.get(i).murl);
            String condition = String.format("url = '%s'", dbinfo.get(i).murl);
            if(!file.exists()){
                mHelper.delete(condition);
            }
        }
        //遍历存储内的所有音乐文件，保存到DB文件里面
        File allFile = Environment.getExternalStorageDirectory();
        search(allFile);
        return params[0];
    }

    private void search(File fileold){
        try {
            File[] files = fileold.listFiles();
            if (files.length > 0) {
                for (int j = 0; j < files.length; j++) {
                    if (!files[j].isDirectory()) {
                        String fileName = files[j].getAbsolutePath();//返回抽象路径名的绝对路径名字符串
                        String name = files[j].getName();//获得文件的名称
                        for (int i = 0; i < ext.length; i++) {
                            if (fileName.endsWith(ext[i])) {//判断文件后缀名是否包含我们定义的格式
                                long size = files[j].length();
                                if(size > 1258291){ //记录大于1.2M的文件
                                    writeDataToDB(name, fileName);
                                }
                                break;
                            }
                        }
                    }
                    else{
                            this.search(files[j]);
                        }
                    }
                }
        }
        catch(Exception e)
        {

        }
    }

    private void writeDataToDB(String name, String url){
        dbMusicInfo asong = new dbMusicInfo(name, url);
        //url如果没有这首歌就保存
        String condition = String.format("url = '%s'", url);
        if(mHelper.query_tab(condition).size() == 0){
            mHelper.insert(asong);
        }
    }

    @Override
    protected void onPostExecute(String info){
        mListener.onSearchLocalMusicInfo(info);
    }

    private OnSearchLocalListener mListener;
    public void setOnSearchLocalListener(OnSearchLocalListener listener){
        mListener = listener;
    }
    public static interface OnSearchLocalListener{
        public abstract void onSearchLocalMusicInfo(String info);
    }
}
