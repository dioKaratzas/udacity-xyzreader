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
package eu.dkaratzas.xyzreader.sync;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.List;

import eu.dkaratzas.xyzreader.data.AppDatabase;
import eu.dkaratzas.xyzreader.data.models.Article;
import eu.dkaratzas.xyzreader.utils.NetworkUtils;

public class XYZReaderSyncTask {

    synchronized public static void syncArticles(Context context) {

        List<Article> articles = NetworkUtils.getArticles(context);
        if (articles != null && articles.size() > 0) {
            AppDatabase.getAppDatabase(context).articleDao().insert(articles);
        }
        Logger.d("Sync Articles");
    }

}