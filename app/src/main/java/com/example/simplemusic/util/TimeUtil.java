package com.example.simplemusic.util;

public class TimeUtil {
    public static String getTimeMMss(String str){
        String time = "0:00";
        int count = 0;
        try {
            count = Integer.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(count >0){
            String shi = String.valueOf((int)(Math.floor(count/60)));
            String ge ;
            if(count%60 > 9){
                ge = String.valueOf(count%60);
            }else{
                ge = "0" + String.valueOf(count%60);
            }
            time =  shi + ":" + ge;
        }
        return time;
    }
}
