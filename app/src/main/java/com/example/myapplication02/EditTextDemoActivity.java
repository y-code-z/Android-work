package com.example.myapplication02;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 页面②：EditText 属性演示（实验步骤 3）
 *
 * 布局里的卡片 1~6 全是纯 XML 属性演示，Java 这边不用管；
 * Java 只负责卡片 7 的综合案例：
 *      点按钮 → 读取 EditText 内容 → Toast 弹出显示。
 */
public class EditTextDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 把本 Activity 和布局绑定：R.layout.activity_edit_text_demo 是布局文件的资源编号
        setContentView(R.layout.activity_edit_text_demo);

        // ========== 卡片 7 综合案例 ==========

        // 1. findViewById：拿编号找到输入框和按钮这两个控件对象
        EditText etInput = findViewById(R.id.et_input);
        Button btnShow = findViewById(R.id.btn_show);

        // 2. 给按钮设置点击监听（匿名内部类的 lambda 简写）
        btnShow.setOnClickListener(v -> {
            // 3. getText() 拿到可编辑对象，toString() 转成字符串，trim() 去掉首尾空格
            String content = etInput.getText().toString().trim();

            // 4. 空值保护：TextUtils.isEmpty 能同时判断 null 和 ""（空字符串）
            if (TextUtils.isEmpty(content)) {
                // 输入为空 → 提示"还没输入"，Toast.LENGTH_SHORT 短暂显示约 2 秒
                Toast.makeText(this, "你还没输入内容哦", Toast.LENGTH_SHORT).show();
            } else {
                // 有内容 → Toast 弹出显示（LENGTH_LONG 约 3.5 秒）
                Toast.makeText(this, "你输入的是：" + content, Toast.LENGTH_LONG).show();
            }
        });
    }
}
