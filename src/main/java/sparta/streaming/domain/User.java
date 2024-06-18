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
    private String role;

    @PrePersist
    protected void onCreate() {
        this.role = "USER";
    }
}