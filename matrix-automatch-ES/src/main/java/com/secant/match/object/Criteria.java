/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secant.match.object;

/**
 *
 * @author Manjut
 */
public class Criteria {

    private String criteriaID;
    private String criterias;
    private int criteriaNumber;

    public int getCriteriaNumber() {
        return criteriaNumber;
    }

    public void setCriteriaNumber(int criteriaNumber) {
        this.criteriaNumber = criteriaNumber;
    }
    private StringBuffer tableCondition = new StringBuffer();
    private StringBuffer pacsCols = new StringBuffer();
    private StringBuffer risCols = new StringBuffer();

    public String getCriterias() {
        return criterias;
    }

    public void setCriterias(String criterias) {
        this.criterias = criterias;
    }

    public String getCriteriaID() {
        return criteriaID;
    }

    public void setCriteriaID(String criteriaID) {
        this.criteriaID = criteriaID;
    }

    public String getPacsCols() {
        return pacsCols.toString();
    }

    public StringBuffer getTableCondition() {
        return tableCondition;
    }

    public void setTableCondition(String tableCondition, boolean last) {
        this.tableCondition.append(tableCondition);
        if (!last) {
            this.tableCondition.append(" and ");
        }
    }

    public void setPacsCols(String pacsCols, boolean last) {
        this.pacsCols.append(pacsCols);
        if (!last) {
            this.pacsCols.append(",");
        }
    }

    public String getRisCols() {
        return risCols.toString();
    }

    public void setRisCols(String risCols, boolean last) {
        this.risCols.append(risCols);
        if (!last) {
            this.risCols.append(",");
        }
    }
}
