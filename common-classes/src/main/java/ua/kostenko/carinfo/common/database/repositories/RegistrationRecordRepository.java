package ua.kostenko.carinfo.common.database.repositories;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ua.kostenko.carinfo.common.api.Constants;
import ua.kostenko.carinfo.common.api.ParamsHolder;
import ua.kostenko.carinfo.common.api.records.*;
import ua.kostenko.carinfo.common.database.repositories.jdbc.crud.CrudRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Repository
@Slf4j
public class RegistrationRecordRepository extends CommonDBRepository<Registration> {//TODO: finish
    private static final RowMapper<Registration> ROW_MAPPER = (resultSet, i) -> Registration.builder()
                                                                                            .adminObjName(resultSet.getString(Constants.AdminObject.NAME))
                                                                                            .adminObjType(resultSet.getString(Constants.AdminObject.TYPE))
                                                                                            .operationCode(resultSet.getLong(Constants.RegistrationOperation.CODE))
                                                                                            .operationName(resultSet.getString(Constants.RegistrationOperation.NAME))
                                                                                            .departmentCode(resultSet.getString(Constants.RegistrationDepartment.NAME))
                                                                                            .departmentAddress(resultSet.getString(Constants.RegistrationDepartment.ADDRESS))
                                                                                            .departmentEmail(resultSet.getString(Constants.RegistrationDepartment.EMAIL))
                                                                                            .kind(resultSet.getString(Constants.RegistrationKind.NAME))
                                                                                            .color(resultSet.getString(Constants.RegistrationColor.NAME))
                                                                                            .bodyType(resultSet.getString(Constants.RegistrationBodyType.NAME))
                                                                                            .purpose(resultSet.getString(Constants.RegistrationPurpose.NAME))
                                                                                            .brand(resultSet.getString(Constants.RegistrationBrand.NAME))
                                                                                            .model(resultSet.getString(Constants.RegistrationModel.NAME))
                                                                                            .fuelType(resultSet.getString(Constants.RegistrationFuelType.NAME))
                                                                                            .engineCapacity(resultSet.getLong(Constants.RegistrationRecord.ENGINE_CAPACITY))
                                                                                            .makeYear(resultSet.getLong(Constants.RegistrationRecord.MAKE_YEAR))
                                                                                            .ownWeight(resultSet.getLong(Constants.RegistrationRecord.OWN_WEIGHT))
                                                                                            .totalWeight(resultSet.getLong(Constants.RegistrationRecord.TOTAL_WEIGHT))
                                                                                            .personType(resultSet.getString(Constants.RegistrationRecord.PERSON_TYPE))
                                                                                            .registrationNumber(resultSet.getString(Constants.RegistrationRecord.REGISTRATION_NUMBER))
                                                                                            .registrationDate(resultSet.getDate(Constants.RegistrationRecord.REGISTRATION_DATE))
                                                                                            .id(resultSet.getLong(Constants.RegistrationRecord.ID))
                                                                                            .build();
    private final DBRepository<AdministrativeObject> administrativeObjectDBRepository;
    private final DBRepository<BodyType> bodyTypeDBRepository;
    private final DBRepository<Color> colorDBRepository;
    private final DBRepository<Department> departmentDBRepository;
    private final DBRepository<FuelType> fuelTypeDBRepository;
    private final DBRepository<Kind> kindDBRepository;
    private final DBRepository<Operation> operationDBRepository;
    private final DBRepository<Purpose> purposeDBRepository;
    private final DBRepository<Vehicle> vehicleDBRepository;

    @Autowired
    public RegistrationRecordRepository(@NonNull @Nonnull JdbcTemplate jdbcTemplate,
                                        @NonNull @Nonnull DBRepository<AdministrativeObject> administrativeObjectDBRepository,
                                        @NonNull @Nonnull DBRepository<BodyType> bodyTypeDBRepository,
                                        @NonNull @Nonnull DBRepository<Color> colorDBRepository,
                                        @NonNull @Nonnull DBRepository<Department> departmentDBRepository,
                                        @NonNull @Nonnull DBRepository<FuelType> fuelTypeDBRepository,
                                        @NonNull @Nonnull DBRepository<Kind> kindDBRepository,
                                        @NonNull @Nonnull DBRepository<Operation> operationDBRepository,
                                        @NonNull @Nonnull DBRepository<Purpose> purposeDBRepository,
                                        @NonNull @Nonnull DBRepository<Vehicle> vehicleDBRepository) {
        super(jdbcTemplate);
        this.administrativeObjectDBRepository = administrativeObjectDBRepository;
        this.bodyTypeDBRepository = bodyTypeDBRepository;
        this.colorDBRepository = colorDBRepository;
        this.departmentDBRepository = departmentDBRepository;
        this.fuelTypeDBRepository = fuelTypeDBRepository;
        this.kindDBRepository = kindDBRepository;
        this.operationDBRepository = operationDBRepository;
        this.purposeDBRepository = purposeDBRepository;
        this.vehicleDBRepository = vehicleDBRepository;
    }

