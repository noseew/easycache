package org.galileo.easycache.easycachetest.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.Objects;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("user")
public class UserDO {
    private int id;
    private Integer age;
    private String name;
    private BigDecimal salary;
    private Date createTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDO)) {
            return false;
        }
        UserDO userDO = (UserDO) o;
        return id == userDO.id && Objects.equal(age, userDO.age) && Objects.equal(name, userDO.name) && Objects
                .equal(salary, userDO.salary) && Objects.equal(createTime, userDO.createTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, age, name, salary, createTime);
    }
}
