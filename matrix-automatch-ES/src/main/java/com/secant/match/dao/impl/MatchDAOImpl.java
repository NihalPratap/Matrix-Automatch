package com.secant.match.dao.impl;
import com.secant.es.connection.ElasticConnectionProvider;
import com.secant.match.dao.MatchDAO;
import com.secant.match.object.Columns;
import com.secant.match.object.Criteria;
import com.secant.match.object.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryAction;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;

/**
 * Implementation of TapeDAO interface for DB operations
 */
public class MatchDAOImpl extends ElasticConnectionProvider implements MatchDAO {

    @Override
    public List<Criteria> getCriteria() {
        List<Criteria> criteriaList = new ArrayList();
        SearchResponse response = client.prepareSearch("automatch_testing")
        .setTypes("standard_matching_criteria")
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFetchSource(new String[]{"criteria","criteria_cols"},null)
                //.setPostFilter(FilterBuilders.rangeFilter("active").gte(1))
                .execute()
                .actionGet();
        
        SearchHit[] results = response.getHits().getHits();
        
        for (SearchHit hit : results) {
        Map<String,Object> result = hit.getSource();   //the retrieved document
        Criteria criteria = new Criteria();
        criteria.setCriteriaID(result.get("criteria").toString());
        criteria.setCriterias(result.get("criteria_cols").toString());
      //System.out.println("@@@@@@@@"+result.get("criteria").toString()+"@@@@@@@@@"+result.get("criteria_cols").toString());
        criteriaList.add(criteria);
        }
        return criteriaList;
    }

    @Override
    public Columns getTableColumn(String criteriaCol) {
        //return (Columns) getSqlSession().selectOne("getTableColumn", criteriaCol);
        
        SearchResponse response =  client.prepareSearch("automatch_testing")
       .setTypes("criteria_field_map")
       .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("criteria_column",criteriaCol))
                .execute()
                .actionGet();
        
