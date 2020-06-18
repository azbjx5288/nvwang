package com.neinei.cong.bean;

/**
 * Created by gaoyuan on 2018/11/23.
 */

public class BooksBean {

    /**
     * ret : 200
     * data : {"code":1001,"msg":"钻石不足，请充值","info":{"code":""}}
     * msg :
     */

    private int ret;
    private DataBean data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * code : 1001
         * msg : 钻石不足，请充值
         * info : {"code":""}
         */

        private int code;
        private String msg;
        private InfoBean info;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean {
            public String id;
            public String post_title;
            public String post_content;
            public String uptime;
            public String name;
            public double coin;
            private String code;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPost_title() {
                return post_title;
            }

            public void setPost_title(String post_title) {
                this.post_title = post_title;
            }

            public String getPost_content() {
                return post_content;
            }

            public void setPost_content(String post_content) {
                this.post_content = post_content;
            }

            public String getUptime() {
                return uptime;
            }

            public void setUptime(String uptime) {
                this.uptime = uptime;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public double getCoin() {
                return coin;
            }

            public void setCoin(double coin) {
                this.coin = coin;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }
        }
    }
}
