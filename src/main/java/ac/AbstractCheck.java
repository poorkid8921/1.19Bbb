package ac;

public interface AbstractCheck {
    String getConfigName();

    double getViolations();

    void reload();
}
