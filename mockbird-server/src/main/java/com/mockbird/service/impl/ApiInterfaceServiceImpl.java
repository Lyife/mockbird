package com.mockbird.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mockbird.entity.ApiInterface;
import com.mockbird.mapper.ApiInterfaceMapper;
import com.mockbird.service.ApiInterfaceService;
import org.springframework.stereotype.Service;

@Service
public class ApiInterfaceServiceImpl extends ServiceImpl<ApiInterfaceMapper, ApiInterface> implements ApiInterfaceService {
}
