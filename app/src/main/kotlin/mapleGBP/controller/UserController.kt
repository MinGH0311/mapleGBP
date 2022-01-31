package mapleGBP.controller

import io.swagger.annotations.ApiOperation
import mapleGBP.model.dto.UserInfo
import mapleGBP.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.persistence.EntityNotFoundException

@RestController
class UserController(
    var userService: UserService
) {

    @ApiOperation("전체 유저 조회")
    @GetMapping("/user")
    fun getAllUsers(): List<UserInfo> {
        return userService.getAllUserInfo()
    }

    @ApiOperation("유저 조회")
    @GetMapping("/user/{nickname}")
    fun getUser(@PathVariable("nickname") nickname: String): UserInfo {
        try {
            return userService.getUserInfo(nickname)
        } catch (e: EntityNotFoundException) {
            return userService.searchUserInfo(nickname)
        }
    }

    @ApiOperation("유저 등록")
    @PostMapping("/user")
    fun createUser(@RequestBody userInfo: UserInfo): Boolean {
        userService.saveOrUpdateUserInfo(userInfo)
        return true
    }

    @ApiOperation("유저 정보 업데이트")
    @PutMapping("/user/{nickname}")
    fun updateUser(@PathVariable("nickname") nickname: String,
                   @RequestBody userInfo: UserInfo): Boolean {
        userService.saveOrUpdateUserInfo(userInfo)
        return true
    }

    @ApiOperation("유저 정보 삭제")
    @DeleteMapping("/user/{nickname}")
    fun deleteUser(@PathVariable("nickname") nickname: String): Boolean {
        userService.deleteUserInfo(nickname)
        return true
    }
}