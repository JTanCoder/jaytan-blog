package space.pst.jaytan.blog.service;

import space.pst.jaytan.blog.entity.BlogCategory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 博客分类 服务类
 * </p>
 *
 * @author: 南街
 * @since 2019-08-30
 */
public interface BlogCategoryService extends IService<BlogCategory> {

    public boolean clearCategory(BlogCategory blogCategory);

}
