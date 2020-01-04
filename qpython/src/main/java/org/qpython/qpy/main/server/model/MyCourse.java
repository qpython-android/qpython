package org.qpython.qpy.main.server.model;

import java.util.List;

/**
 * Created by Hmei
 * 1/22/18.
 */

public class MyCourse {
    /**
     * errorno : 0
     * data : [{"info":{"src":"http://qpython.org","ver":"1.0.0","description":"Tensorflow Programming","crowdfunding":1,"title":"Tensorflow Programming","open":1,"downloads":"1,000+","level":1,"cat":"new","rdate":"2017-09-15","link":"http://edu.qpython.org/aipy-tensorflow/index.html","logo":"http://edu.qpython.org/static/course-tensorflow.png","funding_process":69,"smodule":"aipy-tensorflow"},"list":[{"gd":"course_0.99","created":"2017122712"}],"type":"notready","course":"aipy-tensorflow-index"},{"info":{"description":"How to start QPython","title":"QPython Quick Start","open":1,"downloads":"10,000+","level":1,"rdate":"2017-06-15","link":"http://edu.qpython.org/qpython-quick-start/index.html","type":"free","logo":"http://edu.qpython.org/static/course-qpython-quick-start.png","smodule":"qpython-quick-start"},"list":[{"gd":"course_free","created":"2018021109"}],"type":"free","course":"qpython-quick-start"}]
     */

    private int errorno;
    private List<DataBean> data;

    public int getErrorno() {
        return errorno;
    }

    public void setErrorno(int errorno) {
        this.errorno = errorno;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * info : {"src":"http://qpython.org","ver":"1.0.0","description":"Tensorflow Programming","crowdfunding":1,"title":"Tensorflow Programming","open":1,"downloads":"1,000+","level":1,"cat":"new","rdate":"2017-09-15","link":"http://edu.qpython.org/aipy-tensorflow/index.html","logo":"http://edu.qpython.org/static/course-tensorflow.png","funding_process":69,"smodule":"aipy-tensorflow"}
         * list : [{"gd":"course_0.99","created":"2017122712"}]
         * type : notready
         * course : aipy-tensorflow-index
         */

        private InfoBean info;
        private String         course;
        private String type; // free/crowdfunding/notready
        private List<ListBean> list;

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCourse() {
            return course;
        }

        public void setCourse(String course) {
            this.course = course;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class InfoBean {
            /**
             * src : http://qpython.org
             * ver : 1.0.0
             * description : Tensorflow Programming
             * crowdfunding : 1
             * title : Tensorflow Programming
             * open : 1
             * downloads : 1,000+
             * level : 1
             * cat : new
             * rdate : 2017-09-15
             * link : http://edu.qpython.org/aipy-tensorflow/index.html
             * logo : http://edu.qpython.org/static/course-tensorflow.png
             * funding_process : 69
             * smodule : aipy-tensorflow
             */

            private String src;
            private String ver;
            private String description;
            private int    crowdfunding;
            private String title;
            private int    open;
            private String downloads;
            private int    level;
            private String cat;
            private String rdate;
            private String link;
            private String logo;
            private int    funding_process;
            private String smodule;
            private String auth;

            public String getSrc() {
                return src;
            }

            public void setSrc(String src) {
                this.src = src;
            }

            public String getVer() {
                return ver;
            }

            public void setVer(String ver) {
                this.ver = ver;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public int getCrowdfunding() {
                return crowdfunding;
            }

            public void setCrowdfunding(int crowdfunding) {
                this.crowdfunding = crowdfunding;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getOpen() {
                return open;
            }

            public void setOpen(int open) {
                this.open = open;
            }

            public String getDownloads() {
                return downloads;
            }

            public void setDownloads(String downloads) {
                this.downloads = downloads;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public String getCat() {
                return cat;
            }

            public void setCat(String cat) {
                this.cat = cat;
            }

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

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public int getFunding_process() {
                return funding_process;
            }

            public void setFunding_process(int funding_process) {
                this.funding_process = funding_process;
            }

            public String getSmodule() {
                return smodule;
            }

            public void setSmodule(String smodule) {
                this.smodule = smodule;
            }

            public String getAuth() {
                return auth;
            }

            public void setAuth(String auth) {
                this.auth = auth;
            }
        }

        public static class ListBean {
            /**
             * gd : course_0.99
             * created : 2017122712
             */

            private String gd;
            private String created;

            public String getGd() {
                return gd;
            }

            public void setGd(String gd) {
                this.gd = gd;
            }

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }
        }
    }


//
//    /**
//     * errorno : 0
//     * data : [{"info":{"src":"http://qpython.org","ver":"1.0.0","description":"Python Basic","title":"Python Basic","open":0,"downloads":"1,000+","level":1,"cat":"new","rdate":"2017-09-15","link":"http://edu.qpython.org/python-base/index.html","logo":"http://edu.qpython.org/static/course-python-basic.png","smodule":"python-base"},"list":[{"gd":"course_4.99","created":"2018011017"},{"gd":"course_3.99","created":"2018011017"},{"gd":"course_2.99","created":"2018011017"},{"gd":"course_1.99","created":"2018011017"},{"gd":"course_0.99","created":"2018011017"}],"type":"course","course":"python-base-index"}]
//     */
//
//    private int errorno;
//    private List<DataBean> data;
//
//    public int getErrorno() {
//        return errorno;
//    }
//
//    public void setErrorno(int errorno) {
//        this.errorno = errorno;
//    }
//
//    public List<DataBean> getData() {
//        return data;
//    }
//
//    public void setData(List<DataBean> data) {
//        this.data = data;
//    }
//
//    public static class DataBean {
//        /**
//         * info : {"src":"http://qpython.org","ver":"1.0.0","description":"Python Basic","title":"Python Basic","open":0,"downloads":"1,000+","level":1,"cat":"new","rdate":"2017-09-15","link":"http://edu.qpython.org/python-base/index.html","logo":"http://edu.qpython.org/static/course-python-basic.png","smodule":"python-base"}
//         * list : [{"gd":"course_4.99","created":"2018011017"},{"gd":"course_3.99","created":"2018011017"},{"gd":"course_2.99","created":"2018011017"},{"gd":"course_1.99","created":"2018011017"},{"gd":"course_0.99","created":"2018011017"}]
//         * type : course
//         * course : python-base-index
//         */
//
//        private InfoBean info;
//        private String         type;
//        private String         course;
//        private List<ListBean> list;
//
//        public InfoBean getInfo() {
//            return info;
//        }
//
//        public void setInfo(InfoBean info) {
//            this.info = info;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public String getCourse() {
//            return course;
//        }
//
//        public void setCourse(String course) {
//            this.course = course;
//        }
//
//        public List<ListBean> getList() {
//            return list;
//        }
//
//        public void setList(List<ListBean> list) {
//            this.list = list;
//        }
//
//        public static class InfoBean extends CourseModel{
//            private String type; // free/crowdfunding/notready
//
//            @Override
//            public String getType() {
//                return type;
//            }
//
//            @Override
//            public void setType(String type) {
//                this.type = type;
//            }
//        }
//
//        public static class ListBean {
//            /**
//             * gd : course_4.99
//             * created : 2018011017
//             */
//
//            private String gd;
//            private String created;
//
//            public String getGd() {
//                return gd;
//            }
//
//            public void setGd(String gd) {
//                this.gd = gd;
//            }
//
//            public String getCreated() {
//                return created;
//            }
//
//            public void setCreated(String created) {
//                this.created = created;
//            }
//        }
//    }
}
