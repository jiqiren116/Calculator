package com.example.calculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_result; //显示结果
    private TextView tv_input;//显示输入
    private String currentInput = "";//用于存储当前输入的字符串
    private Character currentOperator = '\0';//存储当前操作符

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_0).setOnClickListener(this); //按钮0设置监听器
        findViewById(R.id.btn_1).setOnClickListener(this); //按钮1设置监听器
        findViewById(R.id.btn_2).setOnClickListener(this); //按钮2设置监听器
        findViewById(R.id.btn_3).setOnClickListener(this);//按钮3设置监听器
        findViewById(R.id.btn_4).setOnClickListener(this);//按钮4设置监听器
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_sub).setOnClickListener(this);
        findViewById(R.id.btn_multiply).setOnClickListener(this);
        findViewById(R.id.btn_div).setOnClickListener(this);
        findViewById(R.id.btn_equal).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_point).setOnClickListener(this);

        tv_result = findViewById(R.id.tv_result);
        tv_input = findViewById(R.id.tv_input);

    }

    /**
     * 处理按钮点击事件
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 处理数字按钮点击事件
        if (id == R.id.btn_0 || id == R.id.btn_1 || id == R.id.btn_2 || id == R.id.btn_3 || id == R.id.btn_4 || id == R.id.btn_5 || id == R.id.btn_6 || id == R.id.btn_7 || id == R.id.btn_8 || id == R.id.btn_9){
            String number = ((Button) v).getText().toString();
            currentInput += number;
            tv_input.setText(currentInput);
        } else if ( id == R.id.btn_add || id == R.id.btn_sub || id == R.id.btn_multiply || id == R.id.btn_div) {

        }
    }





    /**
     * 添加OptionMenu
     *
     * @param menu The options menu in which you place your items.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    /**
     * 处理选项菜单Optionmenu点击事件
     *
     * @param item The menu item that was selected.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.item_history) {
            Toast.makeText(this, "你点击了历史记录", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}