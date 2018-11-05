package org.bcss.collect.naxa.migrate;

import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class MigrationHelper {


    public final String ROOT = Environment.getExternalStorageDirectory() + File.separator;
    public final String MIGRATE_FROM = ROOT + "bcss";
    private final String MIGRATE_TO = ROOT + "fieldsight";
    private final String usernameOrEmail;


    public MigrationHelper(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    String getOldRootPath() {
        return MIGRATE_FROM + File.separator + usernameOrEmail;
    }

    String getNewRootPath() {
        return MIGRATE_TO + File.separator;
    }

    public List<File> listOldAccount() {
        return listFilesInDir(getFileByPath(MIGRATE_FROM));
    }

    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public boolean hasOldAccount() {
        boolean hasOldAccount = false;
        for (File file : listOldAccount()) {
            if (usernameOrEmail.equalsIgnoreCase(file.getName())) {
                hasOldAccount = true;
            }
        }

        return hasOldAccount;
    }


    public static List<File> listFilesInDir(final File dir) {
        return listFilesInDirWithFilter(dir, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return true;
            }
        }, false);
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    public static List<File> listFilesInDirWithFilter(final File dir,
                                                      final FileFilter filter,
                                                      final boolean isRecursive) {
        if (!isDir(dir)) return null;
        List<File> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    list.add(file);
                }
                if (isRecursive && file.isDirectory()) {
                    //noinspection ConstantConditions
                    list.addAll(listFilesInDirWithFilter(file, filter, true));
                }
            }
        }
        return list;
    }


    public static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

}
