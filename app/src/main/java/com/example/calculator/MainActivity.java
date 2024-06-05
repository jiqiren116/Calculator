package com.example.calculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView tv_result; //显示结果
    private TextView tv_input;//显示输入
    private String currentInput = "";//用于存储当前输入的字符串
//    private Character currentOperator = '\0';//存储当前操作符

    @SuppressLint("MissingInflatedId")
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
        findViewById(R.id.btn_square).setOnClickListener(this);

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
        if (id == R.id.btn_0 || id == R.id.btn_1 || id == R.id.btn_2 || id == R.id.btn_3 || id == R.id.btn_4 || id == R.id.btn_5 || id == R.id.btn_6 || id == R.id.btn_7 || id == R.id.btn_8 || id == R.id.btn_9) {
            inputOperation(v);
        } else if (id == R.id.btn_add || id == R.id.btn_sub
                || id == R.id.btn_multiply || id == R.id.btn_div || id == R.id.btn_square) {
            if (operarorIsLegal(v)) {
                inputOperation(v);
            }
        } else if (id == R.id.btn_equal) {
            //中缀表达式转后缀表达式
            infixToPostfix();
            //后缀表达式求值
        } else if (id == R.id.btn_delete) {
            if (currentInput.length() > 0) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                tv_input.setText(currentInput);
            }
        } else if (id == R.id.btn_clear) {
            currentInput = "";
            tv_input.setText(currentInput);
        }
    }

    /**
     * 中缀表达式转后缀表达式
     */
    private void infixToPostfix() {
        String[] tokens = currentInput.split(" ");
        //存放后缀表达式
        StringBuilder postfix = new StringBuilder();
        //定义栈存放操作符
        Stack<String> operatorStack = new Stack<>();


        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("×") || token.equals("÷") || token.equals("^2")) {
                // 遇到操作符，将操作符入栈
                //若栈空直接入栈
                if (operatorStack.isEmpty()) {
                    operatorStack.push(token);
                } else {
                    //栈不为空，比较优先级
                    String topOperator = operatorStack.peek();
                    if (getPriority(token) > getPriority(topOperator)) {
                        //当前操作符优先级大于栈顶操作符优先级，直接入栈
                        operatorStack.push(token);
                    } else {
                        //当前操作符优先级小于等于栈顶操作符优先级，将栈顶操作符弹出，输出到后缀表达式中，再将当前操作符入栈
                        while (!operatorStack.isEmpty() && getPriority(token) <= getPriority(operatorStack.peek())) {
                            postfix.append(operatorStack.pop()).append(" ");
                        }
                        operatorStack.push(token);
                    }
                }
            } else {
                // 遇到数字，将数字输出
                postfix.append(token).append(" ");
            }
        }
        while (!operatorStack.isEmpty()) {
            postfix.append(operatorStack.pop()).append(" ");
        }
    }

    /**
     * 获取操作符的优先级
     * @param token
     * @return
     */
    private int getPriority(String token) {
        switch (token) {
            case "+":
            case "-":
                return 1;
            case "×":
            case "÷":
                return 2;
            case "^2":
                return 3;
            default:
                return 0;
        }
    }

    /**
     * 判断当前输入的运算符是否合法
     *
     * @param v
     */
    private boolean operarorIsLegal(View v) {
        String op = ((Button) v).getText().toString();
        // 如果当前输入的字符串为空，则不允许输入运算符
        if (currentInput.length() == 0) {
            Toast.makeText(this, "计算内容为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 判断前一个字符是不是操作符
        String[] splitCurrentInput = currentInput.split(" ");
        if (splitCurrentInput.length > 1) {
            String s1 = splitCurrentInput[splitCurrentInput.length - 1];
            if (s1.equals("+") || s1.equals("-") || s1.equals("×") || s1.equals("÷")) {
                Toast.makeText(this, "字符不合法！", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        // 因为每个操作符前后都有空格，像2 + 3 - 5，判断前两个位置是不是操作符，不可以输入连续的操作符
//        char preChar = currentInput.charAt(currentInput.length() - 2);
//        if (preChar == '+' || preChar == '-' || preChar == '×' || preChar == '÷') {
//            Toast.makeText(this, "字符不合法！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    /**
     * 执行数字和操作符的输入
     *
     * @param view
     */
    private void inputOperation(View view) {
        String numberOrOperator = ((Button) view).getText().toString();
        //对于x^2特殊处理，只保留^2
        if (numberOrOperator.equals("x^2")) {
            numberOrOperator = "^2";
        }
        //每当输入运算符时，保存前后要加空格，方便后面取值
        if (numberOrOperator.equals("+") || numberOrOperator.equals("-") || numberOrOperator.equals("×") || numberOrOperator.equals("÷") || numberOrOperator.equals("^2")) {
            currentInput += " " + numberOrOperator + " ";
        } else {
            currentInput += numberOrOperator;
        }
//        currentInput += numberOrOperator;
        tv_input.setText(currentInput);
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