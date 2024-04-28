package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.service.UserService;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "User Management")
@RestController
public class UserController {

    // TODO: wire in the user repository (~ 1 line)
    @Autowired
    private UserService userService;

    @ApiOperation(value = "Create a new user")
    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        // TODO: Create an user entity with information given in the payload, store it in the database
        //       and return the id of the user in 200 OK response
        return ResponseEntity.ok(userService.createUser(payload));

    }

    @ApiOperation(value = "Delete a user by ID")
    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam
                                                 @ApiParam(value = "The ID of the user to be deleted", required = true)
                                                 int userId) {
        // TODO: Return 200 OK if a user with the given ID exists, and the deletion is successful
        //       Return 400 Bad Request if a user with the ID does not exist
        //       The response body could be anything you consider appropriate
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
