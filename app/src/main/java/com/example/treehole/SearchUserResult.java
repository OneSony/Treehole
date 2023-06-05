package com.example.treehole;

public class SearchUserResult {
    private String user_id;
    private String username;
    private String photo;

    public SearchUserResult(String user_id,String username, String photo) {
        this.user_id=user_id;
        this.username = username;
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoto() {
        return photo;
    }

    public String getUser_id(){
        return user_id;
    }
}
