package org.qpython.qpy.main.server.gist.response;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.regex.Matcher;

import static org.qpython.qpy.utils.CodePattern.COLOR_BUILTIN;
import static org.qpython.qpy.utils.CodePattern.COLOR_COMMENT;
import static org.qpython.qpy.utils.CodePattern.COLOR_KEYWORD;
import static org.qpython.qpy.utils.CodePattern.PATTERN_PY_BUILD_IN;
import static org.qpython.qpy.utils.CodePattern.PATTERN_PY_COMMENT;
import static org.qpython.qpy.utils.CodePattern.PATTERN_PY_KEYWORD;

/**
 * 文 件 名: GistBean
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/9 09:36
 * 修改时间：
 * 修改备注：
 */

public class GistBean {
    @SerializedName(value = "id")
    private String            id;
    @SerializedName(value = "title")
    private String            title;
    @SerializedName(value = "user")
    private UserBean          user;
    @SerializedName(value = "fav_count")
    private int               star;
    @SerializedName(value = "comment_count")
    private int               comment;
    @SerializedName(value = "create_at")
    private long              createAt;
    @SerializedName(value = "desc")
    private String            description;
    @SerializedName(value = "code")
    private String            source;
    @SerializedName(value = "source_type")
    private String            sourceType;
    @SerializedName(value = "is_feature")
    private boolean           isFeature;
    @SerializedName(value = "comments")
    private List<CommentBean> comments;

    private transient boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SpannableString getSource() {
        SpannableString sStr = new SpannableString(source);
        for (Matcher m = PATTERN_PY_KEYWORD.matcher(sStr); m.find(); ) {
            sStr.setSpan(
                    new ForegroundColorSpan(COLOR_KEYWORD),
                    m.start(),
                    m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (Matcher m = PATTERN_PY_BUILD_IN.matcher(sStr); m.find(); ) {
            sStr.setSpan(
                    new ForegroundColorSpan(COLOR_BUILTIN),
                    m.start(),
                    m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (Matcher m = PATTERN_PY_COMMENT.matcher(sStr); m.find(); ) {
            sStr.setSpan(
                    new ForegroundColorSpan(COLOR_COMMENT),
                    m.start(),
                    m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sStr;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<CommentBean> getComments() {
        return comments;
    }

    public void setComments(List<CommentBean> comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public void changeStar(boolean isAdd) {
        if (isAdd) {
            star++;
        } else {
            star--;
        }
    }

    public int getComment() {
        return comment;
    }

    public void addCommentNum() {
        comment++;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isFeature() {
        return isFeature;
    }

    public void setFeature(boolean feature) {
        isFeature = feature;
    }
}
