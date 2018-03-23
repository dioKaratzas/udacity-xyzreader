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

package eu.dkaratzas.xyzreader.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import eu.dkaratzas.xyzreader.R;
import eu.dkaratzas.xyzreader.data.models.Article;
import eu.dkaratzas.xyzreader.holders.ArticlesViewHolder;
import eu.dkaratzas.xyzreader.ui.activities.ArticleDetailActivity;
import eu.dkaratzas.xyzreader.ui.activities.ArticleListActivity;
import eu.dkaratzas.xyzreader.utils.GlideApp;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesViewHolder> {
    private static final String TAG = ArticlesAdapter.class.toString();

    private ArticleListActivity mParent;
    private List<Article> mArticles;


    public ArticlesAdapter(ArticleListActivity articleListActivity, List<Article> articles) {
        this.mParent = articleListActivity;
        this.mArticles = articles;
    }

    @Override
    public ArticlesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);

        return new ArticlesViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ArticlesViewHolder holder, final int position) {
        final Article article = mArticles.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mParent, ArticleDetailActivity.class);

                intent.putExtra(ArticleListActivity.INTENT_EXTRA_ARTICLE_ID, article.getId());

                if (!mParent.getResources().getBoolean(R.bool.isTablet)) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(mParent, holder.articleContainer, mParent.getString(R.string.article_transition));
                    mParent.startActivity(intent, options.toBundle());
                } else
                    mParent.startActivity(intent);
            }
        });

        holder.titleView.setText(article.getTitle());
        holder.dateView.setText(article.getFormattedDate());
        holder.authorView.setText(article.getAuthor());

        GlideApp.with(mParent.getApplicationContext())
                .asBitmap()
                .load(article.getThumb())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(holder.thumbnailView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                int articleContainerColor;
                                if (palette.getDarkVibrantSwatch() != null)
                                    articleContainerColor = palette.getDarkVibrantSwatch().getRgb();
                                else
                                    articleContainerColor = palette.getDarkMutedColor(ContextCompat.getColor(mParent.getApplicationContext(), R.color.colorPrimary));

                                int textViewColor;
                                if (palette.getLightMutedSwatch() != null)
                                    textViewColor = palette.getLightMutedSwatch().getRgb();
                                else
                                    textViewColor = palette.getLightVibrantColor(ContextCompat.getColor(mParent.getApplicationContext(), android.R.color.white));

                                holder.articleContainer.setBackgroundColor(articleContainerColor);
                                holder.titleView.setTextColor(textViewColor);
                                holder.authorView.setTextColor(textViewColor);
                                holder.dateView.setTextColor(textViewColor);
                            }
                        });
                    }
                });

        holder.thumbnailView.setAspectRatio((float) article.getAspectRatio());
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

}
