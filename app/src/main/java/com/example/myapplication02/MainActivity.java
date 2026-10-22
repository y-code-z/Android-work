package com.example.myapplication02;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ===== 首页入口菜单：三个按钮分别跳转到三个演示页 =====
        // Intent 跳转的固定套路：
        //     new Intent(从哪来 this, 到哪去 XxxActivity.class)  →  startActivity(...)
        // 其中 this 指当前 MainActivity（"从哪来"），第二个参数是目标页面类（"到哪去"）。
        // 注意：被跳转的目标 Activity 必须在 AndroidManifest.xml 里注册，否则一点就闪退。

        // 按钮①：跳转到 TextView 演示页
        // 从哪来：this（当前 MainActivity）
        // 到哪去：TextViewDemoActivity.class
        Button btnOpenTextView = findViewById(R.id.btn_open_textview);
        btnOpenTextView.setOnClickListener(v ->
                startActivity(new Intent(this, TextViewDemoActivity.class)));

        // 按钮②：跳转到 EditText 演示页
        // 从哪来：this（当前 MainActivity）
        // 到哪去：EditTextDemoActivity.class
        Button btnOpenEditText = findViewById(R.id.btn_open_edittext);
        btnOpenEditText.setOnClickListener(v ->
                startActivity(new Intent(this, EditTextDemoActivity.class)));

        // 按钮③：跳转到 动态编辑室
        // 从哪来：this（当前 MainActivity）
        // 到哪去：EditTextEditorActivity.class
        Button btnOpenEditor = findViewById(R.id.btn_open_editor);
        btnOpenEditor.setOnClickListener(v ->
                startActivity(new Intent(this, EditTextEditorActivity.class)));
    }
}
