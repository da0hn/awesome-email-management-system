package dev.da0hn.email.management.system.infrastructure.db.converter;

import dev.da0hn.email.management.system.shared.interfaces.DataLayerEntity;

public interface EntityConverter {

    <E extends DataLayerEntity, D> E toEntity(final D source, final Class<E> targetClass);

    <E, D> D toDomain(final E source, final Class<? extends D> targetClass);

}
