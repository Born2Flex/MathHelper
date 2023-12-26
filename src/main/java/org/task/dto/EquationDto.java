package org.task.dto;

public class EquationDto {
    private int equationId;
    private String leftPart;
    private String rightPart;
    private String equation;

    public int getEquationId() {
        return equationId;
    }

    public void setEquationId(int equationId) {
        this.equationId = equationId;
    }

    public String getLeftPart() {
        return leftPart;
    }

    public void setLeftPart(String leftPart) {
        this.leftPart = leftPart;
    }

    public String getRightPart() {
        return rightPart;
    }

    public void setRightPart(String rightPart) {
        this.rightPart = rightPart;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }
}
