/*
 * Copyright (C) 2017 Stay foolish Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.insightsuen.library.cache;

import com.insightsuen.library.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * *****************************************************************************
 * Taken from the JB source code, can be found in:
 * libcore/luni/src/main/java/libcore/io/DiskLruCache.java
 * or direct link:
 * https://android.googlesource.com/platform/libcore/+/android-4.1.1_r1/luni/src/main/java/libcore/io/DiskLruCache.java
 * *****************************************************************************
 */
public class DiskLruCache {

    private static final String JOURNAL_FILE = "journal";

    private static final String JOURNAL_FILE_TMP = "journal.tmp";

    private static final String MAGIC = "libcore.io.DiskLruCache";

    private static final String VERSION_1 = "1";

    private static final long ANY_SEQUENCE_NUMBER = -1;

    private static final String CLEAN = "CLEAN";

    private static final String DIRTY = "DIRTY";

    private static final String REMOVE = "REMOVE";

    private static final String READ = "READ";

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /*
     * This cache uses a journal file named "journal". A typical journal file
     * looks like this:
     *     libcore.io.DiskLruCache
     *     1
     *     100
     *     2
     *
     *     CLEAN 3400330d1dfc7f3f7f4b8d4d803dfcf6 832 21054
     *     DIRTY 335c4c6028171cfddfbaae1a9c313c52
     *     CLEAN 335c4c6028171cfddfbaae1a9c313c52 3934 2342
     *     REMOVE 335c4c6028171cfddfbaae1a9c313c52
     *     DIRTY 1ab96a171faeeee38496d8b330771a7a
     *     CLEAN 1ab96a171faeeee38496d8b330771a7a 1600 234
     *     READ 335c4c6028171cfddfbaae1a9c313c52
     *     READ 3400330d1dfc7f3f7f4b8d4d803dfcf6
     *
     * The first five lines of the journal form its header. They are the
     * constant string "libcore.io.DiskLruCache", the disk cache's version,
     * the application's version, the value count, and a blank line.
     *
     * Each of the subsequent lines in the file is a record of the state of a
     * cache entry. Each line contains space-separated values: a state, a key,
     * and optional state-specific values.
     *   o DIRTY lines track that an entry is actively being created or updated.
     *     Every successful DIRTY action should be followed by a CLEAN or REMOVE
     *     action. DIRTY lines without a matching CLEAN or REMOVE indicate that
     *     temporary files may need to be deleted.
     *   o CLEAN lines track a cache entry that has been successfully published
     *     and may be read. A publish line is followed by the lengths of each of
     *     its values.
     *   o READ lines track accesses for LRU.
     *   o REMOVE lines track entries that have been deleted.
     *
     * The journal file is appended to as cache operations occur. The journal may
     * occasionally be compacted by dropping redundant lines. A temporary file named
     * "journal.tmp" will be used during compaction; that file should be deleted if
     * it exists when the cache is opened.
     */

    private final File mDirectory;

    private final File mJournalFile;

    private final File mJournalFileTmp;

    private final int mAppVersion;

    private final int mValueCount;

    private final long mMaxSize;

    private long mSize = 0;

    private Writer mJournalWriter;

    private final LinkedHashMap<String, Entry> mLruEntries = new LinkedHashMap<>(0, 0.75f, true);

    private int mRedundantOpCount;

