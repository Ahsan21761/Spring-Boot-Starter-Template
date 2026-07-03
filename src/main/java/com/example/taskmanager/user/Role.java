package com.example.taskmanager.user;

/**
 * Coarse-grained authorization roles. Kept intentionally small; extend with a proper
 * role/permission model if your domain requires finer-grained authorization.
 */
public enum Role {
    USER,
    ADMIN
}
