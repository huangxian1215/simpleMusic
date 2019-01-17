package com.example.simplemusic.httpTask;


import android.os.AsyncTask;
import android.util.Log;

import com.example.simplemusic.http.HttpRequestUtil;
import com.example.simplemusic.tool.HttpReqData;
import com.example.simplemusic.tool.HttpRespData;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

public class GetLrcTask extends AsyncTask<String, Integer, String>{
    private String mSearchName = "";
    private String mCookie = "";
    private String mHost = "";
    private String mReferer = "";
    private final static String TAG = "SearchMusicTask";
    public GetLrcTask(String musicId, String cookie){
        super();
        mSearchName = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?callback=MusicJsonCallback_lrc&pcachetime=1535000999863&songmid="+musicId+"&g_tk=208010740&jsonpCallback=MusicJsonCallback_lrc&loginUin=603350867&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
        mCookie = cookie;
        mHost = "c.y.qq.com";
        mReferer = "https://y.qq.com/portal/player.html";
    }

    @Override
    protected String doInBackground(String... params){

        HttpReqData req_data = new HttpReqData(mSearchName);
        req_data.cookie = mCookie;
        req_data.host = mHost;
        req_data.referer = mReferer;

        HttpRespData resp_data = HttpRequestUtil.getData(req_data);
        Log.d(TAG, "return json = " + resp_data.content);

        String lrc = "";
        if (resp_data.err_msg.length() <= 0) {
            try {
                JSONObject obj = new JSONObject(resp_data.content);
                lrc = obj.getString("lyric");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            lrc = new String(Base64.decode(lrc.getBytes(), Base64.DEFAULT));
        }
        return lrc;
    }

    @Override
    protected void onPostExecute(String info){
        mListener.onGetLrcInfo(info);
    }

    private OnGetLrcInfoListener mListener;
    public void setOnSearchMusicListener(OnGetLrcInfoListener listener){
        mListener = listener;
    }
    public static interface OnGetLrcInfoListener{
        public abstract void onGetLrcInfo(String info);
    }
}

