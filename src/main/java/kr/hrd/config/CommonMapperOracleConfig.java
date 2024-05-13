package kr.hrd.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan(basePackages = {"kr.hrd.mapper.oracle"} , annotationClass = DatabaseOracle.class, sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
public class CommonMapperOracleConfig {

    final List<String> MAPPER_LOCATIONS_PATH = List.of("classpath:mappers/h2/*.xml");
    final String CONFIG_PATH = "";

    @Bean(name="hikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource-oracle.hikari")
    public HikariConfig hikariConfig(){
        return new HikariConfig();
    }

    @Bean(name="datasource-oracle")
    public HikariDataSource hikariDataSource(@Autowired @Qualifier("hikariConfig") HikariConfig hikariConfig){
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name="sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Autowired @Qualifier("datasource-oracle") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        MybatisConfig mybatisConfig = new MybatisConfig();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("kr.hrd.model.oracle");
        factoryBean.setConfiguration(mybatisConfig);
        factoryBean.setMapperLocations(getResolverMapperLocations());
        return factoryBean.getObject();
    }

    // Resource를 만들어서 여러 위치에 있는 매퍼를 등록할 수 있다.
    private Resource[] getResolverMapperLocations() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();

        for(String location : MAPPER_LOCATIONS_PATH) {
            Resource[] mappers = resolver.getResources(location);
            resources.addAll(Arrays.asList(mappers));
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Bean(name="sqlSession")
    public SqlSessionTemplate sqlSession(@Autowired @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Primary
    @Bean(name="transactionManager")
    public DataSourceTransactionManager transactionManager(@Autowired @Qualifier("datasource-oracle") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
