package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐和套餐菜品关联表
     * @param setmealDTO
     * @return
     */
    @Override
    public void insert(SetmealDTO setmealDTO) {
        //新增套餐
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        //得到新增套餐的id
        Long setmealId=setmeal.getId();
        //新增套餐菜品关联表
        List<SetmealDish> setmealDishes=setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            //将套餐id赋值到关系表中
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.insert(setmealDishes);
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page =setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 套餐批量删除
     * @return
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        //判断套餐是否在起售中，起售中不可删除
        for (Long id : ids) {
            SetmealVO setmealVO=setmealMapper.getById(id);
            if(setmealVO.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //删除套餐
        for (Long id : ids) {
            setmealMapper.delete(id);
            //删除setmeal_dish
            setmealDishMapper.deleteBySetmealId(id);
        }

    }

    /**
     * 修改套餐
     * @param setmealDTO
     * @return
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //修改套餐
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        //修改套餐菜品关系表,先全部删除后全部添加
        List<SetmealDish> setmealDishList=setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishList) {
            //新添加的关系表需要把套餐id添加进去
            setmealDish.setSetmealId(setmealDTO.getId());
        }
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        setmealDishMapper.insert(setmealDTO.getSetmealDishes());
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO= setmealMapper.getById(id);
        List<SetmealDish> setmealDishList=setmealDishMapper.getBySetmealId(id);
//        BeanUtils.copyProperties(setmealDishList,setmealVO);
        setmealVO.setSetmealDishes(setmealDishList);
        return setmealVO;
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        //根据id查找套餐
        SetmealVO setmealVO = setmealMapper.getById(id);
        setmealVO.setStatus(status);
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealVO,setmeal);
        //更新套餐
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }



}
