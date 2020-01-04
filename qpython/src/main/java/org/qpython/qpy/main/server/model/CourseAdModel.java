package org.qpython.qpy.main.server.model;

import java.util.List;

/**
 * Created by Hmei on 2017-08-01.
 */

public class CourseAdModel {

    /**
     * qpy : {"website":"http://qpython.org","feedback":"support@qpython.org","ext2":{"adx_setting":"pa|marquee","adx_splash":"pa|full"},"ext_ad":{"featured":[{"ad_man":"qpythonBLfb","ad_market":"Global","ad_link":"http://www.facebook.com/qpython","ad_code":"org.qpython.qpy.fb","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-facebook.png","ad_logo":"","adLink_id":"9"},{"ad_man":"XSocksBL","ad_market":"Global","ad_link":"http://xsvpn.com/","ad_code":"com.quseit.xsocks2","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-xsocks_bSO7gxh.png","ad_logo":"","adLink_id":"10"}],"full":[{"ad_man":"QPython","ad_market":"Global","ad_link":"http://www.qpython.org","ad_code":"org.qpython.qpy","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-748x1062.png","ad_logo":"","adLink_id":"6"}],"marquee":[{"ad_man":"qpythonBLfb","ad_market":"Global","ad_link":"http://www.facebook.com/qpython","ad_code":"org.qpython.qpy.fb","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-facebook.png","ad_logo":"","adLink_id":"9"},{"ad_man":"QPythonBLtw","ad_market":"Global","ad_link":"http://twitter.com/qpython","ad_code":"org.qpython.qpy.tw","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-twitter.png","ad_logo":"","adLink_id":"11"}]}}
     */

    private QpyBean qpy;

    public QpyBean getQpy() {
        return qpy;
    }

    public void setQpy(QpyBean qpy) {
        this.qpy = qpy;
    }

    public static class QpyBean {
        /**
         * website : http://qpython.org
         * feedback : support@qpython.org
         * ext2 : {"adx_setting":"pa|marquee","adx_splash":"pa|full"}
         * ext_ad : {"featured":[{"ad_man":"qpythonBLfb","ad_market":"Global","ad_link":"http://www.facebook.com/qpython","ad_code":"org.qpython.qpy.fb","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-facebook.png","ad_logo":"","adLink_id":"9"},{"ad_man":"XSocksBL","ad_market":"Global","ad_link":"http://xsvpn.com/","ad_code":"com.quseit.xsocks2","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-xsocks_bSO7gxh.png","ad_logo":"","adLink_id":"10"}],"full":[{"ad_man":"QPython","ad_market":"Global","ad_link":"http://www.qpython.org","ad_code":"org.qpython.qpy","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-748x1062.png","ad_logo":"","adLink_id":"6"}],"marquee":[{"ad_man":"qpythonBLfb","ad_market":"Global","ad_link":"http://www.facebook.com/qpython","ad_code":"org.qpython.qpy.fb","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-facebook.png","ad_logo":"","adLink_id":"9"},{"ad_man":"QPythonBLtw","ad_market":"Global","ad_link":"http://twitter.com/qpython","ad_code":"org.qpython.qpy.tw","ad_point":"10","ad_img":"http://apu.quseit.com/static/adimg/qpy-twitter.png","ad_logo":"","adLink_id":"11"}]}
         */

        private String    website;
        private String    feedback;
        private Ext2Bean  ext2;
        private ExtAdBean ext_ad;
        private String    course_open;

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }

        public Ext2Bean getExt2() {
            return ext2;
        }

        public void setExt2(Ext2Bean ext2) {
            this.ext2 = ext2;
        }

        public ExtAdBean getExt_ad() {
            return ext_ad;
        }

        public void setExt_ad(ExtAdBean ext_ad) {
            this.ext_ad = ext_ad;
        }

        public String getCourse_open() {
            return course_open == null ? "0" : course_open;
        }

        public void setCourse_open(String course_open) {
            this.course_open = course_open;
        }


        public static class Ext2Bean {
            /**
             * adx_setting : pa|marquee
             * adx_splash : pa|full
             */

            private String adx_setting;
            private String adx_splash;

            public String getAdx_setting() {
                return adx_setting;
            }

            public void setAdx_setting(String adx_setting) {
                this.adx_setting = adx_setting;
            }

            public String getAdx_splash() {
                return adx_splash;
            }

            public void setAdx_splash(String adx_splash) {
                this.adx_splash = adx_splash;
            }
        }

        public static class ExtAdBean {
            private List<FeaturedBean> featured;
            private List<FullBean>     full;
            private List<MarqueeBean>  marquee;
            private List<FeaturedBean> gistshare;

            public List<FeaturedBean> getFeatured() {
                return featured;
            }

            public void setFeatured(List<FeaturedBean> featured) {
                this.featured = featured;
            }

            public List<FullBean> getFull() {
                return full;
            }

            public void setFull(List<FullBean> full) {
                this.full = full;
            }

            public List<MarqueeBean> getMarquee() {
                return marquee;
            }

            public void setMarquee(List<MarqueeBean> marquee) {
                this.marquee = marquee;
            }

            public List<FeaturedBean> getGistshare() {
                return gistshare;
            }

            public void setGistshare(List<FeaturedBean> gistshare) {
                this.gistshare = gistshare;
            }

