package in.infosys.backend.service;

import in.infosys.backend.dto.CredentialShareRequestDto;
import in.infosys.backend.dto.CredentialShareResponseDto;
import in.infosys.backend.dto.CredentialShareUpdateRequestDto;
import in.infosys.backend.entity.Credential;
import in.infosys.backend.entity.CredentialShare;
import in.infosys.backend.entity.SharePermission;
import in.infosys.backend.entity.User;
import in.infosys.backend.repository.CredentialRepository;
import in.infosys.backend.repository.CredentialShareRepository;
import in.infosys.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CredentialShareService {

    private final CredentialShareRepository credentialShareRepository;
    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;

    public CredentialShareService(
            CredentialShareRepository credentialShareRepository,
            CredentialRepository credentialRepository,
            UserRepository userRepository) {

        this.credentialShareRepository = credentialShareRepository;
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // 1. GET CURRENT LOGGED-IN USER
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Logged-in user not found"));
    }


    // =========================================================
    // SHARE CREDENTIAL
    public CredentialShareResponseDto shareCredential(
            CredentialShareRequestDto request) {

        if (request.getCredentialId() == null) {
            throw new RuntimeException("Credential ID is required");
        }

        if (request.getUsername() == null ||
                request.getUsername().isBlank()) {

            throw new RuntimeException(
                    "Username is required"
            );
        }

        if (request.getPermission() == null) {
            throw new RuntimeException(
                    "Permission is required"
            );
        }

        User owner = getCurrentUser();

        Credential credential =
                credentialRepository
                        .findByIdAndUserAndDeletedFalse(
                                request.getCredentialId(),
                                owner
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Credential not found or access denied"
                                )
                        );

        User sharedWith =
                userRepository
                        .findByUsername(request.getUsername())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User to share with not found"
                                )
                        );

        if (owner.getId().equals(sharedWith.getId())) {
            throw new RuntimeException(
                    "You cannot share a credential with yourself"
            );
        }

        if (request.getExpiresAt() != null &&
                !request.getExpiresAt()
                        .isAfter(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Expiry time must be in the future"
            );
        }

        var existing =
                credentialShareRepository
                        .findByCredentialAndSharedWithAndRevokedFalse(
                                credential,
                                sharedWith
                        );

        CredentialShare share;

        if (existing.isPresent()) {

            share = existing.get();

            share.setPermission(request.getPermission());
            share.setExpiresAt(request.getExpiresAt());
            share.setRevoked(false);

        } else {

            share = new CredentialShare();

            share.setCredential(credential);
            share.setOwner(owner);
            share.setSharedWith(sharedWith);
            share.setPermission(request.getPermission());
            share.setExpiresAt(request.getExpiresAt());
            share.setRevoked(false);
        }

        share =
                credentialShareRepository.save(share);

        return toResponse(share);
    }

    // Dto mapping function
    private CredentialShareResponseDto toResponse(CredentialShare share) {
        return new CredentialShareResponseDto(
                share.getId(),
                share.getCredential().getId(),
                share.getCredential().getTitle(),
                share.getOwner().getUsername(),
                share.getSharedWith().getUsername(),
                share.getPermission(),
                share.getExpiresAt(),
                share.isRevoked()
        );
    }


    // =========================================================
    // GET ACTIVE SHARE
    // =========================================================
// i have to update the global exception so  403 will be send
    public CredentialShare getActiveShare(
            Credential credential,
            User user) {

        return credentialShareRepository
                .findByCredentialAndSharedWithAndRevokedFalse(
                        credential,
                        user
                )
                .filter(this::isShareActive)
                .orElseThrow(() ->
                        new RuntimeException(
                                "You do not have access to this credential"
                        )
                );
    }


    // =========================================================
    // CHECK EXPIRY
    // =========================================================

    private boolean isShareActive(CredentialShare share) {

        // Revoked share is not active
        if (share.isRevoked()) {
            return false;
        }

        // No expiry means never expires
        if (share.getExpiresAt() == null) {
            return true;
        }

        // Expired share
        return share.getExpiresAt().isAfter(LocalDateTime.now());
    }


    // =========================================================
    // CHECK VIEW ACCESS
    // =========================================================

    public boolean canView(
            Credential credential,
            User currentUser) {

        // Owner can always view
        if (credential.getUser().getId().equals(currentUser.getId())) {
            return true;
        }

        // Shared user needs active share
        CredentialShare share = getActiveShare(credential, currentUser);

        return share.getPermission() == SharePermission.VIEW
                || share.getPermission() == SharePermission.EDIT
                || share.getPermission() == SharePermission.MANAGE;
    }

    // =========================================================
    // CHECK EDIT ACCESS
    public boolean canEdit(
            Credential credential,
            User currentUser) {

        // Owner can always edit
        if (credential.getUser()
                .getId()
                .equals(currentUser.getId())) {
            return true;
        }

        // Get active share
        CredentialShare share =
                getActiveShare(credential, currentUser);

        // Only EDIT permission can modify
        if (share.getPermission() != SharePermission.EDIT) {

            throw new RuntimeException(
                    "You have VIEW permission only"
            );
        }

        return true;
    }

    // ========================================================
    // MANAGE ACCESS
    public boolean canManage(
            Credential credential,
            User currentUser) {

        if (credential.getUser()
                .getId()
                .equals(currentUser.getId())) {

            return true;
        }

        CredentialShare share =
                getActiveShare(credential, currentUser);

        if (share.getPermission()
                != SharePermission.MANAGE) {

            throw new RuntimeException(
                    "You do not have management permission"
            );
        }

        return true;
    }

    // =========================================================
    // REVOKE SHARE
    public void revokeShare(Long shareId) {

        User owner = getCurrentUser();

        CredentialShare share =
                credentialShareRepository
                        .findByIdAndOwnerAndRevokedFalse(
                                shareId,
                                owner
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Share not found or access denied"
                                )
                        );

        share.setRevoked(true);

        credentialShareRepository.save(share);
    }


    // =========================================================
    // GET CREDENTIALS SHARED WITH CURRENT USER
    public List<CredentialShareResponseDto> getSharedCredentials() {

        User currentUser = getCurrentUser();

        return credentialShareRepository
                .findAllBySharedWithAndRevokedFalse(currentUser)
                .stream()
                .filter(this::isShareActive)
                .map(this::toResponse)
                .toList();
    }


    // =========================================================
    // GET CREDENTIALS SHARED BY CURRENT USER
    // =========================================================

    public List<CredentialShareResponseDto> getMyShares() {

        User owner = getCurrentUser();

        return credentialShareRepository
                .findAllByOwnerAndRevokedFalse(owner)
                .stream()
                .filter(this::isShareActive)
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // UPDATE SHARE PERMISSION
    public CredentialShareResponseDto updateSharePermission(
            Long shareId,
            CredentialShareUpdateRequestDto request) {

        User owner = getCurrentUser();

        if (request == null || request.getPermission() == null) {
            throw new RuntimeException("Permission is required");
        }

        CredentialShare share =
                credentialShareRepository
                        .findByIdAndOwnerAndRevokedFalse(shareId, owner)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Share not found or access denied"
                                )
                        );

        if (share.getExpiresAt() != null &&
                !share.getExpiresAt().isAfter(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Cannot update an expired share"
            );
        }

        share.setPermission(request.getPermission());

        share = credentialShareRepository.save(share);

        return toResponse(share);
    }

}
