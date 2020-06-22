/*
 * Copyright (C) 2007-2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package g3.viewmusicchoose;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Comparator;

/**
 * @author Peli
 * @author paulburke (ipaulpro)
 * @version 2013-12-11
 */
public class FileUtils {
    private FileUtils() {
    } //private constructor to enforce Singleton pattern

    /**
     * TAG for log messages.
     */
    static final String TAG = "FileUtils";
    private static final boolean DEBUG = false; // Set to true to enable logging

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";

    public static final String HIDDEN_PREFIX = ".";

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * @return Whether the URI is a local one.
     */
    public static boolean isLocal(String url) {
        return url != null && !url.startsWith("http://") && !url.startsWith("https://");
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
    public static String getMimeType(File file) {

        String extension = getExtension(file.getName());

        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    /**
     * @return The MIME type for the give Uri.
     */
    public static String getMimeType(Context context, Uri uri) {
        String path = getPath(context, uri);
        if (!FunctionUtils.isBlank(path)) {
            File file = new File(path);
            return getMimeType(file);
        } else {
            return "";
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndex(column);
                if (column_index != -1) {
                    try {
                        return cursor.getString(column_index);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @HUNGTDO custom
     */
    public static String getNameColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Audio.Media.DISPLAY_NAME;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    //https://stackoverflow.com/a/13133974/938427
    private static String savefileFromUri(Uri sourceuri, Context context) {
        String destinationFilename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + "/tmp.mp3";

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            InputStream in = context.getContentResolver().openInputStream(sourceuri);
            bis = new BufferedInputStream(in);
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return destinationFilename;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     */
    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG){

        }
//            Lo.d(TAG + " File -",
//                    "Authority: " + uri.getAuthority() +
//                            ", Fragment: " + uri.getFragment() +
//                            ", Port: " + uri.getPort() +
//                            ", Query: " + uri.getQuery() +
//                            ", Scheme: " + uri.getScheme() +
//                            ", Host: " + uri.getHost() +
//                            ", Segments: " + uri.getPathSegments().toString()
//            );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
//                Lo.d(TAG, "isDownloadsDocument ID: " + id);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        String filePath = getDataColumn(context, contentUri, null, null);
                        if (FunctionUtils.isBlank(filePath)) {
                            // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                            return savefileFromUri(uri, context);
                        }
                        return filePath;
                    } catch (NumberFormatException e) {
                        return null;
                    }

                } else {
                    return null;
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File - ex: file:///storage/emulated/0/ANH_XINH/listimg/26381.jpg
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see #getPath(Context, Uri)
     */
    public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (!FunctionUtils.isBlank(path) && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    public static String getReadableFileSize(int size) {
        final int BYTES_IN_KILOBYTES = 1024;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        float fileSize = 0;
        String suffix = KILOBYTES;

        if (size > BYTES_IN_KILOBYTES) {
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }
        return String.valueOf(dec.format(fileSize) + suffix);
    }

    /**
     * Attempt to retrieve the thumbnail of given File from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param file
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, File file) {
        return getThumbnail(context, getUri(file), getMimeType(file));
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, Uri uri) {
        return getThumbnail(context, uri, getMimeType(context, uri));
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @param mimeType
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, Uri uri, String mimeType) {
        if (DEBUG){

        }
//            Lo.d(TAG, "Attempting to get thumbnail");

        if (!isMediaUri(uri)) {
//            Lo.e(TAG, "You can only retrieve thumbnails for images and videos.");
            return null;
        }

        Bitmap bm = null;
        if (uri != null) {
            final ContentResolver resolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    final int id = cursor.getInt(0);
                    if (DEBUG)
//                        Lo.d(TAG, "Got thumb ID: " + id);

                    if (mimeType.contains("video")) {
                        bm = MediaStore.Video.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Video.Thumbnails.MINI_KIND,
                                null);
                    } else if (mimeType.contains(FileUtils.MIME_TYPE_IMAGE)) {
                        bm = MediaStore.Images.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Images.Thumbnails.MINI_KIND,
                                null);
                    }
                }
            } catch (Exception e) {
                if (DEBUG){

                }
//                    Lo.e(TAG, "getThumbnail e = " + e);
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return bm;
    }

    /**
     * File and folder comparator. TODO Expose sorting option method
     *
     * @author paulburke
     */
    public static Comparator<File> sComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };

    /**
     * File (not directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return files only (not directories) and skip hidden files
            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Folder (directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return directories only and skip hidden directories
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     * @author paulburke
     */
    public static Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    // ================ FILE PATH ==========================
    public static void copyToExternalStorage(final Context pContext, final int pSourceResourceID, final String pFilename) throws FileNotFoundException {
        FileUtils.copyToExternalStorage(pContext, pContext.getResources().openRawResource(pSourceResourceID), pFilename);
    }

    public static void copyToInternalStorage(final Context pContext, final int pSourceResourceID, final String pFilename) throws FileNotFoundException {
        FileUtils.copyToInternalStorage(pContext, pContext.getResources().openRawResource(pSourceResourceID), pFilename);
    }

    public static void copyToExternalStorage(final Context pContext, final String pSourceAssetPath, final String pFilename) throws IOException {
        FileUtils.copyToExternalStorage(pContext, pContext.getAssets().open(pSourceAssetPath), pFilename);
    }

    public static void copyToInternalStorage(final Context pContext, final String pSourceAssetPath, final String pFilename) throws IOException {
        FileUtils.copyToInternalStorage(pContext, pContext.getAssets().open(pSourceAssetPath), pFilename);
    }

    private static void copyToInternalStorage(final Context pContext, final InputStream pInputStream, final String pFilename) throws FileNotFoundException {
        StreamUtils.copyAndClose(pInputStream, new FileOutputStream(new File(pContext.getFilesDir(), pFilename)));
    }

    public static void copyToExternalStorage(final InputStream pInputStream, final String pFilePath) throws FileNotFoundException {
        if (FileUtils.isExternalStorageWriteable()) {
            final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pFilePath);
            StreamUtils.copyAndClose(pInputStream, new FileOutputStream(absoluteFilePath));
        } else {
            throw new IllegalStateException("External Storage is not writeable.");
        }
    }

    public static void copyToExternalStorage(final Context pContext, final InputStream pInputStream, final String pFilePath) throws FileNotFoundException {
        if (FileUtils.isExternalStorageWriteable()) {
            final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pContext, pFilePath);
            StreamUtils.copyAndClose(pInputStream, new FileOutputStream(absoluteFilePath));
        } else {
            throw new IllegalStateException("External Storage is not writeable.");
        }
    }

    public static boolean isFileExistingOnExternalStorage(final String pFilePath) {
        if (FileUtils.isExternalStorageReadable()) {
            final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pFilePath);
            final File file = new File(absoluteFilePath);
            return file.exists() && file.isFile();
        } else {
            throw new IllegalStateException("External Storage is not readable.");
        }
    }

    public static boolean isFileExistingOnExternalStorage(final Context pContext, final String pFilePath) {
        if (FileUtils.isExternalStorageReadable()) {
            final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pContext, pFilePath);
            final File file = new File(absoluteFilePath);
            return file.exists() && file.isFile();
        } else {
            throw new IllegalStateException("External Storage is not readable.");
        }
    }

    public static boolean isDirectoryExistingOnExternalStorage(final Context pContext, final String pDirectory) {
        if (FileUtils.isExternalStorageReadable()) {
            final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pContext, pDirectory);
            final File file = new File(absoluteFilePath);
            return file.exists() && file.isDirectory();
        } else {
            throw new IllegalStateException("External Storage is not readable.");
        }
    }

