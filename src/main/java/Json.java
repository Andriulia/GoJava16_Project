import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Json {

    public static String readFromLanguagesJson(int index) {
        String language;
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Languages> languages = mapper.readValue(new File("src\\main\\resources\\languages.json"),
                    new TypeReference<List<Languages>>() {
                    });
            language = languages.get(index).getName();
            return language;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}