public class Validator {

    public boolean validateFormat(Data data){
        if(data != null){
            if(data.content != null && data.title != null){
                return true;
            }
            return false;
        } else {
            return false;
        }
    }
}