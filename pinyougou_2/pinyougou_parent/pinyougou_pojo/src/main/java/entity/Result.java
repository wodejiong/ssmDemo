package entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Result implements Serializable{
    private Boolean success;
    private String message;

    private List<Map> list;

    public Result(List<Map> list) {
        this.list = list;
    }

    public Result(Boolean success, String message, List<Map> list) {
        this.success = success;
        this.message = message;
        this.list = list;

    }

    public List<Map> getList() {
        return list;
    }

    public void setList(List<Map> list) {
        this.list = list;
    }

    public Result(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