        SearchHit[] results = response.getHits().getHits();
        Columns col = new Columns();
        for (SearchHit hit : results) {
        Map<String,Object> result = hit.getSource(); 
  //      col.setConditions(result.get("pacs_table_column").toString()+"="+result.get("ris_tablr_column").toString());
        col.setRisColumn(result.get("ris_table_column").toString());
        col.setPacsColumn(result.get("pacs_table_column").toString());
               
    }
     return col;  
    }

    @Override
    public void runMatching(Criteria criteria) {
        try {
            //   getSqlSession().update("createTableOnMatching", criteria);
            
            
            XContentBuilder mapping = jsonBuilder()
                    .startObject()
                    .startObject("temp_"+criteria.getCriteriaID())
                    .startObject("properties")
                    .startObject("t_pacs_id")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .startObject("t_id")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .startObject("matching")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .startObject("UICriteria")
                    .field("type", "string")
                    .field("index", "not_analyzed")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            
            PutMappingResponse putMappingResponse = client.admin().indices()
                    .preparePutMapping("automatch_testing")
                    .setType("temp_"+criteria.getCriteriaID())
                    .setSource(mapping)
                    .execute().actionGet();
            
            
            
            
            SearchResponse SOTsearchResponse =  client.prepareSearch("automatch_testing").setTypes("sot").setQuery(matchAllQuery()).get();
            // System.out.println(SOTsearchResponse);
            for (SearchHit SOThit : SOTsearchResponse.getHits().getHits()) {
                SearchResponse PACSsearchResponse =  client.prepareSearch("automatch_testing").setTypes("unq_siuid_pacs").setQuery(matchAllQuery()).get();
                for (SearchHit PACShit : PACSsearchResponse.getHits().getHits()) {
                    //   System.out.println(PACSsearchResponse);
                    //   System.out.print("#########"/*+PACShit.getSource().get("pacs_id")+"###########"+SOThit.getSource()+"$$$$$$$$$$$$$"*/+criteria.getPacsCols()+"end");
                    String Pacs[] = criteria.getPacsCols().split(",");
                    String Ris[] = criteria.getRisCols().split(",");
                    //  System.out.println(PACShit.getSource().get("automatch_status"));
                    //  System.out.println(Ris[0]);
                    int satisfy = 0;
                    if(Pacs.length > 0 && Ris.length > 0)
                        for(int i=0;i<Pacs.length;i++)
                        {
                            if((PACShit.getSource().get("automatch_status").toString()).equals("-1"))
                                if((PACShit.getSource().get(Pacs[i])).equals(SOThit.getSource().get(Ris[i])))
                                {
                                    satisfy++;
                                }
                        }
                    if(satisfy == Pacs.length)
                    {
                        String deflt = "null";
                        String json = "{" +
                                "\"t_pacs_id\":\""+PACShit.getSource().get("pacs_id")+"\"," +
                                "\"t_id\":\""+SOThit.getSource().get("id")+"\"," +
                                "\"matching\":\""+deflt+"\"," +
                                "\"UICriteria\":\""+criteria.getCriteriaID()+"\"}";
                        IndexResponse indexresponse = client.prepareIndex("automatch_testing", "temp_"+criteria.getCriteriaID())
                                .setSource(json)
                                .execute()
                                .actionGet();
                    }
                    
                }
            }
            
            
            
            //###############################################################################################
            //getSqlSession().update("updatePACS", criteria);
            
            SearchResponse PACSsearchResponse =  client.prepareSearch("automatch_testing").setTypes("unq_siuid_pacs").setQuery(matchAllQuery()).get();
            for (SearchHit PACShit : PACSsearchResponse.getHits()) {
                SearchResponse TEMPsearchResponse =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(matchAllQuery()).get();
                for (SearchHit TEMPhit : TEMPsearchResponse.getHits()) {
                    if((PACShit.getSource().get("pacs_id")).equals(TEMPhit.getSource().get("t_pacs_id")))
                        if((PACShit.getSource().get("automatch_status").toString()).equals("-1"))
                        {
                            String json = "{"+"\"automatch_status\":\""+criteria.getCriteriaID()+"\"}";
                            UpdateResponse response = client.prepareUpdate("automatch_testing", "unq_siuid_pacs",PACShit.getId())
                                    .setDoc(json)
                                    .get();
                        }
                }
            }
//##########################################################ble ######################################

/* Creating pacs, ris_count table is not required**/
//getSqlMapClientTemplate().update("createResultPACS", criteria);

dropTables();
//   alterTable(criteria);
resetCriteriaTable(criteria);

try {
    filterMatchingResults(criteria);
} catch (InterruptedException ex) {
    Logger.getLogger(MatchDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
} catch (IOException ex) {
    Logger.getLogger(MatchDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
}
//Result result = (Result) getSqlSession().selectOne("getTotalMatch", criteria);
//doubt here...what is distinct count for???
SearchResponse searchResponse =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.matchAllQuery()).addAggregation(AggregationBuilders.cardinality("agg").field("t_pacs_id")).execute().actionGet();
Cardinality agg = searchResponse.getAggregations().get("agg");
long value = agg.getValue();
Result result = new Result();
result.setTotalMatch(value);
result.setCriteria_fk(criteria.getCriteriaID());
//#################################################################################################
//result.setOneToOne(Long.parseLong((String) getSqlSession().selectOne("getOneToOne", criteria)));
SearchResponse searchResponse2 =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.termQuery("matching", "OO")).addAggregation(AggregationBuilders.cardinality("agg").field("t_pacs_id")).execute().actionGet();
Cardinality agg1 = searchResponse2.getAggregations().get("agg");
long value1 = agg1.getValue();
// System.out.println(value);
result.setOneToOne(value);


//#################################################################################################
// result.setOneToMany(Long.parseLong((String) getSqlSession().selectOne("getOneToMany", criteria)));

SearchResponse searchResponse3 =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.termQuery("matching", "OM")).addAggregation(AggregationBuilders.cardinality("agg").field("t_pacs_id")).execute().actionGet();
Cardinality agg2 = searchResponse3.getAggregations().get("agg");
long value2 = agg2.getValue();
//System.out.println(value);
result.setOneToMany(value);
/* Creating ris, pacs_count table is not required**/
// getSqlMapClientTemplate().update("createResultRIS", criteria);
//##################################################################################################
// result.setManyToOne(Long.parqseLong((String) getSqlSession().selectOne("getManyToOne", criteria)));
SearchResponse searchResponse4 =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.termQuery("matching", "MO")).addAggregation(AggregationBuilders.cardinality("agg").field("t_pacs_id")).execute().actionGet();
Cardinality agg3 = searchResponse4.getAggregations().get("agg");
long value3 = agg3.getValue();
//System.out.println(value);
result.setManyToOne(value);
//##################################################################################################
//        getSqlMapClientTemplate().update("dropPACSIndex",criteria);
//        getSqlMapClientTemplate().update("dropRISIndex",criteria);
//getSqlSession().insert("insertMatchingResult", result);

