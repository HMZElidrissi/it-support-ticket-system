package ma.hmzelidrissi.frontend.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private Long id;
    private String name;
    private String email;
    private Role role;
}