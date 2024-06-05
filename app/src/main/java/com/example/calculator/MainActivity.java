package com.example.calculator;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView tv_result; //显示结果
    private TextView tv_input;//显示输入
    private String currentInput = "";//用于存储当前输入的字符串

    // 存储历史记录
    private ArrayList<CalculationHistory> calculationHistoryList = new ArrayList<>();

    // 适配器
    HistoryAdapter historyAdapter;


    // 弹出窗口
    private PopupWindow popupWindow;

    //弹出窗口的recyclerView
    RecyclerView popupRecyclerView;


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

        // 模拟数据
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));
        calculationHistoryList.add(new CalculationHistory("1 + 2 = "));

        historyAdapter = new HistoryAdapter(calculationHistoryList);
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
            String postfix = infixToPostfix();
            //后缀表达式求值
            calculatePostfix(postfix);

            //存储计算历史记录
            calculationHistoryList.add(new CalculationHistory(currentInput));
            historyAdapter.notifyDataSetChanged();
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
    private String infixToPostfix() {
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

        return postfix.toString();
    }

    /**
     * 计算后缀表达式
     *
     * @param postfix
     * @return
     */
    private void calculatePostfix(String postfix) {
        // 定义栈存放操作数
        Stack<String> numberStack = new Stack<>();
        String[] tokens = postfix.split(" ");
        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("×") || token.equals("÷") || token.equals("^2")) {
                // 检查栈中是否有足够的操作数，不要出现1 + =这种情况，需要两个操作数但现在只有一个
                if (numberStack.size() < 2 && !token.equals("^2")) {
                    Toast.makeText(this, "操作数 数量不对！", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 遇到操作符，从栈中弹出两个操作数，进行计算，并将结果入栈
                String num2 = null;
                String num1 = null;
                //如果操作符是^2，只弹出栈顶的一个操作数即可
                if (token.equals("^2")) {
                    num2 = numberStack.pop();

                } else {
                    //否则弹出两个操作数
                    num2 = numberStack.pop();
                    num1 = numberStack.pop();
                }

                String result = null;
                switch (token) {
                    case "+":
                        result = String.valueOf(Double.parseDouble(num1) + Double.parseDouble(num2));
                        break;
                    case "-":
                        result = String.valueOf(Double.parseDouble(num1) - Double.parseDouble(num2));
                        break;
                    case "×":
                        result = String.valueOf(Double.parseDouble(num1) * Double.parseDouble(num2));
                        break;
                    case "÷":
                        if (Double.parseDouble(num2) == 0) {
                            Toast.makeText(this, "除数不能为0", Toast.LENGTH_SHORT).show();
                        }
                        result = String.valueOf(Double.parseDouble(num1) / Double.parseDouble(num2));
                        break;
                    case "^2":
                        result = String.valueOf(Double.parseDouble(num2) * Double.parseDouble(num2));
                        break;
                    default:
                        break;
                }
                numberStack.push(result);
            } else {
                numberStack.push(token);
            }
        }

        String result = numberStack.pop();
        // 如果result中包含 .0 则去掉
        if (result.endsWith(".0")) {
            result = result.substring(0, result.length() - 2);
        }
        tv_result.setText(result);
    }

    /**
     * 获取操作符的优先级
     *
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
            // 创建PopupWindow
            createPopupWindow();
            // 显示PopupWindow
            if (!popupWindow.isShowing()) {
                popupWindow.showAtLocation(findViewById(R.id.main), Gravity.NO_GRAVITY, 0, 0);
            }
            return true;
        }
        return true;
    }

    /**
     * 创建PopupWindow
     *
     * @return
     */
    private void createPopupWindow() {
        //弹出窗口初始化
        // 初始化PopupWindow
        View popupView = getLayoutInflater().inflate(R.layout.popup_recycler_view, null);
        // 例如，设置PopupWindow的宽度为屏幕宽度的80%，高度为屏幕高度的60%
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 0.7); // 屏幕宽度的70%
        int height = (int) (metrics.heightPixels * 0.9); // 屏幕高度的60%

        popupWindow = new PopupWindow(popupView, width, height);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //设置弹出窗口的适配器
        popupRecyclerView = popupView.findViewById(R.id.popupRecyclerView);
        popupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        popupRecyclerView.setAdapter(historyAdapter);
        //处理popupwindow消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 设置动画资源
        int animStyle = android.R.style.Animation_Dialog; // 系统提供的动画样式
        popupWindow.setAnimationStyle(animStyle);
    }


}