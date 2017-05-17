package dev.ranggalabs.restapi.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sql2o.Sql2o;
import org.sql2o.quirks.PostgresQuirks;

import javax.sql.DataSource;

/**
 * Created by erlangga on 4/25/2017.
 */
@Configuration
public class DBConfiguration {

    @Bean
    public Sql2o sql2o(DataSource dataSource){
        Sql2o sql2o = new Sql2o(dataSource, new PostgresQuirks());
        return sql2o;
    }

}
