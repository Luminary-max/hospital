package com.bear.hospital.controller;

import com.bear.hospital.pojo.DrugCategory;
import com.bear.hospital.service.DrugCategoryService;
import com.bear.hospital.utils.ResponseData;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("drugCategory")
public class DrugCategoryController {
    @Resource
    private DrugCategoryService drugCategoryService;

    @RequestMapping("findAll")
    public ResponseData findAll(@RequestParam int pageNumber, @RequestParam int size,
                                @RequestParam(required = false) String query) {
        return ResponseData.success("查询成功", this.drugCategoryService.findAllDrugCategories(pageNumber, size, query));
    }

    @RequestMapping("listAll")
    public ResponseData listAll() {
        List<DrugCategory> list = this.drugCategoryService.listAll();
        return ResponseData.success("查询成功", list);
    }

    @RequestMapping("addDrugCategory")
    @ResponseBody
    public ResponseData addDrugCategory(DrugCategory drugCategory) {
        if (this.drugCategoryService.addDrugCategory(drugCategory))
            return ResponseData.success("增加药品分类成功");
        return ResponseData.fail("增加药品分类失败");
    }

    @RequestMapping("modifyDrugCategory")
    @ResponseBody
    public ResponseData modifyDrugCategory(DrugCategory drugCategory) {
        if (this.drugCategoryService.modifyDrugCategory(drugCategory))
            return ResponseData.success("修改药品分类成功");
        return ResponseData.fail("修改药品分类失败");
    }

    @RequestMapping("deleteDrugCategory")
    public ResponseData deleteDrugCategory(@RequestParam Integer dcId) {
        if (this.drugCategoryService.deleteDrugCategory(dcId))
            return ResponseData.success("删除药品分类成功");
        return ResponseData.fail("删除药品分类失败");
    }
}
