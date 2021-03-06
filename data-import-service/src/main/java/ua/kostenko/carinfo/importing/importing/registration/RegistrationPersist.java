package ua.kostenko.carinfo.importing.importing.registration;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ua.kostenko.carinfo.common.api.records.*;
import ua.kostenko.carinfo.common.api.services.DBService;
import ua.kostenko.carinfo.importing.csv.pojo.RegistrationCsvRecord;
import ua.kostenko.carinfo.importing.importing.Persist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RegistrationPersist implements Persist<RegistrationCsvRecord> {
    private static final LocalDateTime START_TIME = LocalDateTime.now();
    private static final ScheduledExecutorService GLOBAL_SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static volatile AtomicInteger globalPersistentCounter = new AtomicInteger(0);
    private static volatile AtomicInteger globalProcessedCounter = new AtomicInteger(0);
    private static volatile AtomicInteger globalLastProcessedCounter = new AtomicInteger(0);

    static {
        GLOBAL_SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(RegistrationPersist::logGlobalStatistics, 2, 2, TimeUnit.MINUTES);
    }

    private final DBService<Registration> registrationDBService;
    private final DBService<AdministrativeObject> administrativeObjectDBService;
    private final DBService<BodyType> bodyTypeDBService;
    private final DBService<Brand> brandDBService;
    private final DBService<Color> colorDBService;
    private final DBService<Department> departmentDBService;
    private final DBService<FuelType> fuelTypeDBService;
    private final DBService<Kind> kindDBService;
    private final DBService<Model> modelDBService;
    private final DBService<Operation> operationDBService;
    private final DBService<Purpose> purposeDBService;
    private final DBService<Vehicle> vehicleDBService;
    private final long localThreadId;
    private final String fileName;
    private final ScheduledExecutorService localScheduledExecutorService;
    private int localPersistentCounter = 0;
    private int localProcessedCounter = 0;
    private int localLastProcessedCounter = 0;

    RegistrationPersist(@NonNull @Nonnull DBService<Registration> registrationDBService,
                        @NonNull @Nonnull DBService<AdministrativeObject> administrativeObjectDBService,
                        @NonNull @Nonnull DBService<BodyType> bodyTypeDBService,
                        @NonNull @Nonnull DBService<Brand> brandDBService,
                        @NonNull @Nonnull DBService<Color> colorDBService,
                        @NonNull @Nonnull DBService<Department> departmentDBService,
                        @NonNull @Nonnull DBService<FuelType> fuelTypeDBService,
                        @NonNull @Nonnull DBService<Kind> kindDBService,
                        @NonNull @Nonnull DBService<Model> modelDBService,
                        @NonNull @Nonnull DBService<Operation> operationDBService,
                        @NonNull @Nonnull DBService<Purpose> purposeDBService,
                        @NonNull @Nonnull DBService<Vehicle> vehicleDBService, String fileName) {
        this.registrationDBService = registrationDBService;
        this.administrativeObjectDBService = administrativeObjectDBService;
        this.bodyTypeDBService = bodyTypeDBService;
        this.brandDBService = brandDBService;
        this.colorDBService = colorDBService;
        this.departmentDBService = departmentDBService;
        this.fuelTypeDBService = fuelTypeDBService;
        this.kindDBService = kindDBService;
        this.modelDBService = modelDBService;
        this.operationDBService = operationDBService;
        this.purposeDBService = purposeDBService;
        this.vehicleDBService = vehicleDBService;
        this.fileName = fileName;
        this.localThreadId = Thread.currentThread().getId();
        this.localScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.localScheduledExecutorService.scheduleAtFixedRate(this::logLocalStatistics, 2, 1, TimeUnit.MINUTES);
    }

    private void logLocalStatistics() {
        final LocalDateTime timeNow = LocalDateTime.now();
        final String formattedTime = timeNow.format(DateTimeFormatter.ISO_DATE_TIME);
        final int difference = localProcessedCounter - localLastProcessedCounter;

        log.info("Thread: {}, Time: {}, File: {} Processed: {}, Saved: {}, Processed from previous time: {}",
                 localThreadId, formattedTime, fileName, localProcessedCounter, localPersistentCounter, difference);
        localLastProcessedCounter = localProcessedCounter;
        if (localProcessedCounter > 0 && difference == 0) {
            log.info("localScheduledExecutorService will shutdown");
            localScheduledExecutorService.shutdown();
        }
    }

    private static void logGlobalStatistics() {
        final LocalDateTime timeNow = LocalDateTime.now();
        final String formattedTime = timeNow.format(DateTimeFormatter.ISO_DATE_TIME);
        final int globalPersistedNumber = globalPersistentCounter.get();
        final int globalProcessesNumber = globalProcessedCounter.get();
        final long minutesBetweenStartAndNow = Duration.between(START_TIME, timeNow).toMinutes();
        final long globalThroughput = minutesBetweenStartAndNow > 0 ? globalProcessesNumber / minutesBetweenStartAndNow : 0;
        final int difference = globalProcessesNumber - globalLastProcessedCounter.get();
        log.info("Global: Time: {}. Processed: {}, Saved: {}, globalThroughput: {}, Processed from previous time: {}",
                 formattedTime, globalProcessesNumber, globalPersistedNumber, globalThroughput, difference);
        globalLastProcessedCounter.set(globalProcessesNumber);

        if (globalProcessesNumber > 0 && difference == 0) {
            log.info("GLOBAL_SCHEDULED_EXECUTOR_SERVICE will shutdown");
            GLOBAL_SCHEDULED_EXECUTOR_SERVICE.shutdown();
        }
    }

    @Override
    public void persist(@NonNull @Nonnull RegistrationCsvRecord record) {
        log.debug("persist: Thread N: {}, record: {}", Thread.currentThread().getId(), record);
        try {
            final Optional<AdministrativeObject> administrativeObject = getAdministrativeObject(record);
            final Optional<Operation> operation = getOperation(record);
            final Optional<Model> model = getModel(record);
            final Optional<Brand> brand = getBrand(record);
            final Optional<Color> color = getColor(record);
            final Optional<Kind> kind = getKind(record);
            final Optional<BodyType> bodyType = getBodyType(record);
            final Optional<Purpose> purpose = getPurpose(record);
            final Optional<FuelType> fuelType = getFuelType(record);
            final Optional<Department> department = getDepartment(record);
            @SuppressWarnings("ConstantConditions") final Optional<Vehicle> createdVehicle = getVehicle(model.orElseGet(null), brand.orElseGet(null));

            final Date registrationDate = record.getDate();

            final String personType = record.getPersonType();
            final Long vehicleMakeYear = record.getVehicleMakeYear();
            final Long vehicleEngineCapacity = record.getVehicleEngineCapacity();
            final Long vehicleOwnWeight = record.getVehicleOwnWeight();
            final Long vehicleTotalWeight = record.getVehicleTotalWeight();
            final String vehicleRegistrationNumber = record.getVehicleRegistrationNumber();

            if (isPresent(operation, "operation") && isPresent(model, "model") &&
                    isPresent(brand, "brand") && isPresent(color, "color") &&
                    isPresent(kind, "kind") && isPresent(purpose, "purpose") &&
                    isPresent(department, "department") && isPresent(createdVehicle, "createdVehicle") &&
                    isNotNull(vehicleMakeYear, "vehicleMakeYear") && isNotNull(registrationDate, "registrationDate") &&
                    isNotBlankPersonType(personType)) {
                @SuppressWarnings("OptionalGetWithoutIsPresent")
                Registration registration = Registration.builder()
                                                        .adminObjName(administrativeObject.map(AdministrativeObject::getAdminObjName).orElse(null))//NULLABLE
                                                        .adminObjType(administrativeObject.map(AdministrativeObject::getAdminObjType).orElse(null))//NULLABLE
                                                        .operationCode(operation.get().getOperationCode())//non NULLABLE opName
                                                        .operationName(operation.get().getOperationName())//non NULLABLE opName
                                                        .departmentCode(department.get().getDepartmentCode())//non NULLABLE
                                                        .departmentAddress(department.get().getDepartmentAddress())//non NULLABLE
                                                        .departmentEmail(department.get().getDepartmentEmail())//non NULLABLE
                                                        .kindName(kind.get().getKindName())//non NULLABLE
                                                        .colorName(color.get().getColorName())//non NULLABLE
                                                        .bodyTypeName(bodyType.map(BodyType::getBodyTypeName).orElse(null))//NULLABLE
                                                        .purposeName(purpose.get().getPurposeName())//non NULLABLE
                                                        .brandName(brand.get().getBrandName())//non NULLABLE
                                                        .modelName(model.get().getModelName())//non NULLABLE
                                                        .fuelTypeName(fuelType.map(FuelType::getFuelTypeName).orElse(null))//NULLABLE
                                                        .engineCapacity(vehicleEngineCapacity)//NULLABLE
                                                        .makeYear(vehicleMakeYear)//non NULLABLE
                                                        .ownWeight(vehicleOwnWeight)//NULLABLE
                                                        .totalWeight(vehicleTotalWeight)//NULLABLE
                                                        .personType(personType)//non NULLABLE
                                                        .registrationNumber(vehicleRegistrationNumber)//NULLABLE
                                                        .registrationDate(record.getDate())//non NULLABLE
                                                        .build();
                if (!registrationDBService.exists(registration)) {
                    registrationDBService.create(registration);
                    globalPersistentCounter.incrementAndGet();
                    localPersistentCounter++;
                }
            } else {
                log.warn("Registration record is not valid. Record = {}", record);
            }
        } catch (Exception ex) {
            log.warn("Problem with saving record: {}", record);
            log.error("ERROR OCCURRED IN PERSISTING CURRENT RECORD", ex);
        }
        globalProcessedCounter.incrementAndGet();
        localProcessedCounter++;
    }

    private Optional<AdministrativeObject> getAdministrativeObject(@NonNull @Nonnull RegistrationCsvRecord record) {
        AdministrativeObject adminObject = record.getAdminObject();
        Optional<Long> adminObjId = Optional.ofNullable(adminObject.getAdminObjId());
        return adminObjId.isPresent() ? administrativeObjectDBService.get(adminObjId.get()) : Optional.empty();
    }

    private Optional<Operation> getOperation(@NonNull @Nonnull RegistrationCsvRecord record) {
        Operation operation = record.getOperation();
        return operationDBService.exists(operation) ? operationDBService.get(operation) : operationDBService.create(operation);
    }

    private Optional<Model> getModel(@NonNull @Nonnull RegistrationCsvRecord record) {
        Model model = record.getModel();
        return modelDBService.exists(model) ? modelDBService.get(model) : modelDBService.create(model);
    }

    private Optional<Brand> getBrand(@NonNull @Nonnull RegistrationCsvRecord record) {
        Brand brand = record.getBrand();
        return brandDBService.exists(brand) ? brandDBService.get(brand) : brandDBService.create(brand);
    }

    private Optional<Color> getColor(@NonNull @Nonnull RegistrationCsvRecord record) {
        Color color = record.getColor();
        return colorDBService.exists(color) ? colorDBService.get(color) : colorDBService.create(color);
    }

    private Optional<Kind> getKind(@NonNull @Nonnull RegistrationCsvRecord record) {
        Kind kind = record.getKind();
        return kindDBService.exists(kind) ? kindDBService.get(kind) : kindDBService.create(kind);
    }

    private Optional<BodyType> getBodyType(@NonNull @Nonnull RegistrationCsvRecord record) {
        BodyType bodyType = record.getBodyType();
        return bodyTypeDBService.exists(bodyType) ? bodyTypeDBService.get(bodyType) : bodyTypeDBService.create(bodyType);
    }

    private Optional<Purpose> getPurpose(@NonNull @Nonnull RegistrationCsvRecord record) {
        Purpose purpose = record.getPurpose();
        return purposeDBService.exists(purpose) ? purposeDBService.get(purpose) : purposeDBService.create(purpose);
    }

    private Optional<FuelType> getFuelType(@NonNull @Nonnull RegistrationCsvRecord record) {
        FuelType fuelType = record.getFuelType();
        return fuelTypeDBService.exists(fuelType) ? fuelTypeDBService.get(fuelType) : fuelTypeDBService.create(fuelType);
    }

    private Optional<Department> getDepartment(@NonNull @Nonnull RegistrationCsvRecord record) {
        Department department = record.getDepartment();
        return departmentDBService.exists(department) ? departmentDBService.get(department) : departmentDBService.create(department);
    }

    private Optional<Vehicle> getVehicle(@Nullable Model createdModel, @Nullable Brand createdBrand) {
        if (Objects.nonNull(createdBrand) && Objects.nonNull(createdModel)) {
            Vehicle vehicle = Vehicle.builder()
                                     .brandName(createdBrand.getBrandName())
                                     .modelName(createdModel.getModelName())
                                     .build();
            return vehicleDBService.exists(vehicle) ? vehicleDBService.get(vehicle) : vehicleDBService.create(vehicle);
        }
        return Optional.empty();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <T> boolean isPresent(Optional<T> optional, String objectName) {
        if (!optional.isPresent()) {
            log.warn("{} is not present", objectName);
        }
        return optional.isPresent();
    }

    private static boolean isNotNull(Object object, String objectName) {
        if (Objects.isNull(object)) {
            log.warn("{} is null", objectName);
        }
        return Objects.nonNull(object);
    }

    private static boolean isNotBlankPersonType(String object) {
        if (StringUtils.isBlank(object)) {
            log.warn("{} is blank", "personType");
        }
        return StringUtils.isNotBlank(object);
    }
}