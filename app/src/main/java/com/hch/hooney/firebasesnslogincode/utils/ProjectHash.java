package com.hch.hooney.firebasesnslogincode.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProjectHash {
    public String catchingHash(Context context) {
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(
                    "com.hch.hooney.firebasesnslogincode",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e("Hash Error...", e.getMessage());
        }
        return null;
    }
}
