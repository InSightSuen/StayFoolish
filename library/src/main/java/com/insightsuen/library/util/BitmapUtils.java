package com.insightsuen.library.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A class containing static methods for {@link Bitmap}
 */
public class BitmapUtils {

    /**
     * Decode a new bitmap from file.The width and height of bitmap will
     * keep the original ratio, and try match the minimum ratio of request width
     * and height. If request width or height is 0, it will ignore the it and
     * match the other ratio. You can not give both request width and height 0 value.
     *
     * @param filePath  the image file path
     * @param reqWidth  the requested width
     * @param reqHeight the requested height
     * @param config    Bitmap config
     * @return the bitmap decoded from file.
     */
    public static Bitmap decodeSampledBitmapFromFile(String filePath,
            int reqWidth, int reqHeight, Bitmap.Config config) {
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("file path is empty or null.");
        }
        if (reqWidth < 0 || reqHeight < 0) {
            throw new IllegalArgumentException("reqWidth or reqHeight can not < 0");
        }
        if (reqWidth == 0 && reqHeight == 0) {
            throw new IllegalArgumentException("reqWidth and reqHeight can not be 0 both.");
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        Bitmap src = BitmapFactory.decodeFile(filePath, options);
        if (src != null) {
            return createScaledBitmap(src, reqWidth, reqHeight);
        }
        return null;
    }

    /**
     * Decode a new bitmap from resource. The width and height of bitmap will
     * keep the original ratio, and try match the minimum ratio of request width
     * and height. If request width or height is 0, it will ignore the it and
     * match the other ratio. You can not give both request width and height 0 value.
     *
     * @param resId     the resource id of the image data
     * @param reqWidth  the requested width
     * @param reqHeight the requested height
     * @param config    Bitmap config
     * @return the bitmap decoded from resource.
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight, Bitmap.Config config) {
        if (res == null) {
            throw new NullPointerException("resource is null.");
        }
        if (resId <= 0) {
            throw new IllegalArgumentException("resId must > 0");
        }
        if (reqWidth < 0 || reqHeight < 0) {
            throw new IllegalArgumentException("reqWidth or reqHeight can not < 0");
        }
        if (reqWidth == 0 && reqHeight == 0) {
            throw new IllegalArgumentException("reqWidth and reqHeight can not be 0 both.");
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = config;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options);
        if (src != null) {
            return createScaledBitmap(src, reqWidth, reqHeight);
        }
        return null;
    }

    /**
     * <p>Create new bitmap scaled from the original bitmap. The width and height of new
     * bitmap is based on the request width and height. It will keep the original ratio,
     * so the result width and height may not be same as the request width or height.
     * However, the result width and height will never greater than the requested. If you
     * give the request width or height 0 value, the result will only match the other side
     * ratio. So you can not give both request width and height 0 value.</p>
     * <p>If you just want the specified request width and height ast the result, you should
     * use {@link Bitmap#createScaledBitmap(android.graphics.Bitmap, int, int, boolean)}</p>
     *
     * @param source    the original bitmap
     * @param reqWidth  the request width
     * @param reqHeight the request height
     * @return scaled bitmap
     */
    public static Bitmap createScaledBitmap(Bitmap source, int reqWidth, int reqHeight) {
        if (source == null) {
            throw new NullPointerException("source is null.");
        }
        if (reqWidth < 0 || reqHeight < 0) {
            throw new IllegalArgumentException("reqWidth or reqHeight can not < 0");
        }
        if (reqWidth == 0 && reqHeight == 0) {
            throw new IllegalArgumentException("reqWidth and reqHeight can not be 0 both.");
        }

        float scale;
        if (reqWidth == 0) {
            scale = reqHeight / source.getHeight();
        } else if (reqHeight == 0) {
            scale = reqWidth / source.getWidth();
        } else {
            scale = Math.min((float) reqWidth / source.getWidth(),
                    (float) reqHeight / source.getHeight());
        }
        int dstWidth = (int) (source.getWidth() * scale);
        int dstHeight = (int) (source.getHeight() * scale);
        Bitmap scaled = Bitmap.createScaledBitmap(source, dstWidth, dstHeight, false);
        if (scaled != source) {
            source.recycle();
        }
        return scaled;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Shape the original bitmap by the mask bitmap decoded from mask resource id.
     *
     * @param src       the original bitmap
     * @param maskResId the mask resource id.
     * @return a new shaped bitmap
     */
    public static Bitmap shape(Context context, Bitmap src, int maskResId) {
        if (context == null) {
            throw new NullPointerException("context is null.");
        }
        if (src == null) {
            throw new NullPointerException("src bitmap is null");
        }
        if (src.isRecycled()) {
            throw new IllegalArgumentException("src bitmap is recycled.");
        }
        if (maskResId <= 0) {
            throw new IllegalArgumentException("mask resId must > 0");
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap shaped = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas shapeCanvas = new Canvas(shaped);
        shapeCanvas.drawBitmap(shaped, 0, 0, paint);

        Drawable drawable = ContextCompat.getDrawable(context, maskResId);
        Bitmap mask = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(mask);
        drawable.setBounds(0, 0, src.getWidth(), src.getHeight());
        drawable.draw(maskCanvas);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        shapeCanvas.drawBitmap(mask, 0, 0, paint);

        if (src != shaped) {
            src.recycle();
        }
        return shaped;
    }

    /**
     * Get the size in bytes of a Bitmap in a BitmapDrawable.
     *
     * @param value the BitmapDrawable wanted to calculate the size of Bitmap inside.
     * @return size in bytes.
     */
    public static int getBitmapSize(BitmapDrawable value) {
        return getBitmapSize(value.getBitmap());
    }

    /**
     * Get the size in bytes of a Bitmap. Note that from Android 4.4 (KitKat)
     * onward this returns the allocated memory size of the bitmap which can be larger than the
     * actual bitmap data byte count (in the case it was re-used).
     *
     * @param value the Bitmap wanted to calculate the size.
     * @return size in bytes
     */
    public static int getBitmapSize(Bitmap value) {
        if (value == null) {
            return -1;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return value.getAllocationByteCount();
        } else {
            return value.getByteCount();
        }
    }

    /**
     * @param candidate     - Bitmap to check
     * @param targetOptions - Options that have the out* value populated
     * @return true if <code>candidate</code> can be used for inBitmap re-use with
     * <code>targetOptions</code>
     */
    public static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
            return candidate.getWidth() == targetOptions.outWidth
                    && candidate.getHeight() == targetOptions.outHeight
                    && targetOptions.inSampleSize == 1;
        }

        // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
        // is smaller than the reusable bitmap candidate allocation byte count.
        int width = targetOptions.outWidth / targetOptions.inSampleSize;
        int height = targetOptions.outHeight / targetOptions.inSampleSize;
        int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
        return byteCount <= candidate.getAllocationByteCount();
    }

