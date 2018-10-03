package ua.kostenko.carinfo.carinfoua.data;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "InfoData")
public class InfoData {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "person")
  private String person; // e.g. P // P (person) or J (juridical)
  @Column(name = "reg_addr_koatuu")
  private String reg_addr_koatuu; // e.g. 1210436900
  @Column(name = "oper_code")
  private String operationCode; // e.g. 440
  @Column(name = "oper_name")
  private String operationName; // e.g. 440 - ПЕРЕРЕЄСТРАЦIЯ ПРИ ВТРАТІ СВIДОЦТВА ПРО РЕЄСТРАЦIЮ
  @Column(name = "d_reg")
  private String registrationDate; // date of operation  yyy-mm-dd 2018-01-01
  @Column(name = "dep_code")
  private String departmentCode; // number 1244
  @Column(name = "dep")
  private String departmentName; // TSC - number ТСЦ 1244
  @Column(name = "brand")
  private String carBrand; // e.g. Mitsubishi
  @Column(name = "model")
  private String carModel; // e.g. Lancer X
  @Column(name = "make_year")
  private String carMakeYear; // date of creation current car 2007
  @Column(name = "color")
  private String carColor; // e.g. БІЛИЙ
  @Column(name = "kind")
  private String carKind; // e.g. ЛЕГКОВИЙ
  @Column(name = "body")
  private String carBody; // e.g. СЕДАН-B
  @Column(name = "purpose")
  private String carPurpose; // e.g. ЗАГАЛЬНИЙ
  @Column(name = "fuel")
  private String carFuel; // e.g. БЕНЗИН
  @Column(name = "capacity")
  private String carEngineCapacity; // e.g. 1198
  @Column(name = "own_weight")
  private String carOwnWeight; // e.g. 955
  @Column(name = "total_weight")
  private String carTotalWeight; // e.g. 1355
  @Column(name = "n_reg_new")
  private String carNewRegistrationNumber; // AX3333EX
  @Column(name = "dataSetYear")
  private String dataSetYear;

  public String getFieldValue(InfoDataFields field) {
    switch (field) {
      case PERSON:
        return person;
      case CAR_BODY:
        return carBody;
      case CAR_BRAND:
        return carBrand;
      case CAR_COLOR:
        return carColor;
      case CAR_ENGINE_CAPACITY:
        return carEngineCapacity;
      case CAR_FUEL:
        return carFuel;
      case CAR_KIND:
        return carKind;
      case CAR_MAKE_YEAR:
        return carMakeYear;
      case CAR_MODEL:
        return carModel;
      case CAR_NEW_REGISTRATION_NUMBER:
        return carNewRegistrationNumber;
      case CAR_OWN_WEIGHT:
        return carOwnWeight;
      case CAR_PURPOSE:
        return carPurpose;
      case CAR_TOTAL_WEIGHT:
        return carTotalWeight;
      case DEPARTMENT_CODE:
        return departmentCode;
      case DEPARTMENT_NAME:
        return departmentName;
      case OPERATION_CODE:
        return operationCode;
      case OPERATION_NAME:
        return operationName;
      case REG_ADDR_KOATUU:
        return reg_addr_koatuu;
      case REGISTRATION_DATE:
        return registrationDate;
      case DATA_SET_YEAR:
        return dataSetYear;
      default:
        return null;
    }
  }
}
