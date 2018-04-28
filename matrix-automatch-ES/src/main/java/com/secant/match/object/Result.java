/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secant.match.object;

/**
 *
 * @author manjut
 */
public class Result {

    private String criteria_fk;
    private long totalMatch;
    private long oneToOne;
    private long oneToMany;
    private long manyToOne;
    private long manyToMany;

    public String getCriteria_fk() {
        return criteria_fk;
    }

    public void setCriteria_fk(String criteria_fk) {
        this.criteria_fk = criteria_fk;
    }

    public long getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(long manyToMany) {
        this.manyToMany = manyToMany;
    }

    public long getManyToOne() {
        return manyToOne;
    }

    public void setManyToOne(long manyToOne) {
        this.manyToOne = manyToOne;
    }

    public long getOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(long oneToMany) {
        this.oneToMany = oneToMany;
    }

    public long getOneToOne() {
        return oneToOne;
    }

    public void setOneToOne(long oneToOne) {
        this.oneToOne = oneToOne;
    }

    public long getTotalMatch() {
        return totalMatch;
    }

    public void setTotalMatch(long totalMatch) {
        this.totalMatch = totalMatch;
    }

    
}
