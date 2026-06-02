package com.lenarsharipov.billing.common.ui.controllers;

import com.lenarsharipov.billing.usercache.UserCacheService;
import com.lenarsharipov.billing.usercache.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cache/users")
@RequiredArgsConstructor
public class UserCacheController {

    private final UserCacheService userCacheService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        UserProfileResponse response = userCacheService.getUserProfile(userId, pageable);
        return ResponseEntity.ok(response);
    }
}