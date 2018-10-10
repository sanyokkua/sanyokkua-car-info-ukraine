package ua.kostenko.carinfo.carinfoua.data.persistent.services;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kostenko.carinfo.carinfoua.configuration.ApplicationProperties;
import ua.kostenko.carinfo.carinfoua.data.persistent.entities.RegistrationInformationEntity;
import ua.kostenko.carinfo.carinfoua.data.persistent.repositories.RegistrationInformationCrudRepository;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Slf4j
public class RegistrationInformationServiceDB implements RegistrationInformationService {
    private final RegistrationInformationCrudRepository registrationInformationCRUDRepository;
    private final ApplicationProperties applicationProperties;
    private final EntityManagerFactory factory;

    @Autowired
    public RegistrationInformationServiceDB(RegistrationInformationCrudRepository registrationInformationCRUDRepository, ApplicationProperties applicationProperties, EntityManagerFactory factory) {
        Preconditions.checkNotNull(registrationInformationCRUDRepository);
        Preconditions.checkNotNull(applicationProperties);
        Preconditions.checkNotNull(factory);
        this.registrationInformationCRUDRepository = registrationInformationCRUDRepository;
        this.applicationProperties = applicationProperties;
        this.factory = factory;
    }

    @Override
    public void saveAll(Collection<RegistrationInformationEntity> registrationInformationEntityList) {
        log.info("Saving list of RegistrationInformationEntity Objects, size: {}", registrationInformationEntityList.size());
        LocalTime before = LocalTime.now();
        log.info("Time when saving started: {}", before.toString());
        long counter = Long.valueOf(applicationProperties.APP_LOG_MAPPER_COUNTER);
        SessionFactory sessionFactory = factory.unwrap(SessionFactory.class);
        if (sessionFactory == null) {
            throw new NullPointerException("factory is not a hibernate factory");
        }
        List<RegistrationInformationEntity> list = new ArrayList<>(registrationInformationEntityList);
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        for (int i = 0; i < list.size(); i++) {
            session.saveOrUpdate(list.get(i));
            if (list.size() % counter == 0 || i + 1 >= list.size()) {
                session.flush();
                session.clear();
            }
        }
        transaction.commit();
        session.close();

//        registrationInformationCRUDRepository.saveAll(registrationInformationEntityList);
//        registrationInformationCRUDRepository.flush();
        Duration duration = Duration.between(before, LocalTime.now());
        log.info("Saved list of RegistrationInformationEntity Objects, size: {}", registrationInformationEntityList.size());
        log.info("Time spent for saving: {} ms, {} sec, {} min", duration.toMillis(), duration.getSeconds(), duration.toMinutes());
    }

    @Override
    public void removeAllByDateForDataSet(String date) {
        log.info("Removing all data for date: {}", date);
        LocalTime before = LocalTime.now();
        registrationInformationCRUDRepository.deleteAllByDataSetYear(date);
        Duration duration = Duration.between(before, LocalTime.now());
        log.info("Removed all data for date: {}", date);
        log.info("Time spent for removing: {} ms, {} sec, {} min", duration.toMillis(), duration.getSeconds(), duration.toMinutes());
    }

    @Override
    public List<RegistrationInformationEntity> search(RegistrationInformationEntity.RegistrationInformationEntityFields field, String value) {
        log.info("Searching RegistrationInformationEntity for field: {}, value: {}", field.name(), value);
        List<RegistrationInformationEntity> results = Collections.EMPTY_LIST;
        switch (field) {
            case CAR_NEW_REGISTRATION_NUMBER:
                results = registrationInformationCRUDRepository.findAllByCarNewRegistrationNumberLike(value);
                break;
            case CAR_MODEL:
                results = registrationInformationCRUDRepository.findAllByCarModelLike(value);
                break;
            case CAR_BRAND:
                results = registrationInformationCRUDRepository.findAllByCarBrandLike(value);
                break;
            default:
                log.warn("Field {} is not supported yet, null will be returned", field.name());
                break;
        }
        return results;
    }

    @Override
    public boolean checkDatasetYearInDb(String date) {
        log.info("Checking existing records for date: {}", date);
        Preconditions.checkNotNull(date);
        RegistrationInformationEntity firstByDataSetYear = registrationInformationCRUDRepository.findFirstByDataSetYear(date);
        return firstByDataSetYear != null && date.equalsIgnoreCase(firstByDataSetYear.getDataSetYear());
    }
}
