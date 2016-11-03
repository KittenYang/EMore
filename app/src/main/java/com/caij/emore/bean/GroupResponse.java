package com.caij.emore.bean;

import com.caij.emore.bean.response.Response;
import com.caij.emore.database.bean.Group;

import java.util.List;

/**
 * Created by Caij on 2016/11/2.
 */

public class GroupResponse extends Response {

    private List<Group> lists;

    private int total_number;

    public List<Group> getLists() {
        return lists;
    }

    public void setLists(List<Group> lists) {
        this.lists = lists;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }
}
