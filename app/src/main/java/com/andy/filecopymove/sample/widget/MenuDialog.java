package com.andy.filecopymove.sample.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import com.andy.filecopymove.sample.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.andy.filecopymove.sample.app.MyApplication.TYPE_COPY;
import static com.andy.filecopymove.sample.app.MyApplication.TYPE_MORE;
import static com.andy.filecopymove.sample.app.MyApplication.TYPE_MOVE;

/**
 * 长按弹出菜单框
 *
 * @author Andy.R
 */
public class MenuDialog extends Dialog {

    private Handler mHandler;

    public MenuDialog(@NonNull Context context, Handler handler) {
        this(context, R.style.AppDialog, handler);
    }

    public MenuDialog(@NonNull Context context, int themeResId, Handler handler) {
        super(context, themeResId);
        this.mHandler = handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_copy, R.id.tv_move, R.id.tv_cancel, R.id.tv_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy:
                mHandler.sendEmptyMessage(TYPE_COPY);
                break;
            case R.id.tv_move:
                mHandler.sendEmptyMessage(TYPE_MOVE);
                break;
            case R.id.tv_more:
                mHandler.sendEmptyMessage(TYPE_MORE);
                break;
            default:
                break;
        }
        dismiss();
    }
}
