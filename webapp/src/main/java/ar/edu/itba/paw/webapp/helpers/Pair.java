package ar.edu.itba.paw.webapp.helpers;

public class Pair<v1, v2> {
    private final v1 leftValue;
    private final v2 rightValue;

    public Pair(v1 leftValue, v2 rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public v1 getLeftValue() {
        return leftValue;
    }

    public v2 getRightValue() {
        return rightValue;
    }
}
