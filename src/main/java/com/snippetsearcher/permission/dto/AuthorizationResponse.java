package com.snippetsearcher.permission.dto;

public record AuthorizationResponse(boolean allowed, String reason) {}