    public static boolean ensureDirectoriesExistOnExternalStorage(final Context pContext, final String pDirectory) {
        if (FileUtils.isDirectoryExistingOnExternalStorage(pContext, pDirectory)) {
            return true;
        }

        if (FileUtils.isExternalStorageWriteable()) {
            final String absoluteDirectoryPath = FileUtils.getAbsolutePathOnExternalStorage(pContext, pDirectory);
            return new File(absoluteDirectoryPath).mkdirs();
        } else {
            throw new IllegalStateException("External Storage is not writeable.");
        }
    }

    public static InputStream openOnExternalStorage(final String pFilePath) throws FileNotFoundException {
        final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pFilePath);
        return new FileInputStream(absoluteFilePath);
    }

    public static InputStream openOnExternalStorage(final Context pContext, final String pFilePath) throws FileNotFoundException {
        final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pContext, pFilePath);
        return new FileInputStream(absoluteFilePath);
    }

    public static String[] getDirectoryListOnExternalStorage(final Context pContext, final String pFilePath) throws FileNotFoundException {
        final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pContext, pFilePath);
        return new File(absoluteFilePath).list();
    }

    public static String[] getDirectoryListOnExternalStorage(final Context pContext, final String pFilePath, final FilenameFilter pFilenameFilter) throws FileNotFoundException {
        final String absoluteFilePath = FileUtils.getAbsolutePathOnExternalStorage(pContext, pFilePath);
        return new File(absoluteFilePath).list(pFilenameFilter);
    }

    public static String getAbsolutePathOnInternalStorage(final Context pContext, final String pFilePath) {
        return pContext.getFilesDir().getAbsolutePath() + pFilePath;
    }

    public static String getAbsolutePathOnExternalStorage(final String pFilePath) {
        return Environment.getExternalStorageDirectory() + "/" + pFilePath;
    }

    public static String getAbsolutePathOnExternalStorage(final Context pContext, final String pFilePath) {
        return Environment.getExternalStorageDirectory() + "/Android/data/" + pContext.getApplicationInfo().packageName + "/files/" + pFilePath;
    }

    public static boolean isExternalStorageWriteable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isExternalStorageReadable() {
        final String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    public static void copyFile(final File pSourceFile, final File pDestinationFile) throws IOException {
        // Create root folder if not exist
        String dirSave = pDestinationFile.getAbsolutePath();
        dirSave = dirSave.substring(0, dirSave.lastIndexOf("/"));
        FunctionUtils.createFolder(dirSave);
        // Copy file
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(pSourceFile);
            out = new FileOutputStream(pDestinationFile);
            StreamUtils.copy(in, out);
        } finally {
            StreamUtils.close(in);
            StreamUtils.close(out);
        }
    }

    /**
     * Deletes all files and sub-directories under <code>dir</code>. Returns
     * true if all deletions were successful. If a deletion fails, the method
     * stops attempting to delete and returns false.
     *
     * @param pFileOrDirectory
     * @return
     */
    public static boolean deleteDirectory(final File pFileOrDirectory) {
        if (pFileOrDirectory.isDirectory()) {
            final String[] children = pFileOrDirectory.list();
            final int childrenCount = children.length;
            for (int i = 0; i < childrenCount; i++) {
                final boolean success = FileUtils.deleteDirectory(new File(pFileOrDirectory, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return pFileOrDirectory.delete();
    }

    public static boolean fileExists(String filename) {
        File file = new File(filename);
        return file.exists();
    }

}
