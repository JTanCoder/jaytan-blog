package space.pst.jaytan.blog.service.impl;

import space.pst.jaytan.blog.entity.BlogComment;
import space.pst.jaytan.blog.dao.BlogCommentMapper;
import space.pst.jaytan.blog.service.BlogCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论信息表 服务实现类
 * </p>
 *
 * @author: 南街
 * @since 2019-09-04
 */
@Service
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements BlogCommentService {

}
