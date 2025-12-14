package com.snippetsearcher.permission.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RecordDtosTest {

  @Test
  void authorizationRequest_record_fields() {
    var r = new AuthorizationRequest("READ", "sid", "owner");
    assertEquals("READ", r.action());
    assertEquals("sid", r.snippetId());
    assertEquals("owner", r.resourceOwnerSub());
  }

  @Test
  void authorizationResponse_record_fields() {
    var r = new AuthorizationResponse(true, "ok");
    assertTrue(r.allowed());
    assertEquals("ok", r.reason());
  }
}
