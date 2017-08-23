public class EmptyFieldException extends Exception {
    EmptyFieldException(){
        super("Текстовое поле не заполнено.");
    }
}
