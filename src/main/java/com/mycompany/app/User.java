package com.mycompany.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class User {
    public String username;
    public Long id;
    public String password;

    public User() {
    }

}