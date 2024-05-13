package kr.hrd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig extends org.apache.ibatis.session.Configuration {

    public MybatisConfig() {
        this.setDefaultFetchSize(100);
        this.isMapUnderscoreToCamelCase();
    }

    @Override
    public boolean isMapUnderscoreToCamelCase() {
        return true;
    }

    @Override
    public void setDefaultFetchSize(Integer defaultFetchSize) {
        super.setDefaultFetchSize(defaultFetchSize);
    }

}
