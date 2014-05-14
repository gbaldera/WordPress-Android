package org.wordpress.android.models;

import android.text.TextUtils;

import org.json.JSONObject;
import org.wordpress.android.util.JSONUtil;
import org.wordpress.android.util.StringUtils;
import org.wordpress.android.util.UrlUtils;

public class ReaderBlogInfo {
    public long blogId;
    public boolean isPrivate;
    public boolean isJetpack;
    public boolean isFollowing;
    public int numSubscribers;

    private String name;
    private String description;
    private String url;

    /*{
    "ID": 3584907,
    "name": "WordPress.com News",
    "description": "The latest news on WordPress.com and the WordPress community.",
    "URL": "http:\/\/en.blog.wordpress.com",
    "jetpack": false,
    "subscribers_count": 9222924,
    "is_private": false,
    "is_following": false,
    "meta": {
        "links": {
            "self": "https:\/\/public-api.wordpress.com\/rest\/v1\/sites\/3584907",
            "help": "https:\/\/public-api.wordpress.com\/rest\/v1\/sites\/3584907\/help",
            "posts": "https:\/\/public-api.wordpress.com\/rest\/v1\/sites\/3584907\/posts\/",
            "comments": "https:\/\/public-api.wordpress.com\/rest\/v1\/sites\/3584907\/comments\/"
        }
    }
    }*/

    public static ReaderBlogInfo fromJson(JSONObject json) {
        ReaderBlogInfo blog = new ReaderBlogInfo();
        if (json == null) {
            return blog;
        }

        blog.blogId = json.optLong("ID");

        blog.setName(JSONUtil.getStringDecoded(json, "name"));
        blog.setDescription(JSONUtil.getStringDecoded(json, "description"));
        blog.setUrl(JSONUtil.getString(json, "URL"));

        blog.isJetpack = JSONUtil.getBool(json, "jetpack");
        blog.isPrivate = JSONUtil.getBool(json, "is_private");
        blog.isFollowing = JSONUtil.getBool(json, "is_following");
        blog.numSubscribers = json.optInt("subscribers_count");

        return blog;
    }

    /*
     * info is considered incomplete if it's missing both the name and description - used
     * used by ReaderBlogAction.updateIncompleteBlogInfo() to fill in incomplete blogInfo,
     * and by ReaderBlogInfoList.removeIncomplete()
     */
    public boolean isIncomplete() {
        return (!hasName() && !hasDescription());
    }

    /*
     * info is considered to external (ie: it's a feed) if it doesn't have a blogId
     */
    public boolean isExternal() {
        return (blogId == 0);
    }

    public String getName() {
        return StringUtils.notNullStr(name);
    }
    public void setName(String blogName) {
        this.name = StringUtils.notNullStr(blogName).trim();
    }

    public String getDescription() {
        return StringUtils.notNullStr(description);
    }
    public void setDescription(String description) {
        this.description = StringUtils.notNullStr(description).trim();
    }

    public String getUrl() {
        return StringUtils.notNullStr(url);
    }
    public void setUrl(String url) {
        this.url = UrlUtils.normalizeUrl(StringUtils.notNullStr(url));
    }

    public boolean hasBlogId() {
        return (blogId != 0);
    }
    public boolean hasUrl() {
        return !TextUtils.isEmpty(url);
    }
    public boolean hasName() {
        return !TextUtils.isEmpty(name);
    }
    public boolean hasDescription() {
        return !TextUtils.isEmpty(description);
    }

    /*
     * returns the mshot url to use for this blog, ex:
     *   http://s.wordpress.com/mshots/v1/http%3A%2F%2Fnickbradbury.com?w=600
     * note that while mshots support a "h=" parameter, this crops rather than
     * scales the image to that height
     *   https://github.com/Automattic/mShots
     */
    public String getMshotsUrl(int width) {
        return "http://s.wordpress.com/mshots/v1/"
             + UrlUtils.urlEncode(getUrl())
             + String.format("?w=%d", width);
    }

    public boolean isSameAs(ReaderBlogInfo blogInfo) {
        return blogInfo != null
            && this.blogId == blogInfo.blogId
            && this.isFollowing == blogInfo.isFollowing
            && this.isPrivate == blogInfo.isPrivate
            && this.numSubscribers == blogInfo.numSubscribers
            && this.getName().equals(blogInfo.getName())
            && this.getDescription().equals(blogInfo.getDescription())
            && this.getUrl().equals(blogInfo.getUrl());
    }
}
