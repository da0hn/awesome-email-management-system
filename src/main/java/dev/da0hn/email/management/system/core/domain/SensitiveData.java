package dev.da0hn.email.management.system.core.domain;

/**
 * Marker interface for objects containing sensitive data that should be masked in logs.
 * Classes implementing this interface will have their toString() method called when logging,
 * so they should ensure sensitive data is properly masked in their toString() implementation.
 */
public interface SensitiveData {
}
