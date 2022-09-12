//package org.galileo.easycache.easycachetest.entity;
//
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.google.common.base.Objects;
//import lombok.Data;
//
//import java.math.BigDecimal;
//import java.util.Date;
//
//@Data
//@TableName("book")
//public class BookDO {
//    private int id;
//    private Integer age;
//    private String name;
//    private BigDecimal salary;
//    private Date createTime;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o)
//            return true;
//        if (!(o instanceof BookDO))
//            return false;
//        BookDO bookDO = (BookDO) o;
//        return id == bookDO.id;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hashCode(id);
//    }
//}
