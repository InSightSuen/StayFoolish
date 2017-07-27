package com.insightsuen.library.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

/**
 * A class containing static methods for java.io etc.
 */
public final class IOUtils {

    public static final int BUFFER_1K = 1024;
    public static final int BUFFER_4K = 4096;


    /**
     * @return remainder of {@link Reader} as a string, closing it finally.
     */
    public static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[BUFFER_1K];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            reader.close();
        }
    }

    /**
     * Close {@link Closeable} ignoring any checked exception.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Recursively delete directory or file.
     */
    public static void deleteFile(File file) throws IOException {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null) {
                throw new IOException("failed to list child files: " + file);
            }
            for (File child : children) {
                deleteFile(child);
            }
        } else if (!file.delete()){
            throw new IOException("failed to delete files: " + file);
        }
    }
}
