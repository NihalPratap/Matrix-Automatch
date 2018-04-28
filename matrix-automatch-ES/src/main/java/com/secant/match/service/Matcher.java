/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secant.match.service;

import com.secant.es.connection.ElasticConnectionProvider;
import com.secant.match.dao.MatchDAO;
import com.secant.match.object.Columns;
import com.secant.match.object.Criteria;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Manjut
 */
public class Matcher extends ElasticConnectionProvider{

    private MatchDAO matchDAO;
    final Log log = LogFactory.getLog(Matcher.class);
    private int criteriaNumber = 0;
    public final void setmatchDAO(final MatchDAO matchDAO) {
        this.matchDAO = matchDAO;
    }
    
    public void startMatching() {
        getEsClient();                 //get elasticsearch connection
        List<Criteria> crit = matchDAO.getCriteria();
        log.info("Total number of Criterias :: " + crit.size());
        for (Criteria criteria : crit) {
            ++criteriaNumber;
            criteria.setCriteriaNumber(criteriaNumber);
            
            assignTableCols(criteria);
          //createIndex(criteria);
            runMatching(criteria);
          //  log.info("Dropping index....");
           // matchdao.dropIndex(criteria);
          //  log.info("Index droped ");
            log.info("Matching finished for criteria :: " + criteria.getCriterias());
        }
    }

    public void assignTableCols(Criteria criteria) {
        log.info("Mapping Criterias with Columns");
        StringBuilder condition = new StringBuilder();
        String criteriaColumn = criteria.getCriterias();
        String[] columns = criteriaColumn.split(";");
        boolean last = false;
        for (int i = 0; i < columns.length; i++) {
            String tableCol = columns[i];

            if (i == columns.length - 1) {
                last = true;
            }
            Columns cols = matchDAO.getTableColumn(tableCol);
            criteria.setTableCondition(cols.getConditions(), last);
            criteria.setPacsCols(cols.getPacsColumn(), last);
            criteria.setRisCols(cols.getRisColumn(), last);
        }
    }

  

//    public void createIndex(Criteria criteria ) {
//        log.info("Creating Index...");
//        matchdao.createIndex(criteria );
//    }

    public void runMatching(Criteria criteria) {
        log.info("Running mathcing on criteria :: " + criteria.getCriterias());
        matchDAO.runMatching(criteria);
    }
     

//    public void dropIndex(Criteria criteria) {
//        log.info("Dropping Index...");
//        matchdao.dropIndex();
//    }
}
