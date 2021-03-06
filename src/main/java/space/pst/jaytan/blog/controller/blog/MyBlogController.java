package space.pst.jaytan.blog.controller.blog;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import space.pst.jaytan.blog.constants.*;
import space.pst.jaytan.blog.controller.vo.BlogDetailVO;
import space.pst.jaytan.blog.dto.AjaxPutPage;
import space.pst.jaytan.blog.dto.AjaxResultPage;
import space.pst.jaytan.blog.dto.Result;
import space.pst.jaytan.blog.entity.*;
import space.pst.jaytan.blog.service.*;
import space.pst.jaytan.blog.util.PageResult;
import space.pst.jaytan.blog.util.ResultGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 博客Controller
 * @date: 2019/9/1 20:57
 */

@Controller
public class MyBlogController {


    public static String theme = "amaze";

    @Autowired
    private BlogInfoService blogInfoService;

    @Autowired
    private BlogTagService blogTagService;

    @Autowired
    private BlogConfigService blogConfigService;

    @Autowired
    private BlogTagRelationService blogTagRelationService;

    @Autowired
    private BlogCommentService blogCommentService;

    @Autowired
    private BlogLinkService blogLinkService;

    /**
     * 博客首页
     *
     * @param request
     * @return java.lang.String
     * @date 2019/9/6 7:03
     */
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        return this.page(request, 1);
    }

    /**
     * 博客分页
     *
     * @param request
     * @param pageNum
     * @return java.lang.String
     * @date 2019/9/6 7:03
     */
    @GetMapping({"/page/{pageNum}"})
    public String page(HttpServletRequest request, @PathVariable("pageNum") int pageNum) {
        Page<BlogInfo> page = new Page<BlogInfo>(pageNum, 8);
        blogInfoService.page(page, new QueryWrapper<BlogInfo>()
                .lambda()
                .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(BlogInfo::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus())
                .orderByDesc(BlogInfo::getCreateTime));
        PageResult blogPageResult = new PageResult
                (page.getRecords(), page.getTotal(), 8, pageNum);
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("pageName", "博客");
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/index";
    }

    /**
     * 搜索
     *
     * @param request
     * @param keyword
     * @return java.lang.String
     * @date 2019/9/6 7:03
     */
    @GetMapping({"/search/{keyword}"})
    public String search(HttpServletRequest request, @PathVariable("keyword") String keyword) {
        return search(request, keyword, 1);
    }

    @GetMapping({"/search/{keyword}/{pageNum}"})
    public String search(HttpServletRequest request, @PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum) {

        Page<BlogInfo> page = new Page<BlogInfo>(pageNum, 8);
        blogInfoService.page(page, new QueryWrapper<BlogInfo>()
                .lambda().like(BlogInfo::getBlogTitle, keyword)
                .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(BlogInfo::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus())
                .orderByDesc(BlogInfo::getCreateTime));
        PageResult blogPageResult = new PageResult
                (page.getRecords(), page.getTotal(), 8, pageNum);

        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "搜索");
        request.setAttribute("pageUrl", "search");
        request.setAttribute("keyword", keyword);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/list";
    }

    /**
     * 标签
     *
     * @param request
     * @param tagId
     * @return java.lang.String
     * @date 2019/9/6 7:04
     */
    @GetMapping({"/tag/{tagId}"})
    public String tag(HttpServletRequest request, @PathVariable("tagId") String tagId) {
        return tag(request, tagId, 1);
    }

    /**
     * 标签分类
     *
     * @param request
     * @param tagId
     * @param pageNum
     * @return java.lang.String
     * @date 2019/9/6 7:04
     */
    @GetMapping({"/tag/{tagId}/{pageNum}"})
    public String tag(HttpServletRequest request, @PathVariable("tagId") String tagId, @PathVariable("pageNum") Integer pageNum) {
        List<BlogTagRelation> list = blogTagRelationService.list(new QueryWrapper<BlogTagRelation>()
                .lambda().eq(BlogTagRelation::getTagId, tagId));
        PageResult blogPageResult = null;
        if (!list.isEmpty()) {
            Page<BlogInfo> page = new Page<BlogInfo>(pageNum, 8);
            blogInfoService.page(page, new QueryWrapper<BlogInfo>()
                    .lambda()
                    .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                    .eq(BlogInfo::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus())
                    .in(BlogInfo::getBlogId, list.stream().map(BlogTagRelation::getBlogId).toArray())
                    .orderByDesc(BlogInfo::getCreateTime));
            blogPageResult = new PageResult
                    (page.getRecords(), page.getTotal(), 8, pageNum);
        }
        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "标签");
        request.setAttribute("pageUrl", "tag");
        request.setAttribute("keyword", tagId);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/list";
    }

    @GetMapping({"/category/{categoryName}"})
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName) {
        return category(request, categoryName, 1);
    }

    /**
     * 分类列表
     *
     * @param request
     * @param categoryName
     * @param pageNum
     * @return java.lang.String
     * @date 2019/9/6 13:04
     */
    @GetMapping({"/category/{categoryName}/{pageNum}"})
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName, @PathVariable("pageNum") Integer pageNum) {
        Page<BlogInfo> page = new Page<BlogInfo>(pageNum, 8);
        blogInfoService.page(page, new QueryWrapper<BlogInfo>()
                .lambda()
                .eq(BlogInfo::getBlogStatus, BlogStatusEnum.RELEASE.getStatus())
                .eq(BlogInfo::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus())
                .eq(BlogInfo::getBlogCategoryName, categoryName)
                .orderByDesc(BlogInfo::getCreateTime));
        PageResult blogPageResult = new PageResult
                (page.getRecords(), page.getTotal(), 8, pageNum);

        request.setAttribute("blogPageResult", blogPageResult);
        request.setAttribute("pageName", "分类");
        request.setAttribute("pageUrl", "category");
        request.setAttribute("keyword", categoryName);
        request.setAttribute("newBlogs", blogInfoService.getNewBlog());
        request.setAttribute("hotBlogs", blogInfoService.getHotBlog());
        request.setAttribute("hotTags", blogTagService.getBlogTagCountForIndex());
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/list";
    }

    /**
     * 文章详情
     *
     * @param request
     * @param blogId
     * @return java.lang.String
     * @date 2019/9/6 13:09
     */
    @GetMapping({"/blog/{blogId}", "/article/{blogId}"})
    public String detail(HttpServletRequest request, @PathVariable("blogId") Long blogId) {
        // 获得文章info
        BlogInfo blogInfo = blogInfoService.getById(blogId);
        List<BlogTagRelation> blogTagRelations = blogTagRelationService.list(new QueryWrapper<BlogTagRelation>()
                .lambda()
                .eq(BlogTagRelation::getBlogId, blogId));
        blogInfoService.updateById(new BlogInfo()
                .setBlogId(blogInfo.getBlogId())
                .setBlogViews(blogInfo.getBlogViews() + 1));

        // 获得关联的标签列表
        List<Integer> tagIds;
        List<BlogTag> tagList = new ArrayList<>();
        if (!blogTagRelations.isEmpty()) {
            tagIds = blogTagRelations.stream()
                    .map(BlogTagRelation::getTagId).collect(Collectors.toList());
            tagList = blogTagService.list(new QueryWrapper<BlogTag>().lambda().in(BlogTag::getTagId, tagIds));
        }

        // 关联评论的Count
        Integer blogCommentCount = blogCommentService.count(new QueryWrapper<BlogComment>()
                .lambda()
                .eq(BlogComment::getCommentStatus, CommentStatusEnum.ALLOW.getStatus())
                .eq(BlogComment::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus())
                .eq(BlogComment::getBlogId, blogId));

        BlogDetailVO blogDetailVO = new BlogDetailVO();
        BeanUtils.copyProperties(blogInfo, blogDetailVO);
        blogDetailVO.setCommentCount(blogCommentCount);
        request.setAttribute("blogDetailVO", blogDetailVO);
        request.setAttribute("tagList", tagList);
        request.setAttribute("pageName", "详情");
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/detail";
    }

    /**
     * 评论列表
     *
     * @param ajaxPutPage
     * @param blogId
     * @return com.site.blog.dto.AjaxResultPage<com.site.blog.entity.BlogComment>
     * @date 2019/11/19 8:42
     */
    @GetMapping("/blog/listComment")
    @ResponseBody
    public AjaxResultPage<BlogComment> listComment(AjaxPutPage<BlogComment> ajaxPutPage, Integer blogId) {
        Page<BlogComment> page = ajaxPutPage.putPageToPage();
        blogCommentService.page(page, new QueryWrapper<BlogComment>()
                .lambda()
                .eq(BlogComment::getBlogId, blogId)
                .eq(BlogComment::getCommentStatus, CommentStatusEnum.ALLOW.getStatus())
                .eq(BlogComment::getIsDeleted, DeleteStatusEnum.NO_DELETED.getStatus())
                .orderByDesc(BlogComment::getCommentCreateTime));
        AjaxResultPage<BlogComment> ajaxResultPage = new AjaxResultPage<>();
        ajaxResultPage.setCount(page.getTotal());
        ajaxResultPage.setData(page.getRecords());
        return ajaxResultPage;
    }

    /**
     * 友链界面
     *
     * @param request
     * @return java.lang.String
     * @date 2019/9/6 17:26
     */
    @GetMapping({"/link"})
    public String link(HttpServletRequest request) {
        request.setAttribute("pageName", "友情链接");
        List<BlogLink> favoriteLinks = blogLinkService.list(new QueryWrapper<BlogLink>()
                .lambda().eq(BlogLink::getLinkType, LinkConstants.LINK_TYPE_FRIENDSHIP.getLinkTypeId())
        );
        List<BlogLink> recommendLinks = blogLinkService.list(new QueryWrapper<BlogLink>()
                .lambda().eq(BlogLink::getLinkType, LinkConstants.LINK_TYPE_RECOMMEND.getLinkTypeId())
        );
        List<BlogLink> personalLinks = blogLinkService.list(new QueryWrapper<BlogLink>()
                .lambda().eq(BlogLink::getLinkType, LinkConstants.LINK_TYPE_PRIVATE.getLinkTypeId())
        );
        //判断友链类别并封装数据 0-友链 1-推荐 2-个人网站
        request.setAttribute("favoriteLinks", favoriteLinks);
        request.setAttribute("recommendLinks", recommendLinks);
        request.setAttribute("personalLinks", personalLinks);
        request.setAttribute("configurations", blogConfigService.getAllConfigs());
        return "blog/" + theme + "/link";
    }

    /**
     * 提交评论
     *
     * @return com.site.blog.dto.Result
     * @date 2019/9/6 17:40
     */
    @PostMapping(value = "/blog/comment")
    @ResponseBody
    public Result<String> comment(HttpServletRequest request,
                                  @Validated BlogComment blogComment) {
        String ref = request.getHeader("Referer");
        // 对非法字符进行转义，防止xss漏洞
        blogComment.setCommentBody(StringEscapeUtils.escapeHtml4(blogComment.getCommentBody()));
        if (StringUtils.isEmpty(ref)) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR, "非法请求");
        }
        boolean flag = blogCommentService.save(blogComment);
        if (flag) {
            return ResultGenerator.getResultByHttp(HttpStatusEnum.OK);
        }
        return ResultGenerator.getResultByHttp(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

}
