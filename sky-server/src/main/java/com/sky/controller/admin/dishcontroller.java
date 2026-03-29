package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.Dishservice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags ="菜品相关接口")
@RequestMapping("/admin/dish")
public class dishcontroller {
    @Autowired
    Dishservice dishservice;
    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO)
    {

        dishservice.savewithflavor(dishDTO);

        return  Result.success();
    }
@GetMapping("/page")
    @ApiOperation("分页查询")

public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO)
{


PageResult pageResult =dishservice.pageQuery(dishPageQueryDTO);
    return Result.success(pageResult);
}

@DeleteMapping
@ApiOperation("菜品批量删除")
public  Result delete( @RequestParam List<Long> ids)
{
    dishservice.deleteBatch(ids);
    return Result.success();
}




}
