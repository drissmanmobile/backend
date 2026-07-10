package com.drissman.adapters.outbound.kernel.dto;

import lombok.Data;

@Data
public class KernelApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String errorCode;
    private String timestamp;
}
