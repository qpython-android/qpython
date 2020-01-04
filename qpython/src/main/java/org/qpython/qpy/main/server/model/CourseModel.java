package org.qpython.qpy.main.server.model;

import org.qpython.qpy.codeshare.pojo.Gist;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hmei
 * on 2017-06-29.
 */

public class CourseModel implements Serializable {

    /**
     * src : http://qpython.org
     * rdate : 2017-06-15
     * auth : River
     * link : http://edu.qpython.org/qpython-quick-start/index.html
     * smodule : qpython-quick-start
     * description : QPython Quick Start
     * title : QPython Quick Start
     * ver : 1.0.0
     * downloads : 10,000+
     * cat : featured
     * level : 1
     * logo : http://www.qpython.org/images/bestpython.png
     * open : 1
     * courses : [{"rdate":"2017-06-15","link":"http://edu.qpython.org/qpython-quick-start/principles.html","smodule":"qpython-quick-start-principles","description":"Why QPython","title":"QPython Principles","downloads":"10,000+"},{"rdate":"2017-06-15","link":"http://edu.qpython.org/qpython-quick-start/terminal-howto.html","smodule":"qpython-quick-start-terminal-howto","description":"Explore to use QPython's Terminal","title":"Terminal howto","downloads":"10,000+"},{"rdate":"2017-06-15","link":"http://edu.qpython.org/qpython-quick-start/terminal-editor.html","smodule":"qpython-quick-start-editor-howto","description":"Develop with QPython's editor ","title":"Editor howto","downloads":"10,000+"},{"rdate":"2017-06-15","link":"http://edu.qpython.org/qpython-quick-start/qpypi-howto.html","smodule":"qpython-quick-start-qpypi-howto","description":"Install packages & programs on QPython","title":"QPYPI howto","downloads":"10,000+"},{"rdate":"2017-06-15","link":"http://edu.qpython.org/qpython-quick-start/community-howto.html","smodule":"qpython-quick-start-community-howto","description":"Join a QPython community","title":"Community howto","downloads":"10,000+"}]
     * author : River
     */

    private String                 src;
    private String                 rdate;
    private String                 auth;
    private String                 link;
    private String                 smodule;
    private String                 description;
    private String                 title;
    private String                 ver;
    private String                 downloads;
    private String                 cat;
    private int                    level;
    private String                 logo;
    private int                    open;
    private String                 author;
    private List<CoursesBean>      courses;
    private int                    crowdfunding;
    private String                 auth_desc;
    private String                 auth_avatar;
    private int                    funding_process;
    private List<FundingUserBean>  funding_user;
    private List<Gist.CommentBean> comment;
    private String                 type; // basic/web/aipy/arvr/database

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    private String explanation;

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getRdate() {
        return rdate;
    }

    public void setRdate(String rdate) {
        this.rdate = rdate;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSmodule() {
        return smodule;
    }

    public void setSmodule(String smodule) {
        this.smodule = smodule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<CoursesBean> getCourses() {
        return courses;
    }

    public void setCourses(List<CoursesBean> courses) {
        this.courses = courses;
    }

    public int getCrowdfunding() {
        return crowdfunding;
    }

    public void setCrowdfunding(int crowdfunding) {
        this.crowdfunding = crowdfunding;
    }

    public String getAuth_desc() {
        return auth_desc;
    }

    public void setAuth_desc(String auth_desc) {
        this.auth_desc = auth_desc;
    }

    public String getAuth_avatar() {
        return auth_avatar == null ? "" : auth_avatar;
    }

    public void setAuth_avatar(String auth_avatar) {
        this.auth_avatar = auth_avatar;
    }

    public int getFunding_process() {
        return funding_process;
    }

    public void setFunding_process(int funding_process) {
        this.funding_process = funding_process;
    }

    public List<FundingUserBean> getFunding_user() {
        return funding_user;
    }

    public void setFunding_user(List<FundingUserBean> funding_user) {
        this.funding_user = funding_user;
    }

    public List<Gist.CommentBean> getComment() {
        return comment;
    }

    public void setComment(List<Gist.CommentBean> comment) {
        this.comment = comment;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class FundingUserBean implements Serializable {
        /**
         * avatar : data
         * name : Zoe Saldana
         */

        private String avatar;
        private String name;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class CoursesBean implements Serializable {
        /**
         * rdate : 2017-06-15
         * link : http://edu.qpython.org/qpython-quick-start/principles.html
         * smodule : qpython-quick-start-principles
         * description : Why QPython
         * title : QPython Principles
         * downloads : 10,000+
         */

        private String rdate;
        private String link;
        private String smodule;
        private String description;
        private String title;
        private String downloads;

        public String getRdate() {
            return rdate;
        }

        public void setRdate(String rdate) {
            this.rdate = rdate;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getSmodule() {
            return smodule;
        }

        public void setSmodule(String smodule) {
            this.smodule = smodule;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDownloads() {
            return downloads;
        }

        public void setDownloads(String downloads) {
            this.downloads = downloads;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
