package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.mapper.SetmealDIshmapper;
import com.sky.result.PageResult;
import com.sky.service.Dishservice;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class Dishserviceimpl implements Dishservice {
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
     private DishMapper dishmapper ;
    @Autowired
    private SetmealDIshmapper setmealDIshmapper ;
    @Override
    @Transactional
    public void savewithflavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());
        //给菜品表插入
dishmapper.insert(dish);




        //给口味表插入
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });

            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }


    }


    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishmapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
    @Transactional
    @Override
    public void deleteBatch( List<Long> ids) {
        //判断是否起售状态
        for(Long id:ids){
        Dish dish = dishmapper.getById(id);
        if(dish.getStatus()==1)
        {
            throw  new DeletionNotAllowedException("起售状态菜品无法删除");
        }


        }

        //判断是否关连套餐

            List<Long> setmealIdByDishIds = setmealDIshmapper.getSetmealIdByDishIds(ids);
            if(!setmealIdByDishIds.isEmpty())
            {
                throw new DeletionNotAllowedException("关联套餐，无法删除");
            }





        //删除菜品
        for(Long id:ids){

            dishmapper.deleteByid(id);
            dishFlavorMapper.deleteByDishId(id);


        }

        //删除关连口味
    }


}
