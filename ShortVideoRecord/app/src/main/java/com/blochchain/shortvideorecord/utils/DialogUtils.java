package com.blochchain.shortvideorecord.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blockchain.shortvideorecord.R;

public class DialogUtils {
    public static Dialog createLoadDialog(Context context, boolean isClickable) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_layout, null);// 得到加载view
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(isClickable);// 不可以用“返回键”取消
        loadingDialog.setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }

    /**
     * 自定义退出登录对话框
     */
    public static AlertDialog showLogoutDialog(Context ctx, String alertContent, View.OnClickListener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        // *** 主要就是在这里实现这种效果的
        // 设置窗口的内容页面,alert_dialog_main_title.xml文件中定义view内容
        View dialogView = View.inflate(ctx, R.layout.alert_dialog_logout,
                null);
        window.setContentView(dialogView);
        // 设置对应提示框内容
        TextView alert_dialog_content = (TextView) dialogView
                .findViewById(R.id.alert_dialog_content);
        if(!TextUtils.isEmpty(alertContent)){
            alert_dialog_content.setText(alertContent);
        }
        //取消
        TextView tv_cancle = (TextView) dialogView
                .findViewById(R.id.tv_cancle);
        //确定
        TextView tv_ensure = (TextView) dialogView
                .findViewById(R.id.tv_ensure);
        tv_cancle.setOnClickListener(listener);
        tv_ensure.setOnClickListener(listener);
        // 点击空白处不消失
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    /**
     * 自定义双按钮对话框
     */
    public static AlertDialog showAlertDoubleBtnDialog(Context ctx, String alertContent, View.OnClickListener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        // *** 主要就是在这里实现这种效果的
        // 设置窗口的内容页面,alert_dialog_main_title.xml文件中定义view内容
        View dialogView = View.inflate(ctx, R.layout.alert_dialog_double_btn,
                null);
        window.setContentView(dialogView);
        // 设置对应提示框内容
        TextView alert_dialog_content = (TextView) dialogView
                .findViewById(R.id.alert_dialog_content);
        if(!TextUtils.isEmpty(alertContent)){
            alert_dialog_content.setText(alertContent);
        }
        //取消
        TextView tv_cancle = (TextView) dialogView
                .findViewById(R.id.tv_cancle);
        //确定
        TextView tv_ensure = (TextView) dialogView
                .findViewById(R.id.tv_ensure);
        tv_cancle.setOnClickListener(listener);
        tv_ensure.setOnClickListener(listener);
        // 点击空白处不消失
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }
}
