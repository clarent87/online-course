package me.whiteship.infleanthejavatest;

public class Study {
    private StudyStatus studyStatus = StudyStatus.DRAFT;
    private int value;
    private String name;

    public Study(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public Study(int parseInt) {
        value = parseInt;
    }

    public Study() {
    }

    public int getValue() {
        return value;
    }

    public StudyStatus getStatus() {
        return studyStatus;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Study{" +
                "studyStatus=" + studyStatus +
                ", value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
