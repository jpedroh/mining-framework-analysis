package controllers;
import ninja.Context;
import models.Article;
import ninja.FilterWith;
import models.ArticleDto;
import ninja.Result;
import ninja.Results;
import ninja.SecureFilter;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.ArticleDao;
import etc.LoggedInUser;
import filters.RememberMeFilter;

@Singleton @FilterWith(value = RememberMeFilter.class) public class ArticleController {
  @Inject ArticleDao articleDao;

  public Result articleShow(@PathParam(value = "id") Long id) {
    Article article = null;
    if (id != null) {
      article = articleDao.getArticle(id);
    }
    return Results.html().render("article", article);
  }

  @FilterWith(value = SecureFilter.class) public Result articleNew() {
    return Results.html();
  }

  @FilterWith(value = SecureFilter.class) public Result articleNewPost(@LoggedInUser String username, Context context, @JSR303Validation ArticleDto articleDto, Validation validation) {
    if (validation.hasViolations()) {
      context.getFlashScope().error("Please correct field.");
      context.getFlashScope().put("title", articleDto.title);
      context.getFlashScope().put("content", articleDto.content);
      return Results.redirect("/article/new");
    } else {
      articleDao.postArticle(username, articleDto);
      context.getFlashScope().success("New article created.");
      return Results.redirect("/");
    }
  }
}