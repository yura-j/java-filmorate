package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserFriendship {
    private Long id;
    private User user;
    private User friend;

    public UserFriendship(Long id, User user, User friend) {
        this.id = id;
        this.user = user;
        this.friend = friend;
    }
}
