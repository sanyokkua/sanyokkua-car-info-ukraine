package ua.kostenko.carinfo.carinfoua.utils.csv.fields;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import ua.kostenko.carinfo.carinfoua.data.persistent.entities.RegistrationInformationEntity;

@Slf4j
public enum RegistrationInformationCSV {
    PERSON("person"),
    ADMINISTRATIVE_OBJECT("reg_addr_koatuu"),
    OPERATION_CODE("oper_code"),
    OPERATION_NAME("oper_name"),
    REGISTRATION_DATE("d_reg"),
    DEPARTMENT_CODE("dep_code"),
    DEPARTMENT_NAME("dep"),
    CAR_BRAND("brand"),
    CAR_MODEL("model"),
    CAR_MAKE_YEAR("make_year"),
    CAR_COLOR("color"),
    CAR_KIND("kind"),
    CAR_BODY("body"),
    CAR_PURPOSE("purpose"),
    CAR_FUEL("fuel"),
    CAR_ENGINE_CAPACITY("capacity"),
    CAR_OWN_WEIGHT("own_weight"),
    CAR_TOTAL_WEIGHT("total_weight"),
    CAR_NEW_REGISTRATION_NUMBER("n_reg_new"),
    DATA_SET_YEAR("dataSetYear");

    private final String fieldName;

    RegistrationInformationCSV(String fieldName) {
        this.fieldName = fieldName;
    }

    public static RegistrationInformationEntity mapRecord(CSVRecord record) {
        RegistrationInformationEntity.RegistrationInformationEntityBuilder builder = new RegistrationInformationEntity.RegistrationInformationEntityBuilder();
        try {
            builder.setPerson(record.get(PERSON.fieldName))
                   .setAdministrativeObjectCode(parseOrGetNull(record.get(ADMINISTRATIVE_OBJECT.fieldName)))
                   .setOperationCode(parseOrGetNull(record.get(OPERATION_CODE.fieldName)))
                   .setOperationName(record.get(OPERATION_NAME.fieldName))
                   .setRegistrationDate(record.get(REGISTRATION_DATE.fieldName))
                   .setDepartmentCode(parseOrGetNull(record.get(DEPARTMENT_CODE.fieldName)))
                   .setDepartmentName(record.get(DEPARTMENT_NAME.fieldName))
                   .setCarBrand(record.get(CAR_BRAND.fieldName))
                   .setCarModel(record.get(CAR_MODEL.fieldName))
                   .setCarMakeYear(parseOrGetNull(record.get(CAR_MAKE_YEAR.fieldName)))
                   .setCarColor(record.get(CAR_COLOR.fieldName))
                   .setCarKind(record.get(CAR_KIND.fieldName))
                   .setCarBody(record.get(CAR_BODY.fieldName))
                   .setCarPurpose(record.get(CAR_PURPOSE.fieldName))
                   .setCarFuel(record.get(CAR_FUEL.fieldName))
                   .setCarEngineCapacity(parseOrGetNull(record.get(CAR_ENGINE_CAPACITY.fieldName)))
                   .setCarOwnWeight(parseOrGetNull(record.get(CAR_OWN_WEIGHT.fieldName)))
                   .setCarTotalWeight(parseOrGetNull(record.get(CAR_TOTAL_WEIGHT.fieldName)))
                   .setCarNewRegistrationNumber(record.get(CAR_NEW_REGISTRATION_NUMBER.fieldName));
        } catch (Exception ex) {
            log.debug("exception", ex);
        }
        RegistrationInformationEntity registrationInformationEntity = builder.build();
        if (registrationInformationEntity != null) {
            registrationInformationEntity.setId(RegistrationInformationEntity.createId(registrationInformationEntity));
        }
        return registrationInformationEntity;
    }

    private static Long parseOrGetNull(String value) {
        Long result = null;
        try {
            result = Long.valueOf(value);
        } catch (Exception ex) {
//      log.warn("Error occurred due parsing string to Long value. Value: {}", value);
        }
        return result;
    }
}
