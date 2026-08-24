package com.holiday;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service 
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User(
            request.getUsername(),
            encodedPassword,
            request.getName(),
            request.getTravelStyle()
        );

        userRepository.save(newUser);
        return "회원가입이 완료되었습니다: " + newUser.getName() + "님 (" + newUser.getTravelStyle() +"선호)";
    }

    public String login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return "로그인 성공 ! 환영합니다, " + user.getName() + "님";
    }
    
}
