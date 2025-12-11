package com.spms.backend.service.idm.impl;

import com.spms.backend.service.idm.UserModelFulfilledSupporter;
import com.spms.backend.service.idm.UserService;
import com.spms.backend.service.model.idm.UserModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class UserModelFulfilledSupporterImpl implements UserModelFulfilledSupporter {

    private final Map<Long,List<Consumer<UserModel>>> fulfilledRequest;
    private final UserService userService;

    /**
     * Constructs a new UserModelFulfilledSupporterImpl with dependency injection.
     * Initializes the internal storage for pending user fulfillment requests.
     * 
     * @param userService The service responsible for retrieving user entities
     */
    public UserModelFulfilledSupporterImpl(UserService userService){
        this.userService = userService;
        fulfilledRequest = new HashMap<>();
    }


    /**
     * Executes a function that requires user model fulfillment support and triggers
     * pending fulfillment operations afterward. This enables deferred user processing.
     * 
     * @param exec The function to execute that may register fulfillment requests
     * @return The result of the executed function, or null if no function provided
     */
    @Override
    public <T> T fulfill(Function<UserModelFulfilledSupporter,T> exec) {
        if (exec != null) {
            var result = exec.apply(this);
            this.fulfill();
            return result;
        }
        return null;
    }

    /**
     * Processes all pending fulfillment requests by:
     * 1. Retrieving user models by their IDs
     * 2. Applying all registered consumers to each user model
     * 3. Handling errors during consumer execution
     * 4. Logging warnings for missing users or execution failures
     */
    @Override
    public void fulfill() {
        fulfilledRequest.forEach((key, value) -> {
            UserModel user = userService.getUserById(key);
            if (user != null) {
                for (Consumer<UserModel> c : value) {
                    try {
                        c.accept(user);
                    } catch (Exception ex) {
                        log.warn("user assign failed", ex);
                    }
                }
            } else {
                log.warn("request user assignment but user not found:{}", key);
            }
        });
    }

    /**
     * Registers a new fulfillment request for a specific user ID.
     * Creates a new request list if none exists for the given ID.
     * 
     * @param id The user ID for which fulfillment is requested
     * @param user The consumer operation to apply when user model is available
     */
    @Override
    public void includeFulfilled(Long id, Consumer<UserModel> user) {
        if(!fulfilledRequest.containsKey(id)){
            fulfilledRequest.put(id,new ArrayList<>());
        }
        fulfilledRequest.get(id).add(user);
    }


}
