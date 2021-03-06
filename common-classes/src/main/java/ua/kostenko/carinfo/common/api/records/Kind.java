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
public class Kind implements Serializable, GenericRecord<String> {
    public static final String KIND_NAME = "kindName";
    private Long kindId;
    private String kindName;

    @JsonIgnore
    @Override
    public Long getId() {
        return kindId;
    }

    @JsonIgnore
    @Override
    public String getIndexField() {
        return getKindName();
    }
}
