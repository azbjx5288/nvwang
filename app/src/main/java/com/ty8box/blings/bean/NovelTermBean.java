package com.neinei.cong.bean;

public class NovelTermBean {


    private String term_id;
    private String name;
    private Object thumb;
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTerm_id() {
        return term_id;
    }

    public void setTerm_id(String term_id) {
        this.term_id = term_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getThumb() {
        return thumb;
    }

    public void setThumb(Object thumb) {
        this.thumb = thumb;
    }
}
