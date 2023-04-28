package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class User {

    @NotNull
    private int id;

    //private List<String> friendship;
    private String friendship;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$")
    private String login;

    private String name;

    @PastOrPresent
    @NotNull
    private LocalDate birthday;

//    public User(int id, Set<Integer> friendVault, String email, String login, String name, LocalDate birthday) {
//        this.id = id;
//        this.friendVault = friendVault;
//        this.email = email;
//        this.login = login;
//        this.name = name;
//        this.birthday = birthday;
//    }
}
