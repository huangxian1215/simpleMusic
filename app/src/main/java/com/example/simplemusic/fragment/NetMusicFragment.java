package com.example.simplemusic.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.example.simplemusic.MainApplication;
import com.example.simplemusic.MainFirstActivity;
import com.example.simplemusic.MusicDetailActivity;
import com.example.simplemusic.adapter.MusicInfoAdapter;
import com.example.simplemusic.bean.MusicFileKindSize;
import com.example.simplemusic.bean.MusicInfo;
import com.example.simplemusic.bean.qqMusicInfo;
import com.example.simplemusic.http.MediaDownloader;
import com.example.simplemusic.httpTask.GetVkeyTask;
import com.example.simplemusic.service.MusicService;
import com.example.simplemusic.util.AlbumLoader;
import com.example.simplemusic.widget.AudioController;
import com.example.simplemusic.widget.VolumeDialog;
import com.example.simplemusic.widget.VolumeDialog.VolumeAdjustListener;
import com.example.simplemusic.httpTask.SearchMusicTask;
import com.example.simplemusic.httpTask.SearchMusicTask.OnGetSearchInfoListener;
import com.example.simplemusic.httpTask.GetLrcTask;
import com.example.simplemusic.httpTask.GetLrcTask.OnGetLrcInfoListener;
import com.example.simplemusic.httpTask.GetVkeyTask.OnGetVkeyListener;
import com.example.simplemusic.widget.RecyclerExtras.OnItemClickListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.simplemusic.R;

public class NetMusicFragment extends Fragment implements OnItemClickListener, OnGetSearchInfoListener, OnGetVkeyListener, OnGetLrcInfoListener {
    private static final String TAG = "NetMusicFragment";
    protected View mView;
    protected Context mContext;
    //控件
    private EditText searchEdt;
    private ListView lv_music;
    private TextView tv_song;
    private AudioController ac_play;
    private AudioManager mAudioMgr;
    private VolumeDialog dialog;
    private MainApplication app;
    private Handler mHandler = new Handler();

    private GetVkeyTask mGetVkeyTask;
    private String mLrcPath = Environment.getExternalStorageDirectory()+"/hxdesign/buff/";
    private String mLrcPathSave = "";
    private String strCookie = "ts_uid=9752082370; RK=xN7oohG1QS; ptcz=5dbfe0567c849ea519a9b539052dd8af8c3d229fb9982edc92543a175ec8381d; pgv_pvid=2113350270; LW_uid=B1X543A0g671v1O4Q6N4d5P5e9; eas_sid=Y1T5l3E0z68171C4h6r727j9U6; pgv_pvi=1894139904; LW_sid=T1T5j3G0m6s1h1l5f1T0A4v4B7; tvfe_boss_uuid=634ea55231ea35f9; 3g_guest_id=-8726999167299211264; g_ut=2; _ga=GA1.2.651937122.1532744535; o_cookie=603350867; pac_uid=1_603350867; pt2gguin=o0603350867; luin=o0603350867; p_luin=o0603350867; uin=o0603350867; ptisp=ctc; pgv_info=ssid=s6690525667; pgv_si=s6287951872; qqmusic_fromtag=66; _qpsvr_localtk=0.08003041217194062; lskey=0001000009c00866b1a1600074e9d2784fc732d8774325fb13730c2e7cd21a9bb866473abc3eab64a86f8127; p_uin=o0603350867; pt4_token=hf96K4TbJwp-SFld3oeMTfTI302Lbs5JUfLnVdFzdwE_; p_skey=HoIVOgHUXXeD8OUXLqgWGrVzoYn89QKhvAVM50PtPQg_; p_lskey=00040000aadd70cda77f6b95574d0d8f51528a89fba10996e6dee067a3372127c51496a3da1f4722757a7a2d; ts_refer=xui.ptlogin2.qq.com/cgi-bin/xlogin; yq_playschange=0; yq_playdata=; player_exist=1; yqq_stat=0; ts_last=y.qq.com/portal/player.html; yplayer_open=1; yq_index=0";
    //记录是否停止了
    private boolean isStop = true;
    private boolean isInit = false;
    MusicInfo mMusic;
    private ArrayList<File> buffFilePath = new ArrayList<File>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mView = inflater.inflate(R.layout.fragment_net_music, container, false);

