package item_9_ex;

import java.io.*;


public class ExampleUtilities {
    private static final String DEFFALT_VAL = "non";
    static String firstLineOfFileLegacy(String path) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(path));
        try{
            return br.readLine();
        }finally {
            br.close();
        }
    }
    static String firstLineOfFile(String path) throws IOException{
        try(BufferedReader br = new BufferedReader(new FileReader(path))){
            return br.readLine();
        }
        // 추가적으로 catch를 추가할 수 있음.
        catch (IOException e){
            return DEFFALT_VAL;
        }
    }

    static void copyLegacy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try{
         OutputStream out = new FileOutputStream(dst);
         try{
             byte[] buf = new byte[1024];
             int n ;
             while ((n = in.read(buf)) >= 0)
                 out.write(buf,0,n);
         } finally {
             out.close();
         }
        }finally {
            in.close();
        }
    }
    static void copy(String src, String dst) throws IOException {

        try(InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[1024];
            int n;
            while ((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        }
    }
}
