package com.mockbird.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mockbird.entity.MockRule;
import com.mockbird.mapper.MockRuleMapper;
import com.mockbird.service.MockRuleService;
import org.springframework.stereotype.Service;

/**
 * MockRule Service 实现类。
 * 继承 ServiceImpl 后自动获得 MyBatis-Plus 通用 CRUD 方法，
 * 复杂查询逻辑可在此类中添加。
 */
@Service
public class MockRuleServiceImpl extends ServiceImpl<MockRuleMapper, MockRule> implements MockRuleService {
}
