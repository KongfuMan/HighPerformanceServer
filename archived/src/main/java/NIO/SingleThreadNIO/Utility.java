package NIO.SingleThreadNIO;

public class Utility {

    public static String generateLargeString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5000000; i++){
            sb.append('a');
        }
        return sb.toString();
    }
}