    /**
     * This cache uses a single background thread to evict entries.
     */
    private final ExecutorService mExecutorService = new ThreadPoolExecutor(0, 1,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private final Callable<Void> mCleanupCallable = new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            synchronized (DiskLruCache.this) {
                if (mJournalWriter != null) {
                    return null; // Closed
                }
                trimToSize();

            }
            return null;
        }
    };

    /**
     * To differentiate between old and current snapshots, each entry is given
     * a sequence number each time an edit is committed. A snapshot is stale if
     * its sequence number is not equal to tis entry's sequence number.
     */
    private long mNextSequenceNumber = 0;

    private DiskLruCache(File directory, int appVersion, int valueCount, long maxSize) {
        mDirectory = directory;
        mAppVersion = appVersion;
        mJournalFile = new File(directory, JOURNAL_FILE);
        mJournalFileTmp = new File(directory, JOURNAL_FILE_TMP);
        mValueCount = valueCount;
        mMaxSize = maxSize;
    }

    /**
     * Opens the cache in {@code directory}, creating a cache if none exists there.
     *
     * @param directory  a writable directory
     * @param appVersion app version
     * @param valueCount the number of values pre cache entry. Must be positive.
     * @param maxSize    the maximum number of bytes this cache should use to store.
     * @throws IOException if reading or writing the cache directory fails.
     */
    public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize)
            throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        }

        // prefer to pick up where we left off
        DiskLruCache cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
        if (cache.mJournalFile.exists()) {
            try {
                cache.readJournal();
                cache.processJournal();
                cache.mJournalWriter = new BufferedWriter(
                        new FileWriter(cache.mJournalFile, true), IO_BUFFER_SIZE);
                return cache;
            } catch (IOException journalIsCorrupt) {
                cache.delete();
            }
        }

        // create a new empty cache
        directory.mkdir();
        cache = new DiskLruCache(directory, appVersion, valueCount, maxSize);
        cache.rebuildJournal();
        return cache;
    }

    private void readJournal() throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(mJournalFile), IO_BUFFER_SIZE);
        try {
            String magic = Utils.readAsciiLine(in);
            String version = Utils.readAsciiLine(in);
            String appVersionString = Utils.readAsciiLine(in);
            String valueCountString = Utils.readAsciiLine(in);
            String blank = Utils.readAsciiLine(in);
            if (!MAGIC.equals(magic)
                    || !VERSION_1.equals(version)
                    || !Integer.toString(mAppVersion).equals(appVersionString)
                    || !Integer.toString(mValueCount).equals(valueCountString)
                    || !"".equals(blank)) {
                throw new IOException("unexpected journal header: ["
                        + magic + ", " + version + ", " + valueCountString + ", " + blank + "]");
            }

            while (true) {
                try {
                    readJournalLien(Utils.readAsciiLine(in));
                } catch (EOFException endOfJournal) {
                    break;
                }
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private void readJournalLien(String line) throws IOException {
        String[] parts = line.split(" ");
        if (parts.length < 2) {
            throw new IOException("unexpected journal line: " + line);
        }

        String state = parts[0];
        String key = parts[1];
        if (state.equals(REMOVE) && parts.length == 2) {
            mLruEntries.remove(key);
            return;
        }

        Entry entry = mLruEntries.get(key);
        if (entry == null) {
            entry = new Entry(key);
            mLruEntries.put(key, entry);
        }

        if (state.equals(CLEAN) && parts.length == 2 + mValueCount) {
            entry.mReadable = true;
            entry.mCurrentEditor = null;
            entry.setLengths(Utils.copyOfRange(parts, 2, parts.length));
        } else if (state.equals(DIRTY) && parts.length == 2) {
            entry.mCurrentEditor = new Editor(entry);
        } else if (state.equals(READ) && parts.length == 2) {
            // this work was already done by calling mLruEntries.get();
        } else {
            throw new IOException("unexpected journal line: " + line);
        }
    }

    /**
     * Computes the initial size and collects garbage as a part of opening the
     * cache. Dirty entries are assumed to be inconsistent and will be deleted.
     */
    private void processJournal() throws IOException {
        IOUtils.deleteFile(mJournalFileTmp);
        for (Iterator<Entry> i = mLruEntries.values().iterator(); i.hasNext(); ) {
            Entry entry = i.next();
            if (entry.mCurrentEditor == null) {
                for (int t = 0; t < mValueCount; t++) {
                    mSize += entry.mLengths[t];
                }
            } else {
                entry.mCurrentEditor = null;
                for (int t = 0; t < mValueCount; t++) {
                    IOUtils.deleteFile(entry.getCleanFile(t));
                    IOUtils.deleteFile(entry.getDirtyFile(t));
                }
            }
        }
    }

    /**
     * Creates a new journal that omits redundant information. This replaces the
     * current journal if it exists.
     */
    private synchronized void rebuildJournal() throws IOException {
        if (mJournalWriter != null) {
            mJournalWriter.close();
        }

        Writer writer = new BufferedWriter(new FileWriter(mJournalFileTmp), IO_BUFFER_SIZE);
        writer.write(MAGIC);
        writer.write("\n");
        writer.write(VERSION_1);
        writer.write("\n");
        writer.write(Integer.toString(mAppVersion));
        writer.write("\n");
        writer.write(Integer.toString(mValueCount));
        writer.write("\n");
        writer.write("\n");

        for (Entry entry : mLruEntries.values()) {
            if (entry.mCurrentEditor != null) {
                writer.write(DIRTY + ' ' + entry.mKey + '\n');
            } else {
                writer.write(CLEAN + ' ' + entry.mKey + entry.getLengths() + '\n');
            }
        }

        writer.close();
        mJournalFileTmp.renameTo(mJournalFile);
        mJournalWriter = new BufferedWriter(new FileWriter(mJournalFile, true), IO_BUFFER_SIZE);
    }

    /**
     * Returns a snapshot of entry named {@code key}, or null if it doesn't
     * exists is not currently readable. If a value is returned, it is moved to
     * the head of the LRU queue.
     */
    public synchronized Snapshot get(String key) throws IOException {
        checkNotClosed();
        validateKey(key);
        Entry entry = mLruEntries.get(key);
        if (entry == null) {
            return null;
        }

        if (!entry.mReadable) {
            return null;
        }

         /*
          * Open all streams eagerly to guarantee that we see a single published
          * snapshot. If we opened streams lazily then the streams could come
          * from different edits.
          */
        InputStream[] ins = new InputStream[mValueCount];
        try {
            for (int i = 0; i < mValueCount; i++) {
                ins[i] = new FileInputStream(entry.getCleanFile(i));
            }
        } catch (FileNotFoundException e) {
            // a file must have been deleted manually!
            return null;
        }

        mRedundantOpCount++;
        mJournalWriter.append(READ + ' ' + key + '\n');
        if (journalRebuildRequired()) {
            mExecutorService.submit(mCleanupCallable);
        }

        return new Snapshot(key, entry.mSequenceNumber, ins);
    }

    /**
     * Returns an editor for the entry named {@code key}, or null if another
     * edit is in progress.
     */
    public Editor edit(String key) throws IOException {
        return edit(key, ANY_SEQUENCE_NUMBER);
    }

    private synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
        checkNotClosed();
        validateKey(key);
        Entry entry = mLruEntries.get(key);
        if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER
                && (entry == null || entry.mSequenceNumber != expectedSequenceNumber)) {
            return null; // snapshot is stale
        }
        if (entry == null) {
            entry = new Entry(key);
            mLruEntries.put(key, entry);
        } else if (entry.mCurrentEditor != null) {
            return null; // another edit is in progress
        }

        Editor editor = new Editor(entry);
        entry.mCurrentEditor = editor;

        // flush the journal before creating files ot prevent file leaks.
        mJournalWriter.write(DIRTY + ' ' + key + '\n');
        mJournalWriter.flush();
        return editor;
    }

    /**
     * @return the directory where this cache shores its data.
     */
    public File getDirectory() {
        return mDirectory;
    }

    /**
     * @return the maximum number of bytes that this cache should use to store
     * its data.
     */
    public long maxSize() {
        return mMaxSize;
    }

    /**
     * @return the number of bytes currently being used to store the values in
     * its cache. This may be greater than the max size if a background
     * deletion is pending.
     */
    public synchronized long size() {
        return mSize;
    }

    private synchronized void completeEdit(Editor editor, boolean success) throws IOException {
        Entry entry = editor.mEntry;
        if (entry.mCurrentEditor != editor) {
            throw new IllegalStateException();
        }

        // if this edit is creating the entry for the first time, every index must have a value
        if (success && !entry.mReadable) {
            for (int i = 0; i < mValueCount; i++) {
                if (!entry.getCleanFile(i).exists()) {
                    editor.abort();
                    throw new IllegalStateException("edit didn't create file " + i);
                }
            }
        }

        for (int i = 0; i < mValueCount; i++) {
            File dirty = entry.getDirtyFile(i);
            if (success) {
                if (dirty.exists()) {
                    File clean = entry.getCleanFile(i);
                    dirty.renameTo(clean);
                    long oldLength = entry.mLengths[i];
                    long newLength = clean.length();
                    entry.mLengths[i] = newLength;
                    mSize = mSize - oldLength + newLength;
                }
            } else {
                IOUtils.deleteFile(dirty);
            }
        }

        mRedundantOpCount++;
        entry.mCurrentEditor = null;
        if (entry.mReadable | success) {
            entry.mReadable = true;
            mJournalWriter.write(CLEAN + ' ' + entry.mKey + entry.getLengths() + '\n');
            if (success) {
                entry.mSequenceNumber = mNextSequenceNumber++;
            }
        } else {
            mLruEntries.remove(entry.mKey);
            mJournalWriter.write(REMOVE + ' ' + entry.mKey + '\n');
        }

        if (mSize > mMaxSize || journalRebuildRequired()) {
            mExecutorService.submit(mCleanupCallable);
        }
    }

    /**
     * We only rebuild the journal when it will halve the size of journal
     * and eliminate at least 2000 ops.
     */
    private boolean journalRebuildRequired() {
        final int redundantOpCompactThreshold = 2000;
        return mRedundantOpCount >= redundantOpCompactThreshold
                && mRedundantOpCount >= mLruEntries.size();
    }

    /**
     * Drop the entry fro {@code key} if it exists and can be removed. Entries
     * actively being edited cannot be removed.
     */
    public synchronized boolean remove(String key) throws IOException {
        checkNotClosed();
        validateKey(key);
        Entry entry = mLruEntries.get(key);
        if (entry == null || entry.mCurrentEditor != null) {
            return false;
        }

        for (int i = 0; i < mValueCount; i++) {
            File file = entry.getCleanFile(i);
            if (!file.delete()) {
                throw new IOException("failed to delete " + file);
            }
            mSize -= entry.mLengths[i];
            entry.mLengths[i] = 0;
        }

        mRedundantOpCount++;
        mJournalWriter.append(REMOVE + ' ' + key + '\n');
        mLruEntries.remove(key);

        if (journalRebuildRequired()) {
            mExecutorService.submit(mCleanupCallable);
        }

        return true;
    }

    /**
     * @return true if this cache has been closed.
     */
    public boolean isClosed() {
        return mJournalWriter == null;
    }

    private void checkNotClosed() {
        if (mJournalWriter == null) {
            throw new IllegalArgumentException("cache is closed");
        }
    }

    /**
     * Closes this cache. Stored values will remain on the filesystem.
     */
    public synchronized void flush() throws IOException {
        if (mJournalWriter == null) {
            return; // already closed
        }
        for (Entry entry : new ArrayList<>(mLruEntries.values())) {
            if (entry.mCurrentEditor != null) {
                entry.mCurrentEditor.abort();
            }
        }
        trimToSize();
        ;
        mJournalWriter.close();
        mJournalWriter = null;
    }

    /**
     * Closes this cache. Stored values will remain on filesystem.
     */
    public synchronized void close() throws IOException {
        if (mJournalWriter == null) {
            return; // already closed
        }
        for (Entry entry : new ArrayList<>(mLruEntries.values())) {
            if (entry.mCurrentEditor != null) {
                entry.mCurrentEditor.abort();
            }
        }
        trimToSize();
        mJournalWriter.close();
        mJournalWriter = null;
    }

    private void trimToSize() throws IOException {
        while (mSize > mMaxSize) {
            final Map.Entry<String, Entry> toEvict = mLruEntries.entrySet().iterator().next();
            remove(toEvict.getKey());
        }
    }

    /**
     * Closes the cache and deletes all of its stored values. This will delete
     * all files in the cache directory including files that weren't created by
     * the cache.
     */
    public void delete() throws IOException {
        close();
        IOUtils.deleteFile(mDirectory);
    }

    private void validateKey(String key) {
        if (key.contains(" ") || key.contains("\n") || key.contains("\r")) {
            throw new IllegalArgumentException(
                    "keys must not contain spaces or newlines: \"" + key + "\"");
        }
    }

    private static String inputSteamToString(InputStream in) throws IOException {
        return IOUtils.readFully(new InputStreamReader(in, UTF_8));
    }

    public final class Snapshot implements Closeable {

        private final String mKey;

        private final long mSequenceNumber;

        private final InputStream[] mIns;

        public Snapshot(String key, long sequenceNumber, InputStream[] ins) {
            mKey = key;
            mSequenceNumber = sequenceNumber;
            mIns = ins;
        }

        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(mKey, mSequenceNumber);
        }

        public InputStream getInputSteam(int index) {
            return mIns[index];
        }

        public String getString(int index) throws IOException {
            return inputSteamToString(getInputSteam(index));
        }

        @Override
        public void close() throws IOException {
            for (InputStream in : mIns) {
                IOUtils.closeQuietly(in);
            }
        }
    }

    public final class Editor {

        private final Entry mEntry;

        private boolean mHasErrors;

        public Editor(Entry entry) {
            mEntry = entry;
        }

        /**
         * Returns an unbuffered input stream to read the last committed value,
         * or null if no value has been committed.
         */
        public InputStream newInputStream(int index) throws IOException {
            synchronized (DiskLruCache.this) {
                if (mEntry.mCurrentEditor != this) {
                    throw new IllegalStateException();
                }
                if (!mEntry.mReadable) {
                    return null;
                }
                return new FileInputStream(mEntry.getCleanFile(index));
            }
        }

        public OutputStream newOutputStream(int index) throws IOException {
            synchronized (DiskLruCache.this) {
                if (mEntry.mCurrentEditor != this) {
                    throw new IllegalStateException();
                }
                return new FaultHidingOutputStream(new FileOutputStream(mEntry.getDirtyFile(index)));
            }
        }

        /**
         * Sets the value at {@code index} to {@code value}.
         */
        public void set(int index, String value) throws IOException {
            Writer writer = null;
            try {
                writer = new OutputStreamWriter(newOutputStream(index), UTF_8);
                writer.write(value);
            } finally {
                IOUtils.closeQuietly(writer);
            }
        }

        /**
         * Set the value at {@code index} to {@code value}
         */
        public void commit() throws IOException {
            if (mHasErrors) {
                completeEdit(this, false);
                remove(mEntry.mKey); // the previous entry is stale
            } else {
                completeEdit(this, true);
            }
        }

        /**
         * Aborbs this edit. This releases the edit lock so another edit may be
         * started on the same key.
         */
        public void abort() throws IOException {
            completeEdit(this, false);
        }

        /**
         * Aborts this edit. This release the edit lock so another edit may be
         * started on the same key.
         */
        private class FaultHidingOutputStream extends FilterOutputStream {

            private FaultHidingOutputStream(OutputStream out) {
                super(out);
            }

            @Override
            public void write(int oneByte) {
                try {
                    out.write(oneByte);
                } catch (IOException e) {
                    mHasErrors = true;
                }
            }

            @Override
            public void write(byte[] buffer, int offset, int lenght) {
                try {
                    out.write(buffer, offset, lenght);
                } catch (IOException e) {
                    mHasErrors = true;
                }
            }

            @Override
            public void close() {
                try {
                    out.close();
                } catch (IOException e) {
                    mHasErrors = true;
                }
            }

            @Override
            public void flush() {
                try {
                    out.flush();
                } catch (IOException e) {
                    mHasErrors = true;
                }
            }
        }
    }

    private final class Entry {

        private final String mKey;

        /**
         * Lengths of this entry's files.
         */
        private final long[] mLengths;

        /**
         * True if this entry has ever been published
         */
        private boolean mReadable;

        /**
         * The ongoing edit or null if theis entry is not being edited.
         */
        private Editor mCurrentEditor;

        private long mSequenceNumber;

        private Entry(String key) {
            mKey = key;
            mLengths = new long[mValueCount];
        }

        public String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            for (long size : mLengths) {
                result.append(' ').append(size);
            }
            return result.toString();
        }

        /**
         * Set lengths using decimal numbers like "10123".
         */
        public void setLengths(String[] strings) throws IOException {
            if (strings.length != mValueCount) {
                throw invalidLengths(strings);
            }

            try {
                for (int i = 0; i < strings.length; i++) {
                    mLengths[i] = Long.parseLong(strings[i]);
                }
            } catch (NumberFormatException e) {
                throw invalidLengths(strings);
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }

        public File getCleanFile(int i) {
            return new File(mDirectory, mKey + "." + i);
        }

        public File getDirtyFile(int i) {
            return new File(mDirectory, mKey + "." + i + ".tmp");
        }
    }
}
