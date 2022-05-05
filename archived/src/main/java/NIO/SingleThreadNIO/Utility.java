package NIO.SingleThreadNIO;

public class Utility {

    public static String generateLargeString(){
        return generateLargeString(5000000);
    }

    public static String generateLargeString(int size){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++){
            sb.append('a');
        }
        return sb.toString();
    }
}
