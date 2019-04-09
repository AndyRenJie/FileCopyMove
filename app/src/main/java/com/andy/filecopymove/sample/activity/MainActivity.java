package com.andy.filecopymove.sample.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.andy.filecopymove.sample.R;
import com.andy.filecopymove.sample.adapter.FileListAdapter;
import com.andy.filecopymove.sample.adapter.FilePathAdapter;
import com.andy.filecopymove.sample.model.FileModel;
import com.andy.filecopymove.sample.presenter.MainPresenter;
import com.andy.filecopymove.sample.utils.FileUtils;
import com.andy.filecopymove.sample.view.IMainView;
import com.andy.filecopymove.sample.widget.MenuDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.common.base.Joiner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.andy.filecopymove.sample.app.MyApplication.FILE_OPERATION_REQUEST_CODE;
import static com.andy.filecopymove.sample.app.MyApplication.TYPE_COPY;
import static com.andy.filecopymove.sample.app.MyApplication.TYPE_MORE;
import static com.andy.filecopymove.sample.app.MyApplication.TYPE_MOVE;

public class MainActivity extends BaseActivity implements IMainView {

    @BindView(R.id.rv_file_path)
    RecyclerView mFilePathRecyclerView;
    @BindView(R.id.rv_file_list)
    RecyclerView mFileListRecyclerView;
    @BindView(R.id.file_operation_layout)
    LinearLayout mFileOperationLayout;

    private FilePathAdapter mFilePathAdapter;
    private FileListAdapter mFileListAdapter;
    /**
     * 路径集合
     */
    private List<String> mFilePathList = new ArrayList<>();
    /**
     * 文件列表集合
     */
    private List<FileModel> mFileModelList = new ArrayList<>();
    /**
     * 文件操作集合
     */
    private List<FileModel> mFileOperationList = new ArrayList<>();
    /**
     * 当前路径
     */
    private String mCurrentFilePath;

    private MainPresenter mMainPresenter;

    private boolean mIsEditMode;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TYPE_COPY:
                    fileOperation(TYPE_COPY);
                    break;
                case TYPE_MOVE:
                    fileOperation(TYPE_MOVE);
                    break;
                case TYPE_MORE:
                    starEditMode();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void setListener() {
        mFilePathAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position < 3) {
                    return;
                }
                int cha = mFilePathList.size() - 1 - position;
                for (int i = 0; i < cha; i++) {
                    mFilePathList.remove(mFilePathList.size() - 1);
                }
                mCurrentFilePath = Joiner.on("/").join(mFilePathList);
                loadFileModelList(mCurrentFilePath);
            }
        });
        mFileListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FileModel fileModel = mFileModelList.get(position);
                if (mIsEditMode) {
                    if (mFileListAdapter.checkFileIsChecked(fileModel)) {
                        mFileListAdapter.getCheckedFileList().remove(fileModel);
                    } else {
                        mFileListAdapter.getCheckedFileList().add(fileModel);
                    }
                    mFileListAdapter.notifyDataSetChanged();
                } else {
                    if (fileModel.isDirectory()) {
                        mCurrentFilePath = fileModel.getFilePath();
                        mFilePathList.clear();
                        mFilePathList.addAll(Arrays.asList(mCurrentFilePath.split("/")));
                        loadFileModelList(mCurrentFilePath);
                    }
                }
            }
        });
        mFileListAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                mFileOperationList.clear();
                mFileOperationList.add(mFileModelList.get(position));
                MenuDialog menuDialog = new MenuDialog(MainActivity.this, mHandler);
                menuDialog.show();
                return false;
            }
        });
    }

    @Override
    public void setAdapter() {
        mFilePathRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mFileListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFilePathAdapter = new FilePathAdapter(R.layout.file_path_item, mFilePathList);
        mFilePathRecyclerView.setAdapter(mFilePathAdapter);

        mFileListAdapter = new FileListAdapter(this, mFileModelList);
        //加载Item动画效果
        mFileListAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        mFileListRecyclerView.setAdapter(mFileListAdapter);
        //添加分割线
        mFileListRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initData() {
        mCurrentFilePath = FileUtils.getRootStoragePath();
        mFilePathList.addAll(Arrays.asList(mCurrentFilePath.split("/")));
        mMainPresenter = new MainPresenter(this);
        loadFileModelList(mCurrentFilePath);
    }

    @OnClick({R.id.file_move_btn, R.id.file_copy_btn})
    public void onClick(View view) {
        int fileOperationType = 0;
        switch (view.getId()) {
            case R.id.file_move_btn:
                fileOperationType = TYPE_MOVE;
                break;
            case R.id.file_copy_btn:
                fileOperationType = TYPE_COPY;
                break;
            default:
                break;
        }
        List<FileModel> checkFileList = mFileListAdapter.getCheckedFileList();
        if (checkFileList != null && !checkFileList.isEmpty()) {
            Intent intent = new Intent(this, FileMoveActivity.class);
            intent.putExtra("fileOperationType", fileOperationType);
            intent.putExtra("fileOperationList", (Serializable) checkFileList);
            startActivityForResult(intent, FILE_OPERATION_REQUEST_CODE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsEditMode) {
                exitEditMode();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 加载文件列表
     *
     * @param filePath
     */
    private void loadFileModelList(final String filePath) {
        showLoadingDialog();
        mMainPresenter.loadFileModelList(filePath);
    }

    @Override
    public void onFileModelList(List<FileModel> list) {
        closeLoadingDialog();
        mFileModelList.clear();
        mFileModelList.addAll(list);
        mFilePathAdapter.replaceData(mFilePathList);
        mFileListAdapter.replaceData(mFileModelList);
    }

    private void fileOperation(int type) {
        Intent intent = new Intent(this, FileMoveActivity.class);
        intent.putExtra("fileOperationType", type);
        intent.putExtra("fileOperationList", (Serializable) mFileOperationList);
        startActivityForResult(intent, FILE_OPERATION_REQUEST_CODE);
    }

    /**
     * 退出更多编辑模式
     */
    private void exitEditMode() {
        mIsEditMode = false;
        mFileOperationLayout.setVisibility(View.GONE);
        mFileListAdapter.showCheckBox(false);
        mFileListAdapter.clearCheckedList();
        mFileOperationList.clear();
    }

    /**
     * 开启更多编辑模式
     */
    private void starEditMode() {
        mIsEditMode = true;
        mFileOperationLayout.setVisibility(View.VISIBLE);
        mFileListAdapter.showCheckBox(true);
        mFileListAdapter.clearCheckedList();
        mFileOperationList.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        exitEditMode();
        if (resultCode == RESULT_OK) {
            loadFileModelList(mCurrentFilePath);
        }
    }
}
