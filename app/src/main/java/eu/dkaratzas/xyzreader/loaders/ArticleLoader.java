/*
 * Copyright 2018 Dionysios Karatzas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.dkaratzas.xyzreader.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

import eu.dkaratzas.xyzreader.data.AppDatabase;
import eu.dkaratzas.xyzreader.data.models.Article;


public class ArticleLoader extends AsyncTaskLoader<Article> {
    // Initialize a List of the Articles, this will hold all the task data
    private Article mTaskData = null;
    private int mId;

    public ArticleLoader(@NonNull Context context, int id) {
        super(context);
        mId = id;
    }

    // onStartLoading() is called when a loader first starts loading data
    @Override
    protected void onStartLoading() {
        if (mTaskData != null) {
            // Delivers any previously loaded data immediately
            deliverResult(mTaskData);
        } else {
            // Force a new load
            forceLoad();
        }
    }

    // loadInBackground() performs asynchronous loading of data
    @Override
    public Article loadInBackground() {
        // Will implement to load data

        // Query and load all task data in the background; sort by priority
        // [Hint] use a try/catch block to catch any errors in loading data

        try {
            return AppDatabase.getAppDatabase(getContext()).articleDao().findById(mId);

        } catch (Exception e) {
            Logger.e("Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

    // deliverResult sends the result of the load, a Cursor, to the registered listener
    public void deliverResult(Article data) {
        mTaskData = data;
        super.deliverResult(data);
    }
}
