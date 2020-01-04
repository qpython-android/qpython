package org.qpython.qpy.main.server.gist.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 文 件 名: CommentBean
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 09:51
 * 修改时间：
 * 修改备注：
 */

public class CommentBean {

    @SerializedName(value = "id")
    private String  id;
    @SerializedName(value = "user_name")
    private String  userName;
    @SerializedName(value = "uname")
    private String  uname;

    @SerializedName(value = "user_avatar")
    private String  userAvatar;
    @SerializedName(value = "from_content")
    private String  comment;
    @SerializedName(value = "create_at")
    private long    createAt;
    @SerializedName(value = "reply_to")
    private String  replyTo;
    @SerializedName(value = "replies")
    private Replies replies;

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Replies getReplies() {
        return replies;
    }

    public void setReplies(Replies replies) {
        this.replies = replies;
    }

    public String getUserName() {
        return userName;
    }
    public String getUName() {
        return uname;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public class Replies {
        private int    id;
        private String user_name;
        private String comment_content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getComment_content() {
            return comment_content;
        }

        public void setComment_content(String comment_content) {
            this.comment_content = comment_content;
        }
    }

}
