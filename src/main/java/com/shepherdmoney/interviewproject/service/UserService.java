package com.shepherdmoney.interviewproject.service;

import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;

public interface UserService {

    /**
     * Create a new User object based on payload
     * @param payload
     * @return user id
     */
    Integer createUser(CreateUserPayload payload);

    /**
     * delete a user in database according to id
     * @param id
     * @return Return 200 OK if a user with the given ID exists, and the deletion is successful
     *         Return 400 Bad Request if a user with the ID does not exist
     */
    String deleteUser(int id);
    // Additional methods as needed
}