        return mView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Button button = (Button) getActivity().findViewById(R.id.btn_search);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMusic();
            }
        });
        getActivity().findViewById(R.id.ac_play).setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if(app.mMusic != null) {
                    Intent intent = new Intent(getActivity(), MusicDetailActivity.class);
                    intent.putExtra("music", app.mMusic);
                    startActivity(intent);
                }
            }
        });
        lv_music = (ListView) getActivity().findViewById(R.id.lv_music);
        tv_song = (TextView) getActivity().findViewById(R.id.tv_song);
        searchEdt = (EditText) getActivity().findViewById(R.id.edit_Music_name);
        initList();
        ac_play = (AudioController) getActivity().findViewById(R.id.ac_play);
        mAudioMgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        app = MainApplication.getInstance();
        initController();
        getVkey();
    }

//    @Override
//    public void onStop(){
//        super.onStop();
//        for(int i = 0; i < buffFilePath.size(); i++){
//            buffFilePath.get(i).delete();
//            if(buffFilePath.get(i).exists()){
//                buffFilePath.remove(i);
//            }
//        }
//    }

    private void initList() {

    }

    private void initController() {
        if (app.mSong != null) {
            tv_song.setText(app.mSong+"正在播放");
        } else {
            tv_song.setText("当前暂无歌曲播放");
        }
        mHandler.postDelayed(mRefreshCtrl, 100);
    }

    //刷新进度条
    private Runnable mRefreshCtrl = new Runnable() {
        @Override
        public void run() {
            if(app.mMediaPlayer.isPlaying()){
                ac_play.setCurrentTime(app.mMediaPlayer.getCurrentPosition(), 0);
            }
            if (app.mMediaPlayer.getCurrentPosition() >= 0 && app.mMediaPlayer.getDuration() >= 0 &&app.mMediaPlayer.getCurrentPosition() >= app.mMediaPlayer.getDuration()) {
                ac_play.setCurrentTime(0, 0);
            }
            mHandler.postDelayed(this, 500);
        }
    };

    public void searchMusic(){
        String musicName = String.valueOf(searchEdt.getText());
        musicName= URLEncoder.encode(musicName);
        //搜索音乐
        Long time = System.currentTimeMillis();
        String str = String.valueOf(time);
        SearchMusicTask httpInfoTask = new SearchMusicTask(musicName, str);
        httpInfoTask.setOnSearchMusicListener(this);
        httpInfoTask.execute("???");
    }
    private String mHttpSearchMusicResult = "";
    private ArrayList<qqMusicInfo> mQqMusicInfoList;
    private MusicInfoAdapter mMusicInfoAdapter;
    //获取网络搜索结果
    @Override
    public void onGetHttpInfo(String info){
        mHttpSearchMusicResult = info;
        mQqMusicInfoList = getMusicList();
        //展示搜索结果
        disPlaySearchMusicList();
    }

    //vkey
    private String mVkey = "";
    private Long mVkeyExpire = null;
    private String mLrc = "";
    @Override
    public void onGetHttpVkeyInfo(String info){
        mVkey = info;
        mVkeyExpire = System.currentTimeMillis();
    }

    @Override
    public void onGetLrcInfo(String info){
        mLrc = info;
        if(!mLrc.equals("")){
            //保存歌词
            File file = new File(mLrcPath);
            if(!file.exists()){
                try {
                    FileOutputStream outStream = new FileOutputStream(file, true);
                    outStream.write(mLrc.getBytes("utf-8"));
                    outStream.flush();
                    outStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            file = new File(mLrcPathSave);
            if(!file.exists()){
                try {
                    FileOutputStream outStream = new FileOutputStream(file, true);
                    outStream.write(mLrc.getBytes("utf-8"));
                    outStream.flush();
                    outStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
        //播放歌词
    }

    public ArrayList<qqMusicInfo> getMusicList(){
        ArrayList<qqMusicInfo> qqMusicInfoList = new ArrayList<qqMusicInfo>();
        try {
            JSONObject dataObj  = new JSONObject(mHttpSearchMusicResult);
            JSONArray songs = dataObj.getJSONObject("data").getJSONObject("song").getJSONArray("list");
            for(int i = 0; i < songs.length(); i++){
                JSONObject resultObj = songs.getJSONObject(i);
                qqMusicInfo song = new qqMusicInfo(resultObj);
                song.mIndex = i;
                qqMusicInfoList.add(song);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return qqMusicInfoList;
    }

    public void disPlaySearchMusicList(){
        mMusicInfoAdapter = new MusicInfoAdapter(getActivity(), R.layout.activity_music_info, mQqMusicInfoList);
        mMusicInfoAdapter.setOnItemClickListener(this);
        lv_music.setAdapter(mMusicInfoAdapter);
    }

    public void getVkey(){
        if(mVkeyExpire ==null || mVkey.equals("") || System.currentTimeMillis() - mVkeyExpire >= 60 * 1000 * 60 ){
            String vkey = "";
            mGetVkeyTask = new GetVkeyTask();
            mGetVkeyTask.setOnGetVkeyListener(this);
            mGetVkeyTask.execute("firstVkey");
        }
    }

    public int getDownType(qqMusicInfo song){
        int type = 0;
        //M500 M800 F000
        MusicFileKindSize kinds = song.getMusicFileSize();
        if(kinds.msize_128 != 0) type += 1;
        if(kinds.msize_320 != 0) type += 2;
        if(kinds.msize_flac != 0) type += 4;
        return type;
    }

    public void downLoadBuffManager(){

    }

    //点击后下载普通品质的音乐
    @Override
    public void onItemClick(View view, int position) {
        getVkey();
        qqMusicInfo clickSong = mQqMusicInfoList.get(position);
        //获取vkey
        //http://streamoc.music.tc.qq.com/M500004VBMk71TdUuR.mp3?vkey=BC86BDEEA71D18E8053CDABA0B969B2B459437A83D903115FD4932635B8CAC0BA3D57737307A90B843766EC525337313FCDDE8B4CEFD7B91&guid=1234567890&uin=1008611&fromtag=8
        String req = "";
        //MusicFileKindSize fileNameSize = clickSong.getMusicFileSize();
        //普通品质
        req = "http://streamoc.music.tc.qq.com/M500"+ clickSong.getMusicFileID() + ".mp3?vkey=" +mVkey + "&guid=1234567890&uin=19901215&fromtag=8";


        //创建文件夹
        String path = Environment.getExternalStorageDirectory()+"/hxdesign/buff";
        File file = new File(path);
        if(!file.exists()){
            Boolean flag = file.mkdirs();
        }
        path = path +"/" + clickSong.mMusicName + clickSong.getMusicID() + ".mp3";
        file = new File(path);
        buffFilePath.add(file);
        //缓存MP3文件
        mMusic = new MusicInfo();
        mMusic.setTitle(clickSong.mMusicName);
        mMusic.setArtist(clickSong.getSingerName());
        mMusic.setUrl(path);
        //记录支持下载
        String sid = clickSong.getMusicFileID();
        app.qqsongid = sid;
        app.vkey = mVkey;
        app.downtype = getDownType(clickSong);
        if(file.exists()){
            Intent intent = new Intent(getActivity(), MusicDetailActivity.class);
            intent.putExtra("music", mMusic);
            startActivity(intent);
        }else {
//            Intent intent = new Intent(this,MusicService.class);
//            intent.putExtra("is_play", true);
//            intent.putExtra("music", mMusic);
//            startService(intent);
            initMusicPlayer();
            new DownloadTack().execute(req, path, "mp3");// + clickSong.mMusicName +
        }
        //缓存.lrc文件
        path = Environment.getExternalStorageDirectory()+"/hxdesign/buff/" + clickSong.mMusicName + clickSong.getMusicID() + ".lrc";
        mLrcPath = path;
        mLrcPathSave = Environment.getExternalStorageDirectory()+"/hxdesign/buff/" + clickSong.mMusicName + ".lrc";
        file = new File(path);

        if(file.exists()){

        }else{
            GetLrcTask httpInfoTask = new GetLrcTask(clickSong.getMusicLrcID(), strCookie);
            httpInfoTask.setOnSearchMusicListener(this);
            httpInfoTask.execute("getLrcReq","Cookie");
        }

        //缓存.jpg文件
        path = Environment.getExternalStorageDirectory()+"/hxdesign/buff/" + clickSong.mMusicName + clickSong.getMusicID() + ".jpg";
        //http://47.107.101.237/api?method=getAlbumInfo&albummid=000dcZ9I1nzO62&_=1546395768721
        req = "https://y.gtimg.cn/music/photo_new/T002R300x300M000" + clickSong.getMusicPicID() + ".jpg?max_age=2592000";;
        file = new File(path);
        if(!file.exists()) {
            new DownloadTack().execute(req, path, "jpg");// + clickSong.mMusicName +
        }

    }

    class DownloadTack extends AsyncTask<String, Integer, Object> implements MediaDownloader.OnDownloadListener {
        int lenght; //记录总大小
        String savePath;

        @Override
        protected Object doInBackground(String... params) {
            new MediaDownloader(this).download(params[0], params[1], params[2]);

            return null;
        }

        @Override
        public void onStart(int size, String path) {
            lenght = size;
            this.savePath = path;
        }

        @Override
        public void onDownloading(int currentSize) {
            //更新进度条
            if (currentSize >= 1024 && isStop) {//
                //开始解析
                setDataAndPlay(savePath);
            }
            int currentRate = 100 * currentSize / lenght;
            publishProgress(currentRate);
        }

        @Override
        public void onDownloadFinish(String type){
            if(type.equals("mp3")){
                //下载完后删除
                if(buffFilePath.size() > 1 && buffFilePath.get(0).exists()){
                    buffFilePath.get(0).delete();
                    buffFilePath.remove(0);
                }
            }
            if(type.equals("jpg")){//显示专辑图片

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //mSeek.setSecondaryProgress(values[0]);
            ac_play.setCurrentTime(app.mMediaPlayer.getCurrentPosition(), values[0]);
        }
    }
    //开始解析文件
    private void setDataAndPlay(String path) {
        isStop = false;
        //设置播放的数据源
        try {
            app.mMediaPlayer.setDataSource(path);
            app.mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            isStop = true;
        }

    }

    private void initMusicPlayer(){
        ac_play.setCurrentTime(0, 0);
        if(app.mMediaPlayer.isPlaying()){
            app.mMediaPlayer.stop();
        }
        if(app.mMediaPlayer != null && !isInit){
            isInit = true;
            //设置准备监听
            app.mMediaPlayer.setOnPreparedListener(onPreparedListener);
            //设置错误监听
            app.mMediaPlayer.setOnErrorListener(onErrorListener);
            //设置播放完毕监听
            app.mMediaPlayer.setOnCompletionListener(onCompletionListener);
            app.mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        if(!isStop){
            Intent intent = new Intent(getActivity(),MusicService.class);
            intent.putExtra("is_play", true);
            intent.putExtra("music", mMusic);
            getActivity().startService(intent);
        }
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            isStop = false;
            //3、进入onPrepared状态(准备完毕)，可以去start、pause、seekTo、stop
            //获取总时间
            mp.start();
            //更新播放时间

        }
    };

    //(解码)错误监听
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            mp.stop();
            mp.reset(); //进入idle状态
            isStop = true;
            return true;
        }
    };

    //播放完毕监听
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(!app.mMediaPlayer.isPlaying()){
                app.mMediaPlayer.start();
            }
        }
    };
}