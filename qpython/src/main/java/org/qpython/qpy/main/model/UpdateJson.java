package org.qpython.qpy.main.model;

import java.util.List;

public class UpdateJson {
    private AppBean app;

    private List<PluginsBean> plugins;

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }

    public List<PluginsBean> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<PluginsBean> plugins) {
        this.plugins = plugins;
    }

    public static class AppBean {
        private String link;
        private int ver;
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

    public static class PluginsBean {
        private String src;
        private int ver;
        private String plugin;
        private String link;
        private String dst;
        private String title;
        private String ver_desc;
        private String desc;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public int getVer() {
            return ver;
        }

        public void setVer(int ver) {
            this.ver = ver;
        }

        public String getPlugin() {
            return plugin;
        }

        public void setPlugin(String plugin) {
            this.plugin = plugin;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getDst() {
            return dst;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVer_desc() {
            return ver_desc;
        }

        public void setVer_desc(String ver_desc) {
            this.ver_desc = ver_desc;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
}
