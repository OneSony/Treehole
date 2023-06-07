package com.example.treehole;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.File;

public class FileUtils {

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getUriFilePath(Context context, Uri uri) {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 处理文档类型 URI
            if (isExternalStorageDocument(uri)) {
                // 处理外部存储文档
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    // 处理其他类型的外部存储文档
                    final String path = getExternalStoragePath(context, type);
                    if (path != null) {
                        return path + "/" + split[1];
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                // 处理下载文档
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = Uri.parse("content://downloads/public_downloads");
                final Uri downloadUri = ContentUris.withAppendedId(contentUri, Long.parseLong(id));
                return getDataColumn(context, downloadUri, null, null);
            } else if (isMediaDocument(uri)) {
                // 处理媒体文档
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 处理 content:// URI
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 处理 file:// URI
            return uri.getPath();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        try (Cursor cursor = context.getContentResolver().query(uri, new String[]{android.provider.MediaStore.Files.FileColumns.DATA}, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.DATA));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getExternalStoragePath(Context context, String type) {
        final File[] externalFilesDirs = ContextCompat.getExternalFilesDirs(context, null);
        for (File file : externalFilesDirs) {
            final String path = file.getPath();
            if (path.endsWith("/" + type)) {
                return path.substring(0, path.indexOf("/Android/data"));
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}

