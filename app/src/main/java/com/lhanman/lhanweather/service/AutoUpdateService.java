package com.lhanman.lhanweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.lhanman.lhanweather.WeatherActivity;
import com.lhanman.lhanweather.gson.Weather;
import com.lhanman.lhanweather.util.HttpUtil;
import com.lhanman.lhanweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 1 * 60 * 60 * 1000 ;//1小时
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    //更新天气信息
    private void updateWeather()
    {
        SharedPreferences prefs = getSharedPreferences("data",MODE_PRIVATE);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null) {
            //有缓存
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherid;

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=" +
                    "f7622651fd39461cad24730270d638f2";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = getSharedPreferences("data",
                                MODE_PRIVATE).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    //更新背景
    private void updateBingPic()
    {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE)
                        .edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }

}
