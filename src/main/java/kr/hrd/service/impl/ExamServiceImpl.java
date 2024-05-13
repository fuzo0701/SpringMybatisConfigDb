package kr.hrd.service.impl;

import kr.hrd.mapper.h2.ExamMapper;
import kr.hrd.mapper.oracle.ExamOracleRepository;
import kr.hrd.service.ExamService;
import org.springframework.stereotype.Service;

@Service
public class ExamServiceImpl implements ExamService {

    private ExamOracleRepository examMapper;

    @Override
    public void getList() {
        examMapper.examList();
    }
}
