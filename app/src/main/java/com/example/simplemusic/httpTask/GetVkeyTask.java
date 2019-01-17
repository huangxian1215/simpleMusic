package com.example.simplemusic.httpTask;

import android.os.AsyncTask;
import android.util.Log;

import com.example.simplemusic.http.HttpRequestUtil;
import com.example.simplemusic.tool.HttpReqData;
import com.example.simplemusic.tool.HttpRespData;

import org.json.JSONException;
import org.json.JSONObject;

public class GetVkeyTask extends AsyncTask<String, Integer, String>{
    private String mVkeyUrl = "";
    private final static String TAG = "GetVkeyTask";
    public GetVkeyTask(){
        super();
        mVkeyUrl = "http://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=0&loginUin=19901215&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&uin=19901215&songmid=003a1tne1nSz1Y&filename=C400003a1tne1nSz1Y.m4a&guid=1234567890";
    }

    @Override
    protected String doInBackground(String... params){
        String dataStr = "";
        if(!params[0].equals("firstVkey")){
            try {
                Thread.sleep(1000*60*60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HttpReqData req_data = new HttpReqData(mVkeyUrl);
        HttpRespData resp_data = HttpRequestUtil.getData(req_data);
        Log.d(TAG, "return json = " + resp_data.content);

        if (resp_data.err_msg.length() <= 0) {
            //查询vkey
            try {
                JSONObject obj = new JSONObject(resp_data.content);
                //getJSONObject("data").getJSONObject("song").getJSONArray("list");
                dataStr = obj.getJSONObject("data").getJSONArray("items").getJSONObject(0).getString("vkey");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return dataStr;
    }

    @Override
    protected void onPostExecute(String info){
        mListener.onGetHttpVkeyInfo(info);
    }

    private OnGetVkeyListener mListener;
    public void setOnGetVkeyListener(OnGetVkeyListener listener){
        mListener = listener;
    }
    public static interface OnGetVkeyListener{
        public abstract void onGetHttpVkeyInfo(String info);
    }
}

