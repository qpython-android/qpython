package org.qpython.qpy.main.server.model;

import java.util.List;

/**
 * Created by Hmei on 2017-06-27.
 */

public class UpdateModel {

    /**
     * app : {"link":"http://www.qpython.org","ver":200,"ver_plugin":"0","ver_desc":"Newest version you should upgrade","ver_name":"2.0.0"}
     * plugins : []
     */

    private AppBean app;
    private List<?> plugins;

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }

    public List<?> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<?> plugins) {
        this.plugins = plugins;
    }

    public static class AppBean {
        /**
         * link : http://www.qpython.org
         * ver : 200
         * ver_plugin : 0
         * ver_desc : Newest version you should upgrade
         * ver_name : 2.0.0
         */

        private String link;
        private int    ver;
        private String ver_plugin;
        private String ver_desc;
        private String ver_name;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getVer() {
            return ver;
        }

        public void setVer(int ver) {
            this.ver = ver;
        }

        public String getVer_plugin() {
            return ver_plugin;
        }

        public void setVer_plugin(String ver_plugin) {
            this.ver_plugin = ver_plugin;
        }

        public String getVer_desc() {
            return ver_desc;
        }

        public void setVer_desc(String ver_desc) {
            this.ver_desc = ver_desc;
        }

        public String getVer_name() {
            return ver_name;
        }

        public void setVer_name(String ver_name) {
            this.ver_name = ver_name;
        }
    }
}
