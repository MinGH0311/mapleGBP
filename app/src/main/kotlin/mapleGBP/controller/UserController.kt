package mapleGBP.controller

import mapleGBP.model.dto.UserInfo
import mapleGBP.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException

@RestController
class UserController(
    var userService: UserService
) {
    @GetMapping("/user")
    fun getAllUsers(): List<UserInfo> {
        return userService.getAllUserInfo()
    }

    @GetMapping("/user/{nickname}")
    fun getUser(@PathVariable("nickname") nickname: String): UserInfo {
        try {
            return userService.getUserInfo(nickname)
        } catch (e: EntityNotFoundException) {
            return userService.searchUserInfo(nickname)
        }
    }

    @PostMapping("/user")
    fun createUser(@RequestBody userInfo: UserInfo): Boolean {
        userService.saveOrUpdateUserInfo(userInfo)
        return true
    }

    @PutMapping("/user/{nickname}")
    fun updateUser(@PathVariable("nickname") nickname: String,
                   @RequestBody userInfo: UserInfo): Boolean {
        userService.saveOrUpdateUserInfo(userInfo)
        return true
    }

    @DeleteMapping("/user/{nickname}")
    fun deleteUser(@PathVariable("nickname") nickname: String): Boolean {
        userService.deleteUserInfo(nickname)
        return true
    }
}