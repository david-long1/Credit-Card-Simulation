package com.shepherdmoney.interviewproject.serviceImpl;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.exception.BusinessException;
import com.shepherdmoney.interviewproject.response.ResponseEnum;
import com.shepherdmoney.interviewproject.service.UserService;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public Integer createUser(CreateUserPayload payload) {
        User user = new User();
        BeanUtils.copyProperties(payload, user);
        user.setCreditCards(new ArrayList<>());
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public String deleteUser(int id) {
        boolean exists = userRepository.existsById(id);
        if (exists) {
            userRepository.deleteById(id);
            return "User deleted successfully";
        } else {
            throw new BusinessException(ResponseEnum.PARAM_EXCEPTION);
        }
    }
}
