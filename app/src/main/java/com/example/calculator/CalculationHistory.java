package com.example.calculator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalculationHistory {
    private String expression; // 计算表达式
    private String timestamp;// 计算时间

    public CalculationHistory(String expression) {
        this.expression = expression;
        this.timestamp = getCurrentTimestamp();
    }

    /**
     * 获取当前时间戳并格式化为字符串
     * @return
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