    @Nullable
    @Override
    public Registration create(@NonNull @Nonnull Registration entity) {//TODO: finish

        String jdbcTemplateInsert = "insert into carinfo.record (" +
                "admin_obj_id, " +
                "op_code, " +
                "dep_code, " +
                "kind_id, " +
                "vehicle_id, " +
                "color_id, " +
                "body_type_id, " +
                "purpose_id, " +
                "fuel_type_id, " +
                "own_weight, " +
                "total_weight, " +
                "engine_capacity, " +
                "make_year, " +
                "registration_date, " +
                "registration_number, " +
                "person_type " +
                ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(jdbcTemplateInsert, Statement.RETURN_GENERATED_KEYS);
            setParamsForStatement(entity, statement);
            return statement;
        }, keyHolder);
        Object id = keyHolder.getKeys().get(Constants.RegistrationRecord.ID);
        return findOne((long) id);
    }

    private void setParamsForStatement(@Nonnull @NonNull Registration entity, @NonNull PreparedStatement statement) throws SQLException {
        AdministrativeObject administrativeObject = administrativeObjectDBRepository.findOne(getBuilder().param(AdministrativeObject.ADMIN_OBJ_NAME, entity.getAdminObjName()).build());
        BodyType bodyType = bodyTypeDBRepository.findOne(getBuilder().param(BodyType.BODY_TYPE_NAME, entity.getBodyType()).build());
        Color color = colorDBRepository.findOne(getBuilder().param(Color.COLOR_NAME, entity.getColor()).build());
        Department department = departmentDBRepository.findOne(getBuilder().param(Department.DEPARTMENT_CODE, entity.getDepartmentCode()).build());
        FuelType fuelType = fuelTypeDBRepository.findOne(getBuilder().param(FuelType.FUEL_NAME, entity.getFuelType()).build());
        Kind kind = kindDBRepository.findOne(getBuilder().param(Kind.KIND_NAME, entity.getKind()).build());
        Operation operation = operationDBRepository.findOne(getBuilder().param(Operation.OPERATION_NAME, entity.getOperationCode()).build());
        Purpose purpose = purposeDBRepository.findOne(getBuilder().param(Purpose.PURPOSE_NAME, entity.getPurpose()).build());
        Vehicle vehicle = vehicleDBRepository.findOne(getBuilder().param(Vehicle.BRAND_NAME, entity.getBrand()).param(Vehicle.MODEL_NAME, entity.getModel()).build());

        Long adminObjId = Objects.nonNull(administrativeObject) ? administrativeObject.getAdminObjId() : null;
        Long operationCode = Objects.nonNull(operation) ? operation.getOperationCode() : null;
        Long departmentCode = Objects.nonNull(department) ? department.getDepartmentCode() : null;
        Long kindId = Objects.nonNull(kind) ? kind.getKindId() : null;
        Long vehicleId = Objects.nonNull(vehicle) ? vehicle.getVehicleId() : null;
        Long colorId = Objects.nonNull(color) ? color.getColorId() : null;
        Long bodyTypeId = Objects.nonNull(bodyType) ? bodyType.getBodyTypeId() : null;
        Long purposeId = Objects.nonNull(purpose) ? purpose.getPurposeId() : null;
        Long fuelTypeId = Objects.nonNull(fuelType) ? fuelType.getFuelTypeId() : null;

        setNullableToStatement(statement,1, adminObjId);
        setNullableToStatement(statement,2, operationCode);
        setNullableToStatement(statement,3, departmentCode);
        setNullableToStatement(statement,4, kindId);
        setNullableToStatement(statement,5, vehicleId);
        setNullableToStatement(statement,6, colorId);
        setNullableToStatement(statement,7, bodyTypeId);
        setNullableToStatement(statement,8, purposeId);
        setNullableToStatement(statement,9, fuelTypeId);
        setNullableToStatement(statement,10, entity.getOwnWeight());
        setNullableToStatement(statement,11, entity.getTotalWeight());
        setNullableToStatement(statement,12, entity.getEngineCapacity());
        setNullableToStatement(statement,13, entity.getMakeYear());
        statement.setDate(14, entity.getRegistrationDate());
        setNullableToStatement(statement, 15, entity.getRegistrationNumber());
        setNullableToStatement(statement, 16, entity.getPersonType());
    }

    private void setNullableToStatement(@NonNull PreparedStatement statement, int index, @Nullable Long value) throws SQLException {
        if (Objects.isNull(value)){
            statement.setNull(index, Types.BIGINT);
        } else {
            statement.setLong(index, value);
        }
    }

    private void setNullableToStatement(@NonNull PreparedStatement statement, int index, @Nullable String value) throws SQLException {
        if (Objects.isNull(value)){
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    @Nullable
    @Override
    public Registration update(@NonNull @Nonnull Registration entity) {//TODO: finish
        String jdbcTemplateUpdate = "update carinfo.record set admin_obj_id = ?," +
                " op_code = ?, dep_code = ?, kind_id = ?, vehicle_id = ?," +
                " color_id = ?, body_type_id = ?, purpose_id = ?, fuel_type_id = ?," +
                " own_weight = ?, total_weight = ?, engine_capacity = ?, make_year = ?," +
                " registration_date = ?, registration_number = ?, person_type = ? " +
                "where id = ?;";
        jdbcTemplate.update(jdbcTemplateUpdate,
                            entity.getOwnWeight(),
                            entity.getTotalWeight(),
                            entity.getEngineCapacity(),
                            entity.getMakeYear(),
                            entity.getRegistrationDate(),
                            entity.getRegistrationNumber(),
                            entity.getId(),
                            entity.getPersonType()
        );
        return findOne(entity.getId());
    }

    @Override
    public boolean delete(long id) {
        String jdbcTemplateDelete = "delete from carinfo.record where id = ?;";
        int updated = jdbcTemplate.update(jdbcTemplateDelete, id);
        return updated > 0;
    }

    @Override
    public boolean existId(long id) {
        String jdbcTemplateSelectCount = "select count(id) from carinfo.record where id = ?;";
        Long numberOfRows = jdbcTemplate.queryForObject(jdbcTemplateSelectCount, (rs, rowNum) -> rs.getLong(1), id);
        return Objects.nonNull(numberOfRows) && numberOfRows > 0;
    }

    @Override
    public boolean exist(@Nonnull Registration entity) {
        ParamsHolder searchParams = getBuilder()
                .param(Registration.OPERATION_CODE, entity.getOperationCode())
                .param(Registration.OPERATION_NAME, entity.getOperationName())
                .param(Registration.DEPARTMENT_CODE, entity.getDepartmentCode())
                .param(Registration.KIND, entity.getKind())
                .param(Registration.COLOR, entity.getColor())
                .param(Registration.PURPOSE, entity.getPurpose())
                .param(Registration.BRAND, entity.getBrand())
                .param(Registration.MODEL, entity.getModel())
                .param(Registration.MAKE_YEAR, entity.getMakeYear())
                .param(Registration.PERSON_TYPE, entity.getPersonType())
                .param(Registration.REGISTRATION_DATE, entity.getRegistrationDate())
                .build();
        return nonNull(findOne(searchParams));
    }

    @Nullable
    @Override
    public Registration findOne(long id) {
        String jdbcTemplateSelect = "select * " +
                "from carinfo.record r, carinfo.admin_object ao, carinfo.operation o, carinfo.department d, carinfo.kind k, carinfo.vehicle v, carinfo.color c, carinfo.body_type bt," +
                " carinfo.purpose p, carinfo.fuel_type ft, carinfo.brand b, carinfo.model m " +
                "where ao.admin_obj_id =  r.admin_obj_id and o.op_code = r.op_code and d.dep_code = r.dep_code and k.kind_id = r.kind_id and v.vehicle_id = r.vehicle_id " +
                "and c.color_id = r.color_id and bt.body_type_id = r.body_type_id and p.purpose_id = r.purpose_id and ft.fuel_type_id = r.fuel_type_id and b.brand_id = v.brand_id " +
                "and m.model_id = v.model_id " +
                "and r.id = ?;";
        return CrudRepository.getNullableResultIfException(() -> jdbcTemplate.queryForObject(jdbcTemplateSelect, ROW_MAPPER, id));
    }

    @Nullable
    @Override
    public Registration findOne(@Nonnull ParamsHolder searchParams) {
        String jdbcTemplateSelect = "select * " +
                "from carinfo.record r, carinfo.admin_object ao, carinfo.operation o, carinfo.department d, carinfo.kind k, carinfo.vehicle v, carinfo.color c, carinfo.body_type bt," +
                " carinfo.purpose p, carinfo.fuel_type ft, carinfo.brand b, carinfo.model m ";
        String where = buildWhereForFind(searchParams);
        return CrudRepository.getNullableResultIfException(() -> jdbcTemplate.queryForObject(jdbcTemplateSelect + where, ROW_MAPPER));
    }

    @Override
    public List<Registration> find() {
        String jdbcTemplateSelect = "select * " +
                "from carinfo.record r, carinfo.admin_object ao, carinfo.operation o, carinfo.department d, carinfo.kind k, carinfo.vehicle v, carinfo.color c, carinfo.body_type bt," +
                " carinfo.purpose p, carinfo.fuel_type ft, carinfo.brand b, carinfo.model m " +
                "where ao.admin_obj_id =  r.admin_obj_id and o.op_code = r.op_code and d.dep_code = r.dep_code and k.kind_id = r.kind_id and v.vehicle_id = r.vehicle_id " +
                "and c.color_id = r.color_id and bt.body_type_id = r.body_type_id and p.purpose_id = r.purpose_id and ft.fuel_type_id = r.fuel_type_id and b.brand_id = v.brand_id " +
                "and m.model_id = v.model_id;";
        return jdbcTemplate.query(jdbcTemplateSelect, ROW_MAPPER);
    }

    @Override
    public Page<Registration> find(@NonNull @Nonnull ParamsHolder searchParams) {
        Pageable pageable = searchParams.getPage();
        String select = "select * ";
        String from = "from carinfo.record r, carinfo.admin_object ao, carinfo.operation o, carinfo.department d," +
                " carinfo.kind k, carinfo.vehicle v, carinfo.color c, carinfo.body_type bt," +
                " carinfo.purpose p, carinfo.fuel_type ft, carinfo.brand b, carinfo.model m ";
        String where = buildWhereForFind(searchParams);
        String countQuery = "select count(1) as row_count " + from + where;
        int total = jdbcTemplate.queryForObject(countQuery, (rs, rowNum) -> rs.getInt(1));

        int limit = pageable.getPageSize();
        long offset = pageable.getOffset();
        String querySql = select + where + " limit ? offset ?";
        List<Registration> result = jdbcTemplate.query(querySql, ROW_MAPPER, limit, offset);
        return new PageImpl<>(result, pageable, total);
    }

    private String buildWhereForFind(@Nonnull @NonNull ParamsHolder searchParams) {//TODO: finish
        String opeCode = searchParams.getString(Registration.OPERATION_CODE);
        String opName = searchParams.getString(Registration.OPERATION_NAME);
        String depCode = searchParams.getString(Registration.DEPARTMENT_CODE);
        String kind = searchParams.getString(Registration.KIND);
        String color = searchParams.getString(Registration.COLOR);
        String purpose = searchParams.getString(Registration.PURPOSE);
        String brand = searchParams.getString(Registration.BRAND);
        String model = searchParams.getString(Registration.MODEL);
        String makeYear = searchParams.getString(Registration.MAKE_YEAR);
        String personType = searchParams.getString(Registration.PERSON_TYPE);
        String regDate = searchParams.getString(Registration.REGISTRATION_DATE);
        //TODO: add other possible params
        return buildWhere()
                .add("ao.admin_obj_id", "r.admin_obj_id")
                .add("o.op_code", "r.op_code")
                .add("d.dep_code", "r.dep_code")
                .add("k.kind_id", "r.kind_id")
                .add("v.vehicle_id", "r.vehicle_id")
                .add("c.color_id", "r.color_id")
                .add("bt.body_type_id", "r.body_type_id")
                .add("p.purpose_id", "r.purpose_id")
                .add("ft.fuel_type_id", "r.fuel_type_id")
                .add("b.brand_id", "v.brand_id")
                .add("m.model_id", "v.model_id")
                .add("o.op_code", opeCode)
                .add("o.op_name", opName)
                .add("d.dep_code", depCode)
                .add("c.color_name", color)
                .add("p.purpose_name", purpose)
                .add("k.kind_name", kind)
                .add("b.brand_name", brand)
                .add("m.model_name", model)
                .add("r.make_year", makeYear)
                .add("r.person_type", personType)
                .add("r.registration_date", regDate)
                .build();
    }
}