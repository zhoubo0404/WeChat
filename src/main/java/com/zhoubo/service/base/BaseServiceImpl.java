package com.zhoubo.service.base;

import com.zhoubo.dao.base.BaseDao;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * service抽象类，实现部分公共方法
 *
 * @author Alone
 */
public abstract class BaseServiceImpl<T> implements BaseService<T> {
    /**
     * 抽象方法，必须实现，返回DAO实例
     *
     * @return DAO实例
     */
    public abstract BaseDao<T> getDao();

    @Override
    @Transactional(readOnly = false)
    public int insert(T t) {
        return getDao().insert(t);
    }

    @Override
    @Transactional(readOnly = false)
    public void insertList(List<T> list) {
        getDao().insertList(list);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(T t) {
        getDao().update(t);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Serializable id) {
        getDao().delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotal(T t) {
        return getDao().getTotal(t);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> getAll() {
        return getDao().getAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> getList(T t) {
        return getDao().getList(t);
    }

    @Override
    @Transactional(readOnly = true)
    public T get(Serializable id) {
        return getDao().get(id);
    }

/*    @Override
    @Transactional(readOnly = true)
    public List<T> getPage(T t, Pager pager) {
        return getDao().getPage(t, null, pager);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> getPage(T t, OrderByDto orderBy, Pager pager) {
        return getDao().getPage(t, orderBy, pager);
    }

    @Override
    @Transactional(readOnly = true)
    public NaviPage<T> getNaviPager(T t, OrderByDto orderBy, Pager pager) {
        NaviPage<T> naviPage = new NaviPage<T>();
        naviPage.setList(getDao().getNaviPager(t, orderBy, pager));
        naviPage.setPager(pager);
        return naviPage;

    }

    @Transactional(readOnly = true)
    public NaviPage<T> getNaviPage(T t, Pager pager) {
        NaviPage<T> naviPage = new NaviPage<T>();
        naviPage.setList(getDao().getNaviPage(t, pager));
        naviPage.setPager(pager);
        return naviPage;
    }

    public NaviPage<T> getNaviPage(Pager pager){
        NaviPage<T> naviPage = new NaviPage<T>();
        naviPage.setList(getDao().getNaviPage(pager));
        naviPage.setPager(pager);
        return naviPage;
    }*/

}