    /**
     * Return the byte usage per pixel of a bitmap based on its configuration.
     *
     * @param config The bitmap configuration.
     * @return The byte usage per pixel.
     */
    private static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

    /**
     * Get the representative frame as the cover of video if can find.
     * Or get the first frame of the video.
     *
     * @return the cover of video.
     */
    public static Bitmap getVideoCover(File videoFile) {
        if (videoFile == null) {
            throw new NullPointerException("video file is null.");
        }
        Bitmap cover = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoFile.getPath());
            cover = retriever.getFrameAtTime();
            if (cover == null) {
                cover = retriever.getFrameAtTime(0);
            }
        } finally {
            retriever.release();
        }
        return cover;
    }

    /**
     * Get the application icon from the apk file.
     *
     * @return the application icon BitmapDrawable of apk file.
     */
    public static BitmapDrawable getApkIcon(Context context, File apkFile) {
        if (apkFile == null) {
            throw new NullPointerException("apk file is null.");
        }
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkFile.getPath(),
                PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            Drawable iconDrawable;
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            appInfo.sourceDir = apkFile.getPath();
            appInfo.publicSourceDir = apkFile.getPath();
            iconDrawable = appInfo.loadIcon(packageManager);
            if (iconDrawable instanceof BitmapDrawable) {
                return (BitmapDrawable) iconDrawable;
            }
        }
        return null;
    }

    public static String saveFileToGallery(Context context, File imageFile) {
        return saveFileToGallery(context, imageFile, imageFile.getName(), null);
    }

    /**
     * Save image file to the system gallery folder.
     *
     * @return the URI of newly file created.
     */
    public static String saveFileToGallery(Context context, File imageFile, String name, String description) {
        if (imageFile == null) {
            throw new NullPointerException("image file is null.");
        }
        try {
            String uriString = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    imageFile.getPath(), name, description);
            Intent scanner = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanner.setData(Uri.fromFile(imageFile));
            context.sendBroadcast(scanner);
            return uriString;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveBitmapToFile(Bitmap souce, String dstPath) {
        return saveBitmapToFile(souce, dstPath, Bitmap.CompressFormat.PNG, 100);
    }

    /**
     * Save the bitmap to file.
     *
     * @param format  the compress format
     * @param quality the quality fo compress
     * @return whether saved or not.
     */
    public static boolean saveBitmapToFile(Bitmap source, String dstPath,
            Bitmap.CompressFormat format, int quality) {
        if (source == null) {
            throw new NullPointerException("source bitmap is null.");
        }
        if (format == null) {
            throw new NullPointerException("format is null.");
        }
        if (quality < 0 || quality > 100) {
            throw new IllegalArgumentException("quality must be 0..100");
        }
        if (TextUtils.isEmpty(dstPath)) {
            throw new IllegalArgumentException("dst path is null or empty");
        }
        File imageFile = new File(dstPath);
        if (imageFile.exists()) {
            throw new IllegalArgumentException("dst file path is already exists.");
        }
        FileOutputStream out = null;
        try {
            if (imageFile.createNewFile()) {
                out = new FileOutputStream(imageFile);
                source.compress(format, quality, out);
            }
        } catch (IOException e) {
            return false;
        } finally {
            IOUtils.closeQuietly(out);
        }
        return true;
    }
}
