package org.qpython.qpy.codeshare.pojo;


import java.io.Serializable;
import java.util.List;

/**
 * Gist detail bean
 * Created by Hmei on 2017-08-10.
 */

public class Gist {

    /**
     * author : String
     * id : String
     * title : String
     * describe : String
     * history : [{"data":"String","content":"String"},{"data":"String","content":"String"},{"data":"String","content":"String"}]
     * comment : [{"id":"String","from":"User name","re":"User name(nullable)","content":"String","data":"String"}]
     * bookmarker : [{"name":"User name","id":"User id"}]
     */

    private String author;
    private String avatar;
    private String id;
    private String title;
    private String describe;
    private String date;

    private transient List<HistoryBean>    history;
    private transient List<CommentBean>    comment;
    private transient List<BookmarkerBean> bookmarker;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<HistoryBean> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryBean> history) {
        this.history = history;
    }

    public List<CommentBean> getComment() {
        return comment;
    }

    public void setComment(List<CommentBean> comment) {
        this.comment = comment;
    }

    public List<BookmarkerBean> getBookmarker() {
        return bookmarker;
    }

    public void setBookmarker(List<BookmarkerBean> bookmarker) {
        this.bookmarker = bookmarker;
    }

    public String getHistoryCount() {
        return history == null ? "0" : history.size() + "";
    }

    public String getCommentCount() {
        return comment == null ? "0" : "" + comment.size();
    }

    public String getBookmarkerCount() {
        return bookmarker == null ? "0" : "" + bookmarker.size();
    }

    public String getLastCommitCode() {
        return history == null ? "" : history.get(history.size() - 1).getContent();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static class HistoryBean {
        /**
         * data : String
         * content : String
         * massage : String
         */

        private String historyId;
        private String data;
        private String content;
        private String massage;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMassage() {
            return massage;
        }

        public void setMassage(String massage) {
            this.massage = massage;
        }

        public String getHistoryId() {
            return historyId;
        }

        public void setHistoryId(String historyId) {
            this.historyId = historyId;
        }
    }

    public static class CommentBean implements Serializable{
        /**
         * id : String
         * avatar : http://www.qqu.cc/uploads/allimg/161214/1-1612141141550-L.jpg
         * from : User name
         * re : User name(nullable)
         * re_content : String
         * from_content : String
         * data : String
         */

        private String id;
        private String avatar;
        private String from;
        private String re;
        private String re_content;
        private String from_content;
        private String data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getRe() {
            return re;
        }

        public void setRe(String re) {
            this.re = re;
        }

        public String getRe_content() {
            return re_content;
        }

        public void setRe_content(String re_content) {
            this.re_content = re_content;
        }

        public String getFrom_content() {
            return from_content;
        }

        public void setFrom_content(String from_content) {
            this.from_content = from_content;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class BookmarkerBean {
        /**
         * name : User name
         * id : User id
         */

        private String name;
        private String id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

//    public void addHistory(HistoryBean historyBean) {
//        history.add(historyBean);
//    }
//
//    public void addComment(CommentBean commentBean) {
//        comment.add(commentBean);
//    }
//
//    public void addBookmarker(BookmarkerBean bookmarkerBean) {
//        bookmarker.add(bookmarkerBean);
//    }
}
