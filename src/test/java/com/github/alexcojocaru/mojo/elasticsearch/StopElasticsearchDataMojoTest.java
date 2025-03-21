package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.github.alexcojocaru.mojo.elasticsearch.NetUtil.ElasticsearchPort;

/**
 * @author alexcojocaru
 *
 */
public class StopElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    private ElasticsearchNode elasticsearchNode;

    private StopElasticsearchNodeMojo mojo;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        String dataPath = new File("target/test-harness/elasticsearch-data").getAbsolutePath();

        Map<ElasticsearchPort, Integer> esPorts = NetUtil.findOpenPortsForElasticsearch();
        int httpPort = esPorts.get(ElasticsearchPort.HTTP);
        int tcpPort = esPorts.get(ElasticsearchPort.TCP);

        elasticsearchNode = ElasticsearchNode.start(dataPath, httpPort, tcpPort);

        //Configure mojo with context
        File testPom = new File(getBasedir(), "src/test/resources/goals/stop/pom.xml");
        mojo = (StopElasticsearchNodeMojo)lookupMojo("stop", testPom);
        mojo.setPluginContext(new HashMap());
        mojo.getPluginContext().put("test", elasticsearchNode);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        if (elasticsearchNode != null && !elasticsearchNode.isClosed())
        {
            elasticsearchNode.stop();
        }
    }
    
    
    public void testMojoLookup() throws Exception
    {
        assertNotNull(mojo);
    }
    
    public void testMojoExecution() throws Exception
    {
        assertNotNull(mojo);
        mojo.execute();

        HttpClient client = HttpClientBuilder.create().build();
<<<<<<< /usr/src/app/output/alexcojocaru/elasticsearch-maven-plugin/80a84ddaaedf4d73d3e2a4a952dea3593b11d4a3/src/test/java/com/github/alexcojocaru/mojo/elasticsearch/StopElasticsearchDataMojoTest.java/left.java
||||||| /usr/src/app/output/alexcojocaru/elasticsearch-maven-plugin/80a84ddaaedf4d73d3e2a4a952dea3593b11d4a3/src/test/java/com/github/alexcojocaru/mojo/elasticsearch/StopElasticsearchDataMojoTest.java/base.java
=======
>>>>>>> /usr/src/app/output/alexcojocaru/elasticsearch-maven-plugin/80a84ddaaedf4d73d3e2a4a952dea3593b11d4a3/src/test/java/com/github/alexcojocaru/mojo/elasticsearch/StopElasticsearchDataMojoTest.java/right.java
        HttpGet get = new HttpGet("http://localhost:" + elasticsearchNode.getHttpPort());
<<<<<<< /usr/src/app/output/alexcojocaru/elasticsearch-maven-plugin/80a84ddaaedf4d73d3e2a4a952dea3593b11d4a3/src/test/java/com/github/alexcojocaru/mojo/elasticsearch/StopElasticsearchDataMojoTest.java/left.java
        
        final int connectionTimeout = 500; // millis
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionTimeout)
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(connectionTimeout)
                .build();
        get.setConfig(requestConfig);

||||||| /usr/src/app/output/alexcojocaru/elasticsearch-maven-plugin/80a84ddaaedf4d73d3e2a4a952dea3593b11d4a3/src/test/java/com/github/alexcojocaru/mojo/elasticsearch/StopElasticsearchDataMojoTest.java/base.java
=======
    
        final int connectionTimeout = 500; // millis
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionTimeout)
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(connectionTimeout)
                .build();
        get.setConfig(requestConfig);

>>>>>>> /usr/src/app/output/alexcojocaru/elasticsearch-maven-plugin/80a84ddaaedf4d73d3e2a4a952dea3593b11d4a3/src/test/java/com/github/alexcojocaru/mojo/elasticsearch/StopElasticsearchDataMojoTest.java/right.java
        try
        {
            client.execute(get);
            
            fail("The ES cluster should have been down by now");
        }
        catch (HttpHostConnectException | ConnectTimeoutException expected)
        {
        }

        assertTrue(elasticsearchNode.isClosed());
    }

}
