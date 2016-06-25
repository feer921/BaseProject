package common.base.beans;

import java.util.List;

/**
 * User: fee(lifei@cloudtone.com.cn)
 * Date: 2016-05-16
 * Time: 15:25
 * DESC:
 */
public interface BaseListEntity<T>{
    List<T> getListData();
}