String json =
        "{\"(criteria_fk\":\""+result.getCriteria_fk()+"\"," +
        "\"total_match\":\""+result.getTotalMatch()+"\"," +
        "\"one_to_one\":\""+result.getOneToOne()+"\","+
        "\"one_to_many\":\""+result.getOneToMany()+"\"," +
        "\"many_to_one\":\""+result.getManyToOne()+"\"," +
        "\"many_to_many\":\""+result.getManyToMany()+"\"}";
IndexResponse response = client.prepareIndex("automatch_testing", "matching_result")
        .setSource(json)
        .execute()
        .actionGet(); 


        } catch (IOException ex) {
            Logger.getLogger(MatchDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
public void filterMatchingResults(Criteria criteria) throws InterruptedException, IOException {
        //getSqlSession().update("ManyToOneTable", criteria);
        //getSqlSession().update("ManyToOneTableIndex", criteria);
    
       
         
        AggregationBuilder aggregation = AggregationBuilders
        .terms("id_agg")
        .field("t_id");
        
        String dflt = "null";
        
        SearchResponse searchResponse =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.termQuery("matching","null")).addAggregation(aggregation).execute().actionGet();
        Terms res = searchResponse.getAggregations().get("id_agg"); 
        for (Terms.Bucket hit : res.getBuckets()) {
            if(hit.getDocCount() > 1){
             String json = "{"+"\"id\":\""+hit.getKey()+"\"}";
             IndexResponse response = client.prepareIndex("automatch_testing", "manytoone")
            .setSource(json)
            .execute()
            .actionGet();   
             
                SearchResponse SearchResponse1 =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.termQuery("t_id",hit.getKey())).execute().actionGet();
             
             for(SearchHit hit1 : SearchResponse1.getHits())
             {  
                 System.out.println("manytoone");
//                 String json2 = "{"+"\"matching\":\"MO\""+"}";
//                 UpdateResponse response3 = client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(),hit1.getId())
//                            .setDoc(json2)
//                            .get();
                UpdateResponse responses = client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(), hit1.getId())
                      .setRefresh(true)
                        .setDoc(jsonBuilder()               
                        .startObject()
                            .field("matching", "MO")
                        .endObject())
                .get(); 
                
                 System.out.println(hit1.getSource().get("matching"));
             }
             
            }
        }
        
//###################################################################################################################  
    
  //    getSqlSession().update("OneToManyTable", criteria);
    //    getSqlSession().update("OneToManyTableIndex", criteria);
        
        AggregationBuilder aggregations = AggregationBuilders
        .terms("pacs_id_agg")
        .field("t_pacs_id");
        
    
        SearchResponse searchResponse1 =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.termQuery("matching","null")).addAggregation(aggregations).execute().actionGet();
        Terms res1 = searchResponse1.getAggregations().get("pacs_id_agg"); 
        for (Terms.Bucket hit : res1.getBuckets()) {
            if(hit.getDocCount() > 1){
             
            String json = "{"+"\"pacs_id\":\""+hit.getKey()+"\"}";
            IndexResponse response = client.prepareIndex("automatch_testing", "onetomany")
            .setSource(json)
            .execute()
            .actionGet();   
            
            SearchResponse SearchResponse1 =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.termQuery("t_pacs_id",hit.getKey())).execute().actionGet();
             
             for(SearchHit hit1 : SearchResponse1.getHits())
             {   System.out.println("onetomany");
//                String json3 = "{"+"\"matching\":\"OM\""+"}";
//                 UpdateResponse response3 = client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(),hit1.getId())
//                            .setDoc(json3)
//                            .get();
                 
                 UpdateResponse responses = client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(),hit1.getId())
                      .setRefresh(true)
                         .setDoc(jsonBuilder()               
                        .startObject()
                            .field("matching", "OM")
                        .endObject())
                .get();
                 
                 System.out.println(hit1.getSource().get("matching"));
             }
             }
    }
     
      
      
//##########################################################################################################
    
            //  getSqlSession().update("updateManyToOneResults", criteria);
    //  *****************earlier code***************
