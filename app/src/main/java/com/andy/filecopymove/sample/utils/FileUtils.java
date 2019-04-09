package com.andy.filecopymove.sample.utils;

import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.andy.filecopymove.sample.model.FileModel;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static String getRootStoragePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 根据路径获取文件集合
     *
     * @param filePath
     * @return
     */
    public static List<FileModel> getFileListByPath(final String filePath) {
        final List<FileModel> fileModelList = new ArrayList<>();
        if (!TextUtils.isEmpty(filePath)) {
            File parentFile = new File(filePath);
            if (parentFile.exists() && parentFile.isDirectory()) {
                File[] fileArray = parentFile.listFiles();
                for (File file : fileArray) {
                    if (!file.isFile() && !file.isDirectory()) {
                        continue;
                    }
                    FileModel fileModel = new FileModel();
                    fileModel.setFile(file.isFile());
                    fileModel.setDirectory(file.isDirectory());
                    fileModel.setFileName(file.getName());
                    fileModel.setFilePath(file.getAbsolutePath());
                    fileModel.setFileDate(file.lastModified());
                    fileModel.setFileSize(getFileSize(file));
                    fileModelList.add(fileModel);
                }
            }
        }
        sortListFolderToTop(fileModelList);
        return fileModelList;
    }

    /**
     * 获取文件/文件夹大小
     *
     * @param file
     * @return
     */
    private static long getFileSize(File file) {
        long fileSize = 0;
        if (file != null && file.exists()) {
            if (file.isFile()) {
                fileSize += file.length();
            } else {
                File[] fileArray = file.listFiles();
                if (fileArray != null && fileArray.length > 0) {
                    for (File file1 : fileArray) {
                        if (file1.isFile()) {
                            fileSize += file1.length();
                        } else {
                            fileSize += getFileSize(file1);
                        }
                    }
                }
            }
        }
        return fileSize;
    }

    /**
     * 文件夹在上排序
     *
     * @param list
     */
    private static void sortListFolderToTop(List<FileModel> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        Collections.sort(list, new Comparator<FileModel>() {
            @Override
            public int compare(FileModel o1, FileModel o2) {
                if (o1.isDirectory() && o2.isFile()) {
                    return -1;
                }
                if (o1.isFile() && o2.isDirectory()) {
                    return 1;
                }
                return 0;
            }
        });
    }

    /**
     * 根据选择的FileModel获取这个对象中的所有文件
     *
     * @param list
     * @return
     */
    public static List<FileModel> getAllFilesByFileModel(List<FileModel> list) {
        List<FileModel> fileModelList = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (FileModel fileModel : list) {
                fileModel.setFileFrom(fileModel.getFilePath());
                fileModelList.add(fileModel);
                if (fileModel.isDirectory()) {
                    getAllFilesIntoFolder(fileModel, fileModel.getFilePath(), fileModelList);
                }
            }
        }
        return fileModelList;
    }

    /**
     * 获取文件夹内所有的文件
     *
     * @param fileModel
     * @param fileFrom
     * @param fileModelList
     */
    private static void getAllFilesIntoFolder(FileModel fileModel, String fileFrom, List<FileModel> fileModelList) {
        File folder = new File(fileModel.getFilePath());
        for (File itemFile : folder.listFiles()) {
            FileModel model = new FileModel();
            model.setFile(itemFile.isFile());
            model.setDirectory(itemFile.isDirectory());
            model.setFileName(itemFile.getName());
            model.setFilePath(itemFile.getAbsolutePath());
            model.setFileFrom(fileFrom);
            model.setFileDate(itemFile.lastModified());
            fileModelList.add(model);
            if (model.isDirectory()) {
                getAllFilesIntoFolder(model, fileFrom, fileModelList);
            }
        }
        sortListFolderToTop(fileModelList);
    }

    /**
     * 复制文件到目标文件
     *
     * @param fromFilePath
     * @param toFilePath
     */
    public static void copyFileTo(String fromFilePath, String toFilePath) {
        copyFileTo(new File(fromFilePath), new File(toFilePath));
    }

    /**
     * 复制文件到目标文件
     *
     * @param fromFile
     * @param targetFile
     */
    public static void copyFileTo(File fromFile, File targetFile) {
        if (!fromFile.exists()) {
            return;
        }
        if (fromFile.isFile()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(fromFile);
                FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
                byte[] buffer = new byte[1024 * 4];
                int readLength;
                while ((readLength = fileInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, readLength);
                }
                fileOutputStream.flush();
                fileInputStream.close();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            targetFile.mkdirs();
        }
    }

    /**
     * 获取不重复的文件名
     *
     * @param folderPath
     * @param fileName
     * @param extension
     * @return
     */
    public static String getUniqueFileName(String folderPath, String fileName, String extension) {
        String uniqueFileName = fileName + extension;
        if (!new File(folderPath.trim() + uniqueFileName).exists()) {
            return uniqueFileName;
        }
        fileName = fileName + "_";
        int sequence = 1;
        for (int magnitude = 1; magnitude < 1000000000; magnitude *= 10) {
            for (int iteration = 0; iteration < 9; ++iteration) {
                uniqueFileName = fileName + sequence + extension;
                File uniqueFile = new File(folderPath + uniqueFileName);
                if (!uniqueFile.exists()) {
                    return uniqueFileName;
                }
                sequence += new Random(SystemClock.uptimeMillis()).nextInt(magnitude) + 1;
            }
        }
        return uniqueFileName;
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param file
     * @return
     */
    public static String getFileNameNoExtension(File file) {
        if (file == null) {
            return "";
        }
        String fileName = file.getName();
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * 获取文件扩展名(包含前面那个点".")
     *
     * @param file
     * @return
     */
    public static String getFileExtensionPoint(File file) {
        if (file == null || file.isDirectory()) {
            return "";
        }
        String fileName = file.getName();
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}
