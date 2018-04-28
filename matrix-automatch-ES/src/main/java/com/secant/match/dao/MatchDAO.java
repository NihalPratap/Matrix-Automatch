/*
 * @author Anant Jagania
 *
 */
package com.secant.match.dao;

import com.secant.match.object.Columns;
import com.secant.match.object.Criteria;
import java.util.List;

/**
 * DAO interface for database operations.
 */
public interface MatchDAO {
    public List<Criteria> getCriteria();
    public Columns getTableColumn(String criteriaCol);
    //public void createIndex(Criteria criteria);
    public void runMatching(Criteria criteria);
    //public void dropIndex(Criteria criteria);
    public void dropTables();
    public void alterTable(Criteria criteria);
    public void resetCriteriaTable(Criteria criteria);
}
