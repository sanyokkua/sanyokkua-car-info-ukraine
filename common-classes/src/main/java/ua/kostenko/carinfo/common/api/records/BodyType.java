package ua.kostenko.carinfo.common.api.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyType implements Serializable, GenericRecord<String> {
    public static final String BODY_TYPE_NAME = "bodyTypeName";
    private Long bodyTypeId;
    private String bodyTypeName;

    @JsonIgnore
    @Override
    public Long getId() {
        return bodyTypeId;
    }

    @JsonIgnore
    @Override
    public String getIndexField() {
        return getBodyTypeName();
    }
}
