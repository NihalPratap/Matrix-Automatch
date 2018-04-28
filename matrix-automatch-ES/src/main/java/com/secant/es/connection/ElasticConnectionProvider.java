/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secant.es.connection;

import com.secant.match.utility.BaseUtility;
import io.searchbox.client.JestClient;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugin.deletebyquery.DeleteByQueryPlugin;


/**
 *
 * @author nihal
 */
public class ElasticConnectionProvider {
    private Log log = LogFactory.getLog(ElasticConnectionProvider.class);

    public static Client client;
    private static Settings settings;

    private static JestClient jestClient;

    public final Client getEsClient() throws UnknownHostException {

        if (client == null) {
            log.info("Initializing Transport client connection");
            client = configureElasticClient();
        }
        return client;
    }

    /**
     * Sets the es client.
     *
     * @param esClient the new es client
     */
    public final void setEsClient(final Client esClient) {

        ElasticConnectionProvider.client = esClient;
    }

    // create connection with default elasticsearch client
    public static Client configureElasticClient() throws UnknownHostException {
        settings = (Settings) Settings.settingsBuilder()
                .put("cluster.name", BaseUtility.getESClusterName())
                //.put("index.number_of_shards", "5")
                //.put("index.number_of_replicas", "1")
                .build();
        
        // TransportClient transportClient = 
//        client = new TransportClient(settings)
//                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
        client = TransportClient.builder().settings(settings)
                .addPlugin(DeleteByQueryPlugin.class)
                .build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        return client;
    }

    public static final void closeElasticClient() {
        if (client != null) {           
            client.close();
        }
    }
}