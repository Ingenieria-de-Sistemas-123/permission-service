package com.snippetsearcher.permission.controller;

import com.snippetsearcher.permission.dto.UserDto;
import com.snippetsearcher.permission.model.UserAccount;
import com.snippetsearcher.permission.service.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService users;

  public UserController(UserService users) {
    this.users = users;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> me(@AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getClaimAsString("sub");
    UserAccount me = users.getBySub(sub);
    return ResponseEntity.ok(UserDto.from(me));
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> list(@AuthenticationPrincipal Jwt jwt) {
    String sub = jwt.getClaimAsString("sub");
    List<UserAccount> others = users.listOthers(sub);
    return ResponseEntity.ok(others.stream().map(UserDto::from).toList());
  }
}
