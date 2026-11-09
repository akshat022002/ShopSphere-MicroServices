package com.akshat.wishlist.common.response;

import java.time.LocalDateTime;

public record ApiResponse<T> (

        boolean success,

        String message,

        T data,

        LocalDateTime timeStamp

){
}