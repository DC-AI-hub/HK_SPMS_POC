package com.spms.backend.service.idm;

import com.spms.backend.service.model.idm.UserModel;

import java.util.function.Consumer;
import java.util.function.Function;

public interface UserModelFulfilledSupporter {

    <T> T fulfill(Function<UserModelFulfilledSupporter,T> exec);
    void fulfill();
    void includeFulfilled(Long id, Consumer<UserModel> user);
}
