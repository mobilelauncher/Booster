package com.yourpackage.optimizer;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.StatFs;
import android.os.Environment;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        setContentView(web);

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new OptimizerInterface(this), "AndroidOptimizer");

        web.loadUrl("file:///android_asset/index.html");
    }

    public class OptimizerInterface {
        Context mContext;

        OptimizerInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String getRAM() {
            ActivityManager actManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long totalMemory = memInfo.totalMem / (1024 * 1024);
            return totalMemory + " MB RAM";
        }

        @JavascriptInterface
        public String getStorage() {
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            long bytesAvailable = (long) stat.getBlockSizeLong() * (long) stat.getBlockCountLong();
            long megAvailable = bytesAvailable / (1024 * 1024);
            return megAvailable + " MB Storage";
        }

        @JavascriptInterface
        public void cleanCache() {
            File cache = mContext.getCacheDir();
            deleteDir(cache);
        }

        private boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) return false;
                }
                return dir.delete();
            } else if (dir != null && dir.isFile()) {
                return dir.delete();
            } else {
                return false;
            }
        }
    }
}
