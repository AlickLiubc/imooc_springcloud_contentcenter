package com.itmuch.contentcenter.domain.entity.content;

import javax.persistence.*;

@Table(name = "mid_user_share")
public class MidUserShare {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * share.id
     */
    @Column(name = "share_id")
    private Integer shareId;

    /**
     * user.id
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取share.id
     *
     * @return share_id - share.id
     */
    public Integer getShareId() {
        return shareId;
    }

    /**
     * 设置share.id
     *
     * @param shareId share.id
     */
    public void setShareId(Integer shareId) {
        this.shareId = shareId;
    }

    /**
     * 获取user.id
     *
     * @return user_id - user.id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置user.id
     *
     * @param userId user.id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}