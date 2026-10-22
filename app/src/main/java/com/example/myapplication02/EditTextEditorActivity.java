package com.example.myapplication02;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 页面③：动态编辑室（实验步骤 4）
 *
 * 这一页演示"用 Java 在运行时改控件属性"：
 *   XML 里已经写好的属性，Java 里都能用 setXxx() 再改一遍，
 *   而且改完立刻生效——这就是"动态编辑"。
 *
 * 三个关键知识点（写报告/答辩时重点说这三条）：
 *   1. dp → px：代码里所有尺寸参数的单位都是像素(px)，我们习惯用 dp，必须转换
 *   2. 改 margin 必须走 LayoutParams：EditText 自己没有 setMargin 方法
 *   3. setLines 与 setMaxLines 会互相影响：先 setLines 再 setMaxLines 才是"至少 n 行、最多 m 行"
 */
public class EditTextEditorActivity extends AppCompatActivity {

    // ============ 默认值常量（"全部还原"时要用） ============
    private static final int DEFAULT_SIZE = 16;        // 默认字号 sp
    private static final int DEFAULT_LINES = 2;        // 默认行数
    private static final int DEFAULT_MAX_LINES = 10;   // 默认最大行数
    private static final int DEFAULT_SPACING = 4;      // 默认行距 dp
    private static final int DEFAULT_MARGIN = 8;       // 默认外边距 dp
    private static final int DEFAULT_PADDING = 8;      // 默认内边距 dp
    private static final String DEFAULT_TEXT = "Android 动态属性演示：拖动下面的滑块，这个框会实时变化。";
    private static final String DEFAULT_HINT = "我是被改造的输入框";

    // ============ 控件对象引用 ============
    private EditText etTarget;          // 被改造的预览输入框（上半屏）
    private EditText etHintInput;       // 输入新 hint 的框（卡片9）

    // 六个滑块
    private SeekBar sbSize, sbLines, sbMaxLines, sbSpacing, sbPadding, sbMargin;
    // 六个滑块旁边的数值文字
    private TextView tvSize, tvLines, tvMaxLines, tvSpacing, tvPadding, tvMargin;

    // 四个下拉框
    private Spinner spColor, spBg, spGravity, spInputType;

    // 四个复选框
    private CheckBox cbBold, cbItalic, cbAllCaps, cbEditable;

    // 当前选中的输入类型下标（"禁止编辑"会临时改输入类型为 null，要记住原来选的）
    private int currentInputTypeIndex = 0;

    // 文字颜色数组，和 arrays.xml 里的 color_names 顺序一一对应
    private final int[] colors = {
            0xFF000000, // 黑色
            0xFFE53935, // 红色
            0xFF3DDC84, // 安卓绿
            0xFF1E88E5  // 蓝色
    };

