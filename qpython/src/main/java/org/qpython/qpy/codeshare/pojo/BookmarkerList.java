package org.qpython.qpy.codeshare.pojo;

import java.util.List;

/**
 * Created by Hmei on 2017-08-10.
 */

public class BookmarkerList {

    /**
     * id :
     * name :
     * bookmaker : [{"id":"","date":"","comment_cout":100,"bookmaker_cout":200,"titile":""},{"id":"","date":"","comment_cout":100,"bookmaker_cout":200,"titile":""}]
     */

    private String id;
    private String              name;
    private List<BookmakerBean> bookmaker;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BookmakerBean> getBookmaker() {
        return bookmaker;
    }

    public void setBookmaker(List<BookmakerBean> bookmaker) {
        this.bookmaker = bookmaker;
    }

    public static class BookmakerBean {
        /**
         * id :
         * date :
         * comment_cout : 100
         * bookmaker_cout : 200
         * titile :
         */

        private String id;
        private String date;
        private int    comment_cout;
        private int    bookmaker_cout;
        private String titile;

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

        public int getComment_cout() {
            return comment_cout;
        }

        public void setComment_cout(int comment_cout) {
            this.comment_cout = comment_cout;
        }

        public int getBookmaker_cout() {
            return bookmaker_cout;
        }

        public void setBookmaker_cout(int bookmaker_cout) {
            this.bookmaker_cout = bookmaker_cout;
        }

        public String getTitile() {
            return titile;
        }

        public void setTitile(String titile) {
            this.titile = titile;
        }
    }
}
