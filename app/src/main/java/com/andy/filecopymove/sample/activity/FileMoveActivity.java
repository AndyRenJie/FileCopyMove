package com.andy.filecopymove.sample.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.filecopymove.sample.R;
import com.andy.filecopymove.sample.adapter.FileListAdapter;
import com.andy.filecopymove.sample.adapter.FilePathAdapter;
import com.andy.filecopymove.sample.model.FileModel;
import com.andy.filecopymove.sample.presenter.MainPresenter;
import com.andy.filecopymove.sample.utils.FileUtils;
import com.andy.filecopymove.sample.view.IMainView;
import com.andy.filecopymove.sample.widget.TipsDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.common.base.Joiner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.andy.filecopymove.sample.app.MyApplication.KEEP_CHECKED_FALSE;
import static com.andy.filecopymove.sample.app.MyApplication.KEEP_CHECKED_TRUE;
import static com.andy.filecopymove.sample.app.MyApplication.REPLACE_CHECKED_FALSE;
import static com.andy.filecopymove.sample.app.MyApplication.REPLACE_CHECKED_TRUE;
import static com.andy.filecopymove.sample.app.MyApplication.SKIP_CHECKED_FALSE;
import static com.andy.filecopymove.sample.app.MyApplication.SKIP_CHECKED_TRUE;
import static com.andy.filecopymove.sample.app.MyApplication.TYPE_COPY;
import static com.andy.filecopymove.sample.app.MyApplication.TYPE_MOVE;

/**
 * @author Andy.R
 */
public class FileMoveActivity extends BaseActivity implements IMainView {

