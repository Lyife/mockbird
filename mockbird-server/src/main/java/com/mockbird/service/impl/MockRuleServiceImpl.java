package com.mockbird.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mockbird.entity.MockRule;
import com.mockbird.mapper.MockRuleMapper;
import com.mockbird.service.MockRuleService;
import org.springframework.stereotype.Service;

@Service
public class MockRuleServiceImpl extends ServiceImpl<MockRuleMapper, MockRule> implements MockRuleService {
}
