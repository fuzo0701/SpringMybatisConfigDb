package kr.hrd.mapper.oracle;

import kr.hrd.config.DatabaseOracle;

import java.util.List;
import java.util.Map;

@DatabaseOracle
public interface ExamOracleRepository {

    public List<Map<String, Object>> examList();
}
