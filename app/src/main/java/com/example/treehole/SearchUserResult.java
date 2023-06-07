package com.example.treehole;

public class SearchUserResult {
    private String user_id;
    private String username;
    private String photo;

    public SearchUserResult(String user_id,String username) {
        this.user_id=user_id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


    public String getUser_id(){
        return user_id;
    }
}
