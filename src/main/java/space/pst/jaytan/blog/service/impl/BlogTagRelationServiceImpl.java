package space.pst.jaytan.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import space.pst.jaytan.blog.entity.BlogInfo;
import space.pst.jaytan.blog.entity.BlogTagRelation;
import space.pst.jaytan.blog.dao.BlogTagRelationMapper;
import space.pst.jaytan.blog.service.BlogTagRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 博客跟标签的关系表 服务实现类
 * </p>
 *
 * @author: 南街
 * @since 2019-08-28
 */
@Service
public class BlogTagRelationServiceImpl extends ServiceImpl<BlogTagRelationMapper, BlogTagRelation> implements BlogTagRelationService {

    @Autowired
    private BlogTagRelationMapper blogTagRelationMapper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeAndsaveBatch(List<Integer> blogTagIds, BlogInfo blogInfo) {
        Long blogId = blogInfo.getBlogId();
        List<BlogTagRelation> list = blogTagIds.stream().map(blogTagId -> new BlogTagRelation()
                .setTagId(blogTagId)
                .setBlogId(blogId)).collect(Collectors.toList());
        blogTagRelationMapper.delete(new QueryWrapper<BlogTagRelation>()
                .lambda()
                .eq(BlogTagRelation::getBlogId, blogInfo.getBlogId()));
        for (BlogTagRelation item : list) {
            blogTagRelationMapper.insert(item);
        }
    }
}
