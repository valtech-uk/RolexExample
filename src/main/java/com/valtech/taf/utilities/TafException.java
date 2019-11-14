package com.valtech.taf.utilities;

/*
 * Class to throw Runtime Exception which is an unchecked exception
 * Uploads the contents on config.xml file on test start
 * @version 1.0
 * @since 2019-08-06
 * Valtech QA
 */

public class TafException extends IllegalArgumentException {

    TafException(String message){
        super(message);
    }
}

