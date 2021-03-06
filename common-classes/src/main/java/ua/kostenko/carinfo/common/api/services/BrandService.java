package ua.kostenko.carinfo.common.api.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ua.kostenko.carinfo.common.api.ParamsHolderBuilder;
import ua.kostenko.carinfo.common.api.records.Brand;
import ua.kostenko.carinfo.common.database.repositories.DBRepository;

import javax.annotation.Nonnull;
import java.util.Optional;

@Slf4j
@Service
class BrandService extends CommonDbService<Brand, String> {

    protected BrandService(@NonNull @Nonnull DBRepository<Brand, String> repository) {
        super(repository);
    }

    @Override
    public boolean isValid(@NonNull @Nonnull Brand entity) {
        return StringUtils.isNotBlank(entity.getBrandName());
    }

    @Override
    public Optional<Brand> get(@NonNull @Nonnull Brand entity) {
        ParamsHolderBuilder builder = new ParamsHolderBuilder();
        builder.param(Brand.BRAND_NAME, entity.getBrandName());
        Brand foundEntity = this.repository.findOne(builder.build());
        return Optional.ofNullable(foundEntity);
    }
}
