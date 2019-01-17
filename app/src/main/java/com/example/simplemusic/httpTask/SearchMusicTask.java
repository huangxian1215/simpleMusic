package com.example.simplemusic.httpTask;

import android.os.AsyncTask;
import android.util.Log;

import com.example.simplemusic.http.HttpRequestUtil;
import com.example.simplemusic.tool.HttpReqData;
import com.example.simplemusic.tool.HttpRespData;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchMusicTask extends AsyncTask<String, Integer, String>{
    private String mSearchName = "";
    private String mCookie = "";
    private final static String TAG = "SearchMusicTask";
    private static String mAddressUrl = "http://47.107.101.237/api?method=search&key=%E6%88%91%E5%A5%BD%E5%83%8F%E5%9C%A8%E5%93%AA%E8%A7%81%E8%BF%87%E4%BD%A0%20&pageIndex=1&pageSize=10&_=1545811421602";
    //源码格式：url := fmt.Sprintf("http://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=%v&n=%v&w=%v&&jsonpCallback=searchCallbacksong2020&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0", pageIndex, pageSize, key)
    //http://47.107.101.237/api?method=search&key=serenity&pageIndex=1&pageSize=20&_=1545961696602
    public SearchMusicTask(String searchName, String time){
        super();
        //mSearchName = "http://47.107.101.237/api?method=search&key=" + searchName + "&pageIndex=1&pageSize=20&_="+time;
        mSearchName = "http://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=100&w="+ searchName +"&&jsonpCallback=searchCallbacksong2020&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
    }

    @Override
    protected String doInBackground(String... params){

        HttpReqData req_data = new HttpReqData(mSearchName);
        HttpRespData resp_data = HttpRequestUtil.getData(req_data);
        Log.d(TAG, "return json = " + resp_data.content);

        String dataStr = "";
        if (resp_data.err_msg.length() <= 0) {
            //查询歌曲
            dataStr = resp_data.content;
//            try {
//                JSONObject obj = new JSONObject(resp_data.content);
//                dataStr = obj.getString("Data");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            //下载歌曲的清单
        }
        return dataStr;
    }

    @Override
    protected void onPostExecute(String info){
        mListener.onGetHttpInfo(info);
    }

    private OnGetSearchInfoListener mListener;
    public void setOnSearchMusicListener(OnGetSearchInfoListener listener){
        mListener = listener;
    }
    public static interface OnGetSearchInfoListener{
        public abstract void onGetHttpInfo(String info);
    }
}

