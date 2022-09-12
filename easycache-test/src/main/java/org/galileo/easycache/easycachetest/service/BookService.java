//package org.galileo.easycache.easycachetest.service;
//
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import org.galileo.easycache.anno.CacheRemove;
//import org.galileo.easycache.anno.CacheUpdate;
//import org.galileo.easycache.anno.Cached;
//import org.galileo.easycache.common.enums.ConsistencyType;
//import org.galileo.easycache.easycachetest.entity.BookDO;
//import org.galileo.easycache.easycachetest.mapper.BookDAO;
//import org.galileo.easycache.springboot.keygenerator.OgnlKeyGeneratorPolicy;
//import org.galileo.easycache.springboot.keygenerator.SpelKeyGeneratorPolicy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@Transactional
//public class BookService {
//
//    @Autowired
//    private BookDAO bookDAO;
//
//    public BookDO add(BookDO book) {
//        bookDAO.insert(book);
//        return book;
//    }
//
//    @Cached(namespace = "game", cacheName = "bookone")
//    public BookDO one() {
//        return new BookDO();
//    }
//
//    @Cached(namespace = "game", cacheName = "book", key = "#id")
//    public BookDO get(int id) {
//        return bookDAO.selectById(id);
//    }
//
//    @Cached(namespace = "game", cacheName = "book_list")
//    public List<BookDO> getList() {
//        return bookDAO.selectList(Wrappers.query());
//    }
//
//    @CacheRemove(namespace = "game", cacheName = "book", key = "#id", consistency = ConsistencyType.STRONG)
//    public void del(int id) {
//        bookDAO.deleteById(id);
//    }
//
//    @CacheUpdate(namespace = "game", cacheName = "book", key = "#book.id", consistency = ConsistencyType.STRONG)
//    public BookDO update(BookDO book) {
//        if (bookDAO.updateById(book) > 0) {
//            return book;
//        }
//        return null;
//    }
//
//
//
//    @Cached(cacheName = "book", key = "id", keyPolicy = OgnlKeyGeneratorPolicy.class)
//    public BookDO getOgel1(int id) {
//        return bookDAO.selectById(id);
//    }
//
//    @Cached(cacheName = "book", key = "bookDO.id", keyPolicy = OgnlKeyGeneratorPolicy.class)
//    public BookDO getOgel2(BookDO bookDO) {
//        return bookDAO.selectById(bookDO.getId());
//    }
//
//    @Cached(cacheName = "book", key = "bookDO.id", keyPolicy = OgnlKeyGeneratorPolicy.class)
//    public BookDO getOgel3(String orgId, BookDO bookDO) {
//        return bookDAO.selectById(bookDO.getId());
//    }
//
//
//    @Cached(cacheName = "book", key = "#id", keyPolicy = SpelKeyGeneratorPolicy.class)
//    public BookDO getSpel1(int id) {
//        return bookDAO.selectById(id);
//    }
//
//    @Cached(cacheName = "book", key = "#bookDO.id", keyPolicy = SpelKeyGeneratorPolicy.class)
//    public BookDO getSpel2(BookDO bookDO) {
//        return bookDAO.selectById(bookDO.getId());
//    }
//
//    @Cached(cacheName = "book", key = "#bookDO.id", keyPolicy = SpelKeyGeneratorPolicy.class)
//    public BookDO getSpel3(String orgId, BookDO bookDO) {
//        return bookDAO.selectById(bookDO.getId());
//    }
//}
