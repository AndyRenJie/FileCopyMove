package com.andy.filecopymove.sample.task;

import android.os.AsyncTask;

import com.andy.filecopymove.sample.model.FileModel;
import com.andy.filecopymove.sample.utils.FileUtils;
import com.andy.filecopymove.sample.view.IMainView;

import java.util.List;

public class FileModelListTask extends AsyncTask<Object, Void, List<FileModel>> {

    private IMainView mIMainView;

    @Override
    protected List<FileModel> doInBackground(Object... objects) {
        this.mIMainView = (IMainView) objects[1];
        return FileUtils.getFileListByPath((String)objects[0]);
    }

    @Override
    protected void onPostExecute(List<FileModel> list) {
        super.onPostExecute(list);
        mIMainView.onFileModelList(list);
    }
}