    private static final String TAG = "FileMoveActivity";

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_file_path)
    RecyclerView filePathRecyclerView;
    @BindView(R.id.rv_file_list)
    RecyclerView fileListRecyclerView;
    /**
     * 文件路径适配器
     */
    private FilePathAdapter filePathAdapter;
    /**
     * 文件集合适配器
     */
    private FileListAdapter fileListAdapter;
    /**
     * 文件路径集合
     */
    private List<String> filePathList = new ArrayList<>();
    /**
     * 文件列表集合
     */
    private List<FileModel> fileModelList = new ArrayList<>();
    /**
     * 文件操作集合
     */
    private List<FileModel> fileOperationList = new ArrayList<>();
    /**
     * 文件临时集合
     */
    private List<FileModel> fileTempList = new ArrayList<>();
    /**
     * 当前文件路径
     */
    private String currentFilePath;

    private MainPresenter mainPresenter;
    /**
     * 文件操作类型
     */
    private int fileOperationType;
    /**
     * 文件操作下标
     */
    private int fileIndex;
    /**
     * 原始文件
     */
    private FileModel fromFile;
    /**
     * 目标文件
     */
    private File targetFile;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REPLACE_CHECKED_TRUE:
                    Iterator<FileModel> iterator = fileOperationList.iterator();
                    while (iterator.hasNext()) {
                        FileModel fileModel = iterator.next();
                        String toFilePath = fileModel.getFilePath().replace(
                                new File(fileModel.getFileFrom()).getParentFile().getAbsolutePath(), currentFilePath);
                        if (fromFile.isFile() && fileModel.isFile()) {
                            FileUtils.copyFileTo(fileModel.getFilePath(), toFilePath);
                            iterator.remove();
                            fileTempList.add(fileModel);
                        } else if (fromFile.isDirectory() && fileModel.isDirectory()) {
                            new File(toFilePath).mkdirs();
                            iterator.remove();
                            fileTempList.add(fileModel);
                        }
                    }
                    break;
                case REPLACE_CHECKED_FALSE:
                    if (fromFile.isFile()) {
                        FileUtils.copyFileTo(fromFile.getFilePath(), targetFile.getAbsolutePath());
                    } else {
                        if (!targetFile.exists()) {
                            targetFile.mkdirs();
                        }
                    }
                    fileIndex++;
                    break;
                case SKIP_CHECKED_TRUE:
                    if (fromFile.isFile()) {
                        for (; fileIndex < fileOperationList.size(); fileIndex++) {
                            if (fileOperationList.get(fileIndex).isFile()) {
                                fileOperationList.get(fileIndex).setSkip(true);
                            }
                        }
                    } else {
                        for (; fileIndex < fileOperationList.size(); fileIndex++) {
                            FileModel fileModel = fileOperationList.get(fileIndex);
                            //判断如果遍历是文件并且没有选择跳过的跳出循环显示对话框
                            if (fileModel.isFile() && !fileModel.isSkip()) {
                                break;
                            }
                            fileModel.setSkip(true);
                            for (FileModel model : fileOperationList) {
                                if (model.getFilePath().startsWith(fileModel.getFilePath())) {
                                    model.setSkip(true);
                                }
                            }
                        }
                    }
                    break;
                case SKIP_CHECKED_FALSE:
                    if (fromFile.isFile()) {
                        fileIndex++;
                        fromFile.setSkip(true);
                    } else {
                        //跳过一个文件夹下所有文件
                        Iterator<FileModel> iterator1 = fileOperationList.iterator();
                        while (iterator1.hasNext()) {
                            FileModel fileModel = iterator1.next();
                            if (fileModel.getFilePath().startsWith(fromFile.getFilePath())) {
                                iterator1.remove();
                                fileModel.setSkip(true);
                                fileTempList.add(fileModel);
                            }
                        }
                    }
                    break;
                case KEEP_CHECKED_TRUE:
                    for (; fileIndex < fileOperationList.size(); fileIndex++) {
                        FileModel fileModel = fileOperationList.get(fileIndex);
                        //拿到每一个文件的目标文件路径
                        String toFilePath = fileModel.getFilePath().replace(
                                new File(fileModel.getFileFrom()).getParentFile().getAbsolutePath(), currentFilePath);
                        File toFile = new File(toFilePath);
                        //添加名称下划线重新命名
                        String newToFileName = FileUtils.getUniqueFileName(toFile.getParentFile().getAbsolutePath() + "/",
                                FileUtils.getFileNameNoExtension(toFile), FileUtils.getFileExtensionPoint(toFile));
                        File newToFile = new File(toFile.getParentFile(), newToFileName);
                        FileUtils.copyFileTo(fileModel.getFilePath(), newToFile.getAbsolutePath());
                    }
                    break;
                case KEEP_CHECKED_FALSE:
                    //添加名称下划线重新命名
                    String newFileName = FileUtils.getUniqueFileName(targetFile.getParentFile().getAbsolutePath() + "/",
                            FileUtils.getFileNameNoExtension(targetFile), FileUtils.getFileExtensionPoint(targetFile));
                    File newTargetFile = new File(targetFile.getParentFile(), newFileName);
                    FileUtils.copyFileTo(fromFile.getFilePath(), newTargetFile.getAbsolutePath());
                    fileIndex++;
                    break;
                default:
                    break;
            }
            operationFile();
        }
    };

    @Override
    protected void setListener() {
        filePathAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (position < 3) {
                    return;
                }
                int cha = filePathList.size() - 1 - position;
                for (int i = 0; i < cha; i++) {
                    filePathList.remove(filePathList.size() - 1);
                }
                currentFilePath = Joiner.on("/").join(filePathList);
                loadFileModelList(currentFilePath);
            }
        });
        fileListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (fileModelList.get(position).isDirectory()) {
                    currentFilePath = fileModelList.get(position).getFilePath();
                    filePathList.clear();
                    filePathList.addAll(Arrays.asList(currentFilePath.split("/")));
                    loadFileModelList(currentFilePath);
                }
            }
        });

    }

    @Override
    protected void initData() {
        fileOperationType = getIntent().getIntExtra("fileOperationType", 0);
        switch (fileOperationType) {
            case TYPE_COPY:
                tvTitle.setText(getString(R.string.copy));
                break;
            case TYPE_MOVE:
                tvTitle.setText(getString(R.string.move));
                break;
            default:
                break;
        }
        List<FileModel> operationList = (List<FileModel>) getIntent().getSerializableExtra("fileOperationList");
        fileOperationList = FileUtils.getAllFilesByFileModel(operationList);
        currentFilePath = FileUtils.getRootStoragePath();
        filePathList.addAll(Arrays.asList(currentFilePath.split("/")));
        mainPresenter = new MainPresenter(this);
        loadFileModelList(currentFilePath);
    }

    /**
     * 加载文件列表
     *
     * @param filePath
     */
    private void loadFileModelList(String filePath) {
        showLoadingDialog();
        mainPresenter.loadFileModelList(filePath);
    }

    @Override
    protected void setAdapter() {
        filePathRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        fileListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        filePathAdapter = new FilePathAdapter(R.layout.file_path_item, filePathList);
        filePathRecyclerView.setAdapter(filePathAdapter);

        fileListAdapter = new FileListAdapter(this, fileModelList);
        //加载Item动画效果
        fileListAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        fileListRecyclerView.setAdapter(fileListAdapter);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_move;
    }

    @Override
    public void onFileModelList(List<FileModel> list) {
        closeLoadingDialog();
        fileModelList.clear();
        //排除和选择的文件夹相同的文件夹
        for (FileModel fileModel : fileOperationList) {
            if (new File(fileModel.getFilePath()).isDirectory()) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    if (list.get(i).getFilePath().equalsIgnoreCase(fileModel.getFilePath())) {
                        list.remove(i);
                    }
                }
            }
        }
        fileModelList.addAll(list);
        filePathAdapter.replaceData(filePathList);
        fileListAdapter.replaceData(fileModelList);
    }

    @OnClick({R.id.tv_back, R.id.ok_btn, R.id.cancel_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
            case R.id.cancel_btn:
                backForResult(false);
                break;
            case R.id.ok_btn:
                if (fileOperationList != null && !fileOperationList.isEmpty()) {
                    operationFile();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    /**
     * 操作文件
     */
    private void operationFile() {
        try {
            if (fileIndex < fileOperationList.size()) {
                fromFile = fileOperationList.get(fileIndex);
                if (!TextUtils.isEmpty(fromFile.getFilePath())) {
                    if (new File(fromFile.getFilePath()).exists()) {
                        targetFile = new File(fromFile.getFilePath().replace(new File(fromFile.getFileFrom()).getParentFile().getAbsolutePath(), currentFilePath));
                        // 判断文件夹或文件是否重名
                        if (targetFile.exists()) {
                            //显示提示框
                            showCopyMoveTipsDialog();
                        } else {
                            if (fromFile.isFile()) {
                                FileUtils.copyFileTo(fromFile.getFilePath(), targetFile.getAbsolutePath());
                            } else {
                                targetFile.mkdirs();
                            }
                            fileIndex++;
                            operationFile();
                        }
                    }
                }
            } else {
                //待所有文件操作完成后，判断是否是移动操作，如果是移动的就删除源文件
                if (fileOperationType == TYPE_MOVE) {
                    //把刚才放进临时集合的文件重新放进操作集合中
                    fileOperationList.addAll(fileTempList);
                    Collections.reverse(fileOperationList);
                    //遍历操作集合判断是否需要跳过
                    for (FileModel fileModel : fileOperationList) {
                        if (fileModel.isFile() && !fileModel.isSkip()) {
                            new File(fileModel.getFilePath()).delete();
                        } else {
                            File dirFile = new File(fileModel.getFilePath());
                            if (dirFile.listFiles().length == 0) {
                                dirFile.delete();
                            }
                        }
                    }
                }
                Toast.makeText(this, getString(R.string.operation_success), Toast.LENGTH_SHORT).show();
                backForResult(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backForResult(boolean isResultOk) {
        if (isResultOk) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    /**
     * 复制替换有重复文件的提示框
     */
    private void showCopyMoveTipsDialog() {
        TipsDialog tipsDialog = new TipsDialog(this, fromFile, targetFile, handler);
        tipsDialog.show();
    }
}
