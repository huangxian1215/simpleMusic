package com.example.simplemusic;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import com.example.simplemusic.bean.LrcContent;
import com.example.simplemusic.bean.MusicInfo;
import com.example.simplemusic.bean.dbMusicInfo;
import com.example.simplemusic.bean.qqMusicInfo;
import com.example.simplemusic.database.UserDBHelper;
import com.example.simplemusic.dialog.Mp3DownLoadChooseDialog;
import com.example.simplemusic.http.MediaDownloader;
import com.example.simplemusic.service.MusicService;
import com.example.simplemusic.task.AudioPlayTask;
import com.example.simplemusic.util.LyricsLoader;
import com.example.simplemusic.util.MeasureUtil;
import com.example.simplemusic.util.Utils;
import com.example.simplemusic.widget.AudioController;
import com.example.simplemusic.widget.VolumeDialog;
import com.example.simplemusic.widget.AudioController.OnSeekChangeListener;
import com.example.simplemusic.widget.VolumeDialog.VolumeAdjustListener;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2016/12/4.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MusicDetailActivity extends AppCompatActivity implements OnClickListener,
        AnimatorListener, OnSeekChangeListener, VolumeAdjustListener{
    private static final String TAG = "MusicDetailActivity";
    private TextView tv_title;
    private TextView tv_artist;

    private TextView tv_music;
    private MusicInfo mMusic;
    private AudioController ac_play;
    private LyricsLoader mLoader;
    private ArrayList<LrcContent> mLrcList;
    private AudioManager mAudioMgr;
    private VolumeDialog dialog;
    private MainApplication app;
    private Handler mHandler = new Handler();
    private int mmode = 1;
    private int mnumber = 0;
    private int mlove = 0;
    private SharedPreferences mShared;
    //
    private TextView btn_down;
    private TextView btn_mode;
    private TextView btn_colect;
    private TextView btn_pre;
    private TextView btn_next;


    //消息ID
    private static final int DOWNLOAD_REQUESTCODE = 1;//添加
    private static final int DOWNLOAD_RESULTCODE = 1;
    private static final int DELETHEME_REQUESTCODE = 2;//删除
    private static final int DELETHEME_RESULTSTCODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_artist = (TextView) findViewById(R.id.tv_artist);
        tv_music = (TextView) findViewById(R.id.tv_music);
        btn_colect = findViewById(R.id.music_love);
        btn_down = findViewById(R.id.music_download);
        btn_mode = findViewById(R.id.music_playMode);
        btn_pre = findViewById(R.id.pre_song);
        btn_next = findViewById(R.id.next_song);
        ac_play = (AudioController) findViewById(R.id.ac_play);
        ac_play.setOnSeekChangeListener(this);
        mMusic = getIntent().getParcelableExtra("music");
        mnumber = getIntent().getIntExtra("currentNum", 0);
        app = MainApplication.getInstance();

        //设置播放完毕监听
        app.mMediaPlayer.setOnCompletionListener(onCompletionListener);
        btn_colect.setOnClickListener(this);
        btn_down.setOnClickListener(this);
        btn_mode.setOnClickListener(this);
        btn_pre.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        initLocalStore();
        initMusic();
    }
    public void initLocalStore(){
        mShared = getSharedPreferences("playmode", MODE_PRIVATE);
        Map<String, Object> mapParam = (Map<String, Object>) mShared.getAll();
        for (Map.Entry<String, Object> item_map : mapParam.entrySet()) {
            String key = item_map.getKey();
            Object value = item_map.getValue();
            if(value instanceof Integer && key.equals("playmode")){
                app.mMode = mShared.getInt(key, 01);
                mmode = app.mMode;
            }
        }
        if(app.downtype != 0){
            mmode = 3;
        }
    }

    public void setLocalStore(){
        SharedPreferences.Editor editor = mShared.edit();
        editor.putInt("playmode", mmode);
        editor.commit();
    }

    public void initMusic(){
        setText();
        String title = mMusic.getTitle() + " - " + mMusic.getArtist();
        tv_title.setText(title);
        mCount = 0;
        mPrePos = -1;
        mNextPos = 0;
        tv_music.setText("无歌词");
        //歌词都放song里面
        String path = Environment.getExternalStorageDirectory()+"/hxdesign/buff/"+ mMusic.getTitle();
        mLoader = LyricsLoader.getInstance(path);
        mLrcList = mLoader.getLrcList();
        if(mLrcList.size() == 0){
            path = mMusic.getUrl();
            mLoader = LyricsLoader.getInstance(path);
            mLrcList = mLoader.getLrcList();
        }
        mLineHeight = Math.round(MeasureUtil.getTextHeight("好", tv_music.getTextSize()));
        mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        playMusic();
        app.mMusic = mMusic;
        if(app.downtype == 0){
            btn_down.setVisibility(View.GONE);
            btn_next.setVisibility(View.VISIBLE);
            btn_pre.setVisibility(View.VISIBLE);
            btn_mode.setVisibility(View.VISIBLE);
            btn_colect.setVisibility(View.VISIBLE);
        }else{
            btn_down.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.GONE);
            btn_pre.setVisibility(View.GONE);
            btn_mode.setVisibility(View.GONE);
            btn_colect.setVisibility(View.GONE);
        }
    }

    public void setMusic(int i){
        if(mnumber >= app.mqqMusicInfo.size()) {
            mnumber = 0;
            i = 0;
        }
        if(i >= app.mqqMusicInfo.size()) return;
        qqMusicInfo  song = app.mqqMusicInfo.get(i);
        mMusic.setTitle(song.mMusicName);
        mMusic.setAlbum(song.mMusicAlbum);
        mMusic.setUrl(song.mMusicUrl);
        initMusic();
    }

    public void setText(){
        if(getLove() == 0){
            btn_colect.setText("喜欢?");
        }else {
            btn_colect.setText("喜欢");
        }
        if(app.mMode == 1) btn_mode.setText("顺序");
        if(app.mMode == 2) btn_mode.setText("随机");
        if(app.mMode == 3) btn_mode.setText("单曲");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.music_love:
                markLove();
                break;
            case R.id.music_download:
                openDownloadMusic();
                break;
            case R.id.music_playMode:
                if(mmode == 3){
                    mmode = 0;
                    btn_mode.setText("顺序");
                }
                if(mmode == 1) btn_mode.setText("随机");
                if(mmode == 2) btn_mode.setText("单曲");
                mmode++;
                app.mMode = mmode;
                setLocalStore();
                break;
            case R.id.pre_song:
                playNext(-1);
                break;
            case R.id.next_song:
                playNext(1);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode) {
            case DOWNLOAD_REQUESTCODE:
                if(data != null){
                    if (resultCode == DOWNLOAD_RESULTCODE) {//下载
                        int quality = (int)data.getSerializableExtra("quality");
                        downloadMusic(quality);
                        //保存到数据库
                    }
                }
                break;
        }
    }

    public void playNext(int how){
        if(app.mMediaPlayer.isPlaying()){
            app.mMediaPlayer.stop();
        }
        if(mmode == 2){
            Random rand = new Random();
            int i = rand.nextInt(app.mqqMusicInfo.size());
            if(app.mqqMusicInfo.get(i).mMusicUrl.equals(mMusic.getUrl())){
                new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    app.mMediaPlayer.start();
                }}, 2000);
                return;
            }
            setMusic(i);
        }else{
            mnumber += how;
            if(mnumber < 0) mnumber = 0;
            setMusic(mnumber);
        }
    }

    public int getLove(){
        String condition ="url = '" +mMusic.getUrl()+"'";
        ArrayList<dbMusicInfo> info = UserDBHelper.getmHelper().query_tab(condition);
        if(info.size() > 0){
            mlove = info.get(0).love;
        }
        return mlove;
    }

    public void markLove(){
        //奖当前标记为喜欢
        String condition ="url = '" +mMusic.getUrl()+"'";
        mlove = mlove == 0 ? 1 : 0;
        UserDBHelper.getmHelper().updatelove(condition , mlove);
        if(mlove == 0){
            btn_colect.setText("喜欢?");
        }else {
            btn_colect.setText("喜欢");
        }
    }

    public void openDownloadMusic(){
        //mMusic.getMusicFileSize();
        Intent intent = new Intent(this, Mp3DownLoadChooseDialog.class);
        intent.putExtra("types", app.downtype);// 1 1 1
        startActivityForResult(intent, DOWNLOAD_RESULTCODE);
    }

    public void downloadMusic(int type){
        String stringType = "";
        String vkey = app.vkey;
        String songid = app.qqsongid;
        if(type == 1) stringType = "M500";
        if(type == 2) stringType = "M800";  //.mp3
        if(type == 3) stringType = "F000"; //.flac

        //创建文件夹
        String path = Environment.getExternalStorageDirectory()+"/hxdesign/down";
        File file = new File(path);
        if(!file.exists()){
            Boolean flag = file.mkdirs();
        }
        String tp = ".mp3";
        String prepath = path +"/" + mMusic.getTitle();
        path = prepath;
        if(type == 3){
            path = path+".flac";
            tp = ".flac";
        }else{
            path = path+".mp3";
        }

        String req = "http://streamoc.music.tc.qq.com/"+stringType+ songid + tp +"?vkey=" +vkey + "&guid=1234567890&uin=19901215&fromtag=8";
        file = new File(path);
        if(!file.exists()){
            new DownloadTack().execute(req, path, "mp3");// + clickSong.mMusicName +
        }else{
            Long time = System.currentTimeMillis();
            String str = String.valueOf(time);
            prepath = prepath + str + tp;
            new DownloadTack().execute(req, prepath, "mp3");
        }

    }

    private int frequence = 8000;
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 播放歌曲
    private void playMusic() {
        Log.d(TAG, "song="+mMusic.getTitle());
        if (Utils.getExtendName(mMusic.getUrl()).equals("pcm")) {
            ac_play.setVisibility(View.GONE);
            AudioPlayTask playTask = new AudioPlayTask();
            //playTask.setOnPlayListener(this);
            playTask.execute(mMusic.getUrl(), ""+frequence, ""+channelConfig, ""+audioFormat);
        } else {
            //以下处理歌词
            if (mLoader.getLrcList()!=null && mLrcList.size()>0) {
                mLrcStr = "";
                for (int i=0; i<mLrcList.size(); i++) {
                    LrcContent item = mLrcList.get(i);
                    mLrcStr = mLrcStr + item.getLrcStr() + "\n";
                }
                tv_music.setText(mLrcStr);
                tv_music.setAnimation(AnimationUtils.loadAnimation(this,R.anim.alpha_music));
            }
            //播放音乐
            if (app.mFilePath==null || !app.mFilePath.equals(mMusic.getUrl())) {
                Intent intent = new Intent(this, MusicService.class);
                intent.putExtra("is_play", true);
                intent.putExtra("music", mMusic);
                startService(intent);
                mHandler.postDelayed(mRefreshLrc, 150);
            } else {
                onMusicSeek(0, app.mMediaPlayer.getCurrentPosition());
            }
            mHandler.postDelayed(mRefreshCtrl, 100);
        }
    }

    //刷新进度条
    private Runnable mRefreshCtrl = new Runnable() {
        @Override
        public void run() {
            ac_play.setCurrentTime(app.mMediaPlayer.getCurrentPosition(), 0);
            if (app.mMediaPlayer.getCurrentPosition() >= 0 && app.mMediaPlayer.getDuration() >= 0 && app.mMediaPlayer.getCurrentPosition() >= app.mMediaPlayer.getDuration()) {
                ac_play.setCurrentTime(0, 0);
            }
            mHandler.postDelayed(this, 500);
        }
    };

    private int mCount = 0;
    private float mCurrentHeight = 0;
    private float mLineHeight = 0;
    //计算每行歌词的动画
    private Runnable mRefreshLrc = new Runnable() {
        @Override
        public void run() {
            if (mLoader.getLrcList()==null || mLrcList.size()<=0) {
                return;
            }
            int offset = mLrcList.get(mCount).getLrcTime()
                    - ((mCount==0)?0:mLrcList.get(mCount-1).getLrcTime()) - 50;
            if (offset <= 0) {
                return;
            }
            startAnimation(mCurrentHeight - mLineHeight, offset);
            Log.d(TAG, "mLineHeight="+mLineHeight+",mCurrentHeight="+mCurrentHeight+",getHeight="+tv_music.getHeight());
        }
    };

    private int mPrePos = -1, mNextPos = 0;
    private String mLrcStr;
    private ObjectAnimator animTranY;
    //歌词滚动动画
    public void startAnimation(float aimHeight, int offset) {
        Log.d(TAG, "mCurrentHeight="+mCurrentHeight+", aimHeight="+aimHeight);
        animTranY = ObjectAnimator.ofFloat(tv_music, "translationY", mCurrentHeight, aimHeight);
        animTranY.setDuration(offset);
        animTranY.setRepeatCount(0);
        animTranY.addListener(this);
        animTranY.start();
        mCurrentHeight = aimHeight;
        if (app.mMediaPlayer.isPlaying() != true) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animTranY.pause();
                }
            }, offset+100);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (mCount < mLrcList.size()) {
            mNextPos = mLrcStr.indexOf("\n", mPrePos+1);
            SpannableString spanText = new SpannableString(mLrcStr);
            spanText.setSpan(new ForegroundColorSpan(Color.RED), mPrePos+1,
                    mNextPos>0?mNextPos:mLrcStr.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mCount++;
            tv_music.setText(spanText);
            if (mNextPos > 0 && mNextPos < mLrcStr.length()-1) {
                mPrePos = mLrcStr.indexOf("\n", mNextPos);
                mHandler.postDelayed(mRefreshLrc, 50);
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    //音乐控制条的拖动操作
    @Override
    public void onMusicSeek(int current, int seekto) {
        Log.d(TAG, "current="+current+", seekto="+seekto);
        if (animTranY != null) {
            animTranY.cancel();
        }
        mHandler.removeCallbacks(mRefreshLrc);
        int i;
        for (i=0; i<mLrcList.size(); i++) {
            LrcContent item = mLrcList.get(i);
            if (item.getLrcTime() > seekto) {
                break;
            }
        }
        mCount = i;
        mPrePos = -1;
        mNextPos = 0;
        if (mCount > 0) {
            for (int j = 0; j < mCount; j++) {
                mNextPos = mLrcStr.indexOf("\n", mPrePos + 1);
                mPrePos = mLrcStr.indexOf("\n", mNextPos);
            }
        }
        startAnimation(-mLineHeight*i, 100);
    }

    @Override
    public void onMusicPause() {
        if(animTranY == null) return;
        animTranY.pause();
    }

    @Override
    public void onMusicResume() {
        if(animTranY == null) return;
        animTranY.resume();
    }

    //音量调节对话框
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            showVolumeDialog(AudioManager.ADJUST_RAISE);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            showVolumeDialog(AudioManager.ADJUST_LOWER);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    private void showVolumeDialog(int direction) {
        if (dialog==null || dialog.isShowing()!=true) {
            dialog = new VolumeDialog(this);
            dialog.setVolumeAdjustListener(this);
            dialog.show();
        }
        dialog.adjustVolume(direction, true);
        onVolumeAdjust(mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    @Override
    public void onVolumeAdjust(int volume) {
    }
    //播放完毕监听
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(!app.mMediaPlayer.isPlaying()){
                //顺序播放
                if(mmode == 1){
                    setMusic(++mnumber);
                }
                //随机播放
                if(mmode == 2){
                    Random rand = new Random();
                    int i = rand.nextInt(app.mqqMusicInfo.size());
                    setMusic(i);
                }
                //单曲循环
                if(mmode == 3){
                    app.mMediaPlayer.start();
                }

            }
        }
    };

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

        }

        @Override
        public void onDownloadFinish(String type){

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}

