package ua.kostenko.carinfo.common.database.repositories;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ua.kostenko.carinfo.common.api.ParamsHolder;
import ua.kostenko.carinfo.common.api.records.Department;
import ua.kostenko.carinfo.common.database.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
class RegistrationDepartmentRepository extends CommonDBRepository<Department, Long> {
    protected static final String CODE_PARAM = "code";
    protected static final String ADDR_PARAM = "addr";
    protected static final String EMAIL_PARAM = "email";
    private static final RowMapper<Department> ROW_MAPPER = (resultSet, i) -> Department.builder()
                                                                                        .departmentCode(resultSet.getLong(Constants.RegistrationDepartment.CODE))
                                                                                        .departmentAddress(resultSet.getString(Constants.RegistrationDepartment.ADDRESS))
                                                                                        .departmentEmail(resultSet.getString(Constants.RegistrationDepartment.EMAIL))
                                                                                        .build();

    @Autowired
    public RegistrationDepartmentRepository(@NonNull @Nonnull NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    RowMapper<Department> getRowMapper() {
        return ROW_MAPPER;
    }

    @Override
    WhereBuilder.BuildResult getWhereFromParams(ParamsHolder params) {
        return buildWhere()
                .addFieldParam(Constants.RegistrationDepartment.CODE, NAME_PARAM, params.getLong(Department.DEPARTMENT_CODE))
                .addFieldParam(Constants.RegistrationDepartment.ADDRESS, NAME_PARAM, params.getString(Department.DEPARTMENT_ADDRESS))
                .addFieldParam(Constants.RegistrationDepartment.EMAIL, NAME_PARAM, params.getString(Department.DEPARTMENT_EMAIL))
                .build();
    }

    @Override
    String getTableName() {
        return Constants.RegistrationDepartment.TABLE;
    }

    @Nullable
    @Override
    public Department create(@NonNull @Nonnull Department entity) {
        String jdbcTemplateInsert = "insert into carinfo.department (dep_code, dep_addr, dep_email) values (:code, :addr, :email);";
        SqlParameterSource parameterSource = getSqlParamBuilder()
                .addParam(CODE_PARAM, entity.getDepartmentCode())
                .addParam(ADDR_PARAM, entity.getDepartmentAddress())
                .addParam(EMAIL_PARAM, entity.getDepartmentEmail())
                .build();
        return create(jdbcTemplateInsert, Constants.RegistrationDepartment.CODE, parameterSource);
    }

    @Nullable
    @Override
    public Department update(@NonNull @Nonnull Department entity) {
        String jdbcTemplateUpdate = "update carinfo.department set dep_addr = :addr, dep_email = :email where dep_code = :code;";
        SqlParameterSource parameterSource = getSqlParamBuilder()
                .addParam(CODE_PARAM, entity.getDepartmentCode())
                .addParam(ADDR_PARAM, entity.getDepartmentAddress())
                .addParam(EMAIL_PARAM, entity.getDepartmentEmail())
                .build();
        jdbcTemplate.update(jdbcTemplateUpdate, parameterSource);
        ParamsHolder holder = getParamsHolderBuilder().param(Department.DEPARTMENT_CODE, entity.getDepartmentCode()).build();
        return findOne(holder);
    }

    @Override
    public boolean delete(long id) {
        String jdbcTemplateDelete = "delete from carinfo.department where dep_code = :code;";
        SqlParameterSource params = getSqlParamBuilder().addParam(CODE_PARAM, id).build();
        return delete(jdbcTemplateDelete, params);
    }

    @Override
    public boolean existId(long id) {
        String jdbcTemplateSelectCount = "select count(dep_code) from carinfo.department where dep_code = :code;";
        SqlParameterSource params = getSqlParamBuilder().addParam(CODE_PARAM, id).build();
        return exist(jdbcTemplateSelectCount, params);
    }

    @Cacheable(cacheNames = "departmentCheck", unless = "#result == false ", key = "#entity.hashCode()")
    @Override
    public boolean exist(@NonNull @Nonnull Department entity) {
        String jdbcTemplateSelectCount = "select count(dep_code) from carinfo.department where dep_code = :code;";
        SqlParameterSource parameterSource = getSqlParamBuilder().addParam(CODE_PARAM, entity.getDepartmentCode()).build();
        return exist(jdbcTemplateSelectCount, parameterSource);
    }

    @Nullable
    @Override
    public Department findOne(long id) {
        String jdbcTemplateSelect = "select * from carinfo.department where dep_code = :code;";
        SqlParameterSource parameterSource = getSqlParamBuilder().addParam(CODE_PARAM, id).build();
        return findOne(jdbcTemplateSelect, parameterSource);
    }

    @Cacheable(cacheNames = "department", unless = "#result == null", key = "#searchParams.hashCode()")
    @Nullable
    @Override
    public Department findOne(@NonNull @Nonnull ParamsHolder searchParams) {
        Long depCode = searchParams.getLong(Department.DEPARTMENT_CODE);
        if (Objects.isNull(depCode)) {
            return null;
        }
        return findOne(depCode);
    }

    @Override
    public List<Department> find() {
        String jdbcTemplateSelect = "select * from carinfo.department;";
        return find(jdbcTemplateSelect);
    }

    @Cacheable(cacheNames = "departmentIndex", unless = "#result == false ", key = "#indexField")
    @Override
    public boolean existsByIndex(@Nonnull @NonNull Long indexField) {
        String jdbcTemplateSelectCount = "select count(dep_code) from carinfo.department where dep_code = :code;";
        SqlParameterSource parameterSource = getSqlParamBuilder().addParam(CODE_PARAM, indexField).build();
        return exist(jdbcTemplateSelectCount, parameterSource);
    }

    @Override
    public Page<Department> find(@NonNull @Nonnull ParamsHolder searchParams) {
        String select = "select * ";
        String from = "from carinfo.department d ";
        Long code = searchParams.getLong(Department.DEPARTMENT_CODE);
        String email = searchParams.getString(Department.DEPARTMENT_EMAIL);
        String address = searchParams.getString(Department.DEPARTMENT_ADDRESS);
        return findPage(searchParams, select, from, buildWhere()
                .addFieldParam("d.dep_code", CODE_PARAM, code)
                .addFieldParam("d.dep_email", ADDR_PARAM, address)
                .addFieldParam("d.dep_addr", EMAIL_PARAM, email));
    }
}
