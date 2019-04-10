package ua.kostenko.carinfo.common.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kostenko.carinfo.common.database.repositories.PageableRepository;
import ua.kostenko.carinfo.common.records.Kind;

import javax.annotation.Nonnull;

@Slf4j
@Service
public class KindService extends DefaultCrudService<Kind> {

    @Autowired
    public KindService(@NonNull @Nonnull PageableRepository<Kind> repository) {
        super(repository);
    }


    @Override
    boolean isValid(Kind entity) {
        return StringUtils.isNotBlank(entity.getKindName());
    }

}