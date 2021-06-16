package com.qljm.swh.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;


public class Utils {

    // 防止多次点击造成的页面一直返回
    // 两次点击按钮接口之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isFullScreen(Activity activity) {
        return (WindowManager.LayoutParams.FLAG_FULLSCREEN & activity.getWindow().getAttributes().flags)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static boolean isTranslucentStatusBar(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & activity.getWindow().getAttributes().flags)
                        == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
    }

    public static float getStatusBarHeight(Context context) {
        final int heightResId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return heightResId > 0 ? context.getResources().getDimensionPixelSize(heightResId) : 0;
    }

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    public static boolean checkPackage(String packageName,Context context)
    {
        if (packageName == null || "".equals(packageName))
            return false;
        try
        {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager
                    .GET_UNINSTALLED_PACKAGES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    /**
     * TODO 获取单个文件的MD5值！
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

}
