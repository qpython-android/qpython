package org.qpython.qpy.codeshare.pojo;

/**
 * Created by Hmei on 2017-08-10.
 */

public class GistBase {

    /**
     * id :
     * date :
     * comment_count : 100
     * bookmaker_count : 200
     * title :
     */

    private String id;
    private String author;
    private String date;
    private String title;
    private String avatar;
    private int    comment_count;
    private int    bookmaker_count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getBookmaker_count() {
        return bookmaker_count;
    }

    public void setBookmaker_count(int bookmaker_count) {
        this.bookmaker_count = bookmaker_count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
