public class Validator {

    public boolean validateFormat(Data data) {
        if (data == null) return false;
        return data.title != null && !data.title.isBlank()
            && data.content != null && !data.content.isBlank();
    }
}
