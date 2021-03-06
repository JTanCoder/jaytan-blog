package space.pst.jaytan.blog.service;

import space.pst.jaytan.blog.entity.BlogTag;
import com.baomidou.mybatisplus.extension.service.IService;
import space.pst.jaytan.blog.entity.BlogTagCount;

import java.util.List;

/**
 * <p>
 * 标签表 服务类
 * </p>
 *
 * @author: 南街
 * @since 2019-08-28
 */
public interface BlogTagService extends IService<BlogTag> {

    List<BlogTagCount> getBlogTagCountForIndex();

    boolean clearTag(Integer tagId);

}
