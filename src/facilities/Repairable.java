package facilities;

public interface Repairable {
    String getName();
    boolean isDamaged();
    void repair();
}