    // 背景颜色数组，和 arrays.xml 里的 bg_color_names 顺序一一对应
    private final int[] bgColors = {
            0xFFFFFFFF, // 白色
            0xFFF9C4,   // 浅黄
            0xE8F5E9,   // 浅绿
            0xE3F2FD,   // 浅蓝
            0xECEFF1    // 浅灰
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 把布局和这个 Activity 关联起来（setContentView 是 Java 与 XML 的桥梁）
        setContentView(R.layout.activity_edit_text_editor);

        // ============ 1. 用 findViewById 把 XML 里的控件一一"找出来" ============
        etTarget = findViewById(R.id.et_target);              // 预览输入框
        etHintInput = findViewById(R.id.et_hint_input);       // 输入 hint 的框

        sbSize = findViewById(R.id.sb_size);                  // 字号滑块
        sbLines = findViewById(R.id.sb_lines);                // 行数滑块
        sbMaxLines = findViewById(R.id.sb_maxlines);          // 最大行数滑块
        sbSpacing = findViewById(R.id.sb_spacing);            // 行距滑块
        sbPadding = findViewById(R.id.sb_padding);            // 内边距滑块
        sbMargin = findViewById(R.id.sb_margin);              // 外边距滑块

        tvSize = findViewById(R.id.tv_size);                  // 字号数值文字
        tvLines = findViewById(R.id.tv_lines);                // 行数数值文字
        tvMaxLines = findViewById(R.id.tv_maxlines);          // 最大行数数值文字
        tvSpacing = findViewById(R.id.tv_spacing);            // 行距数值文字
        tvPadding = findViewById(R.id.tv_padding);            // 内边距数值文字
        tvMargin = findViewById(R.id.tv_margin);              // 外边距数值文字

        spColor = findViewById(R.id.sp_color);                // 文字颜色下拉
        spBg = findViewById(R.id.sp_bg);                     // 背景颜色下拉
        spGravity = findViewById(R.id.sp_gravity);            // 对齐方式下拉
        spInputType = findViewById(R.id.sp_input_type);        // 输入类型下拉

        cbBold = findViewById(R.id.cb_bold);                 // 加粗复选框
        cbItalic = findViewById(R.id.cb_italic);             // 倾斜复选框
        cbAllCaps = findViewById(R.id.cb_allcaps);           // 全大写复选框
        cbEditable = findViewById(R.id.cb_editable);          // 可编辑复选框

        // ============ 2. 给六个滑块设置监听 ============
        // 字号：realtime=true，拖动过程中就实时改 setTextSize（题目要求"实时改"）
        setupSeekBar(sbSize, tvSize, "字号：", "sp", true, this::applyTextSize);
        // 行数/最大行数/行距/内边距/外边距：realtime=false，松手才统一应用，避免拖动时频繁重绘卡顿
        setupSeekBar(sbLines, tvLines, "行数(lines)：", "", false, this::applyLines);
        setupSeekBar(sbMaxLines, tvMaxLines, "最大行数(maxLines)：", "", false, this::applyMaxLines);
        setupSeekBar(sbSpacing, tvSpacing, "行距：", "dp", false, this::applyLineSpacing);
        setupSeekBar(sbPadding, tvPadding, "内边距(padding)：", "dp", false, this::applyPadding);
        setupSeekBar(sbMargin, tvMargin, "外边距(margin)：", "dp", false, this::applyMargin);

        // ============ 3. 给四个下拉框设置选中监听 ============
        // 文字颜色
        spColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                etTarget.setTextColor(colors[position]);   // setTextColor 改文字颜色
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 背景颜色
        spBg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                etTarget.setBackgroundColor(bgColors[position]);   // setBackgroundColor 改背景颜色
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 对齐方式：0=左 1=中 2=右
        spGravity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position == 0) {
                    etTarget.setGravity(Gravity.START);     // setGravity 左对齐
                } else if (position == 1) {
                    etTarget.setGravity(Gravity.CENTER);    // 居中对齐
                } else {
                    etTarget.setGravity(Gravity.END);       // 右对齐
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 输入类型：0普通 1密码 2电话 3数字
        spInputType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                currentInputTypeIndex = position;
                applyInputType();   // 改输入类型（普通/密码/电话/数字）
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // ============ 4. 给四个复选框设置监听 ============
        cbBold.setOnCheckedChangeListener((buttonView, isChecked) -> applyTextStyle());      // 加粗
        cbItalic.setOnCheckedChangeListener((buttonView, isChecked) -> applyTextStyle());    // 倾斜
        cbAllCaps.setOnCheckedChangeListener((buttonView, isChecked) -> etTarget.setAllCaps(isChecked)); // 全大写
        cbEditable.setOnCheckedChangeListener((buttonView, isChecked) -> applyEditable());    // 是否可编辑

        // ============ 5. 动态设置 hint：点按钮后把输入框文字变成预览框提示 ============
        Button btnSetHint = findViewById(R.id.btn_set_hint);
        btnSetHint.setOnClickListener(v -> {
            String newHint = etHintInput.getText().toString().trim();  // 取出输入框内容
            if (newHint.isEmpty()) {
                etTarget.setHint(DEFAULT_HINT);                       // 空内容 → 恢复默认 hint
                Toast.makeText(this, "已恢复默认提示文字", Toast.LENGTH_SHORT).show();
            } else {
                etTarget.setHint(newHint);                            // setHint 改提示文字
                Toast.makeText(this, "提示文字已更新", Toast.LENGTH_SHORT).show();
            }
        });

        // ============ 6. 三个功能按钮 ============
        Button btnClear = findViewById(R.id.btn_clear);
        Button btnRestoreText = findViewById(R.id.btn_restore_text);
        Button btnResetAll = findViewById(R.id.btn_reset_all);

        btnClear.setOnClickListener(v -> etTarget.setText(""));                       // 清空内容
        btnRestoreText.setOnClickListener(v -> etTarget.setText(DEFAULT_TEXT));       // 恢复默认文字
        btnResetAll.setOnClickListener(v -> resetAll());                              // 全部还原

        // ============ 7. 打开页面先按默认值应用一次，保证界面与控件状态一致 ============
        applyAll();
    }

