# 通过 Webview 嵌入网页

新建项目选择 Android，Phone and Tablet，Empty Views Activity

在 android 视图下修改文件

```xml
<!-- res/layout/activity_main.xml -->

<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">
    
    <!-- 添加 WebView -->
    <WebView
            android:id="@+id/webview"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

```kotlin
// java/.../.../.../MainActivity.kt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView = findViewById<WebView>(R.id.webview)
        // 允许 js
        webView.settings.javaScriptEnabled = true
        // 允许放缩，未测试
        webView.settings.builtInZoomControls = true
        // 允许存储，未测试
        webView.settings.domStorageEnabled = true

        // 加载网页url
        webView.loadUrl("http://fellowship.fuhualiang.club");
    }
}
```


```xml
<!-- manifests/AndroidManifest.xml -->

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          xmlns:tools="http://schemas.android.com/tools">

    <!-- 允许联网 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- 允许读文件，没测试过 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" tools:ignore="ScopedStorage"/>

    <!-- android:usesCleartextTraffic="true" 允许使用http -->
    <application
            android:usesCleartextTraffic="true"
            android:allowBackup="true"
            android:dataExtractionRules="@xml/data_extraction_rules"
            android:fullBackupContent="@xml/backup_rules"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:supportsRtl="true"
            android:theme="@style/Theme.MyApplication"
            tools:targetApi="31">
        <activity
                android:name=".MainActivity"
                android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>

</manifest>
```
