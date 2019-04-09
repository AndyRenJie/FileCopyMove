package com.andy.filecopymove.sample.adapter;

import android.support.annotation.Nullable;
import android.util.Log;

import com.andy.filecopymove.sample.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.File;
import java.util.List;

/**
 * 文件路径适配器
 *
 * @author Andy.R
 */
public class FilePathAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public FilePathAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_file_path_name, item + " / ");
    }
}
