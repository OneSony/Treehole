package com.example.treehole;

public class SearchUserResult {
    private String username;
    private String photo;

    public SearchUserResult(String username, String photo) {
        this.username = username;
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoto() {
        return photo;
    }
}
