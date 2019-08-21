package com.hch.hooney.firebasesnslogincode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

public class KakaotalkActivity extends AppCompatActivity
implements View.OnClickListener{
    private static String TAG = KakaotalkActivity.class.getSimpleName();
    private FirebaseAuth mAuth;

    private LoginButton btn_kakao_login;
    private SessionCallback callback;

    private TextView mStatusTextView;
    private TextView mDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakaotalk);

//        Log.i(TAG, new ProjectHash().catchingHash(getApplicationContext()));
        initView();
        initFirebase();
        initKakao();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void initView() {
        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);

        btn_kakao_login = (LoginButton) findViewById(R.id.btn_kakao_login);
        findViewById(R.id.buttonKakaoSignout).setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Session.getCurrentSession().removeCallback(callback);
    }

    private void initKakao() {
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d(TAG, "Session Calling...");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonKakaoSignout) {
            signOut();
        }
    }

    public void signOut() {
        mAuth.signOut();
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Log.d(TAG, "Log out Complete");
                updateUI(null);
            }
        });
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            Log.d(TAG, "Session Opened...");
            RequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Log.e("kakao session fail.", exception.toString());
            }
        }

        private void RequestMe() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    if(errorResult != null) {
                        Log.e("kakao login failure.", errorResult.toString());
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    if(errorResult != null) {
                        Log.e("kakao sesseion closed", errorResult.toString());
                    }
                }

                @Override
                public void onSuccess(MeV2Response userProfile) {
                    if(userProfile == null) {
                        Log.e("kakao login unsuccess", "result is null.");
                        return;
                    }

                    Log.e("SessionCallback :: ", "onSuccess");
                    String nickname = userProfile.getNickname();
                    String profileImagePath = userProfile.getProfileImagePath();
                    String thumnailPath = userProfile.getThumbnailImagePath();

                    Log.e("Profile : ", nickname + "");
                    Log.e("Profile : ", profileImagePath  + "");
                    Log.e("Profile : ", thumnailPath + "");

                    updateUI(userProfile);

                }
            });
        }
    }

    private void updateUI(MeV2Response user) {
        if (user != null) {
            mStatusTextView.setText(getString(R.string.facebook_status_fmt, user.getNickname()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, (user.getId()+"")));

            findViewById(R.id.btn_kakao_login).setVisibility(View.GONE);
            findViewById(R.id.buttonKakaoSignout).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.btn_kakao_login).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonKakaoSignout).setVisibility(View.GONE);
        }
    }
}
