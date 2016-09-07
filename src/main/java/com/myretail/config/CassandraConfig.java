package com.myretail.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = { "com.myretail" })
public class CassandraConfig {
	
	  @Value("${cassandra.ip}")
	  private String cassNodes;
	  
	  @Value("${cassandra.port}")
	  private String port;
	  
	  @Value("${cassandra.keyspace}")
	  private String keyspace;
	

	  @Bean
	  public CassandraClusterFactoryBean cluster() {

	    CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
	    cluster.setContactPoints(cassNodes);
	    cluster.setPort(Integer.parseInt(port));
	    //cluster.setUsername(username);

	    return cluster;
	  }

	  @Bean
	  public CassandraMappingContext mappingContext() {
	    return new BasicCassandraMappingContext();
	  }

	  @Bean
	  public CassandraConverter converter() {
	    return new MappingCassandraConverter(mappingContext());
	  }

	  @Bean
	  public CassandraSessionFactoryBean session() throws Exception {

	    CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
	    session.setCluster(cluster().getObject());
	    session.setKeyspaceName(keyspace);
	    session.setConverter(converter());
	    session.setSchemaAction(SchemaAction.NONE);

	    return session;
	  }

	  @Bean
	  public CassandraOperations cassandraTemplate() throws Exception {
	    return new CassandraTemplate(session().getObject());
	  }

}
