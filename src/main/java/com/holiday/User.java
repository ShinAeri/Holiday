package com.holiday;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본키 

    @Column(nullable = false, unique = true)
    private String username; // 로그인 아이디 (중복 불가)

    @Column(nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(nullable = false)
    private String name; // 사용자 이름

    private String travelStyle; // 여행 성향

    protected User() {}

    public User(String username, String password, String name, String travelStyle) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.travelStyle = travelStyle;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getTravelStyle() { return travelStyle; }
}