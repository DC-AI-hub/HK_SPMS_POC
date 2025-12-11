package com.spms.backend.service.exception;

import java.util.List;


// Definition of error code format
// First code:
// 1. "C" - Critical Error , means the program cannot process.
// 2. "W" - Warning, means something may need to attention.
// 3. "V" - Validation Error , means the program cannot accept the parameter.
// Second Code:
// 1. "S" - Happened in Service
// 2. "C" - Happened in Controller
// 3. "R" - Happened in Repository
// 4. "E" - Happened when calling the external interface
// 5. "Y" - Happened when System Failed
// Code Number:
// 1. It was a string that has five number, for example: 00001 - 99999
// Code format will be [FirstCode][SecondCode]-[CodeNumber]
// For example :
// 1. A critical error Happened in Controller -- CC-00001
// 2. A warning Happened in Calling the external Service -- WE-00002
// 3. A validation error when Happened in Service  -- VS-00003
// 4. A resource not found in service, VS-00004

public class ErrorUtils {


    static {
        //TODO: Load the error code mapping configure from a configure file.
    }


    public static List<Exception> translateErrorCode(List<String> errorList) {

        return List.of();
    }

    public static void translateErrorCodeToException(List<String> errorList) throws SpmsRuntimeException {

    }


}
