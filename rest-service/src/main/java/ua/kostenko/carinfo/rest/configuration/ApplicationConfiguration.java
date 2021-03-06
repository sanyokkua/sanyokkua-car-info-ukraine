package ua.kostenko.carinfo.rest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import ua.kostenko.carinfo.rest.utils.Translation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class ApplicationConfiguration {

    @Bean
    public Translation translation() {
        Map<String, String> loadedTranslation;
        try {
            log.info("Loading translations");
            File file = new ClassPathResource("eng.json").getFile();
            log.info("File with translations: {}", file.getAbsolutePath());
            String json = FileUtils.readFileToString(file, "UTF-8");
            log.info("Content of translations json file: {}", json);
            //noinspection unchecked
            loadedTranslation = (Map<String, String>) new ObjectMapper().readValue(json, HashMap.class);
        } catch (IOException e) {
            loadedTranslation = null;
            log.error("Translation loading failure.", e);
        }
        return Translation.builder().translations(loadedTranslation).build();
    }

}
