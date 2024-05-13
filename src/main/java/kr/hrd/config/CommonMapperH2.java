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
@MapperScan(basePackages = {"kr.hrd.mapper.h2"} , annotationClass = DatabaseH2.class, sqlSessionFactoryRef = "sqlSessionFactory")
@EnableTransactionManagement
public class CommonMapperH2 {

    final List<String> MAPPER_LOCATIONS_PATH = List.of("classpath:mappers/h2/*.xml");
    final String CONFIG_PATH = "";

    @Primary
    @Bean(name="hikariConfig-primary")
    @ConfigurationProperties(prefix = "spring.datasource-h2.hikari")
    public HikariConfig hikariConfig(){
        return new HikariConfig();
    }

    @Primary
    @Bean(name="datasource-h2")
    public HikariDataSource hikariDataSource(@Autowired @Qualifier("hikariConfig-primary") HikariConfig hikariConfig){
        return new HikariDataSource(hikariConfig);
    }

    @Primary
    @Bean(name="sqlSessionFactory-primary")
    public SqlSessionFactory sqlSessionFactory(@Autowired @Qualifier("datasource-h2") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        MybatisConfig mybatisConfig = new MybatisConfig();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTypeAliasesPackage("kr.hrd.model.h2");
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

    @Primary
    @Bean(name="sqlSession-primary")
    public SqlSessionTemplate sqlSession(@Autowired @Qualifier("sqlSessionFactory-primary") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Primary
    @Bean(name="transactionManager-h2")
    public DataSourceTransactionManager transactionManager(@Autowired @Qualifier("datasource-h2") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