    /**
     * 滑块通用监听：
     *   realtime=true（只有字号用）→ 拖动中每动一下就改预览框，做到"实时"；
     *   realtime=false        → 拖动中只更新旁边数字，松手(onStopTrackingTouch)才统一 applyAll，避免卡顿。
     * apply 是具体改某个属性的方法（如 applyTextSize）。
     */
    private void setupSeekBar(SeekBar seekBar, TextView label, String prefix, String unit, boolean realtime, Runnable apply) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                label.setText(prefix + progress + unit);          // 拖动中：只更新数值文字
                if (realtime && fromUser) {
                    apply.run();                                  // 实时模式：立刻改预览框
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!realtime) {
                    apply.run();                                  // 非实时：松手才改
                }
            }
        });
    }

    /** 字号：setTextSize 要显式指定单位，COMPLEX_UNIT_SP 表示传进去的是 sp */
    private void applyTextSize() {
        int size = sbSize.getProgress();
        if (size < 10) size = DEFAULT_SIZE;   // 保底：低于 10 就按 16 处理（防老版本 min 属性失效）
        etTarget.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /** 行数 lines：setLines 把 EditText 设为恰好显示 n 行（内部同时设 min 和 max 都为 n） */
    private void applyLines() {
        etTarget.setLines(sbLines.getProgress());
    }

    /** 最大行数 maxLines：文字超过 n 行后开始滚动；先 setLines 再 setMaxLines 才会变成"至少 n、最多 m" */
    private void applyMaxLines() {
        etTarget.setMaxLines(sbMaxLines.getProgress());
    }

    /** 行距：setLineSpacing(额外间距px, 行高倍数)，第二个参数 1 表示不放大行高 */
    private void applyLineSpacing() {
        etTarget.setLineSpacing(dp2px(sbSpacing.getProgress()), 1f);
    }

    /**
     * 外边距：控件自己没有 setMargin()，必须改它的"布局参数" LayoutParams。
     * 因为预览框的父容器是 LinearLayout，所以要用 LinearLayout.LayoutParams。
     */
    private void applyMargin() {
        int m = dp2px(sbMargin.getProgress());
        ViewGroup.LayoutParams lp = etTarget.getLayoutParams();
        if (lp instanceof LinearLayout.LayoutParams) {
            ((LinearLayout.LayoutParams) lp).setMargins(m, m, m, m);
            etTarget.setLayoutParams(lp);   // 改完必须重新设回去才生效
        }
    }

    /** 内边距：直接 setPadding，注意单位是像素，所以要先 dp2px */
    private void applyPadding() {
        int p = dp2px(sbPadding.getProgress());
        etTarget.setPadding(p, p, p, p);
    }

    /** 字体样式：加粗和倾斜可以叠加，用位或 | 组合后 setTypeface */
    private void applyTextStyle() {
        int style = Typeface.NORMAL;
        if (cbBold.isChecked()) style |= Typeface.BOLD;       // 加粗
        if (cbItalic.isChecked()) style |= Typeface.ITALIC;   // 倾斜
        etTarget.setTypeface(Typeface.defaultFromStyle(style), style);
    }

    /** 输入类型：0普通 1密码 2电话 3数字 */
    private void applyInputType() {
        if (!cbEditable.isChecked()) return;   // 禁止编辑时不动输入类型
        switch (currentInputTypeIndex) {
            case 1: // 密码
                etTarget.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case 2: // 电话
                etTarget.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case 3: // 数字
                etTarget.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            default: // 普通文本
                // 必须加 TYPE_TEXT_FLAG_MULTI_LINE：单加 TYPE_CLASS_TEXT 会把输入框变成单行（不换行），
                // 导致行数/最大行数滑块看起来"没反应"
                etTarget.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;
        }
    }

    /** 是否允许编辑：用 focusable + cursorVisible + 输入类型一起控制 */
    private void applyEditable() {
        boolean editable = cbEditable.isChecked();
        etTarget.setFocusable(editable);             // setFocusable 控制能否获得焦点
        etTarget.setFocusableInTouchMode(editable);  // 触摸模式能否获得焦点
        etTarget.setCursorVisible(editable);         // setCursorVisible 控制光标是否可见
        if (editable) {
            applyInputType();                        // 恢复原来的输入类型（键盘会重新弹出）
        } else {
            etTarget.setInputType(InputType.TYPE_NULL);  // setInputType(TYPE_NULL) 不弹键盘，等于"只读"
        }
    }

    /** 一次性应用所有属性（滑块松手、打开页面时都调它） */
    private void applyAll() {
        applyTextSize();
        applyLines();
        applyMaxLines();    // 先 lines 再 maxLines，顺序不能反
        applyLineSpacing();
        applyMargin();
        applyPadding();
        etTarget.setTextColor(colors[spColor.getSelectedItemPosition()]);       // 文字颜色
        etTarget.setBackgroundColor(bgColors[spBg.getSelectedItemPosition()]);  // 背景颜色
        applyTextStyle();
        etTarget.setAllCaps(cbAllCaps.isChecked());       // 全大写
        applyEditable();
    }

    /** 全部还原：滑块、下拉、勾选全部复位，再重新应用 */
    private void resetAll() {
        sbSize.setProgress(DEFAULT_SIZE);
        sbLines.setProgress(DEFAULT_LINES);
        sbMaxLines.setProgress(DEFAULT_MAX_LINES);
        sbSpacing.setProgress(DEFAULT_SPACING);
        sbMargin.setProgress(DEFAULT_MARGIN);
        sbPadding.setProgress(DEFAULT_PADDING);

        spColor.setSelection(0);     // 文字颜色：黑色
        spBg.setSelection(0);        // 背景颜色：白色
        spGravity.setSelection(0);   // 对齐：左
        spInputType.setSelection(0); // 输入类型：普通

        cbBold.setChecked(false);
        cbItalic.setChecked(false);
        cbAllCaps.setChecked(false);
        cbEditable.setChecked(true);

        etHintInput.setText("");
        etTarget.setHint(DEFAULT_HINT);
        etTarget.setText(DEFAULT_TEXT);
        etTarget.setGravity(Gravity.START);

        applyAll();
    }

    /**
     * dp 转 px：代码里凡是设置尺寸的参数，单位都是像素；
     * 而我们在界面上习惯说 dp，所以必须换算，否则在不同密度屏幕上大小会差很多。
     */
    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
