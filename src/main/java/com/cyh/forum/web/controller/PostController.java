package com.cyh.forum.web.controller;

import com.cyh.forum.exception.BadRequestException;
import com.cyh.forum.exception.ResourceNotFoundException;
import com.cyh.forum.persistence.model.Comment;
import com.cyh.forum.persistence.model.Post;
import com.cyh.forum.service.CategoryService;
import com.cyh.forum.service.CommentService;
import com.cyh.forum.service.PostService;
import com.cyh.forum.util.NewPostFormValidator;
import com.cyh.forum.web.dto.CommentDto;
import com.cyh.forum.web.dto.PostDto;
import com.cyh.forum.web.vo.HotPostVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NewPostFormValidator newPostValidator;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Value("${resource.staticResourceLocation}")
    private String staticResourceLocation;

    @RequestMapping(value = "/post/list", method = RequestMethod.GET)
    public String index(Model model) {
        Map<String, Object> attributes = postService.findPosts();
        if (null == attributes) {
            throw new ResourceNotFoundException("attributes not found.");
        }
        model.addAttribute(attributes);
        return "fragments/posts-list";
    }

    @RequestMapping(value = "/post/{postId}", method = RequestMethod.GET)
    public String getPost(Model model, @PathVariable Long postId) {
        if (null == postId) {
            throw new BadRequestException("Path variable postId cound not be null.");
        }
        Map<String, Object> attributes = this.postService.findPostDetailsAndCommentsByPostId(postId);
        if (null == attributes) {
            throw new ResourceNotFoundException("attributes not found.");
        }
        List<HotPostVo> hotPostVos = postService.hotPostVos();
        model.addAttribute("hotPostVos", hotPostVos);
        model.addAllAttributes(attributes);
        return "forum/post";
    }

    @RequestMapping(value = "/postDowload/{fileName}", method = RequestMethod.GET)
    public String getPost(HttpServletResponse response, @PathVariable String fileName) {
        if (null == fileName) {
            throw new BadRequestException("Path variable fileName cound not be null.");
        }
        if (fileName != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            //设置文件路径
            File file = new File(staticResourceLocation + username + "/file/" + fileName);
            //File file = new File(realPath , fileName);
            if (file.exists()) {
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                OutputStream os = null;
                try {

                    response.setContentType("application/force-download");// 设置强制下载不打开
                    response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("UTF-8"), "iso-8859-1"));
                    //response.addHeader("Content-Disposition", "attachment;fileName=" + filename);// 设置文件名
                    byte[] buffer = new byte[1024];
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    return "下载成功";
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return "下载失败";
    }

    @RequestMapping(value = "/new/{categoryName}", method = RequestMethod.GET)
    public String displayNewPostPageWithCategory(Model model, @PathVariable String categoryName) {
        if (null == categoryName) {
            throw new BadRequestException("Path variable postId cound not be null.");
        }
        Map<String, Object> attributes = this.categoryService.getNewPostPageWithCategoryName(categoryName);
        if (null == attributes) {
            throw new ResourceNotFoundException("attributes not found.");
        }
        model.addAllAttributes(attributes);
        return "forum/new-post";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String displayNewPostPage(Model model) {
        Map<String, Object> attributes = this.categoryService.getNewPostPageWithCategorySelect();
        if (null == attributes) {
            throw new ResourceNotFoundException("attributes not found.");
        }
        model.addAllAttributes(attributes);
        return "forum/new-post";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String processNewPost(@Valid @ModelAttribute("postDto") PostDto postDto, BindingResult bindingResult,
                                 Model model, @RequestParam("file") MultipartFile file) {
        if (null == postDto) {
            throw new BadRequestException("NewPostForm cound not be null.");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        try {
            String fileName = null;
            if (!file.isEmpty()) {
                // 获取文件名
                fileName = file.getOriginalFilename();
                logger.info("上传的文件名为：" + fileName);
                // 获取文件的后缀名
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                logger.info("文件的后缀名为：" + suffixName);
                // 设置文件存储路径
                String path = staticResourceLocation + username + "/file/" + fileName;
                File dest = new File(path);
                // 检测是否存在目录
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();// 新建文件夹
                }
                file.transferTo(dest);// 文件写入
            }
            Post post = this.postService.createNewPost(postDto, fileName);
            if (null == post) {
                throw new ResourceNotFoundException("New post object can't be created.");
            }
            // post form validation
            this.newPostValidator.validate(post, bindingResult);
            if (bindingResult.hasErrors()) {
                logger.info("BindingResult has errors >> " + bindingResult.getFieldError());
                return "forum/new-post";
            } else {
                this.postService.save(post);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/post/{postId}", method = RequestMethod.POST)
    public String processNewComment(@PathVariable Long postId,
                                    @Valid @ModelAttribute("commentDto") CommentDto commentDto) {
        if (null == postId && null == commentDto) {
            throw new BadRequestException("Path variable postId and newCommentForm cound not be null.");
        }
        Comment comment = this.commentService.createNewCommentOnPost(postId, commentDto);
        if (null == comment) {
            throw new ResourceNotFoundException("New comment object can't be created.");
        }
        // comment form validation here ...
        this.commentService.save(comment);
        return "redirect:/post/{postId}";
    }

}