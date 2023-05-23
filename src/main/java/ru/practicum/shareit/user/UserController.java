package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Эндпоинт по созданию пользователя.
     * @param userDto обьек пользовател.
     * @return Возварещает созданного польлзователя.
     */
    @PostMapping
    public UserDto creatUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    /**
     * Эндпонит по изменению пользователя.
     * @param userDto обьект с полями/полям, котрые будут изменены.
     * @param userId идентификатор пользователя, который будет изменет.
     * @return Возварщает измененного пользователя.
     */
    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {
        return userService.updateUser(userDto, userId);
    }

    /**
     * Энодпоинт по нахожднию пользователя по его идентификатору.
     * @param userId идентификатор пользователя.
     * @return Возварщает пользователя по его идентификатору
     */
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    /**
     * Эндпоинт по нахождению всех пользователей.
     * @return Возвращает список всех пользователей.
     */
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Эндпоинт по удалению пользователя по его идентификатору.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }


}
