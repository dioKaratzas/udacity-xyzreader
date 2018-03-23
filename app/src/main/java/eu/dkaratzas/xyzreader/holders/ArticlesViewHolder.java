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

package eu.dkaratzas.xyzreader.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import eu.dkaratzas.xyzreader.R;
import eu.dkaratzas.xyzreader.utils.DynamicHeightNetworkImageView;


public class ArticlesViewHolder extends RecyclerView.ViewHolder {
    public DynamicHeightNetworkImageView thumbnailView;
    public View articleContainer;
    public TextView titleView;
    public TextView authorView;
    public TextView dateView;
    public CardView itemCardContainer;

    public ArticlesViewHolder(View view) {
        super(view);
        articleContainer = view.findViewById(R.id.article_container);
        thumbnailView = view.findViewById(R.id.thumbnail);
        titleView = view.findViewById(R.id.article_title);
        authorView = view.findViewById(R.id.article_author);
        dateView = view.findViewById(R.id.article_date);
        itemCardContainer = view.findViewById(R.id.item_card_container);
    }

}
