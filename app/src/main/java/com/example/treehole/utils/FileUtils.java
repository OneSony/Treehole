package com.example.treehole.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.linkedin.android.litr.MediaTransformer;
import com.linkedin.android.litr.TransformationListener;
import com.linkedin.android.litr.TransformationOptions;
import com.linkedin.android.litr.analytics.TrackTransformationInfo;
import com.linkedin.android.litr.io.MediaRange;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileUtils {
    public static enum MEDIA_TYPE {
        IMAGE,
        VIDEO
    }

    public interface CompressionThreadCallback {
        void onResult(List<File> imageFiles, List<File> videoFiles);
    }

    public static class CompressionHandler extends Handler {
        Integer supposedImageCount = 0;
        Integer supposedVideoCount = 0;
        private List<CompressionThread> threads = new ArrayList<>();
        private List<File> imageFiles = new ArrayList<>();
        private List<File> videoFiles = new ArrayList<>();
        private CompressionThreadCallback callback;

        public static class Builder {
            private Context context;
            private Integer imageCount;
            private Integer videoCount;
            private CompressionHandler compressionHandler;

            public Builder(Context context) {
                this.context = context;
                this.imageCount = 0;
                this.videoCount = 0;
                this.compressionHandler = new CompressionHandler();
            }
            public Builder add(Uri uri, MEDIA_TYPE mediaType) {
                try {
                    if (mediaType==MEDIA_TYPE.IMAGE){
                        imageCount+=1;
                        compressionHandler.threads.add(new CompressionThread(context, compressionHandler, uri, File.createTempFile("image-"+String.valueOf(imageCount), ".jpg"), mediaType));
                    }
                    else if (mediaType==MEDIA_TYPE.VIDEO) {
                        videoCount+=1;
                        compressionHandler.threads.add(new CompressionThread(context, compressionHandler, uri, File.createTempFile("video-"+String.valueOf(videoCount), ".mp4"), mediaType));
                    }
                    else{
                        throw new RuntimeException("invalid MEDIA_TYPE in Builder.add");
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return this;
            }

            public Builder addCallback(CompressionThreadCallback callback) {
                compressionHandler.callback = callback;
                return this;
            }

            public CompressionHandler build() {
                // Set how many images and videos in handler
                compressionHandler.setImageCount(imageCount);
                compressionHandler.setVideoCount(videoCount);
                return compressionHandler;
            }
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String path = data.getString("path");
            if (msg.what==MEDIA_TYPE.IMAGE.ordinal()) {
                imageFiles.add(new File(path));
            }
            else if (msg.what == MEDIA_TYPE.VIDEO.ordinal()) {
                videoFiles.add(new File(path));
            }

            if (checkNumReached()){
                callback.onResult(imageFiles, videoFiles);
            }
        }

        private boolean checkNumReached() {
            if ((imageFiles.size() + videoFiles.size()) >= supposedImageCount+supposedVideoCount) {
                return true;
            }
            return false;
        }
        protected void setImageCount(Integer count) {
            supposedImageCount = count;
        }

        protected void setVideoCount(Integer count) {
            supposedVideoCount = count;
        }

        public void start() {
            Log.d("HANDLER", "Compressing: " + "images:"+imageFiles.size()+"|videos:"+videoFiles.size() + "/" + (supposedImageCount+supposedVideoCount));

            if (checkNumReached()){
                callback.onResult(imageFiles, videoFiles);
            }
            for (CompressionThread thread: threads) {
                thread.start();
            }

        }
    }

    public static class CompressionThread extends Thread {
        private Context context;
        private File outputFile;
        private Uri uri;
        private MEDIA_TYPE mediaType;
        private CompressionHandler handler;
        public CompressionThread(Context context, CompressionHandler handler, Uri uri, File outputFile, MEDIA_TYPE mediaType){
            super();
            this.context = context;
            this.handler = handler;
            this.uri = uri;
            this.outputFile = outputFile;
            this.mediaType = mediaType;
        }
        @Override
        public void run() {
            // Long-running operation goes here
            if (mediaType==MEDIA_TYPE.IMAGE){
                compressImage(context, uri, outputFile, 60);
                Bundle data = new Bundle();
                data.putString("path", getUriFilePath(context, uri));

                // send a message to the handler with the bundle
                Message msg = handler.obtainMessage();
                msg.what = MEDIA_TYPE.IMAGE.ordinal();
                msg.setData(data);

                handler.sendMessage(msg);
            }
            else if (mediaType==MEDIA_TYPE.VIDEO) {
                trimAndCompressVideo(context, uri, outputFile, new TransformationListener() {
                    @Override
                    public void onStarted(@NonNull String id) {
                        Log.d("COMPRESSVIDEO", "started");
                    }

                    @Override
                    public void onProgress(@NonNull String id, float progress) {
                        Integer percentage = Math.round(progress*100);
                        if (percentage%10==0){
                            Log.d("COMPRESSVIDEO", "Progress: "+percentage+"%");
                        }
                    }

                    @Override
                    public void onCompleted(@NonNull String id, @Nullable List<TrackTransformationInfo> trackTransformationInfos) {
                        Bundle data = new Bundle();
                        data.putString("path", outputFile.getAbsolutePath());

                        // send a message to the handler with the bundle
                        Message msg = handler.obtainMessage();
                        msg.what = MEDIA_TYPE.VIDEO.ordinal();
                        msg.setData(data);

                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onCancelled(@NonNull String id, @Nullable List<TrackTransformationInfo> trackTransformationInfos) {

                    }

                    @Override
                    public void onError(@NonNull String id, @Nullable Throwable cause, @Nullable List<TrackTransformationInfo> trackTransformationInfos) {
                        Log.e("VIDEOCOMPRESS", "ERROR: "+cause.getMessage());
                    }
                });
            }
            else {
                throw new RuntimeException("Invalid MEDIA_TYPE for CompressionThread");
            }
        }

    }
    /*
    public static File compressImage(String filePath, int quality) throws IOException {
        // Load the image from the file path
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        // Compress the bitmap to a lower quality
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

        // Save the compressed bitmap to a new File object
        File originalFile = new File(filePath);
        String compressedFilePath = originalFile.getParent() + File.separator + "compressed_" + originalFile.getName();
        File compressedFile = new File(compressedFilePath);
        FileOutputStream fos = new FileOutputStream(compressedFile);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();

        return compressedFile;
    }*/

    public static File compressImage(Context context, Uri uri, File outputFile, int quality) {
        try {
            // Load the image from the file path
            //Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            ContentResolver resolver = context.getContentResolver();

            // open the input stream from the uri
            InputStream inputStream = resolver.openInputStream(uri);

            // decode the bitmap from the input stream
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Compress the bitmap to a lower quality
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

            // Save the compressed bitmap to a new File object
            //File originalFile = new File(filePath);
            //String compressedFilePath = originalFile.getParent() + File.separator + "compressed_" + originalFile.getName();
            //File compressedFile = new File(compressedFilePath);
            //FileOutputStream fos = new FileOutputStream(compressedFile);
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();

            return outputFile;
        } catch (FileNotFoundException e) {
            Log.e("compressImage", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e("compressImage", "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e("compressImage", "Exception: " + e.getMessage());
        }

        return null;
    }

    public static File trimAndCompressVideo(Context context, Uri uri, File outputFile, TransformationListener listener) {
        /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.getPath());
        String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = Long.parseLong(durationStr);*/

        // Trim the video to the first 30 seconds.
            /*long startTime = 0;
            long endTime = Math.min(duration, 30000);*/
        // Set the output file path.
        //File outputFile = new File(Environment.getExternalStorageDirectory().getPath() + "/trimmed_video.mp4");


        MediaTransformer mediaTransformer = new MediaTransformer(context);

        MediaFormat targetVideoFormat = MediaFormat.createVideoFormat(
                MediaFormat.MIMETYPE_VIDEO_VP9,
                //MediaFormat.MIMETYPE_VIDEO_AVC,
                480,
                480
        );
        targetVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3_500_000);
        targetVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 29);
        targetVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
        targetVideoFormat.setInteger(MediaFormat.KEY_ROTATION, 0);
        //targetVideoFormat.setInteger(MediaFormat.KEY_QUALITY, 20);

        /*MediaFormat targetAudioFormat = MediaFormat.createAudioFormat(
                MediaFormat.MIMETYPE_AUDIO_AAC_XHE, 44000, 2
        );
        targetAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 256000);*/


        TransformationOptions options = new TransformationOptions.Builder()
                .setSourceMediaRange(new MediaRange(0, 30000000)) //30000000 for 30 seconds
                .setGranularity(MediaTransformer.GRANULARITY_DEFAULT)
                //.setVideoBitrate(5000000)
                //.setFrameRate(30)
                //.setVideoWidth(640)
                //.setVideoHeight(360)
                //.setAudioChannels(2)
                //.setAudioBitrate(128000)
                //.setAudioSampleRate(44100)
                .build();

            /*final MediaTransformationListener mediaTransformationListener = new MediaTransformationListener(context,
                    transformationState.requestId,
                    transformationState,
                    targetMedia);
            */
            /*mediaTransformer.transform(UUID.randomUUID().toString(),
                    uri,
                    outputFile.getAbsolutePath(),
                    MediaFormat.createVideoFormat("video/avc", 640, 360),
                    AudioFormat.ENCODING_AAC_HE_V1,
                    videoTransformationListener,
                    transformationOptions);*/

        class BackgroundTask extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... params) {
                mediaTransformer.transform(UUID.randomUUID().toString(),
                        uri,
                        outputFile.getAbsolutePath(),
                        targetVideoFormat,
                        //MediaFormat.createVideoFormat("video/avc", 640, 360),
                        //MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_RAW, 640, 360),
                        null,
                        //targetAudioFormat,
                        //MediaFormat.createAudioFormat(MimeType.AUDIO_AAC, 48000, 2),
                        /*
                        new TransformationListener() {
                            @Override
                            public void onStarted(@NonNull String id) {
                                Log.d("FILEUTILS", "Transcoding start");
                            }

                            @Override
                            public void onProgress(@NonNull String id, float progress) {
                                if (progress % 10 == 0) {
                                    Log.d("FILEUTILS", "Transcoding:" + String.valueOf(progress));
                                }

                            }

                            @Override
                            public void onCompleted(@NonNull String id, @Nullable List<TrackTransformationInfo> trackTransformationInfos) {
                                Log.d("FILEUTILS", "Transcoding complete");
                            }

                            @Override
                            public void onCancelled(@NonNull String id, @Nullable List<TrackTransformationInfo> trackTransformationInfos) {
                                Log.d("FILEUTILS", "Transcoding cancelled");
                            }

                            @Override
                            public void onError(@NonNull String id, @Nullable Throwable cause, @Nullable List<TrackTransformationInfo> trackTransformationInfos) {
                                Log.d("FILEUTILS", "Transcoding error: " + cause.getMessage());
                            }
                        },*/
                        listener,
                        options);
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            /*
                new MediaTranscoder.TranscodeConfig.Builder()
                        .setStartTime(startTime)
                        .setEndTime(endTime)
                        .setVideoBitrate(1000000)
                        .setFrameRate(30)
                        .setKeyFrameInterval(3)
                        .setScaleToFit(true)
                        .setVideoHeight(360)
                        .setVideoWidth(640)
                        .setOutputFormat("mp4")
                        .build());
        */
        }
        new BackgroundTask().execute();
        return outputFile;
    }

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

