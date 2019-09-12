package ua.kostenko.carinfo.rest.services.common;

import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.NonNull;
import ua.kostenko.carinfo.common.api.records.GenericRecord;

public interface SearchService<T extends GenericRecord<I>, I> {

    T getById(Long id);

    Page<T> getAll(Pageable pageable);

    Page<T> findForField(@NonNull @Nonnull String field, Pageable pageable);

    Page<T> findByParams(@NonNull @Nonnull Map<String, Object> params, Pageable pageable);

    int countAll();

    int countForField(@NonNull @Nonnull String field);

    int countByParams(@NonNull @Nonnull Map<String, Object> params);
}
