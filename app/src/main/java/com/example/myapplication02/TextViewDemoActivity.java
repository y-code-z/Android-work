package com.example.myapplication02;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 页面①：TextView 属性演示页
 * 本页只负责"把布局显示出来"——所有属性效果都写在 activity_text_view_demo.xml 里，
 * 这就是课本上说的"XML 静态写法"：不改 Java，只改 XML 就能看到不同效果。
 */
public class TextViewDemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 把本 Activity 和布局文件绑定起来：
        // R.layout.activity_text_view_demo 是布局文件 activity_text_view_demo.xml 的资源编号
        setContentView(R.layout.activity_text_view_demo);
    }
}
