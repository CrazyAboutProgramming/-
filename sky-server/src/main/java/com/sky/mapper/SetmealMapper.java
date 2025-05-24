package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 插入新的套餐
     * @param setmeal
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 根据id删除套餐
     * @param id
     */
    @Delete("delete from setmeal where id=#{id}")
    void delete(Long id);

    /**
     * 修改套餐
     * @param setmeal
     */
    @AutoFill(value = OperationType.UPDATE)
    @Update("update setmeal set category_id=#{categoryId},name=#{name},price=#{price},status=#{status}," +
            "description=#{description},image=#{image},update_time=#{updateTime},update_user=#{updateUser}" +
            " where id=#{id}")
    void update(Setmeal setmeal);


}
