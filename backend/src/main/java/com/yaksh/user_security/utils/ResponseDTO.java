package com.yaksh.user_security.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
    private int status;
    private String message;
    private Object data;

    public ResponseDTO(int s, String m){
        this.status=s;
        this.message=m;
    }
}
