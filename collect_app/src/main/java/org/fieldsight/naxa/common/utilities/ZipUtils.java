package org.fieldsight.naxa.common.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    /*
     *
     * Zips a file at a location and places the resulting zip file at the toLocation
     * Example: zipFileAtPath("downloads/myfolder", "downloads/myFolder.zip");
     */

    ZipProgressListener listener;

    public interface ZipProgressListener {
        void onZipping(String message, int progress);
        void onComplete();
    }

    public void setZipProgressListener(ZipProgressListener listener) {
        this.listener = listener;
    }

    public HashMap<String, String> getAllInfo(String sourcePath) {
        HashMap<String, String> allinfo = new HashMap<>();
        File sourceFile = new File(sourcePath);
        if (sourceFile.isDirectory()) {
            allinfo.put("type", "Folder");
            File files[] = sourceFile.listFiles();
            allinfo.put("size", sourceFile.length() + "");
            allinfo.put("total_file", files.length + "");
            StringBuilder fileName = new StringBuilder();
            boolean first = true;
            for (File file : files) {
                if (first) {
                    fileName.append(file.getName());
                    first = false;
                    continue;
                }
                fileName.append("," + file.getName());
            }
            allinfo.put("name", fileName.toString());
        } else {
            allinfo.put("type", "File");
            allinfo.put("name", sourceFile.getName());
            allinfo.put("size", sourceFile.length() + "");
            allinfo.put("total_file", "1");
        }
        return allinfo;
    }

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                int total = fi.available();
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                int totalRead = 0;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                    totalRead += count;
                    if (listener != null) {
                        listener.onZipping("Zipping " + sourceFile.getName(), totalRead * 100 / total);
                    }
                }
            }
            out.close();
            if(listener != null) {
                listener.onComplete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     *
     * Zips a subfolder
     *
     */

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        int totalCount = 0;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                int total = fi.available();
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
//                int totalRead = 0;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
//                    this loop will give the progress of each file zipping progress
//                    totalRead += count;
//                    if (listener != null) {
//                        listener.onZipping("Zipping " + file.getName(), totalRead * 100 / total);
//                    }
                }
//                here the progress is measured out of file zipped vs total file
                totalCount ++;
                if (listener != null) {
                    listener.onZipping("Zipping " + file.getName(), totalCount * 100 / fileList.length);
                }
                origin.close();
            }
        }
        if(listener != null) {
            listener.onComplete();
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }
}
