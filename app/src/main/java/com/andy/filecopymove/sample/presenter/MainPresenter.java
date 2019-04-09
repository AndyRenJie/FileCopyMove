package com.andy.filecopymove.sample.presenter;

import com.andy.filecopymove.sample.task.FileModelListTask;
import com.andy.filecopymove.sample.view.IMainView;

/**
 * @author Andy.R
 */
public class MainPresenter {

    private IMainView mIMainView;

    public MainPresenter(IMainView mIMainView) {
        this.mIMainView = mIMainView;
    }

    /**
     * 加载文件列表
     *
     * @param filePath
     */
    public void loadFileModelList(final String filePath) {
        new FileModelListTask().execute(filePath, mIMainView);
    }
}