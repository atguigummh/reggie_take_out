package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增分类
     * @param category
     * @return
     */
        @PostMapping
        public R<String> save(@RequestBody Category category){
            log.info("category:{}",category);
            categoryService.save(category);
            return R.success("新增分类成功");
        }
    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
        @GetMapping("/page")
        public R<Page> page( int page, int pageSize){
            //分页构造器
            Page<Category> pageinfo=new Page<>(page,pageSize);
            //条件构造器
            LambdaQueryWrapper<Category> wrapper=new LambdaQueryWrapper<>();
            //添加排序条件，根据sort进行排序
            wrapper.orderByAsc(Category::getSort);
            //进行分页查询
            categoryService.page(pageinfo,wrapper);
            return R.success(pageinfo);
        }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
        public R<String> deleteById( Long id){
            log.info("删除分类，id为:{}的",id);
            categoryService.remove(id);
//            categoryService.removeById(id);
            return R.success("分类信息删除成功");
        }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
        public R<String> update(@RequestBody Category category){
            log.info("修改分类，id为:{}的",category.getId());
            categoryService.updateById(category);
            return R.success("修改分类信息成功");
        }

    /**
     * 根据条件查询分类数据
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> wrapper=new LambdaQueryWrapper();
        //添加查询条件
        wrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
        }
}
