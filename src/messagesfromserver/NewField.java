package messagesfromserver;

import java.io.Serializable;

public class NewField implements Serializable {
    private String name;
    private String field;

    public NewField(String name, String field) {
        this.name = name;
        this.field = field;
    }


    public String getName() {
        return name;
    }

    public String getField() {
        return field;
    }
}
