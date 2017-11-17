package obp.repo;

import java.util.Collections;

public class MathTest {
    static public void main(String[] args) {
        float var = 100.0F;
        int a = 7;
        int b = 12;
        System.out.println((a+b)*((a*a) - a * b + (b*b)));
        System.out.println((a+b)*(a*a-a*b+b*b));
        System.out.println((a+b)*((a*(a-b))*b) +b*b);
        System.out.println((a*a*(1-b)+b*b)*(a+b));
        System.out.println((a*b)*(a*(a-b*(b+b))));
    }
}
