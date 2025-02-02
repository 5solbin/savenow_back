package savenow.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import savenow.backend.entity.user.User;
import savenow.backend.entity.user.UserRepository;
import savenow.backend.dto.user.UserReqDto.JoinReqDto;
import savenow.backend.dto.user.UserResDto.JoinResDto;
import savenow.backend.handler.exception.CustomApiException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 회원 가입 서비스
    @Transactional
    public JoinResDto join(JoinReqDto joinReqDto) {
        // 아이디 중복 검사
        if (nameDuplicateCheck(joinReqDto.getUsername())) {
            //이메일 중복
            throw new CustomApiException("동일한 사용자 이름이 존재합니다.");
        }

        //  닉네임 중복 검사
        if (emailDuplicateCheck(joinReqDto.getEmail())) {
            // 닉네임 중복
            throw new CustomApiException("이미 존재하는 이메일 입니다.");
        }

        // 패스워드 인코딩 + 회원가입
        User newUser = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // DTO 응답
        return new JoinResDto(newUser);
    }

    // 이메일 중복 검사 // 중복한다면 true 반환
    public boolean emailDuplicateCheck(String email) {
        Optional<User> userOP = userRepository.findByEmail(email);
        return userOP.isPresent();
    }

    public boolean nameDuplicateCheck(String username) {
        Optional<User> userOP = userRepository.findByUsername(username);
        return userOP.isPresent();
    }
}
