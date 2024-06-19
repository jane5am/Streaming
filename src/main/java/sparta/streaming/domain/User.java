package sparta.streaming.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;
    private String password;
    private String email;
    private String type;// 카카오인지 네이버인지
    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    protected void onCreate() {
        this.role = Role.USER;
        this.type = "kakao";
    }

    public enum Role {
        USER, ADMIN
    }




}