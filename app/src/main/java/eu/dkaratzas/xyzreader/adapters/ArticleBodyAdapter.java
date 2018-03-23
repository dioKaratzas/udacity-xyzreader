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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.dkaratzas.xyzreader.R;


public class ArticleBodyAdapter extends RecyclerView.Adapter<ArticleBodyAdapter.TextHolder> {
    private Context ctx;
    private List<String> body;

    public ArticleBodyAdapter(Context ctx, List<String> body) {
        this.ctx = ctx;
        this.body = body;
    }

    @NonNull
    @Override
    public ArticleBodyAdapter.TextHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.list_item_body_text;
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(layout, parent, false);

        return new TextHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleBodyAdapter.TextHolder holder, int position) {
        String paragraph = body.get(position);

        holder.bind(paragraph);
    }

    @Override
    public int getItemCount() {
        return body.size();
    }

    class TextHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_paragraph)
        TextView paragraphView;

        TextHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bind(String paragraph) {
            String text = Pattern.compile("\r\n").matcher(paragraph).replaceAll(" ");

            paragraphView.setText(Html.fromHtml(text));
            paragraphView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
