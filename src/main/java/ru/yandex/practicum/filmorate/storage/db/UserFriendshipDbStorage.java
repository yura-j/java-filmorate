package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriendship;
import ru.yandex.practicum.filmorate.storage.UserFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.db.easy_jdbc.EasyJdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("UserFriendshipDbStorage")
public class UserFriendshipDbStorage implements UserFriendshipStorage {
    public static final String FRIEND_TABLE = "users_friendship";
    public static final String FRIEND_JOIN_WITHOUT_JOIN = "users_friendship, users, users as friends";
    public static final String COMMON_FRIEND_JOIN = "users_friendship INNER JOIN users_friendship as another_users_friendship" +
            " ON users_friendship.friend_id = another_users_friendship.friend_id" +
            " INNER JOIN users on users_friendship.friend_id = users.id";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserFriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserFriendship getByUserIdAndFriendId(Long friendId, Long userId) {

        return new EasyJdbc<UserFriendship>(jdbcTemplate)
                .select()
                .table(FRIEND_JOIN_WITHOUT_JOIN)
                .fields("users_friendship.id, users.id, users.name, users.login, users.name, users.birthday, friends.*")
                .where("users_friendship.friend_id = ? AND users_friendship.user_id = ? AND (friends.id  = users_friendship.friend_id AND users.id = users_friendship.user_id)")
                .parameters(List.of(friendId, userId))
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
                .one();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long anotherUserId) {
        return new EasyJdbc<User>(jdbcTemplate)
                .select()
                .table(COMMON_FRIEND_JOIN)
                .fields("users_friendship.*, another_users_friendship.*, users.*")
                .where("users_friendship.user_id = ? AND another_users_friendship.user_id = ?")
                .parameters(List.of(userId, anotherUserId))
                .map(((rs, rowNum) -> new User(rs.getLong("users.id")
                        , rs.getString("users.email")
                        , rs.getString("users.login")
                        , rs.getString("users.name")
                        , LocalDate.parse(rs.getString("users.birthday")))
                ))
                .execute()
                .many();
    }

    @Override
    public List<UserFriendship> get() {
        return new EasyJdbc<UserFriendship>(jdbcTemplate)
                .select()
                .table(FRIEND_JOIN_WITHOUT_JOIN)
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
    }

    @Override
    public UserFriendship create(UserFriendship friendship) {
        Number createdFriendship = new EasyJdbc<UserFriendship>(jdbcTemplate)
                .insert()
                .table(FRIEND_TABLE)
                .values(Map.of(
                        "friend_id", friendship.getFriend().getId(),
                        "user_id", friendship.getUser().getId()
                ))
                .execute()
                .id();
        if (createdFriendship == null) {
            throw new RuntimeException("Не удалось создать запись");
        }
        return friendship;
    }

    @Override
    public Long delete(Long id) {
        new EasyJdbc<UserFriendship>(jdbcTemplate)
                .delete()
                .table(FRIEND_TABLE)
                .where("id = ?")
                .parameters(List.of(id))
                .execute()
                .id();
        return id;
    }
}