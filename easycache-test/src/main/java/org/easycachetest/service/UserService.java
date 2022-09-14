package org.easycachetest.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.easycachetest.entity.UserDO;
import org.easycachetest.mapper.UserDAO;
import org.galileo.easycache.anno.CacheRemove;
import org.galileo.easycache.anno.CacheUpdate;
import org.galileo.easycache.anno.Cached;
import org.galileo.easycache.common.enums.BreakdownType;
import org.galileo.easycache.common.enums.ConsistencyType;
import org.galileo.easycache.springboot.keygenerator.OgnlKeyGeneratorPolicy;
import org.galileo.easycache.springboot.keygenerator.SpelKeyGeneratorPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public UserDO add(UserDO userDO) {
        userDAO.insert(userDO);
        return userDO;
    }

    @Cached(cacheName = "user", key = "#id")
    public UserDO get(int id) {
        return userDAO.selectById(id);
    }

    @Cached(cacheName = "user", key = "#id", expire = 5 * 1000)
    public UserDO getBreakdown1(int id) {
        return userDAO.selectById(id);
    }

    @Cached(cacheName = "user", key = "#id", expire = 5 * 1000, breakDown = BreakdownType.RENEWAL)
    public UserDO getBreakdown2(int id) {
        return userDAO.selectById(id);
    }

    @Cached(cacheName = "user_list")
    public List<UserDO> getList() {
        return userDAO.selectList(Wrappers.query());
    }

    @CacheRemove(cacheName = "user", key = "#id")
    public void del(int id) {
        userDAO.deleteById(id);
    }

    @CacheUpdate(cacheName = "user", key = "#userDO.id", consistency = ConsistencyType.EVENTUAL)
    public UserDO update(UserDO userDO) {
        if (userDAO.updateById(userDO) > 0) {
            return userDO;
        }
        return null;
    }


    @Cached(namespace = "game", cacheName = "bookone")
    public UserDO one() {
        return new UserDO();
    }


    @Cached(cacheName = "book", key = "id", keyPolicy = OgnlKeyGeneratorPolicy.class)
    public UserDO getOgel1(int id) {
        return userDAO.selectById(id);
    }

    @Cached(cacheName = "book", key = "bookDO.id", keyPolicy = OgnlKeyGeneratorPolicy.class)
    public UserDO getOgel2(UserDO bookDO) {
        return userDAO.selectById(bookDO.getId());
    }

    @Cached(cacheName = "book", key = "bookDO.id", keyPolicy = OgnlKeyGeneratorPolicy.class)
    public UserDO getOgel3(String orgId, UserDO bookDO) {
        return userDAO.selectById(bookDO.getId());
    }


    @Cached(cacheName = "book", key = "#id", keyPolicy = SpelKeyGeneratorPolicy.class)
    public UserDO getSpel1(int id) {
        return userDAO.selectById(id);
    }

    @Cached(cacheName = "book", key = "#bookDO.id", keyPolicy = SpelKeyGeneratorPolicy.class)
    public UserDO getSpel2(UserDO bookDO) {
        return userDAO.selectById(bookDO.getId());
    }

    @Cached(cacheName = "book", key = "#bookDO.id", keyPolicy = SpelKeyGeneratorPolicy.class)
    public UserDO getSpel3(String orgId, UserDO bookDO) {
        return userDAO.selectById(bookDO.getId());
    }
}
