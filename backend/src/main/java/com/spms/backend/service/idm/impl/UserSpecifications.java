package com.spms.backend.service.idm.impl;

import com.spms.backend.repository.entities.idm.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    private UserSpecifications() {
        // Private constructor to prevent instantiation
    }

    public static Specification<User> searchByQuery(String searchTerm) {
        return (Root<User> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            
            Predicate usernamePred = cb.like(cb.lower(root.get("username")), pattern);
            Predicate emailPred = cb.like(cb.lower(root.get("email")), pattern);
            Predicate descriptionPred = cb.like(cb.lower(root.get("description")), pattern);
            
            try {
                Expression<String> userProfilesJson = root.get("userProfilesJson");
                Predicate jsonPred = cb.like(userProfilesJson, pattern);
                return cb.or(usernamePred, emailPred, descriptionPred, jsonPred);
            } catch (Exception e) {
                return cb.or(usernamePred, emailPred, descriptionPred);
            }
        };
    }

    public static Specification<User> filterByType(User.UserType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }
}
