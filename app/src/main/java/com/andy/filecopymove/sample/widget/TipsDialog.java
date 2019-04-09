package com.andy.filecopymove.sample.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.andy.filecopymove.sample.R;
import com.andy.filecopymove.sample.model.FileModel;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.andy.filecopymove.sample.app.MyApplication.KEEP_CHECKED_FALSE;
import static com.andy.filecopymove.sample.app.MyApplication.KEEP_CHECKED_TRUE;
import static com.andy.filecopymove.sample.app.MyApplication.REPLACE_CHECKED_FALSE;
import static com.andy.filecopymove.sample.app.MyApplication.REPLACE_CHECKED_TRUE;
import static com.andy.filecopymove.sample.app.MyApplication.SKIP_CHECKED_FALSE;
import static com.andy.filecopymove.sample.app.MyApplication.SKIP_CHECKED_TRUE;

/**
 * 提示框
 *
 * @author Andy.R
 */
public class TipsDialog extends Dialog {

    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.skip_btn)
    Button skipBtn;
    @BindView(R.id.keep_btn)
    Button keepBtn;
    @BindView(R.id.replace_btn)
    Button replaceBtn;
    @BindView(R.id.cb_all)
    CheckBox checkBoxAll;

    private FileModel mFromFile;
    private File mTargetFile;
    private Context mContext;
    private Handler mHandler;

    public TipsDialog(@NonNull Context context, FileModel fromFile, File targetFile, Handler handler) {
        this(context, R.style.AppDialog, fromFile, targetFile, handler);
    }

    public TipsDialog(@NonNull Context context, int themeResId, FileModel fromFile, File targetFile, Handler handler) {
        super(context, themeResId);
        this.mContext = context;
        this.mFromFile = fromFile;
        this.mTargetFile = targetFile;
        this.mHandler = handler;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_copy_move);
        ButterKnife.bind(this);
        fillData();
    }

    private void fillData() {
        if (mFromFile.isFile()) {
            tvTips.setText(mTargetFile.getName() + "\n\n" + mContext.getString(R.string.tips1));
            replaceBtn.setText(mContext.getString(R.string.replace));
            skipBtn.setText(mContext.getString(R.string.skip));
            keepBtn.setVisibility(View.VISIBLE);
        } else {
            tvTips.setText(mTargetFile.getName() + "\n\n" + mContext.getString(R.string.tips2));
            replaceBtn.setText(mContext.getString(R.string.yes));
            skipBtn.setText(mContext.getString(R.string.no));
            keepBtn.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.skip_btn, R.id.keep_btn, R.id.replace_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip_btn:
                if (checkBoxAll.isChecked()) {
                    mHandler.sendEmptyMessage(SKIP_CHECKED_TRUE);
                } else {
                    mHandler.sendEmptyMessage(SKIP_CHECKED_FALSE);
                }
                break;
            case R.id.keep_btn:
                if (checkBoxAll.isChecked()) {
                    mHandler.sendEmptyMessage(KEEP_CHECKED_TRUE);
                } else {
                    mHandler.sendEmptyMessage(KEEP_CHECKED_FALSE);
                }
                break;
            case R.id.replace_btn:
                if (checkBoxAll.isChecked()) {
                    mHandler.sendEmptyMessage(REPLACE_CHECKED_TRUE);
                } else {
                    mHandler.sendEmptyMessage(REPLACE_CHECKED_FALSE);
                }
                break;
            default:
                break;
        }
        dismiss();
    }
}
