/*
 * Copyright 2016 jeasonlzy(廖子尧)
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
package com.lzy.okgo.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.lzy.okgo.model.Progress;

import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/8
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DownloadManager extends BaseDao<Progress> {

    private DownloadManager() {
        super(new DBHelper());
    }

    public static DownloadManager getInstance() {
        return DownloadManagerHolder.instance;
    }

    @Override
    public Progress parseCursorToBean(Cursor cursor) {
        return Progress.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(Progress progress) {
        return Progress.buildContentValues(progress);
    }

    @Override
    public String getTableName() {
        return DBHelper.TABLE_DOWNLOAD;
    }

    @Override
    public void unInit() {
    }

    /** 获取下载任务 */
    public Progress get(String tag) {
        return queryOne(Progress.TAG + "=?", new String[]{tag});
    }

    /** 移除下载任务 */
    public void delete(String taskKey) {
        delete(Progress.TAG + "=?", new String[]{taskKey});
    }

    /** 更新下载任务 */
    public boolean update(Progress progress) {
        return update(progress, Progress.TAG + "=?", new String[]{progress.tag});
    }

    /** 更新下载任务 */
    public boolean update(ContentValues contentValues, String tag) {
        return update(contentValues, Progress.TAG + "=?", new String[]{tag});
    }

    /** 获取所有下载信息 */
    public List<Progress> getAll() {
        return query(null, null, null, null, null, Progress.DATE + " ASC", null);
    }

    /** 获取所有下载信息 */
    public List<Progress> getFinished() {
        return query(null, "status=?", new String[]{Progress.FINISH + ""}, null, null, Progress.DATE + " ASC", null);
    }

    /** 获取所有下载信息 */
    public List<Progress> getDownloading() {
        return query(null, "status not in(?)", new String[]{Progress.FINISH + ""}, null, null, Progress.DATE + " ASC", null);
    }

    /** 清空下载任务 */
    public boolean clear() {
        return deleteAll();
    }

    private static class DownloadManagerHolder {
        private static final DownloadManager instance = new DownloadManager();
    }
}
