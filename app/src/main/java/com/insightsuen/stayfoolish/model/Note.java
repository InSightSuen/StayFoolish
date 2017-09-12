package com.insightsuen.stayfoolish.model;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;

/**
 * Created by InSight Suen on 2017/9/12.
 * Note entity
 */

@Entity(indexes = {
        @Index(value = "text, createTime DESC", unique = true)
})
public class Note {

    @Id
    private Long id;

    @NonNull
    private String text;

    private Date createTime;

    @Generated(hash = 879062434)
    public Note(Long id, @NonNull String text, Date createTime) {
        this.id = id;
        this.text = text;
        this.createTime = createTime;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
