package com.example.calculator;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private TextView tv_result; //显示结果
    private TextView tv_input;//显示输入
    private String currentInput = "";//用于存储当前输入的字符串

    private ArrayList<CalculationHistory> calculationHistoryList = new ArrayList<>();// 存储历史记录

    HistoryAdapter historyAdapter;// 适配器

    private PopupWindow popupWindow;// 弹出窗口

    RecyclerView popupRecyclerView;//弹出窗口的recyclerView

    private MyDatabaseHelper dbHelper;// 自己的数据库管理类

    private ProgressDialog progressDialog;// 添加一个ProgressDialog成员变量，用于在计算时显示进度,方便用户感知计算过程

    private final int loadingTime = 1500; // 模拟计算过程的加载时间

    //uiHandler在主线程中创建，所以自动绑定主线程
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // 显示计算过程中的进度
                    showProgressDialog();
                    break;
                case 1: //显示正常运算的结果
                    String result = (String) msg.obj;
                    tv_result.setText(result);
                    // 计算完成后，隐藏ProgressDialog
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                case 2://显示缺少操作数的错误信息
                case 3://显示除数为0的错误信息
                    String error1 = (String) msg.obj;
                    useVibrator();//调用机器马达震动提醒
                    Toast.makeText(MainActivity.this, error1, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


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
        findViewById(R.id.btn_point).setOnClickListener(this);

        tv_result = findViewById(R.id.tv_result);
        tv_input = findViewById(R.id.tv_input);

        historyAdapter = new HistoryAdapter(calculationHistoryList);

        // 初始化数据库管理类
        dbHelper = new MyDatabaseHelper(this, "HistoryStore.db", null, 1);
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
        } else if (id == R.id.btn_point) {
            // 判断小数点输入是否合法
            boolean isLegal = pointIsLegal();
            if (isLegal) {
                inputOperation(v);
            } else {
                useVibrator();//调用机器马达震动提醒
                Toast.makeText(this, "小数点输入不合法", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btn_add || id == R.id.btn_sub
                || id == R.id.btn_multiply || id == R.id.btn_div) {
            if (operarorIsLegal(v)) {
                inputOperation(v);
            }
        } else if (id == R.id.btn_equal) {
            // 定义一个子线程函数，来进行耗时逻辑运算，然后利用Handler的sendMessage方法将结果发送给主线程，从而更新UI
            new Thread(() -> {
                //中缀表达式转后缀表达式
                String postfix = infixToPostfix();
                //后缀表达式求值
                String result = calculatePostfix(postfix);

                //如果返回值为error，说明计算过程中出现了错误，直接发送错误信息给UI
                if (result.equals("error1")) {
                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = "缺少操作数！";
                    uiHandler.sendMessage(msg);
                } else if (result.equals("error2")) {
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = "除数不能为0";
                    uiHandler.sendMessage(msg);
                } else { // 如果计算结果正确，则发送计算结果给UI
                    Message beginMsg = new Message();
                    beginMsg.what = 0;
                    uiHandler.sendMessage(beginMsg);

                    //此处让workerThread线程休眠，模拟计算的耗时过程
                    try {
                        Thread.sleep(loadingTime); // 此休眠时间用于 模拟子线程进行耗时逻辑 使用
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    Message msg = new Message();
                    msg.what = 1;//what是我们自定义的一个Message的识别码，以便于在Handler的handleMessage方法中根据what识别出不同的Message，以便我们做出不同的处理操作
                    msg.obj = result;//我们也可以通过给obj赋值Object类型传递向Message传入任意数据
                    uiHandler.sendMessage(msg);//将该Message发送给对应的Handler
                }
            }).start();

            saveHistoryToDatabase(); //保存计算历史记录到数据库
        } else if (id == R.id.btn_delete) {
            deleteOperation();
        } else if (id == R.id.btn_clear) {
            currentInput = "";
            tv_input.setText("请输入");
            tv_result.setText("运算结果显示");
        }
    }

    /**
     * 保存计算历史记录到数据库
     */
    private void saveHistoryToDatabase() {
        // 保存计算历史记录到数据库
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        CalculationHistory historyTemp = new CalculationHistory(currentInput);
        ContentValues values = new ContentValues();
        values.put("expression", historyTemp.getExpression());
        values.put("timestamp", historyTemp.getTimestamp());
        db.insert("history", null, values);
        values.clear();
        db.close();
    }


    /**
     * 显示ProgressDialog的方法
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在 子线程 运算中...");
            progressDialog.setCanceledOnTouchOutside(false); // 用户点击屏幕外部时，ProgressDialog不会被取消
        }
        progressDialog.show();
    }


    /**
     * 判断小数点输入是否合法
     *
     */
    private boolean pointIsLegal() {
        String currentInputTemp = currentInput.trim();// 获取当前输入的表达式，去除末尾空格
        if (currentInputTemp.isEmpty()) {// 检查当前输入是否为空
            return false;
        }
        String[] parts = currentInputTemp.split(" ");// 分割currentInput以检查小数点位置
        String lastPart = parts[parts.length - 1];// 检查最后一个输入部分

        // 判断条件：最后一个输入部分不以小数点结尾且不包含小数点，且不能以小数点开头
        if (lastPart.endsWith(".") || lastPart.contains(".")) {
            return false;
        }
        String temp = currentInput + ".";// 以下代码是判断小数点前一位是否有数字，一开始直接将小数点加进去
        if (temp.charAt(temp.length() - 2) == ' ') {//在temp字符串中查看最后的小数点位的前一位是否为空
            return false;
        }
        return true;
    }

    /**
     * 执行点击删除按钮时的操作
     */
    private void deleteOperation() {
        // 如果当前输入的最后一个字符是空格，继续删除前一个字符，直到找到非空格字符为止
        while (!currentInput.isEmpty() && currentInput.charAt(currentInput.length() - 1) == ' ') {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }

        if (!currentInput.isEmpty()) {// 删除最后一个非空格字符
            currentInput = currentInput.substring(0, currentInput.length() - 1);//删除最后一个字符
        }

        //如果被删除的字符前面也有空格，则删除该空格，一直到没有空格位置
        while (!currentInput.isEmpty() && currentInput.charAt(currentInput.length() - 1) == ' ') {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }

        //如果前面是加减乘除运算符，则在后面加空格，保持运算符前后都有空格，方便运算时使用“ ”分割
        char lastChar = currentInput.charAt(currentInput.length() - 1);
        if (!currentInput.isEmpty()
                && lastChar == '+'
                || lastChar == '-'
                || lastChar == '×'
                || lastChar == '÷') {
            currentInput += " ";
        }
        tv_input.setText(currentInput);
    }


    /**
     * 中缀表达式转后缀表达式
     */
    private String infixToPostfix() {
        String[] tokens = currentInput.split(" ");
        StringBuilder postfix = new StringBuilder();//存放后缀表达式
        Stack<String> operatorStack = new Stack<>();//定义栈存放操作符

        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("×") || token.equals("÷")) {
                // 遇到操作符，将操作符入栈
                //若栈空直接入栈
                if (operatorStack.isEmpty()) {
                    operatorStack.push(token);
                } else {
                    //栈不为空，比较优先级
                    String topOperator = operatorStack.peek();
                    if (getPriority(token) > getPriority(topOperator)) {
                        operatorStack.push(token);//当前操作符优先级大于栈顶操作符优先级，直接入栈
                    } else {
                        //当前操作符优先级小于等于栈顶操作符优先级，将栈顶操作符弹出，输出到后缀表达式中，再将当前操作符入栈
                        while (!operatorStack.isEmpty() && getPriority(token) <= getPriority(operatorStack.peek())) {
                            postfix.append(operatorStack.pop()).append(" ");
                        }
                        operatorStack.push(token);
                    }
                }
            } else {
                postfix.append(token).append(" ");// 遇到数字，将数字输出
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
     */
    private String calculatePostfix(String postfix) {
        // 定义栈存放操作数
        Stack<String> numberStack = new Stack<>();
        String[] tokens = postfix.split(" ");
        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("×") || token.equals("÷")) {

                // 检查栈中是否有足够的操作数，不要出现1 + =这种情况，需要两个操作数但现在只有一个
                if (numberStack.size() < 2) {
                    return "error1"; //error1表示缺少操作数
                }

                // 遇到操作符，从栈中弹出两个操作数，进行计算，并将结果入栈
                String num2 = numberStack.pop();
                String num1 = numberStack.pop();

                String result = null;
                // 替换原来的switch-case中的运算部分
                switch (token) {
                    case "+":
                        BigDecimal add = new BigDecimal(num1).add(new BigDecimal(num2));

                        //将BigDecimal转换为字符串之前，去除尾部的0
                        result = add.stripTrailingZeros().toPlainString();
                        break;
                    case "-":
                        BigDecimal subtract = new BigDecimal(num1).subtract(new BigDecimal(num2));

                        //将BigDecimal转换为字符串之前，去除尾部的0
                        result = subtract.stripTrailingZeros().toPlainString();
                        break;
                    case "×":
                        BigDecimal multiply = new BigDecimal(num1).multiply(new BigDecimal(num2));

                        //将BigDecimal转换为字符串之前，去除尾部的0
                        result = multiply.stripTrailingZeros().toPlainString();
                        break;
                    case "÷":
                        if (Double.parseDouble(num2) == 0) {
                            return "error2";//error2表示除数不能为0
                        }
                        BigDecimal divide = new BigDecimal(num1).divide(new BigDecimal(num2), 2, RoundingMode.HALF_UP);

                        //将BigDecimal转换为字符串之前，去除尾部的0
                        result = divide.stripTrailingZeros().toPlainString();
                        break;
                    default:
                        break;
                }
                numberStack.push(result);
            } else {
                numberStack.push(token);
            }
        }

        return numberStack.pop();
    }

    /**
     * 调用机器马达震动提醒
     */
    private void useVibrator() {
        //调用机器马达震动提醒
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

    /**
     * 获取操作符的优先级
     *
     */
    private int getPriority(String token) {
        switch (token) {
            case "+":
            case "-":
                return 1;
            case "×":
            case "÷":
                return 2;
            default:
                return 0;
        }
    }

    /**
     * 判断当前输入的运算符是否合法
     *
     */
    private boolean operarorIsLegal(View v) {
        // 如果当前输入的字符串为空，则不允许输入运算符
        if (currentInput.isEmpty()) {
            useVibrator();//调用机器马达震动提醒
            Toast.makeText(this, "计算内容为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 判断前一个字符是不是操作符
        String[] splitCurrentInput = currentInput.split(" ");
        if (splitCurrentInput.length > 1) {
            String s1 = splitCurrentInput[splitCurrentInput.length - 1];
            if (s1.equals("+") || s1.equals("-") || s1.equals("×") || s1.equals("÷")) {
                useVibrator();//调用机器马达震动提醒
                Toast.makeText(this, "字符不合法！", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /**
     * 执行数字和操作符的输入
     *
     */
    private void inputOperation(View view) {
        String numberOrOperator = ((Button) view).getText().toString();

        //每当输入运算符时，保存前后要加空格，方便后面取值
        if (numberOrOperator.equals("+") || numberOrOperator.equals("-") || numberOrOperator.equals("×") || numberOrOperator.equals("÷")) {
            currentInput += " " + numberOrOperator + " ";
        } else {
            currentInput += numberOrOperator;
        }

        tv_input.setText(currentInput);
    }

    /**
     * 添加OptionMenu
     *
     * @param menu The options menu in which you place your items.
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
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.item_history) {
            //每次点击历史记录，首先清空历史记录列表，再查询数据库
            calculationHistoryList.clear();

            // 准备查询数据
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // 查询的结果按时间倒序排列
            Cursor cursor = db.query("history", null, null, null, null, null, "timestamp DESC");
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String expression = cursor.getString(cursor.getColumnIndex("expression"));
                    @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                    //将查询出的历史记录添加到List中，并刷新RecyclerView
                    CalculationHistory history = new CalculationHistory();
                    history.setExpression(expression);
                    history.setTimestamp(timestamp);
                    calculationHistoryList.add(history);
                    historyAdapter.notifyDataSetChanged();
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

            // 创建PopupWindow
            createPopupWindow();
            // 显示PopupWindow
            if (!popupWindow.isShowing()) {
                popupWindow.showAtLocation(findViewById(R.id.main), Gravity.NO_GRAVITY, 0, 0);
            }
            return true;
        } else if (itemId == R.id.item_clear_history) {
            // 使用AlertDialog来显示确认框
            new AlertDialog.Builder(this)
                    .setTitle("清空历史记录") // 设置对话框标题
                    .setMessage("确定要清空所有历史记录吗？") // 设置对话框消息
                    .setPositiveButton("确定", (dialog, which) -> {
                        // 用户点击“确定”按钮后执行的操作
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("history", null, null); // 清空历史记录
                        db.close();
                        calculationHistoryList.clear(); // 清空列表
                        historyAdapter.notifyDataSetChanged(); // 刷新适配器
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        // 用户点击“取消”按钮后执行的操作，这里什么也不做
                        dialog.dismiss();
                    })
                    .show(); // 显示对话框
            return true;
        } else if (itemId == R.id.item_about) {
            // 创建AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("关于");
            builder.setMessage("本软件由robot-x开发，用于学习安卓开发。\n 于2024年6月6日 11:11:11");
            builder.setPositiveButton("确定", null);
            builder.show();
            return true;
        }
        return true;
    }

    /**
     * 创建PopupWindow
     *
     */
    private void createPopupWindow() {
        //弹出窗口初始化
        // 初始化PopupWindow
        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_recycler_view, null);
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
        //处理popup-window消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 设置动画资源
        int animStyle = android.R.style.Animation_Dialog; // 系统提供的动画样式
        popupWindow.setAnimationStyle(animStyle);
    }
}