package obp.repo;

public class Programmer extends Writer {
    public static void write() {
        System.out.println("Writing code");
    }
    public static void main(String[] args) {
        Programmer p = new Programmer();
        p.write();
        Writer w = p;
        w.write();
    }
}
