package com.snippetsearcher.permission.dto;

public record AuthorizationRequest(String action, String snippetId, String resourceOwnerSub) {}
