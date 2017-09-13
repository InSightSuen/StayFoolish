package com.insightsuen.stayfoolish.model;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Convert;
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
        @Index(value = "title, createTime DESC", unique = true)
})
public class Note {

    @Id
    private Long id;

    @NonNull
    private String title;

    private String content;

    private Date createTime;

    @Convert(converter = NoteTypeConverter.class, columnType = String.class)
    private NoteType type;

    @Generated(hash = 1272611929)
    public Note() {
    }

    public Note(Long id) {
        this.id = id;
    }

    @Generated(hash = 2081448229)
    public Note(Long id, @NonNull String title, String content, Date createTime,
            NoteType type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createTime = createTime;
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NoteType getType() {
        return this.type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }
}
