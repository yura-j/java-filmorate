package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.error.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriendship;
import ru.yandex.practicum.filmorate.storage.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.easy_jdbc.EasyJdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    public static final String TABLE_NAME = "users";
    private final Map<String, List<?>> cache = new HashMap<>();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(@Qualifier("UserFriendshipDbStorage") UserFriendshipStorage friendStorage, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void resetCache() {
        cache.clear();
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new EasyJdbc<User>(jdbcTemplate)
                .select()
                .table(TABLE_NAME)
                .fields("users.*")
                .map(this::map)
                .execute()
                .many();

        resetCache();
        users.forEach(this::initRelationsGreedy);
        resetCache();

        return users;
    }

    @Override
    public User updateUser(User user) {
        Number result = new EasyJdbc<User>(jdbcTemplate)
                .update()
                .table(TABLE_NAME)
                .where("users.id = ?")
                .parameters(List.of(user.getId()))
                .values(Map.of(
                        "login", user.getLogin(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "birthday", user.getBirthday()
                ))
                .execute()
                .id();
        if (result == null) {
            throw new NotFoundException("Пользователя с указанным идентификатором нет");
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        Number createdUserId = new EasyJdbc<User>(jdbcTemplate)
                .insert()
                .table(TABLE_NAME)
                .values(Map.of(
                        "login", user.getLogin(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "birthday", user.getBirthday()
                ))
                .execute()
                .id();
        if (createdUserId == null) {
            throw new RuntimeException("Не удалось создать запись");
        }
        user.setId(createdUserId.longValue());
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = new EasyJdbc<User>(jdbcTemplate)
                .select()
                .table(TABLE_NAME)
                .fields("users.*")
                .where("users.id = ?")
                .parameters(List.of(id))
                .map(this::map)
                .execute()
                .one();
        if (user == null) {
            throw new NotFoundException("не найдено");
        }
        initRelationsGreedy(user);
        return user;
    }

    @Override
    public void initRelationsGreedy(User user) {
        List<UserFriendship> userFriendship = cache.containsKey(UserFriendshipDbStorage.FRIEND_TABLE)
                ? (List<UserFriendship>) cache.get(UserFriendshipDbStorage.FRIEND_TABLE)
                : new EasyJdbc<UserFriendship>(jdbcTemplate)
                .select()
                .table(UserFriendshipDbStorage.FRIEND_JOIN_WITHOUT_JOIN)
                .fields("users_friendship.id, users.id, users.name, users.login, users.name, users.birthday, friends.*")
                .where("friends.id  = users_friendship.friend_id AND users.id = users_friendship.user_id")
                .map(((rs, rowNum) -> new UserFriendship(
                        rs.getLong("users_friendship.id")
                        , new User(rs.getLong("users.id")
                        , rs.getString("users.email")
                        , rs.getString("users.login")
                        , rs.getString("users.name")
                        , LocalDate.parse(rs.getString("users.birthday")))

                        , new User(rs.getLong(7)
                        , rs.getString(8)
                        , rs.getString(9)
                        , rs.getString(10)
                        , LocalDate.parse(rs.getString(11)))
                )))
                .execute()
                .many();
        List<User> friends = userFriendship
                .stream()
                .filter(friendship -> friendship.getUser().getId() == user.getId())
                .map(UserFriendship::getFriend)
                .collect(Collectors.toList());
        user.setFriends(new LinkedHashSet<>(friends));

    }

    public User map(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getLong("users.id")
                , rs.getString("users.email")
                , rs.getString("users.login")
                , rs.getString("users.name")
                , LocalDate.parse(rs.getString("users.birthday")));
    }


}
