package kr.hrd.mapper.h2;

import kr.hrd.config.DatabaseH2;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@DatabaseH2
public interface ExamMapper {
    public List<Map<String, Object>> examList();
}
