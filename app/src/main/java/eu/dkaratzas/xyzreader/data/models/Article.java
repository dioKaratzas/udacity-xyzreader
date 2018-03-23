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

package eu.dkaratzas.xyzreader.data.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;

import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Entity(tableName = "articles")
public class Article implements Parcelable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "aspect_ratio")
    private double aspectRatio;
    @ColumnInfo(name = "thumb")
    private String thumb;
    @ColumnInfo(name = "author")
    private String author;
    @ColumnInfo(name = "photo")
    private String photo;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "body")
    private String body;
    @ColumnInfo(name = "published_date")
    private String publishedDate;

    @Ignore
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    @Ignore
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    @Ignore
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    public Article(int id, Double aspectRatio, String thumb, String author, String photo, String title, String body, String publishedDate) {
        this.id = id;
        this.aspectRatio = aspectRatio;
        this.thumb = thumb;
        this.author = author;
        this.photo = photo;
        this.title = title;
        this.body = body;
        this.publishedDate = publishedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<String> getSplitBody() {
        String[] splitArray = body.split("\r\n\r\n");

        return new ArrayList<>(Arrays.asList(splitArray));
    }

    public Spanned getFormattedDate() {
        Date publishedDate = parsePublishedDate(getPublishedDate());
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            return Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString());
        } else {
            return Html.fromHtml(
                    outputFormat.format(publishedDate));
        }
    }

    private Date parsePublishedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Logger.e(ex.getMessage());
            Logger.e("passing today's date");
            return new Date();
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeDouble(this.aspectRatio);
        dest.writeString(this.thumb);
        dest.writeString(this.author);
        dest.writeString(this.photo);
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.publishedDate);
    }

    protected Article(Parcel in) {
        this.id = in.readInt();
        this.aspectRatio = in.readDouble();
        this.thumb = in.readString();
        this.author = in.readString();
        this.photo = in.readString();
        this.title = in.readString();
        this.body = in.readString();
        this.publishedDate = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}