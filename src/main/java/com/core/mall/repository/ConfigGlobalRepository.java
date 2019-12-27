package com.core.mall.repository;

import com.core.mall.model.entity.ConfigGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 全局配置表 dao
 */
public interface ConfigGlobalRepository extends JpaRepository<ConfigGlobal, Long> {

    /**
     * 根据key 查询配置值value
     *
     * @param globalKey 指定key
     * @return 返回value
     */
    @Query(value = "SELECT globalValue FROM ConfigGlobal  WHERE globalKey = ?1")
    String findValueByKey(String globalKey);

    ConfigGlobal findByGlobalKey(String globalKey);


    /**
     * 根据key模糊查询配置到的值
     *
     * @param globalKey 指定key
     * @return 返回对象集合
     */
    List<ConfigGlobal> findByGlobalKeyLike(String globalKey);
}
