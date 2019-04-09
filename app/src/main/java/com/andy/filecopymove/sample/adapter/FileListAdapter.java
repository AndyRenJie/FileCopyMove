package com.andy.filecopymove.sample.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.widget.CompoundButton;

import com.andy.filecopymove.sample.R;
import com.andy.filecopymove.sample.model.FileModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件列表适配器
 *
 * @author Andy.R
 */
public class FileListAdapter extends BaseQuickAdapter<FileModel, BaseViewHolder> {

    private Context mContext;
    private boolean mIsShowCheckBox;
    private List<FileModel> mCheckFileModelList;

    public FileListAdapter(Context context, @Nullable List<FileModel> data) {
        super(R.layout.file_list_item, data);
        this.mContext = context;
        this.mCheckFileModelList = new ArrayList<>();
    }

    @Override
    protected void convert(BaseViewHolder helper, final FileModel item) {
        if (item.isDirectory()) {
            helper.setImageResource(R.id.iv_file_icon, R.mipmap.ic_folder_icon);
        } else {
            helper.setImageResource(R.id.iv_file_icon, R.mipmap.ic_file_icon);
        }
        helper.setText(R.id.tv_file_name, item.getFileName());
        if(item.getFileDate() == 0){
            helper.setText(R.id.tv_file_date, "N/A");
        }else{
            helper.setText(R.id.tv_file_date, DateUtils.formatDateTime(mContext, item.getFileDate(),
                    DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME));
        }
        helper.setText(R.id.tv_file_size, Formatter.formatFileSize(mContext, item.getFileSize()));
        helper.setGone(R.id.cb_file_selected, mIsShowCheckBox);
        if (checkFileIsChecked(item)) {
            helper.setChecked(R.id.cb_file_selected, true);
        } else {
            helper.setChecked(R.id.cb_file_selected, false);
        }
    }

    public void showCheckBox(boolean isShowCheckBox) {
        this.mIsShowCheckBox = isShowCheckBox;
        this.notifyDataSetChanged();
    }

    public List<FileModel> getCheckedFileList() {
        if (mCheckFileModelList == null) {
            mCheckFileModelList = new ArrayList<>();
        }
        return mCheckFileModelList;
    }

    public void clearCheckedList() {
        if (mCheckFileModelList != null && !mCheckFileModelList.isEmpty()) {
            mCheckFileModelList.clear();
        }
    }

    public boolean checkFileIsChecked(FileModel fileModel) {
        for (FileModel model : mCheckFileModelList) {
            if (model.getFilePath().equals(fileModel.getFilePath())) {
                return true;
            }
        }
        return false;
    }
}
