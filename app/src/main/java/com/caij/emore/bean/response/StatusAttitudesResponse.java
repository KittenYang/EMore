package com.caij.emore.bean.response;

import com.caij.emore.bean.Attitude;
import com.caij.emore.database.bean.User;

import java.util.List;

/**
 * Created by Caij on 2016/7/21.
 */
public class StatusAttitudesResponse extends Response{

    private int total_number;
    private List<User> users;


    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