//        SearchResponse PACSsearchResponse =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.matchAllQuery()).get();
//        SearchResponse TEMPsearchResponse =  client.prepareSearch("automatch_testing").setTypes("manytoone").setQuery(QueryBuilders.matchAllQuery()).get();
//        for (SearchHit TEMPhit : PACSsearchResponse.getHits().getHits()) {
//            for (SearchHit MANYhit : TEMPsearchResponse.getHits().getHits()) {
//                if(TEMPhit.getSource().get("t_id").equals(MANYhit.getSource().get("id")))
//                { 
//                    String json = "{\"matching\":\"MO\"}";
//                    UpdateResponse response = client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(),TEMPhit.getId())
//                            .setDoc(json)
//                            .get();
//                }
//                
//               
//            }
//    }
   //######################################################################################################## 
        //getSqlSession().update("updateOneToManyResults", criteria);
        
//        SearchResponse TEMP1searchResponse =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.matchAllQuery()).get();
//        SearchResponse ONEsearchResponse1 =  client.prepareSearch("automatch_testing").setTypes("onetomany").setQuery(QueryBuilders.matchAllQuery()).get();
//       // System.out.println(TEMP1searchResponse);
//        System.out.println(ONEsearchResponse1);
//        for (SearchHit TEMP1hit : TEMP1searchResponse.getHits().getHits()) {            
//            for (SearchHit ONEhit : ONEsearchResponse1.getHits()) {
//                if(((TEMP1hit.getSource().get("t_pacs_id")).toString()).equals((ONEhit.getSource().get("pacs_id")).toString()))
//                { 
//                    System.out.println("OnetoMany");
//                    String json = "{"+"\"matching\":\"OM\""+"}";
//                    UpdateResponse response1 = client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(),TEMP1hit.getId())
//                            .setDoc(json)
//                            .get();
//                }
//            }
//        }             
        
      
//#######################################################################################################3        
        //getSqlSession().update("updateOneToOneResults", criteria);
        
        SearchResponse OsearchResponse =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(QueryBuilders.termQuery("matching","null")).execute().actionGet();
        for(SearchHit Ohit : OsearchResponse.getHits().getHits()){
            System.out.println(Ohit.getSource().get("matching"));
//             String json = "{"+"\"matching\":\"OO\""+"}";
//                    UpdateResponse response = client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(),Ohit.getId())
//                            .setDoc(json)
//                            .get();
                UpdateResponse responses = client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(), Ohit.getId())
                      .setRefresh(true)
                        .setDoc(jsonBuilder()               
                        .startObject()
                            .field("matching", "OO")
                        .endObject())
                .get(); 

        }
    }

    @Override
    public void alterTable(Criteria criteria) {
        //getSqlSession().update("alterCritTable", criteria);
        String deflt = null;
        String json = "{"+"\"matching\":\""+deflt+"\"}";
         SearchResponse searchResponse =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(matchAllQuery()).get();
     for(SearchHit hit : searchResponse.getHits())
     { 
        client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(), hit.getId())          
        .setDoc(json)
        .get();
     }

        
        
    }

    @Override
    public void dropTables() {
        //getSqlSession().update("dropTempTables");         
        
        StringBuilder b = new StringBuilder("");
                b.append("{");
                b.append("  \"query\": {");  
                b.append("      \"match_all\": {");               
                b.append("      }");
                b.append("  }");
                b.append("}");
         DeleteByQueryResponse rsp1 = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
            .setTypes("onetomany")
            .setSource(b.toString())
            .execute()
            .actionGet();
        DeleteByQueryResponse rsp2 = new DeleteByQueryRequestBuilder(client, DeleteByQueryAction.INSTANCE)
            .setTypes("manytoone")
            .setSource(b.toString())
            .execute()
            .actionGet();
    }

    @Override
    public void resetCriteriaTable(Criteria criteria) {
      //  getSqlSession().update("resetCriteriaTable", criteria);
      
    String deflt = null;
    String json = "{"+"\"matching\":\""+deflt+"\"}";
     SearchResponse searchResponse =  client.prepareSearch("automatch_testing").setTypes("temp_"+criteria.getCriteriaID()).setQuery(matchAllQuery()).get();
     for(SearchHit hit : searchResponse.getHits())
     {  
        client.prepareUpdate("automatch_testing", "temp_"+criteria.getCriteriaID(), hit.getId())          
        .setDoc(json)
        .get();
      
      
    }

}
}
