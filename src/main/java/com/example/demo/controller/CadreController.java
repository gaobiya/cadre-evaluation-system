package com.example.demo.controller;

import com.example.demo.entity.Cadre;
import com.example.demo.entity.CadreEvaluationResult;
import com.example.demo.entity.PageResult;
import com.example.demo.entity.Result;
import com.example.demo.service.CadreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 中层干部控制器
 */
@RestController
@RequestMapping("/api/cadre")
public class CadreController {
    @Autowired
    private CadreService cadreService;
    
    /**
     * 获取所有干部列表
     */
    @GetMapping("/list")
    public Result<List<Cadre>> listAll() {
        return Result.success(cadreService.listAll());
    }
    
    /**
     * 分页获取干部列表
     */
    @GetMapping("/page")
    public Result<PageResult<Cadre>> listByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(cadreService.listByPage(pageNum, pageSize));
    }
    
    /**
     * 根据ID获取干部信息
     */
    @GetMapping("/{id}")
    public Result<Cadre> getById(@PathVariable Long id) {
        Cadre cadre = cadreService.getById(id);
        if (cadre == null) {
            return Result.fail("找不到指定干部");
        }
        return Result.success(cadre);
    }
    
    /**
     * 添加干部
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Cadre cadre) {
        boolean result = cadreService.add(cadre);
        return result ? Result.success(true) : Result.fail("添加失败");
    }
    
    /**
     * 更新干部信息
     */
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Cadre cadre) {
        boolean result = cadreService.update(cadre);
        return result ? Result.success(true) : Result.fail("更新失败");
    }
    
    /**
     * 删除干部
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = cadreService.delete(id);
        return result ? Result.success(true) : Result.fail("删除失败");
    }
    
    /**
     * 获取干部测评结果
     */
    @GetMapping("/evaluation/{id}")
    public Result<CadreEvaluationResult> getEvaluationResult(@PathVariable Long id) {
        CadreEvaluationResult result = cadreService.getEvaluationResult(id);
        if (result == null) {
            return Result.fail("找不到指定干部的测评结果");
        }
        return Result.success(result);
    }
    
    /**
     * 获取所有干部测评结果
     */
    @GetMapping("/evaluation/list")
    public Result<List<CadreEvaluationResult>> listAllEvaluationResults() {
        return Result.success(cadreService.listAllEvaluationResults());
    }
    
    /**
     * 分页获取干部测评结果
     */
    @GetMapping("/evaluation/page")
    public Result<PageResult<CadreEvaluationResult>> listEvaluationResultsByPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(cadreService.listEvaluationResultsByPage(pageNum, pageSize));
    }
} 