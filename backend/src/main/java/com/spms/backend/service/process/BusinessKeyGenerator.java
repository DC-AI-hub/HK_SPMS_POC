package com.spms.backend.service.process;

import com.spms.backend.service.model.process.BusinessKeyModel;

public interface BusinessKeyGenerator {

    default BusinessKeyModel generateBusinessKey(String keyPrefix) {
        return this.generateBusinessKey(keyPrefix, "");
    }

    BusinessKeyModel generateBusinessKey(String keyPrefix, String split);

    BusinessKeyModel occupiedBusinessKey(Long keyId, String occupiedId, String occupiedTarget);

}
