package dev.da0hn.email.management.system.core.domain;

public interface RuleVisitor<T> {
    T visit(MoveEmailRule rule);
    T visit(ArchiveEmailRule rule);
    T visit(DeleteEmailRule rule);
}
