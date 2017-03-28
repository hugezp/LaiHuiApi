/**
 * Created by Administrator on 2017/3/27.
 */
public class Test {
    public static void main(String []args){
        String idsn ="41052219880321281X";
        String sexNum = idsn.substring(17,18);
        if (!sexNum.matches("[a-zA-Z]")){
            System.out.print("sdafsafd");
        }else{
            System.out.print("45566");
        }




    }
}