            public static class FeaturedBean {
                /**
                 * ad_man : qpythonBLfb
                 * ad_market : Global
                 * ad_link : http://www.facebook.com/qpython
                 * ad_code : org.qpython.qpy.fb
                 * ad_point : 10
                 * ad_img : http://apu.quseit.com/static/adimg/qpy-facebook.png
                 * ad_logo :
                 * adLink_id : 9
                 */

                private String ad_man;
                private String ad_market;
                private String ad_link;
                private String ad_code;
                private String ad_point;
                private String ad_img;
                private String ad_logo;
                private String adLink_id;

                public String getAd_man() {
                    return ad_man;
                }

                public void setAd_man(String ad_man) {
                    this.ad_man = ad_man;
                }

                public String getAd_market() {
                    return ad_market;
                }

                public void setAd_market(String ad_market) {
                    this.ad_market = ad_market;
                }

                public String getAd_link() {
                    return ad_link;
                }

                public void setAd_link(String ad_link) {
                    this.ad_link = ad_link;
                }

                public String getAd_code() {
                    return ad_code;
                }

                public void setAd_code(String ad_code) {
                    this.ad_code = ad_code;
                }

                public String getAd_point() {
                    return ad_point;
                }

                public void setAd_point(String ad_point) {
                    this.ad_point = ad_point;
                }

                public String getAd_img() {
                    return ad_img;
                }

                public void setAd_img(String ad_img) {
                    this.ad_img = ad_img;
                }

                public String getAd_logo() {
                    return ad_logo;
                }

                public void setAd_logo(String ad_logo) {
                    this.ad_logo = ad_logo;
                }

                public String getAdLink_id() {
                    return adLink_id;
                }

                public void setAdLink_id(String adLink_id) {
                    this.adLink_id = adLink_id;
                }
            }

            public static class FullBean {
                /**
                 * ad_man : QPython
                 * ad_market : Global
                 * ad_link : http://www.qpython.org
                 * ad_code : org.qpython.qpy
                 * ad_point : 10
                 * ad_img : http://apu.quseit.com/static/adimg/qpy-748x1062.png
                 * ad_logo :
                 * adLink_id : 6
                 */

                private String ad_man;
                private String ad_market;
                private String ad_link;
                private String ad_code;
                private String ad_point;
                private String ad_img;
                private String ad_logo;
                private String adLink_id;

                public String getAd_man() {
                    return ad_man;
                }

                public void setAd_man(String ad_man) {
                    this.ad_man = ad_man;
                }

                public String getAd_market() {
                    return ad_market;
                }

                public void setAd_market(String ad_market) {
                    this.ad_market = ad_market;
                }

                public String getAd_link() {
                    return ad_link;
                }

                public void setAd_link(String ad_link) {
                    this.ad_link = ad_link;
                }

                public String getAd_code() {
                    return ad_code;
                }

                public void setAd_code(String ad_code) {
                    this.ad_code = ad_code;
                }

                public String getAd_point() {
                    return ad_point;
                }

                public void setAd_point(String ad_point) {
                    this.ad_point = ad_point;
                }

                public String getAd_img() {
                    return ad_img;
                }

                public void setAd_img(String ad_img) {
                    this.ad_img = ad_img;
                }

                public String getAd_logo() {
                    return ad_logo;
                }

                public void setAd_logo(String ad_logo) {
                    this.ad_logo = ad_logo;
                }

                public String getAdLink_id() {
                    return adLink_id;
                }

                public void setAdLink_id(String adLink_id) {
                    this.adLink_id = adLink_id;
                }
            }

            public static class MarqueeBean {
                /**
                 * ad_man : qpythonBLfb
                 * ad_market : Global
                 * ad_link : http://www.facebook.com/qpython
                 * ad_code : org.qpython.qpy.fb
                 * ad_point : 10
                 * ad_img : http://apu.quseit.com/static/adimg/qpy-facebook.png
                 * ad_logo :
                 * adLink_id : 9
                 */

                private String ad_man;
                private String ad_market;
                private String ad_link;
                private String ad_code;
                private String ad_point;
                private String ad_img;
                private String ad_logo;
                private String adLink_id;

                public String getAd_man() {
                    return ad_man;
                }

                public void setAd_man(String ad_man) {
                    this.ad_man = ad_man;
                }

                public String getAd_market() {
                    return ad_market;
                }

                public void setAd_market(String ad_market) {
                    this.ad_market = ad_market;
                }

                public String getAd_link() {
                    return ad_link;
                }

                public void setAd_link(String ad_link) {
                    this.ad_link = ad_link;
                }

                public String getAd_code() {
                    return ad_code;
                }

                public void setAd_code(String ad_code) {
                    this.ad_code = ad_code;
                }

                public String getAd_point() {
                    return ad_point;
                }

                public void setAd_point(String ad_point) {
                    this.ad_point = ad_point;
                }

                public String getAd_img() {
                    return ad_img;
                }

                public void setAd_img(String ad_img) {
                    this.ad_img = ad_img;
                }

                public String getAd_logo() {
                    return ad_logo;
                }

                public void setAd_logo(String ad_logo) {
                    this.ad_logo = ad_logo;
                }

                public String getAdLink_id() {
                    return adLink_id;
                }

                public void setAdLink_id(String adLink_id) {
                    this.adLink_id = adLink_id;
                }
            }
        }
    }
}
