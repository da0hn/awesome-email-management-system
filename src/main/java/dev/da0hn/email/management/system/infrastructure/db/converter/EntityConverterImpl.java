package dev.da0hn.email.management.system.infrastructure.db.converter;

import dev.da0hn.email.management.system.shared.annotations.Mapper;
import dev.da0hn.email.management.system.shared.interfaces.DataLayerEntity;
import org.springframework.core.convert.ConversionService;

@Mapper
public class EntityConverterImpl implements EntityConverter {

    private final ConversionService conversionService;

    public EntityConverterImpl(final ConversionService conversionService) { this.conversionService = conversionService; }

    @Override
    public <E extends DataLayerEntity, D> E toEntity(final D source, final Class<E> targetClass) {
        if (this.conversionService.canConvert(targetClass, source.getClass())) {
            throw new IllegalArgumentException("Não é possível converter o domínio %s para a entidade %s".formatted(
                source.getClass().getSimpleName(),
                targetClass.getSimpleName()
            ));
        }
        return this.conversionService.convert(source, targetClass);
    }

    @Override
    public <E, D> D toDomain(final E source, final Class<? extends D> targetClass) {
        if (this.conversionService.canConvert(source.getClass(), targetClass)) {
            throw new IllegalArgumentException("Não é possível converter a entidade %s para o domínio %s".formatted(
                source.getClass().getSimpleName(),
                targetClass.getSimpleName()
            ));
        }
        return this.conversionService.convert(source, targetClass);

    }

}
