package obp.repo;

public class SubTest extends SuperTest {
    SubTest() {
        // Line n1
        super(10);
        System.out.println("Sub 2");
        String x = "aaa";
        //String x1 = x;
        String y = "bbb";
        x += y;
        x -= y;

        int i = 10;
        long ii = 10;
        char f = 'aaa';
        char e = '\u3337';
        double g = 1.11;
        float h = 1.11;
        boolean b = true;
        int xx = true;
        int xxx = "abc";

        char[] xxy = x.toCharArray();
        for(char c : xxy) {
            System.out.print(c);
        }

    }
    public static void blah() {
        //My name
        String pNum = null;
        pNum.substring(0,3).append(pNum.substring(4,3).append(pNum.substring(8,4)));
        pNum.substring(0,3) + pNum.substring(4,7) + pNum.substring(8,12)
    }

    public static void main(String[] args) {
        for( String each : args) {
            System.out.println(each);
        }
        String myString = null;
        switch(args[0]) {
            case "0":
                myString = args[0];
                break;
            case "duck":
                myString = "duck";
            default:
                myString = "shit";

        }

    }
    public void blah2() {
        for(int i = 0; i < 5; i++) {
            if ( i == 2) {
                continue;
            }
            System.out.println(i);
        }
    }
}
