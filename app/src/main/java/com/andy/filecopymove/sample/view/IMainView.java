package com.andy.filecopymove.sample.view;

import com.andy.filecopymove.sample.model.FileModel;

import java.util.List;

public interface IMainView {
    /**
     * 显示文件列表
     *
     * @param list
     */
    void onFileModelList(List<FileModel> list);
}
