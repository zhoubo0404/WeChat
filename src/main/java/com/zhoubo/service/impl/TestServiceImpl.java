package com.zhoubo.service.impl;

import com.zhoubo.dao.TestDao;
import com.zhoubo.dao.base.BaseDao;
import com.zhoubo.model.SkinType;
import com.zhoubo.service.TestService;
import com.zhoubo.service.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zhoubo on 2017/2/20.
 */
@Component
public class TestServiceImpl extends BaseServiceImpl<SkinType> implements TestService {

    @Autowired
    private TestDao testDao;

    @Override
    public BaseDao<SkinType> getDao() {
        return testDao;
    }
}
