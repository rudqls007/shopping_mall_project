package toy.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/* 회원 가입 화면으로부터 넘어오는 가입정보를 담을 dto */
@Data
public class MemberFormDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글 입력만 가능합니다.")
    private String name;

    @NotEmpty(message = "아이디는 필수 입력 값입니다.")
    private String loginId;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    private String oriPassword;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min=8, max=16, message="비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;

    private String zipcode;

    private String streetAddress;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String detailAddress;
}
