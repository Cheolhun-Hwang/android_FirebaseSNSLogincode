package com.hch.hooney.firebasesnslogincode.Apps;

import android.app.Application;

import com.hch.hooney.firebasesnslogincode.kakao.KakaoSDKAdapter;
import com.kakao.auth.KakaoSDK;

public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getGlobalApplicationContext() {
        if (instance == null) {
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        }

        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Kakao Sdk 초기화
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
}
