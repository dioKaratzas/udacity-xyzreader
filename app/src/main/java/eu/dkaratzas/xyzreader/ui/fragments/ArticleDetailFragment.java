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

package eu.dkaratzas.xyzreader.ui.fragments;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import eu.dkaratzas.xyzreader.R;
import eu.dkaratzas.xyzreader.adapters.ArticleBodyAdapter;
import eu.dkaratzas.xyzreader.data.models.Article;
import eu.dkaratzas.xyzreader.loaders.ArticleLoader;
import eu.dkaratzas.xyzreader.utils.Misc;


public class ArticleDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Article> {
    public static String EXTRA_ARTICLE = "article";
    public static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";

    View mParentLayout;

    @BindView(R.id.photo)
    ImageView mIvPhoto;
    @BindView(R.id.body_reycler)
    RecyclerView mRvBody;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @Nullable
    @BindView(R.id.collapsing_toolbar_detail)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.fab_share)
    FloatingActionButton fab;
    @Nullable
    @BindView(R.id.article_header_group)
    ConstraintLayout mGrpArticleHeader;
    @BindView(R.id.article_title_text)
    TextView mArticleTitleText;
    @BindView(R.id.article_author_date_text)
    TextView mArticleAuthorDateText;

    private Article mArticle;
    private Unbinder mUnbinder;
    private boolean mIsTablet;
    private boolean mHeaderAnimating = false;
    private RecyclerView.LayoutManager mLayoutManager;

    public static ArticleDetailFragment newInstance(int articleId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ARTICLE, articleId);

        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mParentLayout = getActivity().findViewById(android.R.id.content);

        // restore recycler view position
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            mLayoutManager.onRestoreInstanceState(savedRecyclerLayoutState);
            if (view.findViewById(R.id.app_bar_detail) != null)
                ((AppBarLayout) view.findViewById(R.id.app_bar_detail)).setExpanded(false);
        }

        if (getArguments() != null && getArguments().containsKey(EXTRA_ARTICLE)) {

            mUnbinder = ButterKnife.bind(this, view);

            mIsTablet = getResources().getBoolean(R.bool.isTablet);


            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setHasOptionsMenu(true);

            getActivity().getLoaderManager().initLoader(0, null, this);

        } else {
            Misc.showSnackBar(getActivity().getApplicationContext(), getView(), R.string.no_internet, true);
            getActivity().finish();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // save recycler view position
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRvBody.getLayoutManager().onSaveInstanceState());
    }

    private void setUpUi() {
        Glide.with(getActivity().getApplicationContext())
                .asBitmap()
                .load(mArticle.getPhoto())
                .into(new BitmapImageViewTarget(mIvPhoto) {
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
                                    articleContainerColor = palette.getDarkMutedColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorPrimary));

                                int textViewColor;
                                if (palette.getLightMutedSwatch() != null)
                                    textViewColor = palette.getLightMutedSwatch().getRgb();
                                else
                                    textViewColor = palette.getLightVibrantColor(ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.white));

                                mGrpArticleHeader.setBackgroundColor(articleContainerColor);
                                mArticleTitleText.setTextColor(textViewColor);
                                mArticleAuthorDateText.setTextColor(textViewColor);
                            }
                        });
                    }
                });

        if (mCollapsingToolbarLayout != null)
            mCollapsingToolbarLayout.setTitle(mArticle.getTitle());

        mToolbar.setTitle(mArticle.getTitle());


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareArticle(mArticle);
            }
        });
        slideUp(fab, 300);

        mArticleTitleText.setText(mArticle.getTitle());
        mArticleAuthorDateText.setText(getResources().getString(R.string.article_author_date, mArticle.getFormattedDate(), mArticle.getAuthor()));
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        List<String> article = mArticle.getSplitBody();

        // On tablet add extra lines because the article body 'recycler view' is behind the header container
        if (mIsTablet && mGrpArticleHeader != null) {
            article.add(0, "<br/> <br/> ");
        }
        ArticleBodyAdapter adapter = new ArticleBodyAdapter(getActivity().getApplicationContext(), article);
        mRvBody.setAdapter(adapter);

        if (mLayoutManager == null)
            mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        mRvBody.setLayoutManager(mLayoutManager);
        mRvBody.setHasFixedSize(true);


        // If on tablet mode apply animations to show/hide the article header
        if (mIsTablet && mGrpArticleHeader != null) {
            mRvBody.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 20)
                        hideHeader();
                    else if (dy < -20)
                        showHeader();
                }
            });
        }

        slideUp(mRvBody, 500);

        if (mIsTablet)
            showHeader();
    }

    public void slideUp(View view, int timeMilli) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                view.getHeight(),
                0);
        animate.setDuration(timeMilli);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void hideHeader() {
        if (!mHeaderAnimating && mGrpArticleHeader.getVisibility() == View.VISIBLE) {
            mGrpArticleHeader.animate()
                    .alpha(0f)
                    .translationY(-mGrpArticleHeader.getHeight())
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            mHeaderAnimating = true;
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mGrpArticleHeader.setVisibility(View.GONE);
                            mHeaderAnimating = false;
                        }
                    })
                    .start();
        }
    }

    public void showHeader() {
        if (!mHeaderAnimating && mGrpArticleHeader.getVisibility() == View.GONE) {
            mGrpArticleHeader.animate()
                    .alpha(1f)
                    .translationY(0)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            mGrpArticleHeader.setVisibility(View.VISIBLE);
                            mHeaderAnimating = true;
                        }
                    })
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mHeaderAnimating = false;
                        }
                    })
                    .start();
        }
    }

    private void shareArticle(Article article) {
        Intent chooser = ShareCompat.IntentBuilder.from(getActivity())
                .setType("text/plain")
                .setText(String.format("%s - %s", article.getTitle(), article.getAuthor()))
                .getIntent();

        Intent intent = Intent.createChooser(chooser, getString(R.string.action_share));

        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mIsTablet)
                getActivity().onBackPressed();
            else
                getActivity().supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Article> onCreateLoader(int id, @Nullable Bundle args) {
        return new ArticleLoader(getActivity().getApplicationContext(), getArguments().getInt(EXTRA_ARTICLE));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Article> loader, Article data) {
        mArticle = data;
        setUpUi();
        loader.reset();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Article> loader) {

    }
}